package ohi.andre.consolelauncher.tuils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.RemoteInput;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.Set;

public class PrivateIOReceiver extends BroadcastReceiver {

    public static final String ACTION_PRIVATE_REPLY = "ohi.andre.consolelauncher.PRIVATE_REPLY";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) return;

        if (ACTION_PRIVATE_REPLY.equals(intent.getAction())) {
            CharSequence reply = getRemoteInput(intent);
            if (reply != null) {
                Intent broadcastIntent = new Intent("PRIVATE_IO_REPLY");
                broadcastIntent.putExtra("message", reply);
                LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent);
            }
        }
    }

    private CharSequence getRemoteInput(Intent intent) {
        if (intent == null) return null;
        Intent remoteInputIntent = RemoteInput.getResultsFromIntent(intent);
        if (remoteInputIntent == null) return null;

        Set<String> keys = remoteInputIntent.getExtras().keySet();
        if (keys.isEmpty()) return null;

        for (String key : keys) {
            CharSequence value = remoteInputIntent.getCharSequence(key);
            if (value != null) return value;
        }
        return null;
    }
}
