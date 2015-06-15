package com.omnom.android.restaurateur.model.restaurant;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ch3D on 09.02.2015.
 */
public class WishRequest {

	public static final String TAG_TAKEAWAY = "take-away";

	@Expose
	private List<WishRequestItem> items;

	@Expose
	private WishComments comments;

	@Expose
	private String tags;

	public String getTags() {
		return tags;
	}

	public void setTags(final String tags) {
		this.tags = tags;
	}

	public WishComments getComments() {
		return comments;
	}

	public void setComments(final WishComments comments) {
		this.comments = comments;
	}

	public List<WishRequestItem> getItems() {
		return items;
	}

	public void setItems(final List<WishRequestItem> items) {
		this.items = items;
	}

	public void addItem(WishRequestItem item) {
		if(items == null) {
			items = new ArrayList<WishRequestItem>();
		}
		items.add(item);
	}

}
