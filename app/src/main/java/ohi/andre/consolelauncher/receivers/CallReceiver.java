package ohi.andre.consolelauncher.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;

import ohi.andre.consolelauncher.managers.notifications.NotificationManager;

public class CallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) return;

        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

        String displayName = resolveContactName(context, incomingNumber);

        NotificationManager manager = NotificationManager.create(context);

        if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
            manager.push("Incoming call from: " + (displayName != null ? displayName : incomingNumber));
        } else if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)) {
            manager.push("Call active.");
        } else if (TelephonyManager.EXTRA_STATE_IDLE.equals(state)) {
            manager.push("Call ended or rejected.");
        }
    }

    private String resolveContactName(Context context, String number) {
        if (number == null) return null;

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String name = null;

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
