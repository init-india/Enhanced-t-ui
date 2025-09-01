package ohi.andre.consolelauncher.commands.main.raw;

import android.content.Intent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import ohi.andre.consolelauncher.R;
import ohi.andre.consolelauncher.UIManager;
import ohi.andre.consolelauncher.commands.CommandAbstraction;
import ohi.andre.consolelauncher.commands.ExecutePack;
import ohi.andre.consolelauncher.commands.main.MainPack;
import ohi.andre.consolelauncher.commands.main.ParamCommand;
import ohi.andre.consolelauncher.managers.xml.XMLPrefsManager;
import ohi.andre.consolelauncher.managers.xml.XMLPrefsSave;
import ohi.andre.consolelauncher.managers.xml.options.Behavior;
import ohi.andre.consolelauncher.managers.xml.options.Ui;
import ohi.andre.consolelauncher.tuils.Tuils;
import ohi.andre.consolelauncher.tuils.interfaces.Reloadable;

/**
 * Created by francescoandreuzzi on 21/05/2018.
 */

public class tuiweather extends ParamCommand {

    private enum Param implements ohi.andre.consolelauncher.commands.main.Param {

        update {
            @Override
            public String exec(ExecutePack pack) {
                if (!XMLPrefsManager.getBoolean(Ui.show_weather)) {
                    return pack.context.getString(R.string.weather_disabled);
                } else if (!XMLPrefsManager.wasChanged(Ui.weather_updated)) {
                    return pack.context.getString(R.string.weather_not_updated);
                } else {
                    LocalBroadcastManager.getInstance(pack.context).sendBroadcast(new Intent(UIManager.ACTION_UPDATE_WEATHER));
                    return null;
                }
            }
        },
        enable {
            @Override
            public String exec(ExecutePack pack) {
                XMLPrefsSave save = Ui.show_weather;
                save.parent().write(save, "true");
                ((Reloadable) pack.context).addMessage(null, pack.context.getString(R.string.weather_enabled));
                ((Reloadable) pack.context).reload();
                return null;
            }
        },
        disable {
            @Override
            public String exec(ExecutePack pack) {
                XMLPrefsSave save = Ui.show_weather;
                save.parent().write(save, "false");
                ((Reloadable) pack.context).addMessage(null, pack.context.getString(R.string.weather_disabled));
                ((Reloadable) pack.context).reload();
                return null;
            }
        },
        tutorial {
            @Override
            public String exec(ExecutePack pack) {
                pack.context.startActivity(Tuils.webPageIntent(pack.context.getString(R.string.url_weather_tutorial)));
                return null;
            }
        },
        set_key {
            @Override
            public int[] args() {
                return new int[]{CommandAbstraction.TEXT};
            }

            @Override
            public String exec(ExecutePack pack) {
                Behavior.weather_key.parent().write(Behavior.weather_key, pack.getString());
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
            return pack.context.getString(R.string.help_weather);
        }

        @Override
        public String onArgNotFound(ExecutePack pack, String arg) {
            return pack.context.getString(R.string.output_invalid_arg);
        }
    }

    @Override
    protected Param paramForString(MainPack pack, String param) {
        return Param.get(param);
    }

    @Override
    public int priority() {
        return 2;
    }

    @Override
    public int helpRes() {
        return R.string.help_weather;
    }

    @Override
    public String[] params() {
        return Param.labels();
    }

    @Override
    protected String doThings(ExecutePack pack) {
        return null;
    }
}
