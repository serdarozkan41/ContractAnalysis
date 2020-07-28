package com.teesteknoloji.contractanalysis.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.developer.kalert.KAlertDialog;
import com.github.ybq.android.spinkit.style.CubeGrid;
import com.pixplicity.easyprefs.library.Prefs;
import com.teesteknoloji.contractanalysis.MainApplication;
import com.teesteknoloji.contractanalysis.R;
import com.teesteknoloji.contractanalysis.models.AuthRequestModel;
import com.teesteknoloji.contractanalysis.models.AuthResponseModel;
import com.teesteknoloji.contractanalysis.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    ProgressBar progressBar;
    CubeGrid doubleBounce;
    View progressOverlay;
    Button BuLogin;
    EditText TbPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoadingBarInit();

        BuLogin = findViewById(R.id.BuLogin);
        TbPhone = findViewById(R.id.TbPhoneNumber);

        //Constants.StartLoadingAnim(doubleBounce, progressOverlay);
        BuLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.StartLoadingAnim(doubleBounce, progressOverlay);
                if (TbPhone.getText().length() > 0) {
                    final AuthRequestModel requestModel = new AuthRequestModel();
                    requestModel.setPhone(TbPhone.getText().toString().replace("(","").replace(")","").replace(" ",""));
                    MainApplication.apiManager.Auth(requestModel, new Callback<AuthResponseModel>() {
                        @Override
                        public void onResponse(Call<AuthResponseModel> call, Response<AuthResponseModel> response) {
                            AuthResponseModel responseModel = response.body();
                            if (response.isSuccessful()){
                            Prefs.putString("Token", responseModel.getToken());
                            Prefs.putString("Phone", requestModel.getPhone());
                            Prefs.putLong("BayiId", responseModel.getBayiId());
                            Prefs.putLong("UserId", responseModel.getUserId());
                            Prefs.putBoolean("IsLogin", true);
                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                            startActivity(i);
                            finish();
                            }
                            else{
                               Warning("Kullanıcı bilgileri hatalı");
                            }
                        }

                        @Override
                        public void onFailure(Call<AuthResponseModel> call, Throwable t) {
                           Warning("Giriş başarısız.");
                        }
                    });
                } else {
                   Warning("Numara boş olamaz.");
                }
                Constants.StopLoadingAnim(doubleBounce, progressOverlay);
            }
        });

    }

    private void Warning(String message){
        KAlertDialog pDialog = new KAlertDialog(LoginActivity.this, KAlertDialog.ERROR_TYPE);
        pDialog.getProgressHelper().setBarColor(R.color.appRed);
        pDialog.setTitleText("Uyarı");
        pDialog.setContentText(message);
        pDialog.setConfirmText("Tamam");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void LoadingBarInit() {
        progressOverlay = findViewById(R.id.progress_overlay);
        progressBar = findViewById(R.id.PBar_Loading);
        doubleBounce = new CubeGrid();
        progressBar.setIndeterminateDrawable(doubleBounce);
    }
}