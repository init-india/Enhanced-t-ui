package ohi.andre.consolelauncher.managers;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.telephony.TelephonyManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ohi.andre.consolelauncher.tuils.Tuils;

public class CallManager {

    public enum CallStatus {
        INCOMING, ANSWERED, REJECTED, MISSED, OUTGOING
    }

    public static class CallRecord {
        public String contactName;
        public String number;
        public long timestamp;
        public CallStatus status;

        public CallRecord(String contactName, String number, long timestamp, CallStatus status) {
            this.contactName = contactName;
            this.number = number;
            this.timestamp = timestamp;
            this.status = status;
        }

        @Override
        public String toString() {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            return "[" + status + "] " + contactName + " (" + number + ") at " + sdf.format(new Date(timestamp));
        }
    }

    private final Context context;
    private final List<CallRecord> callHistory;
    private CallRecord activeCall;
    private Ringtone ringtone;

    public CallManager(Context context) {
        this.context = context;
        this.callHistory = new ArrayList<>();
        this.activeCall = null;
    }

    // CallReceiver will call this on incoming call
    public void onIncomingCall(String number, String contactName) {
        long now = System.currentTimeMillis();
        CallRecord record = new CallRecord(contactName, number, now, CallStatus.INCOMING);
        callHistory.add(record);
        activeCall = record;

        // CLI notification
        Tuils.sendOutput(0xFFFFFF, context, "Incoming call from: " + contactName + " (" + number + ")");

        // Vibrate and play ringtone
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(1000);
        }

        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            ringtone = RingtoneManager.getRingtone(context, notification);
            if (ringtone != null) {
                ringtone.setStreamType(AudioManager.STREAM_RING);
                ringtone.play();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // User types 'ans' in CLI
    public void answerCall() {
        if (activeCall != null && activeCall.status == CallStatus.INCOMING) {
            activeCall.status = CallStatus.ANSWERED;
            stopRingtone();
            Tuils.sendOutput(0x00FF00, context, "Call answered: " + activeCall.contactName);

            // TODO: Integrate telephony API to actually answer if app is default dialer
        }
    }

    // User types 'rej' in CLI
    public void rejectCall() {
        if (activeCall != null && activeCall.status == CallStatus.INCOMING) {
            activeCall.status = CallStatus.REJECTED;
            stopRingtone();
            Tuils.sendOutput(0xFF0000, context, "Call rejected: " + activeCall.contactName);

            // TODO: Integrate telephony API to actually reject if app is default dialer
        }
    }

    // Stop ringtone
    private void stopRingtone() {
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
        }
        activeCall = null;
    }

    // Called on call ended or missed
    public void onCallEnded(String number) {
        if (activeCall != null && activeCall.number.equals(number)) {
            if (activeCall.status == CallStatus.INCOMING) {
                activeCall.status = CallStatus.MISSED;
                Tuils.sendOutput(0xFFFF00, context, "Missed call: " + activeCall.contactName);
            }
            stopRingtone();
        }
    }

    // Show all calls
    public void listAllCalls() {
        if (callHistory.isEmpty()) {
            Tuils.sendOutput(0xFFFFFF, context, "No calls yet.");
            return;
        }
        for (CallRecord record : callHistory) {
            Tuils.sendOutput(0xFFFFFF, context, record.toString());
        }
    }

    // Show calls by contact
    public void listCallsByContact(String contactName) {
        boolean found = false;
        for (CallRecord record : callHistory) {
            if (record.contactName.equalsIgnoreCase(contactName)) {
                Tuils.sendOutput(0xFFFFFF, context, record.toString());
                found = true;
            }
        }
        if (!found) {
            Tuils.sendOutput(0xFFFFFF, context, "No call history for: " + contactName);
        }
    }

    // Initiate outgoing call
    public void callContact(String number, String contactName) {
        long now = System.currentTimeMillis();
        CallRecord record = new CallRecord(contactName, number, now, CallStatus.OUTGOING);
        callHistory.add(record);
        activeCall = record;
        Tuils.sendOutput(0x00FF00, context, "Calling " + contactName + " (" + number + ")...");

        // TODO: integrate actual telephony call if default dialer
    }
}
