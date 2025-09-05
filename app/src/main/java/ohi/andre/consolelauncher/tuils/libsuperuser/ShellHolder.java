package ohi.andre.consolelauncher.tuils.libsuperuser;

import com.topjohnwu.superuser.Shell;

/**
 * Simplified replacement for Chainfire's ShellHolder.
 * Keeps a shared interactive shell.
 */
public class ShellHolder {

    private static final Shell.Interactive shell =
            Shell.Builder.create().build();

    public static Shell.Interactive getShell() {
        return shell;
    }
}
