package com.omnom.android.menu.model;

import com.omnom.android.menu.utils.MenuHelper;
import com.omnom.android.utils.generation.AutoGson;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import auto.parcel.AutoParcel;

/**
 * Created by Ch3D on 26.01.2015.
 */
@AutoParcel
@AutoGson
public abstract class Menu implements Parcelable {

    @Nullable
    public abstract String restaurantId();

    @Nullable
    public abstract Items items();

    @Nullable
    public abstract Modifiers modifiers();

    @Nullable
    public abstract List<Category> categories();

    public final List<Category> getFilledCategories() {
        final List<Category> categories = categories();
        if (categories == null) {
            return Collections.EMPTY_LIST;
        }

        final List<Category> result = new ArrayList<>(categories.size());
        for (Category category : categories) {
            if (category.hasChildsOrItems()) {
                result.add(category);
            }
        }
        return result;
    }

    public boolean isEmpty() {
        final List<Category> filledCategories = getFilledCategories();
        return filledCategories == null || filledCategories.size() == 0;
    }

    public Item findItem(final String id) {
        if (items() != null && items().items() != null) {
            return items().items().get(id);
        }
        return null;
    }

    public boolean hasItem(String id) {
        return MenuHelper.hasItem(this, id);
    }
}
