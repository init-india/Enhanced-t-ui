package ohi.andre.consolelauncher.managers;

import android.content.ComponentName;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import it.andreuzzi.comparestring2.StringableObject;

public class LaunchInfo implements Parcelable, StringableObject, Comparable<LaunchInfo> {

    public ComponentName componentName;
    public String label;
    public int launchedTimes;

    // Add any extra fields needed (e.g., shortcuts)
    public LaunchInfo(ComponentName componentName, String label) {
        this.componentName = componentName;
        this.label = label;
        this.launchedTimes = 0;
    }

    protected LaunchInfo(Parcel in) {
        label = in.readString();
        launchedTimes = in.readInt();
        componentName = in.readParcelable(ComponentName.class.getClassLoader());
    }

    public static final Creator<LaunchInfo> CREATOR = new Creator<LaunchInfo>() {
        @Override
        public LaunchInfo createFromParcel(Parcel in) {
            return new LaunchInfo(in);
        }

        @Override
        public LaunchInfo[] newArray(int size) {
            return new LaunchInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(label);
        parcel.writeInt(launchedTimes);
        parcel.writeParcelable(componentName, flags);
    }

    @Override
    public int compareTo(@NonNull LaunchInfo other) {
        return label.compareToIgnoreCase(other.label);
    }

    @Override
    public String write() {
        return label; // Example implementation
    }
}
