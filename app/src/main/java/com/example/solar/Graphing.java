package com.example.solar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Graphing extends AppCompatActivity {
    private LineChart chart;
    private List<SolarData> solarDataArray;
    List<Entry> entries;
    List<Entry> entries2;
    List<Entry> entries3;
    LineDataSet dataSet;
    LineDataSet dataSet2;
    LineDataSet dataSet3;
    LineData lineData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphing);
        chart = findViewById(R.id.lineChart);


        /*
        After you have an instance of your chart, you can create data and add it to the chart.
         This example uses the LineChart, for which the Entry class represents a single entry
         in the chart with x- and y-coordinate. Other chart types, such as BarChart use other
         classes (e.g. BarEntry) for that purpose.
         */
        entries = new ArrayList<>();
        entries2 = new ArrayList<>();
        entries3 = new ArrayList<>();

        /*
        As a next step, you need to add the List<Entry> you created to a LineDataSet object.
        DataSet objects hold data which belongs together, and allow individual styling of that data.
        The below used “Label” has only a descriptive purpose and shows up in the Legend, if enabled.
         */
//        dataSet = new LineDataSet(entries, "Label"); // add entries to dataset
//        dataSet.setColor(...);
//        dataSet.setValueTextColor(...); // styling, ...

        /*
        As a last step, you need to add the LineDataSet object (or objects)
        you created to a LineData object. This object holds all data that is
        represented by a Chart instance and allows further styling.
        After creating the data object, you can set it to the chart and refresh it:
         */
//        lineData = new LineData(dataSet);
//        chart.setData(lineData);
//        chart.invalidate(); // refresh

        AtomicReference<LocalDateTime> currentHourUTC = new AtomicReference<>(LocalDateTime.now(ZoneId.of("UTC"))
                .withMinute(0)
                .withSecond(0)
                .withNano(0));
        getTemps(currentHourUTC.get());
        Button btnMain = findViewById(R.id.btn_main);
        btnMain.setOnClickListener(view -> {
            finish();
            Intent intent = new Intent(this, MainActivity.class);
            // intent.putExtra("solarData", "value");
            startActivity(intent);
        });


    }

    private void getTemps(LocalDateTime currentHourUTC){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collRef = db.collection("test");
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
                            solarDataArray = new ArrayList<>();
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                List<String> lines = (List<String>) document.get("lines");
                                // convert each line (String) into floats and date
                                assert lines != null;
                                int i = 0;
                                for (String line : lines){
                                    String[] lineReading = line.split(",");
//                                    Log.d("DEBUG", line);

                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                                    SolarData solarReading = new SolarData(
                                            LocalDateTime.parse(lineReading[0], formatter),
                                            Float.parseFloat(lineReading[1]),
                                            Float.parseFloat(lineReading[2]),
                                            Float.parseFloat(lineReading[3]),
                                            Float.parseFloat(lineReading[4]),
                                            Float.parseFloat(lineReading[5]),
                                            Float.parseFloat(lineReading[6]),
                                            Float.parseFloat(lineReading[7]),
                                            Float.parseFloat(lineReading[8]),
                                            Float.parseFloat(lineReading[9]),
                                            Float.parseFloat(lineReading[10]));
                                    solarDataArray.add(solarReading);

                                    // remove 50 years before conversion to keep precision.
                                    long seconds = solarReading.getDate().minusYears(50).toEpochSecond(ZoneOffset.UTC);

                                    Log.d("DEBUG", String.valueOf(seconds ));
                                    entries.add(new Entry(seconds , solarReading.getSolarTankHigh()));
                                    entries2.add(new Entry(seconds , solarReading.getSolarTankMid()));
                                    entries3.add(new Entry(seconds , solarReading.getSolarTankLow()));
                                    i++;
                                    }
                                // Set up the x-axis
                                XAxis xAxis = chart.getXAxis();
                                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                                xAxis.setGranularity(1f);

                                xAxis.setValueFormatter(new XAxisFormatter());
                                // Create a LineDataSet with the entries
                                dataSet = new LineDataSet(entries, "Solar Tank High");
                                dataSet.setColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                                dataSet.setDrawCircles(false);
                                dataSet.setDrawValues(false);
                                dataSet.setLineWidth(3);

                                dataSet2 = new LineDataSet(entries2, "Solar Tank Mid");
                                dataSet2.setColor(ContextCompat.getColor(getApplicationContext(), R.color.orange));
                                dataSet2.setDrawCircles(false);
                                dataSet2.setDrawValues(false);
                                dataSet2.setLineWidth(3);

                                dataSet3 = new LineDataSet(entries3, "Solar Tank Low");
                                dataSet3.setColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                                dataSet3.setDrawCircles(false);
                                dataSet3.setDrawValues(false);
                                dataSet3.setLineWidth(3);

                                // Create a LineData object with the LineDataSet
                                LineData lineData = new LineData(dataSet, dataSet2, dataSet3);
                                // Set data to the LineChart
                                chart.setData(lineData);
//                                chart.getDataSetByTouchPoint()
                                chart.invalidate(); // Refresh the chart






                            }
                            for (SolarData sd :solarDataArray){
                                Log.d("DEBUG", sd.toString());
                            }
                        }
                    } else {
                        Log.d("DEBUG--", "Task Failed");
                    }
                });
    }
}
