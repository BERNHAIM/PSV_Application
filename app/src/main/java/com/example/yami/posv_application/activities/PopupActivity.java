package com.example.yami.posv_application.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.example.yami.posv_application.R;

import org.w3c.dom.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PopupActivity extends BaseActivity2 {
    TextView txtText;
    TextView titleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_activity);

        String add_title_array="";

        //UI 객체생성
        txtText = (TextView)findViewById(R.id.txtText);
        titleText = (TextView)findViewById(R.id.titleText);

        //데이터 가져오기
        Intent intent = getIntent();
        String data[] = intent.getExtras().getStringArray("data");

        if(data[0] == null){
            System.out.println("null값이야");
            titleText.setText("앗!");
            add_title_array="결과가 존재하지 않아요ㅠㅠ";

        } else {
            for(int i=0;i<data.length;i++){
                if(i < data.length)
                    add_title_array+=data[i]+"\n\n";
                else add_title_array+=data[i]+"\n";
            }
            titleText.setText("이런 상담센터가 있어요!");
        }
        txtText.setText(add_title_array);
    }

    //확인 버튼 클릭
    public void mOnClose(View v){
        //데이터 전달하기
        Intent intent = new Intent();
        intent.putExtra("result", "Close Popup");
        setResult(RESULT_OK, intent);

        //액티비티(팝업) 닫기
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }
}
