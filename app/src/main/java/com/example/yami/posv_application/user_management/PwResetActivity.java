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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import com.example.yami.posv_application.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class PwResetActivity extends AppCompatActivity {

    Spinner pwdSpinner;
    public InputFilter filterAlphaNum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwsearch);

        //패스워드 재설정에 이메일의 도메인을 설정하기 위한 스피닝
        pwdSpinner = (Spinner)findViewById(R.id.pwdSpinner);
        ArrayAdapter pwdAdapter = ArrayAdapter.createFromResource(this, R.array.email, android.R.layout.simple_spinner_item);
        pwdAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pwdSpinner.setAdapter(pwdAdapter);

        final TextView stored_pw = (TextView)findViewById(R.id.stored_pw);
        final EditText idText = (EditText)findViewById(R.id.idText);
        final EditText emailText = (EditText)findViewById(R.id.emailText);
        final Button resetBtn = (Button) findViewById(R.id.resetBtn);
        //final EditText adet = new EditText(PwResetActivity.this);
        LayoutInflater inf = getLayoutInflater();
        final View pw_dialog = inf.inflate(R.layout.dialog_reset_pw, null);

        final EditText pwd = (EditText)pw_dialog.findViewById(R.id.pwText);

        final SharedPreferences sp = getSharedPreferences("id_email", MODE_PRIVATE);
        final Editor editor = sp.edit();

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

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = pwdSpinner.getSelectedItem().toString();
                final String userEmail = emailText.getText().toString()+ email; //사용자의 이메일에 스피닝으로 선택한 도메인도 함께 저장
                final String userID = idText.getText().toString();

                editor.putString("u_id", userID);
                editor.putString("u_email", userEmail);
                editor.commit();

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
            }
        });
    }
}
