package com.omnom.android.menu.api;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.omnom.android.menu.model.Modifier;
import com.omnom.android.menu.model.Modifiers;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

/**
 * Created by Ch3D on 05.02.2015.
 */
public class ModifiersSerializer implements JsonSerializer<Modifiers>, JsonDeserializer<Modifiers> {
	@Override
	public Modifiers deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
			throws JsonParseException {
		final Modifiers items = Modifiers.create();
		final JsonObject jobj = json.getAsJsonObject();

		final Set<Map.Entry<String, JsonElement>> entries = jobj.entrySet();

		for(Map.Entry<String, JsonElement> entry : entries) {
			final Modifier deserialize = context.deserialize(entry.getValue(), Modifier.class);
			items.items().put(deserialize.id(), deserialize);
		}
		return items;
	}

	@Override
	public JsonElement serialize(final Modifiers src, final Type typeOfSrc, final JsonSerializationContext context) {
		return null;
	}
}
