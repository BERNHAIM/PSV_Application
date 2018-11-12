package com.example.yami.posv_application.admin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.yami.posv_application.R;
import com.example.yami.posv_application.activities.BaseActivity;
import com.example.yami.posv_application.activities.MainActivity;
import com.example.yami.posv_application.activities.PopupActivity;
import com.example.yami.posv_application.models.dataTest;
import com.example.yami.posv_application.notice_board.Post;
import com.example.yami.posv_application.notice_board.PostActivity;
import com.example.yami.posv_application.notice_board.PostDeleteRequest;
import com.example.yami.posv_application.notice_board.PostText;
import com.example.yami.posv_application.notice_board.WritePostActivity;
import com.example.yami.posv_application.notice_board.WritePostRequest;
import com.example.yami.posv_application.user_management.LoginActivity;
import com.example.yami.posv_application.user_management.RegisterActivity;
import com.example.yami.posv_application.user_management.RegisterRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class AdminLBSActivity extends BaseActivity
{
    private EditText txtData1;
    private EditText txtData2;
    private EditText txtData3;
    private Button btnDataAdd;
    private ListView lvTest;

    String d_num, subject, location_x, location_y;
    String postSubject, postLocationX, getPostLocationY;

    private List<dangerArea> dangerAreaList;
    private List<dangerArea> saveList;

    //만든 커스텀 아답터
    private CusromAdapter adapter;
    //사용할 데이터리스트
    private ArrayList<dataTest> listReturn = new ArrayList<dataTest>();
    //데이터에 사용할 인덱스
    private int m_nIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_lbs);

        Intent intent = getIntent();

        this.txtData1 = (EditText)findViewById(R.id.txtData1);
        this.txtData2 = (EditText)findViewById(R.id.txtData2);
        this.txtData3 = (EditText)findViewById(R.id.txtData3);
        this.btnDataAdd = (Button)findViewById(R.id.btnDataAdd);
        this.lvTest = (ListView)findViewById(R.id.lvTest);
        /*this.lvTest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Toast.makeText(
                        getApplicationContext(),
                        "선택된 데이터 : " + position,
                        Toast.LENGTH_SHORT
                ).show();
            }
        });*/

        this.btnDataAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DB로 보내는 변수
                postSubject = txtData1.getText().toString();
                postLocationX = txtData2.getText().toString();
                getPostLocationY = txtData2.getText().toString();

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
                                AlertDialog.Builder builder = new AlertDialog.Builder(AdminLBSActivity.this);
                                builder.setMessage("위험지역 등록 완료")
                                        .setPositiveButton("ok", null)
                                        .create()
                                        .show();

                                //그리고 첫화면으로 돌아감
                                Intent intent = new Intent(AdminLBSActivity.this, AdminLBSActivity.class);
                                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                                AdminLBSActivity.this.startActivity(intent);

                            }
                            //회원 가입 실패시 success값이 false임
                            else {
                                Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_SHORT).show();

                                //알림상자를 만들어서 보여줌
                                AlertDialog.Builder builder = new AlertDialog.Builder(AdminLBSActivity.this);
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

                WriteDangerRequest writeDangerRequest = new WriteDangerRequest(postSubject, postLocationX, getPostLocationY, responseListener);
                //2. RequestQueue를 생성한다.
                RequestQueue queue = Volley.newRequestQueue(AdminLBSActivity.this);
                //3. RequestQueue에 RequestObject를 넘겨준다.
                queue.add(writeDangerRequest);
            }
        });

        adapter = new CusromAdapter(this, 0, listReturn );
        this.lvTest.setAdapter(adapter);

        try{
            String result = new BackgroundTask().execute().get();
            System.out.print("result : " + result);

            intent.putExtra("dangerAreaList", result);

        } catch (InterruptedException e ){
            e.printStackTrace();
        } catch(ExecutionException e){
            e.printStackTrace();
        }


        try{
            //intent로 값을 가져옵니다 이때 JSONObject타입으로 가져옵니다
            JSONObject jsonObject = new JSONObject(intent.getStringExtra("dangerAreaList"));
            //WritePostActivity에서 PostActivity로 넘어갈 때 받아오는 json 데이터가 없으므로 WritePostActivity에서도 json을 받아올 수 있게 해주어야함
            //List.php 웹페이지에서 response라는 변수명으로 JSON 배열을 만들었음..
            JSONArray jsonArray = jsonObject.getJSONArray("response");
            int count = 0;

            //JSON 배열 길이만큼 반복문을 실행
            while(count < jsonArray.length()){
                //count는 배열의 인덱스를 의미
                JSONObject object = jsonArray.getJSONObject(count);
                Log.d("object : ",object.toString());
                d_num = object.optString("d_num", "no value");
                subject = object.optString("subject", "no value");
                location_x = object.optString("x", "no value");
                location_y = object.optString("y", "no value");

                //값들을 User클래스에 묶어줍니다

                this.listReturn.add(new dataTest(count,d_num,subject, location_x, location_y));
                count++;
            }

            this.adapter.notifyDataSetChanged();


        }catch(Exception e){
            e.printStackTrace();
        }





    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class CusromAdapter extends ArrayAdapter<dataTest>
    {
        //리스트에 사용할 데이터
        private ArrayList<dataTest> m_listItem;

        public CusromAdapter(Context context, int textViewResourceId, ArrayList<dataTest> objects )
        {
            super(context, textViewResourceId, objects);

            this.m_listItem = objects;
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            View v = convertView;
            if( null == v)
            {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.listview_item, null);
                //v = vi.inflate(R.layout.listview_item_nobutton, null);
            }

            //위젯 찾기
            TextView txtIndex = (TextView)v.findViewById(R.id.txtIndex);
            TextView txtData1 = (TextView)v.findViewById(R.id.txtData1);
            TextView txtData2 = (TextView)v.findViewById(R.id.txtData2);
            TextView txtData3 = (TextView)v.findViewById(R.id.txtData3);
            TextView txtData4 = (TextView)v.findViewById(R.id.txtData4);
            Button btnEdit = (Button)v.findViewById(R.id.btnEdit);

            //위젯에 데이터를 넣는다.
            dataTest dataItem = m_listItem.get(position);
            txtIndex.setText(dataItem.Index + "");
            txtData1.setText(dataItem.Data1);
            txtData2.setText(dataItem.Data2);
            txtData3.setText(dataItem.Data3);
            txtData4.setText(dataItem.Data4);

            //포지션 입력
            btnEdit.setTag(position);
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LinearLayout itemParent = (LinearLayout)v.getParent();
                    int nPosition = (int) v.getTag();
                    /*Toast.makeText(
                            getApplicationContext(),
                            //"선택된 데이터 : " + ((TextView)itemParent.findViewById(R.id.txtIndex)).getText(),
                            "선택된 데이터 : " + pos,
                            Toast.LENGTH_SHORT
                    ).show();*/

                    AdminLBSActivity.this.RemoveData(nPosition);


                }
            });

            return v;
        }

    }//end class CusromAdapter

    public void  onClick_btnDataAdd_Custom(View v)
    {   //커스텀 Add

        this.adapter.add(new dataTest(this.m_nIndex
                , this.txtData1.getText().toString()
                , this.txtData2.getText().toString()
                , this.txtData3.getText().toString()
                , ""));

        this.adapter.notifyDataSetChanged();
        ++this.m_nIndex;
    }

    public void RemoveData(int nPosition)
    {
        String test = this.listReturn.get(nPosition).Data2;
        Log.d("nPosition" , test);
        this.adapter.remove(this.listReturn.get(nPosition));
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


                        //그리고 첫화면으로 돌아감
                        Intent intent = new Intent(AdminLBSActivity.this, AdminLBSActivity.class);
                        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                        AdminLBSActivity.this.startActivity(intent);

                    }
                    else {
                        Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_SHORT).show();

                        //알림상자를 만들어서 보여줌
                        AlertDialog.Builder builder = new AlertDialog.Builder(AdminLBSActivity.this);
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

        DangerDeleteRequest dangerDeleteRequest = new DangerDeleteRequest(test, responseListener);
        //2. RequestQueue를 생성한다.
        RequestQueue queue = Volley.newRequestQueue(AdminLBSActivity.this);
        //3. RequestQueue에 RequestObject를 넘겨준다.
        queue.add(dangerDeleteRequest);

    }


    class BackgroundTask extends AsyncTask<Void, Void, String> {
        String target;

        @Override
        protected void onPreExecute() {
            //List.php은 파싱으로 가져올 웹페이지
            target = "http://pj9087.dothome.co.kr/DangerArea.php";
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
//            Intent intent = new Intent(LoginActivity.this, PostActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.putExtra("postList", result);//파싱한 값을 넘겨줌
//            LoginActivity.this.startActivity(intent);//Activity로 넘어감
        }
    }
}

