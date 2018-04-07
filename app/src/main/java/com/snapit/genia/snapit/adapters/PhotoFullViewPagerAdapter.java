package com.snapit.genia.snapit.adapters;

import android.content.Context;
import android.graphics.Matrix;
import android.support.v4.view.PagerAdapter;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.snapit.genia.snapit.ConversationFragment;
import com.snapit.genia.snapit.PhotoFullScreenFragment;
import com.snapit.genia.snapit.R;

import java.util.ArrayList;

/**
 * Created by Asierae on 13/03/2018.
 */

public class PhotoFullViewPagerAdapter extends PagerAdapter{

    private Context context;
    private ArrayList<Photo> photoList;
    private  View rootView;
    private PhotoView iv;

   public PhotoFullViewPagerAdapter(Context context, ArrayList<Photo> photoList, View rootView){
        this.context=context;
        this.photoList=photoList;
        this.rootView=rootView;
    }

    @Override
    public int getCount() {
        return photoList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {


        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        iv=new PhotoView(context);
        Glide.with(context).load(context.getString(R.string.url)+photoList.get(position).getPath()).fitCenter().crossFade().into(iv);
        container.addView(iv);
        return iv;
    }

    @Override
    public void startUpdate(ViewGroup container) {
        super.startUpdate(container);
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        super.finishUpdate(container);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

       container.removeView((View) object);
    }

    public ArrayList<Photo> getPhotoList(){
       return photoList;
    }

}
