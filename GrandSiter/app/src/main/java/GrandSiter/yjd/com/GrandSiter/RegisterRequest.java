package GrandSiter.yjd.com.GrandSiter;


import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;


public class RegisterRequest extends StringRequest {
    //final static private String URL = "http://175.212.26.202:3389/register.php";
   final static private String URL = "http://192.168.0.21/register.php";

    private Map<String,String> parameters;

    public RegisterRequest(String userID, String userPassword, String userName, String userAge, String userGender, Response.Listener<String> listener)
    {
        super(Method.POST,URL,listener,null);
        Log.d("Test1 : ", userID + " " + userPassword + " " + userName);
        parameters = new HashMap<>();
        parameters.put("userID",userID);
        parameters.put("userPassword",userPassword);
        parameters.put("userName",userName);
        parameters.put("userAge", userAge);
        parameters.put("userGender", userGender);
    }

    @Override
    public Map<String,String> getParams()
    {
        return parameters;
    }
}

