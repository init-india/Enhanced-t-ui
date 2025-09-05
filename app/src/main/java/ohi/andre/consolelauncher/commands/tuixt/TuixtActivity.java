// File: app/src/main/java/ohi/andre/consolelauncher/commands/tuixt/TuixtActivity.java

package ohi.andre.consolelauncher.commands.tuixt;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import ohi.andre.consolelauncher.R;
import ohi.andre.consolelauncher.commands.Command;
import ohi.andre.consolelauncher.commands.CommandGroup;
import ohi.andre.consolelauncher.commands.CommandTuils;
import ohi.andre.consolelauncher.managers.xml.XMLPrefsManager;
import ohi.andre.consolelauncher.managers.xml.options.Ui;
import ohi.andre.consolelauncher.tuils.StoppableThread;
import ohi.andre.consolelauncher.tuils.Tuils;

public class TuixtActivity extends Activity {

    private final String FIRSTACCESS_KEY = "firstAccess";
    public static final int BACK_PRESSED = 2;
    private long lastEnter;

    public static String PATH = "path";
    public static String ERROR_KEY = "error";

    private EditText inputView;
    private EditText fileView;
    private TextView outputView;

    private TuixtPack pack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final LinearLayout rootView = new LinearLayout(this);

        final Intent intent = getIntent();

        String path = intent.getStringExtra(PATH);
        if(path == null) {
            Uri uri = intent.getData();
            File file = new File(uri.getPath());
            path = file.getAbsolutePath();
        }

        final File file = new File(path);

        CommandGroup group = new CommandGroup(this, "");

        try {
            XMLPrefsManager.loadCommons(this);
        } catch (Exception e) {
            finish();
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(XMLPrefsManager.getColor(Ui.system_statusbar_color));
            window.setNavigationBarColor(XMLPrefsManager.getColor(Ui.system_navbar_color));
        }

        if (!XMLPrefsManager.getBoolean(Ui.system_wallpaper_enabled)) {
            rootView.setBackgroundColor(XMLPrefsManager.getColor(Ui.system_background_color));
        } else {
            setTheme(R.style.Custom_SystemWP);
            rootView.setBackgroundColor(XMLPrefsManager.getColor(Ui.system_background_color));
        }

        final boolean inputBottom = XMLPrefsManager.getBoolean(Ui.input_bottom);
        int layoutId = inputBottom ? R.layout.tuixt_view_bottom : R.layout.tuixt_view_top;
        LayoutInflater inflater = getLayoutInflater();
        View inputOutputView = inflater.inflate(layoutId, null);
        rootView.addView(inputOutputView);

        fileView = inputOutputView.findViewById(R.id.fileView);
        inputView = inputOutputView.findViewById(R.id.inputView);
        outputView = inputOutputView.findViewById(R.id.outputView);

        TextView prefixView = inputOutputView.findViewById(R.id.prefixView);
        ImageButton submitView = inputOutputView.findViewById(R.id.submitView);

        boolean showSubmit = XMLPrefsManager.getBoolean(Ui.show_submit);
        if (!showSubmit) {
            submitView.setVisibility(View.GONE);
            submitView = null;
        }

        String prefix = XMLPrefsManager.get(Ui.input_prefix);
        int ioSize = XMLPrefsManager.getInt(Ui.input_size);
        int outputColor = XMLPrefsManager.getColor(Ui.output_color);
        int inputColor = XMLPrefsManager.getColor(Ui.input_color);

        prefixView.setTypeface(Tuils.getTypeface(this));
        prefixView.setTextColor(inputColor);
        prefixView.setTextSize(ioSize);
        prefixView.setText(prefix);

        if (submitView != null) {
            submitView.setColorFilter(XMLPrefsManager.getColor(Ui.submit_color));
            submitView.setOnClickListener(v -> onNewInput());
        }

        fileView.setTypeface(Tuils.getTypeface(this));
        fileView.setTextSize(ioSize);
        fileView.setTextColor(outputColor);
        fileView.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                outputView.setVisibility(View.GONE);
                outputView.setText(Tuils.EMPTYSTRING);
            }
            return false;
        });

        outputView.setTypeface(Tuils.getTypeface(this));
        outputView.setTextSize(ioSize);
        outputView.setTextColor(outputColor);
        outputView.setMovementMethod(new ScrollingMovementMethod());
        outputView.setVisibility(View.GONE);

        inputView.setTypeface(Tuils.getTypeface(this));
        inputView.setTextSize(ioSize);
        inputView.setTextColor(inputColor);
        inputView.setHint(Tuils.getHint(path));
        inputView.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        inputView.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_GO) {
                onNewInput();
                return true;
            }
            return false;
        });

        setContentView(rootView);

        pack = new TuixtPack(group, file, this, fileView);

        fileView.setText(getString(R.string.tuixt_reading));
        new StoppableThread() {
            @Override
            public void run() {
                super.run();
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    final StringBuilder builder = new StringBuilder();
                    String line, lastLine = null;
                    while((line = reader.readLine()) != null) {
                        if(lastLine != null) builder.append(Tuils.NEWLINE);
                        builder.append(line);
                        lastLine = line;
                    }
                    runOnUiThread(() -> {
                        try {
                            fileView.setText(builder.toString());
                        } catch (OutOfMemoryError e) {
                            fileView.setText(Tuils.EMPTYSTRING);
                            Toast.makeText(TuixtActivity.this, "OOM", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    Log.e("TuixtActivity", "", e);
                    intent.putExtra(ERROR_KEY, e.toString());
                    setResult(1, intent);
                    finish();
                }
            }
        }.start();

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        boolean firstAccess = preferences.getBoolean(FIRSTACCESS_KEY, true);
        if (firstAccess) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(FIRSTACCESS_KEY, false);
            editor.commit();

            inputView.setText("help");
            inputView.setSelection(inputView.getText().length());
        }
    }

    @Override
    public void onBackPressed() {
        setResult(BACK_PRESSED);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.finish();
    }

    private void onNewInput() {
        try {
            String input = inputView.getText().toString();
            inputView.setText(Tuils.EMPTYSTRING);

            input = input.trim();
            if(input.length() == 0) return;

            outputView.setVisibility(View.VISIBLE);

            Command command = CommandTuils.parse(input);
            if(command == null) {
                outputView.setText(R.string.output_command_not_found);
                return;
            }

            String output = command.exec(pack);
            if(output != null) outputView.setText(output);
        } catch (Exception e) {
            outputView.setText(e.toString());
        }
    }
}
