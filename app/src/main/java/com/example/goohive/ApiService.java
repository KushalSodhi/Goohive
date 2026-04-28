package com.example.goohive;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    @Multipart
    @POST("upload")
    Call<Void> uploadFile(@Part MultipartBody.Part file);

    @GET("files")
    Call<Map<String, List<String>>> getFiles();

    @GET("delete/{filename}")
    Call<Void> deleteFile(@Path("filename") String filename);
}