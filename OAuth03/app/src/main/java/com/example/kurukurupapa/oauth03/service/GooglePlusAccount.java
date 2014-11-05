package com.example.kurukurupapa.oauth03.service;

/**
 * Google+ API（https://www.googleapis.com/plus/v1/people/me）の結果を保持するクラスです。
 */
public class GooglePlusAccount {
    public String kind;
    public String etag;
    public class Email {
        public String value;
        public String type;
    }
    public Email[] emails;
    public String objectType;
    public String id;
    public String displayName;
    public class Name {
        public String familyName;
        public String givenName;
    }
    public Name name;
    public class Image {
        public String url;
        public String isDefault;
    }
    public Image image;
    public String isPlusUser;
    public String language;
    public String verified;
    public String domain;
}
