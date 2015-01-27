package com.omnom.android.menu.model;


final class AutoParcel_Item extends Item {
  private final String id;
  private final String name;
  private final String description;
  private final String photo;
  private final long price;
  private final Details details;

  AutoParcel_Item(
      String id,
      String name,
      String description,
      String photo,
      long price,
      Details details) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.photo = photo;
    this.price = price;
    this.details = details;
  }

  @Override
  public String id() {
    return id;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public String description() {
    return description;
  }

  @Override
  public String photo() {
    return photo;
  }

  @Override
  public long price() {
    return price;
  }

  @Override
  public Details details() {
    return details;
  }

  @Override
  public String toString() {
    return "Item{"
        + "id=" + id
        + ", name=" + name
        + ", description=" + description
        + ", photo=" + photo
        + ", price=" + price
        + ", details=" + details
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof Item) {
      Item that = (Item) o;
      return ((this.id == null) ? (that.id() == null) : this.id.equals(that.id()))
          && ((this.name == null) ? (that.name() == null) : this.name.equals(that.name()))
          && ((this.description == null) ? (that.description() == null) : this.description.equals(that.description()))
          && ((this.photo == null) ? (that.photo() == null) : this.photo.equals(that.photo()))
          && (this.price == that.price())
          && ((this.details == null) ? (that.details() == null) : this.details.equals(that.details()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= (id == null) ? 0 : id.hashCode();
    h *= 1000003;
    h ^= (name == null) ? 0 : name.hashCode();
    h *= 1000003;
    h ^= (description == null) ? 0 : description.hashCode();
    h *= 1000003;
    h ^= (photo == null) ? 0 : photo.hashCode();
    h *= 1000003;
    h ^= (price >>> 32) ^ price;
    h *= 1000003;
    h ^= (details == null) ? 0 : details.hashCode();
    return h;
  }



  public static final android.os.Parcelable.Creator<Item> CREATOR = new android.os.Parcelable.Creator<Item>() {
    @Override public Item createFromParcel(android.os.Parcel in) {
      return new AutoParcel_Item(in);
    }
    @Override public Item[] newArray(int size) {
      return new Item[size];
    }
  };

  private final static java.lang.ClassLoader CL = AutoParcel_Item.class.getClassLoader();

  private AutoParcel_Item(android.os.Parcel in) {
    this(
      (String) in.readValue(CL),
      (String) in.readValue(CL),
      (String) in.readValue(CL),
      (String) in.readValue(CL),
      (Long) in.readValue(CL),
      (Details) in.readValue(CL));
  }

  @Override public void writeToParcel(android.os.Parcel dest, int flags) {
    dest.writeValue(id);
    dest.writeValue(name);
    dest.writeValue(description);
    dest.writeValue(photo);
    dest.writeValue(price);
    dest.writeValue(details);

  }

  @Override public int describeContents() {
    return 0;
  }

}
