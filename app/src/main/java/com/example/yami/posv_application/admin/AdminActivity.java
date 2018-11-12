package com.example.yami.posv_application.admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.yami.posv_application.R;
import com.example.yami.posv_application.activities.BaseActivity;
import com.example.yami.posv_application.activities.MainActivity;
import com.example.yami.posv_application.notice_board.PostActivity;
import com.example.yami.posv_application.user_management.AutoLoginUnRequest;
import com.example.yami.posv_application.user_management.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class AdminActivity extends BaseActivity {
    SessionManager session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_admin_main);

        session = new SessionManager(getApplicationContext());
        Button admin_lbs = (Button) findViewById(R.id.admin_lbs);
        Button admin_board = (Button) findViewById(R.id.admin_board);
        Button admin_logout = (Button) findViewById(R.id.admin_logout);

        admin_lbs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminActivity.this, AdminLBSActivity.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                AdminActivity.this.startActivity(intent);
            }
        });

        admin_board.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences pref = getSharedPreferences("psvLoginSes", 0);
                final String userID = pref.getString("id", "");
//        Intent intent = new Intent(getApplicationContext(), PostActivity.class);
                session.checkLogin();
                if (session.isLoggedIn()){
                    //4. 콜백 처리부분(volley 사용을 위한 ResponseListener 구현 부분)
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");

                                Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();

                                //서버에서 보내준 값이 true이면?
                                if (success) {

                                    //Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();

                                    String userNum = jsonResponse.getString("u_num");

                                    //로그인에 성공했으므로 MainActivity로 넘어감
                                    Intent intent = new Intent(AdminActivity.this, PostActivity.class);
                                    String postList = new BackgroundTask().execute().get();
                                    intent.putExtra("postList", postList);
                                    intent.putExtra("u_num", userNum);
                                    startActivity(intent);
                                    Log.e("un = ", userNum);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    AdminActivity.this.startActivity(intent);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    AutoLoginUnRequest loginRequest = new AutoLoginUnRequest(userID, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(AdminActivity.this);
                    queue.add(loginRequest);
                }
                //startActivity(intent);
                finish();
            }
        });

        admin_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session.logoutUser();
            }
        });


    }

    class BackgroundTask extends AsyncTask<Void, Void, String> {
        String target;

        @Override
        protected void onPreExecute() {
            //List.php은 파싱으로 가져올 웹페이지
            target = "http://pj9087.dothome.co.kr/GetForum.php";
        }

        @Override
        protected String doInBackground(Void... voids) {

            try {
                URL url = new URL(target);//URL 객체 생성

                //URL을 이용해서 웹페이지에 연결하는 부분
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                //바이트단위 입력스트림 생성 소스는 httpURLConnection
                InputStream inputStream = httpURLConnection.getInputStream();

                //웹페이지 출력물을 버퍼로 받음 버퍼로 하면 속도가 더 빨라짐
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;

                //문자열 처리를 더 빠르게 하기 위해 StringBuilder클래스를 사용함
                StringBuilder stringBuilder = new StringBuilder();

                //한줄씩 읽어서 stringBuilder에 저장함
                while ((temp = bufferedReader.readLine()) != null) {
                    stringBuilder.append(temp + "\n");//stringBuilder에 넣어줌
                }

                //사용했던 것도 다 닫아줌
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();//trim은 앞뒤의 공백을 제거함

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
//            Intent intent = new Intent(MainActivity.this, PostActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.putExtra("postList", result);//파싱한 값을 넘겨줌
            //MainActivity.this.startActivity(intent);//Activity로 넘어감
        }
    }


}
