package com.omnom.android.fragment;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.omnom.android.OmnomApplication;
import com.omnom.android.R;
import com.omnom.android.auth.AuthError;
import com.omnom.android.auth.AuthServiceException;
import com.omnom.android.menu.model.Menu;
import com.omnom.android.restaurateur.api.observable.RestaurateurObservableApi;
import com.omnom.android.restaurateur.model.decode.HashDecodeRequest;
import com.omnom.android.restaurateur.model.decode.RestaurantResponse;
import com.omnom.android.restaurateur.model.restaurant.Restaurant;
import com.omnom.android.restaurateur.model.restaurant.RestaurantHelper;
import com.omnom.android.utils.observable.OmnomObservable;
import com.omnom.android.utils.utils.AndroidUtils;
import com.omnom.android.utils.utils.ViewUtils;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.functions.Action1;
import rx.functions.Func1;

import static com.omnom.android.utils.Extras.EXTRA_ERROR_AUTHTOKEN_EXPIRED;
import static com.omnom.android.utils.Extras.EXTRA_ERROR_WRONG_PASSWORD;
import static com.omnom.android.utils.Extras.EXTRA_ERROR_WRONG_USERNAME;

/**
 * Created by mvpotter on 2/6/2015.
 */
public class EditHashFragment extends Fragment {

	private static final String TAG = EditHashFragment.class.getSimpleName();

	@InjectView(R.id.panel_enter_hash)
	protected View panelEnterHash;

	@InjectView(R.id.txt_enter_hash)
	protected TextView txtEnterHash;

	@InjectView(R.id.edit_hash)
	protected EditText editHash;

	@InjectView(R.id.hash_underline)
	protected TextView hashUnderline;

	@InjectView(R.id.progress_bar)
	protected ProgressBar progressBar;

	@InjectView(R.id.img_success)
	protected View imgSuccess;

	@Inject
	protected RestaurateurObservableApi api;

	private Subscription mCheckQrSubscription;

	protected Func1<RestaurantResponse, RestaurantResponse> mPreloadBackgroundFunction;

	private EnterHashPanelCloseListener mEnterHashPanelCloseListener;

	private TableFoundListener mTableFoundListener;

	private boolean isError = false;

	private boolean isBusy = false;

