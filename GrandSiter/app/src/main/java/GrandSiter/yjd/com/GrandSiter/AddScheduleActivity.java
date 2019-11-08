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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class AddScheduleActivity extends AppCompatActivity {

    private String grandId;
    private AlertDialog dialog;
    EditText scheNameText, scheDesText;
    DatePicker scheDatePicker;
    Button scheAddBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);
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
        scheNameText = (EditText)findViewById(R.id.scheTitle);
        scheDesText = (EditText)findViewById(R.id.scheDes);
        scheDatePicker = (DatePicker)findViewById(R.id.scheDate);
    }
    void setButton(){
        scheAddBtn = (Button)findViewById(R.id.addScheBtn);
        scheAddBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String tmpName, tmpDes, tmpDate;
                int tmpMonth, tmpDay, tmpYear;

                tmpName = scheNameText.getText().toString();
                tmpDes = scheDesText.getText().toString();
                tmpYear = scheDatePicker.getYear();
                tmpMonth = scheDatePicker.getMonth()+1;
                tmpDay = scheDatePicker.getDayOfMonth();
                tmpDate = tmpYear+"."+tmpMonth+"."+tmpDay;

                if(tmpName.equals("") || tmpDes.equals("") || tmpDate.equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddScheduleActivity.this);
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
                                AlertDialog.Builder builder = new AlertDialog.Builder(AddScheduleActivity.this);
                                dialog = builder.setMessage("등록 성공")
                                        .setPositiveButton("확인", null)
                                        .create();
                                dialog.show();
                                finish();   //되돌아감
                            }
                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(AddScheduleActivity.this);
                                dialog = builder.setMessage("등록 실패")
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
                AddScheRequest addScheRequest = new AddScheRequest(grandId, tmpName, tmpDes, tmpDate, responseListener);
                RequestQueue qu = Volley.newRequestQueue(AddScheduleActivity.this);
                qu.add(addScheRequest);

            }
        });
    }
}
