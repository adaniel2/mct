package com.example.releasesapp.Models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ReleaseModelResponse {
    @SerializedName("status_code")
    private int status;

    @SerializedName("message")
    private String message;

    @SerializedName("releases")
    private ArrayList<ReleaseModel> releases;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<ReleaseModel> getReleases() {
        return releases;
    }

    public void setReleases(ArrayList<ReleaseModel> releases) {
        this.releases = releases;
    }

}