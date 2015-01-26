package com.omnom.android.menu.model;


final class AutoParcel_Modifiers extends Modifiers {

  AutoParcel_Modifiers(
      ) {

  }


  @Override
  public String toString() {
    return "Modifiers{"
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof Modifiers) {
      Modifiers that = (Modifiers) o;
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
