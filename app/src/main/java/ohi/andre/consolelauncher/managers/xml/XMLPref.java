package ohi.andre.consolelauncher.managers.xml;

import ohi.andre.consolelauncher.managers.xml.XMLPrefsElement;

/**
 * Simple placeholder for XMLPref.
 * Add real logic as needed.
 */
public class XMLPref implements XMLPrefsElement {

    private final String label;
    private final String defaultValue;

    public XMLPref(String label, String defaultValue) {
        this.label = label;
        this.defaultValue = defaultValue;
    }

    public String label() {
        return label;
    }

    public String defaultValue() {
        return defaultValue;
    }

    @Override
    public String[] delete() {
        return null;
    }

    @Override
    public void write(XMLPrefsSave save, String value) {
        // implement writing logic if needed
    }

    @Override
    public String path() {
        return "xmlpref.xml";
    }

    @Override
    public XMLPrefsList getValues() {
        return new XMLPrefsList();
    }
}
