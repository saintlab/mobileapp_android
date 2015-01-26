package com.omnom.android.menu.model;


final class AutoParcel_Child extends Child {
  private final int id;
  private final int parentId;
  private final String name;
  private final String description;
  private final int sort;
  private final Schedule schedule;

  AutoParcel_Child(
      int id,
      int parentId,
      String name,
      String description,
      int sort,
      Schedule schedule) {
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
  public String toString() {
    return "Child{"
        + "id=" + id
        + ", parentId=" + parentId
        + ", name=" + name
        + ", description=" + description
        + ", sort=" + sort
        + ", schedule=" + schedule
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
          && (this.name.equals(that.name()))
          && (this.description.equals(that.description()))
          && (this.sort == that.sort())
          && (this.schedule.equals(that.schedule()));
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
    return h;
  }

}
