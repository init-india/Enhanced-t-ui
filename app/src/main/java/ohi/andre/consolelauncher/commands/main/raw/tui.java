package ohi.andre.consolelauncher.commands.main.raw;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.File;

import ohi.andre.consolelauncher.BuildConfig;
import ohi.andre.consolelauncher.LauncherActivity;
import ohi.andre.consolelauncher.R;
import ohi.andre.consolelauncher.UIManager;
import ohi.andre.consolelauncher.commands.CommandAbstraction;
import ohi.andre.consolelauncher.commands.CommandsPreferences;
import ohi.andre.consolelauncher.commands.ExecutePack;
import ohi.andre.consolelauncher.commands.main.MainPack;
import ohi.andre.consolelauncher.commands.main.ParamCommand;
import ohi.andre.consolelauncher.managers.xml.XMLPrefsManager;
import ohi.andre.consolelauncher.tuils.Tuils;
import ohi.andre.consolelauncher.tuils.interfaces.Reloadable;
import ohi.andre.consolelauncher.tuils.stuff.PolicyReceiver;

/**
 * Created by francescoandreuzzi on 10/06/2017.
 */

public class tui extends ParamCommand {

    private enum Param implements ohi.andre.consolelauncher.commands.main.Param {

        rm {
            @Override
            public String exec(ExecutePack pack) {
                MainPack info = (MainPack) pack;

                DevicePolicyManager policy = (DevicePolicyManager) info.context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                ComponentName name = new ComponentName(info.context, PolicyReceiver.class);
                policy.removeActiveAdmin(name);

                Uri packageURI = Uri.parse("package:" + info.context.getPackageName());
                Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
                info.context.startActivity(uninstallIntent);

                return null;
            }
        },
        about {
            @Override
            public String exec(ExecutePack pack) {
                return "Version:" + Tuils.SPACE + BuildConfig.VERSION_NAME +
                        (BuildConfig.DEBUG ? Tuils.NEWLINE + "Debug Build" : "") +
                        Tuils.NEWLINE + Tuils.NEWLINE +
                        pack.context.getString(R.string.about);
            }
        },
        log {
            @Override
            public int[] args() {
                return new int[]{CommandAbstraction.TEXT};
            }

            @Override
            public String exec(ExecutePack pack) {
                Intent i = new Intent(UIManager.ACTION_LOG);
                i.putExtra(UIManager.FILE_NAME, pack.getString());
                LocalBroadcastManager.getInstance(pack.context).sendBroadcast(i);
                return null;
            }

            @Override
            public String onNotArgEnough(ExecutePack pack) {
                return pack.context.getString(R.string.help_log);
            }
        },
        priority {
            @Override
            public int[] args() {
                return new int[]{CommandAbstraction.TEXT};
            }

            @Override
            public String exec(ExecutePack pack) {
                File file = new File(Tuils.getFolder(), CommandsPreferences.COMMANDS_FILE);
                return XMLPrefsManager.set(file, pack.getString());
            }

            @Override
            public String onNotArgEnough(ExecutePack pack) {
                return pack.context.getString(R.string.help_priority);
            }

            @Override
            public String onArgNotFound(ExecutePack pack, String arg) {
                return pack.context.getString(R.string.output_invalid_arg);
            }
        },
        telegram {
            @Override
            public String exec(ExecutePack pack) {
                pack.context.startActivity(Tuils.webPageIntent(pack.context.getString(R.string.url_telegram)));
                return null;
            }
        },
        googlep {
            @Override
            public String exec(ExecutePack pack) {
                pack.context.startActivity(Tuils.webPageIntent(pack.context.getString(R.string.url_googlep)));
                return null;
            }
        },
        twitter {
            @Override
            public String exec(ExecutePack pack) {
                pack.context.startActivity(Tuils.webPageIntent(pack.context.getString(R.string.url_twitter)));
                return null;
            }
        },
        sourcecode {
            @Override
            public String exec(ExecutePack pack) {
                pack.context.startActivity(Tuils.webPageIntent(pack.context.getString(R.string.url_sourcecode)));
                return null;
            }
        },
        reset {
            @Override
            public String exec(ExecutePack pack) {
                Tuils.deleteContentOnly(Tuils.getFolder());

                ((LauncherActivity) pack.context).addMessage(null, pack.context.getString(R.string.reset_done));
                ((Reloadable) pack.context).reload();
                return null;
            }
        },
        folder {
            @Override
            public String exec(ExecutePack pack) {
                Uri selectedUri = Uri.parse(Tuils.getFolder().getAbsolutePath());
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(selectedUri, "resource/folder");

                if (intent.resolveActivityInfo(pack.context.getPackageManager(), 0) != null) {
                    pack.context.startActivity(intent);
                } else {
                    return Tuils.getFolder().getAbsolutePath();
                }

                return null;
            }
        };

        static Param get(String p) {
            p = p.toLowerCase();
            for (Param param : values())
                if (p.endsWith(param.label()))
                    return param;
            return null;
        }

        static String[] labels() {
            Param[] ps = values();
            String[] ss = new String[ps.length];
            for (int i = 0; i < ps.length; i++) {
                ss[i] = ps[i].label();
            }
            return ss;
        }

        @Override
        public int[] args() {
            return new int[0];
        }

        @Override
        public String label() {
            return Tuils.MINUS + name();
        }

        @Override
        public String onNotArgEnough(ExecutePack pack) {
            return null;
        }

        @Override
        public String onArgNotFound(ExecutePack pack, String arg) {
            return null;
        }
    }

    @Override
    protected Param paramForString(MainPack pack, String param) {
        return Param.get(param);
    }

    @Override
    protected String doThings(ExecutePack pack) {
        return null;
    }

    @Override
    public String[] params() {
        return Param.labels();
    }

    @Override
    public int priority() {
        return 4;
    }

    @Override
    public int helpRes() {
        return R.string.help_tui;
    }
}
