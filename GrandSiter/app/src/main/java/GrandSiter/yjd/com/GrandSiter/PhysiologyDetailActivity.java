package GrandSiter.yjd.com.GrandSiter;

import androidx.core.widget.ContentLoadingProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
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
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
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

public class PhysiologyDetailActivity extends AppCompatActivity {
    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout linearLayoutBSheet;
    private ToggleButton tbUpDown;
    private RecyclerView recyclerView, phyRecyclerView;
    private RecyclerView.Adapter adapter, phyAdapter;
    private List<DateListItem> dateListItem;
    private List<PhysiologyListItem> physiologyListItems;
    private String date;
    private Date today;
    private SimpleDateFormat format;
    private int type;
    private String grId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physiology_detail);
        init();
        onClick();
        getData();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        phyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        phyRecyclerView.setAdapter(phyAdapter);
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
        Intent intent = getIntent();
        type = intent.getExtras().getInt("type");
        grId = intent.getExtras().getString("grid");
    }
    //날짜 데이터
    private void getData(){
        new Date_GetData().execute();
        new Physiology_GetData().execute();
    }
    //클릭시 BottomSheet 내려
    public void changeBottomSheet(){
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
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
    public void changeData(DateListItem item){
        date = item.getDate(); //선택한 날짜
        new Physiology_GetData().execute(); //선택한 날짜로 데이터를 다시 가져온다.
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
}
