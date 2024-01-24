package com.example.releasesapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.releasesapp.Models.LoginRequest;
import com.example.releasesapp.Models.LoginResponse;
import com.example.releasesapp.Retrofit.ApiClient;
import com.example.releasesapp.Retrofit.ApiInterface;
import com.example.releasesapp.Session.SessionManagement;
import com.example.releasesapp.databinding.ActivityLoginBinding;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    ApiInterface apiInterface;

    private ActivityLoginBinding binding;
    String captchaSecretKey = BuildConfig.CAPTCHA_SECRET_KEY;
    String captchaSiteKey = BuildConfig.CAPTCHA_SITE_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // captcha
                //verifyGoogleReCAPTCHA(view);
                loginUser(view);
            }

        });

        binding.buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signupUser(view);
            }
        });

        apiInterface = ApiClient.getApiClient(LoginActivity.this).create(ApiInterface.class);
    }

    @Override
    protected void onStart() { // i think this should somehow check token expiration
        super.onStart();

        // logged in check
        SessionManagement sessionManagement = new SessionManagement(LoginActivity.this);

        if (sessionManagement.getSessionToken() != null) {
            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            // do nothing for now
        }

    }

    private void loginUser(View v) {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(binding.editTextEmail.getText().toString());
        loginRequest.setPassword(binding.editTextPassword.getText().toString());

        Call<LoginResponse> loginResponseCall = apiInterface.loginUser(loginRequest);

        loginResponseCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    if (loginResponse.getStatusCode() == 200 && loginResponse.getAuthToken() != null) {
                        // save session
                        SessionManagement sessionManagement = new SessionManagement(LoginActivity.this);
                        sessionManagement.saveSession(loginResponse);

                        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, loginResponse.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }

                }

            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error, no response.",
                        Toast.LENGTH_LONG).show();

                Log.d("debug", t.getMessage());
            }

        });

    }

    private void signupUser(View v) {
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    private void verifyGoogleReCAPTCHA(View view) {
        SafetyNet.getClient(LoginActivity.this).verifyWithRecaptcha(captchaSiteKey)
                 .addOnSuccessListener((Activity) LoginActivity.this,
                         new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                             @Override
                             public void onSuccess(SafetyNetApi.RecaptchaTokenResponse response) {
                                 // Indicates communication with reCAPTCHA service was
                                 // successful.
                                 String userResponseToken = response.getTokenResult();

                                 if (!userResponseToken.isEmpty()) {
                                     // Validate the user response token using the
                                     // reCAPTCHA siteverify API.

                                     // log in the user
                                     //loginUser(view);
                                 }

                             }

                         })
                 .addOnFailureListener((Activity) LoginActivity.this, new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull Exception e) {
                         if (e instanceof ApiException) {
                             // An error occurred when communicating with the
                             // reCAPTCHA service. Refer to the status code to
                             // handle the error appropriately.
                             ApiException apiException = (ApiException) e;
                             int statusCode = apiException.getStatusCode();

                         } else {

                         }

                     }

                 });
    }

}