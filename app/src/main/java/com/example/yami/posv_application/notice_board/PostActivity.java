package com.example.yami.posv_application.notice_board;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.example.yami.posv_application.activities.MainActivity;
import com.example.yami.posv_application.R;
import com.example.yami.posv_application.user_management.LoginActivity;
import com.example.yami.posv_application.user_management.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class PostActivity extends AppCompatActivity {

    private ListView listView;
    private PostListAdapter adapter;
    private List<Post> postList;
    private List<Post> saveList;
    Button btnWrite;
    String userID, postName, currentTime, contents, p_num;
    SessionManager session;
    Spinner forumSpinner;
    String forumList[] = {"지역", "전체 지역", "서울", "경기", "인천", "광주", "부산"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        // Session class instance
        session = new SessionManager(getApplicationContext());

        Intent intent = getIntent();

        listView = (ListView) findViewById(R.id.listView);
        postList = new ArrayList<Post>();
        saveList = new ArrayList<Post>();
        adapter = new PostListAdapter(getApplicationContext(), postList,  saveList);
        listView.setAdapter(adapter);
        btnWrite = (Button)findViewById(R.id.btnWrite);
        EditText search = (EditText)findViewById(R.id.searchPost);

        forumSpinner = (Spinner)findViewById(R.id.forumSpinner);
        ArrayAdapter emailAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, forumList);
        emailAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        forumSpinner.setAdapter(emailAdapter);

        final String userNum = intent.getStringExtra("u_num");
        Button btnLogout = (Button)findViewById(R.id.btnLogout);
        //Log.e("postun", userNum);

        HashMap<String, String> user = session.getUserDetails();
        // name
        String name = user.get(SessionManager.KEY_NAME);
        if(name == null) {
            btnWrite.setVisibility(Button.GONE);
            btnLogout.setVisibility(Button.GONE);
        }
        else btnWrite.setVisibility(Button.VISIBLE);

        try{
            //intent로 값을 가져옵니다 이때 JSONObject타입으로 가져옵니다
            JSONObject jsonObject = new JSONObject(intent.getStringExtra("postList"));
            //WritePostActivity에서 PostActivity로 넘어갈 때 받아오는 json 데이터가 없으므로 WritePostActivity에서도 json을 받아올 수 있게 해주어야함
            //List.php 웹페이지에서 response라는 변수명으로 JSON 배열을 만들었음..
            JSONArray jsonArray = jsonObject.getJSONArray("response");
            int count = 0;

            //JSON 배열 길이만큼 반복문을 실행
            while(count < jsonArray.length()){
                //count는 배열의 인덱스를 의미
                JSONObject object = jsonArray.getJSONObject(count);

                p_num = object.optString("p_num", "no value");
                postName = object.optString("p_name", "no value");
                contents = object.optString("contents", "no value");
                userID = object.optString("userID", "no value");//여기서 ID가 대문자임을 유의
                currentTime = object.optString("c_time", "no value");

                //값들을 User클래스에 묶어줍니다
                Post post = new Post(p_num, postName, contents, userID, currentTime);
                postList.add(post);
                saveList.add(post); //리스트뷰에 값을 추가해줍니다
                count++;
            }


        }catch(Exception e){
            e.printStackTrace();
        }

        btnWrite.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent registerIntent = new Intent(PostActivity.this, WritePostActivity.class);
                registerIntent.putExtra("u_num", userNum);
                registerIntent.addFlags(registerIntent.FLAG_ACTIVITY_NO_HISTORY);
                PostActivity.this.startActivity(registerIntent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
               session.logoutUser();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*setContentView(R.layout.activity_post_comment);*/
                Intent intent = new Intent(getApplicationContext(), PostText.class);

                intent.putExtra("p_num", postList.get(position).getPostNum());
                intent.putExtra("postname",postList.get(position).getPostName());
                intent.putExtra("userID",postList.get(position).getUserID());
                intent.putExtra("date",postList.get(position).getCurrentTime());
                intent.putExtra("contents",postList.get(position).getContents());
                intent.putExtra("u_num", userNum);

                startActivity(intent);
            }
        });

        forumSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if(forumList[i].equals("전체 지역")){
                    try {
                        String postList = new MainForum().execute().get();
                        Intent registerIntent = new Intent(PostActivity.this, PostActivity.class);
                        registerIntent.addFlags(registerIntent.FLAG_ACTIVITY_CLEAR_TOP);
                        registerIntent.putExtra("postList", postList);
                        registerIntent.putExtra("u_num", userNum);
                        Toast.makeText(getApplicationContext(), "전체 지역", Toast.LENGTH_SHORT).show();
                        PostActivity.this.startActivity(registerIntent);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                } else if(forumList[i].equals("서울")){
                    try {
                        String postList = new SeoulForum().execute().get();
                        Intent registerIntent = new Intent(PostActivity.this, PostActivity.class);
                        registerIntent.addFlags(registerIntent.FLAG_ACTIVITY_CLEAR_TOP);
                        registerIntent.putExtra("postList", postList);
                        registerIntent.putExtra("u_num", userNum);
                        Toast.makeText(getApplicationContext(), "서울 지역", Toast.LENGTH_SHORT).show();
                        PostActivity.this.startActivity(registerIntent);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                } else if(forumList[i].equals("경기")){
                    try {
                        String postList = new GyeonggiForum().execute().get();
                        Intent registerIntent = new Intent(PostActivity.this, PostActivity.class);
                        registerIntent.addFlags(registerIntent.FLAG_ACTIVITY_CLEAR_TOP);
                        registerIntent.putExtra("postList", postList);
                        registerIntent.putExtra("u_num", userNum);
                        Toast.makeText(getApplicationContext(), "경기 지역", Toast.LENGTH_SHORT).show();
                        PostActivity.this.startActivity(registerIntent);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                } else if(forumList[i].equals("인천")){
                    try {
                        String postList = new IncheonForum().execute().get();
                        Intent registerIntent = new Intent(PostActivity.this, PostActivity.class);
                        registerIntent.addFlags(registerIntent.FLAG_ACTIVITY_CLEAR_TOP);
                        registerIntent.putExtra("postList", postList);
                        registerIntent.putExtra("u_num", userNum);
                        Toast.makeText(getApplicationContext(), "인천 지역", Toast.LENGTH_SHORT).show();
                        PostActivity.this.startActivity(registerIntent);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                } else if(forumList[i].equals("광주")){
                    try {
                        String postList = new GwangjuForum().execute().get();
                        Intent registerIntent = new Intent(PostActivity.this, PostActivity.class);
                        registerIntent.addFlags(registerIntent.FLAG_ACTIVITY_CLEAR_TOP);
                        registerIntent.putExtra("postList", postList);
                        registerIntent.putExtra("u_num", userNum);
                        Toast.makeText(getApplicationContext(), "광주 지역", Toast.LENGTH_SHORT).show();
                        PostActivity.this.startActivity(registerIntent);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                } else if(forumList[i].equals("부산")){
                    try {
                        String postList = new BusanForum().execute().get();
                        Intent registerIntent = new Intent(PostActivity.this, PostActivity.class);
                        registerIntent.addFlags(registerIntent.FLAG_ACTIVITY_CLEAR_TOP);
                        registerIntent.putExtra("postList", postList);
                        registerIntent.putExtra("u_num", userNum);
                        Toast.makeText(getApplicationContext(), "부산 지역", Toast.LENGTH_SHORT).show();
                        PostActivity.this.startActivity(registerIntent);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //글 검색
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchPost(charSequence.toString());//검색 기능용
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void searchPost(String search){
        postList.clear();
        for(int i = 0; i < saveList.size(); i++){
            if(saveList.get(i).getPostName().contains(search)){//contains메소드로 search 값이 있으면 true를 반환함
                postList.add(saveList.get(i));
            }
        }
        adapter.notifyDataSetChanged();//어댑터에 값일 바뀐것을 알려줌
    }

    // 스피너 선택시 불러올 게시글
    class MainForum extends AsyncTask<Void, Void, String> {
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
//
        }
    }

    class SeoulForum extends AsyncTask<Void, Void, String> {
        String target;

        @Override
        protected void onPreExecute() {
            //List.php은 파싱으로 가져올 웹페이지
            target = "http://pj9087.dothome.co.kr/GetSeoulForum.php";
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
//
        }
    }

    class GyeonggiForum extends AsyncTask<Void, Void, String> {
        String target;

        @Override
        protected void onPreExecute() {
            //List.php은 파싱으로 가져올 웹페이지
            target = "http://pj9087.dothome.co.kr/GetGyenggiForum.php";
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

        }
    }

    class IncheonForum extends AsyncTask<Void, Void, String> {
        String target;

        @Override
        protected void onPreExecute() {
            //List.php은 파싱으로 가져올 웹페이지
            target = "http://pj9087.dothome.co.kr/GetIncheonForum.php";
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

        }
    }

    class GwangjuForum extends AsyncTask<Void, Void, String> {
        String target;

        @Override
        protected void onPreExecute() {
            //List.php은 파싱으로 가져올 웹페이지
            target = "http://pj9087.dothome.co.kr/GetGwangjuForum.php";
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

        }
    }

    class BusanForum extends AsyncTask<Void, Void, String> {
        String target;

        @Override
        protected void onPreExecute() {
            //List.php은 파싱으로 가져올 웹페이지
            target = "http://pj9087.dothome.co.kr/GetBusanForum.php";
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

        }
    }
}