package com.example.yami.posv_application.notice_board;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.yami.posv_application.R;

import java.util.List;

public class CommentListAdapter extends BaseAdapter {
    private Context context;
    private List<Comment> commentList;

    @Override
    public int getCount() {
        return commentList.size();
    }

    @Override
    public Object getItem(int position) {
        return commentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(context, R.layout.comment, null);

        //뷰에 다음 컴포넌트들을 연결시켜줌
        TextView userID = v.findViewById(R.id.userID);
        TextView date = v.findViewById(R.id.date);
        TextView comment = v.findViewById(R.id.comment);

        userID.setText(commentList.get(position).getUserID());
        date.setText(commentList.get(position).getTime());
        comment.setText(commentList.get(position).getComment());

        //만든뷰를 반환함
        return v;
    }

    public CommentListAdapter(Context context, List<Comment> commentList){
        this.context = context;
        this.commentList = commentList;
    }
}
