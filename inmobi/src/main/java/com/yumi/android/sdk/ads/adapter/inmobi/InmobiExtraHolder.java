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

    public static LayerErrorCode decodeError(InMobiAdRequestStatus inMobiAdRequestStatus) {
	    if (inMobiAdRequestStatus == null) {
            return LayerErrorCode.ERROR_INTERNAL;
        }
        StatusCode code = inMobiAdRequestStatus.getStatusCode();
	    if(code == null){
	        LayerErrorCode result = LayerErrorCode.ERROR_INTERNAL;
	        result.setExtraMsg(inMobiAdRequestStatus.getMessage());
	        return result;
        }
        
        LayerErrorCode result;
        switch (code) {
            case NO_FILL:
                result = LayerErrorCode.ERROR_NO_FILL;
                break;
            case REQUEST_INVALID:
                result = LayerErrorCode.ERROR_INVALID;
                break;
            case NETWORK_UNREACHABLE:
                result = LayerErrorCode.ERROR_NETWORK_ERROR;
                break;
            default:
                result = LayerErrorCode.ERROR_INTERNAL;
                break;
        }
        result.setExtraMsg("Inmobi errorMsg: " + inMobiAdRequestStatus.getMessage());
        return result;
    }


	static void onDestroy() {
		isInitlalize = false;
	}

}
