// File: app/src/main/java/ohi/andre/consolelauncher/services/NotificationBridge.java

package ohi.andre.consolelauncher.services;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
            if ("com.whatsapp".equals(pkg)) {
                handleWhatsApp(sbn);
            } else if ("com.google.android.gm".equals(pkg)) {
                handleGmail(sbn);
            }
        } catch (Exception e) {
            Log.w(TAG, "Error in onNotificationPosted", e);
        }
    }

    private void handleWhatsApp(StatusBarNotification sbn) {
        try {
            CharSequence text = sbn.getNotification().extras.getCharSequence("android.text");
            CharSequence title = sbn.getNotification().extras.getCharSequence("android.title");
            SharedPreferences prefs = getSharedPreferences("tui_wa", Context.MODE_PRIVATE);
            String display = String.format("%s: %s", title == null ? "WhatsApp" : title, text);
            prefs.edit().putString("last_wa", display).apply();

            if (sbn.getNotification().actions != null) {
                for (android.app.Notification.Action action : sbn.getNotification().actions) {
                    if (action == null || action.actionIntent == null) continue;
                    if (action.title != null && action.title.toString().equals("Reply")) {
                        SharedPreferences prefs2 = getSharedPreferences("tui_wa", Context.MODE_PRIVATE);
                        try {
                            String piUri = action.actionIntent.toUri(Intent.URI_INTENT_SCHEME);
                            prefs2.edit()
                                    .putString("last_pi", piUri)
                                    .putString("last_action_title", action.title.toString())
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
            String out = (title == null ? "Gmail" : title) + ": " + (text == null ? "" : text);
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

    // Helper to allow WhatsAppCommand to retrieve saved PendingIntent
    @Nullable
    public static PendingIntent getSavedReplyPendingIntent(Context ctx) {
        try {
            String uri = ctx.getSharedPreferences("tui_wa", Context.MODE_PRIVATE).getString("last_pi", null);
            if (uri == null) return null;
            Intent i = Intent.parseUri(uri, Intent.URI_INTENT_SCHEME);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            return PendingIntent.getActivity(ctx, 0, i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } catch (Exception e) {
            return null;
        }
    }

    @Nullable
    public static String getSavedRemoteInputKey(Context ctx) {
        return ctx.getSharedPreferences("tui_wa", Context.MODE_PRIVATE).getString("last_action_key", null);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        // no-op
    }
}
