package com.snapit.genia.snapit;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.snapit.genia.snapit.utils.PrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_CANCELED;
import static android.media.MediaRecorder.VideoSource.CAMERA;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SendPhotoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SendPhotoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SendPhotoFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Bitmap bitmap;
    private ImageView iVSendpic;
    private FloatingActionButton fSemdButton;
    private String user;
    private String id_album;
    private OnFragmentInteractionListener mListener;
    private String user2;
    private PrefManager prefManager;
    private String my_username;
    private EditText snap_message;
    private ContentValues cv;
    private Uri imageUri;

    public SendPhotoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SendPhotoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SendPhotoFragment newInstance(String param1, String param2) {
        SendPhotoFragment fragment = new SendPhotoFragment();
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
            user = getArguments().getString("user");
            user2 = getArguments().getString("user2");
            id_album = getArguments().getString("id_album");
        }
        prefManager = new PrefManager(getContext());
        my_username=prefManager.getUsername();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_send_photo, container, false);

        iVSendpic=(ImageView)rootView.findViewById(R.id.imVsendpic);
        fSemdButton=(FloatingActionButton)rootView.findViewById(R.id.send_photo_b);
        snap_message=(EditText) rootView.findViewById(R.id.et_message_send);

        fSemdButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Toast.makeText(getActivity(), "Sending Snap to: "+ user2, Toast.LENGTH_LONG).show();

                send_pic_as_file(getRealPathFromURI(getContext(),imageUri));
            }
        });

        cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE, "slove_"+my_username+"_"+Long.toString(System.currentTimeMillis()));
        cv.put(MediaStore.Images.Media.DESCRIPTION, "sLove Photo");
        cv.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        cv.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        //cv.put(MediaStore.Images.Media.LATITUDE,"");
        //cv.put(MediaStore.Images.Media.LONGITUDE,"");
        imageUri = getContext().getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CAMERA);

        return rootView;
    }


    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void  send_pic_as_file(String imagePath){
        SimpleMultiPartRequest smr = new SimpleMultiPartRequest(Request.Method.POST, getString(R.string.url)+"/sendS2U.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        try {
                            JSONObject jObj = new JSONObject(response);
                            String message = jObj.getString("message");

                            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                            AlbumPhotosFragment blankFragment2 = new AlbumPhotosFragment();
                            Bundle args = new Bundle();
                            args.putString("user", user2);
                            //args.putString("id_album", response.toString());
                            args.putString("id_album", id_album);
                            blankFragment2.setArguments(args);
                            getActivity().getSupportFragmentManager().beginTransaction().setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.fragment_container,blankFragment2).addToBackStack(null).commit();

                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        //Compress
        //Bitmap bmp = BitmapFactory.decodeFile(imagePath);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);
        //InputStream in = new ByteArrayInputStream(bos.toByteArray());
        RequestQueue mRequestQueue = Volley.newRequestQueue(getContext());
        smr.addFile("image", imagePath);
        smr.addStringParam("username",my_username);
        smr.addStringParam("username2",user2);
        smr.addStringParam("id_album",id_album);
        smr.addStringParam("message",snap_message.getText().toString());

        mRequestQueue.add(smr);
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
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            AlbumFragment blankFragment3 = new AlbumFragment();
            Bundle args3 = new Bundle();
            args3.putString("user",my_username);
            blankFragment3.setArguments(args3);
            getActivity().getSupportFragmentManager().beginTransaction().setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.fragment_container,blankFragment3).addToBackStack(null).commit();

            return;
        }
        if (requestCode == CAMERA) {

             bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
                //ByteArrayOutputStream stream = new ByteArrayOutputStream();
                //bitmap.compress(Bitmap.CompressFormat.JPEG,75,stream);.......
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Glide.with(getContext()).load(imageUri).thumbnail(0.5f).crossFade().into(iVSendpic);
            iVSendpic.setImageBitmap(bitmap);
            // Adding captured image in bitmap.
            //bitmap = (Bitmap) data.getExtras().get("data");
            //iVSendpic.setImageBitmap(bitmap);

        }
    }

}
