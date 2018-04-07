package com.snapit.genia.snapit.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snapit.genia.snapit.R;

import java.util.ArrayList;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MyViewHolder> {

    private ArrayList<Message> myMessages;
    private Context context;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView message;
        public ImageView avatar;
        public RelativeLayout viewBackground, viewForeground;

        public MyViewHolder(View v) {
            super(v);
            name=(TextView)v.findViewById(R.id.name);
            message=(TextView)v.findViewById(R.id.message);
            avatar=(ImageView) v.findViewById(R.id.avatar);
            viewBackground = v.findViewById(R.id.view_background);
            viewForeground = v.findViewById(R.id.view_foreground);
        }
    }


    public MessagesAdapter(ArrayList<Message> mList) {
        this.myMessages = mList;
    }
    public MessagesAdapter(Context context, ArrayList<Message> messageList) {
        this.context = context;
        this.myMessages = messageList;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_message_each, parent, false);

        //Aqui definimos tamaños,margenes,paddings


        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
       //Movie movie = moviesList.get(position);
        //holder.title.setText(movie.getTitle());
        //holder.genre.setText(movie.getGenre());
        //holder.year.setText(movie.getYear());
        holder.name.setText(myMessages.get(position).getUser().getName());
        holder.message.setText(myMessages.get(position).getMessage());
        holder.avatar.setImageResource(R.drawable.ic_launcher_background);//myMessages.get(position).getUser().getAvatar());
    }

    @Override
    public int getItemCount() {
        return myMessages.size();
    }

    public void removeItem(int position) {
        myMessages.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(Message item, int position) {
        myMessages.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }

    public void filter(ArrayList<Message> filtrados) {
        myMessages=filtrados;
        notifyDataSetChanged();
    }
}
