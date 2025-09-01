package ohi.andre.consolelauncher.commands.main.specific;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.text.TextUtils;

import ohi.andre.consolelauncher.CommandAbstraction;
import ohi.andre.consolelauncher.ExecutePack;

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
        // Usage:
        // nav <address>  -> suggest & launch
        // nav lat:12.34,lon:56.78 -> direct
        String[] args = pack.getArgs();
        Context ctx = pack.context;
        if (args == null || args.length == 0) return "Usage: nav <address|lat:xx,lon:yy>";

        StringBuilder sb = new StringBuilder();
        for (String s : args) {
            if (sb.length() > 0) sb.append(' ');
            sb.append(s);
        }
        String input = sb.toString().trim();

        if (input.toLowerCase().startsWith("lat:")) {
            String coord = input.substring(4);
            // expected format lat:12.34,lon:56.78
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + Uri.encode(coord));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(mapIntent);
            return "Navigating to " + coord;
        }

        // Use Geocoder to find suggestions (local)
        Geocoder gc = new Geocoder(ctx, Locale.getDefault());
        try {
            List<Address> results = gc.getFromLocationName(input, 5);
            if (results == null || results.isEmpty()) {
                // fallback to launching maps with raw query
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + Uri.encode(input));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(mapIntent);
                return "No exact suggestions; launched Maps with query: " + input;
            } else {
                StringBuilder out = new StringBuilder();
                int i = 1;
                for (Address a : results) {
                    String line = addressToString(a);
                    out.append(i++).append(") ").append(line).append("\n");
                }
                out.append("Type 'nav <number>' to open (e.g. nav 1).");
                // save suggestions to prefs for subsequent selection
                ctx.getSharedPreferences("tui_nav", Context.MODE_PRIVATE).edit()
                        .putString("last_query", input)
                        .putString("last_results", serializeAddresses(results))
                        .apply();
                return out.toString();
            }
        } catch (IOException e) {
            // fallback: launch maps
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + Uri.encode(input));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(mapIntent);
            return "Geocoder failed; launched Maps with query: " + input;
        }
    }

    private String serializeAddresses(List<Address> list) {
        StringBuilder sb = new StringBuilder();
        for (Address a : list) {
            if (sb.length() > 0) sb.append("||;");
            sb.append(addressToString(a));
        }
        return sb.toString();
    }

    private String addressToString(Address a) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i <= a.getMaxAddressLineIndex(); i++) {
            if (i > 0) s.append(", ");
            s.append(a.getAddressLine(i));
        }
        if (TextUtils.isEmpty(s)) {
            if (a.getLatitude() != 0 && a.getLongitude() != 0) {
                s.append(a.getLatitude()).append(",").append(a.getLongitude());
            } else {
                s.append("Unknown");
            }
        }
        return s.toString();
    }

    @Override
    public String onArg(ExecutePack pack) throws Exception {
        // detect if user typed 'nav <number>' to pick from suggestions
        String[] args = pack.getArgs();
        Context ctx = pack.context;
        if (args != null && args.length == 1) {
            String token = args[0];
            try {
                int pick = Integer.parseInt(token);
                String serialized = ctx.getSharedPreferences("tui_nav", Context.MODE_PRIVATE).getString("last_results", null);
                if (serialized == null) return "No previous suggestions cached.";
                String[] parts = serialized.split("\\|\\|;");
                if (pick < 1 || pick > parts.length) return "Invalid selection.";
                String address = parts[pick - 1];
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + Uri.encode(address));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(mapIntent);
                return "Navigating to: " + address;
            } catch (NumberFormatException ignored) {}
        }
        return onExec(pack);
    }

    @Override
    public String getHelp() {
        return "nav <address> | nav lat:<lat>,lon:<lon> - navigate using Google Maps; suggestions supported";
    }
}
