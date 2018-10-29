package com.example.yami.posv_application.notice_board;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class PostUpdateRequest extends StringRequest{

    //현재 안드로이드앱을 에뮬레이터로 돌리므로 에뮬레이터가 설치된 서버에 있는 아파치 서버에 접근하려면
    //다음과 같이 10.0.2.2:포트번호 로 접근해야합니다 저는 8080 포트를 써서 다음과 같이 했습니다
    final static private String URL = "http://pj9087.dothome.co.kr/PostUpdate.php";
    /* private static Object WritePostRequest;
     private static final Object CheckBox = WritePostRequest;*/
    private Map<String, String> parameters;

    //생성자
    public PostUpdateRequest(String postNum, String postName, String contents, String userID, String anony, Response.Listener<String> listener, boolean check){
        super(Request.Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("p_num", postNum);
        parameters.put("p_name", postName);
        parameters.put("contents", contents);
        if (check == true)
            parameters.put("userID", anony);
        else
            parameters.put("userID", userID+"");

    }

    //추후 사용을 위한 부분
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return parameters;
    }
}
