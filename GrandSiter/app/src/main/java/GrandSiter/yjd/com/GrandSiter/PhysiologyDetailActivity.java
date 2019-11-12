package GrandSiter.yjd.com.GrandSiter;

import androidx.core.widget.ContentLoadingProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class PhysiologyDetailActivity extends AppCompatActivity {
    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout linearLayoutBSheet;
    private ToggleButton tbUpDown;
    private RecyclerView recyclerView, phyRecyclerView;
    private RecyclerView.Adapter adapter, phyAdapter;
    private List<DateListItem> dateListItem;
    private List<PhysiologyListItem> physiologyListItems;
    private String date;
    private LoadingDialog loading;
    private Date today;
    private SimpleDateFormat format;
    private int type, finishFlag;
    private String grId;
    private LineChart mLineChart;
    private LineDataSet lineDataSet;
    private ArrayList<Entry> dayCount;
    private ArrayList<String> xAxisList;
    private int max, min;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physiology_detail);
        init();
        onClick();
        loading.makeDialog();
        getData();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        phyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        phyRecyclerView.setAdapter(phyAdapter);
    }
    public void changeData(DateListItem item){
        date = item.getDate(); //선택한 날짜
        new Physiology_GetData().execute(); //선택한 날짜로 데이터를 다시 가져온다.
    }
    //날짜 데이터
    private void getData(){
        new Date_GetData().execute();
        new DayPhysiology_GetData().execute();
        new Physiology_GetData().execute();
    }

    private void init() {
        today = new Date();
        format = new SimpleDateFormat("yyyy-MM");
        this.linearLayoutBSheet = findViewById(R.id.bottomSheet);
        this.bottomSheetBehavior = BottomSheetBehavior.from(linearLayoutBSheet);
        this.tbUpDown = findViewById(R.id.toggleButton);
        this.dateListItem = new ArrayList<>();
        this.recyclerView = (RecyclerView) findViewById(R.id.daterecycler);
        this.phyRecyclerView = (RecyclerView) findViewById(R.id.phyRecycler);
        this.physiologyListItems = new ArrayList<>();
        this.date = format.format(today);
        this.mLineChart = findViewById(R.id.lineChart);
        this.dayCount = new ArrayList<>();
        this.xAxisList = new ArrayList<>();
        this.loading = new LoadingDialog(this, 3);
        Intent intent = getIntent();
        type = intent.getExtras().getInt("type");
        grId = intent.getExtras().getString("grid");
    }
    //대소변 데이터가 존재하는 날짜 데이터 가져오기
    private class Date_GetData extends AsyncTask<String, Void, String> {
        String errorString = null;
        String target;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            try {
                target = "https://sammaru.cbnu.ac.kr/grandsitters/physiologydate.php";
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            try{
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("datejson");

                //DB에서 가져온 노인 정보를 저장
                for(int i=0; i<jsonArray.length(); i++){
                    JSONObject item = jsonArray.getJSONObject(i);
                    dateListItem.add(new DateListItem(item.getString("date")));
                }
                Log.d("phs", Integer.toString(dateListItem.size()));
                adapter = new DateListAdapter(dateListItem, PhysiologyDetailActivity.this);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                loading.finishLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                //URL 설정및 접속
                URL url = new URL(target);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                //전송 모드 설정
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.connect();

                //서버로 전송
                StringBuffer buffer = new StringBuffer();

                //이 부분에 elderno 대입
                buffer.append("elderno").append("=").append(grId).append("&type=").append(type);                 // php 변수에 값 대입

                OutputStreamWriter outStream = new OutputStreamWriter(httpURLConnection.getOutputStream(), "UTF-8");
                PrintWriter writer = new PrintWriter(outStream);
                writer.write(buffer.toString());
                writer.flush();

                int responseStatusCode = httpURLConnection.getResponseCode();
                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }
                bufferedReader.close();
                return sb.toString().trim();
            } catch (Exception e) {

                errorString = e.toString();

                return null;
            }
        }
    }
    //각 월의 대소변 데이
    private class Physiology_GetData extends AsyncTask<String, Void, String> {
        String errorString = null;
        String target;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            try {
                target = "https://sammaru.cbnu.ac.kr/grandsitters/physiology.php";
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            try{
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("phyjson");
                physiologyListItems.clear();
                //DB에서 가져온 노인 정보를 저장
                for(int i=0; i<jsonArray.length(); i++){
                    JSONObject item = jsonArray.getJSONObject(i);
                    Log.d(item.getString("date"), "awd");
                    //헤더와 내용 분류
                    divData(item);
                }
                phyAdapter = new PhsiologyAdapter(physiologyListItems, PhysiologyDetailActivity.this);
                phyRecyclerView.setAdapter(phyAdapter);
                phyAdapter.notifyDataSetChanged();
                loading.finishLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        private void divData(JSONObject item) throws JSONException {
            //헤더
            if(item.getString("date").length() == 10){
                physiologyListItems.add(new PhysiologyListItem(item.getString("date"), 1));
            }
            //시간
            else{
                String str;
                str = item.getString("date").replaceFirst(":", "시 ");
                str = str.replaceFirst(":", "분 ");
                str = str.substring(0, 8);
                physiologyListItems.add(new PhysiologyListItem(str, 2));
            }
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                //URL 설정및 접속
                URL url = new URL(target);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                //전송 모드 설정
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.connect();

                //서버로 전송
                StringBuffer buffer = new StringBuffer();

                //이 부분에 elderno 대입
                buffer.append("elderno").append("=").append(grId).append("&type=").append(type).append("&date=").append(date);                 // php 변수에 값 대입

                OutputStreamWriter outStream = new OutputStreamWriter(httpURLConnection.getOutputStream(), "UTF-8");
                PrintWriter writer = new PrintWriter(outStream);
                writer.write(buffer.toString());
                writer.flush();

                int responseStatusCode = httpURLConnection.getResponseCode();
                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }
                bufferedReader.close();
                return sb.toString().trim();
            } catch (Exception e) {

                errorString = e.toString();

                return null;
            }
        }
    }

    private class DayPhysiology_GetData extends AsyncTask<String, Void, String> {
        String errorString = null;
        String target;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            try {
                target = "https://sammaru.cbnu.ac.kr/grandsitters/dayphysiologydata.php";
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            try{
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("dayphyjson");
                for(int i=0; i<jsonArray.length(); i++){
                    JSONObject item = jsonArray.getJSONObject(i);
                    Log.d("num : ", item.getString("num"));
                    //dayCount.add(new Entry(i, Integer.parseInt(item.getString("num"))));
                    //xAxisList.add(item.getString("date").substring(8).concat("일"));
                    Log.d("sef", item.getString("num") + " " + item.getString("date"));
                }
                for (int i = 0; i < 12; i++){
                    dayCount.add(new Entry(i, new Random().nextInt(5)));
                }
                for (int i = 0; i < dayCount.size(); i++) {
                    xAxisList.add(String.valueOf(i + 1).concat("일"));
                }
                initLineChart();
                loading.finishLoading();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                //URL 설정및 접속
                URL url = new URL(target);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                //전송 모드 설정
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.connect();

                //서버로 전송
                StringBuffer buffer = new StringBuffer();

                //이 부분에 elderno 대입
                buffer.append("elderno").append("=").append(grId).append("&type=").append(type).append("&date=").append(date);                 // php 변수에 값 대입

                OutputStreamWriter outStream = new OutputStreamWriter(httpURLConnection.getOutputStream(), "UTF-8");
                PrintWriter writer = new PrintWriter(outStream);
                writer.write(buffer.toString());
                writer.flush();

                int responseStatusCode = httpURLConnection.getResponseCode();
                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }
                bufferedReader.close();
                return sb.toString().trim();
            } catch (Exception e) {

                errorString = e.toString();

                return null;
            }
        }
    }

    private void onClick(){
        tbUpDown.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }else{
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View view, int newState) {
                if(newState == BottomSheetBehavior.STATE_EXPANDED){
                    tbUpDown.setChecked(true);
                }else if(newState == BottomSheetBehavior.STATE_COLLAPSED){
                    tbUpDown.setChecked(false);
                }
            }
            @Override
            public void onSlide(View view, float v) { }
        });
    }
    //클릭시 BottomSheet 내려
    public void changeBottomSheet(){
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }


    private void initLineChart() {
        setXAxis();
        setYAxis();
        setExtra();
        setData(dayCount);
    }
    private void setExtra() {
        Legend legend = mLineChart.getLegend();
        legend.setForm(Legend.LegendForm.NONE);
        legend.setTextColor(Color.WHITE);
        Description description = new Description();
        description.setEnabled(false);
        mLineChart.setDescription(description);
        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
        mv.setChartView(mLineChart);
        mLineChart.setMarker(mv);
        mLineChart.setMaxHighlightDistance(30);
    }
    private void setYAxis() {
        //设置left Y轴线
        YAxis axisLeft = mLineChart.getAxisLeft();
        axisLeft.setDrawGridLines(true);
        axisLeft.setDrawAxisLine(false);//去掉左边线
        axisLeft.setLabelCount(max);
        axisLeft.setMaxWidth(max+1);
        axisLeft.setMinWidth(0);
        YAxis axisRight = mLineChart.getAxisRight();
        axisRight.setEnabled(false); //是否显示右轴线
        axisRight.setDrawAxisLine(false); //去掉右边线

        mLineChart.getDescription().setEnabled(false); //设置图标右下方的描述内容
        mLineChart.setDragEnabled(false); //设置是否可以拖拽
        mLineChart.setScaleEnabled(false); //设置是否可以缩放
    }
    private void setXAxis() {
        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        mLineChart.setDrawGridBackground(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisList));
    }
    private void setData(ArrayList<Entry> values) {
        if (mLineChart.getData() != null && mLineChart.getData().getDataSetCount() > 0) {
            lineDataSet = (LineDataSet) mLineChart.getData().getDataSetByIndex(0);
            lineDataSet.setValues(values);
            mLineChart.getData().notifyDataChanged();
            mLineChart.notifyDataSetChanged();
        } else {
            lineDataSet = new LineDataSet(values, "awd");
            lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER); //设置折线的展示方式
            lineDataSet.setColor(Color.parseColor("#456789"));//设置折线的颜色
            lineDataSet.setLineWidth(2f);
            lineDataSet.setDrawCircles(false);
            lineDataSet.setDrawFilled(true);
            lineDataSet.setFillColor(Color.parseColor("#456789"));
            lineDataSet.setDrawValues(false); //设置是否显示折线点的数字
            lineDataSet.setValueTextSize(9);
            lineDataSet.setFormLineWidth(11);
            lineDataSet.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            lineDataSet.setFormSize(15);
            lineDataSet.setHighLightColor(Color.parseColor("#FFB57FFF"));
            lineDataSet.setHighlightLineWidth(1);
            lineDataSet.setHighlightEnabled(true);
            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(lineDataSet);
            LineData data = new LineData(dataSets);
            mLineChart.setData(data);
            mLineChart.animateXY(1000, 1000);
            mLineChart.invalidate();
        }
    }
}
