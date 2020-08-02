package com.teesteknoloji.contractanalysis.services;

import com.scanlibrary.models.SendFormRequestModel;
import com.teesteknoloji.contractanalysis.models.AuthRequestModel;
import com.teesteknoloji.contractanalysis.models.AuthResponseModel;
import com.teesteknoloji.contractanalysis.models.MenuResponseModel;
import com.teesteknoloji.contractanalysis.models.SendResponseModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IContractAnalysisApi {
    @POST("Auth/Auth")
    Call<AuthResponseModel> AuthRequest(@Body AuthRequestModel authRequestModel);

    @GET("Menu/GetMenus")
    Call<MenuResponseModel> getMenus(@Query("BayiId") long BayiId, @Query("UserId") long UserId, @Header("Authorization") String authHeader);

    @POST("Form/SendForm")
    Call<SendResponseModel> SendForm(@Body SendFormRequestModel sendRequestModel);
}
