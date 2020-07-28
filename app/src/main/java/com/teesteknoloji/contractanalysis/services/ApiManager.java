package com.teesteknoloji.contractanalysis.services;

import com.teesteknoloji.contractanalysis.models.AuthRequestModel;
import com.teesteknoloji.contractanalysis.models.AuthResponseModel;
import com.teesteknoloji.contractanalysis.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiManager {
    private static IContractAnalysisApi service;
    private static ApiManager apiManager;

    private ApiManager() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(IContractAnalysisApi.class);
    }

    public static ApiManager getInstance() {
        if (apiManager == null) {
            apiManager = new ApiManager();
        }
        return apiManager;
    }

    public void Auth(AuthRequestModel authRequestModel, Callback<AuthResponseModel> callback) {
        Call<AuthResponseModel> authCall = service.AuthRequest(authRequestModel);
        authCall.enqueue(callback);
    }

}
