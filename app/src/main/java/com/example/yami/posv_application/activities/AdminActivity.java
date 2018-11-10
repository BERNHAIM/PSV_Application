package com.example.yami.posv_application.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.example.yami.posv_application.R;
import com.example.yami.posv_application.user_management.LoginActivity;
import com.example.yami.posv_application.user_management.RegisterActivity;

public class AdminActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_admin_main);

        Button admin_lbs = (Button) findViewById(R.id.admin_lbs);

        admin_lbs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminActivity.this, AdminLBSActivity.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                AdminActivity.this.startActivity(intent);
            }
        });

    }
}
