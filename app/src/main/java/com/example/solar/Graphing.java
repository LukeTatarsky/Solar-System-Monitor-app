package com.example.solar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
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
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

public class Graphing extends AppCompatActivity {
    private static final String TAG = "Graphing";
    private static final String KEY_HOURS = "graph_hours";
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String KEY_SHOW_WEEKS = "show_weeks";
    public static final String KEY_SHOW_BOILER_OUT = "show_boiler";
    public static final String KEY_SHOW_BOILER_MID = "show_boiler_mid";
    public static final String KEY_AVG_MAX_MIN = "avg_max_min";
    private SharedPreferences prefs_shared;
    AtomicReference<LocalDateTime> currentHourUTC;
    private LineChart chart_solar_tank;
    private LineChart chart_glycol;
    private List<SolarData> solarDataArray;
    private TextView txtDataGlycol;
    private TextView txtDataST;
    private List<Entry> entries_high;
    private List<Entry> entries_mid;
    private List<Entry> entries_low;
    private List<Entry> entries_boiler;
    private List<Entry> entries_boiler_out;
    private List<Entry> entries_glycol_roof;
    private List<Entry> entries_glycol_in;
    private List<Entry> entries_glycol_out_st;
    private List<Entry> entries_glycol_out_he;
    private MyXAxisFormatter xAxisTimeFormatter;
    // 0 for avg, 1 for max, 2 for min
    private int selected_avg_max_min;

    int prefs_Hours;
    boolean showingWeeks;
    boolean showingBoilerOut;
    boolean showingBoilerMid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Log.d(TAG, "on create");
        setContentView(R.layout.activity_graphing);
        chart_solar_tank = findViewById(R.id.lineChartSolarTank);
        chart_glycol = findViewById(R.id.lineChartGlycol);
        Button btn_settings = findViewById(R.id.btn_settings);
        Button btn_reset_zoom = findViewById(R.id.btn_reset_zoom);
        txtDataGlycol = findViewById(R.id.txtDataPointGlycol);
        txtDataST = findViewById(R.id.txtDataPointST);
        Button btn_back = findViewById(R.id.btn_back);
        // get saved hours value
        prefs_shared = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        prefs_Hours = prefs_shared.getInt(KEY_HOURS, 1);
        showingWeeks = prefs_shared.getBoolean(KEY_SHOW_WEEKS, false);
        showingBoilerOut = prefs_shared.getBoolean(KEY_SHOW_BOILER_OUT, false);
        showingBoilerMid = prefs_shared.getBoolean(KEY_SHOW_BOILER_MID, true);
        selected_avg_max_min = prefs_shared.getInt(KEY_AVG_MAX_MIN, 0);
        /*
        After you have an instance of your chart, you can create data and add it to the chart.
         This example uses the LineChart, for which the Entry class represents a single entry
         in the chart with x- and y-coordinate. Other chart types, such as BarChart use other
         classes (e.g. BarEntry) for that purpose.
         */
        // to hold all the readings
        solarDataArray = new ArrayList<>();
        // one chart
        entries_high = new ArrayList<>();
        entries_mid = new ArrayList<>();
        entries_low = new ArrayList<>();
        entries_boiler = new ArrayList<>();
        entries_boiler_out = new ArrayList<>();
        // second chart
        entries_glycol_roof = new ArrayList<>();
        entries_glycol_in = new ArrayList<>();
        entries_glycol_out_st = new ArrayList<>();
        entries_glycol_out_he = new ArrayList<>();

        currentHourUTC = new AtomicReference<>(LocalDateTime.now(ZoneId.of("UTC"))
                .withMinute(0)
                .withSecond(0)
                .withNano(0));

        // either show hourly chart or week chart

        if (showingWeeks){
            showingBoilerOut = false;
            getTempsWeeks(currentHourUTC.get());
        }else {
            if (!showingBoilerMid){
                showingBoilerOut = false;
            }
            getTempsHours(currentHourUTC.get());
        }


