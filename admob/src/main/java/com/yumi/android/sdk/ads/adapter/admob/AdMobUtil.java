package com.yumi.android.sdk.ads.adapter.admob;

import android.content.Context;
import android.os.Bundle;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.yumi.android.sdk.ads.publish.AdError;
import com.yumi.android.sdk.ads.publish.YumiSettings;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;

/**
 * Description:
 * <p>
 * Created by lgd on 2019/1/23.
 */
public class AdMobUtil {
    public static AdError recodeError(int errorCode) {
        LayerErrorCode errCode;
        switch (errorCode) {
            case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                errCode = LayerErrorCode.ERROR_INTERNAL;
                break;
            case AdRequest.ERROR_CODE_INVALID_REQUEST:
                errCode = LayerErrorCode.ERROR_INVALID;
                break;
            case AdRequest.ERROR_CODE_NO_FILL:
                errCode = LayerErrorCode.ERROR_NO_FILL;
                break;
            case AdRequest.ERROR_CODE_NETWORK_ERROR:
                errCode = LayerErrorCode.ERROR_NETWORK_ERROR;
                break;
            default:
                errCode = LayerErrorCode.ERROR_INTERNAL;
                break;
        }
        AdError adError = new AdError(errCode);
        adError.setErrorMessage("AdMob errorCode: " + errorCode);
        return adError;
    }

    public static AdRequest getAdRequest(Context context) {
        Boolean isConsent = YumiSettings.isGDPRConsent(context);
        if (isConsent == null || isConsent) {
            return new AdRequest.Builder().build();
        }
        // https://developers.google.com/admob/android/eu-consent#forward_consent_to_the_google_mobile_ads_sdk
        Bundle extras = new Bundle();
        extras.putString("npa", "1");
        return new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, extras).build();
    }
}
