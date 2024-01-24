package com.example.releasesapp.Models;

import com.google.gson.annotations.SerializedName;

public class LoginResponse { // no @Expose means all will be exposed by default
    @SerializedName("status_code")
    private int statusCode;

    @SerializedName("auth_token")
    private String authToken;

    @SerializedName("message")
    private String message;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}