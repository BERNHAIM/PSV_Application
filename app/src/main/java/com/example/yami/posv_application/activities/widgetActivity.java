package com.example.yami.posv_application.activities;

import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.yami.posv_application.R;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class widgetActivity extends BaseActivity{

    EditText textPhoneNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StringBuffer buffer = new StringBuffer();
        String data = null;
        FileInputStream fis = null;

        ArrayList<String> smslist = new ArrayList<String>();

        textPhoneNo = (EditText) findViewById(R.id.editTextPhoneNo);

        //입력한 값을 가져와 변수에 담는다
        //String phoneNo = textPhoneNo.getText().toString();
        //String phoneNo = "01041525946";

        String sms = "제가 지금 위험해요!\n도와주세요!";
        // String sms = textSMS.getText().toString();

        try {
            fis = openFileInput("internal.txt");

            SmsManager smsManager = SmsManager.getDefault();

            BufferedReader iReader = new BufferedReader(new InputStreamReader((fis)));
            data = iReader.readLine();
            while (data != null) {
                buffer.append(data);
                data = iReader.readLine();
                smslist.add(data+"\n");
            }

            System.out.print("smsLIst: "+ smslist);

            for(int i = 0; i < smslist.size(); i++){
                smsManager.sendTextMessage(smslist.get(i), null, sms, null, null);
            }
            Toast.makeText(getApplicationContext(), "전송 완료!", Toast.LENGTH_LONG).show();

            System.out.print(smslist);
            iReader.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "SMS faild, please try again later!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
