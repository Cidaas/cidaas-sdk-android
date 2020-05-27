package de.cidaas.sdk.android.cidaasVerification.domain.Service.Initiate;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.Map;

import de.cidaas.sdk.android.cidaasVerification.data.Entity.Initiate.InitiateEntity;
import de.cidaas.sdk.android.cidaasVerification.data.Entity.Initiate.InitiateResponse;
import de.cidaas.sdk.android.cidaasVerification.data.Service.CidaasSDK_V2_Service;
import de.cidaas.sdk.android.cidaasVerification.data.Service.ICidaasSDK_V2_Services;
import de.cidaas.sdk.android.helper.commonerror.CommonError;
import de.cidaas.sdk.android.helper.enums.EventResult;
import de.cidaas.sdk.android.helper.enums.WebAuthErrorCode;
import de.cidaas.sdk.android.helper.extension.WebAuthError;
import de.cidaas.sdk.android.helper.logger.LogFile;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InitiateService {
    private Context context;

    public static InitiateService shared;

    CidaasSDK_V2_Service service;


    public InitiateService(Context contextFromCidaas) {
        context = contextFromCidaas;
        if (service == null) {
            service = new CidaasSDK_V2_Service();
        }

    }

    public static InitiateService getShared(@NonNull Context contextFromCidaas) {
        try {

            if (shared == null) {
                shared = new InitiateService(contextFromCidaas);
            }
        } catch (Exception e) {
            LogFile.getInstance(contextFromCidaas).addFailureLog("InitiateService instance Creation Exception:-" + e.getMessage());
        }
        return shared;
    }

    //call initiate Service
    public void callInitiateService(@NonNull String initiateURL, Map<String, String> headers, InitiateEntity initiateEntity,
                                    final EventResult<InitiateResponse> initiateCallback) {
        final String methodName = "InitiateService:-callInitiateService()";
        try {
            //call service
            ICidaasSDK_V2_Services cidaasSDK_v2_services = service.getInstance();
            cidaasSDK_v2_services.initiate(initiateURL, headers, initiateEntity).enqueue(new Callback<InitiateResponse>() {
                @Override
                public void onResponse(Call<InitiateResponse> call, Response<InitiateResponse> response) {
                    if (response.isSuccessful()) {
                        initiateCallback.success(response.body());
                    } else {
                        initiateCallback.failure(CommonError.getShared(context).generateCommonErrorEntity(WebAuthErrorCode.INITIATE_VERIFICATION_FAILURE,
                                response, "Error:- " + methodName));
                    }
                }

                @Override
                public void onFailure(Call<InitiateResponse> call, Throwable t) {
                    initiateCallback.failure(WebAuthError.getShared(context).serviceCallFailureException(WebAuthErrorCode.INITIATE_VERIFICATION_FAILURE,
                            t.getMessage(), "Error:- " + methodName));
                }
            });
        } catch (Exception e) {
            initiateCallback.failure(WebAuthError.getShared(context).methodException("Exception :" + methodName, WebAuthErrorCode.INITIATE_VERIFICATION_FAILURE,
                    e.getMessage()));
        }
    }
}
