package com.example.solar;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GraphSettings extends AppCompatActivity {
    private static final String KEY_HOURS = "graph_hours";
    public static final String SHARED_PREFS = "sharedPrefs";
    private SeekBar seekBar_hours;
    private TextView txt_hours;
    private Button btn_save;
    private SharedPreferences prefs_shared;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graph_settings);

        seekBar_hours = findViewById(R.id.seek_hours);
        txt_hours = findViewById(R.id.txt_hours_seek);
        btn_save = findViewById(R.id.btn_Save);

        // get saved hours value
        prefs_shared = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        int selectedHours = prefs_shared.getInt(KEY_HOURS, 1);
        seekBar_hours.setProgress(selectedHours);
        txt_hours.setText(String.valueOf(selectedHours));


        // SeekBar setup
        seekBar_hours.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txt_hours.setText(String.valueOf(progress));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                txt_hours.setTextSize(20);
                txt_hours.setTypeface(null, Typeface.BOLD);
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                txt_hours.setTextSize(18);
                txt_hours.setTypeface(null, Typeface.NORMAL);
            }
        });

        btn_save.setOnClickListener(view -> {
            SharedPreferences.Editor editor = prefs_shared.edit();
            editor.putInt(KEY_HOURS, seekBar_hours.getProgress());
            editor.apply();
        });

    }
}
