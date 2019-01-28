package com.yumi.android.sdk.ads.adapter.iqzone;

import com.yumi.android.sdk.ads.publish.enumbean.LayerErrorCode;

/**
 * Description:
 * <p>
 * Created by lgd on 2019/1/28.
 */
public class IQZoneUtil {
    public static LayerErrorCode recodeError(LayerErrorCode layerErrorCode){
        layerErrorCode.setExtraMsg("IQZone errorMsg: null");
        return layerErrorCode;
    }
}
