package GrandSiter.yjd.com.GrandSiter;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class DeleteGrandRequest extends StringRequest{
    //private static final String URL = "http://192.168.0.21/addmedi.php";
    private static final String URL = "https://sammaru.cbnu.ac.kr/grandsitters/deletegrand.php";
    private Map<String, String> param;

    public DeleteGrandRequest(String id, Response.Listener<String> listener) {
        super(Request.Method.POST, URL, listener, null);
        param = new HashMap<>();
        param.put("grandId", id);
    }

    @Override
    public Map<String, String> getParams(){
        return param;
    }
}
