package com.teesteknoloji.contractanalysis.services;

import com.teesteknoloji.contractanalysis.models.AuthRequestModel;
import com.teesteknoloji.contractanalysis.models.AuthResponseModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface IContractAnalysisApi {
    @POST("Auth/Auth")
    Call<AuthResponseModel> AuthRequest(@Body AuthRequestModel authRequestModel);
}
