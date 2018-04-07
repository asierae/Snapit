package com.snapit.genia.snapit;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.clans.fab.FloatingActionMenu;
import com.snapit.genia.snapit.utils.AdMobManager;
import com.snapit.genia.snapit.utils.PrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import static android.media.MediaRecorder.VideoSource.CAMERA;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SnapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SnapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SnapFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Bitmap snap;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView usernameresp;
    private String id_album;
    private OnFragmentInteractionListener mListener;
    private PhotoView  ImageViewHolder;
    private ImageLoader mImageLoader;
    private FloatingActionButton imgSend,imgReload;
    private PrefManager prefManager;
    private String my_username;
    private EditText snap_message;
    private com.github.clans.fab.FloatingActionButton floatingActionButton1,floatingActionButton2,floatingActionButton3,floatingActionButton4;
    private FloatingActionMenu materialDesignFAM;
    private TextView messageuser;
    private AdMobManager adMobM;
    private Bitmap bitmap;
    private LinearLayout ll_message_photofull;
    private String imagePath;
    private Uri imageUri;

    public SnapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SnapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SnapFragment newInstance(String param1, String param2) {
        SnapFragment fragment = new SnapFragment();
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
            imageUri = getArguments().getParcelable("snap");
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        prefManager = new PrefManager(getContext());
        my_username=prefManager.getUsername();

        //AdMob
        adMobM=new AdMobManager(getContext());
        //Interstitial load
        adMobM.adMob_load_interstitial();
        //VideoReward load
        adMobM.adMob_load_video_reward();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_snap, container, false);
         ImageViewHolder=(PhotoView) rootView.findViewById(R.id.imageView);
         //ImageViewHolder.setOnTouchListener(new ImageMatrixTouchHandler(getContext()));
          usernameresp=(TextView)  rootView.findViewById(R.id.username_resp);
        messageuser=(TextView)  rootView.findViewById(R.id.tv_message_snap);
        ll_message_photofull=(LinearLayout)rootView.findViewById(R.id.ll_message_photofull);
        Bitmap myBitmap = BitmapFactory.decodeFile(getRealPathFromURI(getContext(),imageUri));
         ImageViewHolder.setImageBitmap(myBitmap);
        snap_message=(EditText) rootView.findViewById(R.id.et_message_snap);
         imgReload=(FloatingActionButton)rootView.findViewById(R.id.fabre);
         imgSend=(FloatingActionButton)rootView.findViewById(R.id.fab);
        //onClick Reload
        imgReload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Toast.makeText(getActivity(), "ReSnapting!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                getActivity().startActivityForResult(intent, CAMERA);
            }
        });
        //onCLick Send Snap
        imgSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                imgSend.hide();
                imgReload.hide();
                //AdMob
                adMobM.adMob_show_interstitial();
                //uploadUserImage(snap);
                uploadUserImage(getRealPathFromURI(getContext(),imageUri));
            }
        });

        //MenuFloating
        materialDesignFAM = (FloatingActionMenu) rootView.findViewById(R.id.material_design_android_floating_action_menu);
        floatingActionButton1 = (com.github.clans.fab.FloatingActionButton) rootView.findViewById(R.id.floatingmenu_snap_new);
        floatingActionButton2 = (com.github.clans.fab.FloatingActionButton) rootView.findViewById(R.id.floatingmenu_snapuser);
        floatingActionButton3 = (com.github.clans.fab.FloatingActionButton) rootView.findViewById(R.id.floatingmenu_album);
        floatingActionButton4 = (com.github.clans.fab.FloatingActionButton) rootView.findViewById(R.id.floatingmenu_report);

        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu first item clicked
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA);

            }
        });
        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu third item clicked
                adMobM.adMob_show_videoreward();

                Toast.makeText(getActivity(), "Snaping again to..", Toast.LENGTH_LONG).show();
                SendPhotoFragment blankFragment2 = new SendPhotoFragment();
                Bundle args = new Bundle();
                args.putString("user", my_username);
                args.putString("id_album", id_album);
                args.putString("user2", usernameresp.getText().toString());
                blankFragment2.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.fragment_container,blankFragment2).addToBackStack(null).commit();

            }
        });
        floatingActionButton3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu third item clicked
                Toast.makeText(getActivity(), "Entering Album...", Toast.LENGTH_LONG).show();
                AlbumPhotosFragment blankFragment2 = new AlbumPhotosFragment();
                Bundle args = new Bundle();
                args.putString("user", usernameresp.getText().toString());
                args.putString("id_album", id_album);
                blankFragment2.setArguments(args);
                getActivity().getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.fragment_container,blankFragment2).addToBackStack(null).commit();

            }
        });
        floatingActionButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), usernameresp.getText().toString()+ " Reported success", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

 /*   private void uploadUserImage(Bitmap image) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());//second parameter layout style
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.finding));
        progressDialog.setCancelable(false);
        progressDialog.show();
            RequestQueue requestQueue = Volley.newRequestQueue(getContext());

            StringRequest stringRequest = new StringRequest(Request.Method.POST, getString(R.string.url)+"/uploadfile.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.dismiss();
                    Log.i("Myresponse","-- "+response);
                    Toast.makeText(getContext(), "AQUI1: "+response.length()+response, Toast.LENGTH_SHORT).show();
                    if(response.length()>8) {
                        String[] list = response.split(",");
                        String idresp = list[0];
                        String userresp = list[1];
                        String imgresp = list[2];
                        String dateresp = list[3];
                        id_album=list[4];
                        String message=list[5];
                        final String url = getString(R.string.url) + imgresp;
                        // Instantiate the RequestQueue.
                        usernameresp.setText(userresp);
                        snap_message.setVisibility(View.INVISIBLE);
                        Glide.with(getContext()).load(url).fitCenter().crossFade().into(ImageViewHolder);
                        messageuser.setText(message);
                        //materialDesignFAM.showMenu(true);
                        materialDesignFAM.setVisibility(View.VISIBLE);
                        ll_message_photofull.setVisibility(View.VISIBLE);
                        materialDesignFAM.showMenuButton(true);

                    }
                    else{
                        Toast.makeText(getContext(), "Response= ALGO HA SALIDO MAL:"+ response.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Log.i("Mysmart","-- "+error);
                    Toast.makeText(getContext(), "AQUI2"+error, Toast.LENGTH_SHORT).show();
                    imgSend.show();
                    imgReload.show();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> param = new HashMap<>();
                    String images = getStringImage(snap);
                    //Log.i("Mynewsam",""+images);
                    param.put("image",images);
                    param.put("username",my_username);
                    param.put("message",snap_message.getText().toString());
                    return param;
                }
            };
            //timeout 15s
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                0,//DefaultRetryPolicy.DEFAULT_MAX_RETRIES
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue.add(stringRequest);


        }*/
 private void uploadUserImage(String imagePath) {
     final ProgressDialog progressDialog = new ProgressDialog(getContext());//second parameter layout style
     progressDialog.setIndeterminate(true);
     progressDialog.setMessage(getString(R.string.finding));
     progressDialog.setCancelable(false);
     progressDialog.show();
     SimpleMultiPartRequest smr = new SimpleMultiPartRequest(Request.Method.POST, getString(R.string.url)+"/uploadfile.php",
             new Response.Listener<String>() {
                 @Override
                 public void onResponse(String response) {
                     progressDialog.dismiss();
                     Log.i("Myresponse","-- "+response);
                     Toast.makeText(getContext(), "AQUI1: "+response.length()+response, Toast.LENGTH_SHORT).show();
                     if(response.length()>8) {
                         String[] list = response.split(",");
                         String idresp = list[0];
                         String userresp = list[1];
                         String imgresp = list[2];
                         String dateresp = list[3];
                         id_album=list[4];
                         String message=list[5];
                         final String url = getString(R.string.url) + imgresp;
                         // Instantiate the RequestQueue.
                         usernameresp.setText(userresp);
                         snap_message.setVisibility(View.INVISIBLE);
                         Glide.with(getContext()).load(url).fitCenter().crossFade().into(ImageViewHolder);
                         messageuser.setText(message);
                         //materialDesignFAM.showMenu(true);
                         materialDesignFAM.setVisibility(View.VISIBLE);
                         ll_message_photofull.setVisibility(View.VISIBLE);
                         materialDesignFAM.showMenuButton(true);

                     }
                     else{
                         Toast.makeText(getContext(), "Response= ALGO HA SALIDO MAL:"+ response.toString(), Toast.LENGTH_SHORT).show();
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
     //ByteArrayOutputStream bos = new ByteArrayOutputStream();
     //bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);
     //InputStream in = new ByteArrayInputStream(bos.toByteArray());
     RequestQueue mRequestQueue = Volley.newRequestQueue(getContext());
     smr.setRetryPolicy(new DefaultRetryPolicy(
             20000,
             0,//DefaultRetryPolicy.DEFAULT_MAX_RETRIES
             DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

     smr.addFile("image", imagePath);
     smr.addStringParam("username",my_username);
     smr.addStringParam("message",snap_message.getText().toString());

     mRequestQueue.add(smr);
 }

/*    public String getStringImage(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);

        return temp;
    }*/

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
/*    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 0) {//0=resultcalnceled
            return;
        }
        if (requestCode == CAMERA && data!=null) {

            // Adding captured image in bitmap.
            bitmap = (Bitmap) data.getExtras().get("data");

            Bundle args = new Bundle();
            args.putParcelable("snap", bitmap);
            SnapFragment fragment = new SnapFragment();
            fragment.setArguments(args);
            getFragmentManager().beginTransaction().setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.fragment_container,fragment).addToBackStack(null).commit();
        }
    }*/


}