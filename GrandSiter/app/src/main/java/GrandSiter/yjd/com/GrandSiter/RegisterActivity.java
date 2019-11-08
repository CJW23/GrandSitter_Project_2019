package GrandSiter.yjd.com.GrandSiter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private String userID;
    private String userPassword;
    private String userCheckPassword;
    private String userName;
    private String userGenderText;
    private AlertDialog dialog;
    private boolean validate = false;
    private DatePicker userDate;
    private String userAge;
    private String usertpid;
    private RadioGroup gendergrp;
    private RadioButton menBtn, womanBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        final EditText idText = (EditText) findViewById(R.id.idText);
        final EditText passwordText = (EditText) findViewById(R.id.passwordText);
        final EditText checkpasswordText = (EditText) findViewById(R.id.checkpasswordText);
        final EditText nameText = (EditText) findViewById(R.id.nameText);
        menBtn = (RadioButton) findViewById(R.id.usermenbtn);
        womanBtn = (RadioButton) findViewById(R.id.userwomanbtn);
        gendergrp = (RadioGroup) findViewById(R.id.userGender);
        userDate = (DatePicker) findViewById(R.id.userAge);

        RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListener = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if( checkedId == R.id.usermenbtn){
                    userGenderText = "남성";
                }
                else if(checkedId == R.id.userwomanbtn){
                    userGenderText = "여성";
                }
                //Log.d("gender : ", grGenderText);
            }
        };
        gendergrp.setOnCheckedChangeListener(radioGroupButtonChangeListener);

        final Button idcheckButton = (Button) findViewById(R.id.idcheckButton);

        //이메일 중복 체크
        idcheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userID = idText.getText().toString();

                if (validate) {
                    return;
                }
                if (userID.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("아이디는 빈 칸일 수 없습니다")
                            .setPositiveButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }


                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            usertpid = idText.getText().toString().trim();

                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if(usertpid.length() < 5)
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("아이디를 5글자 이상으로 해주세요")
                                        .setNegativeButton("확인", null)
                                        .create();
                                dialog.show();
                            }
                            else if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("가능한 아이디 입니다.")
                                        .setPositiveButton("확인", null)
                                        .create();
                                dialog.show();
                                idText.setEnabled(false);
                                validate = true;
                                idText.setBackgroundColor(getResources().getColor(R.color.colorGray));
                                idcheckButton.setBackgroundColor(getResources().getColor(R.color.colorGray));
                            }
                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("사용 불가능한 아이디 입니다.")
                                        .setNegativeButton("확인", null)
                                        .create();
                                dialog.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                ValidateRequest validateRequest = new ValidateRequest(userID, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(validateRequest);

            }
        });
        Button registerButton = (Button) findViewById(R.id.registerButton);

        //등록 버튼
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userID = idText.getText().toString().trim();
                userName=nameText.getText().toString().trim();
                userPassword = passwordText.getText().toString().trim();
                userCheckPassword = checkpasswordText.getText().toString().trim();
                //userGenderText
                userAge = userDate.getYear() + "." + userDate.getMonth()+"."+userDate.getDayOfMonth();

                if (!validate) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("아이디 중복체크를 해주세요.")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                if (!userPassword.equals(userCheckPassword)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("비밀번호가 다릅니다!")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                String pwPattern = "^(?=.*\\d)(?=.*[a-z]).{8,15}$";
                Matcher matcher = Pattern.compile(pwPattern).matcher(userPassword);
                pwPattern = "(.)\\1\\1\\1";
                Matcher matcher2 = Pattern.compile(pwPattern).matcher(userPassword);

                if(!matcher.matches()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("패스워드를 영문, 숫자, 특수문자 조합으로 만들어주세요(9~12자리)!")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                if(matcher2.find()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("비밀번호에 같은 문자가 4개이상 있습니다..!")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                if(userPassword.contains(userID)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("비밀번호에 ID를 포함하고 있습니다..!")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                if(userPassword.contains(" ")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("비밀번호에 공백이 있습니다.!")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }


                if (userID.equals("") ||userGenderText.equals("") || userPassword.equals("") || userCheckPassword.equals("") || userName.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("빈칸을 채워주세요.")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("회원 가입 성공")
                                        .setPositiveButton("확인", null)
                                        .create();
                                dialog.show();
                                finish();   //되돌아감
                            }
                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("회원 가입 실패")
                                        .setNegativeButton("확인", null)
                                        .create();
                                dialog.show();
                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                RegisterRequest registerRequest = new RegisterRequest(userID, userPassword, userName, userAge, userGenderText, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(registerRequest);
            }
        });
    }

    protected void onStop(){
        super.onStop();
        if(dialog!=null)
        {
            dialog.dismiss();
            dialog = null;
        }
    }

}