        btn_settings.setOnClickListener(view -> showSettings());
        btn_reset_zoom.setOnClickListener(view -> {
            chart_glycol.fitScreen();
            chart_solar_tank.fitScreen();
        });
        btn_back.setOnClickListener(view -> finish());
        // setup a listener to clearly display the selected time and value
        chart_solar_tank.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                float yValue = e.getY();
                String xVal = xAxisTimeFormatter.StringFromFloatHighlighter(e.getX(), showingWeeks);
                LineDataSet dataSet = (LineDataSet) chart_solar_tank.getData().getDataSetByIndex(h.getDataSetIndex());
                txtDataST.setVisibility(View.VISIBLE);
                txtDataST.setText(dataSet.getLabel()+ "  @  " + xVal + "  =  " + yValue + "  C");
            }
            @Override
            public void onNothingSelected() {
                txtDataST.setVisibility(View.INVISIBLE);
            }
        });
        // setup a listener to clearly display the selected time and value
        chart_glycol.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                float yValue = e.getY();
                String xVal = xAxisTimeFormatter.StringFromFloatHighlighter(e.getX(), showingWeeks);
                LineDataSet dataSet = (LineDataSet) chart_glycol.getData().getDataSetByIndex(h.getDataSetIndex());
                txtDataGlycol.setVisibility(View.VISIBLE);
                txtDataGlycol.setText(dataSet.getLabel()+ "  @  " + xVal + "  =  " + yValue + "  C");
            }
            @Override
            public void onNothingSelected() {
                txtDataGlycol.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        Log.d(TAG, "on restart");
        prefs_Hours = prefs_shared.getInt(KEY_HOURS, 1);
        selected_avg_max_min = prefs_shared.getInt(KEY_AVG_MAX_MIN, 0);
        showingWeeks = prefs_shared.getBoolean(KEY_SHOW_WEEKS, false);
        showingBoilerMid = prefs_shared.getBoolean(KEY_SHOW_BOILER_MID, true);
        if (showingWeeks)
            showingBoilerOut = false;
        else
            showingBoilerOut = prefs_shared.getBoolean(KEY_SHOW_BOILER_OUT, false);

        updateCharts();
    }

    private void updateCharts() {
        solarDataArray.clear();
        entries_high.clear();
        entries_mid.clear();
        entries_low.clear();
        entries_boiler.clear();
        entries_boiler_out.clear();
        entries_glycol_roof.clear();
        entries_glycol_in.clear();
        entries_glycol_out_st.clear();
        entries_glycol_out_he.clear();

        if (showingWeeks) {
            getTempsWeeks(currentHourUTC.get());
        } else {
            getTempsHours(currentHourUTC.get());
        }
    }

    private void showSettings() {
        Intent intent = new Intent(this, Settings.class);
        // intent.putExtra("solarData", "value");
        startActivity(intent);
    }

    private LineDataSet setupLineDataSet(List<Entry> entries, String name, int color){
        LineDataSet dataSet = new LineDataSet(entries, name);
        dataSet.setColor(ContextCompat.getColor(getApplicationContext(), color));
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);
        dataSet.setLineWidth(3f);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setCubicIntensity(0.2f);
        return dataSet;
    }

    /** @noinspection SameParameterValue, SameParameterValue, SameParameterValue */
    private void setChartFormat(LineChart chart, String description, float desc_text_size, float legend_text_size, float axis_text_size, float legend_spacing){
        chart.setVisibleXRangeMinimum(600); // seconds
        chart.setVisibleYRangeMinimum(1.1f, YAxis.AxisDependency.LEFT);
        Description desc_glyc = new Description();
        desc_glyc.setText(description);
        desc_glyc.setTextSize(desc_text_size);
        chart.setDescription(desc_glyc);
        Legend legend_glycol = chart.getLegend();
        legend_glycol.setTextSize(legend_text_size);
        legend_glycol.setXEntrySpace(legend_spacing);
        chart.getAxisLeft().setTextSize(axis_text_size);
        chart.getAxisRight().setTextSize(axis_text_size);
        // Set up the x-axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setTextSize(axis_text_size);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxisTimeFormatter = new MyXAxisFormatter(showingWeeks);
        xAxis.setValueFormatter(xAxisTimeFormatter);
    }

    private void getTempsWeeks(LocalDateTime currentHourUTC){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collRef = db.collection("weeks");
        String fieldNameWeek = "_week_number";
        String fieldNameYear = "_year";
        int weekOfYear = currentHourUTC.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()) - 1 ; // -1 to show atleast one week
        int year = currentHourUTC.getYear();
//        Log.d(TAG, "solarReading creation failed " + weekOfYear + " " + year);
        collRef.whereEqualTo(fieldNameYear, year)
                .whereGreaterThanOrEqualTo(fieldNameWeek, weekOfYear)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
