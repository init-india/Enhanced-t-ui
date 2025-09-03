package ohi.andre.consolelauncher;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import ohi.andre.consolelauncher.managers.MainManager;
import ohi.andre.consolelauncher.tuils.Tuils;

public class LauncherActivity extends AppCompatActivity implements Reloadable {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        // Initialize stuff
        MainManager.init(this);
    }

    public void onButtonClick(View view) {
        Intent i = new Intent(this, MainManager.class);
        startActivity(i);
    }

    @Override
    public void reload() {
        Tuils.sendOutput("Reload called");
    }
}
