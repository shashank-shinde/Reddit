package com.sas_apps.reddit.adaptor;
/*
 * Created by Shashank Shinde.
 */

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sas_apps.reddit.R;
import com.sas_apps.reddit.model.comment.Comment;

import java.util.ArrayList;

public class CommentAdaptor extends ArrayAdapter<Comment> {

    private static final String TAG = "CommentAdaptor";
    private Context context;
    private int resource;
    private int lastPosition = -1;

    public CommentAdaptor(Context context, int resource, ArrayList<Comment> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }
    private static class ViewHolder {
        TextView comment;
        TextView author;
        TextView date;
        ProgressBar progressBar;
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String title = getItem(position).getComment();
        String author = getItem(position).getAuthor();
        String dateUpdated = getItem(position).getDate();
        try{
            View result = null;
            final ViewHolder holder;
            if(convertView == null){
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(resource, parent, false);
                holder= new ViewHolder();
                holder.comment = convertView.findViewById(R.id.text_comment);
                holder.author =  convertView.findViewById(R.id.text_commentAuthor);
                holder.date = convertView.findViewById(R.id.text_commentDate);
                holder.progressBar =  convertView.findViewById(R.id.progressBar_comment);
                result = convertView;
                convertView.setTag(holder);
            }
            else{
                holder = (ViewHolder) convertView.getTag();
                result = convertView;
                holder.progressBar.setVisibility(View.VISIBLE);
            }
            lastPosition = position;
            holder.comment.setText(title);
            holder.author.setText(author);
            holder.date.setText(dateUpdated);
            holder.progressBar.setVisibility(View.GONE);
            return convertView;
        }catch (IllegalArgumentException e){
            Log.e(TAG, "getView: IllegalArgumentException: " + e.getMessage() );
            return convertView;
        }
    }

}