package com.snapit.genia.snapit.adapters;

import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.Fade;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.snapit.genia.snapit.MainActivity;
import com.snapit.genia.snapit.PhotoFullScreenFragment;
import com.snapit.genia.snapit.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Asierae on 21/02/2018.
 */

public class AlbumPhotosAdapter extends RecyclerView.Adapter<AlbumPhotosAdapter.MyViewHolder> {
    private Context mContext;
    public ArrayList<Photo> photoList;
    public ArrayList<Photo> selected_photoList=new ArrayList<>();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView message, count;
        public ImageView thumbnail, overflow;
        public RelativeLayout ll;
        public MyViewHolder(View view) {
            super(view);
            message = (TextView) view.findViewById(R.id.message_photo);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail_photo);
            ll=(RelativeLayout) view.findViewById(R.id.card_alb_photo);
        }
    }


    public AlbumPhotosAdapter(Context mContext, ArrayList<Photo> photoList,ArrayList<Photo> selected) {
        this.mContext = mContext;
        this.photoList = photoList;
        this.selected_photoList=selected;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_albumphoto, parent, false);



        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Photo pic = photoList.get(position);
        String u=pic.getUser();
        String m=pic.getMessage();
        holder.message.setText(m);

        Glide.with(mContext).load(mContext.getResources().getString(R.string.url)+pic.getPath()).thumbnail(0.5f).crossFade().into(holder.thumbnail);

        if(selected_photoList.contains(photoList.get(position)))
            holder.ll.setBackgroundColor(Color.DKGRAY);
        else
            holder.ll.setBackgroundColor(ContextCompat.getColor(mContext, R.color.cardview_light_background));

    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }


    /*private Menu context_menu;
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {


        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.toolbar_delete, menu);
            context_menu = menu;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    };
*/
}
