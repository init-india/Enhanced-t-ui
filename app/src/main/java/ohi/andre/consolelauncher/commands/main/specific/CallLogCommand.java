package ohi.andre.consolelauncher.commands.main.specific;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CallLog;

import androidx.core.content.ContextCompat;

import java.util.Date;

import ohi.andre.consolelauncher.commands.CommandAbstraction;
import ohi.andre.consolelauncher.commands.ExecutePack;

/**
 * CallLog command.
 * Lists recent calls (requires READ_CALL_LOG permission).
 */
public class CallLogCommand implements CommandAbstraction {

    @Override
    public String onExec(ExecutePack pack) {
        return help();
    }

    @Override
    public String onArg(ExecutePack pack) {
        if (pack == null || pack.context == null) return help();

        if (ContextCompat.checkSelfPermission(pack.context, Manifest.permission.READ_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED) {
            return "Permission denied: READ_CALL_LOG";
        }

        StringBuilder result = new StringBuilder();
        try (Cursor cursor = pack.context.getContentResolver().query(
                CallLog.Calls.CONTENT_URI,
                null, null, null,
                CallLog.Calls.DATE + " DESC LIMIT 5")) {

            if (cursor == null || !cursor.moveToFirst()) {
                return "No call logs found.";
            }

            int numberCol = cursor.getColumnIndex(CallLog.Calls.NUMBER);
            int typeCol = cursor.getColumnIndex(CallLog.Calls.TYPE);
            int dateCol = cursor.getColumnIndex(CallLog.Calls.DATE);

            int count = 0;
            do {
                String number = cursor.getString(numberCol);
                int type = cursor.getInt(typeCol);
                long dateMillis = cursor.getLong(dateCol);
                String typeStr;

                switch (type) {
                    case CallLog.Calls.OUTGOING_TYPE: typeStr = "OUT"; break;
                    case CallLog.Calls.INCOMING_TYPE: typeStr = "IN"; break;
                    case CallLog.Calls.MISSED_TYPE:   typeStr = "MISSED"; break;
                    default: typeStr = "OTHER"; break;
                }

                result.append(new Date(dateMillis))
                        .append(" | ")
                        .append(typeStr)
                        .append(" | ")
                        .append(number)
                        .append("\n");

                count++;
            } while (cursor.moveToNext() && count < 5);

        } catch (Exception e) {
            return "Error reading call logs: " + e.getMessage();
        }

        return result.toString().trim();
    }

    @Override
    public int[] argType() {
        return new int[0];
    }

    @Override
    public String help() {
        return "calllog — show the last 5 call log entries.";
    }

    @Override
    public String[] names() {
        return new String[]{"calllog"};
    }
}
