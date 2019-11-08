package GrandSiter.yjd.com.GrandSiter;

import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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


public class AddGrandActivity extends AppCompatActivity {
    private String userID;
    private String grandName;
    private String grandAge;
    private String grandGender;
    private String grandCharateristic;
    private AlertDialog dialog;
    EditText grNameText;
    DatePicker grAge;
    String grGenderText;
    EditText grCharacteristicText;
    RadioGroup genderGrp;
    RadioButton menBtn, womanBtn;
    Button addBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_grand);
        getUserData();
        setEditText();
        setButton();
        setRadioBtn();
    }
    void setRadioBtn(){
        menBtn = (RadioButton) findViewById(R.id.menbtn);
        womanBtn = (RadioButton) findViewById(R.id.womanbtn);
        genderGrp = (RadioGroup) findViewById(R.id.addGrGender);

        RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListener = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if( checkedId == R.id.menbtn){
                    grGenderText = "남성";
                }
                else if(checkedId == R.id.womanbtn){
                    grGenderText = "여성";
                }
                Log.d("gender : ", grGenderText);
            }
        };
        genderGrp.setOnCheckedChangeListener(radioGroupButtonChangeListener);

    }
    void getUserData(){
        Intent intent = getIntent();
        userID = intent.getExtras().getString("userID");
        Log.d("AddGrand userID : ", userID);
    }
    void setEditText(){
        grNameText = (EditText)findViewById(R.id.addGrName);
        grAge = (DatePicker) findViewById(R.id.addGrAge);

        grCharacteristicText=(EditText)findViewById(R.id.addGrCharacteristic);
    }
    void setButton(){
        addBtn = (Button)findViewById(R.id.addGrBtn);
        addBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String tmpName, tmpAge, tmpGender, tmpCh;
                int  tmpMonth, tmpDay, tmpYear;
                tmpName = grNameText.getText().toString();
                tmpYear = grAge.getYear();
                tmpMonth = grAge.getMonth() + 1;
                tmpDay = grAge.getDayOfMonth();
                tmpAge = tmpYear+"."+tmpMonth+"."+tmpDay;
                tmpGender = grGenderText;
                tmpCh = grCharacteristicText.getText().toString();

                if(tmpName.equals("") || tmpAge.equals("") || tmpGender.equals("") || tmpCh.equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddGrandActivity.this);
                    dialog = builder.setMessage("빈 공간이 있습니다.")
                            .setPositiveButton("확인", null)
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
                                AlertDialog.Builder builder = new AlertDialog.Builder(AddGrandActivity.this);
                                dialog = builder.setMessage("등록 성공")
                                        .setPositiveButton("확인", null)
                                        .create();
                                dialog.show();
                                finish();   //되돌아감
                            }
                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(AddGrandActivity.this);
                                dialog = builder.setMessage("등록 실패")
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
                //userID부분 고치기
                AddGrandRequest addGrandRequest = new AddGrandRequest(tmpName, tmpAge, tmpGender, tmpCh, userID, responseListener);
                RequestQueue qu = Volley.newRequestQueue(AddGrandActivity.this);
                qu.add(addGrandRequest);
            }
        });
    }
}
