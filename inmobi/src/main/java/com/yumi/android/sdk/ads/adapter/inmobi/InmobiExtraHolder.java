package com.yumi.android.sdk.ads.adapter.inmobi;

import android.app.Activity;

import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiAdRequestStatus.StatusCode;
import com.inmobi.sdk.InMobiSdk;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;

public class InmobiExtraHolder {

	static boolean isInitlalize = false;

	static void initInmobiSDK(Activity activity, String appid) {
		if (!isInitlalize) {
			InMobiSdk.init(activity, appid);
			InMobiSdk.setLogLevel(InMobiSdk.LogLevel.DEBUG);
			isInitlalize = true;
		}
	}

	static void onDestroy() {
		isInitlalize = false;
	}

}
