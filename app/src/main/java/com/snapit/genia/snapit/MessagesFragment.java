package com.snapit.genia.snapit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.Toast;

import com.snapit.genia.snapit.adapters.Message;
import com.snapit.genia.snapit.adapters.MessagesAdapter;
import com.snapit.genia.snapit.adapters.User;
import com.snapit.genia.snapit.utils.RecyclerItemTouchHelper;
import com.snapit.genia.snapit.utils.RecyclerTouchListener;

import java.util.ArrayList;

import static android.support.v7.widget.DividerItemDecoration.*;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MessagesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MessagesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessagesFragment extends Fragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private ArrayList<Message> datos;
    private MessagesAdapter myMAdapter;
    private CoordinatorLayout coordinatorLayout;

    public MessagesFragment() {


        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MessagesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MessagesFragment newInstance(String param1, String param2) {
        MessagesFragment fragment = new MessagesFragment();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
        ArrayList<Message> filtrados = new ArrayList<Message>();
        for (Message m :datos){
            if(m.getUser().getName().toLowerCase().contains(s.toLowerCase()) || m.getMessage().toLowerCase().contains(s.toLowerCase())){
                filtrados.add(m);
            }
        }
        myMAdapter.filter(filtrados);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_messages, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.myRecyclerViewM);
        coordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.coordinator_layout);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Message m = datos.get(position);
                Toast.makeText(getContext(), m.getMessage() + " Entrar a conver", Toast.LENGTH_SHORT).show();
                ConversationFragment blankFragment2 = new ConversationFragment();
                Bundle args = new Bundle();
                args.putString("message", m.getMessage());
                args.putString("user", m.getUser().getUsername());
                args.putInt("id", m.getId());
                blankFragment2.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.fragment_container,blankFragment2).addToBackStack(null).commit();

            }

            @Override
            public void onLongClick(View view, int position) {
                Message m = datos.get(position);
                Toast.makeText(getContext(), m.getId()+" Seleccionado", Toast.LENGTH_SHORT).show();
            }
        }));
        //son elementos del mismo tamaño
        recyclerView.setHasFixedSize(true);
        //asociamos a un layoputmanager
        LinearLayoutManager linLayMan=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linLayMan);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), VERTICAL));
        //SWIPE
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT , (RecyclerItemTouchHelper.RecyclerItemTouchHelperListener) this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        //ENDSWIPE
        //asociamos a un adapter que es el que va a renderizar la info que tenemos

         datos= new ArrayList<Message>();
        User u=new User(2,"Asier","Asierae","01/02/2018", "/uploads/avatars/iduser.jpg");
        Message m= new Message(2,u,"Hola mongola","12/02/2018");
        User u2=new User(3,"Irene","Iren","01/02/2018", "/uploads/avatars/iduser2.jpg");
        Message m2= new Message(3,u2,"Asier, eres el mejor","12/02/2018");
        User u3=new User(4,"Guadalupe","Guadalupe","01/02/2018", "/uploads/avatars/iduser3.jpg");
        Message m3= new Message(4,u3,"Tengo el mejor novio del mundo","12/02/2018");
        datos.add(m);
        datos.add(m2);
        datos.add(m3);
        datos.add(m);
        datos.add(m2);
        datos.add(m3);
        for(int i=0;i<5;i++)
            datos.add(m);
         myMAdapter=new MessagesAdapter(datos);
        recyclerView.setAdapter(myMAdapter);
        // Inflate the layout for this fragment
        return rootView;
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
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof MessagesAdapter.MyViewHolder) {
            // get the removed item name to display it in snack bar
            String name = datos.get(viewHolder.getAdapterPosition()).getUser().getUsername();

            // backup of removed item for undo purpose
            final Message deletedItem = datos.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            myMAdapter.removeItem(viewHolder.getAdapterPosition());
            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(getActivity().findViewById(android.R.id.content), "Conversación con " + name + " eliminada!", Snackbar.LENGTH_LONG);
            snackbar.setAction("Deshacer", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // undo is selected, restore the deleted item
                   myMAdapter.restoreItem(deletedItem, deletedIndex);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
}
