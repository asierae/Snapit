package com.snapit.genia.snapit;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.snapit.genia.snapit.utils.PrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static int RESULT_LOAD_IMAGE_BG = 102;
    private static int RESULT_LOAD_IMAGE_AVATAR = 101;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private PrefManager prefManager;
    private String my_username;
    private ImageView bg,avatar;
    private TextView user,followers,following;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout laybuttons;
    private ImageButton button_edit_bg,button_ok,button_cancel;
    private Drawable bitmaptmp;
    private ImageButton button_edit_avatar;
    private String mode;
    private SwipeRefreshLayout refresh;
    private TextView tv_profile_bio;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        prefManager = new PrefManager(getContext());
        my_username=prefManager.getUsername();
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_profile, container, false);
        refresh = (SwipeRefreshLayout) rootView.findViewById(R.id.layout_root_profile);
        refresh.setRefreshing(true);
         bg=(ImageView)rootView.findViewById(R.id.profile_background);
         avatar=(ImageView)rootView.findViewById(R.id.avatar_profile);
          button_edit_bg=(ImageButton)rootView.findViewById(R.id.button_edit_bg);
        button_edit_avatar=(ImageButton)rootView.findViewById(R.id.button_edit_avatar);
        laybuttons=(LinearLayout)rootView.findViewById(R.id.buttons_edit);
        button_ok=(ImageButton)rootView.findViewById(R.id.button_edit_ok);
        button_cancel=(ImageButton)rootView.findViewById(R.id.button_edit_cancel);
         user=(TextView)rootView.findViewById(R.id.username_profile);
         followers=(TextView)rootView.findViewById(R.id.tv_profile_followers);
         following=(TextView)rootView.findViewById(R.id.tv_profile_following);
        tv_profile_bio=(TextView)rootView.findViewById(R.id.tv_profile_bio);
        CardView cvFollowers=(CardView)rootView.findViewById(R.id.cV_followers);
        CardView cvFollowing=(CardView)rootView.findViewById(R.id.cV_following);
         user.setText(my_username);
         button_cancel.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 laybuttons.setVisibility(View.INVISIBLE);
                 button_edit_bg.setVisibility(View.VISIBLE);
                 //recuperar la imagen de antes en ek imageview
                 if(mode.equals("background"))
                 bg.setImageDrawable(bitmaptmp);
                 else
                     avatar.setImageDrawable(bitmaptmp);
             }
         });
         button_ok.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Toast.makeText(getContext(), "Guardando...", Toast.LENGTH_SHORT).show();
                 laybuttons.setVisibility(View.INVISIBLE);
                 button_edit_bg.setVisibility(View.VISIBLE);
                 if(mode.equals("background"))
                 update_profile_image("bg");
                 else
                     update_profile_image("avatar");
             }
         });
        cvFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FollowersFragment blankFragment2 = new FollowersFragment();
                Bundle args = new Bundle();
                args.putString("username",my_username);
                args.putString("mode","followers");
                blankFragment2.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.fragment_container, blankFragment2).addToBackStack(null).commit();

            }
        });
        cvFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FollowersFragment blankFragment2 = new FollowersFragment();
                Bundle args = new Bundle();
                args.putString("username",my_username);
                args.putString("mode","following");
                blankFragment2.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.fragment_container, blankFragment2).addToBackStack(null).commit();

            }
        });
        button_edit_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_background();
            }
        });
        button_edit_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_avatar();
            }
        });
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.layout_root_profile);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                load_profile();
                //swipeRefreshLayout.stopNestedScroll();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, R.color.colorAccent, R.color.colorPrimaryDark);

        //load_user_profile
         load_profile();

        return rootView;
    }

    private void load_profile(){
        //Volley request and fit the lista
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        // Request a string response from the provided URL.
        StringRequest req = new StringRequest(Request.Method.POST, getString(R.string.url) + "/GesUsers.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Myresponse", "-- " + response);
                        //Toast.makeText(getContext(), "AQUI1"+response, Toast.LENGTH_SHORT).show();
                        try {
                            JSONArray jsonresp = new JSONArray(response);
                            String avatarpath="/uploads/avatar.png";
                            String bgpath="/uploads/b1.png";
                            String foll="0";
                            String folling="0";
                            String bio="";
                            for (int i = 0; i < jsonresp.length(); i++) {
                                // Get current json object
                                JSONObject profile = jsonresp.getJSONObject(i);
                                // Get the current student (json object) data
                                 avatarpath = profile.getString("avatar");
                                 bgpath = profile.getString("background");
                                 foll = profile.getString("followers");
                                 folling = profile.getString("following");
                                 bio = profile.getString("bio");
                            }
                            //Load data response
                            followers.setText(foll+ " Lovers");
                            following.setText(folling+ " Loving");
                            Glide.with(getContext()).load(getContext().getResources().getString(R.string.url)+avatarpath).thumbnail(0.5f).crossFade().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE) .into(avatar);
                            Glide.with(getContext()).load(getContext().getResources().getString(R.string.url)+bgpath).thumbnail(0.5f).crossFade().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(bg);
                            tv_profile_bio.setText(bio.toString());
                            if(swipeRefreshLayout.isRefreshing()){swipeRefreshLayout.setRefreshing(false);}
                            refresh.setRefreshing(false);
                        } catch (JSONException e) {
                            e.printStackTrace();
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
                param.put("op", "get");
                return param;
            }
        };

        requestQueue.add(req);
    }
    private void update_profile_image(final String av_or_bg){
        //Volley request and fit the lista
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        // Request a string response from the provided URL.
        StringRequest req = new StringRequest(Request.Method.POST, getString(R.string.url) + "/GesUsers.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Myresponse", "-- " + response);
                        Toast.makeText(getContext(), "AQUI1" + response, Toast.LENGTH_SHORT).show();
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
                param.put("op", "updateprofile");
                String image="";
                if(av_or_bg.equals("bg")){
                param.put("op2", "background");
                 image = getStringImage(((BitmapDrawable)bg.getDrawable()).getBitmap());
                }else{
                    param.put("op2", "avatar");
                     image = getStringImage(((BitmapDrawable)avatar.getDrawable()).getBitmap());
                }
                param.put("image", image);
                return param;
            }
        };

        requestQueue.add(req);
    }
    public String getStringImage(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,75, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }
    private void edit_background(){
        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE_BG);
    }
    private void edit_avatar(){
        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE_AVATAR);
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == RESULT_LOAD_IMAGE_BG || requestCode==RESULT_LOAD_IMAGE_AVATAR) && resultCode == RESULT_OK && null != data) {
            button_edit_bg.setVisibility(View.INVISIBLE);
            laybuttons.setVisibility(View.VISIBLE);

            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            if(requestCode==RESULT_LOAD_IMAGE_BG){
                //EDIT BG
                mode="background";
            bitmaptmp = bg.getDrawable();
            bg.setImageBitmap(BitmapFactory.decodeFile(picturePath));}
            else{//EDIT AVATAR
                mode="avatar";
                bitmaptmp = avatar.getDrawable();
                avatar.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            }
        }

    }


}
