package com.example.solar;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Settings extends AppCompatActivity {
    public static final String KEY_HOURS = "graph_hours";
    public static final String KEY_SHOW_WEEKS = "show_weeks";
    public static final String KEY_SHOW_BOILER_OUT = "show_boiler";
    public static final String KEY_SHOW_BOILER_MID = "show_boiler_mid";
    public static final String KEY_MAX_TEMP = "max_bar_temp";
    public static final String KEY_AVG_MAX_MIN = "avg_max_min";
    public static final String SHARED_PREFS = "sharedPrefs";
    private SharedPreferences prefs_shared;
    private SeekBar seekBar_hours;
    private SeekBar seekBar_max_temp;
    private TextView txt_hours;
    private TextView txt_hours_static;
    private TextView txt_max_temp;
    private RadioGroup radioGroup;
    private int selected_avg_max_min;
    int selectedHours;
    int selectedMaxTemp;
    int MAX_HOURS = 24;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graph_settings);

        seekBar_max_temp = findViewById(R.id.seek_max_temp);
        seekBar_hours = findViewById(R.id.seek_hours);
        seekBar_hours.setMax(MAX_HOURS);
        txt_hours = findViewById(R.id.txt_hours_seek);
        txt_max_temp = findViewById(R.id.txt_value_max_temp);
        Button btn_save = findViewById(R.id.btn_Save);
        Button btn_cancel = findViewById(R.id.btn_Cancel);
        SwitchMaterial sw_show_weeks = findViewById(R.id.sw_weeks);
        SwitchMaterial sw_show_boiler_out = findViewById(R.id.sw_show_boiler_out);
        SwitchMaterial sw_show_boiler_mid = findViewById(R.id.sw_show_boiler);
        txt_hours_static = findViewById(R.id.txt_hours_static);
        radioGroup = findViewById(R.id.radioGroup);
        List<Integer> radioIds = new ArrayList<>();
        radioIds.add(R.id.rdio_avg);
        radioIds.add(R.id.rdio_max);
        radioIds.add(R.id.rdio_min);
        prefs_shared = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        selectedHours = prefs_shared.getInt(KEY_HOURS, 1);

        selectedMaxTemp = prefs_shared.getInt(KEY_MAX_TEMP, 75);
        selected_avg_max_min = prefs_shared.getInt(KEY_AVG_MAX_MIN, 0);
        radioGroup.check(radioIds.get(selected_avg_max_min));
        seekBar_max_temp.setProgress(selectedMaxTemp);
        AtomicBoolean showingWeeks = new AtomicBoolean(prefs_shared.getBoolean(KEY_SHOW_WEEKS, false));
        AtomicBoolean showingBoilerOut = new AtomicBoolean(prefs_shared.getBoolean(KEY_SHOW_BOILER_OUT, false));
        AtomicBoolean showingBoilerMid = new AtomicBoolean(prefs_shared.getBoolean(KEY_SHOW_BOILER_MID, true));
        sw_show_weeks.setChecked(showingWeeks.get());

        sw_show_boiler_mid.setChecked(showingBoilerMid.get());
        if (showingBoilerMid.get()){
            sw_show_boiler_out.setChecked(showingBoilerOut.get());
        }
        txt_hours.setText(String.valueOf(selectedHours));
        txt_max_temp.setText(String.valueOf(selectedMaxTemp));



        if (showingWeeks.get()){
            seekBar_hours.setVisibility(View.INVISIBLE);
            txt_hours.setVisibility(View.INVISIBLE);
            radioGroup.setVisibility(View.VISIBLE);
            txt_hours_static.setText(R.string.graphing_weeks);
            showingBoilerOut.set(false);
            sw_show_boiler_out.setVisibility(View.INVISIBLE);

        }else{
            seekBar_hours.setVisibility(View.VISIBLE);
            radioGroup.setVisibility(View.INVISIBLE);
            seekBar_hours.setProgress(selectedHours,true);
        }

        // SeekBar setup
        seekBar_hours.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txt_hours.setText(String.valueOf(progress));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                txt_hours.setTextSize(24);
                txt_hours.setTypeface(null, Typeface.BOLD);
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                txt_hours.setTextSize(20);
                txt_hours.setTypeface(null, Typeface.NORMAL);
            }
        });
        seekBar_max_temp.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txt_max_temp.setText(String.valueOf(progress));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                txt_max_temp.setTextSize(24);
                txt_max_temp.setTypeface(null, Typeface.BOLD);
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                txt_max_temp.setTextSize(20);
                txt_max_temp.setTypeface(null, Typeface.NORMAL);
            }
        });
        showingBoilerOut.set(prefs_shared.getBoolean(KEY_SHOW_BOILER_OUT, false));
        //Switch setup
        sw_show_weeks.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedHours = seekBar_hours.getProgress();
                seekBar_hours.setVisibility(View.INVISIBLE);
                txt_hours.setVisibility(View.INVISIBLE);
                radioGroup.setVisibility(View.VISIBLE);
                txt_hours_static.setText(R.string.graphing_weeks);
                showingWeeks.set(true);
//                showingBoiler.set(false);
                sw_show_boiler_out.setVisibility(View.INVISIBLE);
            } else {
                seekBar_hours.setProgress(selectedHours, true);
                seekBar_hours.setVisibility(View.VISIBLE);
                txt_hours.setVisibility(View.VISIBLE);
                radioGroup.setVisibility(View.INVISIBLE);
                txt_hours_static.setText(R.string.graph_hours);
                showingWeeks.set(false);
//                showingBoiler.set(prefs_shared.getBoolean(KEY_SHOW_BOILER_OUT, false));
                sw_show_boiler_out.setVisibility(View.VISIBLE);
            }
        });
        sw_show_boiler_out.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                showingBoilerOut.set(true);
                sw_show_boiler_mid.setChecked(true);
            } else {
                showingBoilerOut.set(false);
            }
        });
        sw_show_boiler_mid.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                showingBoilerMid.set(true);
//                sw_show_boiler_out.setChecked(prefs_shared.getBoolean(KEY_SHOW_BOILER_OUT, false));
            } else {
                sw_show_boiler_out.setChecked(false);
                showingBoilerMid.set(false);
            }
        });

        // set selection of showing average, max, min
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton checkedRadioButton = findViewById(checkedId);
            if (checkedRadioButton != null) {
                selected_avg_max_min = Integer.parseInt(findViewById(checkedId).getTag().toString());
            }
        });

        btn_save.setOnClickListener(view -> {
            SharedPreferences.Editor editor = prefs_shared.edit();
            editor.putInt(KEY_HOURS, seekBar_hours.getProgress());
            editor.putInt(KEY_MAX_TEMP, seekBar_max_temp.getProgress());
            editor.putBoolean(KEY_SHOW_WEEKS, showingWeeks.get());
            editor.putInt(KEY_AVG_MAX_MIN, selected_avg_max_min);
            editor.putBoolean(KEY_SHOW_BOILER_MID, showingBoilerMid.get());
            if (!showingWeeks.get())
                editor.putBoolean(KEY_SHOW_BOILER_OUT, showingBoilerOut.get());

            editor.apply();
            finish();
        });

        btn_cancel.setOnClickListener(view -> finish());

    }
}
