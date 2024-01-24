package com.example.releasesapp;

public class Track {
    public float progress;
    public int image;
    public String isrc;
    public String trackName;

    public Track(float progress, int image, String isrc, String trackName) {
        this.progress = progress;
        this.image = image;
        this.isrc = isrc;
        this.trackName = trackName;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getIsrc() {
        return isrc;
    }

    public void setIsrc(String isrc) {
        this.isrc = isrc;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

}