package ohi.andre.consolelauncher.commands.main.specific;

import android.content.Context;

import ohi.andre.consolelauncher.CommandAbstraction;
import ohi.andre.consolelauncher.ExecutePack;
import ohi.andre.consolelauncher.managers.GmailManager;

public class GmailCommand implements CommandAbstraction {

    @Override
    public String[] args() {
        return new String[]{"mail", "gmail"};
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
            return GmailManager.listAllEmails(ctx);
        } else {
            String sender = args[0];
            return GmailManager.listEmailsFromSender(ctx, sender);
        }
    }

    @Override
    public String onArg(ExecutePack pack) throws Exception {
        return onExec(pack);
    }

    @Override
    public String getHelp() {
        return "mail [sender] - list all emails or emails from sender";
    }
}
