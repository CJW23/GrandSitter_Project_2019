package GrandSiter.yjd.com.GrandSiter;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class DeleteScheRequest extends StringRequest{
    private static final String URL = "https://sammaru.cbnu.ac.kr/grandsitters/deletesche.php";
    private Map<String, String> param;
    public DeleteScheRequest(String id, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        param = new HashMap<>();
        param.put("scheID", id);
    }
    @Override
    public Map<String, String> getParams(){
        return param;
    }
}
