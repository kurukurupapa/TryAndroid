package com.example.kurukurupapa.oauth03;

import com.google.gson.annotations.SerializedName;

/**
 * Twitter API（GET account/verify_credentials）の結果を保持するクラスです。
 *
 * 参考：
 * GET account / verify_credentials | Twitter Developers
 * https://dev.twitter.com/rest/reference/get/account/verify_credentials
 */
public class TwitterAccount {
    @SerializedName("created_at")
    public String createdAt;
    public String description;
    public String lang;
    public String location;
    public String name;

    @SerializedName("screen_name")
    public String screenName;

    @SerializedName("time_zone")
    public String timeZone;
}
