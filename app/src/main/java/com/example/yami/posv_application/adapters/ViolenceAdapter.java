package com.example.yami.posv_application.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yami.posv_application.R;
import com.example.yami.posv_application.activities.BaseActivity;
import com.example.yami.posv_application.models.Violence;

import java.util.List;


public class ViolenceAdapter extends ArrayAdapter<Violence> {

    private final List<Violence> examples;
    private Context context;
    private int itemResource;

    public ViolenceAdapter(Context context, int itemResource, List<Violence> examples) {

        // 1. Initialize our adapter
        super(context, R.layout.list_item_violence, examples);
        this.examples = examples;
        this.context = context;
        this.itemResource = itemResource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 2. Have we inflated this view before?
        View itemView;
        if (convertView != null) {

            // 2a. We have so let's reuse.
            itemView = convertView;
        }
        else {

            // 2b. We have NOT so let's inflate
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = inflater.inflate(this.itemResource, parent, false);
        }




        // 3. Get the bakery to appear in this item
        Violence violence = this.examples.get(position);
        if (violence != null) {

            // 4. Inflate the UI widgets

            TextView newsSubject = (TextView) itemView.findViewById(R.id.newsSubject);
            TextView violence_description = (TextView) itemView.findViewById(R.id.description);
            TextView area = (TextView) itemView.findViewById(R.id.area);
            TextView upTodate = (TextView) itemView.findViewById(R.id.upTodate);

            // 5. Set the UI widgets with appropriate data from the Bakery model
            newsSubject.setText(violence.newsSubject);
            violence_description.setText(violence.description);
            upTodate.setText(violence.upTodate);
            area.setText(violence.area);
        }

        return itemView;
    }
}