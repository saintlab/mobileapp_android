package com.omnom.android.fragment.dinner;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import com.omnom.android.utils.SparseBooleanArrayParcelable;

import java.util.ArrayList;

/**
 * Created by Ch3D on 25.03.2015.
 */
public abstract class DinnerDataAdapterBase<K, V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<V> {
	protected final Context mContext;

	protected final ArrayList<K> mData;

	protected final LayoutInflater mInflater;

	protected SparseBooleanArrayParcelable mCheckStates = new SparseBooleanArrayParcelable();

	public DinnerDataAdapterBase(final Context context, ArrayList<K> data) {
		mContext = context;
		mData = data;
		mInflater = LayoutInflater.from(mContext);
	}

	public boolean isItemChecked(final int position) {
		return mCheckStates.get(position, false);
	}

	public void setItemChecked(final int position, final boolean isChecked) {
		// cleanup
		for(int i = 0; i < mCheckStates.size(); i++) {
			final int key = mCheckStates.keyAt(i);
			final boolean value = mCheckStates.valueAt(i);
			if(value) {
				mCheckStates.put(key, false);
				notifyItemChanged(key);
			}
		}

		mCheckStates.put(position, isChecked);
	}

	public final K getItem(final int position) {
		return mData.get(position);
	}

	@Override
	public int getItemCount() {
		return mData.size();
	}

	/**
	 * @return single selected item
	 */
	public final K getSelectedItem() {
		for(int i = 0; i < mCheckStates.size(); i++) {
			final int key = mCheckStates.keyAt(i);
			final boolean value = mCheckStates.valueAt(i);
			if(value) {
				return getItem(key);
			}
		}
		return null;
	}
}
