package ohi.andre.consolelauncher.managers;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.LauncherApps;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXParseException;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import it.andreuzzi.comparestring2.StringableObject;
import ohi.andre.consolelauncher.MainManager;
import ohi.andre.consolelauncher.R;
import ohi.andre.consolelauncher.UIManager;
import ohi.andre.consolelauncher.commands.main.MainParser;
import ohi.andre.consolelauncher.managers.xml.XMLPref;
import ohi.andre.consolelauncher.managers.xml.XMLPrefsList;
import ohi.andre.consolelauncher.managers.xml.XMLPrefsManager;
import ohi.andre.consolelauncher.managers.xml.classes.XMLPrefsSave;
import ohi.andre.consolelauncher.tuils.StoppableThread;
import ohi.andre.consolelauncher.tuils.Tuils;

import static ohi.andre.consolelauncher.managers.xml.XMLPrefsElement.VALUE_ATTRIBUTE;

public class AppsManager implements XMLPrefsElement {

    public static final int SHOWN_APPS = 10;
    public static final int HIDDEN_APPS = 11;
    public static final String PATH = "apps.xml";

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

    private File file;

    private BroadcastReceiver appsBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String data = intent.getData() != null ? intent.getData().getSchemeSpecificPart() : null;
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

        appInstalledFormat = XMLPrefsManager.getBooleanFormat("app_installed");
        appUninstalledFormat = XMLPrefsManager.getBooleanFormat("app_uninstalled");

        if (appInstalledFormat != null || appUninstalledFormat != null) {
            pp = Pattern.compile("%p", Pattern.CASE_INSENSITIVE);
            pl = Pattern.compile("%l", Pattern.CASE_INSENSITIVE);
            appInstalledColor = XMLPrefsManager.getColor("app_installed_color");
            appUninstalledColor = XMLPrefsManager.getColor("app_uninstalled_color");
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
                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("apps_initialized"));
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

    @Override
    public String[] delete() {
        return null;
    }

    @Override
    public void write(XMLPrefsSave save, String value) {
        save.setValue(value);
    }

    @Override
    public String path() {
        return PATH;
    }

    @Override
    public XMLPrefsList getValues() {
        return prefsList;
    }

    public void fill() {
        final List<LaunchInfo> allApps = createAppMap(context.getPackageManager());
        hiddenApps = new ArrayList<>();
        groups.clear();

        try {
            prefsList = new XMLPrefsList();

            if (file != null && file.exists()) {
                Object[] xml = XMLPrefsManager.buildDocument(file, PATH);
                if (xml == null) return;

                Document d = (Document) xml[0];
                Element root = (Element) xml[1];

                NodeList nodes = root.getElementsByTagName("*");
                for (int i = 0; i < nodes.getLength(); i++) {
                    Node node = nodes.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element e = (Element) node;
                        String name = e.getNodeName();
                        prefsList.add(name, e.getTextContent());
                    }
                }
            }
        } catch (SAXParseException e) {
            Tuils.sendXMLParseError(context, PATH);
        } catch (Exception e) {
            Tuils.log(e);
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            LauncherApps launcherApps = (LauncherApps) context.getSystemService(Context.LAUNCHER_APPS_SERVICE);
            for (ResolveInfo ri : main) {
                LaunchInfo li = new LaunchInfo(ri.activityInfo.packageName, ri.activityInfo.name);
                infos.add(li);
            }
        } else {
            for (ResolveInfo ri : main) {
                LaunchInfo li = new LaunchInfo(ri.activityInfo.packageName, ri.activityInfo.name);
                infos.add(li);
            }
        }
        return infos;
    }

    private void appInstalled(String packageName) {
        // Handle newly installed app
    }

    private void appUninstalled(String packageName) {
        // Handle uninstalled app
    }

    public LaunchInfo findLaunchInfoWithLabel(String label, int type) {
        List<LaunchInfo> appList = (type == SHOWN_APPS ? appsHolder.getApps() : hiddenApps);
        for (LaunchInfo li : appList) {
            if (li.label.equals(label)) return li;
        }
        return null;
    }

    public Intent getIntent(final LaunchInfo info) {
        return new Intent(Intent.ACTION_MAIN)
                .addCategory(Intent.CATEGORY_LAUNCHER)
                .setComponent(info.componentName)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }
}
