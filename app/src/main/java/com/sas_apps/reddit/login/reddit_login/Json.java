package com.sas_apps.reddit.login.reddit_login;
/*
 * Created by Shashank Shinde.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Json {

    @SerializedName("data")
    @Expose
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Json{" +
                "data=" + data +
                '}';
    }
}