//                        Log.d(TAG, "solarReading creation failed " +querySnapshot.getDocuments().size());
                        if (querySnapshot != null) {

                            for (QueryDocumentSnapshot document : querySnapshot) {
                                List<String> lines = (List<String>) document.get("lines");

                                assert lines != null;
                                SolarData solarReading;
                                for (String line : lines) {
                                    String[] lineReading = line.split(",");
                                    // creating data object. try/catch to stop parse error on "None"
                                    // convert each line (String) into floats and date
                                    try {

                                        solarReading = new SolarData(lineReading, selected_avg_max_min);
                                        solarDataArray.add(solarReading);
//                                        Log.d(TAG, solarReading.toString());
                                    } catch (Exception e) {
                                        // this needs work
//                                        Log.d(TAG, "solarReading creation failed");
                                    }
                                }
                            }
                        }
                        // Setup the charts once data had been processed
                        setupCharts();
                    } else {
//                        Log.d(TAG, "Task Failed");
                    }
                });
    }
    private void getTempsHours(LocalDateTime currentHourUTC){

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
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                List<String> lines = (List<String>) document.get("lines");
                                assert lines != null;
                                int i = 0;
                                for (String line : lines) {
                                    String[] lineReading = line.split(",");

                                    // creating data object. try/catch to stop parse error on "None"
                                    // convert each line (String) into floats and date
                                    SolarData solarReading;
                                    try {
                                        solarReading = new SolarData(lineReading);
                                        solarDataArray.add(solarReading);
                                    }catch(Exception e){
                                        int k = 0;
                                        // find the position of the "None"
                                        for (String reading : lineReading){
                                            if (reading.equals("None")){
                                                // take the previous value if it exists. else set to 0.
                                                if (i > 0){
                                                    lineReading[k] = String.valueOf(solarDataArray.get(solarDataArray.size()-1).getTempByIndex(k));
                                                }else {
                                                    lineReading[k] = "0";
                                                }
                                            }
                                            k ++;
                                        }
                                        solarReading = new SolarData(lineReading);
                                        solarDataArray.add(solarReading);
                                    }
                                    i++;
                                }
                            }
                        }
                        // Setup the charts once data had been processed
                        setupCharts();
                    } else {
//                        Log.d(TAG, "Task Failed");
                    }
                });
    }
    private void setupCharts() {
        for (SolarData solarReading : solarDataArray) {
            // remove 50 years before conversion to keep precision.
            long seconds = solarReading.getDate().minusYears(50).toEpochSecond(ZoneOffset.UTC);
            entries_high.add(new Entry(seconds, solarReading.getSolarTankHigh()));
            entries_mid.add(new Entry(seconds, solarReading.getSolarTankMid()));
            entries_low.add(new Entry(seconds, solarReading.getSolarTankLow()));

            if (showingBoilerOut)
                entries_boiler_out.add(new Entry(seconds, solarReading.getBoilerTankOut()));
            if (showingBoilerMid)
                entries_boiler.add(new Entry(seconds, solarReading.getBoilerTankMid()));

            entries_glycol_roof.add(new Entry(seconds, solarReading.getGlycolRoof()));
            entries_glycol_in.add(new Entry(seconds, solarReading.getGlycolIn()));
            entries_glycol_out_st.add(new Entry(seconds, solarReading.getGlycolOutTank()));
            entries_glycol_out_he.add(new Entry(seconds, solarReading.getGlycolOutHE()));
        }

        // Create a LineDataSet with the entries
        LineDataSet dataSet_high = setupLineDataSet(entries_high, "High", R.color.red);
        LineDataSet dataSet_mid = setupLineDataSet(entries_mid, "Mid", R.color.orange);
        LineDataSet dataSet_low = setupLineDataSet(entries_low, "Low", R.color.green);
        LineDataSet dataSet_boiler = setupLineDataSet(entries_boiler, "Boiler Mid", R.color.purple);
        LineDataSet dataSet_boiler_out = setupLineDataSet(entries_boiler_out, "Boiler Out", R.color.purple_light);

        LineDataSet dataSet_glycol_roof = setupLineDataSet(entries_glycol_roof, "In Roof", R.color.red);
        LineDataSet dataSet_glycol_in = setupLineDataSet(entries_glycol_in, "In", R.color.orange);
        LineDataSet dataSet_glycol_out_tank = setupLineDataSet(entries_glycol_out_st, "Out Tank", R.color.green);
        LineDataSet dataSet_glycol_out_he = setupLineDataSet(entries_glycol_out_he, "Out HE", R.color.blue);

        // Create LineData object and set to LineChart
        LineData lineData_glycol = new LineData(dataSet_glycol_out_he, dataSet_glycol_out_tank, dataSet_glycol_roof, dataSet_glycol_in);
        chart_glycol.setData(lineData_glycol);

        LineData lineData = new LineData(dataSet_high, dataSet_mid, dataSet_low);
        if (showingBoilerOut){
            lineData.addDataSet(dataSet_boiler_out);
        }
        if (showingBoilerMid){
            lineData.addDataSet(dataSet_boiler);
        }
        chart_solar_tank.setData(lineData);

        // set chart settings for glycol chart
        setChartFormat(chart_glycol, "Glycol", 15, 14, 14, 28);
        setChartFormat(chart_solar_tank, "Tank", 15, 14, 14, 24);

        // Refresh (redraw) charts
        chart_solar_tank.invalidate();
        chart_glycol.invalidate();
    }
}