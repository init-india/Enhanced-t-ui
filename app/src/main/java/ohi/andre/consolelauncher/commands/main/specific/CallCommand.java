package ohi.andre.consolelauncher.commands.main.specific;

import android.content.Context;

import ohi.andre.consolelauncher.CommandAbstraction;
import ohi.andre.consolelauncher.ExecutePack;
import ohi.andre.consolelauncher.managers.CallManager;
import ohi.andre.consolelauncher.tuils.Tuils;

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
        Context ctx = pack.context;
        String[] args = pack.getArgs();

        if (args.length == 0) {
            // List all calls
            return CallManager.listAllCalls(ctx);
        } else {
            // Initiate call to contact
            String contact = args[0];
            return CallManager.callContact(ctx, contact);
        }
    }

    @Override
    public String onArg(ExecutePack pack) throws Exception {
        return onExec(pack);
    }

    @Override
    public String getHelp() {
        return "call [contact] - list calls or call contact";
    }
}
