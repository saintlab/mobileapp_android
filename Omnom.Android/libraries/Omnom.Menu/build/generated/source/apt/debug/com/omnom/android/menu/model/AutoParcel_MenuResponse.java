package com.omnom.android.menu.model;


final class AutoParcel_MenuResponse extends MenuResponse {
  private final Menu menu;

  AutoParcel_MenuResponse(
      Menu menu) {
    if (menu == null) {
      throw new NullPointerException("Null menu");
    }
    this.menu = menu;
  }

  @Override
  public Menu menu() {
    return menu;
  }

  @Override
  public String toString() {
    return "MenuResponse{"
        + "menu=" + menu
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof MenuResponse) {
      MenuResponse that = (MenuResponse) o;
      return (this.menu.equals(that.menu()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= menu.hashCode();
    return h;
  }

}
