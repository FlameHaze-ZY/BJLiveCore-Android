package com.baijiahulian.bjhl_liveplayer;

import android.content.Context;
import android.util.Log;

import com.baijiahulian.livecore.LiveSDK;
import com.baijiahulian.livecore.context.LPError;
import com.baijiahulian.livecore.context.LiveRoom;
import com.baijiahulian.livecore.launch.LPLaunchListener;

/**
 * Created by bjhl on 16/6/23.
 */
public class CoreManger {

    private static CoreManger instance;

    private CoreManger() {
    }

    public static CoreManger getInstance() {
        if (null == instance) {
            instance = new CoreManger();
        }
        return instance;
    }

    public void enterRoom(final Context context, final String code, final String name) {
        LiveSDK.enterRoom(context, code, name, new LPLaunchListener() {
            @Override
            public void onLaunchSteps(int step, int totalStep) {
                Log.i("init steps", "step:" + step + "/" + totalStep);
            }

            @Override
            public void onLaunchError(LPError error) {
                Log.e("error", error.getCode() + " " + error.getMessage());
            }

            @Override
            public void onLaunchSuccess(LiveRoom liveRoom) {
                if (liveRoomInitListener != null)
                    liveRoomInitListener.onInitSuccess(liveRoom);
            }
        });
    }

    public void setLiveRoomInitListener(LiveRoomInitListener liveRoomInitListener) {
        this.liveRoomInitListener = liveRoomInitListener;
    }

    private LiveRoomInitListener liveRoomInitListener;

    public interface LiveRoomInitListener {
        void onInitSuccess(LiveRoom liveRoom);
    }
}
