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
    if (name == null) {
      throw new NullPointerException("Null name");
    }
    this.name = name;
    if (description == null) {
      throw new NullPointerException("Null description");
    }
    this.description = description;
    this.sort = sort;
    if (schedule == null) {
      throw new NullPointerException("Null schedule");
    }
    this.schedule = schedule;
    if (children == null) {
      throw new NullPointerException("Null children");
    }
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
          && (this.name.equals(that.name()))
          && (this.description.equals(that.description()))
          && (this.sort == that.sort())
          && (this.schedule.equals(that.schedule()))
          && (this.children.equals(that.children()));
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
    h ^= name.hashCode();
    h *= 1000003;
    h ^= description.hashCode();
    h *= 1000003;
    h ^= sort;
    h *= 1000003;
    h ^= schedule.hashCode();
    h *= 1000003;
    h ^= children.hashCode();
    return h;
  }

}
