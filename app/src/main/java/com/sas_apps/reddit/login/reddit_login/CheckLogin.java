package com.sas_apps.reddit.login.reddit_login;
/*
 * Created by Shashank Shinde.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CheckLogin {

    @SerializedName("json")
    @Expose
    public Json json;

    public Json getJson() {
        return json;
    }

    public void setJson(Json json) {
        this.json = json;
    }

    @Override
    public String toString() {
        return "CheckLogin{" +
                "json=" + json +
                '}';
    }
}
