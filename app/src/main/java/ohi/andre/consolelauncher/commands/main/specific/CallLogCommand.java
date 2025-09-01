package ohi.andre.consolelauncher.commands.main.specific;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import ohi.andre.consolelauncher.CommandAbstraction;
import ohi.andre.consolelauncher.ExecutePack;

public class CallLogCommand implements CommandAbstraction {

    @Override
    public String[] args() {
        return new String[]{"calllog"};
    }

    @Override
    public int argType() {
        return 0;
    }

    @Override
    public String onExec(ExecutePack pack) throws Exception {
        Context ctx = pack.context;
        String[] args = pack.getArgs();
        String filter = null;

        if (args != null && args.length > 0) {
            String sub = args[0].toLowerCase();
            switch (sub) {
                case "missed":
                    filter = CallLog.Calls.TYPE + "=" + CallLog.Calls.MISSED_TYPE;
                    break;
                case "rejected":
                    filter = CallLog.Calls.TYPE + "=" + CallLog.Calls.REJECTED_TYPE;
                    break;
                case "all":
                    filter = null;
                    break;
                default:
                    // treat as contact search
                    StringBuilder sb = new StringBuilder();
                    for (String s : args) {
                        if (sb.length() > 0) sb.append(' ');
                        sb.append(s);
                    }
                    String q = sb.toString();
                    filter = CallLog.Calls.NUMBER + " LIKE '%" + q + "%'";
            }
        }

        ContentResolver cr = ctx.getContentResolver();
        Cursor c = cr.query(CallLog.Calls.CONTENT_URI, null, filter, null, CallLog.Calls.DATE + " DESC LIMIT 200");

        if (c == null) return "Call log not accessible (permission missing?)";

        ArrayList<String> out = new ArrayList<>();
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault());
        int idx = 0;

        while (c.moveToNext() && idx < 200) {
            idx++;
            String num = c.getString(c.getColumnIndexOrThrow(CallLog.Calls.NUMBER));
            long date = c.getLong(c.getColumnIndexOrThrow(CallLog.Calls.DATE));
            int type = c.getInt(c.getColumnIndexOrThrow(CallLog.Calls.TYPE));
            int duration = c.getInt(c.getColumnIndexOrThrow(CallLog.Calls.DURATION));

            String typeStr = typeToStr(type);
            String contact = resolveContactName(ctx, num);

            String line = String.format(Locale.getDefault(),
                    "%d) [%s] %s (%ds) at %s",
                    idx,
                    typeStr,
                    contact != null ? contact : num,
                    duration,
                    df.format(new Date(date))
            );

            out.add(line);
        }
        c.close();

        if (out.isEmpty()) return "No call log entries found.";
        StringBuilder sb = new StringBuilder();
        for (String l : out) sb.append(l).append("\n");
        return sb.toString();
    }

    private String typeToStr(int type) {
        switch (type) {
            case CallLog.Calls.INCOMING_TYPE:
                return "IN";
            case CallLog.Calls.OUTGOING_TYPE:
                return "OUT";
            case CallLog.Calls.MISSED_TYPE:
                return "MISSED";
            case CallLog.Calls.REJECTED_TYPE:
                return "REJECTED";
            case CallLog.Calls.VOICEMAIL_TYPE:
                return "VOICEMAIL";
            default:
                return "OTHER";
        }
    }

    private String resolveContactName(Context ctx, String number) {
        if (number == null) return null;
        Uri uri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number)
        );
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
        return "calllog missed|rejected|all|<contact|number> - show call history with duration & contact names";
    }
}
