
package org.hawkular.client.android.backend.model;

import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;

public class Resource implements Parcelable
{

    private String id;
    private List<Data> data = null;
    public final static Creator<Resource> CREATOR = new Creator<Resource>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Resource createFromParcel(Parcel in) {
            Resource instance = new Resource();
            instance.id = ((String) in.readValue((String.class.getClassLoader())));
            in.readList(instance.data, (Data.class.getClassLoader()));
            return instance;
        }

        public Resource[] newArray(int size) {
            return (new Resource[size]);
        }

    }
    ;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeList(data);
    }

    public int describeContents() {
        return  0;
    }

}
