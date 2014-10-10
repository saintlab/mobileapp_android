package com.omnom.android.restaurateur.model.order;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ch3D on 10.10.2014.
 */
public class OrderItem {
	@Expose
	private String guestId;
	@Expose
	private Integer quantity;
	@Expose
	private Integer priceTotal;
	@Expose
	private Integer pricePerItem;
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

	public String getGuestId() {
		return guestId;
	}

	public void setGuestId(String guestId) {
		this.guestId = guestId;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Integer getPriceTotal() {
		return priceTotal;
	}

	public void setPriceTotal(Integer priceTotal) {
		this.priceTotal = priceTotal;
	}

	public Integer getPricePerItem() {
		return pricePerItem;
	}

	public void setPricePerItem(Integer pricePerItem) {
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
}
