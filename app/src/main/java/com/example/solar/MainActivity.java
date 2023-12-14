package com.example.solar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity {
    private TextView time;
    private LinearLayout linearLayout;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collRef = db.collection("test");
    private String[] SENSOR_NAMES = {"Date","Glycol Roof",
            "Glycol In","Glycol Out Tank","Glycol Out HE",
            "Solar Tank High","Solar Tank Mid","Solar Tank Low",
            "Boiler Tank Mid","Boiler Tank Out","Solar Tank Out"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        time = findViewById(R.id.textView1);
        linearLayout = findViewById(R.id.layout_temperatures);
        // get current time to query the database
        AtomicReference<LocalDateTime> currentHourUTC = new AtomicReference<>(LocalDateTime.now(ZoneId.of("UTC"))
                .withMinute(0)
                .withSecond(0)
                .withNano(0));

        // initial load
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
    }

    private void updateTemperatures(LocalDateTime currentHourUTC){
        String fieldName = "hour";
        Date fsDate = Date.from(currentHourUTC.atZone(ZoneId.of("UTC")).toInstant());
        Date fsDate2 = Date.from(currentHourUTC.plusSeconds(1).atZone(ZoneId.of("UTC")).toInstant());
        collRef.whereGreaterThanOrEqualTo(fieldName, fsDate)
                .whereLessThanOrEqualTo(fieldName, fsDate2)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // The query was successful
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            linearLayout.removeAllViews();
                            // Iterate through the matching documents
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                List<String> lines = (List<String>) document.get("lines");
                                // get the last line
                                String[] lineArray = lines.get(lines.size()-1).split(",");
                                List<String> lineArrayList = new ArrayList<>(Arrays.asList(lineArray));
                                time.setText(lineArrayList.get(0));
                                int i = 0;
                                for (String value : lineArrayList){
                                    if (i > 0) {
                                        LinearListTemp lineLayout = new LinearListTemp(this);
                                        lineLayout.setName(SENSOR_NAMES[i]);
                                        lineLayout.setValue(value);
                                        linearLayout.addView(lineLayout);
                                    }
                                    i++;
                                }
                            }
                        }
                    } else {
                        Log.d("DEBUG--", "Task Failed");
                    }
                });
    }
}