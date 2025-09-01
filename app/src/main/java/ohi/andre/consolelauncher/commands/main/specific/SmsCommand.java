package ohi.andre.consolelauncher.commands.main.specific;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.text.TextUtils;

import ohi.andre.consolelauncher.CommandAbstraction;
import ohi.andre.consolelauncher.ExecutePack;

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
        // usage:
        // sms send <number> <message>
        // sms reply <number> <message>   (alias of send)
        String[] args = pack.getArgs();
        if (args == null || args.length < 2) {
            return "Usage: sms send <number> <message>";
        }

        String cmd = args[0];
        if ("send".equalsIgnoreCase(cmd) || "reply".equalsIgnoreCase(cmd)) {
            if (args.length < 3) return "Usage: sms send <number> <message>";
            String number = args[1];
            StringBuilder sb = new StringBuilder();
            for (int i = 2; i < args.length; i++) {
                if (i > 2) sb.append(' ');
                sb.append(args[i]);
            }
            String message = sb.toString();
            sendSms(pack.context, number, message);
            return "SMS queued to " + number;
        } else if ("inbox".equalsIgnoreCase(cmd) || "history".equalsIgnoreCase(cmd)) {
            // delegate to SmsHistoryCommand by building request
            ExecutePack newPack = new ExecutePack(pack.context, new String[]{"sms", "history"});
            return new SmsHistoryCommand().onExec(newPack);
        }

        return "Unknown sms subcommand. Usage: sms send <number> <message> | sms history [contact]";
    }

    private void sendSms(Context ctx, String number, String message) {
        if (TextUtils.isEmpty(number) || TextUtils.isEmpty(message)) return;
        SmsManager sm = SmsManager.getDefault();
        // simple send; consider multipart if long
        sm.sendTextMessage(number, null, message, null, null);
    }

    @Override
    public String onArg(ExecutePack pack) throws Exception {
        return onExec(pack);
    }

    @Override
    public String getHelp() {
        return "sms send <number> <message> - send SMS; sms history - show SMS history";
    }
}
