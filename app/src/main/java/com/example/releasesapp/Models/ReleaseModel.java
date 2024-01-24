package com.example.releasesapp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReleaseModel {
    // When using GSON to parse JSON data into a model class, the serialized name
    // in the model class must match the keys in the JSON data.

    @Expose
    @SerializedName("owner")
    private String owner;

    @Expose
    @SerializedName("track_name")
    private String trackName;

    @Expose
    @SerializedName("artist")
    private String artist;

    @Expose
    @SerializedName("label")
    private String label;

    @Expose
    @SerializedName("isrc")
    private String isrc;

    @Expose
    @SerializedName("upc")
    private String upc;

    @Expose
    @SerializedName("button_states")
    private String buttonStates;

    @Expose
    @SerializedName("progress")
    private float releaseProgress;

    @Expose
    @SerializedName("success")
    private boolean success;

    @Expose
    @SerializedName("message")
    private String message;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getIsrc() {
        return isrc;
    }

    public void setIsrc(String isrc) {
        this.isrc = isrc;
    }

    public String getUpc() {
        return upc;
    }

    public void setUpc(String upc) {
        this.upc = upc;
    }

    public String getButtonStates() {
        return buttonStates;
    }

    public void setButtonStates(String buttonStates) {
        this.buttonStates = buttonStates;
    }

    public float getReleaseProgress() {
        return releaseProgress;
    }

    public void setReleaseProgress(float releaseProgress) {
        this.releaseProgress = releaseProgress;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}