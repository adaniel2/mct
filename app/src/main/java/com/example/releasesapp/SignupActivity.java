package com.example.releasesapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.releasesapp.Models.UserModel;
import com.example.releasesapp.Retrofit.ApiClient;
import com.example.releasesapp.Retrofit.ApiInterface;
import com.example.releasesapp.databinding.ActivitySignUpBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    ApiInterface apiInterface;

    private ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.buttonCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount(view);
            }

        });

        apiInterface = ApiClient.getApiClient(SignupActivity.this).create(ApiInterface.class);
    }

    public void createAccount(View v) {
        Call<UserModel> userModelCall = apiInterface.createAccount(
                binding.editTextEmail.getText().toString(),
                binding.editTextUsername.getText().toString(),
                binding.editTextPassword.getText().toString(),
                binding.editTextPasswordConfirmation.getText().toString());

        userModelCall.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                if (response.body() != null) {
                    UserModel userModel = response.body();

                    if (userModel.isSuccess()) {
                        Toast.makeText(SignupActivity.this, "Account created. You may now login.",
                                Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        Toast.makeText(SignupActivity.this, userModel.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }

                }

            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                Toast.makeText(SignupActivity.this, "Error, no response.",
                        Toast.LENGTH_LONG).show();
            }

        });

    }

}