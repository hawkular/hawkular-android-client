
package org.hawkular.client.android.backend.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class Tags implements Parcelable
{

    private String chunks;
    private String size;
    public final static Creator<Tags> CREATOR = new Creator<Tags>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Tags createFromParcel(Parcel in) {
            Tags instance = new Tags();
            instance.chunks = ((String) in.readValue((String.class.getClassLoader())));
            instance.size = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public Tags[] newArray(int size) {
            return (new Tags[size]);
        }

    }
    ;

    public String getChunks() {
        return chunks;
    }

    public void setChunks(String chunks) {
        this.chunks = chunks;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(chunks);
        dest.writeValue(size);
    }

    public int describeContents() {
        return  0;
    }

}
