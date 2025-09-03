package ohi.andre.consolelauncher.managers;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ohi.andre.consolelauncher.tuils.Tuils;

/**
 * TUI Location Manager for CLI-based location suggestions and voice-guided navigation.
 */
public class TuiLocationManager {

    private final Context context;
    private final TextToSpeech tts;
    private final Handler handler;

    private List<String> suggestions;
    private String destination;

    public TuiLocationManager(@NonNull Context context) {
        this.context = context;
        this.handler = new Handler();
        this.suggestions = new ArrayList<>();
        this.destination = null;

        // Initialize TextToSpeech
        tts = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.getDefault());
            } else {
                Log.e("TuiLocationManager", "TextToSpeech initialization failed");
            }
        });
    }

    /**
     * Fetch suggestions based on partial query.
     * CLI will call this as user types location.
     */
    public List<String> getSuggestions(String query) {
        // Placeholder: In practice, integrate Google Places API or OSM API
        suggestions.clear();

        if (query.toLowerCase().contains("baker")) {
            suggestions.add("221B Baker Street, London");
            suggestions.add("221B Baker Avenue, Manchester");
            suggestions.add("221B Bamboo Street, Dublin");
        } else if (query.toLowerCase().contains("main")) {
            suggestions.add("Main Street, Springfield");
            suggestions.add("Main Avenue, New York");
        } else {
            suggestions.add("Central Park, New York");
            suggestions.add("Times Square, New York");
        }

        return suggestions;
    }

    /**
     * Set destination after user selects a suggestion.
     */
    public void setDestination(int index) {
        if (index >= 0 && index < suggestions.size()) {
            destination = suggestions.get(index);
            Tuils.sendOutput(0xFF00FF00, context, "Destination selected: " + destination);
        } else {
            Tuils.sendOutput(0xFFFF0000, context, "Invalid selection index!");
        }
    }

    /**
     * Start voice-guided navigation to the selected destination.
     */
    public void navigate() {
        if (destination == null) {
            Tuils.sendOutput(0xFFFF0000, context, "No destination selected!");
            return;
        }

        Tuils.sendOutput(0xFF00FF00, context, "Starting navigation to: " + destination);

        // Placeholder for routing steps
        String[] steps = new String[]{
                "Head north for 200 meters",
                "Turn right onto Baker Street",
                "Destination will be on the left"
        };

        // Announce each step with delay
        for (int i = 0; i < steps.length; i++) {
            final int idx = i;
            handler.postDelayed(() -> speakStep(steps[idx]), i * 5000L); // 5s interval
        }
    }

    /**
     * Speak a single navigation step.
     */
    private void speakStep(String step) {
        Tuils.sendOutput(0xFFFFFFFF, context, "NAV: " + step);
        tts.speak(step, TextToSpeech.QUEUE_ADD, null, "NAV_STEP");
    }

    /**
     * Stop navigation.
     */
    public void stopNavigation() {
        tts.stop();
        Tuils.sendOutput(0xFFFFFF00, context, "Navigation stopped.");
    }

    /**
     * Pause navigation (TTS pause only, routing still active in real implementation).
     */
    public void pauseNavigation() {
        tts.stop();
        Tuils.sendOutput(0xFFFFFF00, context, "Navigation paused.");
    }

    /**
     * Resume navigation from pause.
     */
    public void resumeNavigation() {
        Tuils.sendOutput(0xFFFFFF00, context, "Resuming navigation...");
        navigate();
    }

    /**
     * Release TTS resources on destroy.
     */
    public void onDestroy() {
        if (tts != null) {
            tts.shutdown();
        }
    }
}
