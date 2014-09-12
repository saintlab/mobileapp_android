package com.omnom.android.linker.model.restaurant;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

/**
 * Created by Ch3D on 11.08.2014.
 */
public class Decoration implements Parcelable {

	public static final Creator<Decoration> CREATOR = new Creator<Decoration>() {
		@Override
		public Decoration createFromParcel(Parcel in) {
			return new Decoration(in);
		}

		@Override
		public Decoration[] newArray(int size) {
			return new Decoration[size];
		}
	};

	@Expose
	private String logo;

	@Expose
	private String backgroundImage;

	@Expose
	private String backgroundColor;

	public Decoration(String logo, String backgroundImage, String backgroundColor) {
		this.logo = logo;
		this.backgroundImage = backgroundImage;
		this.backgroundColor = backgroundColor;
	}

	public Decoration(Parcel in) {
		logo = in.readString();
		backgroundColor = in.readString();
		backgroundImage = in.readString();
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getBackgroundImage() {
		return backgroundImage;
	}

	public void setBackgroundImage(String backgroundImage) {
		this.backgroundImage = backgroundImage;
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(logo);
		dest.writeString(backgroundColor);
		dest.writeString(backgroundImage);
	}
}