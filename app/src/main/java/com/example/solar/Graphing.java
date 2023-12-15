package com.example.solar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Graphing extends AppCompatActivity {
    private static final String KEY_HOURS = "graph_hours";
    public static final String SHARED_PREFS = "sharedPrefs";
    AtomicReference<LocalDateTime> currentHourUTC;
    private LineChart chart_solar_tank;
    private LineChart chart_glycol;
    private List<SolarData> solarDataArray;
    private Button btn_settings;
    private Button btn_reset_zoom;
    private TextView txtDataGlycol;
    private TextView txtDataST;
    List<Entry> entries;
    List<Entry> entries2;
    List<Entry> entries3;
    List<Entry> entries4;
    List<Entry> entries_glycol_roof;
    List<Entry> entries_glycol_in;
    List<Entry> entries_glycol_out_st;
    List<Entry> entries_glycol_out_he;
    LineDataSet dataSet;
    LineDataSet dataSet2;
    LineDataSet dataSet3;
    LineDataSet dataSet4;
    LineDataSet dataSet_glycol_roof;
    LineDataSet dataSet_glycol_in;
    LineDataSet dataSet_glycol_out_tank;
    LineDataSet dataSet_glycol_out_he;
    LineData lineData;
    LineData lineData_glycol;
    private SharedPreferences prefs_shared;
    int prefs_Hours;
    int current_Hours;
    boolean updating = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphing);
        chart_solar_tank = findViewById(R.id.lineChartSolarTank);
        chart_glycol = findViewById(R.id.lineChartGlycol);
        btn_settings = findViewById(R.id.btn_settings);
        btn_reset_zoom = findViewById(R.id.btn_reset_zoom);
        txtDataGlycol = findViewById(R.id.txtDataPointGlycol);
        txtDataST = findViewById(R.id.txtDataPointST);
        // get saved hours value
        prefs_shared = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        prefs_Hours = prefs_shared.getInt(KEY_HOURS, 1);
        current_Hours = prefs_Hours;


        /*
        After you have an instance of your chart, you can create data and add it to the chart.
         This example uses the LineChart, for which the Entry class represents a single entry
         in the chart with x- and y-coordinate. Other chart types, such as BarChart use other
         classes (e.g. BarEntry) for that purpose.
         */
        entries = new ArrayList<>();
        entries2 = new ArrayList<>();
        entries3 = new ArrayList<>();
        entries4 = new ArrayList<>();

        entries_glycol_roof = new ArrayList<>();
        entries_glycol_in = new ArrayList<>();
        entries_glycol_out_st = new ArrayList<>();
        entries_glycol_out_he = new ArrayList<>();

        currentHourUTC = new AtomicReference<>(LocalDateTime.now(ZoneId.of("UTC"))
                .withMinute(0)
                .withSecond(0)
                .withNano(0));
        getTemps(currentHourUTC.get());


        btn_settings.setOnClickListener(view -> {
            showSettings();
        });
        btn_reset_zoom.setOnClickListener(view -> {
            chart_glycol.fitScreen();
            chart_solar_tank.fitScreen();
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        prefs_Hours = prefs_shared.getInt(KEY_HOURS, 1);
        if (current_Hours != prefs_Hours){
            updating = true;
            updateCharts();
            updating = false;
        }
    }

    private void updateCharts() {
//        chart_glycol.clearValues();
//        chart_solar_tank.clearValues();
        entries.clear();
        entries2.clear();
        entries3.clear();
        entries4.clear();
        entries_glycol_roof.clear();
        entries_glycol_in.clear();
        entries_glycol_out_st.clear();
        entries_glycol_out_he.clear();
        getTemps(currentHourUTC.get());
    }

    private void showSettings() {
        Intent intent = new Intent(this, GraphSettings.class);
        // intent.putExtra("solarData", "value");
        startActivity(intent);

    }

    private void getTemps(LocalDateTime currentHourUTC){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collRef = db.collection("test");
        String fieldName = "hour";
        Date fsDate = Date.from(currentHourUTC.minusHours(prefs_Hours-1).atZone(ZoneId.of("UTC")).toInstant());
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
                                    // creating data object
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
                                    entries.add(new Entry(seconds , solarReading.getSolarTankHigh()));
                                    entries2.add(new Entry(seconds , solarReading.getSolarTankMid()));
                                    entries3.add(new Entry(seconds , solarReading.getSolarTankLow()));
                                    entries4.add(new Entry(seconds , solarReading.getBoilerTankMid()));

                                    entries_glycol_roof.add(new Entry(seconds , solarReading.getGlycolRoof()));
                                    entries_glycol_in.add(new Entry(seconds , solarReading.getGlycolIn()));
                                    entries_glycol_out_st.add(new Entry(seconds , solarReading.getGlycolOutTank()));
                                    entries_glycol_out_he.add(new Entry(seconds , solarReading.getGlycolOutHE()));
                                    i++;
                                }

                            }
                            XAxisFormatter timeFormatter = new XAxisFormatter();
                            // Set up the x-axis
                            XAxis xAxis = chart_solar_tank.getXAxis();
                            xAxis.setTextSize(10f);
                            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                            xAxis.setGranularity(1f);
                            xAxis.setValueFormatter(timeFormatter);
                            // Create a LineDataSet with the entries
                            // TODO create a function that does this to each dataset
                            dataSet = new LineDataSet(entries, "Solar High");
                            dataSet.setColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                            dataSet.setDrawCircles(false);
                            dataSet.setDrawValues(false);
                            dataSet.setLineWidth(3);
                            dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                            dataSet.setCubicIntensity(0.2f);

                            dataSet2 = new LineDataSet(entries2, "Solar Mid");
                            dataSet2.setColor(ContextCompat.getColor(getApplicationContext(), R.color.orange));
                            dataSet2.setDrawCircles(false);
                            dataSet2.setDrawValues(false);
                            dataSet2.setLineWidth(3);
                            dataSet2.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                            dataSet2.setCubicIntensity(0.2f);

                            dataSet3 = new LineDataSet(entries3, "Solar Low");
                            dataSet3.setColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                            dataSet3.setDrawCircles(false);
                            dataSet3.setDrawValues(false);
                            dataSet3.setLineWidth(3);
                            dataSet3.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                            dataSet3.setCubicIntensity(0.2f);

                            dataSet4 = new LineDataSet(entries4, "Boiler Mid");
                            dataSet4.setColor(ContextCompat.getColor(getApplicationContext(), R.color.purple));
                            dataSet4.setDrawCircles(false);
                            dataSet4.setDrawValues(false);
                            dataSet4.setLineWidth(3);
                            dataSet4.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                            dataSet4.setCubicIntensity(0.2f);

                            // Set up the x-axis
                            XAxis xAxis_glycol_chart = chart_glycol.getXAxis();
                            xAxis_glycol_chart.setTextSize(10f);
                            xAxis_glycol_chart.setPosition(XAxis.XAxisPosition.BOTTOM);
                            xAxis_glycol_chart.setGranularity(1f);
                            xAxis_glycol_chart.setValueFormatter(timeFormatter);


                            dataSet_glycol_roof = new LineDataSet(entries_glycol_roof, "Glycol Roof");
                            dataSet_glycol_roof.setColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                            dataSet_glycol_roof.setDrawCircles(false);
                            dataSet_glycol_roof.setDrawValues(false);
                            dataSet_glycol_roof.setLineWidth(3);
                            // Make the line smooth
                            dataSet_glycol_roof.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                            dataSet_glycol_roof.setCubicIntensity(0.2f);

                            dataSet_glycol_in = new LineDataSet(entries_glycol_in, "Glycol In");
                            dataSet_glycol_in.setColor(ContextCompat.getColor(getApplicationContext(), R.color.orange));
                            dataSet_glycol_in.setDrawCircles(false);
                            dataSet_glycol_in.setDrawValues(false);
                            dataSet_glycol_in.setLineWidth(3);
                            dataSet_glycol_in.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                            dataSet_glycol_in.setCubicIntensity(0.2f);

                            dataSet_glycol_out_tank = new LineDataSet(entries_glycol_out_st, "Glycol Out Tank");
                            dataSet_glycol_out_tank.setColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                            dataSet_glycol_out_tank.setDrawCircles(false);
                            dataSet_glycol_out_tank.setDrawValues(false);
                            dataSet_glycol_out_tank.setLineWidth(3);
                            dataSet_glycol_out_tank.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                            dataSet_glycol_out_tank.setCubicIntensity(0.2f);


                            dataSet_glycol_out_he = new LineDataSet(entries_glycol_out_he, "Glycol Out HE");
                            dataSet_glycol_out_he.setColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                            dataSet_glycol_out_he.setDrawCircles(false);
                            dataSet_glycol_out_he.setDrawValues(false);
                            dataSet_glycol_out_he.setLineWidth(3);
                            dataSet_glycol_out_he.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                            dataSet_glycol_out_he.setCubicIntensity(0.2f);

                            // Create LineData object and set to LineChart
                            lineData_glycol = new LineData(dataSet_glycol_roof, dataSet_glycol_in, dataSet_glycol_out_tank, dataSet_glycol_out_he);
                            chart_glycol.setData(lineData_glycol);


                            lineData = new LineData(dataSet4, dataSet, dataSet2, dataSet3);
                            chart_solar_tank.setData(lineData);


                            // Listener to get the selected point
                            chart_solar_tank.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                                @Override
                                public void onValueSelected(Entry e, Highlight h) {

                                    // Get the selected point's x and y values
                                    float yValue = e.getY();
                                    String xVal = timeFormatter.StringFromFloat(e.getX());

                                    LineDataSet dataSet = (LineDataSet) chart_solar_tank.getData().getDataSetByIndex(h.getDataSetIndex());

                                    // Do something with the selected point
                                    txtDataST.setVisibility(View.VISIBLE);
                                    txtDataST.setText(dataSet.getLabel()+ "  @  " + xVal + "  =  " + yValue + "  C");
                                }
                                @Override
                                public void onNothingSelected() {
                                    // Called when nothing is selected
                                    txtDataST.setVisibility(View.INVISIBLE);
                                }
                            });
                            chart_glycol.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                                @Override
                                public void onValueSelected(Entry e, Highlight h) {
                                    // Get the selected point's x and y values
                                    float yValue = e.getY();
                                    String xVal = timeFormatter.StringFromFloat(e.getX());

                                    LineDataSet dataSet = (LineDataSet) chart_glycol.getData().getDataSetByIndex(h.getDataSetIndex());

                                    // Do something with the selected point
                                    txtDataGlycol.setVisibility(View.VISIBLE);
                                    txtDataGlycol.setText(dataSet.getLabel()+ "  @  " + xVal + "  =  " + yValue + "  C");

                                }
                                @Override
                                public void onNothingSelected() {
                                    // Called when nothing is selected
                                    txtDataGlycol.setVisibility(View.INVISIBLE);
                                }
                            });


                            // set chart settings for glycol chart
                            chart_glycol.setVisibleXRangeMinimum(240); // seconds
                            chart_glycol.setVisibleYRangeMinimum(0.6f, YAxis.AxisDependency.LEFT);
                            Description desc_glyc = new Description();
                            desc_glyc.setText("Glycol");
                            desc_glyc.setTextSize(12f);
                            chart_glycol.setDescription(desc_glyc);
                            Legend legend_glycol = chart_glycol.getLegend();
                            legend_glycol.setTextSize(12f);
                            legend_glycol.setXEntrySpace(16f);
                            chart_glycol.getAxisLeft().setTextSize(12f);
                            chart_glycol.getAxisRight().setTextSize(12f);


                            // set chart settings for solarTank chart
                            chart_solar_tank.setVisibleXRangeMinimum(240);
                            chart_solar_tank.setVisibleYRangeMinimum(0.6f, YAxis.AxisDependency.LEFT);
                            Description desc_tank = new Description();
                            desc_tank.setTextSize(12);
                            desc_tank.setText("Solar Tank");
                            chart_solar_tank.setDescription(desc_tank);
                            Legend legend_solar_tank = chart_solar_tank.getLegend();
                            legend_solar_tank.setTextSize(12);
                            legend_solar_tank.setXEntrySpace(20f);
                            chart_solar_tank.getAxisLeft().setTextSize(12f);
                            chart_solar_tank.getAxisRight().setTextSize(12f);



                            // Refresh (redraw) charts
                            chart_solar_tank.invalidate();
                            chart_glycol.invalidate();


                        }
                    } else {
                        Log.d("DEBUG--", "Task Failed");
                    }
                });
    }

}
