package GrandSiter.yjd.com.GrandSiter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private AlertDialog dialog;
    private String userID;
    private String userPassword;
    private String userName;
    private String userEmail;
    public SharedPreferences settings;
    private Boolean loginChecked;
    EditText idText;
    EditText passwordText;
    Button alpha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Intent intent = new Intent(MainActivity.this, PhysiologyDetailActivity.class); //mainactivity로 넘어가기 전에 Intent에 넣음
        //MainActivity.this.startActivity(intent);

        alpha = (Button)findViewById(R.id.login);
        alpha.getBackground().setAlpha(80);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); //키보드 화면가리기방지

        final TextView registerButton = (TextView)findViewById(R.id.textView2);
        final Button loginButton=(Button)findViewById(R.id.login);
        idText = (EditText)findViewById(R.id.email);
        passwordText = (EditText)findViewById(R.id.password);
        RegisterButton(registerButton);
        LoginButton(loginButton);

        if(!NetworkConnection()){
            NotConnected_showAlert();
        }
    }
    void RegisterButton(TextView registerButton){
        registerButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent registerIntent=new Intent(MainActivity.this,RegisterActivity.class);
                MainActivity.this.startActivity(registerIntent);
            }
        });
    }
    void LoginButton(Button loginButton){
        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                userID = idText.getText().toString().trim();
                userPassword = passwordText.getText().toString().trim();
                if(userID!=null&&!userID.isEmpty() && !userPassword.isEmpty()){
                    Intent intent = new Intent(MainActivity.this,GrandListActivity.class); //mainactivity로 넘어가기 전에 Intent에 넣음
                    login();
                }
            }
        });
    }


    private void login(){
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success"); //로그인 성공시
                    if (success) {
                        userID=jsonResponse.getString("userID"); //정보를 DB에서 가져옵니다.
                        userName = jsonResponse.getString("userName");

                        //모든 알림 서비스 로그인과 동시에 실행.
                        Intent sensorServiceIntent = new Intent(MainActivity.this, NotiService.class);

                        sensorServiceIntent.putExtra("userID", userID);
                        startService(sensorServiceIntent);


                        Intent intent = new Intent(MainActivity.this,GrandListActivity.class); //mainactivity로 넘어가기 전에 Intent에 넣음
                        intent.putExtra("userID",userID);
                        intent.putExtra("userName",userName);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        MainActivity.this.startActivity(intent);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        dialog = builder.setMessage("계정을 확인해 주세요.")
                                .setNegativeButton("다시 시도", null)
                                .create();
                        dialog.show();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        LoginRequest loginRequest = new LoginRequest(userID, userPassword, responseListener); //LoginRequest에 객체를 만들고
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(loginRequest);


    }
    private void AutoLogin(){
        settings=getSharedPreferences("settings", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor=settings.edit();

        editor.putString("userID",userID);
        editor.putString("userPassword",userPassword);
        editor.putString("userName",userName);
        editor.putString("userEmail",userEmail);
        editor.putBoolean("loginChecked",true);

        editor.commit();
    }
    private boolean NetworkConnection(){
        //네트워크 연결오류인지 확인
        ConnectivityManager manager=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isMobileAvailable=manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isAvailable();
        boolean isMobileConnect=manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        boolean isWifiAvailable=manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isAvailable();
        boolean isWifiConnect=manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();

        if((isWifiAvailable&&isWifiConnect)||(isMobileAvailable&&isMobileConnect)){
            return true;
        }else{
            return false;
        }
    }
    private void NotConnected_showAlert(){
        //네트워크 연결 오류시 알림
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("네트워크 연결 오류");
        builder.setMessage("사용 가능한 무선네트워크가 없습니다.")
                .setCancelable(false)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        android.os.Process.killProcess(android.os.Process.myPid());//앱 강제 종료
                    }
                });
        AlertDialog alert=builder.create();
        alert.show();
    }
    public void onStop(){
        //어플리케이션 화면이 닫힐때
        super.onStop();
    }
}
