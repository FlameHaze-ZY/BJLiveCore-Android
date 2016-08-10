package com.baijia.liveplayerdemo.models;

import com.baijiahulian.liveplayer.context.LPConstants;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Shubo on 16/7/1.
 */
public class CodeInfoModel extends BaseDataModel {

    @SerializedName("partner_id")
    public String partnerId;

    @SerializedName("user_role")
    public LPConstants.LPUserType userType;

}
