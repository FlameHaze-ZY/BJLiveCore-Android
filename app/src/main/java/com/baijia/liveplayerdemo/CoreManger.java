package com.baijia.liveplayerdemo;

import android.content.Context;
import android.widget.Toast;

import com.baijia.liveplayerdemo.models.CodeInfoModel;
import com.baijia.liveplayerdemo.network.WebServer;
import com.baijiahulian.liveplayer.LPErrorListener;
import com.baijiahulian.liveplayer.LivePlayerSDK;
import com.baijiahulian.liveplayer.context.LPConstants;
import com.baijiahulian.liveplayer.utils.LPError;

import rx.functions.Action1;

/**
 * Created by bjhl on 16/6/23.
 */
public class CoreManger {

    private static CoreManger instance;
    private WebServer webServer;

    private LPConstants.LPDeployType mDeployType = LPConstants.LPDeployType.Test;

    private CoreManger() {
    }

    public static CoreManger getInstance() {
        if (null == instance) {
            instance = new CoreManger();
        }
        return instance;
    }

    public WebServer getWebServer() {
        if (webServer == null) {
            webServer = WebServer.getNewInstance(LPConstants.HOSTS_WEB[mDeployType.getType()]);
        }
        return webServer;
    }

    public void setDeployType(LPConstants.LPDeployType deployType) {
        webServer = null;
        this.mDeployType = deployType;
    }

    public void enterRoom(final Context context, final String code, final String name) {
        getWebServer().requestCodeInfo(code).subscribe(new Action1<CodeInfoModel>() {
            @Override
            public void call(CodeInfoModel codeInfoModel) {
                LivePlayerSDK.enterRoomQuick(context,
                        code,
                        name,
                        codeInfoModel.userType,
                        mDeployType,
                        codeInfoModel.partnerId,
                        new LPErrorListener() {
                            @Override
                            public void LPRoomCompletion(boolean b, LPError lpError) {
                                if (!b)
                                    Toast.makeText(context, lpError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
                Toast.makeText(context, throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
