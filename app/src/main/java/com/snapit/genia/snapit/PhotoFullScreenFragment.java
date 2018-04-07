package com.snapit.genia.snapit;

import android.app.FragmentTransaction;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.snapit.genia.snapit.utils.PrefManager;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PhotoFullScreenFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PhotoFullScreenFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PhotoFullScreenFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String pic,date,message;

    private OnFragmentInteractionListener mListener;
    private PrefManager prefManager;
    private String my_username,id_album,user2;

    public PhotoFullScreenFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PhotoFullScreenFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PhotoFullScreenFragment newInstance(String param1, String param2) {
        PhotoFullScreenFragment fragment = new PhotoFullScreenFragment();
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
            pic = getArguments().getString("pic");
            date = getArguments().getString("date");
            message=getArguments().getString("message");
            id_album=getArguments().getString("id_album");
            user2=getArguments().getString("user2");
        }
        setHasOptionsMenu(true);
        prefManager = new PrefManager(getContext());
        my_username=prefManager.getUsername();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_photo_full_screen, container, false);
        ImageView photofull=(ImageView)rootView.findViewById(R.id.image_preview);
        TextView messagetv=(TextView)rootView.findViewById(R.id.message);
        TextView datetv=(TextView)rootView.findViewById(R.id.date);

        Glide.with(getContext()).load(getString(R.string.url)+pic).fitCenter().crossFade().into(photofull);
        messagetv.setText(message);
        datetv.setText(date);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(((MainActivity)getActivity()).getSecret().equals("1")){
            inflater.inflate(R.menu.toolbar_hide_mode, menu);
        }
        else {
            inflater.inflate(R.menu.toolbar_send_snap, menu);
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
            case R.id.action_exit_hide_mode:
                AlbumFragment blankFragment3 = new AlbumFragment();
                Bundle args3 = new Bundle();
                args3.putString("user",my_username);
                ((MainActivity)getActivity()).setSecret("0");
                blankFragment3.setArguments(args3);
                getActivity().getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.fragment_container,blankFragment3).addToBackStack(null).commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
}

}
