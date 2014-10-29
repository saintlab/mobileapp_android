package com.omnom.android.restaurateur.model.cards;

import com.google.gson.annotations.Expose;
import com.omnom.android.restaurateur.model.ResponseBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ch3D on 27.10.2014.
 */
public class CardsResponse extends ResponseBase {
	@Expose
	private List<Card> cards = new ArrayList<Card>();

	public List<Card> getCards() {
		return cards;
	}

	public void setCards(List<Card> cards) {
		this.cards = cards;
	}
}
