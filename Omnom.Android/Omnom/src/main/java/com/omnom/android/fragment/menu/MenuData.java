package com.omnom.android.fragment.menu;

import com.omnom.android.menu.model.Category;
import com.omnom.android.menu.model.Menu;

import java.util.ArrayList;
import java.util.List;

import static com.omnom.android.adapter.MultiLevelRecyclerAdapter.Data;

/**
 * Created by Ch3D on 20.02.2015.
 */
public class MenuData {
	private Menu mMenu;

	private ArrayList<Data> mData;

	public MenuData(Menu menu) {
		mMenu = menu;
	}

	public List<Data> getData() {
		return prepareData();
	}

	private List<Data> prepareData() {
		if(mData == null) {
			mData = new ArrayList<Data>();
			for(Category category : mMenu.categories()) {
				final CategoryData categoryData = new CategoryData(null, mMenu, category, 0);
				categoryData.prepareChildren();
				mData.add(categoryData);
			}
		}
		return mData;
	}
}
