package com.omnom.android.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.utils.OmnomFont;
import com.omnom.android.utils.utils.AndroidUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class SearchFragment<T extends Parcelable> extends Fragment {

	public interface ItemClickListener<T> {

		void onItemClick(T item);
	}

	public interface FragmentCloseListener {

		void onFragmentClose();
	}

	public static final String ARG_NAMES = "names";

	public static final String ARG_ITEMS = "items";

	public static <T extends Parcelable> Fragment newInstance(final Map<String, T> itemsByName) {
		final Fragment fragment = new SearchFragment<T>();
		final Bundle bundle = new Bundle();
		bundle.putStringArrayList(ARG_NAMES, new ArrayList<String>(itemsByName.keySet()));
		bundle.putParcelableArrayList(ARG_ITEMS, new ArrayList<T>(itemsByName.values()));
		fragment.setArguments(bundle);
		return fragment;
	}

	@InjectView(R.id.edit_search)
	protected EditText editSearch;

	@InjectView(R.id.list)
	protected ListView list;

	@InjectView(R.id.empty)
	protected TextView txtEmpty;

	protected List<String> itemsNames;

	protected Map<String, T> itemsByName;

	protected ItemClickListener<T> itemClickListener;

	protected FragmentCloseListener fragmentCloseListener;

	protected ArrayAdapter<String> adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		itemsByName = new LinkedHashMap<String, T>();
		if(getArguments() != null) {
			itemsNames = getArguments().getStringArrayList(ARG_NAMES);
			List<T> items = getArguments().getParcelableArrayList(ARG_ITEMS);
			if(items.size() != itemsNames.size()) {
				throw new IllegalArgumentException(
						"Number of names is not equals to number of items");
			}
			for(int i = 0; i < itemsNames.size(); i++) {
				itemsByName.put(itemsNames.get(i), items.get(i));
			}
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		addItemClickListener(activity);
		addFragmentCloseListener(activity);
	}

	private void addItemClickListener(Activity activity) {
		try {
			itemClickListener = (ItemClickListener) activity;
		} catch(ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement ItemClickListener");
		}
	}

	private void addFragmentCloseListener(Activity activity) {
		try {
			fragmentCloseListener = (FragmentCloseListener) activity;
		} catch(ClassCastException e) {
			throw new ClassCastException(
					activity.toString() + " must implement FragmentCloseListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
	                         @Nullable Bundle savedInstanceState) {
		final ViewGroup view = (ViewGroup) inflater
				.inflate(R.layout.fragment_search, container, false);
		AndroidUtils.applyFont(getActivity(), view, OmnomFont.LSF_LE_REGULAR);
		ButterKnife.inject(this, view);
		return view;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		AndroidUtils.applyFont(getActivity(), list, OmnomFont.LSF_LE_REGULAR);
		adapter = new ArrayAdapter<String>(getActivity(), R.layout.item_search_list, R.id.txt_name,
		                                   itemsNames);
		editSearch.setHint(itemsNames.get(new Random().nextInt(itemsNames.size())) + "...");
		list.setAdapter(adapter);
		list.setEmptyView(txtEmpty);
		// empty header and footer are required for top and bottom dividers.
		list.addHeaderView(new View(getActivity()));
		list.addFooterView(new View(getActivity()));
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(itemClickListener != null) {
					itemClickListener.onItemClick(
							itemsByName.get((String) parent.getItemAtPosition(position)));
				}
			}
		});
		editSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
				adapter.getFilter().filter(cs);
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			                              int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
			}
		});
	}

	@OnClick(R.id.btn_close)
	protected void onClose() {
		AndroidUtils.hideKeyboard(editSearch, null);
		txtEmpty.postDelayed(new Runnable() {
			@Override
			public void run() {
				getFragmentManager().popBackStack();
			}
		}, getResources().getInteger(R.integer.default_animation_duration_short));
	}

	@Override
	public void onStart() {
		super.onStart();
		editSearch.postDelayed(new Runnable() {
			@Override
			public void run() {
				AndroidUtils.showKeyboard(editSearch);
			}
		}, getResources().getInteger(R.integer.default_animation_duration_short));
	}

	@Override
	public void onPause() {
		super.onPause();
		if(fragmentCloseListener != null) {
			fragmentCloseListener.onFragmentClose();
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		AndroidUtils.hideKeyboard(editSearch, null);
	}

}
