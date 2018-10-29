package com.example.yami.posv_application.notice_board;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.yami.posv_application.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;

public class PostUpdate extends AppCompatActivity {

    SharedPreferences pref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_update);

        Intent intent = getIntent();

        final EditText postNameText = (EditText) findViewById(R.id.postNameText);
        final CheckBox checkBox = (CheckBox) findViewById(R.id.check1);
        final EditText contentsText = (EditText) findViewById(R.id.contentsText);
        final Button updateBtn = (Button) findViewById(R.id.btnUpdate);
        final Button cancelBtn = (Button) findViewById(R.id.btnCancel);

        postNameText.setText(intent.getStringExtra("postname"));
        contentsText.setText(intent.getStringExtra("contents"));

        final String postNum = intent.getStringExtra("p_num");
        final String p_uid = intent.getStringExtra("userID");
        pref = getSharedPreferences("psvLoginSes", 0);
        final String s_uid = pref.getString("id", null);

        updateBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String postName = postNameText.getText().toString();                         //제목
                String contents = contentsText.getText().toString();                         //내용
                String userID = p_uid;                 //유저아이디 인데 익명도 해야됨
                boolean check = checkBox.isChecked();
                String anony = "익명";

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

                            //게시글 등록 성공시 success값이 true임
                            if (success) {

                                //알림상자를 만들어서 보여줌
                                AlertDialog.Builder builder = new AlertDialog.Builder(PostUpdate.this);
                                builder.setMessage("수정 완료")
                                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();
                                                new BackgroundTask().execute();
                                            }
                                        })
                                        .create()
                                        .show();

                            }
                            //게시글 등록 실패시 success값이 false임
                            else {
                                Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_SHORT).show();

                                //알림상자를 만들어서 보여줌
                                AlertDialog.Builder builder = new AlertDialog.Builder(PostUpdate.this);
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
                PostUpdateRequest postUpdateRequest = new PostUpdateRequest(postNum, postName, contents, userID, anony, responseListener, check);
                //2. RequestQueue를 생성한다.
                RequestQueue queue = Volley.newRequestQueue(PostUpdate.this);
                //3. RequestQueue에 RequestObject를 넘겨준다.
                queue.add(postUpdateRequest);
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

            try{
                URL url = new URL(target);//URL 객체 생성

                //URL을 이용해서 웹페이지에 연결하는 부분
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();

                //바이트단위 입력스트림 생성 소스는 httpURLConnection
                InputStream inputStream = httpURLConnection.getInputStream();

                //웹페이지 출력물을 버퍼로 받음 버퍼로 하면 속도가 더 빨라짐
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;

                //문자열 처리를 더 빠르게 하기 위해 StringBuilder클래스를 사용함
                StringBuilder stringBuilder = new StringBuilder();

                //한줄씩 읽어서 stringBuilder에 저장함
                while((temp = bufferedReader.readLine()) != null){
                    stringBuilder.append(temp + "\n");//stringBuilder에 넣어줌
                }

                //사용했던 것도 다 닫아줌
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();//trim은 앞뒤의 공백을 제거함

            }catch(Exception e){
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
            Intent intent = new Intent(PostUpdate.this, PostActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("postList", result);//파싱한 값을 넘겨줌
            PostUpdate.this.startActivity(intent);//Activity로 넘어감
        }

    }
}