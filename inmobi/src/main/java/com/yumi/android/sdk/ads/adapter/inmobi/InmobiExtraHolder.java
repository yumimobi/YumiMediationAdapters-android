package com.yumi.android.sdk.ads.adapter.inmobi;

import android.app.Activity;

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

    public static LayerErrorCode decodeError(StatusCode code) {
        LayerErrorCode error;
        if(code == null){
            return LayerErrorCode.ERROR_INTERNAL;
        }

        switch (code) {
            case NO_FILL:
                error = LayerErrorCode.ERROR_NO_FILL;
                break;
            case REQUEST_INVALID:
                error = LayerErrorCode.ERROR_INVALID;
                break;
            case NETWORK_UNREACHABLE:
                error = LayerErrorCode.ERROR_NETWORK_ERROR;
                break;
            default:
                error = LayerErrorCode.ERROR_INTERNAL;
                break;
        }
        error.setExtraMsg(code.toString());
        return error;
    }


	static void onDestroy() {
		isInitlalize = false;
	}

}
