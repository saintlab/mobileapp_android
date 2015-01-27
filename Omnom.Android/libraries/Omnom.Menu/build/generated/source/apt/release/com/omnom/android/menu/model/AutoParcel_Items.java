package com.omnom.android.menu.model;

import java.util.Map;

final class AutoParcel_Items extends Items {
  private final Map<String, Item> items;

  AutoParcel_Items(
      Map<String, Item> items) {
    this.items = items;
  }

  @Override
  public Map<String, Item> items() {
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
      return ((this.items == null) ? (that.items() == null) : this.items.equals(that.items()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= (items == null) ? 0 : items.hashCode();
    return h;
  }



  public static final android.os.Parcelable.Creator<Items> CREATOR = new android.os.Parcelable.Creator<Items>() {
    @Override public Items createFromParcel(android.os.Parcel in) {
      return new AutoParcel_Items(in);
    }
    @Override public Items[] newArray(int size) {
      return new Items[size];
    }
  };

  private final static java.lang.ClassLoader CL = AutoParcel_Items.class.getClassLoader();

  private AutoParcel_Items(android.os.Parcel in) {
    this(
      (Map<String, Item>) in.readValue(CL));
  }

  @Override public void writeToParcel(android.os.Parcel dest, int flags) {
    dest.writeValue(items);

  }

  @Override public int describeContents() {
    return 0;
  }

}
