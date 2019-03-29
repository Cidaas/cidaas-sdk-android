package com.example.cidaasv2.Service.Repository.Verification.Fingerprint;

import android.content.Context;

import com.example.cidaasv2.Helper.CommonError.CommonError;
import com.example.cidaasv2.Helper.Entity.CommonErrorEntity;
import com.example.cidaasv2.Helper.Entity.DeviceInfoEntity;
import com.example.cidaasv2.Helper.Entity.ErrorEntity;
import com.example.cidaasv2.Helper.Enums.Result;
import com.example.cidaasv2.Helper.Enums.WebAuthErrorCode;
import com.example.cidaasv2.Helper.Extension.WebAuthError;
import com.example.cidaasv2.Helper.Genral.DBHelper;
import com.example.cidaasv2.Helper.URLHelper.URLHelper;
import com.example.cidaasv2.Helper.Logger.LogFile;
import com.example.cidaasv2.Library.LocationLibrary.LocationDetails;
import com.example.cidaasv2.R;
import com.example.cidaasv2.Service.CidaassdkService;
import com.example.cidaasv2.Service.Entity.MFA.AuthenticateMFA.Fingerprint.AuthenticateFingerprintRequestEntity;
import com.example.cidaasv2.Service.Entity.MFA.AuthenticateMFA.Fingerprint.AuthenticateFingerprintResponseEntity;
import com.example.cidaasv2.Service.Entity.MFA.EnrollMFA.Fingerprint.EnrollFingerprintMFARequestEntity;
import com.example.cidaasv2.Service.Entity.MFA.EnrollMFA.Fingerprint.EnrollFingerprintMFAResponseEntity;
import com.example.cidaasv2.Service.Entity.MFA.InitiateMFA.Fingerprint.InitiateFingerprintMFARequestEntity;
import com.example.cidaasv2.Service.Entity.MFA.InitiateMFA.Fingerprint.InitiateFingerprintMFAResponseEntity;
import com.example.cidaasv2.Service.Entity.MFA.SetupMFA.Fingerprint.SetupFingerprintMFARequestEntity;
import com.example.cidaasv2.Service.Entity.MFA.SetupMFA.Fingerprint.SetupFingerprintMFAResponseEntity;
import com.example.cidaasv2.Service.ICidaasSDKService;
import com.example.cidaasv2.Service.Scanned.ScannedRequestEntity;
import com.example.cidaasv2.Service.Scanned.ScannedResponseEntity;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class FingerprintVerificationService {
    CidaassdkService service;
    private ObjectMapper objectMapper=new ObjectMapper();
    //Local variables
    private String statusId;
    private String authenticationType;
    private String sub;
    private String verificationType;
    private Context context;

    public static FingerprintVerificationService shared;

    public FingerprintVerificationService(Context contextFromCidaas) {
        sub="";
        statusId="";
        verificationType="";
        context=contextFromCidaas;
        authenticationType="";
        //Todo setValue for authenticationType

        if(service==null) {
            service=new CidaassdkService();
        }

    }



    public static FingerprintVerificationService getShared(Context contextFromCidaas )
    {
        try {

            if (shared == null) {
                shared = new FingerprintVerificationService(contextFromCidaas);
            }
        }
        catch (Exception e)
        {
            Timber.i(e.getMessage());
        }
        return shared;
    }

    public void scannedFingerprint(final String baseurl, ScannedRequestEntity scannedRequestEntity, DeviceInfoEntity deviceInfoEntityFromParam,
                                   final Result<ScannedResponseEntity> callback)
    {
        String scannedFingerprintUrl="";
        try
        {
            if(baseurl!=null && !baseurl.equals("")){
                //Construct URL For RequestId
                scannedFingerprintUrl=baseurl+ URLHelper.getShared().getScannedFingerprintURL();
            }
            else {
                callback.failure( WebAuthError.getShared(context).serviceFailureException(WebAuthErrorCode.PROPERTY_MISSING,
                        context.getString(R.string.PROPERTY_MISSING), 400,null,null));
                return;
            }

            Map<String, String> headers = new Hashtable<>();
            // Get Device Information
            DeviceInfoEntity deviceInfoEntity=new DeviceInfoEntity();
            //This is only for testing purpose
            if(deviceInfoEntityFromParam==null) {
                deviceInfoEntity = DBHelper.getShared().getDeviceInfo();
            }
            else if(deviceInfoEntityFromParam!=null)
            {
                deviceInfoEntity=deviceInfoEntityFromParam;
            }
            //Todo - check Construct Headers pending,Null Checking Pending
            //Add headers
            headers.put("Content-Type", URLHelper.contentTypeJson);
            headers.put("verification_api_version","2");
            headers.put("lat", LocationDetails.getShared(context).getLatitude());
            headers.put("lon",LocationDetails.getShared(context).getLongitude());



            if(DBHelper.getShared().getFCMToken()!=null && !DBHelper.getShared().getFCMToken().equals("")) {
                deviceInfoEntity.setPushNotificationId(DBHelper.getShared().getFCMToken());
            }


            scannedRequestEntity.setDeviceInfo(deviceInfoEntity);

            //Call Service-getRequestId
            final ICidaasSDKService cidaasSDKService = service.getInstance();

            cidaasSDKService.scanned(scannedFingerprintUrl,headers, scannedRequestEntity).enqueue(new Callback<ScannedResponseEntity>() {
                @Override
                public void onResponse(Call<ScannedResponseEntity> call, Response<ScannedResponseEntity> response) {
                    if (response.isSuccessful()) {
                        if(response.code()==200) {
                            DBHelper.getShared().setUserDeviceId(response.body().getData().getUserDeviceId(),baseurl);
                            callback.success(response.body());
                        }
                        else {
                            callback.failure( WebAuthError.getShared(context).serviceFailureException(WebAuthErrorCode.SCANNED_FINGERPRINT_MFA_FAILURE,
                                    "Service failure but successful response" , response.code(),null,null));
                        }
                    }
                    else {
                        assert response.errorBody() != null;
                        callback.failure(CommonError.getShared(context).generateCommonErrorEntity(WebAuthErrorCode.SCANNED_FINGERPRINT_MFA_FAILURE,response));
                    }
                }

                @Override
                public void onFailure(Call<ScannedResponseEntity> call, Throwable t) {
                    Timber.e("Failure in Scanned Fingerprint service call"+t.getMessage());
                    LogFile.getShared(context).addRecordToLog("Scanned Fingerprint Service Failure"+t.getMessage());
                    callback.failure( WebAuthError.getShared(context).serviceFailureException(WebAuthErrorCode.SCANNED_FINGERPRINT_MFA_FAILURE,t.getMessage(), 400,null,null));
                }
            });


        }
        catch (Exception e)
        {
            LogFile.getShared(context).addRecordToLog("Scanned Fingerprint Service exception"+e.getMessage());
           callback.failure(WebAuthError.getShared(context).serviceException(WebAuthErrorCode.SCANNED_FINGERPRINT_MFA_FAILURE));
            Timber.e("Scanned Fingerprint Service exception"+e.getMessage());
        }
    }


    //setupFingerprintMFA
    public void setupFingerprint(String baseurl, String accessToken, SetupFingerprintMFARequestEntity setupFingerprintMFARequestEntity, DeviceInfoEntity deviceInfoEntityFromParam,final Result<SetupFingerprintMFAResponseEntity> callback)
    {
        String setupFingerprintMFAUrl="";
        try
        {
            if(baseurl!=null && !baseurl.equals("")){
                //Construct URL For RequestId
                setupFingerprintMFAUrl=baseurl+URLHelper.getShared().getSetupFingerprintMFA();
            }
            else {
                callback.failure( WebAuthError.getShared(context).serviceFailureException(WebAuthErrorCode.PROPERTY_MISSING,
                        context.getString(R.string.PROPERTY_MISSING), 400,null,null));
                return;
            }


            Map<String, String> headers = new Hashtable<>();
            // Get Device Information
            DeviceInfoEntity deviceInfoEntity=new DeviceInfoEntity();
            //This is only for testing purpose
            if(deviceInfoEntityFromParam==null) {
                deviceInfoEntity = DBHelper.getShared().getDeviceInfo();
            }
            else if(deviceInfoEntityFromParam!=null)
            {
                deviceInfoEntity=deviceInfoEntityFromParam;
            }
            //Todo - check Construct Headers pending,Null Checking Pending
            //Add headers
            headers.put("Content-Type", URLHelper.contentTypeJson);
            headers.put("verification_api_version","2");
            headers.put("access_token",accessToken);
            headers.put("lat",LocationDetails.getShared(context).getLatitude());
            headers.put("lon",LocationDetails.getShared(context).getLongitude());



            if(DBHelper.getShared().getFCMToken()!=null && !DBHelper.getShared().getFCMToken().equals("")) {

                //Todo Chaange to FCM acceptence now it is in Authenticator
                 deviceInfoEntity.setPushNotificationId(DBHelper.getShared().getFCMToken());

               // deviceInfoEntity.setPushNotificationId("eEpg_hEUumQ:APA91bG23jgQk-0BOzdE-CpQfcao86c6SBdu600X8WsihKm5rtO58Bbq9-T3T7_kleYkIs6Mr1mdbZBYaE-h583cucUYOd8ok5lljmaeT15QQqgZl9S6MTIHlqoS-TNYilEoXy17mcJco7iDiYlDzjwlrZHtp4O6VQ");
            }
            setupFingerprintMFARequestEntity.setDeviceInfo(deviceInfoEntity);

            //Call Service-getRequestId
            final ICidaasSDKService cidaasSDKService = service.getInstance();

            cidaasSDKService.setupFingerprintMFA(setupFingerprintMFAUrl,headers, setupFingerprintMFARequestEntity).enqueue(new Callback<SetupFingerprintMFAResponseEntity>() {
                @Override
                public void onResponse(Call<SetupFingerprintMFAResponseEntity> call, Response<SetupFingerprintMFAResponseEntity> response) {
                    if (response.isSuccessful()) {
                        if(response.code()==200) {
                            callback.success(response.body());

                            //Todo Listen For The Push notification to recieve

                        }
                        else {
                            callback.failure( WebAuthError.getShared(context).serviceFailureException(WebAuthErrorCode.SETUP_FINGERPRINT_MFA_FAILURE,
                                    "Service failure but successful response" , response.code(),null,null));
                        }
                    }
                    else {
                        assert response.errorBody() != null;
                        callback.failure(CommonError.getShared(context).generateCommonErrorEntity(WebAuthErrorCode.SETUP_FINGERPRINT_MFA_FAILURE,response));
                    }
                }

                @Override
                public void onFailure(Call<SetupFingerprintMFAResponseEntity> call, Throwable t) {
                    Timber.e("Failure in Setup Fingerprint service call"+t.getMessage());
                    LogFile.getShared(context).addRecordToLog("Setup Fingerprint Service Failure"+t.getMessage());
                    callback.failure( WebAuthError.getShared(context).serviceFailureException(WebAuthErrorCode.SETUP_FINGERPRINT_MFA_FAILURE,t.getMessage(), 400,null,null));
                }
            });


        }
        catch (Exception e)
        {
            LogFile.getShared(context).addRecordToLog("acceptConsent Service exception"+e.getMessage());
           callback.failure(WebAuthError.getShared(context).serviceException(WebAuthErrorCode.SETUP_FINGERPRINT_MFA_FAILURE));
            Timber.e("Setup Fingerprint Service exception"+e.getMessage());
        }
    }


    //enrollFingerprintMFA
    public void enrollFingerprint(String baseurl, String accessToken, EnrollFingerprintMFARequestEntity enrollFingerprintMFARequestEntity,DeviceInfoEntity deviceInfoEntityFromParam,
                                     final Result<EnrollFingerprintMFAResponseEntity> callback)
    {
        String enrollFingerprintMFAUrl="";
        try
        {
            if(baseurl!=null && !baseurl.equals("")){
                //Construct URL For RequestId
                enrollFingerprintMFAUrl=baseurl+URLHelper.getShared().getEnrollFingerprintMFA();
            }
            else {
                callback.failure( WebAuthError.getShared(context).serviceFailureException(WebAuthErrorCode.PROPERTY_MISSING,
                        context.getString(R.string.PROPERTY_MISSING), 400,null,null));
                return;
            }

            Map<String, String> headers = new Hashtable<>();
            // Get Device Information
            DeviceInfoEntity deviceInfoEntity=new DeviceInfoEntity();
            //This is only for testing purpose
            if(deviceInfoEntityFromParam==null) {
                deviceInfoEntity = DBHelper.getShared().getDeviceInfo();
            }
            else if(deviceInfoEntityFromParam!=null)
            {
                deviceInfoEntity=deviceInfoEntityFromParam;
            }
            //Todo - check Construct Headers pending,Null Checking Pending
            //Add headers
            headers.put("Content-Type", URLHelper.contentTypeJson);
             headers.put("access_token",accessToken);
            headers.put("verification_api_version","2");
            headers.put("lat",LocationDetails.getShared(context).getLatitude());
            headers.put("lon",LocationDetails.getShared(context).getLongitude());

            if(DBHelper.getShared().getFCMToken()!=null && !DBHelper.getShared().getFCMToken().equals("")) {
                deviceInfoEntity.setPushNotificationId(DBHelper.getShared().getFCMToken());
            }


            enrollFingerprintMFARequestEntity.setDeviceInfo(deviceInfoEntity);
            enrollFingerprintMFARequestEntity.setVerifierPassword(deviceInfoEntity.getDeviceId());

            //Call Service-getRequestId
            final ICidaasSDKService cidaasSDKService = service.getInstance();

            cidaasSDKService.enrollFingerprintMFA(enrollFingerprintMFAUrl,headers, enrollFingerprintMFARequestEntity).enqueue(new Callback<EnrollFingerprintMFAResponseEntity>() {
                @Override
                public void onResponse(Call<EnrollFingerprintMFAResponseEntity> call, Response<EnrollFingerprintMFAResponseEntity> response) {
                    if (response.isSuccessful()) {
                        if(response.code()==200) {
                            callback.success(response.body());
                        }
                        else {
                            callback.failure( WebAuthError.getShared(context).serviceFailureException(WebAuthErrorCode.ENROLL_FINGERPRINT_MFA_FAILURE,
                                    "Service failure but successful response" , response.code(),null,null));
                        }
                    }
                    else {
                        assert response.errorBody() != null;
                        callback.failure(CommonError.getShared(context).generateCommonErrorEntity(WebAuthErrorCode.ENROLL_FINGERPRINT_MFA_FAILURE,response));
                    }
                }

                @Override
                public void onFailure(Call<EnrollFingerprintMFAResponseEntity> call, Throwable t) {
                    Timber.e("Failure in Enroll Fingerprint service call"+t.getMessage());
                    LogFile.getShared(context).addRecordToLog("Enroll Fingerprint Service Failure"+t.getMessage());
                    callback.failure( WebAuthError.getShared(context).serviceFailureException(WebAuthErrorCode.ENROLL_FINGERPRINT_MFA_FAILURE,t.getMessage(), 400,null,null));
                }
            });


        }
        catch (Exception e)
        {
            LogFile.getShared(context).addRecordToLog("Enroll Fingerprint Service exception"+e.getMessage());
           callback.failure(WebAuthError.getShared(context).serviceException(WebAuthErrorCode.ENROLL_FINGERPRINT_MFA_FAILURE));
            Timber.e("Enroll Fingerprint Service exception"+e.getMessage());
        }
    }

    //initiateFingerprintMFA
    public void initiateFingerprint(String baseurl, InitiateFingerprintMFARequestEntity initiateFingerprintMFARequestEntity,DeviceInfoEntity deviceInfoEntityFromParam,
                                       final Result<InitiateFingerprintMFAResponseEntity> callback)
    {
        String initiateFingerprintMFAUrl="";
        try
        {
            if(baseurl!=null && !baseurl.equals("")){
                //Construct URL For RequestId
                initiateFingerprintMFAUrl=baseurl+URLHelper.getShared().getInitiateFingerprintMFA();
            }
            else {
                callback.failure( WebAuthError.getShared(context).serviceFailureException(WebAuthErrorCode.PROPERTY_MISSING,
                        context.getString(R.string.PROPERTY_MISSING), 400,null,null));
                return;
            }

            Map<String, String> headers = new Hashtable<>();
            // Get Device Information
            DeviceInfoEntity deviceInfoEntity=new DeviceInfoEntity();
            //This is only for testing purpose
            if(deviceInfoEntityFromParam==null) {
                deviceInfoEntity = DBHelper.getShared().getDeviceInfo();
            }
            else if(deviceInfoEntityFromParam!=null)
            {
                deviceInfoEntity=deviceInfoEntityFromParam;
            }

            //Todo - check Construct Headers pending,Null Checking Pending
            //Add headers
            headers.put("Content-Type", URLHelper.contentTypeJson);
            headers.put("verification_api_version","2");
            headers.put("lat",LocationDetails.getShared(context).getLatitude());
            headers.put("lon",LocationDetails.getShared(context).getLongitude());


            if(DBHelper.getShared().getFCMToken()!=null && !DBHelper.getShared().getFCMToken().equals("")) {
                deviceInfoEntity.setPushNotificationId(DBHelper.getShared().getFCMToken());
            }


            initiateFingerprintMFARequestEntity.setDeviceInfo(deviceInfoEntity);


            //Call Service-getRequestId
            final ICidaasSDKService cidaasSDKService = service.getInstance();

            cidaasSDKService.initiateFingerprintMFA(initiateFingerprintMFAUrl,headers, initiateFingerprintMFARequestEntity).enqueue(new Callback<InitiateFingerprintMFAResponseEntity>() {
                @Override
                public void onResponse(Call<InitiateFingerprintMFAResponseEntity> call, Response<InitiateFingerprintMFAResponseEntity> response) {
                    if (response.isSuccessful()) {
                        if(response.code()==200) {
                            callback.success(response.body());
                        }
                        else {
                            callback.failure( WebAuthError.getShared(context).serviceFailureException(WebAuthErrorCode.INITIATE_FINGERPRINT_MFA_FAILURE,
                                    "Service failure but successful response" , response.code(),null,null));
                        }
                    }
                    else {
                        assert response.errorBody() != null;
                        callback.failure(CommonError.getShared(context).generateCommonErrorEntity(WebAuthErrorCode.INITIATE_FINGERPRINT_MFA_FAILURE,response));
                    }
                }

                @Override
                public void onFailure(Call<InitiateFingerprintMFAResponseEntity> call, Throwable t) {
                    Timber.e("Failure in Initiate Fingerprint MFA Service call"+t.getMessage());
                    LogFile.getShared(context).addRecordToLog("Initiate Fingerprint MFA Service Service Failure"+t.getMessage());
                    callback.failure( WebAuthError.getShared(context).serviceFailureException(WebAuthErrorCode.INITIATE_FINGERPRINT_MFA_FAILURE,t.getMessage(), 400,null,null));
                }
            });


        }
        catch (Exception e)
        {
            LogFile.getShared(context).addRecordToLog("Initiate Fingerprint MFA Service exception"+e.getMessage());
           callback.failure(WebAuthError.getShared(context).serviceException(WebAuthErrorCode.INITIATE_FINGERPRINT_MFA_FAILURE));
            Timber.e("Initiate Fingerprint MFA Service exception"+e.getMessage());
        }
    }


    //AuthenticateFingerprintMFA
    public void authenticateFingerprint(String baseurl, AuthenticateFingerprintRequestEntity authenticateFingerprintRequestEntity,DeviceInfoEntity deviceInfoEntityFromParam,
                                           final Result<AuthenticateFingerprintResponseEntity> callback)
    {
        String authenticateFingerprintMFAUrl="";
        try
        {
            if(baseurl!=null && !baseurl.equals("")){
                //Construct URL For RequestId
                authenticateFingerprintMFAUrl=baseurl+URLHelper.getShared().getAuthenticateFingerprintMFA();
            }
            else {
                callback.failure( WebAuthError.getShared(context).serviceFailureException(WebAuthErrorCode.PROPERTY_MISSING,
                        context.getString(R.string.PROPERTY_MISSING), 400,null,null));
                return;
            }

            Map<String, String> headers = new Hashtable<>();
            // Get Device Information
            DeviceInfoEntity deviceInfoEntity=new DeviceInfoEntity();
            //This is only for testing purpose
            if(deviceInfoEntityFromParam==null) {
                deviceInfoEntity = DBHelper.getShared().getDeviceInfo();
            }
            else if(deviceInfoEntityFromParam!=null)
            {
                deviceInfoEntity=deviceInfoEntityFromParam;
            }


            //Todo - check Construct Headers pending,Null Checking Pending
            //Add headers
            headers.put("Content-Type", URLHelper.contentTypeJson);
            headers.put("verification_api_version","2");
            headers.put("lat",LocationDetails.getShared(context).getLatitude());
            headers.put("lon",LocationDetails.getShared(context).getLongitude());

            if(DBHelper.getShared().getFCMToken()!=null && !DBHelper.getShared().getFCMToken().equals("")) {
                deviceInfoEntity.setPushNotificationId(DBHelper.getShared().getFCMToken());
            }


            authenticateFingerprintRequestEntity.setDeviceInfo(deviceInfoEntity);
            authenticateFingerprintRequestEntity.setVerifierPassword(deviceInfoEntity.getDeviceId());

            //Call Service-getRequestId
            final ICidaasSDKService cidaasSDKService = service.getInstance();

            cidaasSDKService.authenticateFingerprintMFA(authenticateFingerprintMFAUrl,headers, authenticateFingerprintRequestEntity).enqueue(new Callback<AuthenticateFingerprintResponseEntity>() {
                @Override
                public void onResponse(Call<AuthenticateFingerprintResponseEntity> call, Response<AuthenticateFingerprintResponseEntity> response) {
                    if (response.isSuccessful()) {
                        if(response.code()==200) {
                            callback.success(response.body());
                        }
                        else {
                            callback.failure( WebAuthError.getShared(context).serviceFailureException(WebAuthErrorCode.AUTHENTICATE_FINGERPRINT_MFA_FAILURE,
                                    "Service failure but successful response" , response.code(),null,null));
                        }
                    }
                    else {
                        assert response.errorBody() != null;
                        callback.failure(CommonError.getShared(context).generateCommonErrorEntity(WebAuthErrorCode.AUTHENTICATE_FINGERPRINT_MFA_FAILURE,response));
                    }
                }

                @Override
                public void onFailure(Call<AuthenticateFingerprintResponseEntity> call, Throwable t) {
                    Timber.e("Failure in Authenticate Fingerprint service call"+t.getMessage());
                    LogFile.getShared(context).addRecordToLog("Authenticate Fingerprint Service Failure"+t.getMessage());
                    callback.failure( WebAuthError.getShared(context).serviceFailureException(WebAuthErrorCode.AUTHENTICATE_FINGERPRINT_MFA_FAILURE,t.getMessage(), 400,null,null));
                }
            });


        }
        catch (Exception e)
        {
            LogFile.getShared(context).addRecordToLog("Authenticate Fingerprint MFA Service exception"+e.getMessage());
           callback.failure(WebAuthError.getShared(context).serviceException(WebAuthErrorCode.AUTHENTICATE_FINGERPRINT_MFA_FAILURE));
            Timber.e("Authenticate Fingerprint MFA Service exception"+e.getMessage());
        }
    }
}
