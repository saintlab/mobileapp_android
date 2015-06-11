package com.omnom.android.restaurateur.model.restaurant;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ch3D on 09.02.2015.
 */
public class WishRequestItem {

	public static final String ID_OMNOM_TIPS = "omnom-tips";

	public static WishRequestItem createTip(final int tipsQuantity) {
		return new WishRequestItem(ID_OMNOM_TIPS, tipsQuantity);
	}

	@Expose
	private String id;

	@Expose
	private int quantity;

	private List<ModifierRequestItem> modifiers;

	public WishRequestItem() {
		modifiers = new ArrayList<ModifierRequestItem>();
	}

	public WishRequestItem(String id, int quantity) {
		this.id = id;
		this.quantity = quantity;
		modifiers = new ArrayList<ModifierRequestItem>();
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
