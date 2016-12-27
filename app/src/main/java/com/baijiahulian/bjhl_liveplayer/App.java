package com.baijiahulian.bjhl_liveplayer;

import android.app.Application;

import com.baijiahulian.livecore.LiveSDK;
import com.baijiahulian.livecore.context.LPConstants;


/**
 * Created by yanglei on 16/4/12.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // catch捕获的异常
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
        Thread.setDefaultUncaughtExceptionHandler(crashHandler);

        LiveSDK.init("partnerId", LPConstants.LPDeployType.Product);
    }
}
