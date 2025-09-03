package ohi.andre.consolelauncher.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppsHolder {

    private final List<LaunchInfo> apps;

    public AppsHolder(List<LaunchInfo> apps) {
        if (apps != null) this.apps = new ArrayList<>(apps);
        else this.apps = new ArrayList<>();
    }

    public List<LaunchInfo> getApps() {
        return Collections.unmodifiableList(apps);
    }

    public void add(LaunchInfo info) {
        if (!apps.contains(info)) apps.add(info);
    }

    public void remove(LaunchInfo info) {
        apps.remove(info);
    }

    public void update(boolean sort) {
        if (sort) Collections.sort(apps, (o1, o2) -> o1.label.compareToIgnoreCase(o2.label));
    }

    public LaunchInfo findLaunchInfoWithLabel(String label) {
        for (LaunchInfo info : apps) {
            if (info.label.equals(label)) return info;
        }
        return null;
    }

    public void requestSuggestionUpdate(Object unused) {
        // Placeholder for your suggestion update logic
    }

    public static void checkEquality(List<LaunchInfo> hiddenApps) {
        // Placeholder for equality check if needed
    }
}
