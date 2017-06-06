package com.yumi.android.sdk.ads.adapter.applovin;

import android.app.Activity;

import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkSettings;
import com.yumi.android.sdk.ads.publish.YumiDebug;

public class ApplovinExtraHolder {

	private static boolean hasInit = false;
	private static AppLovinSdk sdk = null;
	
	
	static void initApplovinSDK(Activity activity, String appkey){
		if (!hasInit ) {
			AppLovinSdkSettings settings = new AppLovinSdkSettings();
			settings.setVerboseLogging(YumiDebug.isDebugMode());
			settings.setAutoPreloadTypes("NONE");
//			settings.setAutoPreloadTypes("VIDEOA,REGULAR");
			sdk = AppLovinSdk.getInstance(appkey, settings, activity);
			hasInit = true;
		}
	}

	static AppLovinSdk getAppLovinSDK(){
		return sdk;
	}

	
	static void destroyHolder(){
		hasInit = false;
		sdk = null;
	}
	
	
}
