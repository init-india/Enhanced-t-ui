package ohi.andre.consolelauncher.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.net.Uri;
import android.telephony.TelephonyManager;

import ohi.andre.consolelauncher.managers.CallManager;
import ohi.andre.consolelauncher.managers.notifications.NotificationManager;
import ohi.andre.consolelauncher.tuils.Tuils;

public class CallReceiver extends BroadcastReceiver {

    private static CallManager callManager;

    // Initialize CallManager once
    public static void setCallManager(CallManager manager) {
        callManager = manager;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || callManager == null) return;

        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

        if (incomingNumber == null) return;

        String displayName = resolveContactName(context, incomingNumber);

        NotificationManager manager = NotificationManager.getInstance(context);

        if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
            // Incoming call alert
            manager.push("Incoming call from: " + displayName + " (" + incomingNumber + ")");
            callManager.onIncomingCall(incomingNumber, displayName);
        } else if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)) {
            // Call answered
            manager.push("Call active with: " + displayName);
        } else if (TelephonyManager.EXTRA_STATE_IDLE.equals(state)) {
            // Call ended or missed
            callManager.onCallEnded(incomingNumber);
        }
    }

    private String resolveContactName(Context context, String number) {
        if (number == null) return number;

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String name = number;

        try (android.database.Cursor cursor = context.getContentResolver()
                .query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                name = cursor.getString(0);
            }
        } catch (Exception e) {
            // ignore
        }

        return name;
    }
}
