package com.omnom.android.restaurateur.model.restaurant;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by Ch3D on 09.02.2015.
 */
public class WishRequestItem {
	@Expose
	public String id;

	@Expose
	public int quantity;

	public List<ModifierRequestItem> modifiers;

	public WishRequestItem() {

	}

	public WishRequestItem(String id, int quantity) {
		this.id = id;
		this.quantity = quantity;
	}

	public List<ModifierRequestItem> getModifiers() {
		return modifiers;
	}

	public void setModifiers(final List<ModifierRequestItem> modifiers) {
		this.modifiers = modifiers;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(final int quantity) {
		this.quantity = quantity;
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}
}
