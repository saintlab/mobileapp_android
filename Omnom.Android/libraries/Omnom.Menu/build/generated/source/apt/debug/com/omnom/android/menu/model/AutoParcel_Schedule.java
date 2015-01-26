package com.omnom.android.menu.model;


final class AutoParcel_Schedule extends Schedule {

  AutoParcel_Schedule(
      ) {

  }


  @Override
  public String toString() {
    return "Schedule{"
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof Schedule) {
      Schedule that = (Schedule) o;
      return true;
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    return h;
  }

}
