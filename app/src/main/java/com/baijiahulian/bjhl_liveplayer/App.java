package com.baijiahulian.bjhl_liveplayer;

import android.app.Application;

import com.baijiahulian.livecore.LiveSDK;
import com.baijiahulian.livecore.context.LPConstants;

//import com.squareup.leakcanary.LeakCanary;

/**
 * Created by yanglei on 16/4/12.
 */
public class App extends Application {
    public static App mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

//        LeakCanary.install(this);
        mInstance = this;
        // catch捕获的异常
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(mInstance);
        Thread.setDefaultUncaughtExceptionHandler(crashHandler);

        LiveSDK.init("partnerId", LPConstants.LPDeployType.Test);
    }
}
