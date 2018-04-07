package com.snapit.genia.snapit.utils;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.snapit.genia.snapit.R;

/**
 * Created by Asierae on 26/03/2018.
 */

@SuppressLint("ValidFragment")
public class AdMobManager extends Fragment{
    private Context mcontext;
    private RewardedVideoAd mRewardedVideoAd;
    private InterstitialAd mInterstitialAd;

    public AdMobManager(Context mcontext){
        this.mcontext=mcontext;
    }


    public  void adMob_load_video_reward(){
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(mcontext);
        mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {

            }

            @Override
            public void onRewardedVideoAdOpened() {

            }

            @Override
            public void onRewardedVideoStarted() {

            }

            @Override
            public void onRewardedVideoAdClosed() {

            }

            @Override
            public void onRewarded(RewardItem rewardItem) {

            }

            @Override
            public void onRewardedVideoAdLeftApplication() {

            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {

            }

            @Override
            public void onRewardedVideoCompleted() {

            }
        });
        mRewardedVideoAd.loadAd(mcontext.getString(R.string.adMob_videoReward_key_1),new AdRequest.Builder().build());
    }
    public void adMob_show_videoreward(){
        if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        }
    }
    public void adMob_load_interstitial(){
        mInterstitialAd = new InterstitialAd(mcontext);
        mInterstitialAd.setAdUnitId(mcontext.getString(R.string.adMob_intersitial_key_1));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }
    public void adMob_show_interstitial(){
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }
    }

    @Override
    public void onResume() {
        mRewardedVideoAd.resume(mcontext);
        super.onResume();
    }

    @Override
    public void onPause() {
        mRewardedVideoAd.pause(mcontext);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mRewardedVideoAd.destroy(mcontext);
        super.onDestroy();
    }

}
