package com.omnom.android.restaurateur.model.order;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ch3D on 10.10.2014.
 */
public class OrderItem implements Parcelable {
	public static final Creator<OrderItem> CREATOR = new Creator<OrderItem>() {
		@Override
		public OrderItem createFromParcel(Parcel in) {
			return new OrderItem(in);
		}

		@Override
		public OrderItem[] newArray(int size) {
			return new OrderItem[size];
		}
	};
	@Expose
	private String guestId;
	@Expose
	private int quantity;
	@Expose
	private double priceTotal;
	@Expose
	private double pricePerItem;
	@Expose
	private String internalId;
	@Expose
	private String title;
	@Expose
	private String id;
	@Expose
	private List<Object> modifiers = new ArrayList<Object>();
	@Expose
	private Boolean isModifier;

	public OrderItem(Parcel parcel) {
		guestId = parcel.readString();
		quantity = parcel.readInt();
		priceTotal = parcel.readDouble();
		pricePerItem = parcel.readDouble();
		internalId = parcel.readString();
		title = parcel.readString();
		id = parcel.readString();
		isModifier = parcel.readInt() == 1;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeString(guestId);
		parcel.writeInt(quantity);
		parcel.writeDouble(priceTotal);
		parcel.writeDouble(pricePerItem);
		parcel.writeString(internalId);
		parcel.writeString(title);
		parcel.writeString(id);
		parcel.writeInt(isModifier ? 1 : 0);
	}

	public String getGuestId() {
		return guestId;
	}

	public void setGuestId(String guestId) {
		this.guestId = guestId;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getPriceTotal() {
		return priceTotal;
	}

	public void setPriceTotal(double priceTotal) {
		this.priceTotal = priceTotal;
	}

	public double getPricePerItem() {
		return pricePerItem;
	}

	public void setPricePerItem(double pricePerItem) {
		this.pricePerItem = pricePerItem;
	}

	public String getInternalId() {
		return internalId;
	}

	public void setInternalId(String internalId) {
		this.internalId = internalId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Object> getModifiers() {
		return modifiers;
	}

	public void setModifiers(List<Object> modifiers) {
		this.modifiers = modifiers;
	}

	public Boolean getIsModifier() {
		return isModifier;
	}

	public void setIsModifier(Boolean isModifier) {
		this.isModifier = isModifier;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public String toString() {
		return "OrderItem{" +
				"pricePerItem=" + pricePerItem +
				", title='" + title + '\'' +
				'}';
	}
}
