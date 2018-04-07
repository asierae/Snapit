package com.snapit.genia.snapit.adapters;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.snapit.genia.snapit.AlbumPhotosFragment;
import com.snapit.genia.snapit.MainActivity;
import com.snapit.genia.snapit.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Asierae on 31/01/2018.
 */

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<Album> albumList;
    private String hide;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count;
        public ImageView thumbnail, overflow;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            count = (TextView) view.findViewById(R.id.count);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            overflow = (ImageView) view.findViewById(R.id.overflow);
        }
    }


    public AlbumAdapter(Context mContext, ArrayList<Album> albumList,String hide) {
        this.mContext = mContext;
        this.albumList = albumList;
        this.hide=hide;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_album, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Album album = albumList.get(position);
        String u;
        u=album.getU2();
        holder.title.setText(u);
        holder.count.setText(album.getNumOfPcs() + " " + mContext.getString(R.string.photos));
        Log.d("AlbumAdapter",""+mContext.getResources().getString(R.string.url)+album.getThumbnail());
        Glide.with(mContext).load(mContext.getResources().getString(R.string.url)+album.getThumbnail()).thumbnail(0.5f).crossFade().into(holder.thumbnail);
        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlbumPhotosFragment blankFragment2 = new AlbumPhotosFragment();
                Bundle args = new Bundle();
                args.putString("user", album.getU2());
                args.putInt("numpics", album.getNumOfPcs());
                args.putString("id_album", Integer.toString(album.getId()));
                args.putString("hide",album.getHide());
                blankFragment2.setArguments(args);
                ((FragmentActivity) view.getContext()).getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.fragment_container,blankFragment2).addToBackStack(null).commit();

            }
        });

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow,album.getId());
            }
        });
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view,int album_id) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        if(MainActivity.getSecret().equals("0"))
        inflater.inflate(R.menu.menu_album_popup, popup.getMenu());
        else
        inflater.inflate(R.menu.menu_album_popup_hide, popup.getMenu());//if hide mode
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(view,album_id));
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        private int album_id;
        private View view;
        public MyMenuItemClickListener(View view,int album_id) {
            this.album_id=album_id;
            this.view=view;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            //Dialogo
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            // Get the layout inflater
            LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            switch (menuItem.getItemId()) {
                case R.id.action_hide_album:
                    Toast.makeText(mContext, "Hiding Album..."+album_id, Toast.LENGTH_SHORT).show();
                    menu_action(view,"hide",album_id,1);


                    // Inflate and set the layout for the dialog
                    // Pass null as the parent view because its going in the dialog layout

                    builder.setView(inflater.inflate(R.layout.dialog2, null))
                            // Add action buttons
                            .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    builder.show();
                    return true;
                case R.id.action_unhide_album:
                    Toast.makeText(mContext, "UnHiding Album..."+album_id, Toast.LENGTH_SHORT).show();
                    menu_action(view,"hide",album_id,0);


                    // Inflate and set the layout for the dialog
                    // Pass null as the parent view because its going in the dialog layout

                    builder.setView(inflater.inflate(R.layout.dialog_unhide_delete, null))
                            // Add action buttons
                            .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    builder.show();
                    return true;
                case R.id.action_delete_album:

                    // Inflate and set the layout for the dialog
                    // Pass null as the parent view because its going in the dialog layout
                    builder.setView(inflater.inflate(R.layout.dialog, null))
                            // Add action buttons
                            .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    //Deleting
                                    Toast.makeText(mContext, "Deleting Album...", Toast.LENGTH_SHORT).show();
                                    menu_action(view,"delete",album_id,0);
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //AlbumAdapter.this.getDialog().cancel();
                                }
                            });
                    builder.show();
                    return true;
                default:
            }
            return false;
        }
    }

    private void menu_action(final View view,final String op,final int album_id,final int h) {
        RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());
        // Request a string response from the provided URL.
        StringRequest req = new StringRequest(Request.Method.POST, view.getContext().getString(R.string.url)+"/GesAlbum.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Myresponse","-- "+response);
                        Toast.makeText(view.getContext(),response.toString(),Toast.LENGTH_LONG);
                        for(Album a:albumList){
                            if(a.getId()==album_id) {
                                albumList.remove(a);
                                break;
                            }
                        }
                        notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Mysmart","-- "+error.toString());

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put("op",op);
                param.put("id_album",Integer.toString(album_id));
                param.put("h",Integer.toString(h));
                return param;
            }
        };

        requestQueue.add(req);
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }
}