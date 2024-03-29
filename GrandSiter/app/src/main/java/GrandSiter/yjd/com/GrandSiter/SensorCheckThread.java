package GrandSiter.yjd.com.GrandSiter;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

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

public class SensorCheckThread extends Thread {
    private Handler handler;
    private boolean isRun = true;
    private int response;
    private String name;
    private String notiText;

    public SensorCheckThread(Handler handler){
        this.handler = handler;
        response = 0;
    }
    public void run(){
        while(isRun){
            try{
                new SensorCheck().execute();
                if(response == 1){  //대변 확정
                    Message msg = handler.obtainMessage();
                    msg.what = 0;
                    msg.obj = notiText;     //알림 메세지 내
                    handler.sendMessage(msg);    //Notification 생성하는 서비스
                    response=0;             //대소벼 데이터 알림을 보냈으므로 응답 복귀.
                }
                Thread.sleep(5000); //초씩 쉰다.
            }catch (Exception e) {}
        }
    }
    public void end(){
        isRun = false;
    }
    //모든 환자의 대소변 상태 체크 -> elderno를 쓰지 않음.
    private class SensorCheck extends AsyncTask<String, Void, String> {
        String errorString = null;
        String target;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            try {
                //대소변 데이터가 있는지 체크하는 php
                target = "https://sammaru.cbnu.ac.kr/grandsitters/sensorcheck.php";
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
                response = jsonObject.getInt("flag");   //flag는 0아니면 1값이 온다.
                name = jsonObject.getString("name");
                if(response == 1) {  //flag=1이면 대소변 판단.
                    //알림에 쓸 내
                    notiText = name + " 환자 기저귀 교체해주시기 바랍니다.";
                }
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
