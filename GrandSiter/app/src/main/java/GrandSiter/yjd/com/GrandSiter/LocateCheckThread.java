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

public class LocateCheckThread extends Thread{
    private Handler handler;
    private boolean isRun = true;
    private int response;
    private StringBuilder listItem;
    private String restr;
    private String userID;

    public LocateCheckThread(Handler handler, String userID){
        this.handler = handler;
        response = 0;
        this.userID = userID;
    }

    public void run(){
        while(isRun){
            try{
                new LocateCheck().execute();
                if(response == 1){  //대변 확정
                    Message msg = handler.obtainMessage();
                    msg.what = 2;
                    msg.obj = listItem;
                    //handler.sendMessage(msg);    //Notification 생성하는 서비스
                    response=0;
                }

                Thread.sleep(5000); //라즈베리에서 더빨리 데이터를 넣어주게 코딩 해야함.
            }catch (Exception e) {}
        }
    }
    public void end(){
        isRun = false;
    }
    private class LocateCheck extends AsyncTask<String, Void, String> {
        String errorString = null;
        String target;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            try {
                target = "https://sammaru.cbnu.ac.kr/grandsitters/locatecheck.php";
                //target = "http://192.168.0.21/locatecheck.php";

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
                JSONArray jsonArray = jsonObject.getJSONArray("locatecheck");
                JSONObject jsonflag = jsonArray.getJSONObject(0);
                listItem = new StringBuilder("");
                restr = jsonflag.getString("name");
                //Log.d("nono : ", restr);
                if(!restr.equals("no")){
                    response = 1;
                    JSONObject item;
                    for(int i=0; i<jsonArray.length(); i++){
                        item = jsonArray.getJSONObject(i);
                        listItem.append(item.getString("name")+"님 위치 확인해주시기 바랍니다.\n");
                    }
                }
                Log.d("sensorflag : ", Integer.toString(response));
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
                //Log.d("locUserId : ", userID);
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
}
