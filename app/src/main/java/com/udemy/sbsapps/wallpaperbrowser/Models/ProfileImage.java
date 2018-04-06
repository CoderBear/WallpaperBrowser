package com.udemy.sbsapps.wallpaperbrowser.Models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by michaelmallamo on 6/04/2018.
 */

public class ProfileImage {
    @SerializedName("small")
    private String small;
    @SerializedName("medium")
    private String medium;

    public String getSmall() {
        return small;
    }

    public void setSmall(String small) {
        this.small = small;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }
}
