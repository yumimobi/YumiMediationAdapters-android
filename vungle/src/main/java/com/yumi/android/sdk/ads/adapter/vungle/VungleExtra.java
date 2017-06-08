package com.yumi.android.sdk.ads.adapter.vungle;

import com.vungle.publisher.VunglePub;

import android.app.Activity;

public class VungleExtra {

	private static class VungleExtraHolder{
		private static final VungleExtra EXTRA = new VungleExtra();
	}
	
	static final VungleExtra getExtra(){
		return VungleExtraHolder.EXTRA;
	}

	private VungleExtra(){
		
	}
	
	private boolean hasInitVungle;
	
	void initVungle(Activity activity, String appid){
		if (!hasInitVungle) {
			hasInitVungle = true;
			VunglePub.getInstance().init(activity, appid);
		}
	};
	
	void onDestroy(){
		hasInitVungle = false;
	}
	
}
