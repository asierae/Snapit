package com.snapit.genia.snapit.adapters;

/**
 * Created by Asierae on 19/03/2018.
 */

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.snapit.genia.snapit.R;
import com.snapit.genia.snapit.UserProfileFragment;
import com.snapit.genia.snapit.utils.ClickListener;
import com.snapit.genia.snapit.utils.PrefManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.MyViewHolder> {

    private PrefManager prefManager;
    private String my_username,mode;
    private ArrayList<User> myUsers;
    private Context context;
    private final ClickListener listener;


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name;
        public TextView bio;
        public Button bFollow;
        public ImageView avatar;
        public RelativeLayout viewBackground, viewForeground;

        public MyViewHolder(View v,ClickListener listener) {
            super(v);
            name=(TextView)v.findViewById(R.id.rv_each_username);
            bio=(TextView)v.findViewById(R.id.rv_each_bio);
            avatar=(ImageView) v.findViewById(R.id.rv_each_avatar);
            viewBackground = v.findViewById(R.id.view_background);
            viewForeground = v.findViewById(R.id.view_foreground);

            viewForeground.setOnClickListener(this);
            if(mode.equals("following")){
            bFollow=v.findViewById(R.id.button_follow);
            bFollow.setOnClickListener(this);}
        }

        @Override
        public void onClick(View view) {

            if (mode.equals("following") && view.getId() == bFollow.getId()) {
                //que lo haga el listener del boton
                //send_follow(myUsers.get(getAdapterPosition()).getUsername(),holder.bFollow);
            } else {
                UserProfileFragment blankFragment2 = new UserProfileFragment();
                Bundle args = new Bundle();
                args.putString("username",my_username);
                args.putString("userprofile",myUsers.get(getAdapterPosition()).getUsername());
                blankFragment2.setArguments(args);
                ((FragmentActivity) view.getContext()).getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.fragment_container,blankFragment2).addToBackStack(null).commit();
            }

            //listenerRef.get().onPositionClicked(getAdapterPosition());
        }
    }


    public FollowersAdapter(ArrayList<User> mList,ClickListener listener) {
        this.myUsers = mList;
        this.listener=listener;
    }
    public FollowersAdapter(Context context, ArrayList<User> messageList,String mode,ClickListener listener) {
        this.context = context;
        this.myUsers = messageList;
        prefManager = new PrefManager(context);
        my_username=prefManager.getUsername();
        this.listener=listener;
        this.mode=mode;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if(mode.equals("followers"))
         v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_follower_recyclerview_each, parent, false);
        else
            v = (View) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.my_following_recyclerview_each, parent, false);
        //Aqui definimos tamaños,margenes,paddings


        return new MyViewHolder(v,listener);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.name.setText(myUsers.get(position).getUsername());
        holder.bio.setText(myUsers.get(position).getBio());
        Glide.with(context).load(context.getResources().getString(R.string.url)+myUsers.get(position).getAvatar()).thumbnail(0.5f).crossFade().into(holder.avatar);
        if(mode.equals("following")){
        holder.bFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mode.equals("followers"))
                send_follow_unfollow(myUsers.get(position).getUsername(),holder.bFollow);
                else{
                    //dialogog are you sure you wanna break with?
                    final View mView = LayoutInflater.from(context).inflate(R.layout.dialog_unfollow, null);
                    ImageView avatarmini=mView.findViewById(R.id.dialog_avatar);
                    TextView name=mView.findViewById(R.id.dialog_unfollow);
                    name.setText(name.getText()+" "+myUsers.get(position).getUsername());
                    Glide.with(context).load(context.getResources().getString(R.string.url)+myUsers.get(position).getAvatar()).thumbnail(0.5f).crossFade().into(avatarmini);
                    final AlertDialog dialog=new AlertDialog.Builder(mView.getContext()).setView(mView)
                            .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    send_follow_unfollow(myUsers.get(position).getUsername(),holder.bFollow);
                                }
                            }).setNegativeButton(R.string.cancel, null).show();
                }
            }
        });
        }
    }

    @Override
    public int getItemCount() {
        return myUsers.size();
    }

    public void removeItem(int position) {
        myUsers.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(User item, int position) {
        myUsers.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }

    public void filter(ArrayList<User> filtrados) {
        myUsers=filtrados;
        notifyDataSetChanged();
    }
    private void send_follow_unfollow(final String followto, final Button b){
        //Volley request and fit the lista
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        // Request a string response from the provided URL.
        StringRequest req = new StringRequest(Request.Method.POST, context.getString(R.string.url) + "/GesUsers.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Myresponse", "-- " + response);
                        if(response.equals("OK")){
                            b.setVisibility(View.GONE);
                        }
                        else if(response.equals("REP")){
                            Toast.makeText(context, "Following Already", Toast.LENGTH_SHORT).show();
                            b.setVisibility(View.GONE);
                        }
                        else if(response.equals("OKD")){
                            Toast.makeText(context, "Unfollowed!", Toast.LENGTH_SHORT).show();
                            b.setVisibility(View.GONE);
                        }
                        else{
                            Toast.makeText(context, "AQUI1 ? : "+response, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Mysmart", "-- " + error.toString());
                Toast.makeText(context, "AQUI2" + error, Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("username", my_username);
                if(mode.equals("followers")){
                param.put("following", followto);
                param.put("op", "addf");
                }
                else{
                    param.put("following", followto);
                    param.put("op", "delfing");
                }

                //param.put("user",user);
                return param;
            }

        };

        requestQueue.add(req);
    }
}
