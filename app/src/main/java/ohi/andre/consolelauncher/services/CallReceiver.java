package ohi.andre.consolelauncher.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class CallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            Toast.makeText(context, "Incoming call: " + incomingNumber, Toast.LENGTH_SHORT).show();

            // TODO: Forward this info to T-UI (UIManager or a custom notifier)
        } 
        else if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)) {
            Toast.makeText(context, "Call answered", Toast.LENGTH_SHORT).show();
        } 
        else if (TelephonyManager.EXTRA_STATE_IDLE.equals(state)) {
            Toast.makeText(context, "Call ended / Rejected", Toast.LENGTH_SHORT).show();
        }
    }
}
