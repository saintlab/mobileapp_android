package com.omnom.android.utils;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseBooleanArray;

/**
 * Created by Ch3D on 12.11.2014.
 */
public class SparseBooleanArrayParcelable extends SparseBooleanArray implements Parcelable, Cloneable {
	public static Parcelable.Creator<SparseBooleanArrayParcelable> CREATOR = new Parcelable.Creator<SparseBooleanArrayParcelable>() {
		@Override
		public SparseBooleanArrayParcelable createFromParcel(Parcel source) {
			SparseBooleanArrayParcelable read = new SparseBooleanArrayParcelable();
			int size = source.readInt();

			int[] keys = new int[size];
			boolean[] values = new boolean[size];

			source.readIntArray(keys);
			source.readBooleanArray(values);

			for(int i = 0; i < size; i++) {
				read.put(keys[i], values[i]);
			}

			return read;
		}

		@Override
		public SparseBooleanArrayParcelable[] newArray(int size) {
			return new SparseBooleanArrayParcelable[size];
		}
	};

	public SparseBooleanArrayParcelable() {

	}

	public SparseBooleanArrayParcelable(SparseBooleanArray sparseBooleanArray) {
		for(int i = 0; i < sparseBooleanArray.size(); i++) {
			this.put(sparseBooleanArray.keyAt(i), sparseBooleanArray.valueAt(i));
		}
	}

	@Override
	public SparseBooleanArrayParcelable clone() {
		return (SparseBooleanArrayParcelable) super.clone();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		int[] keys = new int[size()];
		boolean[] values = new boolean[size()];

		for(int i = 0; i < size(); i++) {
			keys[i] = keyAt(i);
			values[i] = valueAt(i);
		}

		dest.writeInt(size());
		dest.writeIntArray(keys);
		dest.writeBooleanArray(values);
	}
}
