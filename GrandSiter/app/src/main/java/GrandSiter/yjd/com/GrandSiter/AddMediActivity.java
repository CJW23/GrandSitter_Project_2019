package GrandSiter.yjd.com.GrandSiter;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import GrandSiter.yjd.com.GrandSiter.R;

public class AddMediActivity extends AppCompatActivity {

    private String grandId;
    private AlertDialog dialog;
    EditText mediNameText, mediDesText;
    TimePicker mediTimePicker;
    Button  mediAddBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medi);

        getUserData();
        setEditText();
        setButton();
    }

    void getUserData(){
        Intent intent = getIntent();
        grandId = intent.getExtras().getString("id");
        Log.d("grandID : ", grandId);
    }
    void setEditText(){
        mediNameText = (EditText)findViewById(R.id.MediTitle);
        mediDesText = (EditText)findViewById(R.id.MediDes);
        mediTimePicker = (TimePicker) findViewById(R.id.MediTime);
    }
    void setButton(){
        mediAddBtn = (Button)findViewById(R.id.addMediBtn);
        mediAddBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String tmpName, tmpDes, tmpDate;
                int tmpHour, tmpMin;

                tmpName = mediNameText.getText().toString();
                tmpDes = mediDesText.getText().toString();
                tmpHour = mediTimePicker.getHour();
                tmpMin = mediTimePicker.getMinute();
                tmpDate = tmpHour+"시 "+tmpMin+"분";

                if(tmpName.equals("") || tmpDes.equals("") || tmpDate.equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddMediActivity.this);
                    dialog = builder.setMessage("빈 공간이 있습니다.")
                            .setPositiveButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(AddMediActivity.this);
                                dialog = builder.setMessage("삭제 성공")
                                        .setCancelable(false)
                                        .setPositiveButton("확인", null)
                                        .create();
                                dialog.show();
                                finish();   //되돌아감
                            }
                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(AddMediActivity.this);
                                dialog = builder.setMessage("등록 실패")
                                        .setCancelable(false)
                                        .setNegativeButton("확인", null)
                                        .create();
                                dialog.show();
                            }

                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                };

                AddMediRequest addMediRequest = new AddMediRequest(grandId, tmpName, tmpDes, tmpDate, responseListener);
                RequestQueue qu = Volley.newRequestQueue(AddMediActivity.this);
                qu.add(addMediRequest);

            }
        });
    }
}
