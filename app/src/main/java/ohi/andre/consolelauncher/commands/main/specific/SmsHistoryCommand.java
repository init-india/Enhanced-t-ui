package ohi.andre.consolelauncher.commands.main.specific;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;

import ohi.andre.consolelauncher.CommandAbstraction;
import ohi.andre.consolelauncher.ExecutePack;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SmsHistoryCommand implements CommandAbstraction {

    @Override
    public String[] args() {
        return new String[]{"sms", "history"};
    }

    @Override
    public int argType() {
        return 0;
    }

    @Override
    public String onExec(ExecutePack pack) throws Exception {
        Context ctx = pack.context;
        String[] args = pack.getArgs();

        String query = null;
        if (args != null && args.length >= 2) {
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                if (i > 1) sb.append(' ');
                sb.append(args[i]);
            }
            query = sb.toString().trim();
        }

        ArrayList<String> lines = new ArrayList<>();
        ContentResolver cr = ctx.getContentResolver();

        Uri uri = Uri.parse("content://sms"); // inbox+sent
        String selection = null;
        String[] selectionArgs = null;
        if (!TextUtils.isEmpty(query)) {
            selection = "address LIKE ?";
            selectionArgs = new String[]{"%" + query + "%"};
        }

        Cursor c = cr.query(uri,
                new String[]{"address", "date", "body", "type"},
                selection,
                selectionArgs,
                "date DESC LIMIT 100");

        if (c == null) {
            return "No SMS provider available or SMS permission missing.";
        }

        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault());
        int count = 0;
        while (c.moveToNext() && count < 100) {
            String address = c.getString(c.getColumnIndexOrThrow("address"));
            long date = c.getLong(c.getColumnIndexOrThrow("date"));
            String body = c.getString(c.getColumnIndexOrThrow("body"));
            int type = c.getInt(c.getColumnIndexOrThrow("type")); // 1=inbox,2=sent

            String contact = resolveContactName(ctx, address);
            String dir = (type == 2) ? "SENT → " : "RECV ← ";

            String line = String.format(Locale.getDefault(),
                    "%d) %s%s\n    %s\n    (%s)",
                    ++count,
                    dir,
                    contact != null ? contact : address,
                    truncate(body, 160),
                    df.format(new Date(date)));

            lines.add(line);
        }
        c.close();

        if (lines.isEmpty()) return "No SMS found (or permission missing).";
        StringBuilder out = new StringBuilder();
        for (String l : lines) {
            out.append(l).append("\n\n");
        }
        return out.toString();
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        if (s.length() <= max) return s;
        return s.substring(0, max - 3) + "...";
    }

    private String resolveContactName(Context ctx, String number) {
        if (number == null) return null;
        Uri uri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number));
        Cursor c = ctx.getContentResolver().query(uri,
                new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME},
                null, null, null);
        if (c != null) {
            if (c.moveToFirst()) {
                String name = c.getString(0);
                c.close();
                return name;
            }
            c.close();
        }
        return null;
    }

    @Override
    public String onArg(ExecutePack pack) throws Exception {
        return onExec(pack);
    }

    @Override
    public String getHelp() {
        return "sms history [contact|number] - show recent SMS threads (inbox & sent).";
    }
}
