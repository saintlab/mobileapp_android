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



  public static final android.os.Parcelable.Creator<Modifiers> CREATOR = new android.os.Parcelable.Creator<Modifiers>() {
    @Override public Modifiers createFromParcel(android.os.Parcel in) {
      return new AutoParcel_Modifiers(in);
    }
    @Override public Modifiers[] newArray(int size) {
      return new Modifiers[size];
    }
  };

  private final static java.lang.ClassLoader CL = AutoParcel_Modifiers.class.getClassLoader();

  private AutoParcel_Modifiers(android.os.Parcel in) {
    this(
      );
  }

  @Override public void writeToParcel(android.os.Parcel dest, int flags) {

  }

  @Override public int describeContents() {
    return 0;
  }

}
