package com.omnom.android.menu.model;

import java.util.List;

final class AutoParcel_Items extends Items {
  private final List<Item> items;

  AutoParcel_Items(
      List<Item> items) {
    if (items == null) {
      throw new NullPointerException("Null items");
    }
    this.items = items;
  }

  @Override
  public List<Item> items() {
    return items;
  }

  @Override
  public String toString() {
    return "Items{"
        + "items=" + items
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof Items) {
      Items that = (Items) o;
      return (this.items.equals(that.items()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= items.hashCode();
    return h;
  }

}
