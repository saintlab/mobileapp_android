package com.omnom.android.activity;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.omnom.android.R;
import com.omnom.android.activity.base.BaseOmnomActivity;
import com.omnom.android.menu.model.Item;
import com.omnom.android.menu.utils.MenuHelper;
import com.omnom.android.utils.utils.ViewUtils;
import com.omnom.android.view.HeaderView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by xCh3Dx on 27.04.2015.
 */
public class OrderResultActivity extends BaseOmnomActivity {

	public class ItemViewHolder {
		@InjectView(R.id.txt_title)
		TextView txtTitle;

		@InjectView(R.id.txt_info)
		TextView txtInfo;

		private ItemViewHolder(View view) {
			ButterKnife.inject(this, view);
		}
	}

	@InjectView(R.id.order_number_continer)
	protected View viewOrderNumber;

	@InjectView(R.id.panel_top)
	protected HeaderView viewHeader;

	@InjectView(R.id.pin_code_container)
	protected View viewPinCode;

	@InjectView(R.id.main_content)
	protected LinearLayout viewContent;

	@Override
	public void initUi() {
		ViewUtils.setVisible(viewOrderNumber, true);
		ViewUtils.setVisible(viewPinCode, true);
		viewHeader.setButtonLeft(R.string.close, new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				onBackPressed();
			}
		});
		addItem(Item.NULL);
		addItem(Item.NULL);
		addItem(Item.NULL);
		addItem(Item.NULL);
	}

	private void addItem(final Item item) {
		final View view = LayoutInflater.from(this).inflate(R.layout.item_order_result, viewContent, false);
		final ItemViewHolder itemViewHolder = new ItemViewHolder(view);
		itemViewHolder.txtTitle.setText(item.name());
		itemViewHolder.txtInfo.setText(MenuHelper.getWeight(this, item));
		viewContent.addView(view);
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_order_result;
	}
}
