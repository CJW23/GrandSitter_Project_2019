package GrandSiter.yjd.com.GrandSiter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

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
import java.util.ArrayList;
import java.util.List;

import GrandSiter.yjd.com.GrandSiter.R;

public class GrandStatusActivity extends AppCompatActivity implements OnChartValueSelectedListener{

    private LineChart mChart;
    private int loadingFlag;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView2;
    private RecyclerView.Adapter adapter2;
    private List<ScheduleListItem> listItem;
    private List<MediListItem> mediListItem;
    private ProgressDialog dialog;
    private Button scheAdd;
    private Button mediAdd;
    private ArrayList<GraphDataItem> graphDataItems;
    private ArrayList<GraphDataItem> graphDataItems1;
    String grName, grGender, grAge, grCh, grId;
    TextView elder_name;

    @Override
    protected void onResume(){
        super.onResume();

        loadingFlag = 0;    //loading 창 flag;
        setContentView(R.layout.activity_grand_status);
        elder_name = (TextView)findViewById(R.id.elder_name);
        scheAdd = (Button)findViewById(R.id.scheAdd);
        mediAdd = (Button)findViewById(R.id.mediAdd);

        makeDialog();   //다이얼로그 생성
        getUserData();  //유저 데이터 가져오기
        setRecycleView();   //리사이클 뷰 설정
        buttonListen();
        new Medi_GetData().execute();
        new Schedule_GetData().execute();
        new FecesGetData().execute();
        new PeeGetData().execute();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    void buttonListen(){
        scheAdd.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent addScheule = new Intent(GrandStatusActivity.this, AddScheduleActivity.class);
                addScheule.putExtra("id", grId);
                GrandStatusActivity.this.startActivity(addScheule);
            }
        });
        mediAdd.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent addMedi = new Intent(GrandStatusActivity.this, AddMediActivity.class);
                addMedi.putExtra("id", grId);
                GrandStatusActivity.this.startActivity(addMedi);
            }
        });
    }

    void setRecycleView(){
        adapter = new ScheduleListAdapter(listItem, this);
        adapter2 = new MediListAdapter(mediListItem, this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        recyclerView = (RecyclerView) findViewById(R.id.sc_recycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);


        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView2 = (RecyclerView) findViewById(R.id.medi_recycle);
        recyclerView2.setHasFixedSize(true);
        recyclerView2.setLayoutManager(layoutManager);

        listItem = new ArrayList<>();
        mediListItem = new ArrayList<>();
        graphDataItems = new ArrayList<>();
        graphDataItems1 = new ArrayList<>();
    }
    //이전 액티비티에서 데이터 가져옴
    void getUserData(){
        Intent intent = getIntent();
        grId = intent.getExtras().getString("id");
        grName = intent.getExtras().getString("name");
        grGender = intent.getExtras().getString("gender");
        grAge = intent.getExtras().getString("age");
        grCh = intent.getExtras().getString("ch");  //질환
        Log.d("grId : ", grId);
        //Log.d("name : ", grName);
    }

    //다이얼로그 출력
    void makeDialog(){
        dialog = new ProgressDialog(GrandStatusActivity.this);
        dialog.setMax(100);
        dialog.setMessage("정보");
        dialog.setTitle("로딩 중");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    while(loadingFlag >= 0){
                        if(loadingFlag >=2 ){
                            dialog.dismiss();
                            break;
                        }
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //그래프 그리기
    void makeChart(){
        PieChart pieChart = (PieChart) findViewById(R.id.piechart);
        PieChart pieChart1 = (PieChart)findViewById(R.id.piechart1);
        pieChart.setUsePercentValues(true);
        pieChart1.setUsePercentValues(true);

        List<PieEntry> yvalues = new ArrayList<PieEntry>();
        List<PieEntry> yvalues1 = new ArrayList<PieEntry>();
        for(int i=0; i<graphDataItems.size(); i++){
            Log.d("awdawd : ", graphDataItems.get(i).getDate());
            yvalues.add(new PieEntry(graphDataItems.get(i).getNum(), graphDataItems.get(i).getDate()));
        }
        for(int i=0; i<graphDataItems1.size(); i++){
            Log.d("awdawd : ", graphDataItems1.get(i).getDate());
            yvalues1.add(new PieEntry(graphDataItems1.get(i).getNum(), graphDataItems1.get(i).getDate()));
        }

        PieDataSet dataSet = new PieDataSet(yvalues, "");
        PieData data = new PieData(dataSet);
        PieDataSet dataSet1 = new PieDataSet(yvalues1, "");
        PieData data1 = new PieData(dataSet1);
        // In Percentage term
        data.setValueFormatter(new PercentFormatter());
        data1.setValueFormatter(new PercentFormatter());
        // Default value
        //data.setValueFormatter(new DefaultAxisValueFormatter(0));
        pieChart.setData(data);
        pieChart.setCenterText("5일간 대변 비율");
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setTransparentCircleRadius(25f);
        pieChart.setHoleRadius(40f);

        pieChart1.setData(data1);
        pieChart1.setCenterText("5일간 소변 비율");
        pieChart1.getDescription().setEnabled(false);
        pieChart1.setDrawHoleEnabled(true);
        pieChart1.setTransparentCircleRadius(25f);
        pieChart1.setHoleRadius(40f);

        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        dataSet1.setColors(ColorTemplate.VORDIPLOM_COLORS);
        data.setValueTextSize(13f);
        data.setValueTextColor(Color.DKGRAY);
        data1.setValueTextSize(13f);
        data1.setValueTextColor(Color.DKGRAY);

        pieChart.setOnChartValueSelectedListener(this);
        pieChart1.setOnChartValueSelectedListener(this);

        pieChart.animateXY(1400, 1400);
        pieChart1.animateXY(1400, 1400);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    private class Schedule_GetData extends AsyncTask<String, Void, String> {
        String errorString = null;
        String target;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            try {
               //target = "http://175.212.26.202:3389/schedule.php";
                target = "http://192.168.0.21/schedule.php";

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
                JSONArray jsonArray = jsonObject.getJSONArray("scjson");

                //DB에서 가져온 노인 정보를 저장
                for(int i=0; i<jsonArray.length(); i++){
                    JSONObject item = jsonArray.getJSONObject(i);
                    listItem.add(new ScheduleListItem("제목 : " + item.getString("name"), "일정 : " + item.getString("time"),
                            "내용 : " + item.getString("des"), item.getString("id")));
                }

                adapter = new ScheduleListAdapter(listItem, GrandStatusActivity.this);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                loadingFlag++;

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
                buffer.append("elderno").append("=").append(grId);                 // php 변수에 값 대입

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
    private class Medi_GetData extends AsyncTask<String, Void, String> {
        String errorString = null;
        String target;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            try {
                //target = "http://175.212.26.202:3389/medicine.php";
                target = "http://192.168.0.21/medicine.php";

            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            try{
                Log.d("status : ", result);
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("medijson");

                //DB에서 가져온 노인 정보를 저장
                for(int i=0; i<jsonArray.length(); i++){
                    JSONObject item = jsonArray.getJSONObject(i);
                    mediListItem.add(new MediListItem("약 이름 : " + item.getString("name"), "복용 시간 : " + item.getString("time"), "설명 : " + item.getString("des"),item.getString("id")));
                }
                adapter2 = new MediListAdapter(mediListItem, GrandStatusActivity.this);
                recyclerView2.setAdapter(adapter2);
                adapter2.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            loadingFlag++;
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
                buffer.append("elderno").append("=").append(grId);                 // php 변수에 값 대입

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
    private class FecesGetData extends AsyncTask<String, Void, String> {
        String errorString = null;
        String target;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            try {
                //target = "http://175.212.26.202:3389/fecesdata.php";
                target = "http://192.168.0.21/fecesdata.php";

            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            try{
                Log.d("status : ", result);
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("graphdate");

                //DB에서 가져온 노인 정보를 저장
                for(int i=0; i<jsonArray.length(); i++){
                    JSONObject item = jsonArray.getJSONObject(i);
                    graphDataItems.add(new GraphDataItem(item.getString("date").substring(5), item.getInt("num")));
                }
                makeChart();
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
                buffer.append("grandID").append("=").append(grId);                 // php 변수에 값 대입

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
    private class PeeGetData extends AsyncTask<String, Void, String> {
        String errorString = null;
        String target;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            try {
                //target = "http://175.212.26.202:3389/peedata.php";
                target = "http://192.168.0.21/peedata.php";

            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            try{
                Log.d("status : ", result);
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("graphdate");

                //DB에서 가져온 노인 정보를 저장
                for(int i=0; i<jsonArray.length(); i++){
                    JSONObject item = jsonArray.getJSONObject(i);
                    graphDataItems1.add(new GraphDataItem(item.getString("date").substring(5), item.getInt("num")));
                }
                makeChart();
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
                buffer.append("grandID").append("=").append(grId);                 // php 변수에 값 대입

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
