package ohi.andre.consolelauncher.managers;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXParseException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import ohi.andre.consolelauncher.MainManager;
import ohi.andre.consolelauncher.R;
import ohi.andre.consolelauncher.UIManager;
import ohi.andre.consolelauncher.commands.main.MainPack;

import ohi.andre.consolelauncher.managers.xml.XMLPref;
import ohi.andre.consolelauncher.managers.xml.XMLPrefsManager;
import ohi.andre.consolelauncher.managers.xml.classes.XMLPrefsElement;
import ohi.andre.consolelauncher.managers.xml.classes.XMLPrefsList;
import ohi.andre.consolelauncher.managers.xml.classes.XMLPrefsSave;

import ohi.andre.consolelauncher.tuils.StoppableThread;
import ohi.andre.consolelauncher.tuils.Tuils;

public class AppsManager implements XMLPrefsElement {

    public static final int SHOWN_APPS = 10;
    public static final int HIDDEN_APPS = 11;

    public static final String PATH = "apps.xml";
    private final String NAME = "APPS";
    private File file;

    private final String SHOW_ATTRIBUTE = "show";
    private static final String APPS_SEPARATOR = ";";
    private Context context;

    private AppsHolder appsHolder;
    private List<LaunchInfo> hiddenApps;

    private final String PREFS = "apps";
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public static XMLPrefsElement instance = null;

    private XMLPrefsList prefsList;

    public List<Group> groups;                        
    private Pattern pp, pl;
    private String appInstalledFormat, appUninstalledFormat;
    private int appInstalledColor, appUninstalledColor;

    @Override
    public String[] delete() {
        return null;
    }

    @Override
    public void write(XMLPrefsSave save, String value) {
        set(new File(Tuils.getFolder(), PATH), save.list);
    }

    @Override
    public String path() {
        return PATH;
    }

    @Override
    public XMLPrefsList getValues() {
        return prefsList;
    }

    private BroadcastReceiver appsBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String data = intent.getData().getSchemeSpecificPart();
            if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
                appInstalled(data);
            } else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
                appUninstalled(data);
            }
        }
    };

    public AppsManager(final Context context) {
        instance = this;
        this.context = context;

        appInstalledFormat = XMLPrefsManager.getBoolean("appInstalledFormat", "%p installed");
        appUninstalledFormat = XMLPrefsManager.getBoolean("appUninstalledFormat", "%p uninstalled");

        if (appInstalledFormat != null || appUninstalledFormat != null) {
            pp = Pattern.compile("%p", Pattern.CASE_INSENSITIVE);
            pl = Pattern.compile("%l", Pattern.CASE_INSENSITIVE);

            appInstalledColor = XMLPrefsManager.getColor("appInstalledColor", 0xFF00FF00);
            appUninstalledColor = XMLPrefsManager.getColor("appUninstalledColor", 0xFFFF0000);
        } else {
            pp = null;
            pl = null;
        }

        File root = Tuils.getFolder();
        this.file = root != null ? new File(root, PATH) : null;

        this.preferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        this.editor = preferences.edit();

        this.groups = new ArrayList<>();
        initAppListener(context);

        new StoppableThread() {
            @Override
            public void run() {
                super.run();
                fill();
                LocalBroadcastManager.getInstance(context).registerReceiver(appsBroadcast, new IntentFilter());
            }
        }.start();
    }

    private void initAppListener(Context c) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");

        c.registerReceiver(appsBroadcast, intentFilter);
    }

    public void fill() {
        final List<LaunchInfo> allApps = createAppMap(context.getPackageManager());
        hiddenApps = new ArrayList<>();
        groups.clear();

        try {
            prefsList = new XMLPrefsList();
            if (file != null && !file.exists()) {
                resetFile(file, NAME);
            }

            Object[] o = XMLPrefsManager.buildDocument(file);
            if (o == null) {
                Tuils.sendXMLParseError(context, file);
                return;
            }

            Document d = (Document) o[0];
            Element root = (Element) o[1];
            // Additional parsing here
        } catch (SAXParseException e) {
            Tuils.sendXMLParseError(context, file);
            return;
        } catch (Exception e) {
            Tuils.log(e);
            return;
        }

        appsHolder = new AppsHolder(allApps, prefsList);
    }

    private List<LaunchInfo> createAppMap(PackageManager mgr) {
        List<LaunchInfo> infos = new ArrayList<>();

        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> main;
        try {
            main = mgr.queryIntentActivities(i, 0);
        } catch (Exception e) {
            return infos;
        }

        for (ResolveInfo ri : main) {
            LaunchInfo li = new LaunchInfo(ri.activityInfo.packageName, ri.activityInfo.name);
            infos.add(li);
        }

        return infos;
    }

    private void appInstalled(String packageName) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo packageInfo = manager.getPackageInfo(packageName, 0);

            if (appInstalledFormat != null) {
                String cp = pp.matcher(appInstalledFormat).replaceAll(packageName);
                Tuils.sendOutput(appInstalledColor, cp);
            }

            Intent i = manager.getLaunchIntentForPackage(packageName);
            if (i == null) return;

            ComponentName name = i.getComponent();
            LaunchInfo app = new LaunchInfo(packageName, name.getClassName());
            appsHolder.add(app);
        } catch (Exception ignored) {}
    }

    private void appUninstalled(String packageName) {
        if (appsHolder == null || context == null) return;

        List<LaunchInfo> infos = appsHolder.findByPackage(packageName);

        if (appUninstalledFormat != null) {
            String cp = pl.matcher(appUninstalledFormat).replaceAll(packageName);
            Tuils.sendOutput(appUninstalledColor, cp);
        }

        for (LaunchInfo i : infos) appsHolder.remove(i);
    }
}
