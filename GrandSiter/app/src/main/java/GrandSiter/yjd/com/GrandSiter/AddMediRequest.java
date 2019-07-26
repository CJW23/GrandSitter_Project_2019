package GrandSiter.yjd.com.GrandSiter;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class AddMediRequest extends StringRequest {

    private static final String URL = "http://192.168.0.21/addmedi.php";
    //private static final String URL = "http://175.212.26.202:3389/addmedi.php";
    private Map<String, String> param;

    public AddMediRequest(String id, String name, String des, String date, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        param = new HashMap<>();
        param.put("grandId", id);
        param.put("mediName", name);
        param.put("mediDes", des);
        param.put("mediDate", date);
    }

    @Override
    public Map<String, String> getParams(){
        return param;
    }
}
