/* TO DO LIST
* 전체적인 레이아웃 수정
* 카카오톡 플러스친구 (자동응답(챗봇), 1대1 채팅-디자인 ) 추가해야함
* */

package com.example.yami.posv_application.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.yami.posv_application.R;
import com.kakao.plusfriend.PlusFriendService;
import com.kakao.util.exception.KakaoException;

public class COUNSELOR_Activity extends AppCompatActivity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.counselor_main);
    }

    /** 전화 연결 버튼 **/
    public void btn_Dial_To_Counselor(View view){
        /*권한 체크*/
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        if(permissionCheck == PackageManager.PERMISSION_DENIED){
            //No Permission
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CALL_PHONE},0);
        } else {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:1388"));
            try {
                startActivity(intent);
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public void btn_Kakao_To_Counselor(View view){
        try {
            PlusFriendService.getInstance().chat(COUNSELOR_Activity.this, "_wefuj");
        } catch (KakaoException e) {
            // 에러 처리 (앱키 미설정 등등)
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }
    /** 권한 승인 요청 **/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if(requestCode == 0){
            if(grantResults[0] == 0){
                Toast.makeText(this,"전화 다이얼 권한 승인",Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(this,"권한이 거절되었습니다. 권한을 승인해주세요.",Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);

        finish();
    }

}
