package ohi.andre.consolelauncher.commands.main.specific;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;

import ohi.andre.consolelauncher.CommandAbstraction;
import ohi.andre.consolelauncher.ExecutePack;
import ohi.andre.consolelauncher.tuils.Tuils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class NavCommand implements CommandAbstraction {

    @Override
    public String[] args() {
        return new String[]{"nav", "navigate"};
    }

    @Override
    public int argType() {
        return 0;
    }

    @Override
    public String onExec(ExecutePack pack) throws Exception {
        String[] args = pack.getArgs();
        Context ctx = pack.context;

        if (args.length == 0) return "Usage: nav <address>";

        String addressInput = String.join(" ", args);

        // Direct navigation if lat/lon
        if (addressInput.toLowerCase().startsWith("lat:")) {
            Uri uri = Uri.parse("google.navigation:q=" + addressInput.substring(4));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
            mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mapIntent.setPackage("com.google.android.apps.maps");
            ctx.startActivity(mapIntent);
            return "Navigating to coordinates: " + addressInput;
        }

        // Geocode suggestions
        Geocoder gc = new Geocoder(ctx, Locale.getDefault());
        try {
            List<Address> results = gc.getFromLocationName(addressInput, 5);
            if (results == null || results.isEmpty()) {
                Uri uri = Uri.parse("google.navigation:q=" + addressInput);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
                mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mapIntent.setPackage("com.google.android.apps.maps");
                ctx.startActivity(mapIntent);
                return "No exact suggestions; launched Maps for: " + addressInput;
            } else {
                StringBuilder out = new StringBuilder();
                int i = 1;
                for (Address a : results) {
                    out.append(i++).append(") ").append(addressToString(a)).append("\n");
                }
                out.append("Type 'nav <number>' to pick an option");
                return out.toString();
            }
        } catch (IOException e) {
            Uri uri = Uri.parse("google.navigation:q=" + addressInput);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
            mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mapIntent.setPackage("com.google.android.apps.maps");
            ctx.startActivity(mapIntent);
            return "Geocoder failed; launched Maps for: " + addressInput;
        }
    }

    private String addressToString(Address a) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i <= a.getMaxAddressLineIndex(); i++) {
            if (i > 0) s.append(", ");
            s.append(a.getAddressLine(i));
        }
        return s.toString();
    }

    @Override
    public String onArg(ExecutePack pack) throws Exception {
        return onExec(pack);
    }

    @Override
    public String getHelp() {
        return "nav <address> | nav lat:<lat>,lon:<lon> - show suggestions or navigate";
    }
}
