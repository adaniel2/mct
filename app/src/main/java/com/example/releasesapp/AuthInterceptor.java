package com.example.releasesapp;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.releasesapp.Session.SessionManagement;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private SessionManagement sessionManagement;

    public AuthInterceptor(Context ctx) {
        this.sessionManagement = new SessionManagement(ctx);
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder requestBuilder = request.newBuilder();

        // if token saved, add to request
        String token = sessionManagement.getSessionToken();

        if (token != null) {
            requestBuilder.addHeader("Accept", "application/json")
                          .addHeader("Content-Type", "application/json; charset=utf-8")
                          .addHeader("Authorization", "Bearer "
                                  + token);
        }

        return chain.proceed(requestBuilder.build());
    }

}