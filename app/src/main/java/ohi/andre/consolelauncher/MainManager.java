package ohi.andre.consolelauncher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;

import ohi.andre.consolelauncher.managers.AppsManager;
import ohi.andre.consolelauncher.managers.ContactManager;
import ohi.andre.consolelauncher.managers.XMLPrefsManager;
import ohi.andre.consolelauncher.tuils.Tuils;

public class MainManager {

    private static MainManager instance;

    private Context context;
    private AppsManager appsManager;
    private ContactManager contactManager;

    public static MainManager getInstance(Context context) {
        if (instance == null) {
            instance = new MainManager(context);
        }
        return instance;
    }

    private MainManager(Context context) {
        this.context = context;
        this.appsManager = new AppsManager(context);
        this.contactManager = new ContactManager(context);

        initReceivers();
    }

    private void initReceivers() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");

        context.registerReceiver(appsBroadcast, filter);
    }

    private BroadcastReceiver appsBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String data = intent.getData() != null ? intent.getData().getSchemeSpecificPart() : null;

            if (action == null || data == null) return;

            if (action.equals(Intent.ACTION_PACKAGE_ADDED)) {
                appsManager.appInstalled(data);
            } else if (action.equals(Intent.ACTION_PACKAGE_REMOVED)) {
                appsManager.appUninstalled(data);
            }
        }
    };

    public AppsManager getAppsManager() {
        return appsManager;
    }

    public ContactManager getContactManager() {
        return contactManager;
    }

    /**
     * Sends a CLI output message safely.
     */
    public void sendOutput(@NonNull String message) {
        Tuils.sendOutput(message);
    }

    /**
     * Returns list of installed app labels.
     */
    @NonNull
    public List<String> getInstalledAppsLabels() {
        List<String> labels = new ArrayList<>();
        List<AppsManager.LaunchInfo> apps = appsManager.getAppsHolder().getApps();
        for (AppsManager.LaunchInfo info : apps) {
            labels.add(info.label);
        }
        return labels;
    }

    /**
     * Returns list of contact names.
     */
    @NonNull
    public List<String> getContacts() {
        return contactManager.getAllContactNames();
    }

    /**
     * Returns numbers for a given contact.
     */
    @NonNull
    public List<String> getNumbers(String contactName) {
        return contactManager.getNumbersByContact(contactName);
    }

    /**
     * Cleanup receivers when app exits.
     */
    public void cleanup() {
        try {
            context.unregisterReceiver(appsBroadcast);
        } catch (Exception e) {
            Tuils.log(e);
        }
    }
}
