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

}
