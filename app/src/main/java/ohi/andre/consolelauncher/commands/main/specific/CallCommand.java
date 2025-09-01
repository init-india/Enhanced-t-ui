package ohi.andre.consolelauncher.commands.main.specific;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.telecom.TelecomManager;
import android.text.TextUtils;

import ohi.andre.consolelauncher.CommandAbstraction;
import ohi.andre.consolelauncher.ExecutePack;

public class CallCommand implements CommandAbstraction {

    @Override
    public String[] args() {
        return new String[]{"call"};
    }

    @Override
    public int argType() {
        return 0;
    }

    @Override
    public String onExec(ExecutePack pack) throws Exception {
        // usage:
        // call <number>    -> dial
        // call answer      -> accept ringing call
        // call reject      -> reject / end call
        String[] args = pack.getArgs();
        if (args == null || args.length == 0) {
            return "Usage: call <number> | call answer | call reject";
        }

        String sub = args[0].toLowerCase();
        Context ctx = pack.context;

        if ("answer".equals(sub)) {
            TelecomManager tm = (TelecomManager) ctx.getSystemService(Context.TELECOM_SERVICE);
            if (tm != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    try {
                        tm.acceptRingingCall();
                        return "Call answered.";
                    } catch (Exception e) {
                        // fallback: try endCall? not needed here
                        return "Failed to answer call: " + e.getMessage();
                    }
                } else {
                    return "Answering calls programmatically requires API >= 26 on many devices.";
                }
            }
            return "TelecomManager unavailable.";
        } else if ("reject".equals(sub) || "end".equals(sub) || "hangup".equals(sub)) {
            TelecomManager tm = (TelecomManager) ctx.getSystemService(Context.TELECOM_SERVICE);
            if (tm != null) {
                try {
                    tm.endCall();
                    return "Call rejected/ended.";
                } catch (Exception e) {
                    return "Failed to end call: " + e.getMessage();
                }
            }
            return "TelecomManager unavailable.";
        } else {
            // dial
            String number = sub;
            if (args.length > 1) {
                // number may be multiple tokens
                StringBuilder sb = new StringBuilder(sub);
                for (int i = 1; i < args.length; i++) {
                    sb.append(" ").append(args[i]);
                }
                number = sb.toString().trim();
            }
            if (TextUtils.isEmpty(number)) return "No number provided.";
            Intent i = new Intent(Intent.ACTION_CALL);
            i.setData(Uri.parse("tel:" + number));
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(i);
            return "Calling " + number;
        }
    }

    @Override
    public String onArg(ExecutePack pack) throws Exception {
        return onExec(pack);
    }

    @Override
    public String getHelp() {
        return "call <number> | call answer | call reject";
    }
}
