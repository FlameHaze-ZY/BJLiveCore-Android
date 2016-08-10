package com.baijia.liveplayerdemo.network;

import com.baijia.liveplayerdemo.models.CodeInfoModel;
import com.baijia.liveplayerdemo.models.ShortResult;
import com.baijia.liveplayerdemo.utils.JsonUtils;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by bjhl on 16/4/6.
 */
public class WebServer {

    private LPWebApiInterface apiInterface;

    public WebServer(LPWebApiInterface apiInterface) {
        this.apiInterface = apiInterface;
    }

    public static WebServer getNewInstance(String endPoint) {

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(JsonUtils.gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(endPoint)
                .build();


        return new WebServer(retrofit.create(LPWebApiInterface.class));
    }

    public Observable<CodeInfoModel> requestCodeInfo(String code) {
        return apiInterface.requestCodeInfo(code)
                .map(new Func1<ShortResult<CodeInfoModel>, CodeInfoModel>() {
                    @Override
                    public CodeInfoModel call(ShortResult<CodeInfoModel> result) {
                        RuntimeException exception = analyticsResponse(result);
                        if (exception != null) {
                            throw exception;
                        }
                        return result.data;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private RuntimeException analyticsResponse(ShortResult response) {
        if (response == null) {
            return new NullPointerException("response is null.");
        }
        long errNo = response.errNo;

        if (errNo != 0) {
            return new LPException(errNo, response.message);
        }
        return null;
    }

}
