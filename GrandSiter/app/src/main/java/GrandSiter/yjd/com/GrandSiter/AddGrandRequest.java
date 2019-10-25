package GrandSiter.yjd.com.GrandSiter;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class AddGrandRequest extends StringRequest{

    private static final String URL = "https://sammaru.cbnu.ac.kr/grandsitters/addgr.php";
    //private static final String URL = "http://192.168.0.21/addgr.php";
    private Map<String, String> param;

    public AddGrandRequest(String grName, String grAge, String grGender, String grCh, String userID, Response.Listener<String>listener) {
        super(Method.POST, URL, listener, null);
        param = new HashMap<>();
        param.put("grName", grName);
        param.put("grAge", grAge);
        param.put("grGender", grGender);
        param.put("grCh", grCh);
        //userID = "a@a.a";       //임시
        param.put("userID", userID);
        Log.d("Hello : ",grName+" "+grAge+" "+grGender+" "+grCh+" "+userID);
    }

    @Override
    public Map<String, String> getParams(){
        return param;
    }
}
