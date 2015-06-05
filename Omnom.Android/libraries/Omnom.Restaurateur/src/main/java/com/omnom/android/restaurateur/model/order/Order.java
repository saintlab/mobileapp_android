package com.omnom.android.restaurateur.model.order;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.omnom.android.currency.Currency;
import com.omnom.android.currency.Money;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ch3D on 10.10.2014.
 */
public class Order implements Parcelable {

	public static final Parcelable.Creator<Order> CREATOR = new Parcelable.Creator<Order>() {

		@Override
		public Order createFromParcel(Parcel in) {
			return new Order(in);
		}

		@Override
		public Order[] newArray(int size) {
			return new Order[size];
		}
	};

	public static Order create() {
		return new Order();
	}

	@Expose
	private int guests;

	@Expose
	private String internalId;

	@Expose
	private String internalOpenTime;

	@Expose
	private String internalTableId;

	@Expose
	private String modifiedTime;

	@Expose
	private String openTime;

	@Expose
	private String restaurantId;

	@Expose
	private String revision;

	@Expose
	private String status;

	@Expose
	private String tableId;

	@Expose
	private String waiterId;

	@Expose
	private String waiterName;

	@Expose
	private List<OrderItem> items = new ArrayList<OrderItem>();

	@Expose
	private boolean isClosed;

	@Expose
	private String id;

	@Expose
	private OrderTips tips;

	@Nullable
	@Expose
	private OrderPaid paid;

	private Order() {

	}

	public Order(final Parcel parcel) {
		guests = parcel.readInt();
		internalId = parcel.readString();
		internalOpenTime = parcel.readString();
		internalTableId = parcel.readString();
		modifiedTime = parcel.readString();
		openTime = parcel.readString();
		restaurantId = parcel.readString();
		revision = parcel.readString();
		status = parcel.readString();
		tableId = parcel.readString();
		waiterId = parcel.readString();
		waiterName = parcel.readString();
		parcel.readTypedList(items, OrderItem.CREATOR);
		isClosed = parcel.readInt() == 1;
		id = parcel.readString();
		tips = parcel.readParcelable(OrderTips.class.getClassLoader());
		paid = parcel.readParcelable(OrderPaid.class.getClassLoader());
	}

	public OrderPaid getPaid() {
		return paid;
	}

	public void setPaid(final OrderPaid paid) {
		this.paid = paid;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeInt(guests);
		parcel.writeString(internalId);
		parcel.writeString(internalOpenTime);
		parcel.writeString(internalTableId);
		parcel.writeString(modifiedTime);
		parcel.writeString(openTime);
		parcel.writeString(restaurantId);
		parcel.writeString(revision);
		parcel.writeString(status);
		parcel.writeString(tableId);
		parcel.writeString(waiterId);
		parcel.writeString(waiterName);
		parcel.writeTypedList(items);
		parcel.writeInt(isClosed ? 1 : 0);
		parcel.writeString(id);
		parcel.writeParcelable(tips, flags);
		parcel.writeParcelable(paid, flags);
	}

	public int getGuests() {
		return guests;
	}

	public void setGuests(int guests) {
		this.guests = guests;
	}

	public String getInternalId() {
		return internalId;
	}

	public void setInternalId(String internalId) {
		this.internalId = internalId;
	}

	public String getInternalOpenTime() {
		return internalOpenTime;
	}

	public void setInternalOpenTime(String internalOpenTime) {
		this.internalOpenTime = internalOpenTime;
	}

	public String getInternalTableId() {
		return internalTableId;
	}

	public void setInternalTableId(String internalTableId) {
		this.internalTableId = internalTableId;
	}

	public String getModifiedTime() {
		return modifiedTime;
	}

	public void setModifiedTime(String modifiedTime) {
		this.modifiedTime = modifiedTime;
	}

	public String getOpenTime() {
		return openTime;
	}

	public void setOpenTime(String openTime) {
		this.openTime = openTime;
	}

	public String getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(String restaurantId) {
		this.restaurantId = restaurantId;
	}

	public String getRevision() {
		return revision;
	}

	public void setRevision(String revision) {
		this.revision = revision;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTableId() {
		return tableId;
	}

	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

	public String getWaiterId() {
		return waiterId;
	}

	public void setWaiterId(String waiterId) {
		this.waiterId = waiterId;
	}

	public String getWaiterName() {
		return waiterName;
	}

	public void setWaiterName(String waiterName) {
		this.waiterName = waiterName;
	}

	public List<OrderItem> getItems() {
		return items;
	}

	public void setItems(List<OrderItem> items) {
		this.items = items;
	}

	public boolean getIsClosed() {
		return isClosed;
	}

	public void setIsClosed(boolean isClosed) {
		this.isClosed = isClosed;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public OrderTips getTips() {
		return tips;
	}

	public void setTips(OrderTips tips) {
		this.tips = tips;
	}

	public Money getPaidMoney(Currency currency) {
		if(paid == null) {
			return Money.getZero(currency);
		}
		return paid.getAmount(currency).subtract(paid.getTip(currency));
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		if(o == null || getClass() != o.getClass()) {
			return false;
		}

		Order order = (Order) o;

		if(id != null ? !id.equals(order.id) : order.id != null) {
			return false;
		}
		if(tableId != null ? !tableId.equals(order.tableId) : order.tableId != null) {
			return false;
		}
		if(waiterId != null ? !waiterId.equals(order.waiterId) : order.waiterId != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = tableId != null ? tableId.hashCode() : 0;
		result = 31 * result + (waiterId != null ? waiterId.hashCode() : 0);
		result = 31 * result + (id != null ? id.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "OrderData{" +
				"modifiedTime='" + modifiedTime + '\'' +
				", openTime='" + openTime + '\'' +
				", tableId='" + tableId + '\'' +
				", status='" + status + '\'' +
				", restaurantId='" + restaurantId + '\'' +
				", isClosed=" + isClosed +
				", id='" + id + '\'' +
				'}';
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public Money getTotalAmount(Currency currency) {
		Money result = Money.getZero(currency);
		for(final OrderItem item : items) {
			result = result.plus(Money.create(item.getPriceTotal(), currency));
		}
		return result;
	}

	public Money getTotalMoney(Currency currency) {
		double sum = 0;
		for(final OrderItem item : items) {
			sum += item.getPriceTotal();
		}
		return Money.create(sum, currency);
	}

	public Money getAmountToPay(Currency currency) {
		return Money.max(getTotalAmount(currency).subtract(getPaidMoney(currency)), Money.getZero(currency));
	}

	public Money getMoneyToPay(Currency currency) {
		final Money toPay = getTotalMoney(currency).subtract(getPaidMoney(currency));
		if(!toPay.isNegativeOrZero()) {
			return toPay;
		}
		return Money.getZero(currency);
	}

	public Money getPaidTip(Currency currency) {
		return paid != null ? paid.getTip(currency) : Money.getZero(currency);
	}

	public boolean isEverythingPaid(final Currency currency) {
		final Money paidMoney = getPaidMoney(currency);
		final Money totalMoney = getTotalMoney(currency);
		return paidMoney.isGreatherOrEquals(totalMoney);
	}
}
