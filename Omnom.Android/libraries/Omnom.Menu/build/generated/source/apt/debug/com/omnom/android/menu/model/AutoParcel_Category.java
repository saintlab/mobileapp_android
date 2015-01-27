package com.omnom.android.menu.model;

import java.util.List;

final class AutoParcel_Category extends Category {
  private final int id;
  private final int parentId;
  private final String name;
  private final String description;
  private final int sort;
  private final Schedule schedule;
  private final List<Child> children;

  AutoParcel_Category(
      int id,
      int parentId,
      String name,
      String description,
      int sort,
      Schedule schedule,
      List<Child> children) {
    this.id = id;
    this.parentId = parentId;
    this.name = name;
    this.description = description;
    this.sort = sort;
    this.schedule = schedule;
    this.children = children;
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
  public List<Child> children() {
    return children;
  }

  @Override
  public String toString() {
    return "Category{"
        + "id=" + id
        + ", parentId=" + parentId
        + ", name=" + name
        + ", description=" + description
        + ", sort=" + sort
        + ", schedule=" + schedule
        + ", children=" + children
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof Category) {
      Category that = (Category) o;
      return (this.id == that.id())
          && (this.parentId == that.parentId())
          && ((this.name == null) ? (that.name() == null) : this.name.equals(that.name()))
          && ((this.description == null) ? (that.description() == null) : this.description.equals(that.description()))
          && (this.sort == that.sort())
          && ((this.schedule == null) ? (that.schedule() == null) : this.schedule.equals(that.schedule()))
          && ((this.children == null) ? (that.children() == null) : this.children.equals(that.children()));
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
    h ^= (children == null) ? 0 : children.hashCode();
    return h;
  }



  public static final android.os.Parcelable.Creator<Category> CREATOR = new android.os.Parcelable.Creator<Category>() {
    @Override public Category createFromParcel(android.os.Parcel in) {
      return new AutoParcel_Category(in);
    }
    @Override public Category[] newArray(int size) {
      return new Category[size];
    }
  };

  private final static java.lang.ClassLoader CL = AutoParcel_Category.class.getClassLoader();

  private AutoParcel_Category(android.os.Parcel in) {
    this(
      (Integer) in.readValue(CL),
      (Integer) in.readValue(CL),
      (String) in.readValue(CL),
      (String) in.readValue(CL),
      (Integer) in.readValue(CL),
      (Schedule) in.readValue(CL),
      (List<Child>) in.readValue(CL));
  }

  @Override public void writeToParcel(android.os.Parcel dest, int flags) {
    dest.writeValue(id);
    dest.writeValue(parentId);
    dest.writeValue(name);
    dest.writeValue(description);
    dest.writeValue(sort);
    dest.writeValue(schedule);
    dest.writeValue(children);

  }

  @Override public int describeContents() {
    return 0;
  }

}
