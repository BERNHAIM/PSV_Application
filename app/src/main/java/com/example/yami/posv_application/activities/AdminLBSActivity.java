package com.example.yami.posv_application.activities;

import android.content.Context;
import android.os.Bundle;
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

import com.example.yami.posv_application.R;
import com.example.yami.posv_application.models.dataTest;

import java.util.ArrayList;


public class AdminLBSActivity extends BaseActivity
{
    private EditText txtData1;
    private EditText txtData2;
    private EditText txtData3;
    private Button btnDataAdd;
    private ListView lvTest;

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

        adapter = new CusromAdapter(this, 0, listReturn );
        this.lvTest.setAdapter(adapter);

        this.listReturn.add(new dataTest(0, "a", "b", "c"));
        this.listReturn.add(new dataTest(1, "a1", "b1", "c1"));
        this.listReturn.add(new dataTest(2, "a2", "b2", "c2"));
        this.listReturn.add(new dataTest(3, "a3", "b3", "c3"));
        this.listReturn.add(new dataTest(4, "a4", "b4", "c5"));
        this.listReturn.add(new dataTest(5, "a5", "b5", "c5"));
        this.listReturn.add(new dataTest(6, "a6", "b6", "c6"));
        this.listReturn.add(new dataTest(7, "a7", "b7", "c7"));
        this.adapter.notifyDataSetChanged();
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

    public void  onClick_btnDataAdd_Simple(View v)
    {   //심플 Add
        String[] sData = {"a", "b", "c", "d", "e", "f", "g"};
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1 , sData);
        this.lvTest.setAdapter(adapter);
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
            Button btnEdit = (Button)v.findViewById(R.id.btnEdit);

            //위젯에 데이터를 넣는다.
            dataTest dataItem = m_listItem.get(position);
            txtIndex.setText(dataItem.Index + "");
            txtData1.setText(dataItem.Data1);
            txtData2.setText(dataItem.Data2);
            txtData3.setText(dataItem.Data3);

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
                , this.txtData3.getText().toString()));

        this.adapter.notifyDataSetChanged();
        ++this.m_nIndex;
    }

    public void RemoveData(int nPosition)
    {
        this.adapter.remove(this.listReturn.get(nPosition));
    }
}
