package com.omnom.android.menu.model;


final class AutoParcel_Item extends Item {
  private final String id;
  private final String name;
  private final String description;
  private final String photo;
  private final long price;

  AutoParcel_Item(
      String id,
      String name,
      String description,
      String photo,
      long price) {
    if (id == null) {
      throw new NullPointerException("Null id");
    }
    this.id = id;
    if (name == null) {
      throw new NullPointerException("Null name");
    }
    this.name = name;
    if (description == null) {
      throw new NullPointerException("Null description");
    }
    this.description = description;
    if (photo == null) {
      throw new NullPointerException("Null photo");
    }
    this.photo = photo;
    this.price = price;
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
  public String toString() {
    return "Item{"
        + "id=" + id
        + ", name=" + name
        + ", description=" + description
        + ", photo=" + photo
        + ", price=" + price
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof Item) {
      Item that = (Item) o;
      return (this.id.equals(that.id()))
          && (this.name.equals(that.name()))
          && (this.description.equals(that.description()))
          && (this.photo.equals(that.photo()))
          && (this.price == that.price());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= id.hashCode();
    h *= 1000003;
    h ^= name.hashCode();
    h *= 1000003;
    h ^= description.hashCode();
    h *= 1000003;
    h ^= photo.hashCode();
    h *= 1000003;
    h ^= (price >>> 32) ^ price;
    return h;
  }

}
