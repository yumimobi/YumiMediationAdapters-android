package com.yumi.android.sdk.ads.adapter.chartboost;

import android.content.Context;

import com.chartboost.sdk.Chartboost;
import com.chartboost.sdk.Model.CBError;
import com.yumi.android.sdk.ads.publish.AdError;
import com.yumi.android.sdk.ads.publish.YumiSettings;
import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;
import com.yumi.android.sdk.ads.publish.enumbean.YumiGDPRStatus;

import static com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode.ERROR_INTERNAL;

/**
 * Description:
 * <p>
 * Created by lgd on 2019/1/23.
 */
public class ChartboostUtil {
    static AdError recodeError(CBError.CBImpressionError chartBoostError) {
        AdError result;
        if (chartBoostError == null) {
            result = new AdError(ERROR_INTERNAL);
            result.setErrorMessage("ChartBoost errorMes: null");
            return result;
        }

        LayerErrorCode errCode;
        switch (chartBoostError) {
            case INTERNAL:
                errCode = ERROR_INTERNAL;
                break;
            case NO_AD_FOUND:
                errCode = LayerErrorCode.ERROR_NO_FILL;
                break;
            case INVALID_LOCATION:
                errCode = LayerErrorCode.ERROR_INVALID;
                break;
            case NETWORK_FAILURE:
                errCode = LayerErrorCode.ERROR_NETWORK_ERROR;
                break;
            default:
                errCode = ERROR_INTERNAL;
                break;
        }
        result = new AdError(errCode);
        result.setErrorMessage("ChartBoost errorMes: " + chartBoostError.toString());
        return result;
    }

    static void updateGDPRStatus(Context context){

        if(YumiSettings.getGDPRStatus() == YumiGDPRStatus.UNKNOWN){
            return;
        }

        boolean isConsent = YumiSettings.getGDPRStatus() == YumiGDPRStatus.PERSONALIZED;
        // https://answers.chartboost.com/en-us/child_article/android#gdpr
        if(isConsent) {
            Chartboost.setPIDataUseConsent(context, Chartboost.CBPIDataUseConsent.YES_BEHAVIORAL);
        }else {
            Chartboost.setPIDataUseConsent(context, Chartboost.CBPIDataUseConsent.NO_BEHAVIORAL);
        }
    }
}
