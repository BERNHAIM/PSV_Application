package com.example.yami.posv_application.user_management;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.example.yami.posv_application.R;

import com.example.yami.posv_application.activities.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class PwResetActivity extends BaseActivity {

    public InputFilter filterAlphaNum;
    Spinner pwdSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwsearch);

        //패스워드 재설정에 이메일의 도메인을 설정하기 위한 스피닝
        pwdSpinner = (Spinner)findViewById(R.id.pwdSpinner);
        ArrayAdapter pwdAdapter = ArrayAdapter.createFromResource(this, R.array.email, android.R.layout.simple_spinner_item);
        pwdAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pwdSpinner.setAdapter(pwdAdapter);

        final EditText idText = (EditText)findViewById(R.id.idText);
        final EditText emailText = (EditText)findViewById(R.id.emailText);
        final Button resetBtn = (Button) findViewById(R.id.resetBtn);
        LayoutInflater inf = getLayoutInflater();
        final View pw_dialog = inf.inflate(R.layout.dialog_reset_pw, null);

        final EditText pwd = (EditText)pw_dialog.findViewById(R.id.pwText);

        final SharedPreferences sp = getSharedPreferences("id_email", MODE_PRIVATE);
        final Editor editor = sp.edit();

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = pwdSpinner.getSelectedItem().toString();
                final String userEmail = emailText.getText().toString()+ email; //사용자의 이메일에 스피닝으로 선택한 도메인도 함께 저장
                final String userID = idText.getText().toString();

                editor.putString("u_id", userID);
                editor.putString("u_email", userEmail);
                editor.commit();

                // 이메일, 아이디, 새로운 비밀번호 입력을 영어로 제한하기 위한 함수
                filterAlphaNum = new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                        Pattern ps = Pattern.compile("^[a-zA-Z0-9]+$");
                        if(!ps.matcher(source).matches()){
                            return "";
                        }
                        return null;
                    }
                };

                // 이메일, 아이디, 새로운 비밀번호 입력을 영어로 제한
                idText.setFilters(new InputFilter[]{filterAlphaNum});
                emailText.setFilters(new InputFilter[]{filterAlphaNum});
                pwd.setFilters(new InputFilter[]{filterAlphaNum});

                //4. 콜백 처리부분(volley 사용을 위한 ResponseListener 구현 부분)
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();

                            //서버에서 보내준 값이 true이면?
                            if(success){

                                AlertDialog.Builder builder = new AlertDialog.Builder(PwResetActivity.this);
                                builder.setView(pw_dialog).setMessage("비밀번호 설정")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();

                                                String new_pwd = pwd.getText().toString();
                                                String userID = sp.getString("u_id", null);
                                                String userEmail = sp.getString("u_email", null);

                                                //4. 콜백 처리부분(volley 사용을 위한 ResponseListener 구현 부분)
                                                Response.Listener<String> responseListener = new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        try{
                                                            JSONObject jsonResponse = new JSONObject(response);
                                                            boolean success = jsonResponse.getBoolean("success");

                                                            Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();

                                                            //서버에서 보내준 값이 true이면?
                                                            if(success){

                                                                //알림상자를 만들어서 보여줌
                                                                AlertDialog.Builder builder = new AlertDialog.Builder(PwResetActivity.this);
                                                                builder.setMessage("비밀번호 설정 완료")
                                                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(DialogInterface dialog, int which) {
                                                                                //Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();

                                                                                //그리고 첫화면으로 돌아감
                                                                                Intent intent = new Intent(PwResetActivity.this, LoginActivity.class);
                                                                                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                                PwResetActivity.this.startActivity(intent);

                                                                                editor.clear();
                                                                                finish();
                                                                            }
                                                                        })
                                                                        .create()
                                                                        .show();

                                                            }else{//로그인 실패시
                                                                AlertDialog.Builder builder = new AlertDialog.Builder(PwResetActivity.this);
                                                                builder.setMessage("설정 실패")
                                                                        .setNegativeButton("retry", null)
                                                                        .create()
                                                                        .show();
                                                            }

                                                        }catch(JSONException e){
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                };

                                                PwResetRequest pwrtRequest = new PwResetRequest(userID, userEmail, new_pwd, responseListener);
                                                RequestQueue queue = Volley.newRequestQueue(PwResetActivity.this);
                                                queue.add(pwrtRequest);
                                            }
                                        })
                                        .create()
                                        .show();


                            }else{//로그인 실패시
                                AlertDialog.Builder builder = new AlertDialog.Builder(PwResetActivity.this);
                                builder.setMessage("아이디나 이메일이 잘못 되었습니다")
                                        .setNegativeButton("retry", null)
                                        .create()
                                        .show();
                            }

                        }catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                };

                IdEmailRequest ieRequest = new IdEmailRequest(userID, userEmail, responseListener);
                RequestQueue queue = Volley.newRequestQueue(PwResetActivity.this);
                queue.add(ieRequest);
            }
        });
    }
}

class IdEmailRequest extends StringRequest {

    //현재 안드로이드앱을 에뮬레이터로 돌리므로 에뮬레이터가 설치된 서버에 있는 아파치 서버에 접근하려면
    //다음과 같이 10.0.2.2:포트번호 로 접근해야합니다 저는 8080 포트를 써서 다음과 같이 했습니다
    final static private String URL = "http://pj9087.dothome.co.kr/id_email_find.php";
    private Map<String, String> parameters;

    //생성자
    public IdEmailRequest(String userID, String userEmail, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("u_id", userID);
        parameters.put("u_email", userEmail);
    }

    //추후 사용을 위한 부분
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return parameters;
    }
}
