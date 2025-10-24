package com.example.solar;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private TextView time;
    private LinearLayout linearLayout;
    private int max_bar_temp;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference collRef = db.collection("test");
    private DocumentReference documentReference = null;
    private ListenerRegistration listenerRegistration = null;
    private final String[] SENSOR_NAMES = {"Date","Glycol Roof",
            "Glycol In","Glycol Out Tank","Glycol Out HE",
            "S Tank High","S Tank Mid","S Tank Low",
            "Boiler Mid","Boiler Out","S Tank Out"};
    private final AtomicReference<LocalDateTime> currentHourUTC = new AtomicReference<>(LocalDateTime.now(ZoneId.of("UTC"))
            .withMinute(0)
            .withSecond(0)
            .withNano(0));
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        time = findViewById(R.id.textView);
        linearLayout = findViewById(R.id.layout_temperatures);

        // get current time to query the database

        // initial load. gets the preferences and updates the temps
        update_prefs();
        updateTemperatures(currentHourUTC.get());


        // when update button is clicked
        Button btnUpdate = findViewById(R.id.btn_update);
        btnUpdate.setOnClickListener(view -> {
            currentHourUTC.set(LocalDateTime.now(ZoneId.of("UTC"))
                    .withMinute(0)
                    .withSecond(0)
                    .withNano(0));
            updateTemperatures(currentHourUTC.get());
        });
        Button btnGraph = findViewById(R.id.btnGraph);
        btnGraph.setOnClickListener(view -> {
            Intent intent = new Intent(this, Graphing.class);
            // intent.putExtra("solarData", "value");
            startActivity(intent);
        });
        Button btnSettings = findViewById(R.id.btnSettingsMain);
        btnSettings.setOnClickListener(view -> {
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
        });

        // subscribe to notifications
        FirebaseMessaging.getInstance().subscribeToTopic("hot")
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        String msg = "Subscribe failed";
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
                    }
                });
        FirebaseMessaging.getInstance().subscribeToTopic("debug")
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        String msg = "Subscribe failed";
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
                    }
                });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        Log.d(TAG, "on restart");
        update_prefs();
        currentHourUTC.set(LocalDateTime.now(ZoneId.of("UTC"))
                .withMinute(0)
                .withSecond(0)
                .withNano(0));
        updateTemperatures(currentHourUTC.get());
    }
    @Override
    protected void onResume() {
        super.onResume();
        registerListener();

    }
    void registerListener(){
        if (documentReference != null) {
            listenerRegistration = documentReference.addSnapshotListener((docSnapshot, e) -> {
                if (e != null) {
                    Log.d(TAG, "Error in registerListener");
                    return;
                }

                if (docSnapshot != null && docSnapshot.exists()) {

                    linearLayout.removeAllViews();
                    DecimalFormat df = new DecimalFormat("0.0");
                    List<String> lines = (List<String>) docSnapshot.get("lines");
                    Double glycol_in_max = (Double) docSnapshot.get("glycol_in_max");
                    Double glycol_roof_max = (Double) docSnapshot.get("glycol_roof_max");
                    assert lines != null;
                    String[] lineArray = lines.get(lines.size()-1).split(",");
                    List<String> lineArrayList = new ArrayList<>(Arrays.asList(lineArray));

                    DateTimeFormatter formatterInput = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    DateTimeFormatter formatterOutput = DateTimeFormatter.ofPattern("E, MMM d    h:mm:ss a");
                    LocalDateTime timeLocal = LocalDateTime.parse(lineArrayList.get(0), formatterInput);
                    time.setText(timeLocal.format(formatterOutput));
                    // Process the last line
                    for (int i = 1; i < lineArrayList.size() ; i++){
                        if (i <= 2 ) {
                            TemperatureLineMax lineLayout = new TemperatureLineMax(this, max_bar_temp);
                            lineLayout.setName(SENSOR_NAMES[i]);
//                                        lineLayout.setNumBold(true);
                            Double tempDbl = Double.parseDouble(lineArrayList.get(i));
//                                        lineLayout.setValue(value);
                            lineLayout.setCurrValue(df.format(tempDbl));
                            lineLayout.setProgress(tempDbl);
                            lineLayout.setMaxBarTemp(max_bar_temp);
                            if (i == 1)
                                lineLayout.setMaxValue(df.format(glycol_roof_max));
                            else if (i == 2)
                                lineLayout.setMaxValue(df.format(glycol_in_max));
                            linearLayout.addView(lineLayout);
                        } else if (i > 2 && i != 4) {
                            TemperatureLine lineLayout = new TemperatureLine(this, max_bar_temp);
                            lineLayout.setName(SENSOR_NAMES[i]);
//                                        lineLayout.setNumBold(true);
                            Double tempDbl = Double.parseDouble(lineArrayList.get(i));
//                                        lineLayout.setValue(value);
                            lineLayout.setValue(df.format(tempDbl));
                            lineLayout.setProgress(tempDbl);
                            lineLayout.setMaxBarTemp(max_bar_temp);
                            linearLayout.addView(lineLayout);
                            }
                        }
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }
    private void update_prefs(){
        SharedPreferences prefs_shared = getSharedPreferences(Settings.SHARED_PREFS, MODE_PRIVATE);
        max_bar_temp = prefs_shared.getInt(Settings.KEY_MAX_TEMP, 75);
        // update the Bar maximums
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            View child = linearLayout.getChildAt(i);
            if (child instanceof TemperatureLine) {
                ((TemperatureLine) child).setMaxBarTemp(max_bar_temp);
            }else if (child instanceof TemperatureLineMax) {
                ((TemperatureLineMax) child).setMaxBarTemp(max_bar_temp);
            }
        }
    }
    /** @noinspection ConstantValue*/
    private void updateTemperatures(LocalDateTime currentHourUTC){
        String fieldName = "hour";
        Date fsDate = Date.from(currentHourUTC.atZone(ZoneId.of("UTC")).toInstant());
        Date fsDate2 = Date.from(currentHourUTC.plusSeconds(1).atZone(ZoneId.of("UTC")).toInstant());
        collRef.whereGreaterThanOrEqualTo(fieldName, fsDate)
                .whereLessThanOrEqualTo(fieldName, fsDate2)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            linearLayout.removeAllViews();
                            DecimalFormat df = new DecimalFormat("0.0");
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                List<String> lines = (List<String>) document.get("lines");
                                Double glycol_in_max = (Double) document.get("glycol_in_max");
                                Double glycol_roof_max = (Double) document.get("glycol_roof_max");
                                documentReference = document.getReference();
                                // get the last line
                                assert lines != null;
                                String[] lineArray = lines.get(lines.size()-1).split(",");
                                List<String> lineArrayList = new ArrayList<>(Arrays.asList(lineArray));

                                DateTimeFormatter formatterInput = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                DateTimeFormatter formatterOutput = DateTimeFormatter.ofPattern("E, MMM d    h:mm:ss a");
                                LocalDateTime timeLocal = LocalDateTime.parse(lineArrayList.get(0), formatterInput);
                                time.setText(timeLocal.format(formatterOutput));
                                // Process the last line
                                for (int i = 1; i < lineArrayList.size() ; i++){
                                    if (i <= 2 ) {
                                        TemperatureLineMax lineLayout = new TemperatureLineMax(this, max_bar_temp);
                                        lineLayout.setName(SENSOR_NAMES[i]);
//                                        lineLayout.setNumBold(true);
                                        Double tempDbl = Double.parseDouble(lineArrayList.get(i));
//                                        lineLayout.setValue(value);
                                        lineLayout.setCurrValue(df.format(tempDbl));
                                        lineLayout.setProgress(tempDbl);
                                        lineLayout.setMaxBarTemp(max_bar_temp);
                                        if (i == 1)
                                            lineLayout.setMaxValue(df.format(glycol_roof_max));
                                        else if (i == 2)
                                            lineLayout.setMaxValue(df.format(glycol_in_max));
                                        linearLayout.addView(lineLayout);
                                    } else if (i > 2 && i != 4) {
                                        TemperatureLine lineLayout = new TemperatureLine(this, max_bar_temp);
                                        lineLayout.setName(SENSOR_NAMES[i]);
//                                        lineLayout.setNumBold(true);
                                        Double tempDbl = Double.parseDouble(lineArrayList.get(i));
//                                        lineLayout.setValue(value);
                                        lineLayout.setValue(df.format(tempDbl));
                                        lineLayout.setProgress(tempDbl);
                                        lineLayout.setMaxBarTemp(max_bar_temp);
                                        linearLayout.addView(lineLayout);
                                    }
                                }
                            }
                            registerListener();
                        }
                    }else{
                        time.setText(R.string.no_data_found);
                    }
                });

    }
}