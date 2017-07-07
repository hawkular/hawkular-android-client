
package org.hawkular.client.android.backend.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Data implements Parcelable
{

    private Long timestamp;
    private String value;
    private Tags tags;
    public final static Creator<Data> CREATOR = new Creator<Data>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Data createFromParcel(Parcel in) {
            Data instance = new Data();
            instance.timestamp = ((Long) in.readValue((Integer.class.getClassLoader())));
            instance.value = ((String) in.readValue((String.class.getClassLoader())));
            instance.tags = ((Tags) in.readValue((Tags.class.getClassLoader())));
            return instance;
        }

        public Data[] newArray(int size) {
            return (new Data[size]);
        }

    }
    ;

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Tags getTags() {
        return tags;
    }

    public void setTags(Tags tags) {
        this.tags = tags;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(timestamp);
        dest.writeValue(value);
        dest.writeValue(tags);
    }

    public int describeContents() {
        return  0;
    }

}
