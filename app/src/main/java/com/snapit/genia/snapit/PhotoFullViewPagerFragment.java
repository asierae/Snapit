package com.snapit.genia.snapit;

import android.Manifest;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.snapit.genia.snapit.adapters.Photo;
import com.snapit.genia.snapit.adapters.PhotoFullViewPagerAdapter;
import com.snapit.genia.snapit.utils.PhotoListSerializable;
import com.snapit.genia.snapit.utils.PrefManager;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PhotoFullViewPagerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PhotoFullViewPagerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PhotoFullViewPagerFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String[] images;
    private PhotoListSerializable photoListS;
    private ArrayList<Photo> photoList;

    private OnFragmentInteractionListener mListener;
    private int pos;
    private PrefManager prefManager;
    private String my_username;
    private String id_album,user2;
    private ViewPager viewPager;
    private PhotoFullViewPagerAdapter viewPagerAdapter;

    public PhotoFullViewPagerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PhotoFullViewPagerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PhotoFullViewPagerFragment newInstance(String param1, String param2) {
        PhotoFullViewPagerFragment fragment = new PhotoFullViewPagerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            photoListS= ((PhotoListSerializable) getArguments().getSerializable("photoList"));
            photoList=photoListS.getPhotoList();
            pos=getArguments().getInt("pos");
            id_album=getArguments().getString("id_album");
            user2=getArguments().getString("user2");
        }
        setHasOptionsMenu(true);
        prefManager = new PrefManager(getContext());
        my_username=prefManager.getUsername();

       //((MainActivity)getActivity()).swipe_toolbar();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_photo_full_view_pager, container, false);

        viewPager=(ViewPager)rootView.findViewById(R.id.viewPager_photofull);
        TextView text_date=(TextView) rootView.findViewById(R.id.tv_viewpager);
        viewPagerAdapter=new PhotoFullViewPagerAdapter(getContext(),photoList,rootView);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(pos);
        show_image_data(pos,rootView);
        ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                show_image_data(position,getView());
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        };
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    public void show_image_data(int position,View v){
        TextView tv2=(TextView)v.findViewById(R.id.tv_viewpager);
        TextView tvm=(TextView)v.findViewById(R.id.tv_message_photofull);
        LinearLayout ll=(LinearLayout) v.findViewById(R.id.ll_message_photofull);
        ll.setVisibility(View.VISIBLE);
        AlphaAnimation fadeIn=new AlphaAnimation(0.0f,1.0f);
        tv2.startAnimation(fadeIn);
        tvm.startAnimation(fadeIn);
        fadeIn.setDuration(500);
        fadeIn.setFillAfter(true);
        tv2.setText(photoList.get(position).getDate());
        tv2.bringToFront();
        if(photoList.get(position).getMessage().length()>0) {
            tvm.setText(photoList.get(position).getMessage());
            tvm.bringToFront();}
                else{
            ll.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AlbumPhotosFragment.OnFragmentInteractionListener) {
            mListener = (PhotoFullViewPagerFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(((MainActivity)getActivity()).getSecret().equals("1")){
            inflater.inflate(R.menu.toolbar_hide_mode, menu);
        }
        else {
            inflater.inflate(R.menu.toolbar_share_delete_snap, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        int xxxx=item.getItemId();
        switch (item.getItemId()) {
            case R.id.action_send_snap:
                SendPhotoFragment blankFragment2 = new SendPhotoFragment();
                Bundle args = new Bundle();
                args.putString("user",my_username);
                args.putString("id_album", id_album);
                args.putString("user2", user2);
                blankFragment2.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.fragment_container,blankFragment2).addToBackStack(null).commit();
                return true;
            case R.id.action_delete_photo:
                final Photo photo=photoList.get(viewPager.getCurrentItem());

                final AlertDialog dialog=new AlertDialog.Builder(getContext())
                        .setTitle(getString(R.string.dialog_delete)).setIcon(R.drawable.ic_delete_black)
                        .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                delete_photo(photo);
                            }
                        }).setNegativeButton(R.string.cancel, null).show();
                return true;
            case R.id.action_share:
                if(hasPermissions()){
                View x = viewPager.getChildAt(1);
                Bitmap bitmap = getBitmapFromView(viewPager.getChildAt(0));
                File root = Environment.getExternalStorageDirectory();
                File cachePath = new File(root.getAbsolutePath() + "/DCIM/Camera/image01random.jpg");
                try {
                    cachePath.createNewFile();
                    FileOutputStream ostream = new FileOutputStream(cachePath);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                    ostream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/*");
                share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(cachePath));
                startActivity(Intent.createChooser(share,"Share via"));
                }
                return true;
            case R.id.action_delete:

                return true;
            case R.id.action_exit_hide_mode:
                final AlertDialog dialog2=new AlertDialog.Builder(getContext()).setTitle(getString(R.string.exithide))
                        .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                AlbumFragment blankFragment3 = new AlbumFragment();
                                Bundle args3 = new Bundle();
                                args3.putString("user",my_username);
                                ((MainActivity)getActivity()).setSecret("0");
                                blankFragment3.setArguments(args3);
                                getActivity().getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.fragment_container,blankFragment3).addToBackStack(null).commit();
                            }
                        }).setNegativeButton(R.string.cancel, null).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

private void delete_photo(final Photo photo){
    //Volley request and fit the lista
    RequestQueue requestQueue = Volley.newRequestQueue(getContext());
    // Request a string response from the provided URL.
    StringRequest req = new StringRequest(Request.Method.POST, getString(R.string.url) + "/GesPhotos.php",
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("Myresponse", "-- " + response);
                    photoList.remove(photo);
                    if(photoList.size()>0){
                    viewPagerAdapter.getPhotoList().remove(photo);
                    viewPager.setAdapter(viewPagerAdapter);}
                    else{
                        AlbumPhotosFragment blankFragment2 = new AlbumPhotosFragment();
                        Bundle args = new Bundle();
                        args.putString("username",user2);
                        args.putString("numpics","");
                        args.putString("id_album",id_album);
                        blankFragment2.setArguments(args);
                        getActivity().getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.fragment_container, blankFragment2).addToBackStack(null).commit();

                    }
                }
            }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.i("Mysmart", "-- " + error.toString());
            Toast.makeText(getContext(), "AQUI2" + error, Toast.LENGTH_SHORT).show();

        }
    }) {
        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            Map<String, String> param = new HashMap<>();
            param.put("username", my_username);
            param.put("deletes", String.valueOf(photo.getId()));
            param.put("id_album", id_album);
            param.put("op", "delete");
            return param;
        }
    };

    requestQueue.add(req);
}
public boolean hasPermissions() {
    if (ContextCompat.checkSelfPermission(getActivity(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {

        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Show an expanation to the user *asynchronously* -- don't block
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    101);
        } else {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    101);//101 el codigo inventado que recoge en main onpermisionrequest
        }
    }
    else
        return true;
    return false;
}

    public static Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            // if we unable to get background drawable then we will set white color as wallpaper
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnedBitmap;
    }
}
