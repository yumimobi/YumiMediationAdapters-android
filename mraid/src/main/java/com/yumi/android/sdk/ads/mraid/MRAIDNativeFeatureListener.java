package com.yumi.android.sdk.ads.mraid;

/******************************************************************************
 * A listener for MRAIDView/MRAIDInterstitial to listen for notifications
 * when the following native features are requested from a creative:
 * 
 *   * make a phone call
 *   * add a calendar entry
 *   * play a video (external)
 *   * open a web page in a browser
 *   * store a picture
 *   * send an SMS
 *   
 * If you don't implement this interface, the default for
 * supporting these features in the creative will be false.
 ******************************************************************************/
        
public interface MRAIDNativeFeatureListener {

    public void mraidNativeFeatureCallTel(String url);

    public void mraidNativeFeatureCreateCalendarEvent(String eventJSON);

    public void mraidNativeFeaturePlayVideo(String url);

    public void mraidNativeFeatureOpenBrowser(String url);

    public void mraidNativeFeatureStorePicture(String url);

    public void mraidNativeFeatureSendSms(String url);

}
