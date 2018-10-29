package com.example.yami.posv_application.recyclerview.itemholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yami.posv_application.R;
import com.example.yami.posv_application.models.Violence;


public class ViolenceHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final ImageView violenceLogo;
    private final TextView newsSubject;
    private final TextView upTodate;
    private final TextView description;
    private final TextView area;

    private Violence violence;
    private Context context;

    public ViolenceHolder(Context context, View itemView) {

        super(itemView);

        // 1. Set the context
        this.context = context;

        // 2. Inflate the UI widgets of the holder
        this.violenceLogo = (ImageView) itemView.findViewById(R.id.logo);
        this.newsSubject = (TextView) itemView.findViewById(R.id.newsSubject);
        this.upTodate = (TextView) itemView.findViewById(R.id.upTodate);
        this.description = (TextView) itemView.findViewById(R.id.description);
        this.area = (TextView) itemView.findViewById(R.id.area);

        // 3. Set the "onClick" listener of the holder
        itemView.setOnClickListener(this);
    }

    public void bindViolence(Violence violence) {

        // 4. Bind the data to the ViewHolder
        this.violence = violence;
        this.newsSubject.setText(violence.newsSubject);
        this.upTodate.setText(violence.upTodate);
        this.area.setText(violence.area);
        this.description.setText(violence.description);
        this.violenceLogo.setImageBitmap(violence.logo);
    }

    @Override
    public void onClick(View v) {

        // 5. Handle the onClick event for the ViewHolder
        if (this.violence != null) {

            Toast.makeText(this.context, "Clicked on " + this.violence.newsSubject, Toast.LENGTH_SHORT ).show();
        }
    }
}
