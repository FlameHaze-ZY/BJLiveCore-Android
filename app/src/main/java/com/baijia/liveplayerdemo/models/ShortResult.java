package com.baijia.liveplayerdemo.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by bjhl on 16/6/23.
 */
public class ShortResult<T> extends BaseDataModel {
    @SerializedName("code")
    public int errNo;

    @SerializedName("msg")
    public String message;

    @SerializedName("data")
    public T data;

}

