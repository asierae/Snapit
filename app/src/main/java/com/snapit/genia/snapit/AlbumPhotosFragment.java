package com.snapit.genia.snapit;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.snapit.genia.snapit.adapters.AlbumPhotosAdapter;
import com.snapit.genia.snapit.adapters.Photo;
import com.snapit.genia.snapit.utils.PhotoListSerializable;
import com.snapit.genia.snapit.utils.PrefManager;
import com.snapit.genia.snapit.utils.RecyclerItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class AlbumPhotosFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private int numpic;
    private String id;
    private String user, date;
    private ArrayList<Photo> lista;
    private OnFragmentInteractionListener mListener;
    private AlbumPhotosAdapter myAdapter;
    private RecyclerView myRecycler;
    boolean isMultiSelect = false;
    ActionMode mActionMode;
    ArrayList<Photo> multiselect_list = new ArrayList<>();
    private Menu context_menu;
    private PrefManager prefManager;
    private String my_username;
    private View rootView;
    private Toolbar toolbar;
    private CollapsingToolbarLayout ctl;
    private ImageView iValbumphotos;
    private ImageView iValbumphotos_header;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton floating_follow,floating_unfollow;

    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    public AlbumPhotosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AlbumPhotosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AlbumPhotosFragment newInstance(String param1, String param2) {
        AlbumPhotosFragment fragment = new AlbumPhotosFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //((MainActivity)getActivity()).getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getString("user");
            numpic = getArguments().getInt("numpics");
            id = getArguments().getString("id_album");
        }
        ((MainActivity) getActivity()).setActionBarTitle(user);
        prefManager = new PrefManager(getContext());
        my_username = prefManager.getUsername();
        lista=new ArrayList<Photo>();
        //TEMP
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        //((MainActivity)getActivity()).getSupportActionBar().hide();
        //getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

      /*      Window window = ((MainActivity)getActivity()).getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);*/

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_album_photos, container, false);

        setHasOptionsMenu(true);//tiene cosas en la toolbar
        setHasOptionsMenu(true);//tiene cosas en la toolbar
        ctl = (CollapsingToolbarLayout) rootView.findViewById(R.id.collapsing_toolbar);
        ctl.setTitle(user);
        //toolbar del collapsing
        android.support.v7.widget.Toolbar tool=(android.support.v7.widget.Toolbar)rootView.findViewById(R.id.toolbar_collapsing);
        tool.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        if (((MainActivity) getActivity()).getSecret().equals("1")) {
        tool.inflateMenu(R.menu.toolbar_hide_mode);
        } else {
        tool.inflateMenu(R.menu.toolbar_send_snap);
        }

        tool.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return onOptionMenuCLick(item);
            }
        });
        tool.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlbumFragment blankFragment3 = new AlbumFragment();
                Bundle args3 = new Bundle();
                args3.putString("user", my_username);
                blankFragment3.setArguments(args3);
                getActivity().getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.fragment_container, blankFragment3).addToBackStack(null).commit();
            }
        });
        iValbumphotos=(ImageView)rootView.findViewById(R.id.albumphotos_avatar);
        iValbumphotos_header=(ImageView)rootView.findViewById(R.id.albumphotos_header);
        myRecycler = (RecyclerView) rootView.findViewById(R.id.recycler_view_photos);
        final RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);//dos fotos(cardview) cada linea
        myRecycler.setLayoutManager(mLayoutManager);
        myRecycler.setItemAnimator(new DefaultItemAnimator());
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myRecycler.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), myRecycler, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isMultiSelect) {
                    multi_select(position);
                } else {
                    ((MainActivity)getActivity()).getSupportActionBar().show();
                    PhotoListSerializable photoList = new PhotoListSerializable(lista);
                    PhotoFullViewPagerFragment blankFragment2 = new PhotoFullViewPagerFragment();
                    Bundle args = new Bundle();
                    args.putSerializable("photoList", photoList);
                    args.putString("user2", user);
                    args.putInt("pos", position);
                    args.putString("id_album", id);
                    blankFragment2.setArguments(args);
                    getActivity().getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.fragment_container, blankFragment2).addToBackStack(null).commit();
                }
            }


            @Override
            public void onItemLongClick(View view, int position) {
                if (!isMultiSelect) {
                    multiselect_list = new ArrayList<Photo>();
                    isMultiSelect = true;


                    if (mActionMode == null) {

                        mActionMode = getActivity().startActionMode(mActionModeCallback);
                    }
                }
                multi_select(position);
            }
        }));

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.layout_swipe_albumphoto);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                prepareAlbumPhotos(id, user);
            }
        });
        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, R.color.colorAccent, R.color.colorPrimaryDark);

        floating_follow=(FloatingActionButton)rootView.findViewById(R.id.floating_albumphotos_follow);
        floating_unfollow=(FloatingActionButton)rootView.findViewById(R.id.floating_albumphotos_unfollow);
        floating_unfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final View mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_unfollow, null);
                ImageView avatarmini=mView.findViewById(R.id.dialog_avatar);
                TextView name=mView.findViewById(R.id.dialog_unfollow);
                name.setText(name.getText()+" "+user + "?");
                avatarmini.setImageDrawable(iValbumphotos.getDrawable());
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
        prepareAlbumPhotos(id, user);
        //Para el boton back
        //((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        return rootView;
    }

    public void multi_select(int position) {
        if (mActionMode != null) {
            if (multiselect_list.contains(lista.get(position)))
                multiselect_list.remove(lista.get(position));
            else
                multiselect_list.add(lista.get(position));


            if (multiselect_list.size() > 0)
                mActionMode.setTitle("" + multiselect_list.size());
            else {
                mActionMode.finish();
            }

            refreshAdapter();
        }
    }

    public void prepareAlbumPhotos(final String id_album, final String user) {
        //Volley request and fit the lista
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        lista = new ArrayList<Photo>();
        // Request a string response from the provided URL.
        StringRequest req = new StringRequest(Request.Method.POST, getString(R.string.url) + "/GesPhotos.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Myresponse", "-- " + response);
                        //Toast.makeText(getContext(), "AQUI1"+response, Toast.LENGTH_SHORT).show();
                        try {
                            JSONArray jsonresp = new JSONArray(response);
                            JSONArray photos=jsonresp.getJSONArray(1);
                            JSONArray profile=jsonresp.getJSONArray(0);
                            JSONArray f=jsonresp.getJSONArray(2);
                            int fo= (int) f.get(0);

                            //Profile
                            String bg="";
                            String avatar="";
                            for (int i = 0; i < profile.length(); i++) {
                                // Get current json object
                                JSONObject u = profile.getJSONObject(i);
                                 bg = u.getString("background");
                                 avatar = u.getString("avatar");
                                int id = u.getInt("id");
                            }
                            Glide.with(getContext()).load(getContext().getResources().getString(R.string.url)+bg).thumbnail(0.5f).crossFade().into(iValbumphotos_header);
                            Glide.with(getContext()).load(getContext().getResources().getString(R.string.url)+avatar).thumbnail(0.5f).crossFade().into(iValbumphotos);
                            //Photos
                            for (int i = 0; i < photos.length(); i++) {
                                // Get current json object
                                JSONObject album = photos.getJSONObject(i);
                                // Get the current student (json object) data
                                int id = album.getInt("id");
                                String user1 = album.getString("user1");
                                String user2 = album.getString("user2");
                                String pic1 = album.getString("image1");
                                String pic2 = album.getString("image2");
                                String message = album.getString("message");
                                String date = album.getString("date");
                                //Cuando sea envio unico y no por intercambio
                                if (pic2.length() > 6) {
                                    Photo a = new Photo(id, user2, pic2, message, date);
                                    lista.add(a);
                                }
                                if (pic1.length() > 6) {
                                    Photo b = new Photo(id, user1, pic1, message, date);
                                    lista.add(b);
                                }

                            }

                            if(fo==1)
                                floating_unfollow.show();
                            else
                                floating_follow.show();

                            myAdapter = new AlbumPhotosAdapter(getContext(), lista, multiselect_list);
                            myRecycler.setAdapter(myAdapter);
                            if(swipeRefreshLayout.isRefreshing()){swipeRefreshLayout.setRefreshing(false);
                                Toast.makeText(getContext(), "Updated", Toast.LENGTH_SHORT).show();}

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
                param.put("id_album", "" + id_album + "");
                param.put("op", "get");
                param.put("userprofile", user);
                param.put("username", my_username);
                //param.put("user",user);
                return param;
            }
        };

        requestQueue.add(req);
        Log.d("LISTA", "" + lista.size());
    }


    private boolean onOptionMenuCLick(MenuItem item){
        int xxxx = item.getItemId();
        switch (item.getItemId()) {
            case R.id.action_send_snap:
                SendPhotoFragment blankFragment2 = new SendPhotoFragment();
                Bundle args = new Bundle();
                args.putString("user", my_username);
                args.putString("id_album", id);
                args.putString("user2", user);
                blankFragment2.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.fragment_container, blankFragment2).addToBackStack(null).commit();
                return true;
            case R.id.action_exit_hide_mode:
                final AlertDialog dialog = new AlertDialog.Builder(getContext()).setTitle(getString(R.string.exithide))
                        .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                AlbumFragment blankFragment3 = new AlbumFragment();
                                Bundle args3 = new Bundle();
                                args3.putString("user", my_username);
                                ((MainActivity) getActivity()).setSecret("0");
                                blankFragment3.setArguments(args3);
                                getActivity().getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.fragment_container, blankFragment3).addToBackStack(null).commit();
                            }
                        }).setNegativeButton(R.string.cancel, null).show();

                return true;
            default:
                Toast.makeText(getContext(), item.getItemId(), Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);
        }
    }

    /*    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (((MainActivity) getActivity()).getSecret().equals("1")) {
            inflater.inflate(R.menu.toolbar_hide_mode, menu);
        } else {
            inflater.inflate(R.menu.toolbar_send_snap, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }*/
    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        int xxxx = item.getItemId();
        switch (item.getItemId()) {
            case R.id.action_send_snap:
                SendPhotoFragment blankFragment2 = new SendPhotoFragment();
                Bundle args = new Bundle();
                args.putString("user", my_username);
                args.putString("id_album", id);
                args.putString("user2", user);
                blankFragment2.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.fragment_container, blankFragment2).addToBackStack(null).commit();
                return true;
            case R.id.action_exit_hide_mode:
                final AlertDialog dialog = new AlertDialog.Builder(getContext()).setTitle(getString(R.string.exithide))
                        .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                AlbumFragment blankFragment3 = new AlbumFragment();
                                Bundle args3 = new Bundle();
                                args3.putString("user", my_username);
                                ((MainActivity) getActivity()).setSecret("0");
                                blankFragment3.setArguments(args3);
                                getActivity().getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.fragment_container, blankFragment3).addToBackStack(null).commit();
                            }
                        }).setNegativeButton(R.string.cancel, null).show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/

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
        ((MainActivity)getActivity()).getSupportActionBar().show();//borrar

        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        if (mActionMode != null)
            mActionMode.finish();
        mListener = null;
        super.onDetach();
    }

    @Override
    public void onPause() {
        ((MainActivity)getActivity()).getSupportActionBar().show();//borrar
        super.onPause();
    }

    @Override
    public void onResume() {
        ((MainActivity)getActivity()).getSupportActionBar().hide();//borrar
        super.onResume();
    }

    @Override
    public void onDestroy() {
        if (mActionMode != null)
            mActionMode.finish();
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        if (mActionMode != null)
            mActionMode.finish();
        super.onDestroyView();
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

    public void refreshAdapter() {
        myAdapter.photoList = lista;
        myAdapter.selected_photoList = multiselect_list;
        myAdapter.notifyDataSetChanged();
    }

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
            switch (item.getItemId()) {
                case R.id.action_delete:
                    String dels = "";
                    for (int i = 0; i < multiselect_list.size(); i++) {
                        lista.remove(multiselect_list.get(i));
                        if (i < multiselect_list.size() - 1)
                            dels = dels + multiselect_list.get(i).getId() + ",";
                        else
                            dels = dels + multiselect_list.get(i).getId();
                    }
                    mode.finish();
                    deletePhotos(id, dels);

                    return true;
                case android.R.id.home://back
                    mode.finish();
                    //((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);//quitar back button
                    AlbumFragment blankFragment2 = new AlbumFragment();
                    Bundle args = new Bundle();
                    args.putString("hide", "0");
                    blankFragment2.setArguments(args);
                    getActivity().getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.fragment_container, blankFragment2).addToBackStack(null).commit();
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            isMultiSelect = false;
            multiselect_list = new ArrayList<Photo>();
            refreshAdapter();
        }
    };

    public void deletePhotos(final String id_album, final String del_photo_list) {
        //Volley request and fit the lista
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        //lista = new ArrayList<Photo>();
        // Request a string response from the provided URL.
        StringRequest req = new StringRequest(Request.Method.POST, getString(R.string.url) + "/GesPhotos.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Myresponse", "-- " + response);
                        Toast.makeText(getContext(), "AQUI3" + response, Toast.LENGTH_SHORT).show();
                        if (response.toString().equals("OK")) {
                            refreshAdapter();
                            //myAdapter.notifyDataSetChanged();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Mysmart", "-- " + error.toString());
                Toast.makeText(getContext(), "AQUI4" + error, Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("id_album", "" + id_album + "");
                param.put("deletes", "" + del_photo_list + "");
                param.put("op", "delete");
                //param.put("user",user);
                return param;
            }
        };

        requestQueue.add(req);
        Log.d("LISTA", "" + lista.size());
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
                param.put("following", user);
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

}

