package com.omnom.android.menu.model;


final class AutoParcel_Schedule extends Schedule {
  private final boolean forever;

  AutoParcel_Schedule(
      boolean forever) {
    this.forever = forever;
  }

  @Override
  public boolean forever() {
    return forever;
  }

  @Override
  public String toString() {
    return "Schedule{"
        + "forever=" + forever
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof Schedule) {
      Schedule that = (Schedule) o;
      return (this.forever == that.forever());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= forever ? 1231 : 1237;
    return h;
  }



  public static final android.os.Parcelable.Creator<Schedule> CREATOR = new android.os.Parcelable.Creator<Schedule>() {
    @Override public Schedule createFromParcel(android.os.Parcel in) {
      return new AutoParcel_Schedule(in);
    }
    @Override public Schedule[] newArray(int size) {
      return new Schedule[size];
    }
  };

  private final static java.lang.ClassLoader CL = AutoParcel_Schedule.class.getClassLoader();

  private AutoParcel_Schedule(android.os.Parcel in) {
    this(
      (Boolean) in.readValue(CL));
  }

  @Override public void writeToParcel(android.os.Parcel dest, int flags) {
    dest.writeValue(forever);

  }

  @Override public int describeContents() {
    return 0;
  }

}
