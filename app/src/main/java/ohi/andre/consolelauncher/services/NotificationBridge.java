package ohi.andre.consolelauncher.services;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.annotation.Nullable;

public class NotificationBridge extends NotificationListenerService {

    private static final String TAG = "NotificationBridge";

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        Log.i(TAG, "NotificationBridge connected");
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        try {
            String pkg = sbn.getPackageName();
            // Capture WhatsApp notifications
            if ("com.whatsapp".equals(pkg) || "com.whatsapp.w4b".equals(pkg)) {
                handleWhatsApp(sbn);
            } else if ("com.google.android.gm".equals(pkg) || "com.google.android.apps.inbox".equals(pkg)) {
                handleGmail(sbn);
            }
        } catch (Exception e) {
            Log.w(TAG, "Error in onNotificationPosted: " + e.getMessage());
        }
    }

    private void handleWhatsApp(StatusBarNotification sbn) {
        try {
            CharSequence text = sbn.getNotification().extras.getCharSequence("android.text");
            CharSequence title = sbn.getNotification().extras.getCharSequence("android.title");
            // Save last message into prefs for CLI read
            SharedPreferences prefs = getSharedPreferences("tui_wa", Context.MODE_PRIVATE);
            String display = String.format("%s: %s", title == null ? "WhatsApp" : title.toString(), text == null ? "" : text.toString());
            prefs.edit().putString("last_wa", display).apply();

            // If there's a direct reply action with RemoteInputs, save its PendingIntent and key
            if (sbn.getNotification().actions != null) {
                for (android.app.Notification.Action action : sbn.getNotification().actions) {
                    if (action == null || action.actionIntent == null) continue;
                    if (action.title != null && action.title.toString().toLowerCase().contains("reply")) {
                        // Save a reference to the pending intent by storing a stub "ComponentName" approach is not sufficient;
                        // So we serialize the intent extras we need to re-create RemoteInput results later:
                        // We store remote input key names into prefs, and also store a "flattened" PendingIntent via toUri if possible.
                        SharedPreferences prefs2 = getSharedPreferences("tui_wa", Context.MODE_PRIVATE);
                        try {
                            String piUri = action.actionIntent.toUri(Intent.URI_INTENT_SCHEME);
                            prefs2.edit()
                                    .putString("last_wa_reply_pi", piUri)
                                    .putString("last_wa_reply_key", getFirstRemoteInputKey(action))
                                    .putString("last_wa", prefs2.getString("last_wa",""))
                                    .apply();
                        } catch (Exception ignored) {}
                        break;
                    }
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "handleWhatsApp error: " + e.getMessage());
        }
    }

    private void handleGmail(StatusBarNotification sbn) {
        try {
            CharSequence title = sbn.getNotification().extras.getCharSequence("android.title");
            CharSequence text = sbn.getNotification().extras.getCharSequence("android.text");
            SharedPreferences prefs = getSharedPreferences("tui_gmail", Context.MODE_PRIVATE);
            String out = (title == null ? "Gmail" : title.toString()) + " - " + (text == null ? "" : text.toString());
            prefs.edit().putString("last_gmail", out).apply();
        } catch (Exception e) {
            Log.w(TAG, "handleGmail error: " + e.getMessage());
        }
    }

    private String getFirstRemoteInputKey(android.app.Notification.Action action) {
        try {
            android.app.RemoteInput[] inputs = action.getRemoteInputs();
            if (inputs != null && inputs.length > 0) {
                return inputs[0].getResultKey();
            }
        } catch (Exception ignored) {}
        return null;
    }

    // Helper to allow WhatsAppCommand to retrieve saved pending intent and key
    public static PendingIntent getSavedReplyPendingIntent(Context ctx) {
        try {
            String uri = ctx.getSharedPreferences("tui_wa", Context.MODE_PRIVATE).getString("last_wa_reply_pi", null);
            if (uri == null) return null;
            Intent i = Intent.parseUri(uri, Intent.URI_INTENT_SCHEME);
            // PendingIntent cannot be reconstructed easily from the raw Intent URI and system PendingIntent tokens.
            // Instead: if actionIntent was explicit PendingIntent for activity/service, we may re-create an equivalent Intent.
            // For many devices this will not restore the original PendingIntent perfectly; it's best-effort.
            // We'll wrap the Intent into a PendingIntent targeting the same package/activity if possible.
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            return PendingIntent.getActivity(ctx, 0, i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getSavedRemoteInputKey(Context ctx) {
        return ctx.getSharedPreferences("tui_wa", Context.MODE_PRIVATE).getString("last_wa_reply_key", null);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        // no-op
    }
}
