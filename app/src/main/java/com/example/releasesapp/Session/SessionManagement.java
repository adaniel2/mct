package com.example.releasesapp.Session;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.auth0.android.jwt.JWT;
import com.example.releasesapp.Models.LoginResponse;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class SessionManagement {
    private SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    private MasterKey masterKey;
    private final String SESSION_TOKEN = "session_token"; // session token
    private final String SESSION_USERNAME = "session_username"; // user username
    private final String SESSION_UID = "session_uid"; // user id

    public SessionManagement(Context context) {
        try {
            masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        try {
            sharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    "secret_prefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        editor = sharedPreferences.edit();
    }

    public void saveSession(LoginResponse response) {
        setSessionToken(response);

        JWT jwt = new JWT(getSessionToken());

        setSessionUid(jwt);
        setUsername(jwt);
    }

    public void setSessionUid(JWT jwt) {
        editor.putString(SESSION_UID, jwt.getSubject()).apply();
    }

    public void setUsername(JWT jwt) {
        editor.putString(SESSION_USERNAME, jwt.getClaim("userName").asString()).apply();
    }

    public void setSessionToken(LoginResponse resp) {
        editor.putString(SESSION_TOKEN, resp.getAuthToken()).apply();
    }

    public String getSessionUid() {
        return sharedPreferences.getString(SESSION_UID, null);
    }

    public String getSessionToken() {
        return sharedPreferences.getString(SESSION_TOKEN, null);
    }

    public String getUsername() {
        return sharedPreferences.getString(SESSION_USERNAME, null);
    }

    public void removeSession() {
        editor.putString(SESSION_TOKEN, null).apply();
        editor.putString(SESSION_USERNAME, null).apply();
        editor.putString(SESSION_UID, null).apply();
    }

}