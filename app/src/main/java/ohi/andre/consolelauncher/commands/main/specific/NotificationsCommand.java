package ohi.andre.consolelauncher.commands.main.specific;

import android.content.Context;

import ohi.andre.consolelauncher.CommandAbstraction;
import ohi.andre.consolelauncher.ExecutePack;
import ohi.andre.consolelauncher.managers.notifications.NotificationManager;

public class NotificationsCommand implements CommandAbstraction {

    @Override
    public String[] args() {
        return new String[]{"notifications"};
    }

    @Override
    public int argType() {
        return 0;
    }

    @Override
    public String onExec(ExecutePack pack) {
        Context ctx = pack.context;
        NotificationManager manager = NotificationManager.create(ctx);
        String[] recent = manager.getRecent();
        if (recent.length == 0) return "No notifications.";
        StringBuilder sb = new StringBuilder();
        for (String n : recent) {
            sb.append(n).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String onArg(ExecutePack pack) {
        return onExec(pack);
    }

    @Override
    public String getHelp() {
        return "notifications - show recent incoming calls and SMS";
    }
}
