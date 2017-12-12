package com.yumi.android.sdk.ads.adapter.vungle;

import android.app.Activity;

import com.vungle.publisher.AdConfig;
import com.vungle.publisher.Orientation;
import com.vungle.publisher.VungleInitListener;
import com.vungle.publisher.VunglePub;
import com.yumi.android.sdk.ads.utils.ZplayDebug;

/**
 *vungle 获取对象和初始化类
 */
public class VungleInstantiate {

	private static final String TAG = "VungleExtra";
	private static final boolean onoff = true;

	private static class VungleInstantiateHolder {
		private static final VungleInstantiate instantiate = new VungleInstantiate();
	}
	static final VungleInstantiate getInstantiate(){
		return VungleInstantiateHolder.instantiate;
	}

	private final VunglePub vungle = VunglePub.getInstance();
	private boolean hasInitVungle;
	
	public void initVungle(Activity activity, String appid, String[] placementIds){
		if (!hasInitVungle) {
			hasInitVungle = true;
			vungle.init(activity, appid, placementIds, new VungleInitListener() {
				@Override
				public void onSuccess() {
					AdConfig overrideConfig = vungle.getGlobalAdConfig();
					overrideConfig.setSoundEnabled(true);
					overrideConfig.setOrientation(Orientation.autoRotate);
					ZplayDebug.d(TAG, "vungle initVungleSDK onSuccess()", onoff);
				}
				@Override
				public void onFailure(Throwable e) {
					ZplayDebug.d(TAG, "vungle initVungleSDK onFailure() Throwable Message : "+e.getMessage(), onoff);
				}
			});
			ZplayDebug.d(TAG, "vungle initVungleSDK vungle.init", onoff);
		}else {
			ZplayDebug.d(TAG, "vungle initVungleSDK vungle initialized", onoff);
		}
	};

	public VunglePub getVunglePub()
	{
		return vungle;
	}
	
	void onDestroy(){
		hasInitVungle = false;
	}
	
}
