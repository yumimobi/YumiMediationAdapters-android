package com.yumi.android.sdk.ads.adapter.inmobi;

import com.inmobi.ads.InMobiAdRequestStatus;
import com.yumi.android.sdk.ads.publish.AdError;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;

import static android.text.TextUtils.isEmpty;

/**
 * Description:
 * <p>
 * Created by lgd on 2019/1/23.
 */
public class InmobUtil {

    public static AdError recodeError(InMobiAdRequestStatus inMobiAdRequestStatus) {
        AdError result;
        if (inMobiAdRequestStatus == null) {
            result = new AdError(LayerErrorCode.ERROR_INTERNAL);
            result.setErrorMessage("Inmobi errorMsg: null");
            return result;
        }
        InMobiAdRequestStatus.StatusCode code = inMobiAdRequestStatus.getStatusCode();
        if (code == null) {
            result = new AdError(LayerErrorCode.ERROR_INTERNAL);
            result.setErrorMessage("Inmobi errorMsg: " + inMobiAdRequestStatus.getMessage());
            return result;
        }

        switch (code) {
            case NO_FILL:
                result = new AdError(LayerErrorCode.ERROR_NO_FILL);
                break;
            case REQUEST_INVALID:
                result = new AdError(LayerErrorCode.ERROR_INVALID);
                break;
            case NETWORK_UNREACHABLE:
                result = new AdError(LayerErrorCode.ERROR_NETWORK_ERROR);
                break;
            default:
                result = new AdError(LayerErrorCode.ERROR_INTERNAL);
                break;
        }
        result.setErrorMessage("Inmobi errorMsg: " + inMobiAdRequestStatus.getMessage());
        return result;
    }

    public static AdError recodeError(LayerErrorCode errorCode) {
        return recodeError(errorCode, null);
    }

    public static AdError recodeError(LayerErrorCode errorCode, String yumiLog) {
        String extraMsg = "Inmobi errorMsg: ";
        if (!isEmpty(yumiLog)) {
            extraMsg += ", " + yumiLog;
        }
        AdError result = new AdError(errorCode);
        result.setErrorMessage(extraMsg);
        return result;
    }

}
