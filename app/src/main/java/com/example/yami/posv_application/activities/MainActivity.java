package com.example.yami.posv_application.activities;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.yami.posv_application.BackPressHandler;
import com.example.yami.posv_application.R;
import com.example.yami.posv_application.notice_board.PostActivity;
import com.example.yami.posv_application.user_management.AutoLoginUnRequest;
import com.example.yami.posv_application.user_management.LoginActivity;
import com.example.yami.posv_application.user_management.SessionManager;
import com.example.yami.posv_application.utilities.lbsService;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

public class MainActivity extends BaseActivity {
    EditText searchText;
    TextView searchResult;
    String data;
    String keyword;
    public static StringBuilder sb;
    Button searchBtn;
    SessionManager session;

    private BackPressHandler backPressHandler;

    boolean isPage_flag = false;

    Animation transLeft;
    Animation transRight;

    LinearLayout lay_slide_menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new SessionManager(getApplicationContext());

        searchText = (EditText) findViewById(R.id.searchText);
        searchBtn = (Button) findViewById(R.id.searchBtn);

        // 뒤로가기 핸들러
        backPressHandler = new BackPressHandler(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        searchBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                keyword = searchText.getText().toString();

                new Thread() {
                    public void run() {
                        String newsHTML = getSearch(keyword);

                        Bundle bun = new Bundle();
                        bun.putString("HTML_DATA", newsHTML);

                        Message msg = handler.obtainMessage();
                        msg.setData(bun);
                        handler.sendMessage(msg);

                    }
                }.start();
            }

            @SuppressLint("HandlerLeak")
            Handler handler = new Handler() {
                public void handleMessage(Message msg) {
                    Bundle bun = msg.getData();
                    String naverHtml = bun.getString("HTML_DATA");


                }
            };

        });

    }


    /**
     * LBS 메뉴
     **/
    private lbsService mService;
    private boolean isBind;

    public void btn_LBS_menu(View v) {
        Intent intent = new Intent(getApplicationContext(), LBS_Activity.class);
        startActivity(intent);


        finish();

    }

    /**
     * 상담사 연결 메뉴
     **/
    public void btn_COUNSELOR_menu(View v) {
        Intent intent = new Intent(getApplicationContext(), COUNSELOR_Activity.class);
        startActivity(intent);

        finish();
    }

    public void btn_Violence_menu(View v) {
        Intent intent = new Intent(getApplicationContext(), ViolenceListingsActivity.class);
        startActivity(intent);

        finish();
    }

    //익명게시판 버튼
    public void btn_BOARD_menu(View v) {
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
                            Intent intent = new Intent(MainActivity.this, PostActivity.class);
                            String postList = new BackgroundTask().execute().get();
                            intent.putExtra("postList", postList);
                            intent.putExtra("u_num", userNum);
                            startActivity(intent);
                            Log.e("un = ", userNum);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            MainActivity.this.startActivity(intent);
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
            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
            queue.add(loginRequest);
        }
        //startActivity(intent);
        finish();
    }

    //뒤로가기 버튼 활성화
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backPressHandler.onBackPressed();
    }

    public void btn_clicked(View v) {
        if (isPage_flag) {
            lay_slide_menu.startAnimation(transRight);
        } else {
            lay_slide_menu.setVisibility(View.VISIBLE);
            lay_slide_menu.startAnimation(transLeft);
        }
    }

    private class SlidingPageAnimationListener implements Animation.AnimationListener {
        public void onAnimationEnd(Animation animation) {
            if (isPage_flag) {
                lay_slide_menu.setVisibility(View.INVISIBLE);

                isPage_flag = false;
            } else {

                isPage_flag = true;
            }
        }


        public void onAnimationRepeat(Animation animation) {

        }

        public void onAnimationStart(Animation animation) {

        }


    }


    public String getSearch(String keyword) {
        String clientID = "3PjuqLjCXPHbcnS4Bluj";
        String clientSecret = "5mmpOiirt5";
        int display = 5;

        try {
            String text = URLEncoder.encode(keyword, "utf-8");
            String unChangeText = URLEncoder.encode(" 청소년상담센터","utf-8");
            String apiURL = "https://openapi.naver.com/v1/search/local.json?query=" + text + unChangeText+"&display=" + display + "&sort=random";

            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-Naver-Client-Id", clientID);
            con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
            con.disconnect();

            //슬라이싱 부분
            String target = "<b>";
            String target2 = "</b>";
            String data = sb.toString();
            String[] array;
            array = data.split("\"");
            String[] title = new String[display];
            String[] link = new String[display];
            String[] category = new String[display];
            String[] description = new String[display];
            String[] telephone = new String[display];
            String[] address = new String[display];
            String[] mapx = new String[display];
            String[] mapy = new String[display];

            int k = 0;


            for (int i = 0; i < array.length; i++) {
                if (array[i].equals("title"))
                    title[k] = array[i + 2];
                if (array[i].equals("link"))
                    link[k] = array[i + 2];
                if (array[i].equals("category"))
                    category[k] = array[i + 2];
                if (array[i].equals("description"))
                    description[k] = array[i + 2];
                if (array[i].equals("telephone"))
                    telephone[k] = array[i + 2];
                if (array[i].equals("address"))
                    address[k] = array[i + 2];
                if (array[i].equals("mapx"))
                    mapx[k] = array[i + 2];
                if (array[i].equals("mapy")) {
                    mapy[k] = array[i + 2];
                    k++;
                }
            }
            System.out.println(sb);
            System.out.println("----------------------------");

            String titleArray[] = new String[display];

            for (int i = 0; i < display; i++) {
                if(title[i] == null) break;
                else {
                    titleArray[i] = title[i].replace(target, " ").replace(target2, "")
                            + "\n" + link[i];
                    System.out.println(titleArray[i]);
                }
            }

            /* POPUP 창으로 데이터 보내는 부분*/
            Intent intent = new Intent(MainActivity.this, PopupActivity.class);
            intent.putExtra("data", titleArray);

            //데이터 보내기
            startActivityForResult(intent, 1);

        } catch (Exception e) {
            System.out.println(e);
        }

        return sb.toString();
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
