package com.snapit.genia.snapit;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private String my_username,userprofile;
    private ImageView bg,avatar;
    private TextView followers,following,user;
    private FloatingActionButton floating_follow,floating_unfollow;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserProfileFragment newInstance(String param1, String param2) {
        UserProfileFragment fragment = new UserProfileFragment();
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
            my_username = getArguments().getString("username");
            userprofile = getArguments().getString("userprofile");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_user_profile, container, false);
        bg=(ImageView)rootView.findViewById(R.id.iv_userprofile_background);
        avatar=(ImageView)rootView.findViewById(R.id.iv_user_avatar_profile);
        user=(TextView)rootView.findViewById(R.id.tv_user_username_profile);
        followers=(TextView)rootView.findViewById(R.id.tv_user_profile_followers);
        following=(TextView)rootView.findViewById(R.id.tv_user_profile_following);
        CardView cvFollowers=(CardView)rootView.findViewById(R.id.cV_user_followers);
        CardView cvFollowing=(CardView)rootView.findViewById(R.id.cV_user_following);
        floating_follow=(FloatingActionButton)rootView.findViewById(R.id.floating_follow);
        floating_unfollow=(FloatingActionButton)rootView.findViewById(R.id.floating_unfollow);
        floating_unfollow.hide();//mirar cual deberia oculat
        floating_unfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final View mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_unfollow, null);
                ImageView avatarmini=mView.findViewById(R.id.dialog_avatar);
                TextView name=mView.findViewById(R.id.dialog_unfollow);
                name.setText(name.getText()+" "+userprofile + "?");
                avatarmini.setImageDrawable(avatar.getDrawable());
                final AlertDialog dialog=new AlertDialog.Builder(mView.getContext()).setView(mView)
                        .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                follow_unfollow(0);
                            }
                        }).setNegativeButton(R.string.cancel, null).show();
            }
        });
        floating_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                follow_unfollow(1);
            }
        });
        user.setText(userprofile);
        cvFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FollowersFragment blankFragment2 = new FollowersFragment();
                Bundle args = new Bundle();
                args.putString("username",userprofile);
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
                args.putString("username",userprofile);
                args.putString("mode","following");
                blankFragment2.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.fragment_container, blankFragment2).addToBackStack(null).commit();

            }
        });
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
                            JSONArray userprofile=jsonresp.getJSONArray(0);
                            JSONArray f=jsonresp.getJSONArray(1);
                            int fo= (int) f.get(0);
                            String avatarpath="/uploads/avatar.png";
                            String bgpath="/uploads/b1.png";
                            String foll="0";
                            String folling="0";
                            for (int i = 0; i < userprofile.length(); i++) {
                                // Get current json object
                                JSONObject profile = userprofile.getJSONObject(i);
                                // Get the current student (json object) data
                                avatarpath = profile.getString("avatar");
                                bgpath = profile.getString("background");
                                foll = profile.getString("followers");
                                folling = profile.getString("following");
                            }
                            //Load data response
                            followers.setText(foll+ " Lovers");
                            following.setText(folling+ " Loving");
                            Glide.with(getContext()).load(getContext().getResources().getString(R.string.url)+avatarpath).thumbnail(0.5f).crossFade().into(avatar);
                            Glide.with(getContext()).load(getContext().getResources().getString(R.string.url)+bgpath).thumbnail(0.5f).crossFade().into(bg);
                            if(fo==1)
                                floating_unfollow.show();
                            else
                                floating_follow.show();

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
                param.put("userprofile", userprofile);
                param.put("op", "getprofile");
                return param;
            }
        };

        requestQueue.add(req);
    }

    private void follow_unfollow(final int mode){
        //Volley request and fit the lista
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        // Request a string response from the provided URL.
        StringRequest req = new StringRequest(Request.Method.POST, getString(R.string.url) + "/GesUsers.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Myresponse", "-- " + response);
                        Toast.makeText(getContext(), "AQUI1"+response, Toast.LENGTH_SHORT).show();
                        if(floating_follow.isShown()){
                            floating_follow.hide();
                            floating_unfollow.show();
                        }
                        else{
                            floating_unfollow.hide();
                            floating_follow.show();
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
                param.put("following", userprofile);
                //param.put("op", "delf");
                if(mode==0)
                param.put("op", "delfing");
                else
                    param.put("op", "addf");
                //param.put("user",user);
                return param;
            }
        };

        requestQueue.add(req);
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
}
