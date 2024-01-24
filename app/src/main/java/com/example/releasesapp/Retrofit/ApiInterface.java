package com.example.releasesapp.Retrofit;

import com.example.releasesapp.Models.LoginRequest;
import com.example.releasesapp.Models.LoginResponse;
import com.example.releasesapp.Models.ReleaseModel;
import com.example.releasesapp.Models.ReleaseModelResponse;
import com.example.releasesapp.Models.UserModel;

import java.time.LocalDate;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {
    @POST("loginuser.php")
    Call<LoginResponse> loginUser(@Body LoginRequest request);

    @FormUrlEncoded
    @POST("createaccount.php")
    Call<UserModel> createAccount(@Field("email") String email,
                                  @Field("username") String username, // aka artist
                                  @Field("password") String password,
                                  @Field("password_confirmation") String passwordConfirmation);

    @FormUrlEncoded
    @POST("addrelease.php")
    Call<ReleaseModel> addRelease(@Field("owner") String owner,
                                  @Field("track_name") String trackName,
                                  @Field("artist") String artist,
                                  @Field("label") String label,
                                  @Field("isrc") String isrc,
                                  @Field("upc") String upc,
                                  @Field("release_date") LocalDate date,
                                  @Field("button_states") String buttonStates,
                                  @Field("release_progress") float releaseProgress);

    @GET("getreleases.php")
    Call<ReleaseModelResponse> getReleases(@Query("initState") boolean initState);

}