package com.omnom.android.menu.model;

import java.util.List;

final class AutoParcel_Child extends Child {
  private final int id;
  private final int parentId;
  private final String name;
  private final String description;
  private final int sort;
  private final Schedule schedule;
  private final List<String> items;

  AutoParcel_Child(
      int id,
      int parentId,
      String name,
      String description,
      int sort,
      Schedule schedule,
      List<String> items) {
    this.id = id;
    this.parentId = parentId;
    this.name = name;
    this.description = description;
    this.sort = sort;
    this.schedule = schedule;
    this.items = items;
  }

  @Override
  public int id() {
    return id;
  }

  @Override
  public int parentId() {
    return parentId;
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
  public int sort() {
    return sort;
  }

  @Override
  public Schedule schedule() {
    return schedule;
  }

  @Override
  public List<String> items() {
    return items;
  }

  @Override
  public String toString() {
    return "Child{"
        + "id=" + id
        + ", parentId=" + parentId
        + ", name=" + name
        + ", description=" + description
        + ", sort=" + sort
        + ", schedule=" + schedule
        + ", items=" + items
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof Child) {
      Child that = (Child) o;
      return (this.id == that.id())
          && (this.parentId == that.parentId())
          && ((this.name == null) ? (that.name() == null) : this.name.equals(that.name()))
          && ((this.description == null) ? (that.description() == null) : this.description.equals(that.description()))
          && (this.sort == that.sort())
          && ((this.schedule == null) ? (that.schedule() == null) : this.schedule.equals(that.schedule()))
          && ((this.items == null) ? (that.items() == null) : this.items.equals(that.items()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= id;
    h *= 1000003;
    h ^= parentId;
    h *= 1000003;
    h ^= (name == null) ? 0 : name.hashCode();
    h *= 1000003;
    h ^= (description == null) ? 0 : description.hashCode();
    h *= 1000003;
    h ^= sort;
    h *= 1000003;
    h ^= (schedule == null) ? 0 : schedule.hashCode();
    h *= 1000003;
    h ^= (items == null) ? 0 : items.hashCode();
    return h;
  }



  public static final android.os.Parcelable.Creator<Child> CREATOR = new android.os.Parcelable.Creator<Child>() {
    @Override public Child createFromParcel(android.os.Parcel in) {
      return new AutoParcel_Child(in);
    }
    @Override public Child[] newArray(int size) {
      return new Child[size];
    }
  };

  private final static java.lang.ClassLoader CL = AutoParcel_Child.class.getClassLoader();

  private AutoParcel_Child(android.os.Parcel in) {
    this(
      (Integer) in.readValue(CL),
      (Integer) in.readValue(CL),
      (String) in.readValue(CL),
      (String) in.readValue(CL),
      (Integer) in.readValue(CL),
      (Schedule) in.readValue(CL),
      (List<String>) in.readValue(CL));
  }

  @Override public void writeToParcel(android.os.Parcel dest, int flags) {
    dest.writeValue(id);
    dest.writeValue(parentId);
    dest.writeValue(name);
    dest.writeValue(description);
    dest.writeValue(sort);
    dest.writeValue(schedule);
    dest.writeValue(items);

  }

  @Override public int describeContents() {
    return 0;
  }

}
