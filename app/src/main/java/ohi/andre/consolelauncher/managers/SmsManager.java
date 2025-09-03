package ohi.andre.consolelauncher.managers;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.telephony.SmsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ohi.andre.consolelauncher.receivers.SmsReceiver;
import ohi.andre.consolelauncher.tuils.Tuils;

/**
 * Manager for handling SMS:
 * - Sending SMS
 * - Storing/Reading SMS history
 * - Vibrate + Beep tone alerts on new messages
 * - CLI-friendly query methods
 */
public class SmsManagerConsole {

    private final Context context;
    private final SmsManager smsManager;
    private final NotificationManager notificationManager;

    // Store messages in-memory for quick CLI access
    private final Map<String, List<String>> messagesByContact = new HashMap<>();

    public SmsManagerConsole(Context context, NotificationManager notificationManager) {
        this.context = context;
        this.notificationManager = notificationManager;
        this.smsManager = SmsManager.getDefault();

        // Register SMS receiver
        IntentFilter filter = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        context.registerReceiver(new SmsReceiver(this), filter);
    }

    /** Send SMS to a number */
    public void sendSms(String number, String message) {
        smsManager.sendTextMessage(number, null, message, null, null);
        addMessage(number, "You: " + message);
        notificationManager.push("SMS sent to " + resolveContactName(number) + ": " + message);
    }

    /** Called by SmsReceiver when a new SMS arrives */
    public void onSmsReceived(String number, String body) {
        addMessage(number, resolveContactName(number) + ": " + body);

        // Vibrate
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(500); // vibrate for 500 ms
        }

        // Beep
        ToneGenerator toneGen = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
        toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 300);

        // Push to CLI
        notificationManager.push("New SMS from " + resolveContactName(number) + ": " + body);
    }

    /** Store message in history */
    private void addMessage(String number, String body) {
        String key = resolveContactName(number);
        if (!messagesByContact.containsKey(key)) {
            messagesByContact.put(key, new ArrayList<>());
        }
        messagesByContact.get(key).add(body);
    }

    /** Get conversation history with a contact */
    public List<String> getConversation(String contactOrNumber) {
        String key = resolveContactName(contactOrNumber);
        return messagesByContact.getOrDefault(key, new ArrayList<>());
    }

    /** Get all SMS history */
    public Map<String, List<String>> getAllMessages() {
        return messagesByContact;
    }

    /** Resolve contact name from number */
    private String resolveContactName(String number) {
        if (number == null) return "Unknown";

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String name = null;

        try (Cursor cursor = context.getContentResolver().query(
                uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                name = cursor.getString(0);
            }
        } catch (Exception ignored) {}

        return name != null ? name : number;
    }
}
