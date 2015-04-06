package com.omnom.android.menu.api;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.omnom.android.menu.model.Item;
import com.omnom.android.menu.model.Items;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

/**
 * Created by Ch3D on 26.01.2015.
 */
public class ItemsSerializer implements JsonSerializer<Items>, JsonDeserializer<Items> {
	@Override
	public Items deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
			throws JsonParseException {
		final Items items = Items.create();
		final JsonObject jobj = json.getAsJsonObject();

		final Set<Map.Entry<String, JsonElement>> entries = jobj.entrySet();

		for(Map.Entry<String, JsonElement> entry : entries) {
			final Item deserialize = context.deserialize(entry.getValue(), Item.class);
			items.items().put(deserialize.id(), deserialize);
		}
		return items;
	}

	@Override
	public JsonElement serialize(final Items src, final Type typeOfSrc, final JsonSerializationContext context) {
		return null;
	}
}
