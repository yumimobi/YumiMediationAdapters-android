package com.yumi.android.sdk.ads.adapter.chartboost;

import android.app.Activity;

import com.chartboost.sdk.Chartboost;
import com.chartboost.sdk.ChartboostDelegate;
import com.chartboost.sdk.Libraries.CBLogging.Level;
import com.chartboost.sdk.Model.CBError.CBImpressionError;

import static com.yumi.android.sdk.ads.adapter.chartboost.ChartboostUtil.updateGDPRStatus;

public class ChartboostExtra {

    private boolean hasInitCharboost = false;
    private ChartboostDelegate mediaDelegate;
    private ChartboostDelegate instertitialDelegate;
    private ChartboostDelegate delegate;

    private ChartboostExtra() {
    }

    static ChartboostExtra getChartboostExtra() {
        return ChartboostExtraHolder.INSTANCE;
    }

    ;

    void setInstertitialListener(ChartboostDelegate instertitialDelegate) {
        this.instertitialDelegate = instertitialDelegate;
    }

    void setMediaListener(ChartboostDelegate mediaDelegate) {
        this.mediaDelegate = mediaDelegate;
    }

    void initChartboostSDK(Activity activity, String appId, String appSignature) {
        if (!hasInitCharboost) {
            hasInitCharboost = true;
            createDelegate();
            Chartboost.startWithAppId(activity, appId, appSignature);
            Chartboost.setLoggingLevel(Level.ALL);
            Chartboost.setDelegate(delegate);
            Chartboost.onCreate(activity);
            Chartboost.onStart(activity);
            Chartboost.setAutoCacheAds(false);
            updateGDPRStatus(activity);
        }
    }

    private void createDelegate() {
        delegate = new ChartboostDelegate() {
            @Override
            public void didCacheInterstitial(String location) {
                if (instertitialDelegate != null) {
                    instertitialDelegate.didCacheInterstitial(location);
                }
            }

            @Override
            public void didFailToLoadInterstitial(String location,
                                                  CBImpressionError error) {
                if (instertitialDelegate != null) {
                    instertitialDelegate.didFailToLoadInterstitial(location, error);
                }
            }

            @Override
            public void didCloseInterstitial(String location) {
                if (instertitialDelegate != null) {
                    instertitialDelegate.didCloseInterstitial(location);
                }
            }

            @Override
            public void didClickInterstitial(String location) {
                if (instertitialDelegate != null) {
                    instertitialDelegate.didClickInterstitial(location);
                }
            }

            @Override
            public void didDisplayInterstitial(String location) {
                if (instertitialDelegate != null) {
                    instertitialDelegate.didDisplayInterstitial(location);
                }
            }

            @Override
            public void didCacheRewardedVideo(String location) {
                if (mediaDelegate != null) {
                    mediaDelegate.didCacheRewardedVideo(location);
                }
            }

            @Override
            public void didFailToLoadRewardedVideo(String location,
                                                   CBImpressionError error) {
                if (mediaDelegate != null) {
                    mediaDelegate.didFailToLoadRewardedVideo(location, error);
                }
            }

            @Override
            public void didCloseRewardedVideo(String location) {
                if (mediaDelegate != null) {
                    mediaDelegate.didCloseRewardedVideo(location);
                }
            }

            @Override
            public void didClickRewardedVideo(String location) {
                if (mediaDelegate != null) {
                    mediaDelegate.didClickRewardedVideo(location);
                }
            }

            @Override
            public void didDismissRewardedVideo(String location) {
                if (mediaDelegate != null) {
                    mediaDelegate.didDismissRewardedVideo(location);
                }
            }

            @Override
            public void didCompleteRewardedVideo(String location, int reward) {
                if (mediaDelegate != null) {
                    mediaDelegate.didCompleteRewardedVideo(location, reward);
                }
            }

            @Override
            public void didInitialize() {
              //Do not use the chartboost didInitialize callback, because if the application process is not killed, the second call to the chartboost initialization interface will not return the didInitialize callback
            }
        };
    }

    void onDestroy() {
        hasInitCharboost = false;
    }

    private static class ChartboostExtraHolder {

        private static final ChartboostExtra INSTANCE = new ChartboostExtra();

    }

}
