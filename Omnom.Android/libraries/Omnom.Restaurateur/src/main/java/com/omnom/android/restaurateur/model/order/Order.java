package com.omnom.android.restaurateur.model.order;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ch3D on 10.10.2014.
 */
public class Order {
	@Expose
	private Integer guests;
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
	private Boolean isClosed;
	@Expose
	private String id;
	@Expose
	private OrderTips tips;
	@Expose
	private Integer paidAmount;

	public Integer getGuests() {
		return guests;
	}

	public void setGuests(Integer guests) {
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

	public Boolean getIsClosed() {
		return isClosed;
	}

	public void setIsClosed(Boolean isClosed) {
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

	public Integer getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(Integer paidAmount) {
		this.paidAmount = paidAmount;
	}

	@Override
	public String toString() {
		return "OrderData{" +
				"modifiedTime='" + modifiedTime + '\'' +
				", openTime='" + openTime + '\'' +
				", tableId='" + tableId + '\'' +
				", status='" + status + '\'' +
				", restaurantId='" + restaurantId + '\'' +
				", id='" + id + '\'' +
				", isClosed=" + isClosed +
				'}';
	}
}
