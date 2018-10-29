package com.example.yami.posv_application.notice_board;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.yami.posv_application.R;

import java.util.List;

/**
 * Created by kch on 2018. 2. 17..
 */

public class PostListAdapter extends BaseAdapter {

    private Context context;
    private List<Post> postList;
    private List<Post> saveList;

    public PostListAdapter(Context context, List<Post> postList, List<Post> saveList){
        this.context = context;
        this.postList = postList;
        this.saveList = saveList;
    }

    //출력할 총갯수를 설정하는 메소드
    @Override
    public int getCount() {
        return postList.size();
    }

    //특정한 유저를 반환하는 메소드
    @Override
    public Object getItem(int i) {
        return postList.get(i);
    }

    //아이템별 아이디를 반환하는 메소드
    @Override
    public long getItemId(int i) {
        return i;
    }

    //가장 중요한 부분
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = View.inflate(context, R.layout.post, null);

        //뷰에 다음 컴포넌트들을 연결시켜줌
        TextView userID = (TextView)v.findViewById(R.id.userID);
        TextView postName = (TextView)v.findViewById(R.id.postName);
        TextView date = (TextView)v.findViewById(R.id.date);

        postName.setText(postList.get(i).getPostName());
        userID.setText(postList.get(i).getUserID());
        date.setText(postList.get(i).getCurrentTime());

        //만든뷰를 반환함
        return v;
    }
}
