package ohi.andre.consolelauncher.commands.main.specific;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.widget.RemoteViews;

import ohi.andre.consolelauncher.CommandAbstraction;
import ohi.andre.consolelauncher.ExecutePack;

import java.lang.reflect.Field;

public class WhatsAppCommand implements CommandAbstraction {

    @Override
    public String[] args() {
        return new String[]{"wa", "whatsapp"};
    }

    @Override
    public int argType() {
        return 0;
    }

    @Override
    public String onExec(ExecutePack pack) throws Exception {
        // usage:
        // wa last            -> show last cached WA notification
        // wa reply <message> -> reply to last WA notification using saved PendingIntent
        String[] args = pack.getArgs();
        if (args == null || args.length == 0) {
            return "Usage: wa last | wa reply <message>";
        }
        String cmd = args[0];
        Context ctx = pack.context;

        if ("last".equalsIgnoreCase(cmd)) {
            String cached = ctx.getSharedPreferences("tui_wa", Context.MODE_PRIVATE).getString("last_wa", "No WhatsApp notifications cached.");
            return cached;
        } else if ("reply".equalsIgnoreCase(cmd)) {
            if (args.length < 2) return "Usage: wa reply <message>";
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                if (i > 1) sb.append(' ');
                sb.append(args[i]);
            }
            String message = sb.toString();
            return tryReply(ctx, message);
        }
        return "Unknown wa command.";
    }

    private String tryReply(Context ctx, String message) {
        // NotificationListenerService should save the PendingIntent / RemoteInput key in prefs by the NotificationListener implementation.
        // We will read an intent action saved as serialized extras and fire it with RemoteInput bundle.
        try {
            PendingIntent pi = NotificationBridge.getSavedReplyPendingIntent(ctx);
            String remoteKey = NotificationBridge.getSavedRemoteInputKey(ctx);
            if (pi == null || TextUtils.isEmpty(remoteKey)) return "No saved WhatsApp reply action available (enable notification access).";

            Intent i = new Intent();
            Bundle b = new Bundle();
            b.putCharSequence(remoteKey, message);
            android.app.RemoteInput.addResultsToIntent(new android.app.RemoteInput[]{new android.app.RemoteInput.Builder(remoteKey).build()}, i, b);
            pi.send(ctx, 0, i);
            return "Reply sent (via notification).";
        } catch (Exception e) {
            return "Failed to send reply: " + e.getMessage();
        }
    }

    @Override
    public String onArg(ExecutePack pack) throws Exception {
        return onExec(pack);
    }

    @Override
    public String getHelp() {
        return "wa last | wa reply <message> - work via Notification access";
    }
}
