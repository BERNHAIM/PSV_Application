package com.example.yami.posv_application.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yami.posv_application.R;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

public class SimpleActivity extends BaseActivity {
    /**
     * Called when the activity is first created.
     */

    Button addButton, roadButton;
    EditText editTextPhoneNo;


    public String data1 = "";
    public StringBuffer data2 = new StringBuffer();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.widget_sms_layout);

        addButton = (Button) findViewById(R.id.addButton);
        roadButton = (Button) findViewById(R.id.roadButton);

        editTextPhoneNo = (EditText) findViewById(R.id.editTextPhoneNo);


        addButton.setOnClickListener(new View.OnClickListener() {
            FileOutputStream fos = null;
            @Override
            public void onClick(View view) {
                String inputData = editTextPhoneNo.getText().toString()+"\n";
                Log.d("input : ",inputData);
                try {
                    fos = openFileOutput("internal.txt", Context.MODE_APPEND);

                    fos.write(inputData.getBytes());
                    fos.close();

                    InputMethodManager immhide = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    immhide.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    editTextPhoneNo.setText(null);

                    Toast.makeText(getApplicationContext(),"번호가 등록되었습니다.", Toast.LENGTH_LONG);



                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        roadButton.setOnClickListener(new View.OnClickListener() {

            String data = null;
            FileInputStream fis = null;

            TextView savedNumber = (TextView)findViewById(R.id.savedNumber);

            @Override
            public void onClick(View view) {
                try {
                    StringBuffer buffer = new StringBuffer();
                    fis = openFileInput("internal.txt");
                    BufferedReader iReader = new BufferedReader(new InputStreamReader((fis)));

                    data = iReader.readLine();
                    while (data != null) {
                        buffer.append(data+"\n");
                        data = iReader.readLine();
                    }
                    savedNumber.setText(buffer.toString());
                    iReader.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
