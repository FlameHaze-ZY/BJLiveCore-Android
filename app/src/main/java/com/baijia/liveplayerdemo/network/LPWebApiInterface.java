package com.baijia.liveplayerdemo.network;


import com.baijia.liveplayerdemo.models.CodeInfoModel;
import com.baijia.liveplayerdemo.models.ShortResult;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by bjhl on 16/6/22.
 */
public interface LPWebApiInterface {

    @FormUrlEncoded
    @POST("room/codeinfo")
    Observable<ShortResult<CodeInfoModel>> requestCodeInfo(@Field("code") String code);
}
