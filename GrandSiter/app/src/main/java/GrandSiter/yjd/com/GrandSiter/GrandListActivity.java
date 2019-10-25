package GrandSiter.yjd.com.GrandSiter;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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

public class GrandListActivity extends AppCompatActivity {

    private String userID;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<GrandListItem> listItem;
    Toolbar myToolbar;

    @Override
    protected void onResume(){
        super.onResume();
        setContentView(R.layout.activity_grandlist);

        //MainActivity에서 가져온 유저 정보 저장(추후 자동 로그인 하면 shared를 사용하여 정보 가져와야함)
        getUserData();

        //툴바 설정
        myToolbar = (Toolbar)findViewById(R.id.addtoolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("");

        //어댑터를 통해 노인 리스트 출력.
        adapter = new GrandListAdapter(listItem, this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listItem = new ArrayList<>();

        new GetData().execute();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onBackPressed() {       //뒤로가기 버튼 누를시 종료 여부
        final AlertDialog.Builder alertdialog = new AlertDialog.Builder(this);
        alertdialog.setMessage("종료하시겠습니까?").setCancelable(false).
                setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finishAffinity();

                        //Service 종료
                        Intent intent = new Intent(
                                getApplicationContext(),
                                NotiService.class);
                        stopService(intent);        //onDestroy 호출
                    }
                }).
                setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alert = alertdialog.create();
        alert.setTitle("종료");
        alert.show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        //return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.add:
                Intent addGrand = new Intent(GrandListActivity.this, AddGrandActivity.class);
                addGrand.putExtra("userID", userID);
                GrandListActivity.this.startActivity(addGrand);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);

        return true;
    }
    private class GetData extends AsyncTask<String, Void, String>{
        String errorString = null;
        String target;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            try {
                target = "https://sammaru.cbnu.ac.kr/grandsitters/grantlist.php";
                //target = "http://192.168.0.21/grantlist.php";
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
                JSONArray jsonArray = jsonObject.getJSONArray("webnautes");

                //DB에서 가져온 노인 정보를 저장
                for(int i=0; i<jsonArray.length(); i++){
                    JSONObject item = jsonArray.getJSONObject(i);
                    listItem.add(new GrandListItem(item.getString("id"), item.getString("name"), item.getString("age"),
                            item.getString("gender"), item.getString("characteristic")));
                }

                adapter = new GrandListAdapter(listItem, GrandListActivity.this);
                recyclerView.setAdapter(adapter);
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
                buffer.append("userID").append("=").append(userID);                 // php 변수에 값 대입

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

    void getUserData(){
        Intent intent = getIntent();
        userID = intent.getExtras().getString("userID");
    }
}