	public static EditHashFragment newInstance() {
		return new EditHashFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		OmnomApplication.get(getActivity()).inject(this);
		final ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_enter_hash, container, false);
		ButterKnife.inject(this, view);
		AndroidUtils.applyFont(getActivity(), view, "fonts/Futura-LSF-Omnom-LE-Regular.otf");
		initPreloadBackgroundFunction();
		initEditHash();
		return view;
	}

	private void initPreloadBackgroundFunction() {
		mPreloadBackgroundFunction = new Func1<RestaurantResponse, RestaurantResponse>() {
			@Override
			public RestaurantResponse call(final RestaurantResponse decodeResponse) {
				final List<Restaurant> restaurants = decodeResponse.getRestaurants();
				if(restaurants.size() == 1) {
					final Restaurant restaurant = restaurants.get(0);
					if(restaurant != null) {
						final String bgImgUrl = RestaurantHelper.getBackground(restaurant, getResources().getDisplayMetrics().widthPixels);
						if(!TextUtils.isEmpty(bgImgUrl)) {
							try {
								OmnomApplication.getPicasso(getActivity()).load(bgImgUrl).get();
							} catch(IOException e) {
								Log.e(TAG, "unable to load img = " + bgImgUrl);
							}
						}
					}
				}
				return decodeResponse;
			}
		};
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		addEnterHashPanelCloseListener(activity);
		addTableFoundListener(activity);
	}

	private void addEnterHashPanelCloseListener(Activity activity) {
		try {
			mEnterHashPanelCloseListener = (EnterHashPanelCloseListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement EnterHashPanelCloseListener");
		}
	}

	private void addTableFoundListener(Activity activity) {
		try {
			mTableFoundListener = (TableFoundListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement TableFoundListener");
		}
	}

	@Override
	public void onDestroy() {
		OmnomObservable.unsubscribe(mCheckQrSubscription);
		super.onDestroy();
	}

	private void initEditHash() {
		AndroidUtils.showKeyboard(editHash);
		editHash.post(new Runnable() {
			@Override
			public void run() {
				editHash.setSelection(editHash.getText().toString().length());
			}
		});
		editHash.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					if (!isBusy() && !editHash.getText().toString().isEmpty()) {
						setBusy(true);
						ViewUtils.setVisible(progressBar, true);
						editHash.setTextColor(getResources().getColor(R.color.enter_hash_color));
						loadTable(editHash.getText().toString());
					}
					return true;
				}
				return false;
			}
		});
		editHash.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				if (isBusy()) {
					editHash.removeTextChangedListener(this);
					editHash.setText(s);
					editHash.setSelection(s.length());
					editHash.addTextChangedListener(this);
				}
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (isError) {
					onHashChange();
				}
			}
		});
	}

	@OnClick(R.id.btn_close)
	protected void onBtnClose() {
		mEnterHashPanelCloseListener.onEnterHashPanelClose();
	}

	private void loadTable(final String hash) {
		mCheckQrSubscription = AndroidObservable
				.bindActivity(getActivity(), api.decode(new HashDecodeRequest(hash), mPreloadBackgroundFunction)).subscribe(
						new Action1<RestaurantResponse>() {
							@Override
							public void call(final RestaurantResponse decodeResponse) {
								if(decodeResponse.hasAuthError()) {
									throw new AuthServiceException(EXTRA_ERROR_WRONG_USERNAME | EXTRA_ERROR_WRONG_PASSWORD,
											new AuthError(EXTRA_ERROR_AUTHTOKEN_EXPIRED,
													decodeResponse.getError()));
								}
								if(!TextUtils.isEmpty(decodeResponse.getError())) {
									showError(getString(R.string.error_unknown_hash));
								} else if (decodeResponse.hasOnlyRestaurant()) {
									ViewUtils.setVisible(progressBar, false);
									ViewUtils.setVisible(imgSuccess, true);
									Restaurant restaurant = decodeResponse.getRestaurants().get(0);
									mTableFoundListener.onTableFound(decodeResponse.getRequestId(), restaurant);
								} else {
									showError(getString(R.string.error_unknown_hash));
								}
							}
						}, new Action1<Throwable>() {
							@Override
							public void call(Throwable throwable) {
								showError(getString(R.string.something_went_wrong));
							}
						});
	}

	private void onHashChange() {
		isError = false;
		txtEnterHash.setText(getString(R.string.enter_hash));
		GradientDrawable drawable = (GradientDrawable) hashUnderline.getBackground();
		drawable.setColor(getResources().getColor(R.color.enter_hash_color));
		editHash.setTextColor(getResources().getColor(android.R.color.black));
		txtEnterHash.setTextColor(getResources().getColor(R.color.qr_hint_color));
	}

	private void showError(final String message) {
		setBusy(false);
		ViewUtils.setVisible(progressBar, false);
		isError = true;
		txtEnterHash.setText(message);
		GradientDrawable drawable = (GradientDrawable) hashUnderline.getBackground();
		int color = getResources().getColor(R.color.cadre_border);
		drawable.setColor(color);
		editHash.setTextColor(color);
		txtEnterHash.setTextColor(color);
	}

	private boolean isBusy() {
		return isBusy;
	}

	private void setBusy(boolean isBusy) {
		this.isBusy = isBusy;
	}

	public interface EnterHashPanelCloseListener {
		void onEnterHashPanelClose();
	}

	public interface TableFoundListener {
		void onTableFound(String requestId, Restaurant restaurant, Menu menu);
	}

}
