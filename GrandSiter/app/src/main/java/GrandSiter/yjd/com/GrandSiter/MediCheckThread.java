package GrandSiter.yjd.com.GrandSiter;

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

public class MediCheckThread extends Thread{
    Handler handler;
    int response;
    StringBuilder listItem;

    boolean isRun = true;
    public MediCheckThread(Handler handler){
        this.handler = handler;

    }
    public void run(){
        while(isRun){
            try{

                new MediCheckThread.MediCheck().execute();
                //Log.d("mediflag : ", Integer.toString(response));
                if(response == 1){
                    Message msg = handler.obtainMessage();
                    msg.what = 1;
                    msg.obj = listItem;

                    handler.sendMessage(msg);    //Notification 생성하는 서비스

                    Thread.sleep(60000);
                    response=0;
                }
                response=0;
                Thread.sleep(5000); //초씩 쉰다.
            }catch (Exception e) {}
        }
    }
    private class MediCheck extends AsyncTask<String, Void, String> {
        String errorString = null;
        String target;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            try {
                //target = "http://175.212.26.202:3389/medicheck.php";
                target = "http://192.168.0.21/medicheck.php";

            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            try{
                //Log.d("awdawdwa", result);
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("medicheck");
                JSONObject jsonflag = jsonArray.getJSONObject(0);
                listItem = new StringBuilder("");
                response = jsonflag.getInt("flag");
                //Log.d("medijsonflag : ", Integer.toString(jsonflag.getInt("flag")));
                if(response == 1) {
                    JSONObject item;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        item = jsonArray.getJSONObject(i);
                        listItem.append(item.getString("elname") + "님 " + item.getString("mename") + " 복용하실 시간입니다.\n");

                    }
                    item = jsonArray.getJSONObject(0);
                    listItem.append("시간 : " + item.getString("time")+"\n");
                    //Log.d("msgobj", listItem.toString());

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

                //이 부분에 elderno 대입
                //buffer.append("elderno").append("=").append(grId);                 // php 변수에 값 대입

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
