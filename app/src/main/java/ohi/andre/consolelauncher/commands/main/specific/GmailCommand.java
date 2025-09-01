package ohi.andre.consolelauncher.commands.main.specific;

import android.content.Context;
import android.text.TextUtils;

import ohi.andre.consolelauncher.CommandAbstraction;
import ohi.andre.consolelauncher.ExecutePack;

import java.util.List;

/**
 * Minimal Gmail CLI integration.
 *
 * NOTE: This file expects you to implement GmailServiceHelper which handles OAuth2 flow and calls
 * Google Gmail REST APIs. The helper below is a simple stub you must wire with proper client secrets
 * and token storage.
 */
public class GmailCommand implements CommandAbstraction {

    @Override
    public String[] args() {
        return new String[]{"gmail", "mail"};
    }

    @Override
    public int argType() {
        return 0;
    }

    @Override
    public String onExec(ExecutePack pack) throws Exception {
        String[] args = pack.getArgs();
        Context ctx = pack.context;
        if (args == null || args.length == 0) {
            return "Usage: gmail inbox | gmail read <n> | gmail send <to> <subj> <body> | gmail reply <n> <msg>";
        }

        String cmd = args[0];
        if ("inbox".equalsIgnoreCase(cmd)) {
            List<String> mails = GmailServiceHelper.listInbox(ctx, 20);
            if (mails == null || mails.isEmpty()) return "No mails or not authenticated.";
            StringBuilder sb = new StringBuilder();
            int i = 1;
            for (String m : mails) {
                sb.append(i++).append(") ").append(m).append("\n");
            }
            return sb.toString();
        } else if ("read".equalsIgnoreCase(cmd)) {
            if (args.length < 2) return "Usage: gmail read <n>";
            int n = Integer.parseInt(args[1]);
            String mail = GmailServiceHelper.readMail(ctx, n);
            return mail == null ? "No such mail or not authenticated." : mail;
        } else if ("send".equalsIgnoreCase(cmd)) {
            if (args.length < 4) return "Usage: gmail send <to> <subject> <body>";
            String to = args[1];
            String subj = args[2];
            StringBuilder sb = new StringBuilder();
            for (int i = 3; i < args.length; i++) {
                if (i > 3) sb.append(' ');
                sb.append(args[i]);
            }
            String body = sb.toString();
            boolean ok = GmailServiceHelper.sendMail(ctx, to, subj, body);
            return ok ? "Mail sent." : "Failed to send (not authenticated or error).";
        } else if ("reply".equalsIgnoreCase(cmd)) {
            if (args.length < 3) return "Usage: gmail reply <n> <message>";
            int n = Integer.parseInt(args[1]);
            StringBuilder sb = new StringBuilder();
            for (int i = 2; i < args.length; i++) {
                if (i > 2) sb.append(' ');
                sb.append(args[i]);
            }
            boolean ok = GmailServiceHelper.replyMail(ctx, n, sb.toString());
            return ok ? "Reply sent." : "Failed to reply.";
        }

        return "Unknown gmail command.";
    }

    @Override
    public String onArg(ExecutePack pack) throws Exception {
        return onExec(pack);
    }

    @Override
    public String getHelp() {
        return "gmail inbox|read|send|reply - Gmail via API (requires config)";
    }
}
