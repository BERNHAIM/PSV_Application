package com.example.yami.posv_application.user_management;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.Spanned;
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

import com.example.yami.posv_application.BackPressHandler;
import com.example.yami.posv_application.R;
import com.example.yami.posv_application.activities.BaseActivity;
import com.example.yami.posv_application.activities.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class RegisterActivity extends BaseActivity {
    public InputFilter filterKor;
    public InputFilter filterAlphaNum;
    Spinner emailSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        //회원가입에 이메일의 도메인을 설정하기 위한 스피닝
        emailSpinner = (Spinner)findViewById(R.id.registSpinner);
        ArrayAdapter emailAdapter = ArrayAdapter.createFromResource(this, R.array.email, android.R.layout.simple_spinner_item);
        emailAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        emailSpinner.setAdapter(emailAdapter);


        final EditText idText = (EditText) findViewById(R.id.idText);
        final EditText passwordText = (EditText) findViewById(R.id.passwordText);
        final EditText nameText = (EditText) findViewById(R.id.nameText);
        final EditText emailText = (EditText) findViewById(R.id.emailText);

        final TextView backToLogin = (TextView) findViewById(R.id.backToLogin);



        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent t = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(t);

                finish();

            }
        });

        //입력하는 값을 영서,숫자로 제한하는 함수
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

        //입력하는 값을 한글로 제한하는 함수
        filterKor = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                Pattern ps = Pattern.compile("^[ㄱ-ㅎㅏ-ㅣ가-힣]+$");
                if (!ps.matcher(source).matches()) {
                    return "";
                }
                return null;
            }
        };

        //아이디,이메일,패스워드의 입력을 영어,숫자로 제한, 이름의 입력을 한글로 제한
        nameText.setFilters(new InputFilter[]{filterKor});
        idText.setFilters(new InputFilter[]{filterAlphaNum});
        emailText.setFilters(new InputFilter[]{filterAlphaNum});
        passwordText.setFilters(new InputFilter[]{filterAlphaNum});

        Button regbtn = (Button) findViewById(R.id.registerbtn);
        regbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String email = emailSpinner.getSelectedItem().toString();

                String userID = idText.getText().toString();
                String userPassword = passwordText.getText().toString();
                String userName = nameText.getText().toString();
                String userEmail = emailText.getText().toString() + email; //이메일 뒤에 @도메인 까지 저장

                //아이디 입력을 최소 5자 초과로 받기
                if (userID.length() < 5) {
                    if (userID.length() == 0)
                        Toast.makeText(getApplicationContext(), "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "6자 이상의 아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();

                    //비밀번호 입력을 최소 5자 초과로 받기
                } else if (userPassword.length() < 5) {
                    if (userPassword.length() == 0)
                        Toast.makeText(getApplicationContext(), "패스워드를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "8자 이상의 패스워드를 입력해주세요.", Toast.LENGTH_SHORT).show();

               //사용자의 이름을 1자 초과로 받기
                } else if (userName.length() < 1) {
                    if (userPassword.length() == 0)
                        Toast.makeText(getApplicationContext(), "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "한 자 이상을 입력해주세요.", Toast.LENGTH_SHORT).show();

                //사용자의 이메일을 8자 초과로 받기
                } else if (userEmail.length() < 8) {
                    if (userPassword.length() == 0)
                        Toast.makeText(getApplicationContext(), "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "적절한 이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }

                //4. 콜백 처리부분(volley 사용을 위한 ResponseListener 구현 부분)
                Response.Listener<String> responseListener = new Response.Listener<String>() {

                    //서버로부터 여기서 데이터를 받음
                    @Override
                    public void onResponse(String response) {
                        try {
                            //서버로부터 받는 데이터는 JSON타입의 객체이다.
                            JSONObject jsonResponse = new JSONObject(response);
                            //그중 Key값이 "success"인 것을 가져온다.
                            boolean success = jsonResponse.getBoolean("success");

                            //회원 가입 성공시 success값이 true임
                            if (success) {

                                Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();

                                //알림상자를 만들어서 보여줌
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                builder.setMessage("회원가입 완료")
                                        .setPositiveButton("ok", null)
                                        .create()
                                        .show();

                                //그리고 첫화면으로 돌아감
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                RegisterActivity.this.startActivity(intent);

                            }
                            //회원 가입 실패시 success값이 false임
                            else {
                                Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_SHORT).show();

                                //알림상자를 만들어서 보여줌
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                builder.setMessage("register fail!!")
                                        .setNegativeButton("ok", null)
                                        .create()
                                        .show();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                };//responseListener 끝

                //volley 사용법
                //1. RequestObject를 생성한다. 이때 서버로부터 데이터를 받을 responseListener를 반드시 넘겨준다.


                RegisterRequest registerRequest = new RegisterRequest(userID, userPassword, userName, userEmail, responseListener);
                //2. RequestQueue를 생성한다.
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                //3. RequestQueue에 RequestObject를 넘겨준다.

                //아이디를 6자이상, 비밀번호를 8자 이상, 이름을 2글자 이상, 이메일을 6자 이상으로 받기
                if (userID.length() >= 6 && userPassword.length() >= 8 && userName.length() >= 2 && userEmail.length() >= 6)
                    queue.add(registerRequest);
            }
        });
    }



}