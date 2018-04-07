package com.snapit.genia.snapit;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.snapit.genia.snapit.adapters.Album;
import com.snapit.genia.snapit.adapters.AlbumAdapter;
import com.snapit.genia.snapit.utils.PrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AlbumFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AlbumFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlbumFragment extends Fragment implements Response.Listener,Response.ErrorListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    //private BottomNavigationView mynav;
    private OnFragmentInteractionListener mListener;

    RecyclerView recyclerAlbum;
    ArrayList<Bitmap> photoList;
    ArrayList<Album> lista;
    ProgressDialog dialog;
    private String my_username;

    RequestQueue request;
    JsonObjectRequest jsonobjectrequest;
    private RecyclerView recyclerView;
    private FloatingActionButton mFloatingActionButton;
    //private ArrayList<Album> albumList;
    private AlbumAdapter myMAdapter;
    private PrefManager prefManager;
    private SwipeRefreshLayout swipeRefreshLayout;

    private AdView mAdView;


    public AlbumFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AlbumFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AlbumFragment newInstance(String param1, String param2) {
        AlbumFragment fragment = new AlbumFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefManager = new PrefManager(getContext());
        my_username=prefManager.getUsername();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       final View rootView= inflater.inflate(R.layout.fragment_album, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.layout_swipe_album);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                prepareAlbums(my_username,rootView);
            }
        });
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, R.color.colorAccent, R.color.colorPrimaryDark);
        swipeRefreshLayout.setRefreshing(true);
        if(((MainActivity)getActivity()).getSecret().equals("1")){
            setHasOptionsMenu(true);
            ((MainActivity)getActivity()).setActionBarTitle("Snaps Secret Mode");
        }
        else
            ((MainActivity)getActivity()).setActionBarTitle("Snaps");

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerViewAlbum);
        mFloatingActionButton=(FloatingActionButton)rootView.findViewById(R.id.fabsecret);
        //AdMob
        mAdView = rootView.findViewById(R.id.adMobView_banner_1);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //albumList = new ArrayList<Album>();
        //adapter = new AlbumAdapter(getContext(), albumList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && mFloatingActionButton.getVisibility() == View.VISIBLE) {
                    mFloatingActionButton.hide();
                } else if (dy < 0 && mFloatingActionButton.getVisibility() != View.VISIBLE) {
                    mFloatingActionButton.show();
                }
            }
        });

        FloatingActionButton floating_secret=(FloatingActionButton)rootView.findViewById(R.id.fabsecret);
        if(((MainActivity)getActivity()).getSecret().equals("1")){
            floating_secret.hide();
            rootView.findViewById(R.id.layout_album).setBackground(new ColorDrawable(Color.DKGRAY));
        }
        floating_secret.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) rootView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                if(prefManager.isFirstTimeSecret()){
                    final View mView = inflater.inflate(R.layout.dialog_secretmode_register, null);
                    final AlertDialog dialog=new AlertDialog.Builder(mView.getContext()).setView(mView)
                            .setPositiveButton(R.string.dialog_ok,null).setNegativeButton(R.string.cancel, null).show();
                    //AlertDialog.Builder builder = new AlertDialog.Builder(mView.getContext());
                    final TextInputLayout et1 = (TextInputLayout) mView.findViewById(R.id.til_pass1);
                    final TextInputLayout et2= (TextInputLayout) mView.findViewById(R.id.til_pass2);
                       // builder.setView(mView)
                         //       .setPositiveButton(R.string.dialog_ok,null).setNegativeButton(R.string.cancel, null);
                    //builder.show();
                        Button bPositive=dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        bPositive.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String pass=et1.getEditText().getText().toString().trim();
                                String pass2=et2.getEditText().getText().toString().trim();
                                if(pass.equals(pass2) && (pass.length()>4)){
                                    prefManager.setPass(pass);
                                    prefManager.setFirstTimeSecret(false);
                                    dialog.dismiss();

                                }
                                else{
                                    et1.setError(null);
                                    et2.setError(null);
                                    AlphaAnimation fadeIn=new AlphaAnimation(0.0f,1.0f);
                                    if(pass.isEmpty()){
                                        et1.setError(getString(R.string.pass_empty));
                                        //AlphaAnimation fadeOut=new AlphaAnimation(1.0f,0.0f);
                                        et1.getChildAt(1).startAnimation(fadeIn);
                                        fadeIn.setDuration(800);
                                        fadeIn.setFillAfter(true);
                                        ((MainActivity)getActivity()).setSecret("1");
                                        Toast.makeText(getActivity(), "Entrando en Album Privado", Toast.LENGTH_LONG).show();
                                        dialog.dismiss();
                                        AlbumFragment blankFragment2 = new AlbumFragment();
                                        Bundle args = new Bundle();
                                        blankFragment2.setArguments(args);
                                        getActivity().getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.fragment_container, blankFragment2).addToBackStack(null).commit();

                                    }
                                    else if(pass2.isEmpty()){
                                        et2.setError(getString(R.string.pass_empty));
                                        //AlphaAnimation fadeOut=new AlphaAnimation(1.0f,0.0f);
                                        et2.getChildAt(1).startAnimation(fadeIn);
                                        fadeIn.setDuration(800);
                                        fadeIn.setFillAfter(true);
                                    }
                                    else if(!pass.equals(pass2)){
                                        et1.setError(getString(R.string.password_match));
                                        et2.setError(getString(R.string.password_match));
                                        //AlphaAnimation fadeOut=new AlphaAnimation(1.0f,0.0f);
                                        et1.getChildAt(1).startAnimation(fadeIn);
                                        et2.getChildAt(1).startAnimation(fadeIn);
                                        fadeIn.setDuration(800);
                                        fadeIn.setFillAfter(true);
                                    }
                                    else if(pass.length()<5){
                                       // et1.getChildAt(1).startAnimation(fadeIn);
                                        et1.setError(getString(R.string.pass_lenght));
                                        et2.setError(getString(R.string.pass_lenght));
                                        //AlphaAnimation fadeOut=new AlphaAnimation(1.0f,0.0f);
                                        et1.getChildAt(1).startAnimation(fadeIn);
                                        et2.getChildAt(1).startAnimation(fadeIn);
                                        fadeIn.setDuration(800);
                                        fadeIn.setFillAfter(true);
                                    }

                                }

                            }
                        });
                }
                else {

                    final View mView = inflater.inflate(R.layout.dialog_secretmode_request_password, null);
                    final AlertDialog dialog=new AlertDialog.Builder(mView.getContext()).setView(mView)
                            .setPositiveButton(R.string.dialog_ok,null).setNegativeButton(R.string.cancel, null).show();
                    final EditText et = (EditText) mView.findViewById(R.id.edittext_password);

                    Button bPositive=dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    bPositive.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String pass = et.getText().toString();
                            if (pass.equals(prefManager.getPass())) {
                                ((MainActivity)getActivity()).setSecret("1");
                                Toast.makeText(getActivity(), "Entrando en Album Privado", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                                AlbumFragment blankFragment2 = new AlbumFragment();
                                Bundle args = new Bundle();
                                blankFragment2.setArguments(args);
                                getActivity().getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.fragment_container, blankFragment2).addToBackStack(null).commit();

                            } else {

                                TextView mytv=(TextView) mView.findViewById(R.id.tView_error_password);
                                AlphaAnimation fadeIn=new AlphaAnimation(0.0f,1.0f);
                                //AlphaAnimation fadeOut=new AlphaAnimation(1.0f,0.0f);
                                mytv.startAnimation(fadeIn);
                                fadeIn.setDuration(800);
                                fadeIn.setFillAfter(true);
                                mytv.setText(getString(R.string.failpass));

                            }

                        }
                    });
                }
            }
        });
        //Cargar Temp


        prepareAlbums(my_username,rootView);
        return rootView;
    }


    private ArrayList<Album> prepareAlbums(final String username, final View rootView) {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        lista=new ArrayList<Album>();
        // Request a string response from the provided URL.
        StringRequest req = new StringRequest(Request.Method.POST, getString(R.string.url)+"/GesAlbum.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Myresponse","-- "+response);
                        //Toast.makeText(getContext(), "AQUI1"+response, Toast.LENGTH_SHORT).show();
                        try {
                            JSONArray jsonresp = new JSONArray(response);

                            for(int i=0;i<jsonresp.length();i++){
                                // Get current json object
                                JSONObject album = jsonresp.getJSONObject(i);
                                // Get the current student (json object) data
                                int id = album.getInt("id");
                                String user1 = album.getString("user1");
                                String user2 = album.getString("user2");
                                int num_pics = album.getInt("num_pics");
                                String date = album.getString("date");
                                String lastpic = album.getString("last_pic");
                                String h=album.getString("hide");
                                Album a=new Album(id,num_pics,user1,user2,lastpic,date,h);
                                lista.add(a);
                            }
                            if(lista.size()>0) {
                                myMAdapter = new AlbumAdapter(getContext(), lista, ((MainActivity) getActivity()).getSecret());
                                recyclerView.setAdapter(myMAdapter);
                            }
                            else{
                                TextView tv=(TextView) rootView.findViewById(R.id.tv_empty_album);
                                if(((MainActivity)getActivity()).getSecret().equals("0"))
                                tv.setText(getString(R.string.empty_album));
                                else
                                    tv.setText(getString(R.string.empty_album_hidemode));
                                //RelativeLayout rl=(RelativeLayout)getView().findViewById(R.id.layout_album);
                                //rl.setBackgroundResource(R.drawable.gradient);
                            }
                            if(swipeRefreshLayout.isRefreshing()){swipeRefreshLayout.setRefreshing(false);}

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Mysmart","-- "+error.toString());
                Toast.makeText(getContext(), "AQUI2"+error, Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put("username",username);
                param.put("op","get");
                param.put("hide",((MainActivity)getActivity()).getSecret());
                return param;
            }
        };

        requestQueue.add(req);

        Log.d("LISTA",""+lista.size());
        return lista;
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

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(),"Error en conexion" + error.toString(),Toast.LENGTH_LONG);
        dialog.hide();
        Log.d("AlbumFragmentError",error.toString());
    }

    @Override
    public void onResponse(Object response) {

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
            inflater.inflate(R.menu.toolbar_exit, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        int xxxx=item.getItemId();
        switch (item.getItemId()) {
            case R.id.action_exit_hide_mode:
                final AlertDialog dialog=new AlertDialog.Builder(getContext()).setTitle(getString(R.string.exithide))
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
}
