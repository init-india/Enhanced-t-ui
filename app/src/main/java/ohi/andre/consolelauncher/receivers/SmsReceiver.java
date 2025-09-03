package ohi.andre.consolelauncher.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.media.RingtoneManager;
import android.media.Ringtone;
import android.telephony.SmsMessage;

import ohi.andre.consolelauncher.managers.SmsManagerConsole;

public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle == null) return;

        Object[] pdus = (Object[]) bundle.get("pdus");
        if (pdus == null) return;

        for (Object pdu : pdus) {
            SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
            String sender = sms.getDisplayOriginatingAddress();
            String body = sms.getMessageBody();

            // 👉 Vibrate + beep
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (v != null) v.vibrate(500);

            try {
                Ringtone r = RingtoneManager.getRingtone(context, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                if (r != null) r.play();
            } catch (Exception e) {
                // ignore
            }

            // 👉 Pass SMS to manager
            SmsManagerConsole.getInstance(context).onSmsReceived(sender, body);
        }
    }
}
