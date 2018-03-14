package com.sas_apps.reddit.login.reddit_login;
/*
 * Created by Shashank Shinde.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("modhash")
    @Expose
    public String modhash;

    @SerializedName("cookie")
    @Expose
    public String cookie;


    public String getModhash() {
        return modhash;
    }

    public void setModhash(String modhash) {
        this.modhash = modhash;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    @Override
    public String toString() {
        return "Data{" +
                "modhash='" + modhash + '\'' +
                ", cookie='" + cookie + '\'' +
                '}';
    }
}