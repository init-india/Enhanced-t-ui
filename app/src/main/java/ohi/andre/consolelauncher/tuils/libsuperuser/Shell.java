package ohi.andre.consolelauncher.tuils.libsuperuser;

import com.topjohnwu.superuser.Shell;

import java.util.List;

/**
 * Compatibility wrapper around libsu.
 * Replaces Chainfire's old Shell.java
 */
public class Shell {

    /** Run a single command (normal shell) */
    public static List<String> run(String command) {
        return Shell.cmd(command).exec().getOut();
    }

    /** Run multiple commands (normal shell) */
    public static List<String> run(String... commands) {
        return Shell.cmd(commands).exec().getOut();
    }

    /** Check if root is available */
    public static boolean isRootAvailable() {
        return Shell.rootAccess();
    }

    /** Non-root shell helper */
    public static class SH {
        public static List<String> run(String command) {
            return Shell.cmd(command).exec().getOut();
        }

        public static List<String> run(String... commands) {
            return Shell.cmd(commands).exec().getOut();
        }
    }

    /** Root shell helper */
    public static class SU {
        public static List<String> run(String command) {
            return Shell.cmd(command).exec().getOut();
        }

        public static List<String> run(String... commands) {
            return Shell.cmd(commands).exec().getOut();
        }

        public static boolean available() {
            return Shell.rootAccess();
        }
    }
}
