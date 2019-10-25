package GrandSiter.yjd.com.GrandSiter;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class AddScheRequest extends StringRequest{

    private static final String URL = "https://sammaru.cbnu.ac.kr/grandsitters/addsche.php";
    //private static final String URL = "http://192.168.0.21/addsche.php";
    private Map<String, String> param;

    public AddScheRequest(String id, String name, String des, String date, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        param = new HashMap<>();
        Log.d("schedule", id+" "+name+" "+des+" "+date);
        param.put("scheId", id);
        param.put("scheName", name);
        param.put("scheDes", des);
        param.put("scheDate", date);

    }

    @Override
    public Map<String, String> getParams(){
        return param;
    }
}
