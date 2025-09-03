package ohi.andre.consolelauncher.commands.main.specific;

import ohi.andre.consolelauncher.commands.CommandAbstraction;
import ohi.andre.consolelauncher.commands.ExecutePack;

/**
 * Legacy command kept for compatibility.
 * Delegates to SmsCommand to avoid duplicated code.
 *
 * Usage:
 *   sms         → list conversations
 *   sms <name>  → show conversation with contact
 *   sms <name> <message> → send SMS to contact
 */
public class SmsHistoryCommand implements CommandAbstraction {

    private final SmsCommand smsCommand = new SmsCommand();

    @Override
    public String onExec(ExecutePack pack) throws Exception {
        // Just call SmsCommand
        return smsCommand.onExec(pack);
    }

    @Override
    public int[] modes() {
        return smsCommand.modes();
    }

    @Override
    public int priority() {
        return smsCommand.priority();
    }
}
