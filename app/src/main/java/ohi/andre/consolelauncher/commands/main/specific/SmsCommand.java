package ohi.andre.consolelauncher.commands.main.specific;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import ohi.andre.consolelauncher.commands.CommandAbstraction;
import ohi.andre.consolelauncher.commands.ExecutePack;
import ohi.andre.consolelauncher.managers.ContactManager;
import ohi.andre.consolelauncher.tuils.Tuils;

/**
 * CLI command: sms
 *
 * - "sms" → list recent SMS threads (contact + 1st line preview).
 * - "sms <contact>" → show conversation history + allow reply.
 */
public class SmsCommand implements CommandAbstraction {

    @Override
    public String onExec(ExecutePack pack) throws Exception {
        Context context = pack.context;
        String[] args = pack.args;

        if (args.length == 0) {
            // List all recent conversations
            return listConversations(context);
        } else {
            // Show history with a specific contact
            String contactName = TextUtils.join(" ", args);
            return showConversation(context, contactName);
        }
    }

    private String listConversations(Context context) {
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(
                Telephony.Sms.Inbox.CONTENT_URI,
                new String[]{Telephony.Sms.ADDRESS, Telephony.Sms.BODY, Telephony.Sms.DATE},
                null,
                null,
                Telephony.Sms.DEFAULT_SORT_ORDER
        );

        if (cursor == null) return "No SMS found.";

        StringBuilder sb = new StringBuilder();
        List<String> seenContacts = new ArrayList<>();

        while (cursor.moveToNext()) {
            String address = cursor.getString(0);
            String body = cursor.getString(1);
            long date = cursor.getLong(2);

            String contact = ContactManager.getContactName(context, address);
            if (contact == null) contact = address;

            if (!seenContacts.contains(contact)) {
                seenContacts.add(contact);

                String preview = body.split("\n")[0];
                if (preview.length() > 30) preview = preview.substring(0, 30) + "...";

                sb.append("[").append(Tuils.formatDate(date))
                  .append("] ").append(contact)
                  .append(": \"").append(preview).append("\"\n");
            }
        }

        cursor.close();

        if (sb.length() == 0) return "No SMS threads found.";
        return sb.toString();
    }

    private String showConversation(Context context, String contactName) {
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(
                Telephony.Sms.CONTENT_URI,
                new String[]{Telephony.Sms.ADDRESS, Telephony.Sms.BODY, Telephony.Sms.TYPE, Telephony.Sms.DATE},
                null,
                null,
                Telephony.Sms.DEFAULT_SORT_ORDER
        );

        if (cursor == null) return "No messages with " + contactName;

        StringBuilder sb = new StringBuilder();
        sb.append("--- SMS with ").append(contactName).append(" ---\n");

        while (cursor.moveToNext()) {
            String address = cursor.getString(0);
            String body = cursor.getString(1);
            int type = cursor.getInt(2);
            long date = cursor.getLong(3);

            String resolved = ContactManager.getContactName(context, address);
            if (resolved == null) resolved = address;

            if (resolved.equalsIgnoreCase(contactName)) {
                String sender = (type == Telephony.Sms.MESSAGE_TYPE_INBOX) ? resolved : "You";
                sb.append("[").append(Tuils.formatDate(date)).append("] ")
                  .append(sender).append(": ").append(body).append("\n");
            }
        }

        cursor.close();
        sb.append("-----------------------\nReply to ").append(contactName).append(":");

        return sb.toString();
    }

    /**
     * Helper: Send SMS to a contact.
     */
    public static String sendSms(Context context, String contactName, String message) {
        String number = ContactManager.getPhoneNumber(context, contactName);
        if (number == null) {
            return "Contact not found: " + contactName;
        }

        try {
            Uri uri = Uri.parse("content://sms/sent");
            ContentValues values = new ContentValues();
            values.put("address", number);
            values.put("body", message);
            context.getContentResolver().insert(uri, values);

            // Actually send SMS using SmsManager
            android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
            smsManager.sendTextMessage(number, null, message, null, null);

            return "✔ SMS sent to " + contactName + ": \"" + message + "\"";
        } catch (Exception e) {
            return "❌ Failed to send SMS: " + e.getMessage();
        }
    }

    @Override
    public int[] modes() {
        return new int[]{CommandAbstraction.DEFAULT};
    }

    @Override
    public int priority() {
        return 5;
    }
}
