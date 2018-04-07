package com.snapit.genia.snapit;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.snapit.genia.snapit.adapters.FollowersAdapter;
import com.snapit.genia.snapit.adapters.User;
import com.snapit.genia.snapit.utils.ClickListener;
import com.snapit.genia.snapit.utils.RecyclerItemTouchHelperFollowers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FollowersFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FollowersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FollowersFragment extends Fragment implements RecyclerItemTouchHelperFollowers.RecyclerItemTouchHelperListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String my_username;
    private OnFragmentInteractionListener mListener;
    private CoordinatorLayout coordinatorLayout;
    private ArrayList<User> listaUsers;
    private FollowersAdapter myMAdapter;
    private RecyclerView recyclerView;
    private TextView tv_numFollowers;
    private String mode;
    private Snackbar snackbar;

    public FollowersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FollowersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FollowersFragment newInstance(String param1, String param2) {
        FollowersFragment fragment = new FollowersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            my_username = getArguments().getString("username");
            mode = getArguments().getString("mode");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_followers, container, false);
        recyclerView=(RecyclerView)rootView.findViewById(R.id.rView_followers);
        coordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.coordinator_layout_followers);
        tv_numFollowers=(TextView)rootView.findViewById(R.id.tv_num_followers);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
/*        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                UserProfileFragment blankFragment2 = new UserProfileFragment();
                Bundle args = new Bundle();
                args.putString("username",my_username);
                args.putString("userprofile",listaUsers.get(position).getUsername());
                blankFragment2.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction().setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.fragment_container, blankFragment2).addToBackStack(null).commit();

            }

            @Override
            public void onLongClick(View view, int position) {
                User m = listaUsers.get(position);
                Toast.makeText(getContext(), m.getId()+" Seleccionado", Toast.LENGTH_SHORT).show();
            }
        }));*/
        //son elementos del mismo tamaño
        recyclerView.setHasFixedSize(true);
        //asociamos a un layoputmanager
        LinearLayoutManager linLayMan=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linLayMan);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), VERTICAL));
        //SWIPE
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelperFollowers(0, ItemTouchHelper.LEFT , (RecyclerItemTouchHelperFollowers.RecyclerItemTouchHelperListener) this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        load_followers();

        return rootView;
    }

    private void load_followers(){
        //Volley request and fit the lista
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        listaUsers = new ArrayList<User>();
        // Request a string response from the provided URL.
        StringRequest req = new StringRequest(Request.Method.POST, getString(R.string.url) + "/GesUsers.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Myresponse", "-- " + response);
                        //Toast.makeText(getContext(), "AQUI1"+response, Toast.LENGTH_SHORT).show();
                        try {
                            JSONArray jsonresp = new JSONArray(response);

                            for (int i = 0; i < jsonresp.length(); i++) {
                                // Get current json object
                                JSONObject profile = jsonresp.getJSONObject(i);
                                // Get the current student (json object) data
                                int id = profile.getInt("id");
                                String username = profile.getString("username");
                                String background = profile.getString("background");
                                String bio = profile.getString("bio");
                                int followers = profile.getInt("followers");
                                int following = profile.getInt("following");
                                String date = profile.getString("date");
                                String avatar = profile.getString("avatar");

                                    User a = new User(id,username,background,followers,following,bio,date,avatar);
                                    listaUsers.add(a);

                            }
                            if(mode.equals("followers"))
                            tv_numFollowers.setText(listaUsers.size()+" sLovers");
                            else
                                tv_numFollowers.setText(listaUsers.size()+" sLovings");
                            myMAdapter=new FollowersAdapter(getContext(), listaUsers,mode, new ClickListener() {
                                @Override
                                public void onPositionClicked(int position) {
                                    //Lo coge del adapter
                                }

                                @Override
                                public void onLongClicked(int position) {

                                }
                            });
                            recyclerView.setAdapter(myMAdapter);

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
                if(mode.equals("followers")) param.put("op", "followers");
                else param.put("op", "following");
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
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, final int position) {
        final Restored r=new Restored();
        if (viewHolder instanceof FollowersAdapter.MyViewHolder) {
            // get the removed item name to display it in snack bar
            String name = listaUsers.get(viewHolder.getAdapterPosition()).getUsername();

            // backup of removed item for undo purpose
            final User deletedItem = listaUsers.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();
            final int restored=0;
            // remove the item from recycler view
            myMAdapter.removeItem(viewHolder.getAdapterPosition());
            // showing snack bar with Undo option
            String msj="";
            if(mode.equals("following"))
                msj="User "+name+ " unLoved";
                else
                msj="User "+name+ " Blocked";

                snackbar = Snackbar
                    .make(getActivity().findViewById(android.R.id.content), msj, Snackbar.LENGTH_LONG);
            snackbar.setAction("Deshacer", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // undo is selected, restore the deleted item
                    myMAdapter.restoreItem(deletedItem, deletedIndex);
                    r.setRestored(true);
                }
            });
            snackbar.addCallback(new Snackbar.Callback() {

                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    //see Snackbar.Callback docs for event details
                    if(r!=null && !r.isRestored()){
                    Toast.makeText(getContext(), "deleted!", Toast.LENGTH_SHORT).show();
                    if(mode.equals("following"))
                    delete_following(deletedItem,0);
                    else
                        delete_following(deletedItem,1);

                    }
                      }

                @Override
                public void onShown(Snackbar snackbar) {

                }
            });

            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
    //Clase para borrar los followers y saber si se ha pulsado deshacer y no hacerlo
    class Restored{
        boolean restored=false;
        Restored(){}
        public void setRestored(boolean b){this.restored=b;}
        public boolean isRestored(){return restored;}
    }
    private void delete_following(final User u, final int blocked){
        //Volley request and fit the lista
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        // Request a string response from the provided URL.
        StringRequest req = new StringRequest(Request.Method.POST, getString(R.string.url) + "/GesUsers.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Myresponse", "-- " + response);
                        Toast.makeText(getContext(), "AQUI1"+response, Toast.LENGTH_SHORT).show();
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
                if(blocked==0){
                param.put("following", u.getUsername());
                param.put("op", "delfing");}
                else{
                param.put("blocked", u.getUsername());
                param.put("op", "block");}
                //param.put("user",user);
                return param;
            }
        };

        requestQueue.add(req);
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
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.search_menu_messages, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = new SearchView(((MainActivity) getContext()).getSupportActionBar().getThemedContext());
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        MenuItemCompat.setActionView(item, searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter(query.toString());
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText.toString());
                return false;
            }
        });
        searchView.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              Toast.makeText(getContext(),"???",Toast.LENGTH_SHORT);
                                          }
                                      }
        );
    }

    private void filter(String s) {
        ArrayList<User> filtrados = new ArrayList<User>();
        for (User m :listaUsers){
            if(m.getBio().toLowerCase().contains(s.toLowerCase()) || m.getUsername().toLowerCase().contains(s.toLowerCase())){
                filtrados.add(m);
            }
        }
        myMAdapter.filter(filtrados);
    }

}
