package com.omnom.android.menu.model;


final class AutoParcel_Details extends Details {
  private final int weight;
  private final int energyTotal;

  AutoParcel_Details(
      int weight,
      int energyTotal) {
    this.weight = weight;
    this.energyTotal = energyTotal;
  }

  @Override
  public int weight() {
    return weight;
  }

  @Override
  public int energyTotal() {
    return energyTotal;
  }

  @Override
  public String toString() {
    return "Details{"
        + "weight=" + weight
        + ", energyTotal=" + energyTotal
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof Details) {
      Details that = (Details) o;
      return (this.weight == that.weight())
          && (this.energyTotal == that.energyTotal());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= weight;
    h *= 1000003;
    h ^= energyTotal;
    return h;
  }



  public static final android.os.Parcelable.Creator<Details> CREATOR = new android.os.Parcelable.Creator<Details>() {
    @Override public Details createFromParcel(android.os.Parcel in) {
      return new AutoParcel_Details(in);
    }
    @Override public Details[] newArray(int size) {
      return new Details[size];
    }
  };

  private final static java.lang.ClassLoader CL = AutoParcel_Details.class.getClassLoader();

  private AutoParcel_Details(android.os.Parcel in) {
    this(
      (Integer) in.readValue(CL),
      (Integer) in.readValue(CL));
  }

  @Override public void writeToParcel(android.os.Parcel dest, int flags) {
    dest.writeValue(weight);
    dest.writeValue(energyTotal);

  }

  @Override public int describeContents() {
    return 0;
  }

}
