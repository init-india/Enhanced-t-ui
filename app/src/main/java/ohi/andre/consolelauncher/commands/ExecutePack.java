package ohi.andre.consolelauncher.commands;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import ohi.andre.consolelauncher.managers.AppsManager;
import ohi.andre.consolelauncher.managers.xml.XMLPrefsSave;

@SuppressWarnings("deprecation")
public abstract class ExecutePack {

    public Object[] args;
    public Context context;
    public CommandGroup commandGroup;

    public int currentIndex = 0;

    public ExecutePack(CommandGroup group) {
        this.commandGroup = group;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> c) {
        return (T) get();
    }

    public <T> T get(Class<T> c, int index) {
        if(index < args.length) return (T) args[index];
        return null;
    }

    public Object get() {
        if(currentIndex < args.length) return args[currentIndex++];
        return null;
    }

    @Nullable
    public String getString() {
        Object obj = get();
        return obj instanceof String ? (String) obj : null;
    }

    public int getInt() {
        Object obj = get();
        return obj instanceof Integer ? (int) obj : 0;
    }

    public boolean getBoolean() {
        Object obj = get();
        return obj instanceof Boolean && (boolean) obj;
    }

    @Nullable
    public ArrayList<?> getList() {
        Object obj = get();
        return obj instanceof ArrayList ? (ArrayList<?>) obj : null;
    }

    @Nullable
    public XMLPrefsSave getPrefsSave() {
        Object obj = get();
        return obj instanceof XMLPrefsSave ? (XMLPrefsSave) obj : null;
    }

    @Nullable
    public AppsManager.LaunchInfo getLaunchInfo() {
        Object obj = get();
        return obj instanceof AppsManager.LaunchInfo ? (AppsManager.LaunchInfo) obj : null;
    }

    public void set(Object[] args) {
        this.args = args;
    }

    public void clear() {
        args = null;
        currentIndex = 0;
    }
}
