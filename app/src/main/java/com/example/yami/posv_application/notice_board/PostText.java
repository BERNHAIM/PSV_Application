package com.example.yami.posv_application.notice_board;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.yami.posv_application.R;
import com.example.yami.posv_application.user_management.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class PostText extends AppCompatActivity {

    TextView textView;
    CommentListAdapter adapter;
    List<Comment> commentList;
    Button btnWrite, btnUpdate, btnDelete;
    SharedPreferences pref;
    //String commentNum, postNum, c_userID, comment, time;
    ListView listView;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_comment);

        Intent intent = getIntent();

        final TextView postname = (TextView) findViewById(R.id.postName);
        final TextView userID = (TextView) findViewById(R.id.userID);
        final TextView currentTime = (TextView) findViewById(R.id.date);
        final TextView contents = (TextView) findViewById(R.id.contents);

        listView = (ListView) findViewById(R.id.commentList);
        commentList = new ArrayList<Comment>();
        adapter = new CommentListAdapter(this, commentList);
        listView.setAdapter(adapter);

        final EditText commentText = (EditText) findViewById(R.id.comment);
        btnWrite = (Button) findViewById(R.id.writeBtn);
        btnUpdate = (Button) findViewById(R.id.updateBtn);
        btnDelete = (Button) findViewById(R.id.deleteBtn);

        pref = getSharedPreferences("psvLoginSes", 0);
        final String p_num = intent.getStringExtra("p_num");

        postname.setText(intent.getStringExtra("postname"));
        userID.setText(intent.getStringExtra("userID"));
        currentTime.setText(intent.getStringExtra("date"));
        contents.setText(intent.getStringExtra("contents"));

        final String ses_uid = pref.getString("id", "null");
        final String post_uid = userID.getText().toString();
        final String ses_unum = intent.getStringExtra("u_num");

        session = new SessionManager(getApplicationContext());

        //4. 콜백 처리부분(volley 사용을 위한 ResponseListener 구현 부분)
        final Response.Listener<String> responseListener3 = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();

                    //서버에서 보내준 값이 true이면?
                    if (success) {

                        //Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();

                        String postUserNum = jsonResponse.getString("u_num");

                        if (ses_unum.equals(postUserNum)){
                            btnUpdate.setVisibility(View.VISIBLE);
                            btnDelete.setVisibility(View.VISIBLE);
                        }
                    }

                }  catch (Exception e) {
                    e.printStackTrace();
                }
            }

        };
        UserNumRequest userNumRequest = new UserNumRequest(p_num, responseListener3);
        RequestQueue unQueue = Volley.newRequestQueue(PostText.this);
        unQueue.add(userNumRequest);

        HashMap<String, String> user = session.getUserDetails();

        // name
        String name = user.get(SessionManager.KEY_NAME);

        //유저 아이디가 같지 않으면 수정 버튼 보이지 않음
        if (ses_uid.equals("admin")){
            btnUpdate.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.VISIBLE);
        }//게스트 로그인일때 댓글쓰기 안보이게
        else if(name == null)
            btnWrite.setVisibility(View.GONE);

        else btnWrite.setVisibility(View.VISIBLE);

        //댓글 불러오기
        //4. 콜백 처리부분(volley 사용을 위한 ResponseListener 구현 부분)
        final Response.Listener<String> responseListener2 = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //intent로 값을 가져옵니다 이때 JSONObject타입으로 가져옵니다
                    JSONObject jsonObject = new JSONObject(response);
                    //WritePostActivity에서 PostActivity로 넘어갈 때 받아오는 json 데이터가 없으므로 WritePostActivity에서도 json을 받아올 수 있게 해주어야함
                    //List.php 웹페이지에서 response라는 변수명으로 JSON 배열을 만들었음..
                    JSONArray jsonArray = jsonObject.getJSONArray("response");
                    int count = 0;

                    //JSON 배열 길이만큼 반복문을 실행
                    while (count < jsonArray.length()) {
                        //count는 배열의 인덱스를 의미
                        JSONObject object = jsonArray.getJSONObject(count);

                        String commentNum = object.optString("c_num", "no value");
                        String postNum = object.optString("p_num", "no value");
                        String userID = object.optString("userID", "no ID value");
                        String date = object.optString("time", "no time value");
                        String comment = object.optString("comment", "no comment value");

                        //값들을 User클래스에 묶어줍니다
                        Comment com = new Comment(commentNum, postNum, userID, comment, date);
                        commentList.add(com);//리스트뷰에 값을 추가해줍니다
                        count++;
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        };
        CommentListRequest commentListRequest = new CommentListRequest(p_num, responseListener2);
        RequestQueue queue = Volley.newRequestQueue(PostText.this);
        queue.add(commentListRequest);


        //댓글 쓰기 버튼
        btnWrite.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final String comment = commentText.getText().toString();
                final String u_id = pref.getString("id", "");

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

                                //알림상자를 만들어서 보여줌
                                AlertDialog.Builder builder = new AlertDialog.Builder(PostText.this);
                                builder.setMessage("댓글 쓰기 완료")
                                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();
                                                //그리고 첫화면으로 돌아감
                                                Intent intent = new Intent(PostText.this, PostText.class);
                                                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);

                                                intent.putExtra("p_num", p_num);
                                                intent.putExtra("postname", postname.getText().toString());
                                                intent.putExtra("userID", userID.getText().toString());
                                                intent.putExtra("date", currentTime.getText().toString());
                                                intent.putExtra("contents", contents.getText().toString());
                                                intent.putExtra("u_num", ses_unum);

                                                startActivity(intent);

                                                //new BackgroundTask().execute();
                                            }
                                        })
                                        .create()
                                        .show();

                            }
                            //회원 가입 실패시 success값이 false임
                            else {
                                Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_SHORT).show();

                                //알림상자를 만들어서 보여줌
                                AlertDialog.Builder builder = new AlertDialog.Builder(PostText.this);
                                builder.setMessage("댓글 쓰기 실패")
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
                CommentRequest commentRequest = new CommentRequest(p_num, u_id, comment, responseListener);
                //2. RequestQueue를 생성한다.
                RequestQueue queue = Volley.newRequestQueue(PostText.this);
                //3. RequestQueue에 RequestObject를 넘겨준다.
                queue.add(commentRequest);
            }
        });

        //댓글 길게 클릭시 삭제
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final String com_num = commentList.get(position).getCommentNum();
                final String ses_uid = pref.getString("id", "null");
                final String com_uid = commentList.get(position).getUserID();

                if (ses_uid.equals(com_uid)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PostText.this);
                    builder.setMessage("댓글을 삭제하시겠습니까?")
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();

                                    //4. 콜백 처리부분(volley 사용을 위한 ResponseListener 구현 부분)
                                    final Response.Listener<String> responseListener3 = new Response.Listener<String>() {
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
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(PostText.this);
                                                    builder.setMessage("댓글 삭제 완료")
                                                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();
                                                                    //그리고 첫화면으로 돌아감
                                                                    Intent intent = new Intent(PostText.this, PostText.class);
                                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                    intent.putExtra("p_num", p_num);
                                                                    intent.putExtra("postname", postname.getText().toString());
                                                                    intent.putExtra("userID", userID.getText().toString());
                                                                    intent.putExtra("date", currentTime.getText().toString());
                                                                    intent.putExtra("contents", contents.getText().toString());
                                                                    intent.putExtra("u_num", ses_unum);

                                                                    PostText.this.startActivity(intent);
                                                                }
                                                            })
                                                            .create()
                                                            .show();

                                                }
                                                //게시글 등록 실패시 success값이 false임
                                                else {
                                                    Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_SHORT).show();

                                                    //알림상자를 만들어서 보여줌
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(PostText.this);
                                                    builder.setMessage("댓글 삭제 실패")
                                                            .setNegativeButton("ok", null)
                                                            .create()
                                                            .show();

                                                }

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                    };
                                    CommentDeleteRequest commentDeleteRequest = new CommentDeleteRequest(com_num, responseListener3);
                                    RequestQueue queue = Volley.newRequestQueue(PostText.this);
                                    queue.add(commentDeleteRequest);


                                }
                            }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                            .create()
                            .show();
                } else {
                    Toast.makeText(getApplicationContext(), "권한이 없습니다", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        //글 수정 버튼
        btnUpdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(PostText.this, PostUpdate.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);

                intent.putExtra("p_num", p_num);
                intent.putExtra("postname", postname.getText().toString());
                intent.putExtra("userID", userID.getText().toString());
                intent.putExtra("date", currentTime.getText().toString());
                intent.putExtra("contents", contents.getText().toString());
                intent.putExtra("u_num", ses_unum);
//
                startActivity(intent);
            }
        });

        //글 삭제 버튼
        btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(PostText.this);
                builder.setMessage("글을 삭제하시겠습니까?")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();

                                //4. 콜백 처리부분(volley 사용을 위한 ResponseListener 구현 부분)
                                final Response.Listener<String> responseListener4 = new Response.Listener<String>() {
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
                                                AlertDialog.Builder builder = new AlertDialog.Builder(PostText.this);
                                                builder.setMessage("글 삭제 완료")
                                                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();
                                                                //그리고 첫화면으로 돌아감
                                                                try {
                                                                    String postList = new BackgroundTask().execute().get();
                                                                    Intent intent = new Intent(PostText.this, PostActivity.class);
                                                                    intent.putExtra("u_num", ses_unum);
                                                                    intent.putExtra("postList", postList);
                                                                    startActivity(intent);
                                                                } catch (InterruptedException e) {
                                                                    e.printStackTrace();
                                                                } catch (ExecutionException e) {
                                                                    e.printStackTrace();
                                                                }//new BackgroundTask().execute();
                                                            }
                                                        })
                                                        .create()
                                                        .show();

                                            }
                                            //게시글 등록 실패시 success값이 false임
                                            else {
                                                Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_SHORT).show();

                                                //알림상자를 만들어서 보여줌
                                                AlertDialog.Builder builder = new AlertDialog.Builder(PostText.this);
                                                builder.setMessage("글 삭제 실패")
                                                        .setNegativeButton("ok", null)
                                                        .create()
                                                        .show();

                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                };
                                PostDeleteRequest postDeleteRequest = new PostDeleteRequest(p_num, responseListener4);
                                RequestQueue queue = Volley.newRequestQueue(PostText.this);
                                queue.add(postDeleteRequest);


                            }
                        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                        .create()
                        .show();
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
//            Intent intent = new Intent(PostText.this, PostActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.putExtra("postList", result);//파싱한 값을 넘겨줌
//            PostText.this.startActivity(intent);//Activity로 넘어감
        }

    }
}

