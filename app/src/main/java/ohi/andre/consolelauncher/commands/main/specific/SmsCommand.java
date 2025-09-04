package ohi.andre.consolelauncher.commands.main.specific;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;

import ohi.andre.consolelauncher.CommandAbstraction;
import ohi.andre.consolelauncher.ExecutePack;
import ohi.andre.consolelauncher.managers.MessagesManager;
import ohi.andre.consolelauncher.managers.notificatio.NotificationManager;
import ohi.andre.consolelauncher.tuils.Tuils;

import java.util.ArrayList;
import java.util.List;

public class SmsCommand implements CommandAbstraction {

    @Override
    public String[] args() {
        return new String[]{"sms"};
    }

    @Override
    public int argType() {
        return 0;
    }

    @Override
    public String onExec(ExecutePack pack) throws Exception {
        Context ctx = pack.context;
        String[] args = pack.getArgs();

        if (args.length == 0) {
            // List all contacts with first line of SMS
            return listSmsContacts(ctx);
        } else {
            // Show full history for selected contact
            String contact = args[0];
            return listSmsHistory(ctx, contact);
        }
    }

    private String listSmsContacts(Context ctx) {
        Uri inboxUri = Telephony.Sms.Inbox.CONTENT_URI;
        Cursor cursor = ctx.getContentResolver().query(inboxUri,
                new String[]{"_id", "address", "body", "date"}, null, null, "date DESC");

        if (cursor == null) return "No messages.";

        List<String> output = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                String sender = cursor.getString(cursor.getColumnIndexOrThrow("address"));
                String body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));

                String firstLine = body.split("\n")[0];
                output.add(sender + " | " + firstLine + " | " + Tuils.getFormattedDate(date));
            }
        } finally {
            cursor.close();
        }

        return String.join("\n", output);
    }

    private String listSmsHistory(Context ctx, String contact) {
        Uri inboxUri = Telephony.Sms.CONTENT_URI;
        Cursor cursor = ctx.getContentResolver().query(inboxUri,
                new String[]{"_id", "address", "body", "date"}, "address=?", new String[]{contact}, "date DESC");

        if (cursor == null) return "No messages for " + contact;

        List<String> output = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                String body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                output.add(Tuils.getFormattedDate(date) + " | " + body);
            }
        } finally {
            cursor.close();
        }

        return String.join("\n", output);
    }

    @Override
    public String onArg(ExecutePack pack) throws Exception {
        return onExec(pack);
    }

    @Override
    public String getHelp() {
        return "sms [contact] - list all messages or messages by contact";
    }
}
