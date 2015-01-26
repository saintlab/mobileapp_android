package com.omnom.android.menu.model;

import java.util.List;

final class AutoParcel_Menu extends Menu {
  private final String restaurantId;
  private final Items items;
  private final Modifiers modifiers;
  private final List<Category> categories;

  AutoParcel_Menu(
      String restaurantId,
      Items items,
      Modifiers modifiers,
      List<Category> categories) {
    if (restaurantId == null) {
      throw new NullPointerException("Null restaurantId");
    }
    this.restaurantId = restaurantId;
    if (items == null) {
      throw new NullPointerException("Null items");
    }
    this.items = items;
    if (modifiers == null) {
      throw new NullPointerException("Null modifiers");
    }
    this.modifiers = modifiers;
    if (categories == null) {
      throw new NullPointerException("Null categories");
    }
    this.categories = categories;
  }

  @Override
  public String restaurantId() {
    return restaurantId;
  }

  @Override
  public Items items() {
    return items;
  }

  @Override
  public Modifiers modifiers() {
    return modifiers;
  }

  @Override
  public List<Category> categories() {
    return categories;
  }

  @Override
  public String toString() {
    return "Menu{"
        + "restaurantId=" + restaurantId
        + ", items=" + items
        + ", modifiers=" + modifiers
        + ", categories=" + categories
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof Menu) {
      Menu that = (Menu) o;
      return (this.restaurantId.equals(that.restaurantId()))
          && (this.items.equals(that.items()))
          && (this.modifiers.equals(that.modifiers()))
          && (this.categories.equals(that.categories()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= restaurantId.hashCode();
    h *= 1000003;
    h ^= items.hashCode();
    h *= 1000003;
    h ^= modifiers.hashCode();
    h *= 1000003;
    h ^= categories.hashCode();
    return h;
  }

}
