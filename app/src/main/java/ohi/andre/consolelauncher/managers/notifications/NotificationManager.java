package ohi.andre.consolelauncher.managers.notifications;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Vibrator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXParseException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ohi.andre.consolelauncher.R;
import ohi.andre.consolelauncher.managers.RegexManager;
import ohi.andre.consolelauncher.managers.xml.XMLPrefsManager;
import ohi.andre.consolelauncher.managers.xml.classes.XMLPrefsE;
import ohi.andre.consolelauncher.managers.xml.classes.XMLPrefsList;
import ohi.andre.consolelauncher.managers.xml.classes.XMLPrefsSave;
import ohi.andre.consolelauncher.managers.xml.options.OptionBoolean;
import ohi.andre.consolelauncher.tuils.Tuils;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationManager implements XMLPrefsE {

    private static String COLOR_ATTRIBUTE = "color";
    public static String ENABLED_ATTRIBUTE = "enabled";
    public static String ID_ATTRIBUTE = "id";
    public static String FORMAT_ATTRIBUTE = "format";
    public static String FILTER_ATTRIBUTE = "filter";

    public static final String PATH = "notifications.xml";
    private static final String NAME = "NOTIFICATIONS";

    public boolean default_app_state;
    public String default_color;

    private XMLPrefsList values;
    private List<NotificatedApp> apps;
    private List<Pattern> filters;
    private List<XMLPrefsManager.IdValue> formats;

    public static NotificationManager instance = null;

    public static NotificationManager create(Context context) {
        if(instance == null) return new NotificationManager(context);
        else return instance;
    }

    private NotificationManager(Context context) {
        instance = this;

        apps = new ArrayList<>();
        filters = new ArrayList<>();
        formats = new ArrayList<>();
        values = new XMLPrefsList();

        try {
            File r = Tuils.getFolder();
            if(r == null) {
                Tuils.sendOutput(Color.RED, context, "Error accessing folder");
                return;
            }

            File file = new File(r, PATH);
            if(!file.exists()) {
                resetFile(file, NAME);
            }

            Object[] o;
            try {
                o = XMLPrefsManager.buildDocument(file);
                if(o == null) {
                    Tuils.sendXMLParseError(context, PATH);
                    return;
                }
            } catch (SAXParseException e) {
                Tuils.sendXMLParseError(context, PATH);
                return;
            } catch (Exception e) {
                Tuils.log(e);
                return;
            }

            Document d = (Document) o[0];
            Element root = (Element) o[1];

            NodeList nodes = root.getElementsByTagName("*");

            for(int count = 0; count < nodes.getLength(); count++) {
                Node node = nodes.item(count);
                String nn = node.getNodeName();

                if(nn.equals(FILTER_ATTRIBUTE)) {
                    if(node.getNodeType() == Node.ELEMENT_NODE) {
                        Element e = (Element) node;
                        Pattern pattern;
                        String regex = XMLPrefsManager.get(e, "regex");
                        if(regex == null) continue;
                        try {
                            pattern = RegexManager.instance.getPattern(regex);
                        } catch (Exception exc) {
                            pattern = Pattern.compile(regex);
                        }
                        filters.add(pattern);
                    }
                } else if(nn.equals(FORMAT_ATTRIBUTE)) {
                    if(node.getNodeType() == Node.ELEMENT_NODE) {
                        Element e = (Element) node;
                        String format = XMLPrefsManager.get(e, "value");
                        if(format == null) continue;
                        int id;
                        try {
                            id = e.hasAttribute(ID_ATTRIBUTE) ? Integer.parseInt(e.getAttribute(ID_ATTRIBUTE)) : -1;
                        } catch (NumberFormatException ex) {
                            continue;
                        }
                        formats.add(new XMLPrefsManager.IdValue(id, format));
                    }
                } else {
                    if(node.getNodeType() == Node.ELEMENT_NODE) {
                        Element e = (Element) node;
                        NotificatedApp app;

                        boolean enabled = XMLPrefsManager.getBoolean(e, ENABLED_ATTRIBUTE, true);
                        String color = XMLPrefsManager.get(e, COLOR_ATTRIBUTE, "#FFFFFF");
                        String format = XMLPrefsManager.get(e, FORMAT_ATTRIBUTE, "");
                        app = new NotificatedApp(nn, color, enabled, format);
                        apps.add(app);
                    }
                }
            }

        } catch (Exception e) {
            Tuils.log(e);
            Tuils.toFile(e);
        }

        default_app_state = XMLPrefsManager.getBoolean(null, "default_state", true);
        default_color = XMLPrefsManager.get(null, "default_color", "#FFFFFF");
    }

    public void dispose() {
        if(values != null) { values.list.clear(); values = null; }
        if(apps != null) { apps.clear(); apps = null; }
        if(filters != null) { filters.clear(); filters = null; }
        if(formats != null) { formats.clear(); formats = null; }
        instance = null;
    }

    // ================== CLI Alert Feature ==================
    public void push(String text, Context context) {
        // Default console output
        Tuils.sendOutput(Color.YELLOW, context, text);

        // Vibrate
        try {
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if(v != null) v.vibrate(500);
        } catch (Exception ignored) {}

        // Beep
        try {
            ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
            tg.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
        } catch (Exception ignored) {}
    }
    // ======================================================

    public boolean match(String text) {
        for(Pattern f : filters) {
            Matcher m = f.matcher(text);
            if(m.matches() || m.find() || text.equals(f.toString())) return true;
        }
        return false;
    }

    public int apps() { return apps.size(); }

    public NotificatedApp getAppState(String pkg) {
        int index = Tuils.find(pkg, apps);
        if(index == -1) return null;
        return apps.get(index);
    }

    public static class NotificatedApp {
        String pkg, color, format;
        boolean enabled;

        public NotificatedApp(String pkg, String color, boolean enabled, String format) {
            this.pkg = pkg;
            this.color = color;
            this.enabled = enabled;
            this.format = format;
        }

        @Override
        public boolean equals(Object obj) { return this.toString().equals(obj.toString()); }

        @Override
        public String toString() { return pkg; }
    }
}
