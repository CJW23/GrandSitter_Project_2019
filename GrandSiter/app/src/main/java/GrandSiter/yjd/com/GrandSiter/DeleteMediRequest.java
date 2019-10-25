package GrandSiter.yjd.com.GrandSiter;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class DeleteMediRequest extends StringRequest{
    private static final String URL = "https://sammaru.cbnu.ac.kr/grandsitters/deletemedi.php";
    //private static final String URL = "http://192.168.0.21/deletemedi.php";
    private Map<String, String> param;

    public DeleteMediRequest(String id, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        param = new HashMap<>();
        param.put("mediID", id);
    }
    @Override
    public Map<String, String> getParams(){
        return param;
    }
}
