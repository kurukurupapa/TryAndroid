package com.example.kurukurupapa.oauth03;

/**
 * https://www.googleapis.com/oauth2/v2/userinfo の結果を保持するクラスです。
 */
public class GoogleOAuthUserInfo {
    public String id;
    public String email;
    public String verified_email;
    public String name;
    public String givenName;
    public String familyName;
    public String picture;
    public String locale;
    public String hd;
}
