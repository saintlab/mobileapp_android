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
    this.restaurantId = restaurantId;
    this.items = items;
    this.modifiers = modifiers;
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
      return ((this.restaurantId == null) ? (that.restaurantId() == null) : this.restaurantId.equals(that.restaurantId()))
          && ((this.items == null) ? (that.items() == null) : this.items.equals(that.items()))
          && ((this.modifiers == null) ? (that.modifiers() == null) : this.modifiers.equals(that.modifiers()))
          && ((this.categories == null) ? (that.categories() == null) : this.categories.equals(that.categories()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= (restaurantId == null) ? 0 : restaurantId.hashCode();
    h *= 1000003;
    h ^= (items == null) ? 0 : items.hashCode();
    h *= 1000003;
    h ^= (modifiers == null) ? 0 : modifiers.hashCode();
    h *= 1000003;
    h ^= (categories == null) ? 0 : categories.hashCode();
    return h;
  }



  public static final android.os.Parcelable.Creator<Menu> CREATOR = new android.os.Parcelable.Creator<Menu>() {
    @Override public Menu createFromParcel(android.os.Parcel in) {
      return new AutoParcel_Menu(in);
    }
    @Override public Menu[] newArray(int size) {
      return new Menu[size];
    }
  };

  private final static java.lang.ClassLoader CL = AutoParcel_Menu.class.getClassLoader();

  private AutoParcel_Menu(android.os.Parcel in) {
    this(
      (String) in.readValue(CL),
      (Items) in.readValue(CL),
      (Modifiers) in.readValue(CL),
      (List<Category>) in.readValue(CL));
  }

  @Override public void writeToParcel(android.os.Parcel dest, int flags) {
    dest.writeValue(restaurantId);
    dest.writeValue(items);
    dest.writeValue(modifiers);
    dest.writeValue(categories);

  }

  @Override public int describeContents() {
    return 0;
  }

}
