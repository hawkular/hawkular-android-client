package org.hawkular.client.android.backend.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public final class Tenant implements Parcelable
{
	@SerializedName("id")
	private String id;

	public String getId() {
		return id;
	}

	public static Creator<Tenant> CREATOR = new Creator<Tenant>() {
		@Override
		public Tenant createFromParcel(Parcel parcel) {
			return new Tenant(parcel);
		}

		@Override
		public Tenant[] newArray(int size) {
			return new Tenant[size];
		}
	};

	private Tenant(Parcel parcel) {
		this.id = parcel.readString();
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeString(id);
	}

	@Override
	public int describeContents() {
		return 0;
	}
}
