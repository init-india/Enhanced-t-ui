package ohi.andre.consolelauncher.managers;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import ohi.andre.consolelauncher.tuils.Tuils;

public class MessagesManager {

    private final Context context;
    private final Vibrator vibrator;
    private final HashMap<String, List<SMS>> smsHistory; // contactName -> list of SMS
    private boolean cliUnlocked = false; // simulation of CLI unlock / biometric auth

    public MessagesManager(Context context) {
        this.context = context;
        this.vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        this.smsHistory = new HashMap<>();
    }

    // Represents an individual SMS
    public static class SMS {
        public final String sender;
        public final String message;
        public final long timestamp;
        public boolean viewed;

        public SMS(String sender, String message) {
            this.sender = sender;
            this.message = message;
            this.timestamp = System.currentTimeMillis();
            this.viewed = false;
        }

        public String firstLine() {
            String[] lines = message.split("\n");
            return lines.length > 0 ? lines[0] : message;
        }

        public String fullMessage() {
            return message;
        }

        public String timeString() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            return sdf.format(new Date(timestamp));
        }
    }

    // Receive a new SMS
    public void receiveSMS(String sender, String message) {
        // Add to history
        smsHistory.computeIfAbsent(sender, k -> new ArrayList<>()).add(new SMS(sender, message));

        // Alert CLI
        alertCLI(sender, message);
    }

    // Vibrate + beep alert
    private void alertCLI(String sender, String message) {
        // Beep
        Tuils.sendOutput(0xFFFF00, context, "[SMS] New message from: " + sender);

        // Vibrate (500ms)
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(500);
            }
        }

        // Show first line only
        SMS sms = smsHistory.get(sender).get(smsHistory.get(sender).size() - 1);
        Tuils.sendOutput(0xFFFFFF, context, sms.firstLine());
    }

    // Unlock CLI (biometric simulation)
    public void unlockCLI() {
        cliUnlocked = true;
    }

    // Lock CLI
    public void lockCLI() {
        cliUnlocked = false;
    }

    // List all contacts with unread messages (show first line)
    public void listSMS() {
        for (String contact : smsHistory.keySet()) {
            List<SMS> list = smsHistory.get(contact);
            if (list.isEmpty()) continue;

            SMS last = list.get(list.size() - 1);
            Tuils.sendOutput(0x00FF00, context,
                    contact + " | " + last.timeString() + " | " + last.firstLine());
        }
    }

    // Show full history for a contact (only if CLI unlocked)
    public void showHistory(String contact) {
        if (!cliUnlocked) {
            Tuils.sendOutput(0xFF0000, context, "CLI locked. Unlock to view full history.");
            return;
        }

        List<SMS> list = smsHistory.get(contact);
        if (list == null || list.isEmpty()) {
            Tuils.sendOutput(0xFFFFFF, context, "No messages for " + contact);
            return;
        }

        for (SMS sms : list) {
            Tuils.sendOutput(0xFFFFFF, context,
                    sms.timeString() + " | " + sms.sender + " | " + sms.fullMessage());
            sms.viewed = true;
        }
    }

    // Reply to a contact
    public void replySMS(String contact, String replyMessage) {
        List<SMS> list = smsHistory.get(contact);
        if (list == null) {
            Tuils.sendOutput(0xFF0000, context, "No messages for " + contact);
            return;
        }

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(contact, null, replyMessage, null, null);
            Tuils.sendOutput(0x00FF00, context, "Sent reply to " + contact);
            receiveSMS(contact, replyMessage); // also store reply in history
        } catch (Exception e) {
            Tuils.sendOutput(0xFF0000, context, "Failed to send SMS: " + e.getMessage());
        }
    }

    // Count total messages
    public int totalMessages() {
        int count = 0;
        for (List<SMS> list : smsHistory.values()) {
            count += list.size();
        }
        return count;
    }

}
