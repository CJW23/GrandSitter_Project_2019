package GrandSiter.yjd.com.GrandSiter;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class LoginRequest extends StringRequest {
    final static private String URL="https://sammaru.cbnu.ac.kr/grandsitters/login.php";
    //final static private String URL="http://192.168.0.21/login.php";
    private Map<String,String> parameters;

    public LoginRequest(String userID, String userPassword, Response.Listener<String> listener)
    {
        super(Method.POST,URL,listener,null);
        parameters=new HashMap<>();
        parameters.put("userID",userID);
        parameters.put("userPassword",userPassword);
    }

    @Override
    public Map<String, String> getParams()
    {
        return parameters;
    }

}

