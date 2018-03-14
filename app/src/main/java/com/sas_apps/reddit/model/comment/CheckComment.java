package com.sas_apps.reddit.model.comment;
/*
 * Created by Shashank Shinde.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CheckComment {

    @SerializedName("success")
    @Expose
    private String isSuccess;

    public CheckComment(String isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(String isSuccess) {
        this.isSuccess = isSuccess;
    }

    @Override
    public String toString() {
        return "CheckComment{" +
                "isSuccess=" + isSuccess +
                '}';
    }
}
