package ohi.andre.consolelauncher.managers;

import android.content.Context;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ohi.andre.consolelauncher.tuils.Tuils;

public class SmsManagerConsole {

    private static SmsManagerConsole instance;

    private final Context context;
    private final HashMap<String, List<String>> history = new HashMap<>();

    private SmsManagerConsole(Context context) {
        this.context = context.getApplicationContext();
    }

    public static synchronized SmsManagerConsole getInstance(Context context) {
        if (instance == null) {
            instance = new SmsManagerConsole(context);
        }
        return instance;
    }

    // Called by SmsReceiver
    public void onSmsReceived(String sender, String body) {
        saveMessage(sender, "IN: " + body);
        Tuils.sendOutput(context, "📩 SMS from " + sender + ": " + body);
    }

    // Called by CLI command when user sends SMS
    public void onSmsSent(String recipient, String body) {
        saveMessage(recipient, "OUT: " + body);
        Tuils.sendOutput(context, "✅ SMS sent to " + recipient + ": " + body);
    }

    private void saveMessage(String contact, String message) {
        List<String> msgs = history.get(contact);
        if (msgs == null) {
            msgs = new ArrayList<>();
            history.put(contact, msgs);
        }
        msgs.add(message);
    }

    public List<String> getHistory(String contact) {
        return history.getOrDefault(contact, new ArrayList<>());
    }

    public HashMap<String, List<String>> getAllHistory() {
        return history;
    }
}
