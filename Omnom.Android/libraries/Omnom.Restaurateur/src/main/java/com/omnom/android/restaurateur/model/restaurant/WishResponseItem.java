package com.omnom.android.restaurateur.model.restaurant;

import com.google.gson.annotations.Expose;

/**
 * Created by Ch3D on 20.03.2015.
 */
public class WishResponseItem {
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
	private Boolean isModifier;

	@Expose
	private String id;

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(final Integer quantity) {
		this.quantity = quantity;
	}

	public Integer getPriceTotal() {
		return priceTotal;
	}

	public void setPriceTotal(final Integer priceTotal) {
		this.priceTotal = priceTotal;
	}

	public Integer getPricePerItem() {
		return pricePerItem;
	}

	public void setPricePerItem(final Integer pricePerItem) {
		this.pricePerItem = pricePerItem;
	}

	public String getInternalId() {
		return internalId;
	}

	public void setInternalId(final String internalId) {
		this.internalId = internalId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public Boolean getIsModifier() {
		return isModifier;
	}

	public void setIsModifier(final Boolean isModifier) {
		this.isModifier = isModifier;
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}
}
