package com.baijiahulian.bjhl_liveplayer;

import android.app.Application;
import android.util.Log;


/**
 * Created by yanglei on 16/4/12.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("sdfds", "" + NotUsedClass.H);
        // catch捕获的异常
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
        Thread.setDefaultUncaughtExceptionHandler(crashHandler);
    }
}
