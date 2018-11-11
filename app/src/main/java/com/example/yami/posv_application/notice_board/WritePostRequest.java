package com.example.yami.posv_application.notice_board;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class WritePostRequest extends StringRequest {

    //현재 안드로이드앱을 에뮬레이터로 돌리므로 에뮬레이터가 설치된 서버에 있는 아파치 서버에 접근하려면
    //다음과 같이 10.0.2.2:포트번호 로 접근해야합니다 저는 8080 포트를 써서 다음과 같이 했습니다
//    final static private String URL = "http://pj9087.dothome.co.kr/WritePost2.php";
    final static private String URL = "http://pj9087.dothome.co.kr/NewWritePost.php";
    private Map<String, String> parameters;

    //생성자
    public WritePostRequest(String postName, String contents, String area, String userID, String anony, String userNum, Response.Listener<String> listener, boolean check){
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("p_name", postName);
        parameters.put("contents", contents);
        parameters.put("area", area);
        if (check == true)
            parameters.put("userID", anony);
        else
            parameters.put("userID", userID);
        parameters.put("u_num", userNum+"");
    }

    //추후 사용을 위한 부분
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return parameters;
    }
}