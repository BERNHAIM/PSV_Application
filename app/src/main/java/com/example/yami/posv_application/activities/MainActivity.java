package com.example.yami.posv_application.activities;

// TODO 2018.10.28 남은 작업은 익명게시판 디자인과 상담사연결 디자인(크게 그리드레이아웃:세로 2분할)
// TODO 그리고 위젯 기능 

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
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

import com.example.yami.posv_application.BackPressHandler;
import com.example.yami.posv_application.R;
import com.example.yami.posv_application.user_management.LoginActivity;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends BaseActivity {
    EditText searchText;
    TextView searchResult;
    String data;
    String keyword;
    public static StringBuilder sb;
    Button searchBtn;

    private BackPressHandler backPressHandler;

    boolean isPage_flag = false;

    Animation transLeft;
    Animation transRight;

    LinearLayout lay_slide_menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);

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


}
