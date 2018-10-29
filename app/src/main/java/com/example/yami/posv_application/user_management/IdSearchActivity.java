package com.example.yami.posv_application.user_management;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.AdapterView;
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

public class IdSearchActivity extends AppCompatActivity {

    Spinner emailSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idsearch);

        //스피너로 이메일을 선택
        emailSpinner = (Spinner)findViewById(R.id.emailSpinner);
        ArrayAdapter emailAdapter = ArrayAdapter.createFromResource(this, R.array.email, android.R.layout.simple_spinner_item);
        emailAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        emailSpinner.setAdapter(emailAdapter);


        final EditText emailText = (EditText)findViewById(R.id.emailText);
        final Button searchbtn = (Button) findViewById(R.id.searchbtn);

        //이메일 부분 영어로만 받기
        emailText.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                Pattern ps = Pattern.compile("^[a-zA-Z0-9]+$");
                if(!ps.matcher(source).matches()){
                    return "";
                }
                return null;
            }
        }});


        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailSpinner.getSelectedItem().toString();
                final String userEmail = emailText.getText().toString() + email; //이메일에 스피닝으로 입력한 도메인 함께 저장

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

                                //Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();

                                String userID = jsonResponse.getString("u_id");
                                String userPassword = jsonResponse.getString("u_password");
//                                String userName = jsonResponse.getString("u_name");

                                //알림상자를 만들어서 보여줌
                                AlertDialog.Builder builder = new AlertDialog.Builder(IdSearchActivity.this);
                                builder.setMessage("회원님의 아이디는 "  + userID + "입니다")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();
                                                //그리고 첫화면으로 돌아감
                                                Intent intent = new Intent(IdSearchActivity.this, LoginActivity.class);
                                                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                IdSearchActivity.this.startActivity(intent);
                                            }
                                        })
                                        .create()
                                        .show();

                            }else{//로그인 실패시
                                AlertDialog.Builder builder = new AlertDialog.Builder(IdSearchActivity.this);
                                builder.setMessage("찾기 실패")
                                        .setNegativeButton("retry", null)
                                        .create()
                                        .show();
                            }

                        }catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                };

                IdSearchRequest idsRequest = new IdSearchRequest(userEmail, responseListener);
                RequestQueue queue = Volley.newRequestQueue(IdSearchActivity.this);
                queue.add(idsRequest);
            }
        });
    }
}