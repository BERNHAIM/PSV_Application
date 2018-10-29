package com.example.yami.posv_application.notice_board;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.yami.posv_application.activities.MainActivity;
import com.example.yami.posv_application.R;
import com.example.yami.posv_application.user_management.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PostActivity extends AppCompatActivity {

    private ListView listView;
    private PostListAdapter adapter;
    private List<Post> postList;
    private List<Post> saveList;
    Button btnWrite;
    String userID, postName, currentTime, contents, p_num;
    SessionManager session;

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
                registerIntent.addFlags(registerIntent.FLAG_ACTIVITY_NO_HISTORY);
                PostActivity.this.startActivity(registerIntent);
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

                startActivity(intent);
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
        session.logoutUser();
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
}