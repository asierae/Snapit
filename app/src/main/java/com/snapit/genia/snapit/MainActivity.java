package com.snapit.genia.snapit;

import android.Manifest;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.snapit.genia.snapit.adapters.User;
import com.snapit.genia.snapit.utils.PrefManager;

import java.io.IOException;

import static android.media.MediaRecorder.VideoSource.CAMERA;

public class MainActivity extends AppCompatActivity implements UserProfileFragment.OnFragmentInteractionListener,FollowersFragment.OnFragmentInteractionListener,ProfileFragment.OnFragmentInteractionListener,PhotoFullViewPagerFragment.OnFragmentInteractionListener,SendPhotoFragment.OnFragmentInteractionListener,PhotoFullScreenFragment.OnFragmentInteractionListener,AlbumPhotosFragment.OnFragmentInteractionListener,ConversationFragment.OnFragmentInteractionListener,SnapFragment.OnFragmentInteractionListener,AlbumFragment.OnFragmentInteractionListener,MessagesFragment.OnFragmentInteractionListener{

    private TextView mTextMessage;
    private  ActionBar toolbar;//barra principal arriba
    Bitmap bitmap;
    private ContentValues cv;
    private Uri imageUri;
    private PrefManager prefManager;
    private String my_username;
    private static String SECRET="0";
    private BottomNavigationView navigation;//Barra navegacion abajo
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.setTheme( R.style.AppTheme );
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //PrefManager prefManager = new PrefManager(getApplicationContext());
        //prefManager.setUsername("pruebagooglex");
        //Analytics
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
        //End analytics

        mTextMessage = (TextView) findViewById(R.id.message);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        toolbar = getSupportActionBar();
        toolbar.setTitle("SnapIt");

        //Adding the HOME fragment
        ProfileFragment fragment = new ProfileFragment();
        getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).add(R.id.fragment_container, fragment).commit();;

            // Show an expanation to the user *asynchronously* -- don't block
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    101);

    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    toolbar.setTitle(R.string.hello_messages);
                    getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    MessagesFragment blankFragment2 = new MessagesFragment();
                    getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.fragment_container,blankFragment2).addToBackStack(null).commit();

                    return true;
                case R.id.navigation_dashboard:;
                    toolbar.setTitle("Snapit");
                    //Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    //startActivityForResult(intent, CAMERA);
                    prefManager = new PrefManager(getApplicationContext());
                    my_username=prefManager.getUsername();
                    cv = new ContentValues();
                    cv.put(MediaStore.Images.Media.TITLE, "slove_"+my_username+"_"+Long.toString(System.currentTimeMillis()));
                    cv.put(MediaStore.Images.Media.DESCRIPTION, "sLove Photo");
                    cv.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
                    cv.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                    //cv.put(MediaStore.Images.Media.LATITUDE,"");
                    //cv.put(MediaStore.Images.Media.LONGITUDE,"");
                    imageUri = getContentResolver().insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, CAMERA);
                    return true;
                case R.id.navigation_snapalbum:
                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    toolbar.setTitle("Snaps");
                    AlbumFragment fragment = new AlbumFragment();
                    getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.fragment_container, fragment).addToBackStack(null).commit();

                    return true;
                case R.id.navigation_profile:
                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    toolbar.setTitle(getString(R.string.tittle_profile));
                    ProfileFragment fragment3 = new ProfileFragment();
                    getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.fragment_container, fragment3).addToBackStack(null).commit();

                    return true;
            }
            return false;
        }
    };
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == CAMERA) {

            bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                //ByteArrayOutputStream stream = new ByteArrayOutputStream();
                //bitmap.compress(Bitmap.CompressFormat.JPEG,75,stream);.......
            } catch (IOException e) {
                e.printStackTrace();
            }
            //getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            Bundle args = new Bundle();
            //String imagePath=getRealPathFromURI(getApplicationContext(),imageUri);
            args.putParcelable("snap",imageUri);
            SnapFragment fragment = new SnapFragment();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.fragment_container,fragment).addToBackStack(null).commit();


        }
    }

/*    public String getRealPathFromURI(Context context, Uri contentUri) {
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
    }*/
    @Override
    public void onFragmentInteraction(Uri uri) {
        //
    }
    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    //Para los back <- en los fragments
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    //Esto no estaba se puede boorar
    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getFragmentManager().popBackStack();
        }
    }

    public static final void setSecret(String h){
        SECRET=h;
    }
    public static final String getSecret(){
        return SECRET;
    }


    public void swipe_toolbar() {
        if(toolbar.isShowing()){
            toolbar.hide();
        }
        else{
            toolbar.show();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 101: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}
