package com.example.cidaasv2.Controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.nfc.tech.IsoDep;
import android.os.Build;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.example.cidaasv2.BuildConfig;
import com.example.cidaasv2.Controller.Repository.AccessToken.AccessTokenController;
import com.example.cidaasv2.Controller.Repository.ChangePassword.ChangePasswordController;
import com.example.cidaasv2.Controller.Repository.Client.ClientController;
import com.example.cidaasv2.Controller.Repository.Configuration.BackupCode.BackupCodeConfigurationController;
import com.example.cidaasv2.Controller.Repository.Configuration.Email.EmailConfigurationController;
import com.example.cidaasv2.Controller.Repository.Configuration.FIDO.FIDOConfigurationController;
import com.example.cidaasv2.Controller.Repository.Configuration.Face.FaceConfigurationController;
import com.example.cidaasv2.Controller.Repository.Configuration.Fingerprint.FingerprintConfigurationController;
import com.example.cidaasv2.Controller.Repository.Configuration.IVR.IVRConfigurationController;
import com.example.cidaasv2.Controller.Repository.Configuration.Pattern.PatternConfigurationController;
import com.example.cidaasv2.Controller.Repository.Configuration.SMS.SMSConfigurationController;
import com.example.cidaasv2.Controller.Repository.Configuration.SmartPush.SmartPushConfigurationController;
import com.example.cidaasv2.Controller.Repository.Configuration.TOTP.TOTPConfigurationController;
import com.example.cidaasv2.Controller.Repository.Configuration.Voice.VoiceConfigurationController;
import com.example.cidaasv2.Controller.Repository.Consent.ConsentController;
import com.example.cidaasv2.Controller.Repository.Deduplication.DeduplicationController;
import com.example.cidaasv2.Controller.Repository.DocumentScanner.DocumentScannnerController;
import com.example.cidaasv2.Controller.Repository.UserLoginInfo.UserLoginInfoController;
import com.example.cidaasv2.Controller.Repository.Login.LoginController;
import com.example.cidaasv2.Controller.Repository.MFASettings.VerificationSettingsController;
import com.example.cidaasv2.Controller.Repository.Registration.RegistrationController;
import com.example.cidaasv2.Controller.Repository.RequestId.RequestIdController;
import com.example.cidaasv2.Controller.Repository.ResetPassword.ResetPasswordController;
import com.example.cidaasv2.Controller.Repository.Tenant.TenantController;
import com.example.cidaasv2.Helper.Converter.EntityToModelConverter;
import com.example.cidaasv2.Helper.CustomTab.Helper.CustomTabHelper;
import com.example.cidaasv2.Helper.Entity.ConsentEntity;
import com.example.cidaasv2.Helper.Entity.DeviceInfoEntity;
import com.example.cidaasv2.Helper.Entity.FingerPrintEntity;
import com.example.cidaasv2.Helper.Entity.LocalAuthenticationEntity;
import com.example.cidaasv2.Helper.Entity.LoginEntity;
import com.example.cidaasv2.Helper.Entity.PasswordlessEntity;
import com.example.cidaasv2.Helper.Entity.RegistrationEntity;
import com.example.cidaasv2.Helper.Enums.HttpStatusCode;
import com.example.cidaasv2.Helper.Enums.Result;
import com.example.cidaasv2.Helper.Enums.UsageType;
import com.example.cidaasv2.Helper.Enums.WebAuthErrorCode;
import com.example.cidaasv2.Helper.Extension.WebAuthError;
import com.example.cidaasv2.Helper.Genral.DBHelper;
import com.example.cidaasv2.Helper.Genral.FileHelper;
import com.example.cidaasv2.Helper.Loaders.ICustomLoader;
import com.example.cidaasv2.Helper.Logger.LogFile;
import com.example.cidaasv2.Interface.IOAuthWebLogin;
import com.example.cidaasv2.Library.BiometricAuthentication.BiometricCallback;
import com.example.cidaasv2.Library.BiometricAuthentication.BiometricManager;
import com.example.cidaasv2.Models.DBModel.AccessTokenModel;
import com.example.cidaasv2.R;
import com.example.cidaasv2.Service.Entity.AccessTokenEntity;
import com.example.cidaasv2.Service.Entity.AuthRequest.AuthRequestResponseEntity;
import com.example.cidaasv2.Service.Entity.ClientInfo.ClientInfoEntity;
import com.example.cidaasv2.Service.Entity.ConsentManagement.ConsentDetailsResultEntity;
import com.example.cidaasv2.Service.Entity.ConsentManagement.ConsentManagementAcceptedRequestEntity;
import com.example.cidaasv2.Service.Entity.Deduplication.DeduplicationResponseEntity;
import com.example.cidaasv2.Service.Entity.Deduplication.RegisterDeduplication.RegisterDeduplicationEntity;
import com.example.cidaasv2.Service.Entity.DocumentScanner.DocumentScannerServiceResultEntity;
import com.example.cidaasv2.Service.Entity.UserLoginInfo.UserLoginInfoEntity;
import com.example.cidaasv2.Service.Entity.UserLoginInfo.UserLoginInfoResponseEntity;
import com.example.cidaasv2.Service.Entity.LoginCredentialsEntity.LoginCredentialsRequestEntity;
import com.example.cidaasv2.Service.Entity.LoginCredentialsEntity.LoginCredentialsResponseEntity;
import com.example.cidaasv2.Service.Entity.MFA.AuthenticateMFA.BackupCode.AuthenticateBackupCodeRequestEntity;
import com.example.cidaasv2.Service.Entity.MFA.AuthenticateMFA.Email.AuthenticateEmailRequestEntity;
import com.example.cidaasv2.Service.Entity.MFA.AuthenticateMFA.FIDOKey.AuthenticateFIDORequestEntity;
import com.example.cidaasv2.Service.Entity.MFA.AuthenticateMFA.FIDOKey.AuthenticateFIDOResponseEntity;
import com.example.cidaasv2.Service.Entity.MFA.AuthenticateMFA.FIDOKey.FidoSignTouchResponse;
import com.example.cidaasv2.Service.Entity.MFA.AuthenticateMFA.Face.AuthenticateFaceRequestEntity;
import com.example.cidaasv2.Service.Entity.MFA.AuthenticateMFA.Face.AuthenticateFaceResponseEntity;
import com.example.cidaasv2.Service.Entity.MFA.AuthenticateMFA.Fingerprint.AuthenticateFingerprintRequestEntity;
import com.example.cidaasv2.Service.Entity.MFA.AuthenticateMFA.Fingerprint.AuthenticateFingerprintResponseEntity;
import com.example.cidaasv2.Service.Entity.MFA.AuthenticateMFA.IVR.AuthenticateIVRRequestEntity;
import com.example.cidaasv2.Service.Entity.MFA.AuthenticateMFA.Pattern.AuthenticatePatternRequestEntity;
import com.example.cidaasv2.Service.Entity.MFA.AuthenticateMFA.Pattern.AuthenticatePatternResponseEntity;
import com.example.cidaasv2.Service.Entity.MFA.AuthenticateMFA.SMS.AuthenticateSMSRequestEntity;
import com.example.cidaasv2.Service.Entity.MFA.AuthenticateMFA.SmartPush.AuthenticateSmartPushRequestEntity;
import com.example.cidaasv2.Service.Entity.MFA.AuthenticateMFA.SmartPush.AuthenticateSmartPushResponseEntity;
import com.example.cidaasv2.Service.Entity.MFA.AuthenticateMFA.Voice.AuthenticateVoiceRequestEntity;
import com.example.cidaasv2.Service.Entity.MFA.AuthenticateMFA.Voice.AuthenticateVoiceResponseEntity;
import com.example.cidaasv2.Service.Entity.MFA.DeleteMFA.DeleteMFAResponseEntity;
import com.example.cidaasv2.Service.Entity.MFA.EnrollMFA.Email.EnrollEmailMFAResponseEntity;
import com.example.cidaasv2.Service.Entity.MFA.EnrollMFA.FIDOKey.EnrollFIDOMFARequestEntity;
import com.example.cidaasv2.Service.Entity.MFA.EnrollMFA.FIDOKey.EnrollFIDOMFAResponseEntity;
import com.example.cidaasv2.Service.Entity.MFA.EnrollMFA.FIDOKey.FIDOTouchResponse;
import com.example.cidaasv2.Service.Entity.MFA.EnrollMFA.Face.EnrollFaceMFARequestEntity;
import com.example.cidaasv2.Service.Entity.MFA.EnrollMFA.Face.EnrollFaceMFAResponseEntity;
import com.example.cidaasv2.Service.Entity.MFA.EnrollMFA.Fingerprint.EnrollFingerprintMFARequestEntity;
import com.example.cidaasv2.Service.Entity.MFA.EnrollMFA.Fingerprint.EnrollFingerprintMFAResponseEntity;
import com.example.cidaasv2.Service.Entity.MFA.EnrollMFA.IVR.EnrollIVRMFAResponseEntity;
import com.example.cidaasv2.Service.Entity.MFA.EnrollMFA.Pattern.EnrollPatternMFARequestEntity;
import com.example.cidaasv2.Service.Entity.MFA.EnrollMFA.Pattern.EnrollPatternMFAResponseEntity;
import com.example.cidaasv2.Service.Entity.MFA.EnrollMFA.SMS.EnrollSMSMFAResponseEntity;
import com.example.cidaasv2.Service.Entity.MFA.EnrollMFA.SmartPush.EnrollSmartPushMFARequestEntity;
import com.example.cidaasv2.Service.Entity.MFA.EnrollMFA.SmartPush.EnrollSmartPushMFAResponseEntity;
import com.example.cidaasv2.Service.Entity.MFA.EnrollMFA.TOTP.EnrollTOTPMFAResponseEntity;
import com.example.cidaasv2.Service.Entity.MFA.EnrollMFA.Voice.EnrollVoiceMFARequestEntity;
import com.example.cidaasv2.Service.Entity.MFA.EnrollMFA.Voice.EnrollVoiceMFAResponseEntity;
import com.example.cidaasv2.Service.Entity.MFA.InitiateMFA.BackupCode.InitiateBackupCodeMFARequestEntity;
import com.example.cidaasv2.Service.Entity.MFA.InitiateMFA.Email.InitiateEmailMFARequestEntity;
import com.example.cidaasv2.Service.Entity.MFA.InitiateMFA.Email.InitiateEmailMFAResponseEntity;
import com.example.cidaasv2.Service.Entity.MFA.InitiateMFA.FIDOKey.InitiateFIDOMFARequestEntity;
import com.example.cidaasv2.Service.Entity.MFA.InitiateMFA.Face.InitiateFaceMFARequestEntity;
import com.example.cidaasv2.Service.Entity.MFA.InitiateMFA.Fingerprint.InitiateFingerprintMFARequestEntity;
import com.example.cidaasv2.Service.Entity.MFA.InitiateMFA.IVR.InitiateIVRMFARequestEntity;
import com.example.cidaasv2.Service.Entity.MFA.InitiateMFA.IVR.InitiateIVRMFAResponseEntity;
import com.example.cidaasv2.Service.Entity.MFA.InitiateMFA.Pattern.InitiatePatternMFARequestEntity;
import com.example.cidaasv2.Service.Entity.MFA.InitiateMFA.SMS.InitiateSMSMFARequestEntity;
import com.example.cidaasv2.Service.Entity.MFA.InitiateMFA.SMS.InitiateSMSMFAResponseEntity;
import com.example.cidaasv2.Service.Entity.MFA.InitiateMFA.SmartPush.InitiateSmartPushMFARequestEntity;
import com.example.cidaasv2.Service.Entity.MFA.InitiateMFA.TOTP.InitiateTOTPMFARequestEntity;
import com.example.cidaasv2.Service.Entity.MFA.InitiateMFA.Voice.InitiateVoiceMFARequestEntity;
import com.example.cidaasv2.Service.Entity.MFA.MFAList.MFAListResponseEntity;
import com.example.cidaasv2.Service.Entity.MFA.SetupMFA.BackupCode.SetupBackupCodeMFAResponseEntity;
import com.example.cidaasv2.Service.Entity.MFA.SetupMFA.Email.SetupEmailMFAResponseEntity;
import com.example.cidaasv2.Service.Entity.MFA.SetupMFA.FIDO.SetupFIDOMFARequestEntity;
import com.example.cidaasv2.Service.Entity.MFA.SetupMFA.Face.SetupFaceMFARequestEntity;
import com.example.cidaasv2.Service.Entity.MFA.SetupMFA.Fingerprint.SetupFingerprintMFARequestEntity;
import com.example.cidaasv2.Service.Entity.MFA.SetupMFA.IVR.SetupIVRMFAResponseEntity;
import com.example.cidaasv2.Service.Entity.MFA.SetupMFA.Pattern.SetupPatternMFARequestEntity;
import com.example.cidaasv2.Service.Entity.MFA.SetupMFA.SMS.SetupSMSMFAResponseEntity;
import com.example.cidaasv2.Service.Entity.MFA.SetupMFA.SmartPush.SetupSmartPushMFARequestEntity;
import com.example.cidaasv2.Service.Entity.MFA.SetupMFA.TOTP.SetupTOTPMFARequestEntity;
import com.example.cidaasv2.Service.Entity.MFA.SetupMFA.Voice.SetupVoiceMFARequestEntity;
import com.example.cidaasv2.Service.Entity.MFA.TOTPEntity.TOTPEntity;
import com.example.cidaasv2.Service.Entity.NotificationEntity.DenyNotification.DenyNotificationRequestEntity;
import com.example.cidaasv2.Service.Entity.NotificationEntity.DenyNotification.DenyNotificationResponseEntity;
import com.example.cidaasv2.Service.Entity.NotificationEntity.GetPendingNotification.NotificationEntity;
import com.example.cidaasv2.Service.Entity.ResetPassword.ChangePassword.ChangePasswordRequestEntity;
import com.example.cidaasv2.Service.Entity.ResetPassword.ChangePassword.ChangePasswordResponseEntity;
import com.example.cidaasv2.Service.Entity.ResetPassword.ResetNewPassword.ResetNewPasswordResponseEntity;
import com.example.cidaasv2.Service.Entity.ResetPassword.ResetNewPassword.ResetPasswordEntity;
import com.example.cidaasv2.Service.Entity.ResetPassword.ResetPasswordRequestEntity;
import com.example.cidaasv2.Service.Entity.ResetPassword.ResetPasswordResponseEntity;
import com.example.cidaasv2.Service.Entity.ResetPassword.ResetPasswordValidateCode.ResetPasswordValidateCodeResponseEntity;
import com.example.cidaasv2.Service.Entity.TenantInfo.TenantInfoEntity;
import com.example.cidaasv2.Service.Entity.UserList.ConfiguredMFAListEntity;
import com.example.cidaasv2.Service.Entity.UserinfoEntity;
import com.example.cidaasv2.Service.Register.RegisterUser.RegisterNewUserRequestEntity;
import com.example.cidaasv2.Service.Register.RegisterUser.RegisterNewUserResponseEntity;
import com.example.cidaasv2.Service.Register.RegisterUserAccountVerification.RegisterUserAccountInitiateRequestEntity;
import com.example.cidaasv2.Service.Register.RegisterUserAccountVerification.RegisterUserAccountInitiateResponseEntity;
import com.example.cidaasv2.Service.Register.RegisterUserAccountVerification.RegisterUserAccountVerifyResponseEntity;
import com.example.cidaasv2.Service.Register.RegistrationSetup.RegistrationSetupRequestEntity;
import com.example.cidaasv2.Service.Register.RegistrationSetup.RegistrationSetupResponseEntity;
import com.example.cidaasv2.Service.Register.RegistrationSetup.RegistrationSetupResultDataEntity;
import com.example.cidaasv2.Service.Repository.OauthService;
import com.example.cidaasv2.Service.Repository.Verification.Face.FaceVerificationService;
import com.example.cidaasv2.Service.Scanned.ScannedResponseEntity;

import java.io.File;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import timber.log.Timber;

import static android.os.Build.MODEL;
import static android.os.Build.VERSION;

/**
 * Created by widasrnarayanan on 16/1/18.
 */

public class Cidaas implements IOAuthWebLogin {

    private static final int MY_SCAN_REQUEST_CODE = 3;
    private static final int LOCAL_AUTH_REQUEST_CODE = 303;
    private static final int LOCAL_REQUEST_CODE = 302;
    private static final int RESULT_OK = -1;


    Result<LocalAuthenticationEntity> localAuthenticationEntityCallback;


    public Context context;
    public Activity activityFromCidaas;

    public static String instanceId = "";
    public static ICustomLoader loader;
    public static String baseurl = "";
    public static final String FIDO_VERSION = "U2F_V2";

    public RegistrationSetupResultDataEntity[] registerFields;

    CountDownTimer countDownTimer;

    public WebAuthError webAuthError = null;


    String logoURLlocal="https://cdn.shortpixel.ai/client/q_glossy,ret_img/https://www.cidaas.com/wp-content/uploads/2018/02/logo.png";
    public static String APP_NAME = "cidaas";
    public static String APP_VERSION = "";

    public String loginURL;
    public String DomainURL="";
    public String redirectUrl;
    public Result<AccessTokenEntity> logincallback;


    //saved Properties for Global Access
  //  Dictionary<String, String> savedProperties;

    //To check the Loader
    private boolean displayLoader = false;

    //Todo Confirm it must be a static one
    //Extra parameter that is passed in URL
    public static HashMap<String, String> extraParams = new HashMap<>();

//Create a Shared Instance

    private static Cidaas cidaasInstance;


    public static Cidaas getInstance(Context YourActivitycontext) {
        if (cidaasInstance == null) {
            cidaasInstance = new Cidaas(YourActivitycontext);
        }

        return cidaasInstance;
    }


    public boolean isENABLE_PKCE() {
        ENABLE_PKCE = DBHelper.getShared().getEnablePKCE();
        return ENABLE_PKCE;
    }

    public void setENABLE_PKCE(boolean ENABLE_PKCE) {
        this.ENABLE_PKCE = ENABLE_PKCE;
        DBHelper.getShared().setEnablePKCE(ENABLE_PKCE);
    }

    public boolean ENABLE_PKCE;
    public boolean ENABLE_LOG;



 /*   final String[] baseurlArray = new String[1];

    String baseurl=baseurlArray[0];
    final String[] clientId = new String[1];
*/

    public DeviceInfoEntity deviceInfoEntity;


    public Cidaas(Context yourActivityContext) {
        this.context = yourActivityContext;

        //Initialise Shared Preferences
        DBHelper.setConfig(context);

        //Default Value;
        ENABLE_PKCE = true;

        //Default Log Value
        ENABLE_LOG = false;

        //Set Callback Null;
        logincallback = null;


        //Add Device info
        deviceInfoEntity = new DeviceInfoEntity();
        deviceInfoEntity.setDeviceId(Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
        deviceInfoEntity.setDeviceModel(MODEL);
        deviceInfoEntity.setDeviceVersion(String.valueOf(VERSION.RELEASE));
        deviceInfoEntity.setDeviceMake(Build.MANUFACTURER);


        Cidaas.baseurl=DomainURL;


        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo = null;

        try {
            applicationInfo = packageManager.getApplicationInfo(context.getApplicationInfo().packageName, 0);
           APP_VERSION= context.getPackageManager().getPackageInfo(context.getApplicationInfo().packageName, 0).versionName;
        } catch (final PackageManager.NameNotFoundException e) {
        }

        if(applicationInfo!=null) {

            APP_NAME = packageManager.getApplicationLabel(applicationInfo).toString();
        }
        else
        {
            APP_NAME="UNKNOWN";
        }



        //Store Device info for Later Purposes
        DBHelper.getShared().addDeviceInfo(deviceInfoEntity);

//Read and save file in shared preference
        saveLoginProperties(new Result<Dictionary<String, String>>() {
            @Override
            public void success(final Dictionary<String, String> loginResult) {
              //  savedProperties=result;
                DomainURL=loginResult.get("DomainURL");

                checkPKCEFlow(loginResult, new Result<Dictionary<String, String>>() {
                    @Override
                    public void success(Dictionary<String, String> result) {
                        DBHelper.getShared().addLoginProperties(result);
                    }

                    @Override
                    public void failure(WebAuthError error) {
                        String loggerMessage = "Cidaas constructor failure : " + "Error Code - "
                                + error.errorCode + ", Error Message - " + error.ErrorMessage + ", Status Code - " + error.statusCode;
                        LogFile.addRecordToLog(loggerMessage);
                    }
                });

            }

            @Override
            public void failure(WebAuthError error) {
                String loggerMessage = "Cidaas constructor failure : " + "Error Code - "
                        + error.errorCode + ", Error Message - " + error.ErrorMessage + ", Status Code - " + error.statusCode;
                LogFile.addRecordToLog(loggerMessage);
            }
        });


        //Set Context For sdkservice

        //CidaassdkService service=new CidaassdkService();
      //  service.setContext(context);


    }



    //Set FCM Token For Update
    public void setFCMToken(String FCMToken) {

        //Store Device info for Later Purposes
        DBHelper.getShared().setFCMToken(FCMToken);

    }

    //Get the remote messages from the Push notification
    public static void validateDevice(Map<String, String> instanceIdFromPush) {
        try {
            if (instanceIdFromPush.get("usage_pass") != null && instanceIdFromPush.get("usage_pass") != "") {
                instanceId = instanceIdFromPush.get("usage_pass");
            } else {
                instanceId = "";
            }
        } catch (Exception e) {
            String loggerMessage = "Set remote Message : " + " Error Message - " + e.getMessage();
            LogFile.addRecordToLog(loggerMessage);
        }

    }

    // Get the instance ID
    public String getInstanceId() {
        if (instanceId != null && instanceId != "") {
            return instanceId;
        } else {
            return null;
        }
    }


    //Get Request Id By passing loginProperties as an Object
    // 1. Read properties from file
    // 2. Call request id from dictionary method
    // 3. Maintain logs based on flags

    // -----------------------------------------------------***** REQUEST ID *****---------------------------------------------------------------

    //Get Request Id By Passing loginProperties as Value in parameters
    @Override
    public void getRequestId(@NonNull String DomainUrl, @NonNull String ClientId, @NonNull String RedirectURL,
                             @Nullable String ClientSecret, final Result<AuthRequestResponseEntity> result) {



        //WebError Code instance Creation
        webAuthError = WebAuthError.getShared(context);
        try {
            if (ClientId != null && !ClientId.equals("") && DomainUrl != null && !DomainUrl.equals("")
                    && RedirectURL != null && !RedirectURL.equals("") && ClientSecret != null && !ClientSecret.equals("")) {

                DomainURL=DomainUrl;
                Cidaas.baseurl=DomainURL;

                FileHelper.getShared(context).paramsToDictionaryConverter(DomainUrl, ClientId, RedirectURL,
                        ClientSecret, new Result<Dictionary<String, String>>() {
                            @Override
                            public void success(Dictionary<String, String> loginproperties) {

                                //on successfully completion of file reading add it to LocalDB(shared Preference) and call requestIdByloginProperties

                                checkPKCEFlow(loginproperties, new Result<Dictionary<String, String>>() {
                                    @Override
                                    public void success(Dictionary<String, String> savedLoginProperties) {
                                        //Call requestIdBy LoginProperties parameter

                                        RequestIdController.getShared(context).getRequestId(savedLoginProperties, result,extraParams);
                                    }

                                    @Override
                                    public void failure(WebAuthError error) {
                                        result.failure(error);
                                        String loggerMessage = "Request-Id params to dictionary conversion failure : " + "Error Code - "
                                                + error.errorCode + ", Error Message - " + error.ErrorMessage + ", Status Code - " + error.statusCode;
                                        LogFile.addRecordToLog(loggerMessage);
                                    }
                                });

                            }

                            @Override
                            public void failure(WebAuthError error) {
                                String loggerMessage = "Request-Id params to dictionary conversion failure : " + "Error Code - "
                                        + error.errorCode + ", Error Message - " + error.ErrorMessage + ", Status Code - " + error.statusCode;
                                LogFile.addRecordToLog(loggerMessage);
                                result.failure(error);
                            }
                        });

            } else {
                result.failure(webAuthError.propertyMissingException());
            }
        } catch (Exception e) {
            String loggerMessage = "Request-Id service Exception :  Error Message - " + e.getMessage();
            LogFile.addRecordToLog(loggerMessage);
            Timber.d(e.getMessage());
        }
    }

    //Get Request Id without passing any value


    public void getRequestId(final Dictionary<String, String> loginproperties ,final Result<AuthRequestResponseEntity> Primaryresult,
                             @Nullable HashMap<String, String>... extraParams) {
        try {

            if(loginproperties!=null && loginproperties.get("DomainURL")!=null && loginproperties.get("DomainURL")!="") {

                DomainURL = loginproperties.get("DomainURL");
                Cidaas.baseurl = DomainURL;

                RequestIdController.getShared(context).getRequestId(loginproperties, Primaryresult, extraParams);
            }
            else
            {
                Primaryresult.failure(WebAuthError.getShared(context).propertyMissingException());
            }
        } catch (Exception e) {

            String loggerMessage = "Request-Id  failure : " + "Error Code - "
                    + 400 + ", Error Message - " + e.getMessage() + ", Status Code - " + 400;
            LogFile.addRecordToLog(loggerMessage);
            Timber.e(e.getMessage());

        }
    }


    public void getRequestId(@NonNull final String DomainUrl, @NonNull String ClientId, @NonNull String RedirectURL,
                             @Nullable final HashMap<String, String> extraParams , final Result<AuthRequestResponseEntity> Primaryresult) {
        try {
            if (ClientId != null && !ClientId.equals("") && DomainUrl != null && !DomainUrl.equals("")
                    && RedirectURL != null && !RedirectURL.equals("")) {
              FileHelper.getShared(context).paramsToDictionaryConverter(DomainUrl, ClientId, RedirectURL, new Result<Dictionary<String, String>>() {
                  @Override
                  public void success(Dictionary<String, String> result) {

                      DomainURL=DomainUrl;
                      Cidaas.baseurl=DomainURL;

                      RequestIdController.getShared(context).getRequestId(result, Primaryresult,extraParams);
                  }

                  @Override
                  public void failure(WebAuthError error) {
                      Primaryresult.failure(error);
                  }
              });
            }

        } catch (Exception e) {

            String loggerMessage = "Request-Id  failure : " + "Error Code - "
                    + 400 + ", Error Message - " + e.getMessage() + ", Status Code - " + 400;
            LogFile.addRecordToLog(loggerMessage);
            Timber.e(e.getMessage());

        }
    }



    @Override
    public void getRequestId( final Result<AuthRequestResponseEntity> resulttoReturn,@Nullable final HashMap<String, String>... extraParams) {
        try {

            //Todo Check in saved file
            saveLoginProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {

                    DomainURL=result.get("DomainURL");
                    Cidaas.baseurl=DomainURL;



                        final Dictionary<String, String> loginProperties = DBHelper.getShared().getLoginProperties(DomainURL);

                        if (loginProperties != null && !loginProperties.isEmpty() && loginProperties.size() > 0) {
                            //check here for already saved properties
                            checkPKCEFlow(loginProperties, new Result<Dictionary<String, String>>() {
                                @Override
                                public void success(Dictionary<String, String> result) {
                                    RequestIdController.getShared(context).getRequestId(loginProperties, resulttoReturn,extraParams);
                                }

                                @Override
                                public void failure(WebAuthError error) {

                                    resulttoReturn.failure(error);
                                }
                            });
                        } else {
                            //Read File from asset to get URL
                            readFromFile(new Result<Dictionary<String, String>>() {
                                @Override
                                public void success(Dictionary<String, String> savedLoginProperties) {
                                    //Call requestIdBy LoginProperties parameter
                                    RequestIdController.getShared(context).getRequestId(savedLoginProperties, resulttoReturn,extraParams);
                                }

                                @Override
                                public void failure(WebAuthError error) {

                                    resulttoReturn.failure(error);
                                }
                            });
                        }

                }

                @Override
                public void failure(WebAuthError error) {
                   resulttoReturn.failure(error);
                }
            });


        } catch (Exception e) {
            //Todo Handle Exception
            Timber.d(e.getMessage());
        }
    }

    // -----------------------------------------------------***** TENANT INFO *****---------------------------------------------------------------
    @Override
    public void getTenantInfo(final Result<TenantInfoEntity> tenantresult) {
        try {

            if(DomainURL!=null && DomainURL!="") {

                checkSavedProperties(new Result<Dictionary<String, String>>() {
                    @Override
                    public void success(Dictionary<String, String> stringresult) {
                        String baseurl = stringresult.get("DomainURL");
                        String clientId = stringresult.get("ClientId");
                        TenantController.getShared(context).getTenantInfo(baseurl, tenantresult);
                    }

                    @Override
                    public void failure(WebAuthError error) {
                        tenantresult.failure(error);
                    }
                });
            }
            else
            {
                tenantresult.failure(WebAuthError.getShared(context).customException(417, "DomainURL Must not be empty", HttpStatusCode.EXPECTATION_FAILED));
            }

            /*if ((baseurl != null) && (baseurl.equals(""))) {

                TenantController.getShared(context).getTenantInfo(baseurl,tenantresult);
            }*/
        } catch (Exception e) {
            String errorMessae = e.getMessage();
            tenantresult.failure(WebAuthError.getShared(context).customException(417, errorMessae, HttpStatusCode.EXPECTATION_FAILED));
        }
    }

    // -----------------------------------------------------***** CLIENT INFO *****-------------------------------------------------------------------------

   public void getClientInfo(final Result<ClientInfoEntity> clientInfoEntityResult,final HashMap<String,String>... extraParams)
   {
       try
       {
           checkSavedProperties(new Result<Dictionary<String, String>>() {
               @Override
               public void success(Dictionary<String, String> loginProperties) {
                   getRequestId(loginProperties, new Result<AuthRequestResponseEntity>() {
                       @Override
                       public void success(AuthRequestResponseEntity result) {
                           getClientInfo(result.getData().getRequestId(),clientInfoEntityResult);
                       }

                       @Override
                       public void failure(WebAuthError error) {
                           clientInfoEntityResult.failure(error);
                       }
                   },extraParams);
               }

               @Override
               public void failure(WebAuthError error) {
                   clientInfoEntityResult.failure(error);
               }
           });
       }
       catch (Exception e)
       {

       }
   }

    @Override
    public void getClientInfo(final String RequestId, final Result<ClientInfoEntity> clientInfoEntityResult) {
        try {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {
                    //Todo Check notnull Re   questId
                    String baseurl = result.get("DomainURL");

                    if (RequestId != null && RequestId != "") {
                        ClientController.getShared(context).getClientInfo(baseurl, RequestId, clientInfoEntityResult);
                    } else {
                        String errorMessage = "RequestId must not be empty";

                        clientInfoEntityResult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                errorMessage, HttpStatusCode.EXPECTATION_FAILED));

                    }
                }

                @Override
                public void failure(WebAuthError error) {

                    clientInfoEntityResult.failure(error);
                }
            });
        } catch (Exception e) {
            String errorMessage = "ClientInfo Exception" + e.getMessage();

            clientInfoEntityResult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                    errorMessage, HttpStatusCode.EXPECTATION_FAILED));
        }
    }

    // -----------------------------------------------------***** LOGIN WITH CREDENTIALS *****---------------------------------------------------------------

    public void loginWithCredentials(final LoginEntity loginEntity,
                                     final Result<LoginCredentialsResponseEntity> loginresult,final HashMap<String,String>... extraParams)
    {
        try
        {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> loginProperties) {
                    getRequestId(loginProperties, new Result<AuthRequestResponseEntity>() {
                        @Override
                        public void success(AuthRequestResponseEntity result) {
                            loginWithCredentials(result.getData().getRequestId(),loginEntity,loginresult);
                        }

                        @Override
                        public void failure(WebAuthError error) {
                            loginresult.failure(error);
                        }
                    },extraParams);
                }

                @Override
                public void failure(WebAuthError error) {
                    loginresult.failure(error);
                }
            });
        }
        catch (Exception e)
        {

        }
    }


    @Override
    public void loginWithCredentials(final String requestId, final LoginEntity loginEntity,
                                     final Result<LoginCredentialsResponseEntity> loginresult) {
        try {

            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {
                    String baseurl = result.get("DomainURL");
                    String clientId = result.get("ClientId");

                    if (loginEntity.getUsername_type() == null && loginEntity.getUsername_type() == "") {
                        loginEntity.setUsername_type("email");
                    }

                    if (loginEntity.getPassword() != null && loginEntity.getPassword() != "" &&
                            loginEntity.getUsername() != null && loginEntity.getUsername() != "") {
                        LoginCredentialsRequestEntity loginCredentialsRequestEntity = new LoginCredentialsRequestEntity();
                        loginCredentialsRequestEntity.setUsername(loginEntity.getUsername());
                        loginCredentialsRequestEntity.setUsername_type(loginEntity.getUsername_type());
                        loginCredentialsRequestEntity.setPassword(loginEntity.getPassword());
                        loginCredentialsRequestEntity.setRequestId(requestId);
                        LoginController.getShared(context).loginwithCredentials(baseurl, loginCredentialsRequestEntity, loginresult);
                    } else {

                        String errorMessage = "Username or password must not be empty";

                        loginresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                errorMessage, HttpStatusCode.EXPECTATION_FAILED));

                    }

                }

                @Override
                public void failure(WebAuthError error) {

                    loginresult.failure(error);
                }
            });
        } catch (Exception e) {

            String errorMessage = "Login with Credentials Exception" + e.getMessage();

            loginresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                    errorMessage, HttpStatusCode.EXPECTATION_FAILED));
        }
    }

    // -----------------------------------------------------***** CONSENT MANAGEMENT *****---------------------------------------------------------------
    @Override
    public void getConsentDetails(@NonNull final String consentName,
                                  final Result<ConsentDetailsResultEntity> consentResult) {

        try {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {
                    String baseurl = result.get("DomainURL");
                    String clientId = result.get("ClientId");

                    if (consentName != null && !consentName.equals("") && baseurl != null && !baseurl.equals("")
                            && clientId != null && !clientId.equals("")) {


                        ConsentController.getShared(context).getConsentDetails(baseurl, consentName, consentResult);
                    } else {

                        String errorMessage = "ConsentName or consentVersion or trackid must not be empty";

                        consentResult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                errorMessage, HttpStatusCode.EXPECTATION_FAILED));
                        return;
                    }

                    //Todo put not null
                }

                @Override
                public void failure(WebAuthError error) {
                    consentResult.failure(error);
                }
            });
        } catch (Exception e) {
            String errorMessage = "Get Consent Details Exception" + e.getMessage();

            consentResult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                    errorMessage, HttpStatusCode.EXPECTATION_FAILED));
        }

    }

    @Override
    public void loginAfterConsent(@NonNull final ConsentEntity consentEntity,
                                  final Result<LoginCredentialsResponseEntity> loginresult) {
        try {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {
                    String baseurl = result.get("DomainURL");
                    String clientId = result.get("ClientId");


                    if (consentEntity.getSub() != null && !consentEntity.getSub().equals("") &&
                            consentEntity.getConsentName() != null && !consentEntity.getConsentName().equals("") &&
                            consentEntity.getConsentVersion() != null && !consentEntity.getConsentVersion().equals("")
                            && consentEntity.isAccepted() != false) {

                        ConsentManagementAcceptedRequestEntity consentManagementAcceptedRequestEntity = new ConsentManagementAcceptedRequestEntity();
                        consentManagementAcceptedRequestEntity.setAccepted(consentEntity.isAccepted());
                        consentManagementAcceptedRequestEntity.setSub(consentEntity.getSub());
                        consentManagementAcceptedRequestEntity.setClient_id(clientId);
                        consentManagementAcceptedRequestEntity.setName(consentEntity.getConsentName());
                        consentManagementAcceptedRequestEntity.setTrackId(consentEntity.getTrackId());
                        consentManagementAcceptedRequestEntity.setVersion(consentEntity.getConsentVersion());


                        ConsentController.getShared(context).acceptConsent(baseurl, consentManagementAcceptedRequestEntity, loginresult);
                    } else {
                        String errorMessage = "Sub must not be null or Accepted must not be false";

                        loginresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                errorMessage, HttpStatusCode.EXPECTATION_FAILED));
                    }

                }

                @Override
                public void failure(WebAuthError error) {
                    loginresult.failure(error);
                }
            });
        } catch (Exception e) {
            String errorMessage = "Login after Consent Exception" + e.getMessage();

            loginresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                    errorMessage, HttpStatusCode.EXPECTATION_FAILED));
        }
    }

    // -----------------------------------------------------***** GET MFA LIST *****---------------------------------------------------------------

    @Override
    public void getMFAList(final String sub, final Result<MFAListResponseEntity> mfaresult) {
        try {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {
                    String baseurl = result.get("DomainURL");
                    if (sub != null && sub != "") {
                        VerificationSettingsController.getShared(context).getmfaList(baseurl, sub, mfaresult);
                    } else {
                        String errorMessage = "Sub must not be empty";

                        mfaresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                errorMessage, HttpStatusCode.EXPECTATION_FAILED));
                    }
                }

                @Override
                public void failure(WebAuthError error) {
                    mfaresult.failure(error);
                }
            });
        } catch (Exception e) {
            String errorMessage = "Getting MFA List Exception" + e.getMessage();

            mfaresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                    errorMessage, HttpStatusCode.EXPECTATION_FAILED));
        }
    }

    // -----------------------------------------------------***** PASSWORD LESS LOGIN AND MFA SERVICE CALL *****---------------------------------------------------------------


    // ****** DONE LOGIN WITH EMAIL AND VERIFY EMAIL *****----------------------------------------------------------------------


    @Override
    public void configureEmail(final String sub, final Result<SetupEmailMFAResponseEntity> result) {
        try {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> lpresult) {
                    String baseurl = lpresult.get("DomainURL");
                    String clientId = lpresult.get("ClientId");
                    ///Todo Call configure email

                    EmailConfigurationController.getShared(context).configureEmail(sub, baseurl, result);
                }

                @Override
                public void failure(WebAuthError error) {
                    result.failure(WebAuthError.getShared(context).propertyMissingException());
                }
            });
        } catch (Exception e) {
            String errorMessage = "Configure Email Exception" + e.getMessage();

            result.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                    errorMessage, HttpStatusCode.EXPECTATION_FAILED));
        }

    }

    @Override
    public void enrollEmail(final String code, final String statusId, final Result<EnrollEmailMFAResponseEntity> enrollresult) {
        try {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {
                    String baseurl = result.get("DomainURL");
                    String clientId = result.get("ClientId");

                    //todo call enroll Email
                    if (code != null && !code.equals("") || statusId != null && !statusId.equals("")) {
                        EmailConfigurationController.getShared(context).enrollEmailMFA(code, statusId, baseurl, enrollresult);
                    } else {
                        String errorMessage = "Code or StatusId Must not be empty";
                        enrollresult.failure(WebAuthError.getShared(context).customException(417, errorMessage, HttpStatusCode.EXPECTATION_FAILED));
                    }
                }

                @Override
                public void failure(WebAuthError error) {
                    enrollresult.failure(WebAuthError.getShared(context).propertyMissingException());
                }
            });
        } catch (Exception e) {
            enrollresult.failure(WebAuthError.getShared(context).propertyMissingException());
        }

    }


    @Override
    public void loginWithEmail(final PasswordlessEntity passwordlessEntity, final Result<InitiateEmailMFAResponseEntity> initiateresult) {
        try {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {
                    String baseurl = result.get("DomainURL");
                    String clientId = result.get("ClientId");

                    if (passwordlessEntity.getSub() != null && !passwordlessEntity.getSub().equals("") &&
                            (passwordlessEntity.getUsageType() != null && !passwordlessEntity.getUsageType().equals(""))) {

                        if (passwordlessEntity.getUsageType().equals(UsageType.MFA)) {
                            if (passwordlessEntity.getTrackId() == null || passwordlessEntity.getTrackId() == "") {
                                String errorMessage = "trackId must not be empty";

                                initiateresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                        errorMessage, HttpStatusCode.EXPECTATION_FAILED));
                                return;
                            }
                        }

                        InitiateEmailMFARequestEntity initiateEmailMFARequestEntity = new InitiateEmailMFARequestEntity();
                        initiateEmailMFARequestEntity.setSub(passwordlessEntity.getSub());
                        initiateEmailMFARequestEntity.setUsageType(passwordlessEntity.getUsageType());
                        initiateEmailMFARequestEntity.setVerificationType("email");


                        EmailConfigurationController.getShared(context).loginWithEmail(baseurl, passwordlessEntity.getTrackId(),
                                passwordlessEntity.getRequestId(), initiateEmailMFARequestEntity, initiateresult);
                    } else {

                        initiateresult.failure(WebAuthError.getShared(context).propertyMissingException());
                    }

                }

                @Override
                public void failure(WebAuthError error) {
                    initiateresult.failure(WebAuthError.getShared(context).propertyMissingException());
                }
            });
        } catch (Exception e) {
            initiateresult.failure(WebAuthError.getShared(context).propertyMissingException());
        }

    }

    @Override
    public void verifyEmail(@NonNull final String code, @NonNull final String statusId, final Result<LoginCredentialsResponseEntity> loginresult) {
        try {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {
                    String baseurl = result.get("DomainURL");
                    String clientId = result.get("ClientId");

                    //Todo call initiate

                    if (code != null && code != "") {

                        AuthenticateEmailRequestEntity authenticateEmailRequestEntity = new AuthenticateEmailRequestEntity();
                        authenticateEmailRequestEntity.setCode(code);

                        EmailConfigurationController.getShared(context).verifyEmail(baseurl, code, statusId, clientId, authenticateEmailRequestEntity, loginresult);
/*
                          String userDeviceId = DBHelper.getShared().getUserDeviceId(baseurl);

                          if (userDeviceId != null && !userDeviceId.equals("")) {
                              authenticatePatternRequestEntity.setUserDeviceId(userDeviceId);
                          } else {
                              loginresult.failure(WebAuthError.getShared(context).propertyMissingException());
                          }*/
                    } else {
                        loginresult.failure(WebAuthError.getShared(context).propertyMissingException());
                    }


                }

                @Override
                public void failure(WebAuthError error) {
                    loginresult.failure(error);
                }
            });

        } catch (Exception e) {
            loginresult.failure(WebAuthError.getShared(context).propertyMissingException());
        }
    }

    // ****** DONE LOGIN WITH SMS AND VERIFY SMS *****----------------------------------------------------------------------


    @Override
    public void configureSMS(final String sub, final Result<SetupSMSMFAResponseEntity> result) {
        try {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> lpresult) {
                    String baseurl = lpresult.get("DomainURL");
                    String clientId = lpresult.get("ClientId");

                    SMSConfigurationController.getShared(context).configureSMS(sub, baseurl, result);
                }

                @Override
                public void failure(WebAuthError error) {
                    result.failure(WebAuthError.getShared(context).propertyMissingException());
                }
            });
        } catch (Exception e) {
            result.failure(WebAuthError.getShared(context).propertyMissingException());
        }

    }


    public void enrollSMS(final String code, final String statusId, final Result<EnrollSMSMFAResponseEntity> result) {
        try {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> lpresult) {
                    String baseurl = lpresult.get("DomainURL");
                    String clientId = lpresult.get("ClientId");

                    if (code != null && code != "") {
                        SMSConfigurationController.getShared(context).enrollSMSMFA(code, statusId, baseurl, result);
                    } else {

                    }

                }

                @Override
                public void failure(WebAuthError error) {
                    result.failure(WebAuthError.getShared(context).propertyMissingException());
                }
            });
        } catch (Exception e) {
            result.failure(WebAuthError.getShared(context).propertyMissingException());
        }

    }

    public void loginWithSMS(final PasswordlessEntity passwordlessEntity, final Result<InitiateSMSMFAResponseEntity> initiateresult) {
        try {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {
                    String baseurl = result.get("DomainURL");
                    String clientId = result.get("ClientId");
                    if (passwordlessEntity.getSub() != null && !passwordlessEntity.getSub().equals("") &&
                            (passwordlessEntity.getUsageType() != null && !passwordlessEntity.getUsageType().equals(""))) {


                        if (passwordlessEntity.getUsageType().equals(UsageType.MFA)) {
                            if (passwordlessEntity.getTrackId() == null || passwordlessEntity.getTrackId() == "") {
                                String errorMessage = "trackId must not be empty";

                                initiateresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                        errorMessage, HttpStatusCode.EXPECTATION_FAILED));
                                return;
                            }
                        }

                        InitiateSMSMFARequestEntity initiateSMSMFARequestEntity = new InitiateSMSMFARequestEntity();
                        initiateSMSMFARequestEntity.setSub(passwordlessEntity.getSub());
                        initiateSMSMFARequestEntity.setUsageType(passwordlessEntity.getUsageType());
                        initiateSMSMFARequestEntity.setVerificationType("SMS");

                     /*   String userDeviceId=DBHelper.getShared().getUserDeviceId(baseurl);
                        if(userDeviceId!=null && !userDeviceId.equals("") ) {
                            initiateSMSMFARequestEntity.setUserDeviceId(userDeviceId);
                        }
                        else
                        {
                            String errorMessage="UserDeviceId must not be empty";

                            initiateresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                    errorMessage,HttpStatusCode.EXPECTATION_FAILED));
                        }
                        */
                        SMSConfigurationController.getShared(context).loginWithSMS(baseurl, passwordlessEntity.getTrackId(),
                                clientId, passwordlessEntity.getRequestId(), initiateSMSMFARequestEntity, initiateresult);
                    } else {

                        initiateresult.failure(WebAuthError.getShared(context).propertyMissingException());
                    }

                }

                @Override
                public void failure(WebAuthError error) {
                    initiateresult.failure(WebAuthError.getShared(context).propertyMissingException());
                }
            });
        } catch (Exception e) {
            initiateresult.failure(WebAuthError.getShared(context).propertyMissingException());
        }

    }

    @Override
    public void verifySMS(final String code, final String statusId, final Result<LoginCredentialsResponseEntity> loginresult) {
        try {

            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {
                    String baseurl = result.get("DomainURL");
                    String clientId = result.get("ClientId");

                    //Todo call initiate

                    if (code != null && code != "") {

                        AuthenticateSMSRequestEntity authenticateSMSRequestEntity = new AuthenticateSMSRequestEntity();
                        authenticateSMSRequestEntity.setCode(code);
                        authenticateSMSRequestEntity.setStatusId(statusId);

                        SMSConfigurationController.getShared(context).verifySMS(baseurl, clientId, authenticateSMSRequestEntity, loginresult);
/*
                          String userDeviceId = DBHelper.getShared().getUserDeviceId(baseurl);

                          if (userDeviceId != null && !userDeviceId.equals("")) {
                              authenticatePatternRequestEntity.setUserDeviceId(userDeviceId);
                          } else {
                              loginresult.failure(WebAuthError.getShared(context).propertyMissingException());
                          }*/
                    } else {
                        String errorMessage = "code must not be empty";

                        loginresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                errorMessage, HttpStatusCode.EXPECTATION_FAILED));

                    }


                }

                @Override
                public void failure(WebAuthError error) {
                    loginresult.failure(error);
                }
            });
        } catch (Exception e) {
            loginresult.failure(WebAuthError.getShared(context).propertyMissingException());
        }
    }

    // ****** done LOGIN WITH IVR AND VERIFY IVR *****----------------------------------------------------------------------

    //done CHANGE TO LOGIN AND VERIFY
// ****** DONE ENROLL AND LOGIN WITH IVR  *****----------------------------------------------------------------------

    @Override
    public void configureIVR(final String sub, final Result<SetupIVRMFAResponseEntity> result) {
        try {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> lpresult) {
                    String baseurl = lpresult.get("DomainURL");
                    String clientId = lpresult.get("ClientId");
                    //todo call enroll Email

                    IVRConfigurationController.getShared(context).configureIVR(sub, baseurl, result);
                }

                @Override
                public void failure(WebAuthError error) {
                    result.failure(WebAuthError.getShared(context).propertyMissingException());
                }
            });
        } catch (Exception e) {
            result.failure(WebAuthError.getShared(context).propertyMissingException());
        }

    }

    public void enrollIVR(final String code, final String statusId, final Result<EnrollIVRMFAResponseEntity> result) {
        try {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> lpresult) {
                    String baseurl = lpresult.get("DomainURL");
                    String clientId = lpresult.get("ClientId");

                    if (code != null && code != "") {
                        IVRConfigurationController.getShared(context).enrollIVRMFA(code, statusId, baseurl, result);
                    } else {
                        result.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.ENROLL_IVR_MFA_FAILURE,"Code Must not be null",HttpStatusCode.BAD_REQUEST));
                    }
                }

                @Override
                public void failure(WebAuthError error) {
                    result.failure(error);
                }
            });
        } catch (Exception e) {
            result.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.ENROLL_IVR_MFA_FAILURE,e.getMessage(),HttpStatusCode.BAD_REQUEST));
        }

    }


    @Override
    public void loginWithIVR(final PasswordlessEntity passwordlessEntity, final Result<InitiateIVRMFAResponseEntity> initiateresult) {
        try {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {
                    String baseurl = result.get("DomainURL");
                    String clientId = result.get("ClientId");
                    if (passwordlessEntity.getSub() != null && !passwordlessEntity.getSub().equals("") &&
                            (passwordlessEntity.getUsageType() != null && !passwordlessEntity.getUsageType().equals(""))) {


                        if (passwordlessEntity.getUsageType().equals(UsageType.MFA)) {
                            if (passwordlessEntity.getTrackId() == null || passwordlessEntity.getTrackId() == "") {
                                String errorMessage = "trackId must not be empty";

                                initiateresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                        errorMessage, HttpStatusCode.EXPECTATION_FAILED));
                                return;
                            }
                        }

                        InitiateIVRMFARequestEntity initiateIVRMFARequestEntity = new InitiateIVRMFARequestEntity();
                        initiateIVRMFARequestEntity.setSub(passwordlessEntity.getSub());
                        initiateIVRMFARequestEntity.setUsageType(passwordlessEntity.getUsageType());
                        initiateIVRMFARequestEntity.setVerificationType("IVR");


                        IVRConfigurationController.getShared(context).loginWithIVR(baseurl,
                                passwordlessEntity.getTrackId(), clientId, passwordlessEntity.getRequestId(),
                                initiateIVRMFARequestEntity, initiateresult);
                    } else {

                        initiateresult.failure(WebAuthError.getShared(context).propertyMissingException());
                    }

                }

                @Override
                public void failure(WebAuthError error) {
                    initiateresult.failure(WebAuthError.getShared(context).propertyMissingException());
                }
            });
        } catch (Exception e) {
            initiateresult.failure(WebAuthError.getShared(context).propertyMissingException());
        }

    }

    public void verifyIVR(final String code, final String statusId, final Result<LoginCredentialsResponseEntity> loginresult) {
        try {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {
                    String baseurl = result.get("DomainURL");
                    String clientId = result.get("ClientId");

                    if (code != null && code != "") {

                        AuthenticateIVRRequestEntity authenticateIVRRequestEntity = new AuthenticateIVRRequestEntity();
                        authenticateIVRRequestEntity.setCode(code);
                        authenticateIVRRequestEntity.setStatusId(statusId);

                        IVRConfigurationController.getShared(context).verifyIVR(baseurl, clientId, authenticateIVRRequestEntity, loginresult);
/*
                          String userDeviceId = DBHelper.getShared().getUserDeviceId(baseurl);

                          if (userDeviceId != null && !userDeviceId.equals("")) {
                              authenticatePatternRequestEntity.setUserDeviceId(userDeviceId);
                          } else {
                              loginresult.failure(WebAuthError.getShared(context).propertyMissingException());
                          }*/
                    } else {
                        String errorMessage = "code must not be empty";

                        loginresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                errorMessage, HttpStatusCode.EXPECTATION_FAILED));

                    }

                }

                @Override
                public void failure(WebAuthError error) {
                    loginresult.failure(WebAuthError.getShared(context).propertyMissingException());
                }
            });
        } catch (Exception e) {
            loginresult.failure(WebAuthError.getShared(context).propertyMissingException());
        }
    }

    // ****** DONE ENROLL AND LOGIN WITH Backupcode  *****----------------------------------------------------------------------


    @Override
    public void configureBackupcode(final String sub, final Result<SetupBackupCodeMFAResponseEntity> result) {
        try {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> lpresult) {
                    String baseurl = lpresult.get("DomainURL");
                    String clientId = lpresult.get("ClientId");
                    //todo call enroll Email
                    BackupCodeConfigurationController.getShared(context).configureBackupCode(sub, baseurl, result);

                }

                @Override
                public void failure(WebAuthError error) {
                    result.failure(WebAuthError.getShared(context).propertyMissingException());
                }
            });
        } catch (Exception e) {
            result.failure(WebAuthError.getShared(context).propertyMissingException());
        }

    }

    @Override
    public void loginWithBackupcode(final String code, final PasswordlessEntity passwordlessEntity,
                                    final Result<LoginCredentialsResponseEntity> loginresult) {
        try {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {
                    String baseurl = result.get("DomainURL");
                    String clientId = result.get("ClientId");
                    if (passwordlessEntity.getSub() != null && !passwordlessEntity.getSub().equals("") &&
                            (passwordlessEntity.getUsageType() != null && !passwordlessEntity.getUsageType().equals("")
                                    && code != null && !code.equals(""))) {


                        if (passwordlessEntity.getUsageType().equals(UsageType.MFA)) {
                            if (passwordlessEntity.getTrackId() == null || passwordlessEntity.getTrackId() == "") {
                                String errorMessage = "trackId must not be empty";

                                loginresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                        errorMessage, HttpStatusCode.EXPECTATION_FAILED));
                                return;
                            }
                        }

                        InitiateBackupCodeMFARequestEntity initiateBackupCodeMFARequestEntity = new InitiateBackupCodeMFARequestEntity();
                        initiateBackupCodeMFARequestEntity.setSub(passwordlessEntity.getSub());
                        initiateBackupCodeMFARequestEntity.setUsageType(passwordlessEntity.getUsageType());
                        initiateBackupCodeMFARequestEntity.setVerificationType("BACKUPCODE");

                     /*   String userDeviceId=DBHelper.getShared().getUserDeviceId(baseurl);
                        if(userDeviceId!=null && !userDeviceId.equals("") ) {
                            initiateBackupCodeMFARequestEntity.setUserDeviceId(userDeviceId);
                        }
                        else
                        {
                            String errorMessage="UserDeviceId must not be empty";

                            initiateresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                    errorMessage,HttpStatusCode.EXPECTATION_FAILED));
                        }
                        */
                        BackupCodeConfigurationController.getShared(context).loginWithBackupCode(code, baseurl,
                                passwordlessEntity.getTrackId(), clientId, passwordlessEntity.getRequestId(),
                                initiateBackupCodeMFARequestEntity, loginresult);
                    } else {

                        loginresult.failure(WebAuthError.getShared(context).propertyMissingException());
                    }

                }

                @Override
                public void failure(WebAuthError error) {
                    loginresult.failure(WebAuthError.getShared(context).propertyMissingException());
                }
            });
        } catch (Exception e) {
            loginresult.failure(WebAuthError.getShared(context).propertyMissingException());
        }

    }

    @Override
    public void verifyBackupcode(final String code, final String statusId, final Result<LoginCredentialsResponseEntity> loginresult) {
        try {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {
                    String baseurl = result.get("DomainURL");
                    String clientId = result.get("ClientId");

                    if (code != null && code != "") {

                        AuthenticateBackupCodeRequestEntity authenticateBackupCodeRequestEntity = new AuthenticateBackupCodeRequestEntity();
                        authenticateBackupCodeRequestEntity.setVerifierPassword(code);
                        authenticateBackupCodeRequestEntity.setStatusId(statusId);

                        BackupCodeConfigurationController.getShared(context).verifyBackupCode(baseurl, clientId,
                                authenticateBackupCodeRequestEntity, loginresult);
/*
                          String userDeviceId = DBHelper.getShared().getUserDeviceId(baseurl);

                          if (userDeviceId != null && !userDeviceId.equals("")) {
                              authenticatePatternRequestEntity.setUserDeviceId(userDeviceId);
                          } else {
                              loginresult.failure(WebAuthError.getShared(context).propertyMissingException());
                          }*/
                    } else {
                        String errorMessage = "code must not be empty";

                        loginresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                errorMessage, HttpStatusCode.EXPECTATION_FAILED));

                    }

                }

                @Override
                public void failure(WebAuthError error) {
                    loginresult.failure(WebAuthError.getShared(context).propertyMissingException());
                }
            });
        } catch (Exception e) {
            loginresult.failure(WebAuthError.getShared(context).propertyMissingException());
        }
    }



    // ****** DONE Verification *****-------------------------------------------------------------------------------------------------------



    public void updateFCMToken(@NonNull final String sub,@NonNull final String FCMToken)
    {

        try {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {

                    final String baseurl = result.get("DomainURL");
                    String clientId = result.get("ClientId");

                    String oldFCMToken=DBHelper.getShared().getFCMToken();


                    if(oldFCMToken.equalsIgnoreCase(FCMToken))
                    {
                        //Do nothing
                    }
                    else{
                        DBHelper.getShared().setFCMToken(FCMToken);

                        AccessTokenController.getShared(context).getAccessToken(sub, new Result<AccessTokenEntity>() {
                            @Override
                            public void success(AccessTokenEntity result) {
                                VerificationSettingsController.getShared(context).updateFCMToken(baseurl, result.getAccess_token(), FCMToken, new Result<Object>() {
                                    @Override
                                    public void success(Object result) {
                                        LogFile.addRecordToLog("Update FCM Token Success");
                                    }

                                    @Override
                                    public void failure(WebAuthError error) {
                                        LogFile.addRecordToLog("Update FCM Token Error" + error.getMessage());
                                    }
                                });
                            }

                            @Override
                            public void failure(WebAuthError error) {
                                LogFile.addRecordToLog("Update FCM Token exception" + error.getMessage());
                            }
                        });


                    }


                }

                @Override
                public void failure(WebAuthError error) {
                    LogFile.addRecordToLog("Update FCM Token exception" + error.getMessage());
                }
            });
        }

        catch (Exception e)
        {
            LogFile.addRecordToLog("Update FCM Token exception" + e.getMessage());
            Timber.e("Update FCM Token exception" + e.getMessage());
        }
    }


    public void scanned(@NonNull final String statusId, @NonNull final String sub,@NonNull final String verificationType, final Result<ScannedResponseEntity> scannedResult)
    {
        try
        {

            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {

                    String baseurl = result.get("DomainURL");
                    String clientId=result.get("ClientId");



                    if(verificationType.equalsIgnoreCase("PATTERN"))
                    {
                        PatternConfigurationController.getShared(context).scannedWithPattern(baseurl,statusId,clientId,scannedResult);
                    }
                    else if(verificationType.equalsIgnoreCase("TOUCHID"))
                    {
                        FingerprintConfigurationController.getShared(context).scannedWithFingerprint(baseurl,statusId,clientId,scannedResult);
                    }
                    else if(verificationType.equalsIgnoreCase("VOICE"))
                    {
                        VoiceConfigurationController.getShared(context).scannedWithVoice(baseurl,statusId,clientId,scannedResult);
                    }
                    else if(verificationType.equalsIgnoreCase("FACE"))
                    {
                        FaceConfigurationController.getShared(context).scannedWithFace(baseurl,statusId,clientId,scannedResult);
                    }
                    else if(verificationType.equalsIgnoreCase("TOTP"))
                    {
                        TOTPConfigurationController.getShared(context).scannedWithTOTP(baseurl,statusId,clientId,scannedResult);
                    }
                    else if(verificationType.equalsIgnoreCase("PUSH"))
                    {
                        SmartPushConfigurationController.getShared(context).scannedWithSmartPush(baseurl,statusId,clientId,scannedResult);
                    }
                    else if(verificationType.equalsIgnoreCase("FIDOU2F"))
                    {
                        FIDOConfigurationController.getShared(context).scannedWithFIDO(baseurl,statusId,clientId,scannedResult);
                    }

                }

                @Override
                public void failure(WebAuthError error) {
                    scannedResult.failure(error);
                }
            });

        }
        catch (Exception e)
        {
            LogFile.addRecordToLog("Scanned Pattern exception" + e.getMessage());
            scannedResult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,"Scanned Pattern exception"+ e.getMessage(),
                    HttpStatusCode.EXPECTATION_FAILED));
            Timber.e("Scanned Pattern exception" + e.getMessage());
        }
    }


    // ****** DONE PATTERN *****-------------------------------------------------------------------------------------------------------


    @Override
    public void configurePatternRecognition(@NonNull final String pattern, @NonNull final String sub,@NonNull final String logoURL,
                                            final Result<EnrollPatternMFAResponseEntity> enrollresult)
    {
        try {


            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {
                    String baseurl = result.get("DomainURL");
                    String clientId=result.get("ClientId");


                    if (sub != null && !sub.equals("") && baseurl != null && !baseurl.equals("") &&
                            pattern != null && !pattern.equals("")) {

                        final String finalBaseurl = baseurl;


                        if(!logoURL.equals("") && logoURL!=null) {
                           logoURLlocal=logoURL;
                        }

                        SetupPatternMFARequestEntity setupPatternMFARequestEntity = new SetupPatternMFARequestEntity();
                        setupPatternMFARequestEntity.setClient_id(result.get("ClientId"));
                        setupPatternMFARequestEntity.setLogoUrl(logoURLlocal);
                        PatternConfigurationController.getShared(context).configurePattern(sub, finalBaseurl, pattern, setupPatternMFARequestEntity,
                                enrollresult);

                    } else {
                        String errorMessage = "Sub or Pattern or logoURL cannot be null";
                        enrollresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING, errorMessage,
                                HttpStatusCode.EXPECTATION_FAILED));
                    }
                }

                @Override
                public void failure(WebAuthError error) {

                    enrollresult.failure(error);
                }
            });

        } catch (Exception e) {
            LogFile.addRecordToLog("Configure Pattern exception" + e.getMessage());
            enrollresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,"Configure Pattern exception"+ e.getMessage(),
                    HttpStatusCode.EXPECTATION_FAILED));
            Timber.e("Configure Pattern exception" + e.getMessage());
        }


    }





    public void enrollPattern(@NonNull final String patternString, @NonNull final String sub,@NonNull final String statusId,  final Result<EnrollPatternMFAResponseEntity> enrollResult)
    {
        try
        {

            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {

                    final String baseurl = result.get("DomainURL");
                    final String clientId =result.get("ClientId");

                    String userDeviceId=DBHelper.getShared().getUserDeviceId(baseurl);

                    final EnrollPatternMFARequestEntity enrollPatternMFARequestEntity=new EnrollPatternMFARequestEntity();
                    enrollPatternMFARequestEntity.setVerifierPassword(patternString);
                    enrollPatternMFARequestEntity.setStatusId(statusId);
                    enrollPatternMFARequestEntity.setUserDeviceId(userDeviceId);
                    enrollPatternMFARequestEntity.setClientId(clientId);

                    AccessTokenController.getShared(context).getAccessToken(sub, new Result<AccessTokenEntity>() {
                        @Override
                        public void success(AccessTokenEntity result) {
                            PatternConfigurationController.getShared(context).enrollPattern(baseurl,result.getAccess_token(),enrollPatternMFARequestEntity,enrollResult);
                        }

                        @Override
                        public void failure(WebAuthError error) {
                          enrollResult.failure(error);
                        }
                    });



                }

                @Override
                public void failure(WebAuthError error) {
                   enrollResult.failure(error);
                }
            });
        }
        catch (Exception e)
        {
            LogFile.addRecordToLog("Enroll Pattern exception" + e.getMessage());
            enrollResult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,"Enroll Pattern exception"+ e.getMessage(),
                    HttpStatusCode.EXPECTATION_FAILED));
            Timber.e("Enroll Pattern exception" + e.getMessage());
        }
    }

    public void scannedPattern(@NonNull final String statusId, @NonNull final String sub, final Result<ScannedResponseEntity> scannedResult)
    {
      try
      {

          checkSavedProperties(new Result<Dictionary<String, String>>() {
              @Override
              public void success(Dictionary<String, String> result) {

                  String baseurl = result.get("DomainURL");
                  String clientId=result.get("ClientId");


                  PatternConfigurationController.getShared(context).scannedWithPattern(baseurl,statusId,clientId,scannedResult);
              }

              @Override
              public void failure(WebAuthError error) {
                  scannedResult.failure(error);
              }
          });

      }
      catch (Exception e)
      {
          LogFile.addRecordToLog("Scanned Pattern exception" + e.getMessage());
          scannedResult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,"Scanned Pattern exception"+ e.getMessage(),
                  HttpStatusCode.EXPECTATION_FAILED));
          Timber.e("Scanned Pattern exception" + e.getMessage());
      }
    }



    //Todo login with pattern by Passing the pattern String Directly
    // 1. Todo Check For Local Variable or Read properties from file
    // 2. Todo Check For NotNull Values
    // 3. Todo getAccessToken From Sub
    // 3. Todo Call configure Pattern From Pattern Controller and return the result
    // 4. Todo Maintain logs based on flags
    @Override
    public void loginWithPatternRecognition(@NonNull final String pattern, @NonNull final PasswordlessEntity passwordlessEntity,
                                            final Result<LoginCredentialsResponseEntity> loginresult) {

        try {


            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {
                    String baseurl = result.get("DomainURL");
                    String clientId = result.get("ClientId");

                    if (passwordlessEntity.getUsageType() != null && passwordlessEntity.getUsageType() != ""
                            && pattern != null && !pattern.equals("") && passwordlessEntity.getRequestId() != null &&
                            passwordlessEntity.getRequestId() != "") {

                        if (baseurl == null || baseurl.equals("") && clientId == null || clientId.equals("")) {
                            String errorMessage = "baseurl or clientId or mobile number must not be empty";

                            loginresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                    errorMessage, HttpStatusCode.EXPECTATION_FAILED));
                            return;
                        }

                        if (((passwordlessEntity.getSub() == null || passwordlessEntity.getSub().equals("")) &&
                                (passwordlessEntity.getEmail() == null || passwordlessEntity.getEmail().equals("")) &&
                                (passwordlessEntity.getMobile() == null || passwordlessEntity.getMobile().equals("")))) {
                            String errorMessage = "sub or email or mobile number must not be empty";

                            loginresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                    errorMessage, HttpStatusCode.EXPECTATION_FAILED));
                            return;
                        }

                        if (passwordlessEntity.getUsageType().equals(UsageType.MFA)) {
                            if (passwordlessEntity.getTrackId() == null || passwordlessEntity.getTrackId() == "") {
                                String errorMessage = "trackId must not be empty";

                                loginresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                        errorMessage, HttpStatusCode.EXPECTATION_FAILED));
                                return;
                            }
                        }

                        InitiatePatternMFARequestEntity initiatePatternMFARequestEntity = new InitiatePatternMFARequestEntity();
                        initiatePatternMFARequestEntity.setSub(passwordlessEntity.getSub());
                        initiatePatternMFARequestEntity.setUsageType(passwordlessEntity.getUsageType());
                        initiatePatternMFARequestEntity.setEmail(passwordlessEntity.getEmail());
                        initiatePatternMFARequestEntity.setMobile(passwordlessEntity.getMobile());

                        //Todo check for email or sub or mobile


                        PatternConfigurationController.getShared(context).LoginWithPattern(pattern, baseurl, clientId,
                                passwordlessEntity.getTrackId(),
                                passwordlessEntity.getRequestId(), initiatePatternMFARequestEntity, loginresult);
                    } else {
                        String errorMessage = "UsageType or PatternCode or requestId must not be empty";

                        loginresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                errorMessage, HttpStatusCode.EXPECTATION_FAILED));
                    }
                }

                @Override
                public void failure(WebAuthError error) {
                    loginresult.failure(error);
                }
            });


        } catch (Exception e) {
            LogFile.addRecordToLog("Login with Pattern exception" + e.getMessage());
            String errorMessage = e.getMessage();
            loginresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                    errorMessage, HttpStatusCode.EXPECTATION_FAILED));
            Timber.e("Login with Pattern exception" + e.getMessage());
        }
    }


    public void verifyPattern(final String patternString, final String statusId, final Result<AuthenticatePatternResponseEntity> result)
    {
        try {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> lpresult) {
                    String baseurl = lpresult.get("DomainURL");
                    String clientId = lpresult.get("ClientId");
                    //todo call verify pattern

                    AuthenticatePatternRequestEntity authenticatePatternRequestEntity=new AuthenticatePatternRequestEntity();
                    authenticatePatternRequestEntity.setStatusId(statusId);
                    authenticatePatternRequestEntity.setVerifierPassword(patternString);
                    authenticatePatternRequestEntity.setUserDeviceId(DBHelper.getShared().getUserDeviceId(baseurl));
                    authenticatePatternRequestEntity.setClient_id(clientId);



                    PatternConfigurationController.getShared(context).authenticatePattern(baseurl,authenticatePatternRequestEntity,result);
                   // PatternVerificationService.getShared(context).authenticatePattern(baseurl,authenticatePatternRequestEntity,null,result);

                }

                @Override
                public void failure(WebAuthError error) {
                    result.failure(WebAuthError.getShared(context).propertyMissingException());
                }
            });
        } catch (Exception e) {
            LogFile.addRecordToLog("Configure Face exception" + e.getMessage());
            result.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.INITIATE_FACE_MFA_FAILURE,
                    e.getMessage(), HttpStatusCode.EXPECTATION_FAILED));
            Timber.e("Configure Face exception" + e.getMessage());
        }

    }





    // ****** TODO LOGIN WITH FACE *****-------------------------------------------------------------------------------------------------------


    @Override
    public void configureFaceRecognition(final File photo, final String sub,@NonNull final String logoURL,@NonNull final int attempt, final Result<EnrollFaceMFAResponseEntity> enrollresult)
    {
        try {


            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {
                    String baseurl = result.get("DomainURL");


                    if (sub != null && !sub.equals("") && baseurl != null && !baseurl.equals("")) {

                       if(!logoURL.equals("") && logoURL!=null) {
                            logoURLlocal=logoURL;
                        }

                        // String logoUrl = "https://docs.cidaas.de/assets/logoss.png";

                        SetupFaceMFARequestEntity setupFaceMFARequestEntity = new SetupFaceMFARequestEntity();
                        setupFaceMFARequestEntity.setClient_id(result.get("ClientId"));
                        setupFaceMFARequestEntity.setLogoUrl(logoURLlocal);



                        FaceConfigurationController.getShared(context).configureFace(photo, sub, baseurl, attempt,setupFaceMFARequestEntity, enrollresult);


                    }
                }

                @Override
                public void failure(WebAuthError error) {
                    enrollresult.failure(WebAuthError.getShared(context).propertyMissingException());
                }
            });


        } catch (Exception e) {
            LogFile.addRecordToLog("Configure Face exception" + e.getMessage());
            enrollresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.INITIATE_FACE_MFA_FAILURE,
                    e.getMessage(), HttpStatusCode.EXPECTATION_FAILED));
            Timber.e("Configure Face exception" + e.getMessage());
        }


    }


    @Override
    public void loginWithFaceRecognition(@NonNull final File photo, @NonNull final PasswordlessEntity passwordlessEntity,
                                         final Result<LoginCredentialsResponseEntity> loginresult) {
        try {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {

                    String baseurl = result.get("DomainURL");
                    String clientId = result.get("ClientId");

                    if (passwordlessEntity.getUsageType() != null && !passwordlessEntity.getUsageType().equals("") &&
                            passwordlessEntity.getRequestId() != null && !passwordlessEntity.getRequestId().equals("") &&
                            photo != null) {

                        if (baseurl == null || baseurl.equals("") && clientId == null || clientId.equals("")) {
                            String errorMessage = "baseurl or clientId must not be empty";

                            loginresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                    errorMessage, HttpStatusCode.EXPECTATION_FAILED));
                        }

                        if (((passwordlessEntity.getSub() == null || passwordlessEntity.getSub().equals("")) &&
                                (passwordlessEntity.getEmail() == null || passwordlessEntity.getEmail().equals("")) &&
                                (passwordlessEntity.getMobile() == null || passwordlessEntity.getMobile().equals("")))) {
                            String errorMessage = "sub or email or mobile number must not be empty";

                            loginresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                    errorMessage, HttpStatusCode.EXPECTATION_FAILED));
                        }

                        if (passwordlessEntity.getUsageType().equals(UsageType.MFA)) {
                            if (passwordlessEntity.getTrackId() == null || passwordlessEntity.getTrackId() == "") {
                                String errorMessage = "trackId must not be empty";

                                loginresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                        errorMessage, HttpStatusCode.EXPECTATION_FAILED));
                                return;
                            }
                        }

                        InitiateFaceMFARequestEntity initiateFaceMFARequestEntity = new InitiateFaceMFARequestEntity();
                        initiateFaceMFARequestEntity.setSub(passwordlessEntity.getSub());
                        initiateFaceMFARequestEntity.setUsageType(passwordlessEntity.getUsageType());
                        initiateFaceMFARequestEntity.setEmail(passwordlessEntity.getEmail());
                        initiateFaceMFARequestEntity.setMobile(passwordlessEntity.getMobile());

                        //Todo check for email or sub or mobile


                        FaceConfigurationController.getShared(context).LoginWithFace(photo, baseurl, clientId,
                                passwordlessEntity.getTrackId(), passwordlessEntity.getRequestId(),
                                initiateFaceMFARequestEntity, loginresult);
                    } else {
                        String errorMessage = "Image File or RequestId or UsageType must not be empty";

                        loginresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                errorMessage, HttpStatusCode.EXPECTATION_FAILED));
                    }

                }

                @Override
                public void failure(WebAuthError error) {
                    loginresult.failure(WebAuthError.getShared(context).propertyMissingException());
                }
            });
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            loginresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.INITIATE_FACE_MFA_FAILURE,
                    errorMessage, HttpStatusCode.EXPECTATION_FAILED));
        }
    }

    public void scannedFace(@NonNull final String statusId, @NonNull final String sub, final Result<ScannedResponseEntity> scannedResult)
    {
        try
        {

            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {

                    String baseurl = result.get("DomainURL");
                    String clientId=result.get("ClientId");



                    FaceConfigurationController.getShared(context).scannedWithFace(baseurl,statusId,clientId,scannedResult);
                }

                @Override
                public void failure(WebAuthError error) {
                    scannedResult.failure(error);
                }
            });

        }
        catch (Exception e)
        {
            LogFile.addRecordToLog("Scanned Face exception" + e.getMessage());
            scannedResult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.SCANNED_FACE_MFA_FAILURE,"Scanned Face exception"+ e.getMessage(),
                    HttpStatusCode.EXPECTATION_FAILED));
            Timber.e("Scanned Face exception" + e.getMessage());
        }
    }

    public void enrollFace(@NonNull final File Face, @NonNull final String sub,@NonNull final String statusId,@NonNull final int attempt,  final Result<EnrollFaceMFAResponseEntity> enrollResult)
    {
        try
        {

            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {

                    final String baseurl = result.get("DomainURL");
                    final String clientId= result.get("ClientId");
                    String userDeviceId=DBHelper.getShared().getUserDeviceId(baseurl);

                    final EnrollFaceMFARequestEntity enrollFaceMFARequestEntity=new EnrollFaceMFARequestEntity();
                    enrollFaceMFARequestEntity.setImagetoSend(Face);
                    enrollFaceMFARequestEntity.setStatusId(statusId);
                    enrollFaceMFARequestEntity.setUserDeviceId(userDeviceId);
                    enrollFaceMFARequestEntity.setClient_id(clientId);
                    enrollFaceMFARequestEntity.setAttempt(attempt);

                    AccessTokenController.getShared(context).getAccessToken(sub, new Result<AccessTokenEntity>() {
                        @Override
                        public void success(AccessTokenEntity result) {
                            FaceConfigurationController.getShared(context).enrollFace(baseurl,result.getAccess_token(),enrollFaceMFARequestEntity,enrollResult);
                        }

                        @Override
                        public void failure(WebAuthError error) {
                            enrollResult.failure(error);
                        }
                    });



                }

                @Override
                public void failure(WebAuthError error) {
                    enrollResult.failure(error);
                }
            });
        }
        catch (Exception e)
        {
            LogFile.addRecordToLog("Enroll Face exception" + e.getMessage());
            enrollResult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.ENROLL_FACE_MFA_FAILURE,"Enroll Face exception"+ e.getMessage(),
                    HttpStatusCode.EXPECTATION_FAILED));
            Timber.e("Enroll Face exception" + e.getMessage());
        }
    }


    public void verifyFace(@NonNull final File photo,@NonNull final String statusId, final Result<AuthenticateFaceResponseEntity> result) {
        try {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> lpresult) {
                    String baseurl = lpresult.get("DomainURL");
                    String clientId = lpresult.get("ClientId");
                    //todo call enroll Email



                    AuthenticateFaceRequestEntity authenticateFaceRequestEntity=new AuthenticateFaceRequestEntity();
                    authenticateFaceRequestEntity.setStatusId(statusId);
                    authenticateFaceRequestEntity.setImagetoSend(photo);
                    authenticateFaceRequestEntity.setUserDeviceId(DBHelper.getShared().getUserDeviceId(baseurl));


                    FaceConfigurationController.getShared(context).authenticateFace(baseurl,authenticateFaceRequestEntity,result);


                }

                @Override
                public void failure(WebAuthError error) {
                    result.failure(WebAuthError.getShared(context).propertyMissingException());
                }
            });
        } catch (Exception e) {
            LogFile.addRecordToLog("Verify Face exception" + e.getMessage());
            String errorMessage = e.getMessage();
            result.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.AUTHENTICATE_EMAIL_MFA_FAILURE,
                    errorMessage, HttpStatusCode.EXPECTATION_FAILED));
        }

    }



    // ****** TODO LOGIN WITH MultiFACE *****-------------------------------------------------------------------------------------------------------


    public void configureFaceRecognition(final List<File> photo, final String sub, @NonNull final String logoURL, final Result<EnrollFaceMFAResponseEntity> enrollresult) {
        try {


            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {
                    String baseurl = result.get("DomainURL");


                    if (sub != null && !sub.equals("") && baseurl != null && !baseurl.equals("")) {

                        if(!logoURL.equals("") && logoURL!=null) {
                            logoURLlocal=logoURL;
                        }

                        // String logoUrl = "https://docs.cidaas.de/assets/logoss.png";

                        SetupFaceMFARequestEntity setupFaceMFARequestEntity = new SetupFaceMFARequestEntity();
                        setupFaceMFARequestEntity.setClient_id(result.get("ClientId"));
                        setupFaceMFARequestEntity.setLogoUrl(logoURLlocal);


                      //  FaceConfigurationController.getShared(context).ConfigureFaces(photo, sub, baseurl, setupFaceMFARequestEntity, enrollresult);


                    }
                }

                @Override
                public void failure(WebAuthError error) {
                    enrollresult.failure(error);
                }
            });


        } catch (Exception e) {
            LogFile.addRecordToLog("Configure Face exception" + e.getMessage());
            enrollresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.ENROLL_FACE_MFA_FAILURE,"Configure Face exception" + e.getMessage(),417));
            Timber.e("Configure Face exception" + e.getMessage());
        }


    }



    public void loginWithFaceRecognition(@NonNull final List<File> photo, @NonNull final PasswordlessEntity passwordlessEntity,
                                         final Result<LoginCredentialsResponseEntity> loginresult) {
        try {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {

                    String baseurl = result.get("DomainURL");
                    String clientId = result.get("ClientId");

                    if (passwordlessEntity.getUsageType() != null && !passwordlessEntity.getUsageType().equals("") &&
                            passwordlessEntity.getRequestId() != null && !passwordlessEntity.getRequestId().equals("") &&
                            photo != null) {

                        if (baseurl == null || baseurl.equals("") && clientId == null || clientId.equals("")) {
                            String errorMessage = "baseurl or clientId must not be empty";

                            loginresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                    errorMessage, HttpStatusCode.EXPECTATION_FAILED));
                        }

                        if (((passwordlessEntity.getSub() == null || passwordlessEntity.getSub().equals("")) &&
                                (passwordlessEntity.getEmail() == null || passwordlessEntity.getEmail().equals("")) &&
                                (passwordlessEntity.getMobile() == null || passwordlessEntity.getMobile().equals("")))) {
                            String errorMessage = "sub or email or mobile number must not be empty";

                            loginresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                    errorMessage, HttpStatusCode.EXPECTATION_FAILED));
                        }

                        if (passwordlessEntity.getUsageType().equals(UsageType.MFA)) {
                            if (passwordlessEntity.getTrackId() == null || passwordlessEntity.getTrackId() == "") {
                                String errorMessage = "trackId must not be empty";

                                loginresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                        errorMessage, HttpStatusCode.EXPECTATION_FAILED));
                                return;
                            }
                        }

                        InitiateFaceMFARequestEntity initiateFaceMFARequestEntity = new InitiateFaceMFARequestEntity();
                        initiateFaceMFARequestEntity.setSub(passwordlessEntity.getSub());
                        initiateFaceMFARequestEntity.setUsageType(passwordlessEntity.getUsageType());
                        initiateFaceMFARequestEntity.setEmail(passwordlessEntity.getEmail());
                        initiateFaceMFARequestEntity.setMobile(passwordlessEntity.getMobile());

                        //Todo check for email or sub or mobile


                      /*  FaceConfigurationController.getShared(context).LoginWithFaces(photo, baseurl, clientId,
                                passwordlessEntity.getTrackId(), passwordlessEntity.getRequestId(),
                                initiateFaceMFARequestEntity, loginresult);*/
                    } else {
                        String errorMessage = "Image File or RequestId or UsageType must not be empty";

                        loginresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                errorMessage, HttpStatusCode.EXPECTATION_FAILED));
                    }

                }

                @Override
                public void failure(WebAuthError error) {
                    loginresult.failure(WebAuthError.getShared(context).propertyMissingException());
                }
            });
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            loginresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                    errorMessage, HttpStatusCode.EXPECTATION_FAILED));
        }
    }


    public void verifyFace(@NonNull final List<File> photo,final String statusId, final Result<AuthenticateFaceResponseEntity> result) {
        try {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> lpresult) {
                    String baseurl = lpresult.get("DomainURL");
                    String clientId = lpresult.get("ClientId");
                    //todo call enroll Email

                    AuthenticateFaceRequestEntity authenticateFaceRequestEntity=new AuthenticateFaceRequestEntity();
                    authenticateFaceRequestEntity.setStatusId(statusId);
                    authenticateFaceRequestEntity.setImagesToSend(photo);
                    authenticateFaceRequestEntity.setUserDeviceId(DBHelper.getShared().getUserDeviceId(baseurl));
                    authenticateFaceRequestEntity.setClient_id(clientId);


                    FaceVerificationService.getShared(context).authenticateFace(baseurl,authenticateFaceRequestEntity,null,result);


                }

                @Override
                public void failure(WebAuthError error) {
                    result.failure(WebAuthError.getShared(context).propertyMissingException());
                }
            });
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            result.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                    errorMessage, HttpStatusCode.EXPECTATION_FAILED));
        }

    }


    // ****** TODO LOGIN WITH Finger *****-------------------------------------------------------------------------------------------------------


    @Override
    public void configureFingerprint(final Context context, final String sub, @NonNull final String logoURL, FingerPrintEntity fingerPrintEntity, final Result<EnrollFingerprintMFAResponseEntity> enrollresult) {
        try {
            if (Build.VERSION.SDK_INT >= 23) {


                callFingerPrint(context, fingerPrintEntity, new Result<String>() {
                    @Override
                    public void success(String result) {
                        checkSavedProperties(new Result<Dictionary<String, String>>() {
                            @Override
                            public void success(Dictionary<String, String> result) {
                                final String baseurl = result.get("DomainURL");
                                final String clinetId = result.get("ClientId");


                                if (sub != null && !sub.equals("") && baseurl != null && !baseurl.equals("")) {

                                    final String finalBaseurl = baseurl;


                                    // String logoUrl = "https://docs.cidaas.de/assets/logoss.png";


                                    if (!logoURL.equals("") && logoURL != null) {
                                        logoURLlocal = logoURL;
                                    }

                                    SetupFingerprintMFARequestEntity setupFingerprintMFARequestEntity = new SetupFingerprintMFARequestEntity();
                                    setupFingerprintMFARequestEntity.setClient_id(clinetId);
                                    setupFingerprintMFARequestEntity.setLogoUrl(logoURLlocal);
                                    FingerprintConfigurationController.getShared(context).configureFingerprint(sub, finalBaseurl, setupFingerprintMFARequestEntity,
                                            enrollresult);

                                } else {
                                    String errorMessage = "Sub or Pattern cannot be null";
                                    enrollresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING, errorMessage,
                                            HttpStatusCode.EXPECTATION_FAILED));
                                }
                            }

                            @Override
                            public void failure(WebAuthError error) {
                                enrollresult.failure(error);
                            }
                        });
                    }

                    @Override
                    public void failure(WebAuthError error) {
                    enrollresult.failure(error);
                    }
                });
            }
            else
            {
                String ErrorMessage="Fingerprint doesnot Support in your mobile";
                enrollresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.FINGERPRINT_AUTHENTICATION_FAILED,ErrorMessage,HttpStatusCode.EXPECTATION_FAILED));

            }

        } catch (Exception e) {
           // enrollresult.failure(WebAuthError.getShared(context).propertyMissingException());
            LogFile.addRecordToLog("Enroll Finger exception" + e.getMessage());
            enrollresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.ENROLL_FINGERPRINT_MFA_FAILURE,"Enroll Finger exception"+ e.getMessage(),
                    HttpStatusCode.EXPECTATION_FAILED));
            Timber.e("Enroll Finger exception" + e.getMessage());
        }

    }

    public void scannedFingerprint(@NonNull final String statusId, @NonNull final String sub, final Result<ScannedResponseEntity> scannedResult)
    {
        try
        {
            if (Build.VERSION.SDK_INT >= 23) {

                checkSavedProperties(new Result<Dictionary<String, String>>() {
                    @Override
                    public void success(Dictionary<String, String> result) {

                        String baseurl = result.get("DomainURL");
                        String clientId = result.get("ClientId");


                        FingerprintConfigurationController.getShared(context).scannedWithFingerprint(baseurl, statusId, clientId, scannedResult);
                    }

                    @Override
                    public void failure(WebAuthError error) {
                        scannedResult.failure(error);
                    }
                });
            }
            else{
                String ErrorMessage="Fingerprint doesnot Support in your mobile";
                scannedResult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.FINGERPRINT_AUTHENTICATION_FAILED,ErrorMessage,HttpStatusCode.EXPECTATION_FAILED));

            }

        }
        catch (Exception e)
        {
            LogFile.addRecordToLog("Scanned Fingerprint exception" + e.getMessage());
            scannedResult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,"Scanned Fingerprint exception"+ e.getMessage(),
                    HttpStatusCode.EXPECTATION_FAILED));
            Timber.e("Scanned Fingerprint exception" + e.getMessage());
        }
    }

    public void enrollFingerprint(final Context context, @NonNull final String sub, @NonNull final String statusId, FingerPrintEntity fingerPrintEntity, final Result<EnrollFingerprintMFAResponseEntity> enrollResult)
    {
        try
        {

            //Todo Call FingerPrintAuthentication

            callFingerPrint(context, fingerPrintEntity, new Result<String>() {
                @Override
                public void success(String result) {
                    checkSavedProperties(new Result<Dictionary<String, String>>() {
                        @Override
                        public void success(Dictionary<String, String> result) {

                            final String baseurl = result.get("DomainURL");
                            final String clientId= result.get("ClientId");

                            final String userDeviceId=DBHelper.getShared().getUserDeviceId(baseurl);

                            AccessTokenController.getShared(context).getAccessToken(sub, new Result<AccessTokenEntity>() {
                                @Override
                                public void success(AccessTokenEntity result) {
                                    EnrollFingerprintMFARequestEntity enrollFingerprintMFARequestEntity=new EnrollFingerprintMFARequestEntity();
                                    enrollFingerprintMFARequestEntity.setStatusId(statusId);
                                    enrollFingerprintMFARequestEntity.setUserDeviceId(userDeviceId);
                                    enrollFingerprintMFARequestEntity.setClient_id(clientId);


                                    FingerprintConfigurationController.getShared(context).enrollFingerprint(baseurl,result.getAccess_token(),enrollFingerprintMFARequestEntity,enrollResult);
                                }

                                @Override
                                public void failure(WebAuthError error) {
                                    enrollResult.failure(error);
                                }
                            });



                        }

                        @Override
                        public void failure(WebAuthError error) {
                            enrollResult.failure(error);
                        }
                    });
                }

                @Override
                public void failure(WebAuthError error) {
                      enrollResult.failure(error);
                }
            });


        }
        catch (Exception e)
        {
            LogFile.addRecordToLog("Enroll Fingerprint exception" + e.getMessage());
            enrollResult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,"Enroll Fingerprint exception"+ e.getMessage(),
                    HttpStatusCode.EXPECTATION_FAILED));
            Timber.e("Enroll Fingerprint exception" + e.getMessage());
        }
    }


    @Override
    public void loginWithFingerprint(final Context context, final PasswordlessEntity passwordlessEntity, FingerPrintEntity fingerPrintEntity, final Result<LoginCredentialsResponseEntity> loginresult) {
        try {


            callFingerPrint(context, fingerPrintEntity, new Result<String>() {
                        @Override
                        public void success(String result) {
                            checkSavedProperties(new Result<Dictionary<String, String>>() {
                                @Override
                                public void success(Dictionary<String, String> result) {
                                    final String baseurl = result.get("DomainURL");
                                    final String clientId = result.get("ClientId");


                                    if (passwordlessEntity.getUsageType() != null && passwordlessEntity.getUsageType() != "" &&
                                            passwordlessEntity.getRequestId() != null && passwordlessEntity.getRequestId() != "") {

                                        if (baseurl == null || baseurl.equals("") && clientId == null || clientId.equals("")) {
                                            String errorMessage = "baseurl or clientId  must not be empty";

                                            loginresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                                    errorMessage, HttpStatusCode.EXPECTATION_FAILED));
                                        }


                                        if (((passwordlessEntity.getSub() == null || passwordlessEntity.getSub().equals("")) &&
                                                (passwordlessEntity.getEmail() == null || passwordlessEntity.getEmail().equals("")) &&
                                                (passwordlessEntity.getMobile() == null || passwordlessEntity.getMobile().equals("")))) {
                                            String errorMessage = "sub or email or mobile number must not be empty";

                                            loginresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                                    errorMessage, HttpStatusCode.EXPECTATION_FAILED));
                                        }

                                        if (passwordlessEntity.getUsageType().equals(UsageType.MFA)) {
                                            if (passwordlessEntity.getTrackId() == null || passwordlessEntity.getTrackId() == "") {
                                                String errorMessage = "trackId must not be empty For Multifactor Authentication";

                                                loginresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                                        errorMessage, HttpStatusCode.EXPECTATION_FAILED));
                                                return;
                                            }
                                        }

                                        InitiateFingerprintMFARequestEntity initiateFingerprintMFARequestEntity = new InitiateFingerprintMFARequestEntity();
                                        initiateFingerprintMFARequestEntity.setSub(passwordlessEntity.getSub());
                                        initiateFingerprintMFARequestEntity.setUsageType(passwordlessEntity.getUsageType());
                                        initiateFingerprintMFARequestEntity.setEmail(passwordlessEntity.getEmail());
                                        initiateFingerprintMFARequestEntity.setMobile(passwordlessEntity.getMobile());

                                        //Todo check for email or sub or mobile


                                        FingerprintConfigurationController.getShared(context).LoginWithFingerprint(baseurl, clientId,
                                                passwordlessEntity.getTrackId(), passwordlessEntity.getRequestId(),
                                                initiateFingerprintMFARequestEntity, loginresult);
                                    } else {
                                        String errorMessage = "UsageType or FingerprintCode or requestId must not be empty";

                                        loginresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                                errorMessage, HttpStatusCode.EXPECTATION_FAILED));
                                    }
                                }

                                @Override
                                public void failure(WebAuthError error) {
                                    loginresult.failure(error);
                                }
                            });
                        }

                        @Override
                        public void failure(WebAuthError error) {
                           loginresult.failure(error);
                        }
                    });

        } catch (Exception e) {
            String errorMessage = e.getMessage();
            loginresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                    errorMessage, HttpStatusCode.EXPECTATION_FAILED));
        }
    }

    public void verifyFingerprint(final Context context, final String statusId, FingerPrintEntity fingerPrintEntity, final Result<AuthenticateFingerprintResponseEntity> callBackresult) {
        try {

            callFingerPrint(context, fingerPrintEntity, new Result<String>() {
                @Override
                public void success(String result) {
                    checkSavedProperties(new Result<Dictionary<String, String>>() {
                        @Override
                        public void success(Dictionary<String, String> result) {
                            String baseurl = result.get("DomainURL");
                            String clientId = result.get("ClientId");
                            //todo call enroll Email

                            AuthenticateFingerprintRequestEntity authenticateFingerprintRequestEntity=new AuthenticateFingerprintRequestEntity();
                            authenticateFingerprintRequestEntity.setStatusId(statusId);
                            authenticateFingerprintRequestEntity.setUserDeviceId(DBHelper.getShared().getUserDeviceId(baseurl));
                            authenticateFingerprintRequestEntity.setClient_id(clientId);


                            FingerprintConfigurationController.getShared(context).authenticateFingerprint(baseurl,authenticateFingerprintRequestEntity,callBackresult);

                        }

                        @Override
                        public void failure(WebAuthError error) {
                            callBackresult.failure(WebAuthError.getShared(context).propertyMissingException());
                        }
                    });
                }

                @Override
                public void failure(WebAuthError error) {
                     callBackresult.failure(error);
                }
            });

        } catch (Exception e) {
            //callBackresult.failure(WebAuthError.getShared(context).propertyMissingException());
            LogFile.addRecordToLog("Verify Finger exception" + e.getMessage());
            callBackresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.AUTHENTICATE_FINGERPRINT_MFA_FAILURE,"Verify Finger exception"+ e.getMessage(),
                    HttpStatusCode.EXPECTATION_FAILED));
            Timber.e("Verify Finger exception" + e.getMessage());
        }

    }







    // ****** DONE FIDO *****-------------------------------------------------------------------------------------------------------


    public void enrollFIDO(@NonNull final FIDOTouchResponse fidoResponse,  @NonNull final String sub, @NonNull final String statusId, final Result<EnrollFIDOMFAResponseEntity> enrollResult)
    {
    try
    {



        checkSavedProperties(new Result<Dictionary<String, String>>() {
            @Override
            public void success(Dictionary<String, String> lpresult) {
                final String baseurl = lpresult.get("DomainURL");
                final String clientId= lpresult.get("ClientId");

                AccessTokenController.getShared(context).getAccessToken(sub, new Result<AccessTokenEntity>() {
                    @Override
                    public void success(AccessTokenEntity accessTOkenresult) {

                        EnrollFIDOMFARequestEntity enrollFIDOMFARequestEntity=new EnrollFIDOMFARequestEntity();
                        enrollFIDOMFARequestEntity.setFidoTouchResponse(fidoResponse);
                        enrollFIDOMFARequestEntity.setStatusId(statusId);
                        enrollFIDOMFARequestEntity.setUserDeviceId(DBHelper.getShared().getUserDeviceId(baseurl));
                        enrollFIDOMFARequestEntity.setClient_id(clientId);

                        FIDOConfigurationController.getShared(context).enrollFIDO(baseurl,accessTOkenresult.getAccess_token(),enrollFIDOMFARequestEntity,enrollResult);
                    }

                    @Override
                    public void failure(WebAuthError error) {
                        enrollResult.failure(error);
                    }
                });
            }

            @Override
            public void failure(WebAuthError error) {
                enrollResult.failure(error);
            }
        });


    }
    catch (Exception e)
     {
         LogFile.addRecordToLog("enroll FIDO exception" + e.getMessage());
         enrollResult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,"Enroll FIDO exception"+ e.getMessage(),
                 HttpStatusCode.EXPECTATION_FAILED));
         Timber.e("Enroll FIDO exception" + e.getMessage());
     }
    }
    public void configureFIDO(@NonNull final IsoDep isoTag, @NonNull final String sub, @NonNull final String logoURL,
                                         final Result<EnrollFIDOMFAResponseEntity> enrollresult)
    {
        try {


            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {
                    String baseurl = result.get("DomainURL");


                    if (sub != null && !sub.equals("") && baseurl != null && !baseurl.equals("")) {

                        final String finalBaseurl = baseurl;


                        if(!logoURL.equals("") && logoURL!=null) {
                            logoURLlocal=logoURL;
                        }




                        SetupFIDOMFARequestEntity setupFIDOMFARequestEntity = new SetupFIDOMFARequestEntity();
                        setupFIDOMFARequestEntity.setClient_id(result.get("ClientId"));
                        setupFIDOMFARequestEntity.setLogoUrl(logoURLlocal);


                        FIDOConfigurationController.getShared(context).configureFIDO(isoTag,sub, finalBaseurl, setupFIDOMFARequestEntity,
                                enrollresult);

                    } else {
                        String errorMessage = "Sub or FIDO or logoURL cannot be null";
                        enrollresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING, errorMessage,
                                HttpStatusCode.EXPECTATION_FAILED));
                    }
                }

                @Override
                public void failure(WebAuthError error) {

                    enrollresult.failure(error);
                }
            });

        } catch (Exception e) {
            LogFile.addRecordToLog("Configure FIDO exception" + e.getMessage());
            enrollresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,"Configure FIDO exception"+ e.getMessage(),
                    HttpStatusCode.EXPECTATION_FAILED));
            Timber.e("Configure FIDO exception" + e.getMessage());
        }


    }



    // ****** TODO Logout for embeddedBrowser *****-------------------------------------------------------------------------------------------------------


    /* public void logout(@NonNull )
     {
         try
         {

         }
         catch (Exception e)
     }*/


   /* public void enrollFIDO(@NonNull final IsoDep isoTag, final ScannedResponseDataEntity scannedResponseDataEntity, @NonNull final String sub,@NonNull final String statusId,  final Result<EnrollFIDOMFAResponseEntity> enrollResult)
    {
        try
        {

            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {

                    final String baseurl = result.get("DomainURL");
                    String userDeviceId=DBHelper.getShared().getUserDeviceId(baseurl);

                 // EnrollFIDOMFARequestEntity enrollFIDOMFARequestEntity=new EnrollFIDOMFARequestEntity();
                    *//*enrollFIDOMFARequestEntity.setFidoTouchResponse(FIDO);
                    enrollFIDOMFARequestEntity.setStatusId(statusId);
                    enrollFIDOMFARequestEntity.setUserDeviceId(userDeviceId);*//*



                    //Entity For FIDO


                    AccessTokenController.getShared(context).getAccessToken(sub, new Result<AccessTokenEntity>() {
                        @Override
                        public void success(AccessTokenEntity result) {

                            if (scannedResponseDataEntity != null && scannedResponseDataEntity.getFidoInitRequest() != null
                                    && scannedResponseDataEntity.getFidoInitRequest().getRegisterRequests() != null
                                    && scannedResponseDataEntity.getFidoInitRequest().getRegisterRequests().length > 0) {


                                EnrollFIDOMFARequestEntity enrollFIDOMFARequestEntity =
                                        FIDOConfigurationController.getShared(context).generateEnrollEntity(isoTag, scannedResponseDataEntity.getFidoInitRequest().getRegisterRequests()[0].getChallenge(),
                                                scannedResponseDataEntity.getFidoInitRequest().getAppId(),scannedResponseDataEntity.getFidoInitRequest().getFidoRequestId(),baseurl);

                                enrollFIDOMFARequestEntity.setStatusId(statusId);
                                enrollFIDOMFARequestEntity.setUserDeviceId(DBHelper.getShared().getUserDeviceId(baseurl));
                                FIDOConfigurationController.getShared(context).enrollFIDO(baseurl,result.getAccess_token(),enrollFIDOMFARequestEntity,enrollResult);


                            }
                            else
                            {
                                enrollResult.failure(WebAuthError.getShared(context)
                                        .customException(WebAuthErrorCode.ENROLL_FIDO_MFA_FAILURE,"One of the property is missing",HttpStatusCode.EXPECTATION_FAILED));
                            }

                        }

                        @Override
                        public void failure(WebAuthError error) {
                            enrollResult.failure(error);
                        }
                    });



                }

                @Override
                public void failure(WebAuthError error) {
                    enrollResult.failure(error);
                }
            });
        }
        catch (Exception e)
        {
            LogFile.addRecordToLog("Enroll FIDO exception" + e.getMessage());
            enrollResult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,"Enroll FIDO exception"+ e.getMessage(),
                    HttpStatusCode.EXPECTATION_FAILED));
            Timber.e("Enroll FIDO exception" + e.getMessage());
        }
    }*/

    public void scannedFIDO(@NonNull final String statusId, @NonNull final String sub, final Result<ScannedResponseEntity> scannedResult)
    {
        try
        {

            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {

                    String baseurl = result.get("DomainURL");
                    String clientId=result.get("ClientId");


                    FIDOConfigurationController.getShared(context).scannedWithFIDO(baseurl,statusId,clientId,scannedResult);
                }

                @Override
                public void failure(WebAuthError error) {
                    scannedResult.failure(error);
                }
            });

        }
        catch (Exception e)
        {
            LogFile.addRecordToLog("Scanned FIDO exception" + e.getMessage());
            scannedResult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,"Scanned FIDO exception"+ e.getMessage(),
                    HttpStatusCode.EXPECTATION_FAILED));
            Timber.e("Scanned FIDO exception" + e.getMessage());
        }
    }



    //Todo login with FIDO by Passing the FIDO String Directly
    // 1. Todo Check For Local Variable or Read properties from file
    // 2. Todo Check For NotNull Values
    // 3. Todo getAccessToken From Sub
    // 3. Todo Call configure FIDO From FIDO Controller and return the result
    // 4. Todo Maintain logs based on flags

    public void loginWithFIDO(@NonNull final IsoDep isoTag, @NonNull final PasswordlessEntity passwordlessEntity,
                                            final Result<LoginCredentialsResponseEntity> loginresult) {

        try {


            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {
                    String baseurl = result.get("DomainURL");
                    String clientId = result.get("ClientId");

                    if (passwordlessEntity.getUsageType() != null && passwordlessEntity.getUsageType() != ""
                            && passwordlessEntity.getRequestId() != null && passwordlessEntity.getRequestId() != "") {

                        if (baseurl == null || baseurl.equals("") && clientId == null || clientId.equals("")) {
                            String errorMessage = "baseurl or clientId or mobile number must not be empty";

                            loginresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                    errorMessage, HttpStatusCode.EXPECTATION_FAILED));
                            return;
                        }

                        if (((passwordlessEntity.getSub() == null || passwordlessEntity.getSub().equals("")) &&
                                (passwordlessEntity.getEmail() == null || passwordlessEntity.getEmail().equals("")) &&
                                (passwordlessEntity.getMobile() == null || passwordlessEntity.getMobile().equals("")))) {
                            String errorMessage = "sub or email or mobile number must not be empty";

                            loginresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                    errorMessage, HttpStatusCode.EXPECTATION_FAILED));
                            return;
                        }

                        if (passwordlessEntity.getUsageType().equals(UsageType.MFA)) {
                            if (passwordlessEntity.getTrackId() == null || passwordlessEntity.getTrackId() == "") {
                                String errorMessage = "trackId must not be empty";

                                loginresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                        errorMessage, HttpStatusCode.EXPECTATION_FAILED));
                                return;
                            }
                        }

                        InitiateFIDOMFARequestEntity initiateFIDOMFARequestEntity = new InitiateFIDOMFARequestEntity();
                        initiateFIDOMFARequestEntity.setSub(passwordlessEntity.getSub());
                        initiateFIDOMFARequestEntity.setUsageType(passwordlessEntity.getUsageType());
                        initiateFIDOMFARequestEntity.setEmail(passwordlessEntity.getEmail());
                        initiateFIDOMFARequestEntity.setMobile(passwordlessEntity.getMobile());

                        //Todo check for email or sub or mobile

                        FIDOConfigurationController.getShared(context).LoginWithFIDO(isoTag, baseurl, clientId,
                                passwordlessEntity.getTrackId(),
                                passwordlessEntity.getRequestId(), initiateFIDOMFARequestEntity, loginresult);
                    } else {
                        String errorMessage = "UsageType or FIDOCode or requestId must not be empty";

                        loginresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                errorMessage, HttpStatusCode.EXPECTATION_FAILED));
                    }
                }

                @Override
                public void failure(WebAuthError error) {
                    loginresult.failure(error);
                }
            });


        } catch (Exception e) {
            LogFile.addRecordToLog("Login with FIDO exception" + e.getMessage());
            String errorMessage = e.getMessage();
            loginresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                    errorMessage, HttpStatusCode.EXPECTATION_FAILED));
            Timber.e("Login with FIDO exception" + e.getMessage());
        }
    }


    /*public void verifyFIDO(final IsoDep isoTag, final NFCSignObject signObject, final String statusId, final Result<AuthenticateFIDOResponseEntity> result)
    {
        try {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> lpresult) {
                    String baseurl = lpresult.get("DomainURL");
                    String clientId = lpresult.get("ClientId");
                    //todo call verify FIDO

                    AuthenticateFIDORequestEntity
                            authenticateFIDORequestEntity=FIDOConfigurationController.getShared(context).generateAuthenticateEntity(isoTag,baseurl,signObject);
                    authenticateFIDORequestEntity.setStatusId(statusId);
                    authenticateFIDORequestEntity.setUserDeviceId(DBHelper.getShared().getUserDeviceId(baseurl));



                    FIDOConfigurationController.getShared(context).authenticateFIDO(baseurl,authenticateFIDORequestEntity,result);
                    // FIDOVerificationService.getShared(context).authenticateFIDO(baseurl,authenticateFIDORequestEntity,null,result);

                }

                @Override
                public void failure(WebAuthError error) {
                    result.failure(WebAuthError.getShared(context).propertyMissingException());
                }
            });
        } catch (Exception e) {
            result.failure(WebAuthError.getShared(context).propertyMissingException());
        }

    }*/


    public void verifyFIDO(final FidoSignTouchResponse fidoSignTouchResponse, final String statusId, final Result<AuthenticateFIDOResponseEntity> result)
    {
        try {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> lpresult) {
                    String baseurl = lpresult.get("DomainURL");
                    String clientId = lpresult.get("ClientId");
                    //todo call verify FIDO

                    AuthenticateFIDORequestEntity authenticateFIDORequestEntity=new AuthenticateFIDORequestEntity();
                    authenticateFIDORequestEntity.setStatusId(statusId);
                    authenticateFIDORequestEntity.setUserDeviceId(DBHelper.getShared().getUserDeviceId(baseurl));
                    authenticateFIDORequestEntity.setFidoSignTouchResponse(fidoSignTouchResponse);
                    authenticateFIDORequestEntity.setClient_id(clientId);



                    FIDOConfigurationController.getShared(context).authenticateFIDO(baseurl,authenticateFIDORequestEntity,result);
                    // FIDOVerificationService.getShared(context).authenticateFIDO(baseurl,authenticateFIDORequestEntity,null,result);

                }

                @Override
                public void failure(WebAuthError error) {
                    result.failure(WebAuthError.getShared(context).propertyMissingException());
                }
            });
        } catch (Exception e) {
           // result.failure(WebAuthError.getShared(context).propertyMissingException());
            LogFile.addRecordToLog("Verify FIDO exception" + e.getMessage());
            result.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.AUTHENTICATE_FIDO_MFA_FAILURE,"Verify FIDO exception"+ e.getMessage(),
                    HttpStatusCode.EXPECTATION_FAILED));
            Timber.e("Scanned FIDO exception" + e.getMessage());

        }

    }


    // ****** TODO LOGIN WITH Smart push *****-------------------------------------------------------------------------------------------------------


    @Override
    public void configureSmartPush(final String sub,@NonNull final String logoURL, final Result<EnrollSmartPushMFAResponseEntity> enrollresult)
    {
        try {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {
                    String baseurl = result.get("DomainURL");
                    final String clientId= result.get("ClientId");

                    if (sub != null && !sub.equals("") && baseurl != null && !baseurl.equals("")) {

                        final String finalBaseurl = baseurl;

                        //String logoUrl = "https://docs.cidaas.de/assets/logoss.png";

                       if(!logoURL.equals("") && logoURL!=null) {
                            logoURLlocal=logoURL;
                        }

                        SetupSmartPushMFARequestEntity setupSmartPushMFARequestEntity = new SetupSmartPushMFARequestEntity();
                        setupSmartPushMFARequestEntity.setClient_id(result.get("ClientId"));
                        setupSmartPushMFARequestEntity.setLogoUrl(logoURLlocal);

                        SmartPushConfigurationController.getShared(context).configureSmartPush(sub, finalBaseurl, setupSmartPushMFARequestEntity,
                                enrollresult);

                    } else {
                        String errorMessage = "Sub or SmartPush cannot be null";
                        enrollresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING, errorMessage,
                                HttpStatusCode.EXPECTATION_FAILED));
                    }
                }

                @Override
                public void failure(WebAuthError error) {
                    enrollresult.failure(error);
                }
            });

        } catch (Exception e) {
            LogFile.addRecordToLog("configure SmartPush  exception" + e.getMessage());
            enrollresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.AUTHENTICATE_VOICE_MFA_FAILURE,"Configure SmartPush exception"+ e.getMessage(),
                    HttpStatusCode.EXPECTATION_FAILED));
            Timber.e("Configure smartpush  exception" + e.getMessage());
        }

    }

    public void scannedSmartPush(@NonNull final String statusId, @NonNull final String sub, final Result<ScannedResponseEntity> scannedResult)
    {
        try
        {

            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {

                    String baseurl = result.get("DomainURL");
                    String clientId=result.get("ClientId");

                    SmartPushConfigurationController.getShared(context).scannedWithSmartPush(baseurl,statusId,clientId,scannedResult);
                }

                @Override
                public void failure(WebAuthError error) {
                    scannedResult.failure(WebAuthError.getShared(context).propertyMissingException());
                }
            });

        }
        catch (Exception e)
        {
            LogFile.addRecordToLog("Scanned SmartPush exception" + e.getMessage());
            scannedResult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,"Scanned SmartPush exception"+ e.getMessage(),
                    HttpStatusCode.EXPECTATION_FAILED));
            Timber.e("Scanned SmartPush exception" + e.getMessage());
        }
    }

    public void enrollSmartPush(@NonNull final String randomNumber, @NonNull final String sub,@NonNull final String statusId,  final Result<EnrollSmartPushMFAResponseEntity> enrollResult)
    {
        try
        {

            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {

                    final String baseurl = result.get("DomainURL");
                    final String clientId= result.get("ClientId");

                    String userDeviceId=DBHelper.getShared().getUserDeviceId(baseurl);

                    final EnrollSmartPushMFARequestEntity enrollSmartPushMFARequestEntity=new EnrollSmartPushMFARequestEntity();
                    enrollSmartPushMFARequestEntity.setVerifierPassword(randomNumber);
                    enrollSmartPushMFARequestEntity.setStatusId(statusId);
                    enrollSmartPushMFARequestEntity.setUserDeviceId(userDeviceId);
                    enrollSmartPushMFARequestEntity.setClient_id(clientId);

                    AccessTokenController.getShared(context).getAccessToken(sub, new Result<AccessTokenEntity>() {
                        @Override
                        public void success(AccessTokenEntity result) {
                            SmartPushConfigurationController.getShared(context).enrollSmartPush(baseurl,result.getAccess_token(),enrollSmartPushMFARequestEntity,enrollResult);
                        }

                        @Override
                        public void failure(WebAuthError error) {
                            enrollResult.failure(error);
                        }
                    });



                }

                @Override
                public void failure(WebAuthError error) {
                    enrollResult.failure(error);
                }
            });
        }
        catch (Exception e)
        {
            LogFile.addRecordToLog("Enroll SmartPush exception" + e.getMessage());
            enrollResult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,"Enroll SmartPush exception"+ e.getMessage(),
                    HttpStatusCode.EXPECTATION_FAILED));
            Timber.e("Enroll SmartPush exception" + e.getMessage());
        }
    }


    @Override
    public void loginWithSmartPush(final PasswordlessEntity passwordlessEntity,
                                   final Result<LoginCredentialsResponseEntity> loginresult) {
        try {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {
                    String baseurl = result.get("DomainURL");
                    String clientId = result.get("ClientId");
                    if (passwordlessEntity.getUsageType() != null && passwordlessEntity.getUsageType() != "" &&
                            passwordlessEntity.getRequestId() != null && passwordlessEntity.getRequestId() != "") {

                        if (baseurl == null || baseurl.equals("") && clientId == null || clientId.equals("")) {
                            String errorMessage = "baseurl or clientId  must not be empty";

                            loginresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                    errorMessage, HttpStatusCode.EXPECTATION_FAILED));
                        }

                        if (((passwordlessEntity.getSub() == null || passwordlessEntity.getSub().equals("")) &&
                                (passwordlessEntity.getEmail() == null || passwordlessEntity.getEmail().equals("")) &&
                                (passwordlessEntity.getMobile() == null || passwordlessEntity.getMobile().equals("")))) {
                            String errorMessage = "sub or email or mobile number must not be empty";

                            loginresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                    errorMessage, HttpStatusCode.EXPECTATION_FAILED));
                        }

                        if (passwordlessEntity.getUsageType().equals(UsageType.MFA)) {
                            if (passwordlessEntity.getTrackId() == null || passwordlessEntity.getTrackId() == "") {
                                String errorMessage = "trackId must not be empty";

                                loginresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                        errorMessage, HttpStatusCode.EXPECTATION_FAILED));
                                return;
                            }
                        }

                        InitiateSmartPushMFARequestEntity initiateSmartPushMFARequestEntity = new InitiateSmartPushMFARequestEntity();
                        initiateSmartPushMFARequestEntity.setSub(passwordlessEntity.getSub());
                        initiateSmartPushMFARequestEntity.setUsageType(passwordlessEntity.getUsageType());
                        initiateSmartPushMFARequestEntity.setEmail(passwordlessEntity.getEmail());
                        initiateSmartPushMFARequestEntity.setMobile(passwordlessEntity.getMobile());
                        initiateSmartPushMFARequestEntity.setClient_id(clientId);

                        //Todo check for email or sub or mobile


                        SmartPushConfigurationController.getShared(context).LoginWithSmartPush(baseurl, clientId,
                                passwordlessEntity.getTrackId(), passwordlessEntity.getRequestId(),
                                initiateSmartPushMFARequestEntity, loginresult);
                    } else {
                        String errorMessage = "UsageType or SmartPushCode or requestId must not be empty";

                        loginresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                errorMessage, HttpStatusCode.EXPECTATION_FAILED));
                    }

                }

                @Override
                public void failure(WebAuthError error) {
                    loginresult.failure(WebAuthError.getShared(context).propertyMissingException());
                }
            });
        } catch (Exception e) {
            //loginresult.failure(WebAuthError.getShared(context).propertyMissingException());
            String loggerMessage = "Verify Smartpush : " + " Error Message - " + e.getMessage();
            LogFile.addRecordToLog(loggerMessage);
            loginresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.AUTHENTICATE_SMARTPUSH_MFA_FAILURE,"Something Went wrong please try again"+e.getMessage(),417));

        }
    }

    public void verifySmartPush(final String randomNumber, final String statusId, final Result<AuthenticateSmartPushResponseEntity> result) {
        try {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> lpresult) {
                    String baseurl = lpresult.get("DomainURL");
                    String clientId = lpresult.get("ClientId");
                    //todo call enroll Email


                    AuthenticateSmartPushRequestEntity authenticateSmartPushRequestEntity=new AuthenticateSmartPushRequestEntity();
                    authenticateSmartPushRequestEntity.setStatusId(statusId);
                    authenticateSmartPushRequestEntity.setVerifierPassword(randomNumber);
                    authenticateSmartPushRequestEntity.setUserDeviceId(DBHelper.getShared().getUserDeviceId(baseurl));
                    authenticateSmartPushRequestEntity.setClient_id(clientId);



                    SmartPushConfigurationController.getShared(context).authenticateSmartPush(baseurl,authenticateSmartPushRequestEntity,result);
                    //SmartPushVerificationService.getShared(context).authenticateSmartPush(baseurl,authenticateSmartPushRequestEntity,null,result);



                }

                @Override
                public void failure(WebAuthError error) {
                    result.failure(WebAuthError.getShared(context).propertyMissingException());
                }
            });
        } catch (Exception e) {
            String loggerMessage = "Verify Smartpush : " + " Error Message - " + e.getMessage();
            LogFile.addRecordToLog(loggerMessage);
            result.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.AUTHENTICATE_SMARTPUSH_MFA_FAILURE,"Something Went wrong please try again"+e.getMessage(),417));

        }

    }

    // ****** TODO LOGIN WITH TOTP *****-------------------------------------------------------------------------------------------------------


    @Override
    public void configureTOTP(final String sub,@NonNull final String logoURL, final Result<EnrollTOTPMFAResponseEntity> enrollresult) {
        try {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {
                    String baseurl = result.get("DomainURL");
                    String clientId = result.get("ClientId");

                    if (sub != null && !sub.equals("") && baseurl != null && !baseurl.equals("")) {

                        final String finalBaseurl = baseurl;

                        //String logoUrl = "https://docs.cidaas.de/assets/logoss.png";

                       if(!logoURL.equals("") && logoURL!=null) {
                            logoURLlocal=logoURL;
                        }


                        SetupTOTPMFARequestEntity setupTOTPMFARequestEntity = new SetupTOTPMFARequestEntity();
                        setupTOTPMFARequestEntity.setClient_id(result.get("ClientId"));
                        setupTOTPMFARequestEntity.setLogoUrl(logoURLlocal);


                        TOTPConfigurationController.getShared(context).configureTOTP(sub, finalBaseurl, setupTOTPMFARequestEntity, enrollresult);

                    } else {
                        String errorMessage = "Sub or TOTP cannot be null";
                        enrollresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING, errorMessage,
                                HttpStatusCode.EXPECTATION_FAILED));
                    }
                }

                @Override
                public void failure(WebAuthError error) {
                    enrollresult.failure(WebAuthError.getShared(context).propertyMissingException());
                }
            });

        } catch (Exception e) {
            LogFile.addRecordToLog("Enroll TOTP  exception" + e.getMessage());
            enrollresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.AUTHENTICATE_VOICE_MFA_FAILURE,"Enroll TOTP exception"+ e.getMessage(),
                    HttpStatusCode.EXPECTATION_FAILED));
            Timber.e("Enroll TOTP exception" + e.getMessage());
        }

    }


    @Override
    public void loginWithTOTP(final PasswordlessEntity passwordlessEntity,
                              final Result<LoginCredentialsResponseEntity> loginresult) {
        try {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {
                    String baseurl = result.get("DomainURL");
                    String clientId = result.get("ClientId");
                    if (passwordlessEntity.getUsageType() != null && passwordlessEntity.getUsageType() != "" &&
                            passwordlessEntity.getRequestId() != null && passwordlessEntity.getRequestId() != "") {

                        if (baseurl == null || baseurl.equals("") && clientId == null || clientId.equals("")) {
                            String errorMessage = "baseurl or clientId  must not be empty";

                            loginresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                    errorMessage, HttpStatusCode.EXPECTATION_FAILED));
                        }

                        if (((passwordlessEntity.getSub() == null || passwordlessEntity.getSub().equals("")) &&
                                (passwordlessEntity.getEmail() == null || passwordlessEntity.getEmail().equals("")) &&
                                (passwordlessEntity.getMobile() == null || passwordlessEntity.getMobile().equals("")))) {
                            String errorMessage = "sub or email or mobile number must not be empty";

                            loginresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                    errorMessage, HttpStatusCode.EXPECTATION_FAILED));
                        }

                        if (passwordlessEntity.getUsageType().equals(UsageType.MFA)) {
                            if (passwordlessEntity.getTrackId() == null || passwordlessEntity.getTrackId() == "") {
                                String errorMessage = "trackId must not be empty";

                                loginresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                        errorMessage, HttpStatusCode.EXPECTATION_FAILED));
                                return;
                            }
                        }

                        InitiateTOTPMFARequestEntity initiateTOTPMFARequestEntity = new InitiateTOTPMFARequestEntity();
                        initiateTOTPMFARequestEntity.setSub(passwordlessEntity.getSub());
                        initiateTOTPMFARequestEntity.setUsageType(passwordlessEntity.getUsageType());
                        initiateTOTPMFARequestEntity.setEmail(passwordlessEntity.getEmail());
                        initiateTOTPMFARequestEntity.setMobile(passwordlessEntity.getMobile());

                        //Todo check for email or sub or mobile


                        TOTPConfigurationController.getShared(context).LoginWithTOTP(baseurl, clientId,
                                passwordlessEntity.getTrackId(), passwordlessEntity.getRequestId(),
                                initiateTOTPMFARequestEntity, loginresult);
                    } else {
                        String errorMessage = "UsageType or requestId must not be empty";

                        loginresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                errorMessage, HttpStatusCode.EXPECTATION_FAILED));
                    }
                }

                @Override
                public void failure(WebAuthError error) {
                    loginresult.failure(WebAuthError.getShared(context).propertyMissingException());
                }
            });
        } catch (Exception e) {
            LogFile.addRecordToLog("Login TOTP exception" + e.getMessage());
            loginresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.AUTHENTICATE_VOICE_MFA_FAILURE,"Login TOTP exception"+ e.getMessage(),
                    HttpStatusCode.EXPECTATION_FAILED));
            Timber.e("Login TOTP exception" + e.getMessage());
        }
    }


    public void scannedTOTP(@NonNull final String statusId, @NonNull final String sub, final Result<ScannedResponseEntity> scannedResult)
    {
        try
        {

            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {

                    String baseurl = result.get("DomainURL");
                    String clientId=result.get("ClientId");


                    TOTPConfigurationController.getShared(context).scannedWithTOTP(baseurl,statusId,clientId,scannedResult);
                }

                @Override
                public void failure(WebAuthError error) {
                    scannedResult.failure(WebAuthError.getShared(context).propertyMissingException());
                }
            });

        }
        catch (Exception e)
        {
            LogFile.addRecordToLog("Scanned TOTP exception" + e.getMessage());
            scannedResult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,"Scanned TOTP exception"+ e.getMessage(),
                    HttpStatusCode.EXPECTATION_FAILED));
            Timber.e("Scanned TOTP exception" + e.getMessage());
        }
    }




    public void listenTOTP(String sub) {
        final String secret = DBHelper.getShared().getSecret(sub);
        final Intent i = new Intent("TOTPListener");
        if (!secret.equals("") && secret != null) {

            countDownTimer = new CountDownTimer(System.currentTimeMillis(), 1000) {
                @Override
                public void onTick(long l) {
                    final TOTPEntity TOTPString = TOTPConfigurationController.getShared(context).generateTOTP(secret);


                    i.putExtra("TOTP", TOTPString);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(i);
                }

                @Override
                public void onFinish() {

                    this.cancel();

                }
            }.start();
        } else {
            //Todo Handle Error Message
            return;

        }

    }


    public void cancelListenTOTP() {

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

    }


    // ******  LOGIN WITH voice *****-------------------------------------------------------------------------------------------------------


    @Override
    public void configureVoiceRecognition(final File voice, @NonNull final String logoURL,final String sub, final Result<EnrollVoiceMFAResponseEntity> enrollresult) {
        try {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {
                    String baseurl = result.get("DomainURL");


                    if (sub != null && !sub.equals("") && baseurl != null && !baseurl.equals("")) {

                      //  String logoUrl = "https://docs.cidaas.de/assets/logoss.png";


                       if(!logoURL.equals("") && logoURL!=null) {
                            logoURLlocal=logoURL;
                        }

                        SetupVoiceMFARequestEntity setupVoiceMFARequestEntity = new SetupVoiceMFARequestEntity();
                        setupVoiceMFARequestEntity.setClient_id(result.get("ClientId"));
                        setupVoiceMFARequestEntity.setLogoUrl(logoURLlocal);


                        VoiceConfigurationController.getShared(context).configureVoice(voice,sub, baseurl, setupVoiceMFARequestEntity, enrollresult);


                    }
                }

                @Override
                public void failure(WebAuthError error) {
                    enrollresult.failure(WebAuthError.getShared(context).propertyMissingException());
                }
            });

        } catch (Exception e) {
            LogFile.addRecordToLog("Configure Voice exception" + e.getMessage());
            enrollresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,"Configure Voice exception"+ e.getMessage(),
                    HttpStatusCode.EXPECTATION_FAILED));
            Timber.e("Configure Voice exception" + e.getMessage());
        }

    }

    public void scannedVoice(@NonNull final String statusId, @NonNull final String sub, final Result<ScannedResponseEntity> scannedResult)
    {
        try
        {

            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {

                    String baseurl = result.get("DomainURL");
                    String clientId=result.get("ClientId");


                    VoiceConfigurationController.getShared(context).scannedWithVoice(baseurl,statusId,clientId,scannedResult);
                }

                @Override
                public void failure(WebAuthError error) {
                    scannedResult.failure(WebAuthError.getShared(context).propertyMissingException());
                }
            });

        }
        catch (Exception e)
        {
            LogFile.addRecordToLog("Scanned Voice exception" + e.getMessage());
            scannedResult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,"Scanned Voice exception"+ e.getMessage(),
                    HttpStatusCode.EXPECTATION_FAILED));
            Timber.e("Scanned Voice exception" + e.getMessage());
        }
    }

    public void enrollVoice(@NonNull final File voice, @NonNull final String sub,@NonNull final String statusId,  final Result<EnrollVoiceMFAResponseEntity> enrollResult)
    {
        try
        {

            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {

                    final String baseurl = result.get("DomainURL");
                    final String clientId= result.get("ClientId");

                    String userDeviceId=DBHelper.getShared().getUserDeviceId(baseurl);

                    final EnrollVoiceMFARequestEntity enrollVoiceMFARequestEntity=new EnrollVoiceMFARequestEntity();
                    enrollVoiceMFARequestEntity.setAudioFile(voice);
                    enrollVoiceMFARequestEntity.setStatusId(statusId);
                    enrollVoiceMFARequestEntity.setUserDeviceId(userDeviceId);
                    enrollVoiceMFARequestEntity.setClient_id(clientId);

                    AccessTokenController.getShared(context).getAccessToken(sub, new Result<AccessTokenEntity>() {
                        @Override
                        public void success(AccessTokenEntity result) {
                            VoiceConfigurationController.getShared(context).enrollVoice(baseurl,result.getAccess_token(),enrollVoiceMFARequestEntity,enrollResult);
                        }

                        @Override
                        public void failure(WebAuthError error) {
                            enrollResult.failure(error);
                        }
                    });



                }

                @Override
                public void failure(WebAuthError error) {
                    enrollResult.failure(WebAuthError.getShared(context).propertyMissingException());
                }
            });
        }
        catch (Exception e)
        {
            LogFile.addRecordToLog("Enroll Voice exception" + e.getMessage());
            enrollResult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,"Enroll Voice exception"+ e.getMessage(),
                    HttpStatusCode.EXPECTATION_FAILED));
            Timber.e("Enroll Voice exception" + e.getMessage());
        }
    }


    @Override
    public void loginWithVoiceRecognition(final File voice, final PasswordlessEntity passwordlessEntity,
                                          final Result<LoginCredentialsResponseEntity> loginresult) {
        try {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {
                    String baseurl = result.get("DomainURL");
                    String clientId = result.get("ClientId");

                    if (passwordlessEntity.getUsageType() != null && !passwordlessEntity.getUsageType().equals("") &&
                            passwordlessEntity.getRequestId() != null && !passwordlessEntity.getRequestId().equals("") && voice != null) {

                        if (baseurl == null || baseurl.equals("") && clientId == null || clientId.equals("")) {
                            String errorMessage = "baseurl or clientId must not be empty";

                            loginresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                    errorMessage, HttpStatusCode.EXPECTATION_FAILED));
                        }

                        if (((passwordlessEntity.getSub() == null || passwordlessEntity.getSub().equals("")) &&
                                (passwordlessEntity.getEmail() == null || passwordlessEntity.getEmail().equals("")) &&
                                (passwordlessEntity.getMobile() == null || passwordlessEntity.getMobile().equals("")))) {
                            String errorMessage = "sub or email or mobile number must not be empty";

                            loginresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                    errorMessage, HttpStatusCode.EXPECTATION_FAILED));
                        }

                        if (passwordlessEntity.getUsageType().equals(UsageType.MFA)) {
                            if (passwordlessEntity.getTrackId() == null || passwordlessEntity.getTrackId().equals("")) {
                                String errorMessage = "trackId must not be empty";


                                loginresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                        errorMessage, HttpStatusCode.EXPECTATION_FAILED));
                                return;
                            }
                        }

                        InitiateVoiceMFARequestEntity initiateVoiceMFARequestEntity = new InitiateVoiceMFARequestEntity();
                        initiateVoiceMFARequestEntity.setSub(passwordlessEntity.getSub());
                        initiateVoiceMFARequestEntity.setUsageType(passwordlessEntity.getUsageType());
                        initiateVoiceMFARequestEntity.setEmail(passwordlessEntity.getEmail());
                        initiateVoiceMFARequestEntity.setMobile(passwordlessEntity.getMobile());

                        //Todo check for email or sub or mobile


                        VoiceConfigurationController.getShared(context).LoginWithVoice(voice, baseurl, clientId,
                                passwordlessEntity.getTrackId(), passwordlessEntity.getRequestId(),
                                initiateVoiceMFARequestEntity, loginresult);
                    } else {
                        String errorMessage = "Image File or RequestId or UsageType must not be empty";

                        loginresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                errorMessage, HttpStatusCode.EXPECTATION_FAILED));
                    }


                }

                @Override
                public void failure(WebAuthError error) {
                    loginresult.failure(WebAuthError.getShared(context).propertyMissingException());
                }
            });
        } catch (Exception e) {
            LogFile.addRecordToLog("Login Voice exception" + e.getMessage());
            loginresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.AUTHENTICATE_VOICE_MFA_FAILURE,"Login Voice exception"+ e.getMessage(),
                    HttpStatusCode.EXPECTATION_FAILED));
            Timber.e("Login Voice exception" + e.getMessage());
        }
    }

    public void verifyVoice(final File voice, final String statusId, final Result<AuthenticateVoiceResponseEntity> result) {
        try {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> lpresult) {
                    String baseurl = lpresult.get("DomainURL");
                    String clientId = lpresult.get("ClientId");
                    //todo call enroll Email


                    AuthenticateVoiceRequestEntity authenticateVoiceRequestEntity=new AuthenticateVoiceRequestEntity();
                    authenticateVoiceRequestEntity.setStatusId(statusId);
                    authenticateVoiceRequestEntity.setVoiceFile(voice);
                    authenticateVoiceRequestEntity.setUserDeviceId(DBHelper.getShared().getUserDeviceId(baseurl));
                    authenticateVoiceRequestEntity.setClient_id(clientId);

                    VoiceConfigurationController.getShared(context).authenticateVoice(baseurl,authenticateVoiceRequestEntity,result);



                }

                @Override
                public void failure(WebAuthError error) {
                    result.failure(WebAuthError.getShared(context).propertyMissingException());
                }
            });
        } catch (Exception e) {
            LogFile.addRecordToLog("Verify Voice exception" + e.getMessage());
            result.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.AUTHENTICATE_VOICE_MFA_FAILURE,"Verify Voice exception"+ e.getMessage(),
                    HttpStatusCode.EXPECTATION_FAILED));
            Timber.e("Verify Voice exception" + e.getMessage());
        }

    }





    //---------------------------------------DELETE CALL-------------------------------------------------------------------------------------------------




    //Delete call
    public void deleteVerificationByType(@NonNull final String verificationType, @NonNull final String sub, final Result<DeleteMFAResponseEntity> deleteResult)
    {
        try
        {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> lpresult) {
                    final String baseurl = lpresult.get("DomainURL");
                    String clientId = lpresult.get("ClientId");
                    String userDeviceId="";

                    String typeOfVerification="";

                    if(verificationType!=null && verificationType!="") {

                         typeOfVerification = verificationType.toUpperCase();
                    }
                    else
                    {
                        deleteResult.failure( WebAuthError.getShared(context).customException(WebAuthErrorCode.MFA_LIST_FAILURE,"Verification Type must not be empty",HttpStatusCode.BAD_REQUEST));

                    }

                    if(DBHelper.getShared().getUserDeviceId(baseurl)!=null && DBHelper.getShared().getUserDeviceId(baseurl)!="" && sub!=null && sub!="")
                    {
                        userDeviceId=DBHelper.getShared().getUserDeviceId(baseurl);

                        final String finalUserDeviceId = userDeviceId;
                        final String finalTypeOfVerification = typeOfVerification;
                        AccessTokenController.getShared(context).getAccessToken(sub, new Result<AccessTokenEntity>() {
                            @Override
                            public void success(AccessTokenEntity result) {
                                //After getting Access Token
                                //Remove Secret from Shared Preference

                                if(verificationType.equalsIgnoreCase("TOTP")) {
                                    DBHelper.getShared().removeSecret(sub);
                                }
                                VerificationSettingsController.getShared(context).deleteMFA(baseurl, result.getAccess_token(),finalUserDeviceId, finalTypeOfVerification,deleteResult);
                            }

                            @Override
                            public void failure(WebAuthError error) {
                                deleteResult.failure(error);
                            }
                        });


                    }
                    else
                    {
                        deleteResult.failure( WebAuthError.getShared(context).customException(WebAuthErrorCode.MFA_LIST_FAILURE,"User deviceID or Sub must not be empty",HttpStatusCode.BAD_REQUEST));
                    }
                }

                @Override
                public void failure(WebAuthError error) {
                    deleteResult.failure(WebAuthError.getShared(context).propertyMissingException());
                }
            });
        }
        catch (Exception e)
        {
            Timber.e("Faliure in delete service call"+e.getMessage());
            deleteResult.failure( WebAuthError.getShared(context).customException(WebAuthErrorCode.MFA_LIST_FAILURE,e.getMessage(),HttpStatusCode.BAD_REQUEST));

        }
    }

    //Delete call
    public void deleteVerificationByDevice( @NonNull final String sub,final Result<DeleteMFAResponseEntity> result)
    {
        try
        {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> lpresult) {
                    final String baseurl = lpresult.get("DomainURL");
                    String clientId = lpresult.get("ClientId");
                    String userDeviceId="";


                    if(DBHelper.getShared().getUserDeviceId(baseurl)!=null && DBHelper.getShared().getUserDeviceId(baseurl)!="" && sub!=null && sub!="")
                    {
                        userDeviceId=DBHelper.getShared().getUserDeviceId(baseurl);

                        final String finalUserDeviceId = userDeviceId;

                            DBHelper.getShared().removeSecret(sub);

                        AccessTokenController.getShared(context).getAccessToken(sub, new Result<AccessTokenEntity>() {
                            @Override
                            public void success(AccessTokenEntity accessTokenresult) {

                                VerificationSettingsController.getShared(context).deleteAllMFA(baseurl,accessTokenresult.getAccess_token(), finalUserDeviceId,result);
                            }

                            @Override
                            public void failure(WebAuthError error) {
                                result.failure(error);
                            }
                        });


                    }
                    else
                    {
                        result.failure( WebAuthError.getShared(context).customException(WebAuthErrorCode.MFA_LIST_FAILURE,"User deviceID or sub must not be empty",HttpStatusCode.BAD_REQUEST));
                    }
                }

                @Override
                public void failure(WebAuthError error) {
                    result.failure(WebAuthError.getShared(context).propertyMissingException());
                }
            });
        }
        catch (Exception e)
        {
            Timber.e("Faliure in delete All service call"+e.getMessage());
            result.failure( WebAuthError.getShared(context).customException(WebAuthErrorCode.MFA_LIST_FAILURE,e.getMessage(),HttpStatusCode.BAD_REQUEST));

        }
    }


    //Deny Call
    public void denyNotification(@NonNull final String sub, @NonNull final String reason, @NonNull final String statusId, final Result<DenyNotificationResponseEntity> result)
    {
        try
        {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> lpresult) {
                    final String baseurl = lpresult.get("DomainURL");
                    String clientId = lpresult.get("ClientId");
                    String userDeviceId="";

                    if(sub!=null && sub!="") {


                        AccessTokenController.getShared(context).getAccessToken(sub, new Result<AccessTokenEntity>() {
                            @Override
                            public void success(AccessTokenEntity accessTokenresult) {

                                if (reason != "" && reason != null && statusId != null && statusId != "") {

                                    DenyNotificationRequestEntity denyNotificationRequestEntity = new DenyNotificationRequestEntity();
                                    denyNotificationRequestEntity.setReject_reason(reason);
                                    denyNotificationRequestEntity.setStatusId(statusId);


                                    VerificationSettingsController.getShared(context).denyNotification(baseurl, accessTokenresult.getAccess_token(), denyNotificationRequestEntity, result);
                                } else {
                                    result.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.MFA_LIST_FAILURE,
                                            "Verification Type must not be empty", HttpStatusCode.BAD_REQUEST));
                                }
                            }

                            @Override
                            public void failure(WebAuthError error) {
                                result.failure(error);
                            }
                        });
                    }
                    else {
                        result.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PENDING_NOTIFICATION_FAILURE,
                                "Sub must not be empty", HttpStatusCode.BAD_REQUEST));
                    }

                }

                @Override
                public void failure(WebAuthError error) {
                    result.failure(error);
                }
            });
        }
        catch (Exception e)
        {

            Timber.e("Faliure in delete All service call"+e.getMessage());
            result.failure( WebAuthError.getShared(context).customException(WebAuthErrorCode.MFA_LIST_FAILURE,e.getMessage(),HttpStatusCode.BAD_REQUEST));

        }
    }


    //Get Pending Notification
    public void getPendingNotificationList(@NonNull final String sub,  final Result<NotificationEntity> result)
    {
        try
        {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> lpresult) {
                    final String baseurl = lpresult.get("DomainURL");
                    String clientId = lpresult.get("ClientId");


                    if(sub!=null && sub!="") {

                        AccessTokenController.getShared(context).getAccessToken(sub, new Result<AccessTokenEntity>() {
                            @Override
                            public void success(AccessTokenEntity accessTokenresult) {

                                if(DBHelper.getShared().getUserDeviceId(baseurl)!=null && DBHelper.getShared().getUserDeviceId(baseurl)!="") {
                                    String userDeviceId = DBHelper.getShared().getUserDeviceId(baseurl);


                                    if (userDeviceId != "" && userDeviceId != null) {


                                        VerificationSettingsController.getShared(context).getPendingNotification(baseurl, accessTokenresult.getAccess_token(), userDeviceId, result);
                                    } else {
                                        result.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.MFA_LIST_FAILURE,
                                                "User Device Id must not be empty", HttpStatusCode.BAD_REQUEST));
                                    }
                                }
                                else
                                {
                                    result.failure( WebAuthError.getShared(context).customException(WebAuthErrorCode.MFA_LIST_FAILURE,"User deviceID must not be empty",HttpStatusCode.BAD_REQUEST));
                                }
                            }

                            @Override
                            public void failure(WebAuthError error) {
                                result.failure(error);
                            }
                        });
                    }
                    else {
                        result.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PENDING_NOTIFICATION_FAILURE,
                                "Sub must not be empty", HttpStatusCode.BAD_REQUEST));
                    }

                }

                @Override
                public void failure(WebAuthError error) {
                    result.failure(error);
                }
            });
        }
        catch (Exception e)
        {

            Timber.e("Faliure in delete All service call"+e.getMessage());
            result.failure( WebAuthError.getShared(context).customException(WebAuthErrorCode.MFA_LIST_FAILURE,e.getMessage(),HttpStatusCode.BAD_REQUEST));

        }
    }


    //Get user List Notification
    public void getConfigurationList(@NonNull final String sub,  final Result<ConfiguredMFAListEntity> result)
    {
        try
        {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> lpresult) {
                    final String baseurl = lpresult.get("DomainURL");

                    if(sub!=null && sub!="") {

                        if(DBHelper.getShared().getUserDeviceId(baseurl)!=null && DBHelper.getShared().getUserDeviceId(baseurl)!="") {

                            String userDeviceId = DBHelper.getShared().getUserDeviceId(baseurl);
                            VerificationSettingsController.getShared(context).getConfiguredMFAList(baseurl, sub, userDeviceId, result);
                         }
                         else
                         {
                            result.failure( WebAuthError.getShared(context).customException(WebAuthErrorCode.CONFIGURED_LIST_MFA_FAILURE,"User deviceID must not be empty",HttpStatusCode.BAD_REQUEST));
                         }

                       }
                       else {
                          result.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.CONFIGURED_LIST_MFA_FAILURE,
                            "Sub must not be empty", HttpStatusCode.BAD_REQUEST));
                       }
                }

                @Override
                public void failure(WebAuthError error) {
                    result.failure(error);
                }
            });
        }
        catch (Exception e)
        {

            Timber.e("Faliure in getConfiguration List  service call"+e.getMessage());
            result.failure( WebAuthError.getShared(context).customException(WebAuthErrorCode.CONFIGURED_LIST_MFA_FAILURE,e.getMessage(),HttpStatusCode.BAD_REQUEST));

        }
    }





    //-----------------Scan the ID card----------------------------------------------------------------------------------


 /*   public void startDocumentScanner(Activity activity) {
        Intent scanIntent = new Intent(context, CardIOActivity.class);
        scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_SCAN, true); // supmit cuando termine de reconocer el documento
        scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, true); // esconder teclado
        scanIntent.putExtra(CardIOActivity.EXTRA_HIDE_CARDIO_LOGO, true); // cambiar logo de paypal por el de card.io
        scanIntent.putExtra(CardIOActivity.EXTRA_RETURN_CARD_IMAGE, true); // capture img
        scanIntent.putExtra(CardIOActivity.EXTRA_CAPTURED_CARD_IMAGE, true); // capturar img

        // laszar activity
        activity.startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);

    }*/



/*

    public void onActivityResult(int requestCode, int resultCode, Intent data, Result<File> result) {

        try {
            if (requestCode == MY_SCAN_REQUEST_CODE || requestCode == RESULT_SCAN_SUPPRESSED) {

                Bitmap card = CardIOActivity.getCapturedCardImage(data);


                if (card != null) {

                    File imagefile = DocumentScannnerController.getShared(context).convertImageJpeg(card);

                    // Intent intent=new Intent(this, IdCardScannerActivity.class);
                    //intent.putExtra("image",byteArray);
                    //startActivity(intent);


                    //return imagefile;

                    result.success(imagefile);
                } else {
                    result.failure(new WebAuthError(context).customException(401, " no document found", 417));
                }

            }
        } catch (Exception e) {
            result.failure(new WebAuthError(context).customException(401, "Bad document or no document", 417));
        }
    }
*/

    // ****** LOGIN WITH Document *****-------------------------------------------------------------------------------------------------------

    public void VerifyDocument(final File photo, final String sub, final Result<DocumentScannerServiceResultEntity> resultEntityResult) {
        try {

            if (photo != null && !sub.equals("") && sub!=null) {

                checkSavedProperties(new Result<Dictionary<String, String>>() {
                    @Override
                    public void success(Dictionary<String, String> result) {
                        final String baseurl = result.get("DomainURL");

                        if (baseurl != null && !baseurl.equals("")) {

                            getAccessToken(sub, new Result<AccessTokenEntity>() {
                                @Override
                                public void success(AccessTokenEntity result) {
                                    DocumentScannnerController.getShared(context).sendtoServicecall(baseurl, photo, result.getAccess_token(),resultEntityResult);
                                }

                                @Override
                                public void failure(WebAuthError error) {
                                    resultEntityResult.failure(error);
                                }
                            });



                        } else {
                            resultEntityResult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.DOCUMENT_VERIFICATION_FAILURE, "BaseURL must not be null", 417));
                        }
                    }

                    @Override
                    public void failure(WebAuthError error) {

                        resultEntityResult.failure(error);
                    }
                });
            } else {
                resultEntityResult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.DOCUMENT_VERIFICATION_FAILURE, "Photo or sub must not be null", 417));
            }
        } catch (Exception e) {
            resultEntityResult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.DOCUMENT_VERIFICATION_FAILURE, "Unexpected Error :" + e.getMessage(), 417));
        }
    }


    // ****** GET REGISTERATION *****-------------------------------------------------------------------------------------------------------


    public void getRegistrationFields( final String locale, final Result<RegistrationSetupResponseEntity> registerFieldsresult,
                                       final HashMap<String,String>... extraParams)
    {
        try
        {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> loginProperties) {
                    getRequestId(loginProperties, new Result<AuthRequestResponseEntity>() {
                        @Override
                        public void success(AuthRequestResponseEntity result) {
                            getRegistrationFields(result.getData().getRequestId(),locale,registerFieldsresult);
                        }

                        @Override
                        public void failure(WebAuthError error) {
                            registerFieldsresult.failure(error);
                        }
                    },extraParams);
                }

                @Override
                public void failure(WebAuthError error) {
                    registerFieldsresult.failure(error);
                }
            });
        }
        catch (Exception e)
        {

        }
    }

    @Override
    public void getRegistrationFields(@NonNull final String requestId, final String locale,
                                      final Result<RegistrationSetupResponseEntity> registerFieldsresult) {
        try {

            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {
                    String baseurl = result.get("DomainURL");
                    String clientId = result.get("ClientId");
                    String language;

                    if (!requestId.equals("")) {

                        final RegistrationSetupRequestEntity registrationSetupRequestEntity;

                        registrationSetupRequestEntity = new RegistrationSetupRequestEntity();
                        registrationSetupRequestEntity.setRequestId(requestId);

                        if (locale == null || locale == "") {
                            language = Locale.getDefault().getLanguage();
                            registrationSetupRequestEntity.setAcceptedLanguage(language);

                        } else {
                            language = locale;
                            registrationSetupRequestEntity.setAcceptedLanguage(language);
                        }

                        RegistrationController.getShared(context).getRegisterationFields(baseurl, registrationSetupRequestEntity,
                                new Result<RegistrationSetupResponseEntity>() {
                                    @Override
                                    public void success(RegistrationSetupResponseEntity result) {
                                        registerFields = result.getData();
                                        registerFieldsresult.success(result);
                                    }

                                    @Override
                                    public void failure(WebAuthError error) {
                                        registerFieldsresult.failure(error);
                                    }
                                });

                    } else {
                        String errorMessage = "RequestId must not be empty";

                        registerFieldsresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                errorMessage, HttpStatusCode.EXPECTATION_FAILED));
                    }

                }

                @Override
                public void failure(WebAuthError error) {
                    registerFieldsresult.failure(WebAuthError.getShared(context).propertyMissingException());
                }
            });
        } catch (Exception e) {
            String errorMessage = "Custom Exception" + e.getMessage();

            registerFieldsresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                    errorMessage, HttpStatusCode.EXPECTATION_FAILED));
        }
    }

    public void registerUser( final RegistrationEntity registrationEntity, final Result<RegisterNewUserResponseEntity> registerFieldsresult,
                                       final HashMap<String,String>... extraParams)
    {
        try
        {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> loginProperties) {
                    getRequestId(loginProperties, new Result<AuthRequestResponseEntity>() {
                        @Override
                        public void success(AuthRequestResponseEntity result) {
                            registerUser(result.getData().getRequestId(),registrationEntity,registerFieldsresult);
                        }

                        @Override
                        public void failure(WebAuthError error) {
                            registerFieldsresult.failure(error);
                        }
                    },extraParams);
                }

                @Override
                public void failure(WebAuthError error) {
                    registerFieldsresult.failure(error);
                }
            });
        }
        catch (Exception e)
        {

        }
    }

    @Override
    public void registerUser(@NonNull final String requestId, final RegistrationEntity registrationEntity,
                             final Result<RegisterNewUserResponseEntity> registerFieldsresult) {
        try {

            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {
                    String baseurl = result.get("DomainURL");
                    String clientId = result.get("ClientId");
                    String language;

                    if (!requestId.equals("")) {
                        if (registerFields != null)

                        {
                            if (registerFields.length > 0) {
                                for (RegistrationSetupResultDataEntity dataEntity : registerFields) {

                                    if (dataEntity.getFieldKey().equals("email")) {
                                        if (dataEntity.isRequired() && registrationEntity.getEmail().equals("")) {
                                            String errorMessage = "Email must not be empty";
                                            registerFieldsresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                                    errorMessage, HttpStatusCode.EXPECTATION_FAILED));


                                        }

                                    }

                                    if (dataEntity.getFieldKey().equals("given_name")) {
                                        if (dataEntity.isRequired() && registrationEntity.getGiven_name().equals("")) {
                                            String errorMessage = "given_name must not be empty";
                                            registerFieldsresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                                    errorMessage, HttpStatusCode.EXPECTATION_FAILED));
                                        }

                                    }

                                    if (dataEntity.getFieldKey().equals("family_name")) {
                                        if (dataEntity.isRequired() && registrationEntity.getFamily_name().equals("")) {
                                            String errorMessage = "family_name must not be empty";
                                            registerFieldsresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                                    errorMessage, HttpStatusCode.EXPECTATION_FAILED));

                                        }

                                    }

                                    if (dataEntity.getFieldKey().equals("mobile_number")) {
                                        if (dataEntity.isRequired() && registrationEntity.getMobile_number().equals("")) {
                                            String errorMessage = "mobile_number must not be empty";
                                            registerFieldsresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                                    errorMessage, HttpStatusCode.EXPECTATION_FAILED));

                                        }

                                    }

                                    if (dataEntity.getFieldKey().equals("password")) {
                                        if (dataEntity.isRequired() && registrationEntity.getPassword().equals("")) {
                                            String errorMessage = "password must not be empty";
                                            registerFieldsresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                                    errorMessage, HttpStatusCode.EXPECTATION_FAILED));


                                        }

                                    }

                                    if (dataEntity.getFieldKey().equals("password_echo")) {
                                        if (dataEntity.isRequired() && registrationEntity.getGiven_name().equals("")) {
                                            String errorMessage = "password_echo must not be empty";
                                            registerFieldsresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                                    errorMessage, HttpStatusCode.EXPECTATION_FAILED));

                                        }
                                        if (registrationEntity.getPassword().equals(registrationEntity.getPassword_echo())) {

                                            String errorMessage = "Password and password_echo must be same";
                                            registerFieldsresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                                    errorMessage, HttpStatusCode.EXPECTATION_FAILED));

                                        }

                                    }

                                    if (dataEntity.getFieldKey().equals("username")) {
                                        if (dataEntity.isRequired() && registrationEntity.getUsername().equals("")) {
                                            String errorMessage = "username must not be empty";
                                            registerFieldsresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                                    errorMessage, HttpStatusCode.EXPECTATION_FAILED));

                                        }

                                    }

                                    if (dataEntity.getFieldKey().equals("birthdate")) {
                                        if (dataEntity.isRequired() && registrationEntity.getBirthdate().equals("")) {
                                            String errorMessage = "birthdate must not be empty";
                                            registerFieldsresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                                    errorMessage, HttpStatusCode.EXPECTATION_FAILED));

                                        }

                                    }

                                    if (registrationEntity.getProvider() != null && registrationEntity.getProvider() != "") {

                                        String errorMessage = "Provider must not be empty";
                                        registerFieldsresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                                errorMessage, HttpStatusCode.EXPECTATION_FAILED));


                                    }

                                    for (int i = 0; i < registrationEntity.getCustomFields().size(); i++) {
                                        if (registrationEntity.getCustomFields().keys().hasMoreElements()) {

                                            // registrationEntity.getCustomFields().get()
                                        }
                                    }


                                }
                            } else {

                            }
                        } else {

                        }

                        final RegisterNewUserRequestEntity registerNewUserRequestEntity;

                        registerNewUserRequestEntity = new RegisterNewUserRequestEntity();
                        registerNewUserRequestEntity.setRequestId(requestId);
                        registrationEntity.setProvider("self");
                        registerNewUserRequestEntity.setRegistrationEntity(registrationEntity);


                        RegistrationController.getShared(context).registerNewUser(baseurl, registerNewUserRequestEntity,
                                new Result<RegisterNewUserResponseEntity>() {
                                    @Override
                                    public void success(RegisterNewUserResponseEntity result) {
                                        registerFieldsresult.success(result);
                                    }

                                    @Override
                                    public void failure(WebAuthError error) {
                                        registerFieldsresult.failure(error);
                                    }
                                });

                    } else {
                        String errorMessage = "RequestId must not be empty";

                        registerFieldsresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                errorMessage, HttpStatusCode.EXPECTATION_FAILED));
                    }

                }

                @Override
                public void failure(WebAuthError error) {
                    registerFieldsresult.failure(WebAuthError.getShared(context).propertyMissingException());
                }
            });
        } catch (Exception e) {
            String errorMessage = "Custom Exception" + e.getMessage();

            registerFieldsresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                    errorMessage, HttpStatusCode.EXPECTATION_FAILED));
        }
    }

    public void initiateEmailVerification( @NonNull final String sub,  final Result<RegisterUserAccountInitiateResponseEntity> Result,
                                       final HashMap<String,String>... extraParams)
    {
        try
        {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> loginProperties) {
                    getRequestId(loginProperties, new Result<AuthRequestResponseEntity>() {
                        @Override
                        public void success(AuthRequestResponseEntity result) {
                            initiateEmailVerification(sub,result.getData().getRequestId(),Result);
                        }

                        @Override
                        public void failure(WebAuthError error) {
                            Result.failure(error);
                        }
                    },extraParams);
                }

                @Override
                public void failure(WebAuthError error) {
                    Result.failure(error);
                }
            });
        }
        catch (Exception e)
        {

        }
    }


    @Override
    public void initiateEmailVerification(@NonNull final String sub, @NonNull final String requestId,
                                          final Result<RegisterUserAccountInitiateResponseEntity> Result) {
        try {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {
                    String baseurl = result.get("DomainURL");
                    String clientId = result.get("ClientId");

                    RegisterUserAccountInitiateRequestEntity registerUserAccountInitiateRequestEntity = new RegisterUserAccountInitiateRequestEntity();
                    registerUserAccountInitiateRequestEntity.setProcessingType("CODE");

                 /*   if(verificationMedium.equals("") || verificationMedium.equals(null))
                    {
                        registerUserAccountInitiateRequestEntity.setVerificationMedium("email");
                    }
                    else
                    {
                        registerUserAccountInitiateRequestEntity.setVerificationMedium(verificationMedium);
                    }*/
                    registerUserAccountInitiateRequestEntity.setVerificationMedium("email");

                    registerUserAccountInitiateRequestEntity.setSub(sub);
                    registerUserAccountInitiateRequestEntity.setRequestId(requestId);

                    RegistrationController.getShared(context).initiateAccountVerificationService(baseurl, registerUserAccountInitiateRequestEntity, Result);

                }

                @Override
                public void failure(WebAuthError error) {
                    Result.failure(WebAuthError.getShared(context).propertyMissingException());
                }
            });
        } catch (Exception e) {
            Result.failure(WebAuthError.getShared(context).propertyMissingException());
        }

    }

    public void initiateSMSVerification( @NonNull final String sub,  final Result<RegisterUserAccountInitiateResponseEntity> Result,
                                           final HashMap<String,String>... extraParams)
    {
        try
        {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> loginProperties) {
                    getRequestId(loginProperties, new Result<AuthRequestResponseEntity>() {
                        @Override
                        public void success(AuthRequestResponseEntity result) {
                            initiateSMSVerification(sub,result.getData().getRequestId(),Result);
                        }

                        @Override
                        public void failure(WebAuthError error) {
                            Result.failure(error);
                        }
                    },extraParams);
                }

                @Override
                public void failure(WebAuthError error) {
                    Result.failure(error);
                }
            });
        }
        catch (Exception e)
        {

        }
    }


    @Override
    public void initiateSMSVerification(@NonNull final String sub,@NonNull final String requestId,
                                        final Result<RegisterUserAccountInitiateResponseEntity> Result) {
        try {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {
                    String baseurl = result.get("DomainURL");
                    String clientId = result.get("ClientId");

                    RegisterUserAccountInitiateRequestEntity registerUserAccountInitiateRequestEntity = new RegisterUserAccountInitiateRequestEntity();
                    registerUserAccountInitiateRequestEntity.setProcessingType("CODE");
                    registerUserAccountInitiateRequestEntity.setVerificationMedium("sms");
                    registerUserAccountInitiateRequestEntity.setSub(sub);
                    registerUserAccountInitiateRequestEntity.setRequestId(requestId);
                 /*   if(verificationMedium.equals("") || verificationMedium.equals(null))
                    {
                        registerUserAccountInitiateRequestEntity.setVerificationMedium("email");
                    }
                    else
                    {
                        registerUserAccountInitiateRequestEntity.setVerificationMedium(verificationMedium);
                    }*/


                    RegistrationController.getShared(context).initiateAccountVerificationService(baseurl, registerUserAccountInitiateRequestEntity, Result);


                }

                @Override
                public void failure(WebAuthError error) {
                    Result.failure(WebAuthError.getShared(context).propertyMissingException());
                }
            });
        } catch (Exception e) {
            Result.failure(WebAuthError.getShared(context).propertyMissingException());
        }

    }
    public void initiateIVRVerification( @NonNull final String sub,  final Result<RegisterUserAccountInitiateResponseEntity> Result,
                                           final HashMap<String,String>... extraParams)
    {
        try
        {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> loginProperties) {
                    getRequestId(loginProperties, new Result<AuthRequestResponseEntity>() {
                        @Override
                        public void success(AuthRequestResponseEntity result) {
                            initiateIVRVerification(sub,result.getData().getRequestId(),Result);
                        }

                        @Override
                        public void failure(WebAuthError error) {
                            Result.failure(error);
                        }
                    },extraParams);
                }

                @Override
                public void failure(WebAuthError error) {
                    Result.failure(error);
                }
            });
        }
        catch (Exception e)
        {

        }
    }


    @Override
    public void initiateIVRVerification(@NonNull final String sub, @NonNull final String requestId,
                                        final Result<RegisterUserAccountInitiateResponseEntity> Result) {
        try {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {
                    String baseurl = result.get("DomainURL");
                    String clientId = result.get("ClientId");

                    RegisterUserAccountInitiateRequestEntity registerUserAccountInitiateRequestEntity = new RegisterUserAccountInitiateRequestEntity();
                    registerUserAccountInitiateRequestEntity.setProcessingType("CODE");
                    registerUserAccountInitiateRequestEntity.setVerificationMedium("ivr");
                    registerUserAccountInitiateRequestEntity.setSub(sub);
                    registerUserAccountInitiateRequestEntity.setRequestId(requestId);

                 /*   if(verificationMedium.equals("") || verificationMedium.equals(null))
                    {
                        registerUserAccountInitiateRequestEntity.setVerificationMedium("email");
                    }
                    else
                    {
                        registerUserAccountInitiateRequestEntity.setVerificationMedium(verificationMedium);
                    }*/


                    RegistrationController.getShared(context).initiateAccountVerificationService(baseurl, registerUserAccountInitiateRequestEntity, Result);


                }

                @Override
                public void failure(WebAuthError error) {
                    Result.failure(WebAuthError.getShared(context).propertyMissingException());
                }
            });
        } catch (Exception e) {
            Result.failure(WebAuthError.getShared(context).propertyMissingException());
        }

    }


    @Override
    public void verifyAccount(@NonNull final String code, @NonNull final String accvid, final Result<RegisterUserAccountVerifyResponseEntity> result) {

        try {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> lpresult) {
                    String baseurl = lpresult.get("DomainURL");
                    String clientId = lpresult.get("ClientId");

                    if (code != null && !code.equals("") && accvid != null && accvid != "") {
                        RegistrationController.getShared(context).verifyAccountVerificationService(baseurl, code, accvid, result);
                    } else {
                        result.failure(WebAuthError.getShared(context).customException(417,"Verification Code or accvid must not be empty",417));
                    }

                }

                @Override
                public void failure(WebAuthError error) {
                    result.failure(WebAuthError.getShared(context).propertyMissingException());
                }
            });
        } catch (Exception e) {
            result.failure(WebAuthError.getShared(context).propertyMissingException());
        }
    }


    //----------------------------------DEDEUPLICATION------------------------------------------------------------------------------------------------------

    @Override
    public void getDeduplicationDetails(@NonNull final String trackId, final Result<DeduplicationResponseEntity> deduplicaionResult) {
        try {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {
                    String baseurl = result.get("DomainURL");
                    String clientId = result.get("ClientId");
                    if (trackId != null && !trackId.equals("")) {
                        DeduplicationController.getShared(context).getDeduplicationList(baseurl, trackId, deduplicaionResult);
                    } else {

                    }
                }

                @Override
                public void failure(WebAuthError error) {
                    deduplicaionResult.failure(WebAuthError.getShared(context).propertyMissingException());
                }
            });
        } catch (Exception e) {
            deduplicaionResult.failure(WebAuthError.getShared(context).propertyMissingException());
        }
    }

    @Override
    public void registerUser(@NonNull final String trackId, final Result<RegisterDeduplicationEntity> deduplicaionResult) {
        try {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {
                    String baseurl = result.get("DomainURL");
                    String clientId = result.get("ClientId");
                    if (trackId != null && trackId != "") {
                        DeduplicationController.getShared(context).registerDeduplication(baseurl, trackId, deduplicaionResult);
                    } else {
                        String errorMessage = "TrackId Must not be null";
                        deduplicaionResult.failure(WebAuthError.getShared(context).customException(417, errorMessage, HttpStatusCode.EXPECTATION_FAILED));

                    }
                }

                @Override
                public void failure(WebAuthError error) {
                    deduplicaionResult.failure(WebAuthError.getShared(context).propertyMissingException());
                }
            });
        } catch (Exception e) {
            deduplicaionResult.failure(WebAuthError.getShared(context).propertyMissingException());
        }
    }


    public void loginWithDeduplication(@NonNull final String sub, @NonNull final String password,
                                     final Result<LoginCredentialsResponseEntity> loginresult,final HashMap<String,String>... extraParams)
    {
        try
        {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> loginProperties) {
                    getRequestId(loginProperties, new Result<AuthRequestResponseEntity>() {
                        @Override
                        public void success(AuthRequestResponseEntity result) {
                            loginWithDeduplication(result.getData().getRequestId(),sub,password,loginresult);
                        }

                        @Override
                        public void failure(WebAuthError error) {
                            loginresult.failure(error);
                        }
                    },extraParams);
                }

                @Override
                public void failure(WebAuthError error) {
                    loginresult.failure(error);
                }
            });
        }
        catch (Exception e)
        {

        }
    }


    @Override
    public void loginWithDeduplication(final String requestId, @NonNull final String sub, @NonNull final String password,
                                       final Result<LoginCredentialsResponseEntity> deduplicaionResult) {
        try {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {
                    String baseurl = result.get("DomainURL");
                    String clientId = result.get("ClientId");
                    if (sub != null && sub != "" && password != null && password != "") {
                        DeduplicationController.getShared(context).loginDeduplication(baseurl, requestId, sub, password, deduplicaionResult);
                    } else {
                        String errorMessage = "Sub or requestId or Password Must not be null";
                        deduplicaionResult.failure(WebAuthError.getShared(context).customException(417, errorMessage, HttpStatusCode.EXPECTATION_FAILED));
                    }
                }

                @Override
                public void failure(WebAuthError error) {
                    deduplicaionResult.failure(WebAuthError.getShared(context).propertyMissingException());
                }
            });
        } catch (Exception e) {
            deduplicaionResult.failure(WebAuthError.getShared(context).propertyMissingException());
        }
    }


    //----------------------------------------------------------------------------------------------------------------------------------------

    public void initiateResetPasswordByEmail(final String email,
                                             final Result<ResetPasswordResponseEntity> resetPasswordResponseEntityResult,final HashMap<String,String>... extraParams)
    {
        try
        {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> loginProperties) {
                    getRequestId(loginProperties, new Result<AuthRequestResponseEntity>() {
                        @Override
                        public void success(AuthRequestResponseEntity result) {
                            initiateResetPasswordByEmail(result.getData().getRequestId(),email,resetPasswordResponseEntityResult);
                        }

                        @Override
                        public void failure(WebAuthError error) {
                            resetPasswordResponseEntityResult.failure(error);
                        }
                    },extraParams);
                }

                @Override
                public void failure(WebAuthError error) {
                    resetPasswordResponseEntityResult.failure(error);
                }
            });
        }
        catch (Exception e)
        {

        }
    }

    // Todo change
    @Override
    public void initiateResetPasswordByEmail(final String requestId, final String email,
                                             final Result<ResetPasswordResponseEntity> resetPasswordResponseEntityResult) {
        try {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {
                    String baseurl = result.get("DomainURL");
                    String clientId = result.get("ClientId");

                    if (email != null && !email.equals("") && requestId != null && !requestId.equals("")) {
                        ResetPasswordRequestEntity resetPasswordRequestEntity = new ResetPasswordRequestEntity();
                        resetPasswordRequestEntity.setProcessingType("CODE");
                        resetPasswordRequestEntity.setRequestId(requestId);
                        resetPasswordRequestEntity.setEmail(email);
                        resetPasswordRequestEntity.setResetMedium("email");

                        ResetPasswordController.getShared(context).initiateresetPasswordService(baseurl, resetPasswordRequestEntity, resetPasswordResponseEntityResult);
                    } else {
                        String ErrorMessage = "RequestID or email mustnot be null";
                        resetPasswordResponseEntityResult.failure(WebAuthError.getShared(context).customException(417, ErrorMessage, HttpStatusCode.EXPECTATION_FAILED));
                    }
                }


                @Override
                public void failure(WebAuthError error) {
                    resetPasswordResponseEntityResult.failure(WebAuthError.getShared(context).propertyMissingException());
                }
            });
        } catch (Exception e) {
            resetPasswordResponseEntityResult.failure(WebAuthError.getShared(context).propertyMissingException());
        }

    }
    public void initiateResetPasswordBySMS(final String mobileNumber,
                                             final Result<ResetPasswordResponseEntity> resetPasswordResponseEntityResult,final HashMap<String,String>... extraParams)
    {
        try
        {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> loginProperties) {
                    getRequestId(loginProperties, new Result<AuthRequestResponseEntity>() {
                        @Override
                        public void success(AuthRequestResponseEntity result) {
                            initiateResetPasswordBySMS(result.getData().getRequestId(),mobileNumber,resetPasswordResponseEntityResult);
                        }

                        @Override
                        public void failure(WebAuthError error) {
                            resetPasswordResponseEntityResult.failure(error);
                        }
                    },extraParams);
                }

                @Override
                public void failure(WebAuthError error) {
                    resetPasswordResponseEntityResult.failure(error);
                }
            });
        }
        catch (Exception e)
        {

        }
    }

    @Override
    public void initiateResetPasswordBySMS(final String requestId, final String mobileNumber,
                                           final Result<ResetPasswordResponseEntity> resetPasswordResponseEntityResult) {
        try {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {
                    String baseurl = result.get("DomainURL");
                    String clientId = result.get("ClientId");

                    if (mobileNumber != null && !mobileNumber.equals("") && requestId != null && !requestId.equals("")) {
                        ResetPasswordRequestEntity resetPasswordRequestEntity = new ResetPasswordRequestEntity();
                        resetPasswordRequestEntity.setProcessingType("CODE");
                        resetPasswordRequestEntity.setRequestId(requestId);
                        resetPasswordRequestEntity.setPhoneNumber(mobileNumber);
                        resetPasswordRequestEntity.setResetMedium("sms");

                        ResetPasswordController.getShared(context).initiateresetPasswordService(baseurl, resetPasswordRequestEntity, resetPasswordResponseEntityResult);
                    } else {
                        String ErrorMessage = "RequestID or Mobile Number mustnot be null";
                        resetPasswordResponseEntityResult.failure(WebAuthError.getShared(context).customException(417, ErrorMessage, HttpStatusCode.EXPECTATION_FAILED));
                    }
                }


                @Override
                public void failure(WebAuthError error) {
                    resetPasswordResponseEntityResult.failure(WebAuthError.getShared(context).propertyMissingException());
                }
            });
        } catch (Exception e) {
            resetPasswordResponseEntityResult.failure(WebAuthError.getShared(context).propertyMissingException());
        }

    }

    @Override
    public void handleResetPassword(@NonNull final String verificationCode, final String rprq,
                                    final Result<ResetPasswordValidateCodeResponseEntity> resetpasswordResult) {
        try {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {
                    String baseurl = result.get("DomainURL");
                    String clientId = result.get("ClientId");

                    if (verificationCode != null && !verificationCode.equals("") && rprq != null && !rprq.equals("")) {

                        ResetPasswordController.getShared(context).resetPasswordValidateCode(baseurl, verificationCode, rprq, resetpasswordResult);

                    } else {
                        resetpasswordResult.failure(WebAuthError.getShared(context).propertyMissingException());
                    }

                }

                @Override
                public void failure(WebAuthError error) {
                    resetpasswordResult.failure(WebAuthError.getShared(context).propertyMissingException());
                }
            });
        } catch (Exception e) {
            resetpasswordResult.failure(WebAuthError.getShared(context).propertyMissingException());
        }

    }

    //Todo Change to entity
    @Override
    public void resetPassword(@NonNull final ResetPasswordEntity resetPasswordEntity,
                              final Result<ResetNewPasswordResponseEntity> resetpasswordResult) {
        try {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {
                    String baseurl = result.get("DomainURL");
                    String clientId = result.get("ClientId");

                    if (resetPasswordEntity.getPassword() != null && !resetPasswordEntity.getPassword().equals("") &&
                            resetPasswordEntity.getConfirmPassword() != null && !resetPasswordEntity.getConfirmPassword().equals("")) {
                        if (resetPasswordEntity.getPassword().equals(resetPasswordEntity.getConfirmPassword())) {
                            if (resetPasswordEntity.getResetRequestId() != null && !resetPasswordEntity.getResetRequestId().equals("") &&
                                    resetPasswordEntity.getExchangeId() != null && !resetPasswordEntity.getExchangeId().equals("")) {
                                ResetPasswordController.getShared(context).
                                        resetNewPassword(baseurl, resetPasswordEntity, resetpasswordResult);
                            } else {
                                String errorMessage = "resetRequestId and ExchangeId must not be null";

                                resetpasswordResult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                        errorMessage, HttpStatusCode.EXPECTATION_FAILED));
                            }
                        } else {
                            String errorMessage = "Password and confirmPassword must  be same";

                            resetpasswordResult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                    errorMessage, HttpStatusCode.EXPECTATION_FAILED));
                        }

                    } else {
                        String errorMessage = "Password or confirmPassword must not be empty";

                        resetpasswordResult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                errorMessage, HttpStatusCode.EXPECTATION_FAILED));
                    }
                }

                @Override
                public void failure(WebAuthError error) {
                    resetpasswordResult.failure(WebAuthError.getShared(context).propertyMissingException());
                }
            });
        } catch (Exception e) {
            String errorMessage = e.getMessage();

            resetpasswordResult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                    errorMessage, HttpStatusCode.EXPECTATION_FAILED));


        }
    }




    //----------------------------------------------------------------------------------------------------------------------------------------

    //Todo change to Sub and Identity id
    public void changePassword(String sub, final ChangePasswordRequestEntity changePasswordRequestEntity, final Result<ChangePasswordResponseEntity> result) {
        try {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> lpresult) {
                    String baseurl = lpresult.get("DomainURL");
                    String clientId = lpresult.get("ClientId");

                    ChangePasswordController.getShared(context).changePassword(baseurl, changePasswordRequestEntity, result);
                }

                @Override
                public void failure(WebAuthError error) {
                    result.failure(WebAuthError.getShared(context).propertyMissingException());
                }
            });
        } catch (Exception e) {
            result.failure(WebAuthError.getShared(context).propertyMissingException());
        }
    }


    @Override
    public void getAccessToken(String sub, Result<AccessTokenEntity> result) {
        try {
            AccessTokenController.getShared(context).getAccessToken(sub, result);
        } catch (Exception e) {
            String errorMessage = "Access Token Exception" + e.getMessage();
            result.failure(WebAuthError.getShared(context).customException(417, errorMessage, 417));
        }
    }





    public void getAccessTokenBySocial(final String token, final String provider, String DomainUrl, final String viewType,final Result<AccessTokenEntity> accessTokenCallback, final HashMap<String,String>... extraParams)
    {
        try
        {
            DomainURL=DomainUrl;
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(final Dictionary<String, String> loginProperties) {

                    getRequestId(loginProperties,new Result<AuthRequestResponseEntity>() {
                        @Override
                        public void success(AuthRequestResponseEntity result) {
                            AccessTokenController.getShared(context).getAccessTokenBySocial(token,provider,"token",result.getData().getRequestId(),viewType,loginProperties,accessTokenCallback);
                        }

                        @Override
                        public void failure(WebAuthError error) {
                         accessTokenCallback.failure(error);
                        }
                    },extraParams);


                }

                @Override
                public void failure(WebAuthError error) {
                    accessTokenCallback.failure(error);
                }
            });


        }
        catch (Exception e)
        {
            //todo handle excepksdjncksjdc
            LogFile.addRecordToLog("Access Token exception" + e.getMessage());
            accessTokenCallback.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.ACCESSTOKEN_SERVICE_FAILURE,"Access Token exception"+ e.getMessage(),
                    HttpStatusCode.EXPECTATION_FAILED));
            Timber.e("Access Token exception" + e.getMessage());
        }
    }


    //Get userinfo Based on Access Token
    @Override
    public void getUserInfo(String sub, final Result<UserinfoEntity> callback) {
        try {
            if (sub != null && sub != "") {

                getAccessToken(sub, new Result<AccessTokenEntity>() {
                    @Override
                    public void success(AccessTokenEntity result) {

                      if(DomainURL!=null && DomainURL!="") {
                             OauthService.getShared(context).getUserinfo(result.getAccess_token(), DomainURL, new Result<UserinfoEntity>() {
                               @Override
                              public void success(UserinfoEntity result) {
                            // hideLoader();
                                  callback.success(result);
                               }

                               @Override
                               public void failure(WebAuthError error) {
                               // hideLoader();
                               callback.failure(error);
                           }
                         });
                      }
                     else {
                          callback.failure(WebAuthError.getShared(context).customException(417, "DomainURL Must not be empty", HttpStatusCode.EXPECTATION_FAILED));
                            }
                    }

                    @Override
                    public void failure(WebAuthError error) {
                        callback.failure(error);
                    }
                });

            } else {
                String errorMessage = "Sub must not be null";
                callback.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING, errorMessage, HttpStatusCode.EXPECTATION_FAILED));
            }
        } catch (Exception e) {
            Timber.d(e.getMessage()); //Todo Handle Exception
        }
    }

    @Override
    public void renewToken(String refershtoken, Result<AccessTokenEntity> result) {

    }

    public void loginWithBrowser(@NonNull final Context activityContext, @Nullable final String color, final Result<AccessTokenEntity> callbacktoMain) {
        try {

           getLoginURL(new Result<String>() {
                @Override
                public void success(String result) {
                    loginURL=result;
                    logincallback = callbacktoMain;
                    if (loginURL != null) {
                        String url = loginURL;
                        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                                             builder.setShowTitle(true);//TO show title

                      //  CustomTabsClient.getPackageName()
                    builder.setStartAnimations(activityContext, android.R.anim.slide_in_left, android.R.anim.slide_out_right);

                        if (color != null) {

                            builder.setToolbarColor(Color.parseColor(color));
                        }

                        CustomTabsIntent customTabsIntent = builder.build();

                  String packageName= CustomTabHelper.getShared().getPackageNameToUse(context);

                        if(packageName!=null && packageName!="")
                        {
                            customTabsIntent.intent.setPackage(packageName);
                        }

                        customTabsIntent.launchUrl(context, Uri.parse(url));
                    } else {
                        //TODo callback Failure
                        String loggerMessage = "LoginURL failure : " + "Error Code - ";
                        // +error.errorCode + ", Error Message - " + error.ErrorMessage + ", Status Code - " +  error.statusCode;
                        LogFile.addRecordToLog(loggerMessage);
                    }
                }

                @Override
                public void failure(WebAuthError error) {
                    callbacktoMain.failure(error);
                }
            });


        } catch (Exception e) {
            Timber.d(e.getMessage());// TODO: Handle Exception
        }
    }

    public void loginWithSocial(@NonNull final Context activityContext,@NonNull final String provider, @Nullable final String color, final Result<AccessTokenEntity> callbacktoMain, final HashMap<String, String>... extraParams)
    {

      checkSavedProperties(new Result<Dictionary<String, String>>() {
          @Override
          public void success(Dictionary<String, String> loginProperties) {
              getRequestId(loginProperties,new Result<AuthRequestResponseEntity>() {
                  @Override
                  public void success(AuthRequestResponseEntity result) {
                      loginWithSocial(activityContext,result.getData().getRequestId(),provider,color,callbacktoMain);
                  }

                  @Override
                  public void failure(WebAuthError error) {
                      callbacktoMain.failure(error);
                  }
              },extraParams);
          }

          @Override
          public void failure(WebAuthError error) {
              callbacktoMain.failure(error);
          }
      });

    }

    public void loginWithSocial(@NonNull final Context activityContext,@NonNull final String requestId, @NonNull final String provider, @Nullable final String color, final Result<AccessTokenEntity> callbacktoMain) {
        try {

            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {
                    getSocialLoginURL(requestId,provider,new Result<String>() {
                        @Override
                        public void success(String socialLoginURL){
                            logincallback = callbacktoMain;
                            if (socialLoginURL != null) {
                                String url = socialLoginURL;
                                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                                builder.setShowTitle(true);//TO show title

                                //  CustomTabsClient.getPackageName()
                                builder.setStartAnimations(activityContext, android.R.anim.slide_in_left, android.R.anim.slide_out_right);



                                if (color != null) {

                                    builder.setToolbarColor(Color.parseColor(color));
                                }


                                CustomTabsIntent customTabsIntent = builder.build();

                                String packageName= CustomTabHelper.getShared().getPackageNameToUse(context);

                                if(packageName!=null && packageName!="")
                                {
                                    customTabsIntent.intent.setPackage(packageName);
                                }

                                customTabsIntent.launchUrl(activityContext, Uri.parse(url));
                            } else {
                                //TODo callback Failure
                                String loggerMessage = "LoginURL failure : " + "Error Code - ";
                                // +error.errorCode + ", Error Message - " + error.ErrorMessage + ", Status Code - " +  error.statusCode;
                                LogFile.addRecordToLog(loggerMessage);
                            }
                        }

                        @Override
                        public void failure(WebAuthError error) {
                            callbacktoMain.failure(error);
                        }
                    });

                }

                @Override
                public void failure(WebAuthError error) {
                  callbacktoMain.failure(error);
                }
            });



        } catch (Exception e) {
            Timber.d(e.getMessage());// TODO: Handle Exception
        }
    }


    public void getLoginCode(String url, Result<AccessTokenEntity> callback) {
        try {
            // showLoader();
            String code = getCodeFromUrl(url);
            if (code != null) {
                //  hideLoader();

                AccessTokenController.getShared(context).getAccessTokenByCode(code, callback);
            } else {
                // hideLoader();
                String loggerMessage = "Request-Id params to dictionary conversion failure : " + "Error Code - ";
                //+error.errorCode + ", Error Message - " + error.ErrorMessage + ", Status Code - " +  error.statusCode;
                LogFile.addRecordToLog(loggerMessage);
            }
        } catch (Exception e) {
            Timber.d(e.getMessage()); //Todo handle Exception
        }
    }




    private void saveLoginProperties(final Result<Dictionary<String, String>> result)
    {
        readFromFile(new Result<Dictionary<String, String>>() {
            @Override
            public void success(Dictionary<String, String> loginProperties) {
                if (loginProperties.get("DomainURL").equals("") || loginProperties.get("DomainURL") == null || loginProperties == null) {
                    webAuthError = webAuthError.propertyMissingException();
                    String loggerMessage = "SavedLoginProperties readProperties failure : " + "Error Code - "
                            + webAuthError.errorCode + ", Error Message -  DomainURL is missing" + webAuthError.ErrorMessage + ", Status Code - " + webAuthError.statusCode;
                    LogFile.addRecordToLog(loggerMessage);
                    result.failure(webAuthError);
                }
                if (loginProperties.get("ClientId").equals("") || loginProperties.get("ClientId") == null || loginProperties == null) {
                    webAuthError = webAuthError.propertyMissingException();
                    String loggerMessage = "SavedLoginProperties readProperties failure : " + "Error Code - ClientId is missing"
                            + webAuthError.errorCode + ", Error Message -  ClientId is missing" + webAuthError.ErrorMessage + ", Status Code - " + webAuthError.statusCode;

                    LogFile.addRecordToLog(loggerMessage);
                    result.failure(webAuthError);
                }
                if (loginProperties.get("RedirectURL").equals("") || loginProperties.get("RedirectURL") == null || loginProperties == null) {
                    webAuthError = webAuthError.propertyMissingException();
                    String loggerMessage = "SavedLoginProperties readProperties failure : " + "Error Code - RedirectURL is missing"
                            + webAuthError.errorCode + ", Error Message -  RedirectURL is missing" + webAuthError.ErrorMessage + ", Status Code - " + webAuthError.statusCode;

                    LogFile.addRecordToLog(loggerMessage);
                    result.failure(webAuthError);
                }
                Cidaas.baseurl = loginProperties.get("DomainURL");
                DBHelper.getShared().addLoginProperties(loginProperties);
                result.success(loginProperties);

            }

            @Override
            public void failure(WebAuthError error) {
                result.failure(error);
            }
        });
    }


    // --------------------------------------------------------------------------------------------------

//Done Configure pattern by Passing the pattern String Directly
    // 1. Done Check For Local Variable or Read properties from file
    // 2. Done Check For NotNull Values
    // 3. Done Call configure Pattern From Pattern Controller and return the result
    // 4. Done Maintain logs based on flags

    public void checkSavedProperties(final Result<Dictionary<String, String>> result) {

        if(DomainURL!=null && !DomainURL.equals("")){


            final Dictionary<String, String> loginProperties = DBHelper.getShared().getLoginProperties(DomainURL);

            if (loginProperties != null && !loginProperties.isEmpty() && loginProperties.size() > 0) {
                //check here for already saved properties

                if (loginProperties.get("RedirectURL").equals("") || loginProperties.get("RedirectURL") == null || loginProperties == null) {
                    webAuthError = webAuthError.propertyMissingException();
                    String loggerMessage = "Check saved properties failure : " + "Error Code - "
                            + webAuthError.errorCode + ", Error Message - " + webAuthError.ErrorMessage + ", Status Code - " + webAuthError.statusCode;
                    LogFile.addRecordToLog(loggerMessage);
                    result.failure(webAuthError);
                    return;
                }
                if (loginProperties.get("ClientId").equals("") || loginProperties.get("ClientId") == null || loginProperties == null) {
                    webAuthError = webAuthError.propertyMissingException();
                    String loggerMessage = "Accept Consent readProperties failure : " + "Error Code - "
                            + webAuthError.errorCode + ", Error Message - " + webAuthError.ErrorMessage + ", Status Code - " + webAuthError.statusCode;

                    LogFile.addRecordToLog(loggerMessage);
                    result.failure(webAuthError);
                    return;
                }


             result.success(loginProperties);
            } else {
                //Read File from asset to get URL
                readFromFile(new Result<Dictionary<String, String>>() {
                    @Override
                    public void success(Dictionary<String, String> savedLoginProperties) {
                        //Call requestIdBy LoginProperties parameter
                       result.success(savedLoginProperties);
                    }

                    @Override
                    public void failure(WebAuthError error) {
                        result.failure(error);
                    }
                });
            }
        }
        else
        {
            result.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,"DomainURL must not be null",HttpStatusCode.EXPECTATION_FAILED));
        }

    }

    public void loginWithFIDO(String usageType, String email, String sub, String trackId, final Result<LoginCredentialsResponseEntity> result) {
        try {
            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {
                    String baseurl = result.get("DomainURL");
                    String clientId = result.get("ClientId");
                }

                @Override
                public void failure(WebAuthError error) {
                    result.failure(WebAuthError.getShared(context).propertyMissingException());
                }
            });
        } catch (Exception e) {
            result.failure(WebAuthError.getShared(context).propertyMissingException());
        }
    }


    // -----------------------------------------------------------------------------------------------------------------------------------------------------
    //Private Methods
    private String getCodeFromUrl(String url) {
        try {
            String code = null;
            if (url.contains("code=")) {
                String[] codeComponents = url.split("code=");
                if (codeComponents.length > 1) {
                    codeComponents = codeComponents[1].split("&");
                    if (codeComponents.length > 0) {
                        code = codeComponents[0];
                        return code;
                    } else {
                        return code;
                    }
                }
            }
            return code;
        } catch (Exception e) {
            Timber.d(e.getMessage());
            return null;
            //Todo Handle Exception
        }
    }

    //Method to check the pkce flow and save it to DB
    private void checkPKCEFlow(Dictionary<String, String> loginproperties, Result<Dictionary<String, String>> savedResult) {
        try {

            webAuthError = WebAuthError.getShared(context);

            //Check all the login Properties are Correct
            if (loginproperties.get("DomainURL") == null || loginproperties.get("DomainURL") == ""
                    || !((Hashtable) loginproperties).containsKey("DomainURL")) {
                webAuthError = webAuthError.propertyMissingException();
                String loggerMessage = "Check PKCE Flow readProperties failure : " + "Error Code - " + webAuthError.errorCode + ", Error Message - "
                        + webAuthError.ErrorMessage + ", Status Code - " + webAuthError.statusCode;
                LogFile.addRecordToLog(loggerMessage);
                savedResult.failure(webAuthError);

                return;
            }
            if (loginproperties.get("ClientId").equals(null) || loginproperties.get("ClientId").equals("")
                    || !((Hashtable) loginproperties).containsKey("ClientId")) {
                webAuthError = webAuthError.propertyMissingException();
                String loggerMessage = "Check PKCE Flow readProperties failure : " + "Error Code - "
                        + webAuthError.errorCode + ", Error Message - " + webAuthError.ErrorMessage + ", Status Code - " + webAuthError.statusCode;
                LogFile.addRecordToLog(loggerMessage);
                savedResult.failure(webAuthError);
                return;
            }
            if (!((Hashtable) loginproperties).containsKey("RedirectURL") || loginproperties.get("RedirectURL").equals(null)
                    || loginproperties.get("RedirectURL").equals("")) {
                webAuthError = webAuthError.propertyMissingException();
                String loggerMessage = "Check PKCE Flow  readProperties failure : " + "Error Code - "
                        + webAuthError.errorCode + ", Error Message - " + webAuthError.ErrorMessage + ", Status Code - " + webAuthError.statusCode;
                LogFile.addRecordToLog(loggerMessage);
                savedResult.failure(webAuthError);
                return;
            }


          //  savedProperties = loginproperties;
            //Get enable Pkce Flag
            ENABLE_PKCE = DBHelper.getShared().getEnablePKCE();

            // Check Client Secret if the PKCE Flow is Disabled
            if (!ENABLE_PKCE) {
                if (loginproperties.get("ClientSecret") == null || loginproperties.get("ClientSecret") == "" ||
                        !((Hashtable) loginproperties).containsKey("ClientSecret")) {
                    webAuthError = webAuthError.propertyMissingException();
                    savedResult.failure(webAuthError);
                } else {
                    loginproperties.put("ClientSecret", loginproperties.get("ClientSecret"));
                }
            }/* else {
                //Create Challenge And Verifier
                OAuthChallengeGenerator generator = new OAuthChallengeGenerator();
                savedProperties.put("Verifier", generator.getCodeVerifier());
                savedProperties.put("Challenge", generator.getCodeChallenge(savedProperties.get("Verifier")));
                savedProperties.put("Method", generator.codeChallengeMethod);
            }*/
            DBHelper.getShared().addLoginProperties(loginproperties);
            savedResult.success(loginproperties);
        } catch (Exception e) {
            Timber.e("Check PKCE Flow  service exception : " + e.getMessage());
            savedResult.failure(webAuthError);
        }
    }


    //Get Login URL without any Argument
    public void getLoginURL( final Result<String> callback) {
        try {
            //Check requestId is not null

                checkSavedProperties(new Result<Dictionary<String, String>>() {
                    @Override
                    public void success(Dictionary<String, String> result) {

                     //This is to generate the Code Challenge

                /////This is for testing purpose
                        Dictionary<String,String> challengeProperties=DBHelper.getShared().getChallengeProperties();

                            if(challengeProperties.size()==0) {

                                getRequestId(new Result<AuthRequestResponseEntity>() {
                                    @Override
                                    public void success(AuthRequestResponseEntity result) {

                                    }

                                    @Override
                                    public void failure(WebAuthError error) {

                                    }
                                });
                            }


                        LoginController.getShared(context).getLoginURL(DomainURL,result,null, new Result<String>() {
                            @Override
                            public void success(String result) {

                                callback.success(result);
                            }

                            @Override
                            public void failure(WebAuthError error) {
                                callback.failure(error);
                                String loggerMessage = "Login URL service failure : " + "Error Code - "
                                        + error.errorCode + ", Error Message - " + error.ErrorMessage + ", Status Code - " + error.statusCode;
                                LogFile.addRecordToLog(loggerMessage);
                            }
                        });
                    }

                    @Override
                    public void failure(WebAuthError error) {
                          callback.failure(error);
                    }
                });


        } catch (Exception ex) {
            //Todo Handle Error
            Timber.d(ex.getMessage());
        }
    }


    public void getSocialLoginURL(final String provider, final Result<String> callback, final HashMap<String,String>... extraParams) {
     try
     {
         checkSavedProperties(new Result<Dictionary<String, String>>() {
             @Override
             public void success(Dictionary<String, String> loginProperties) {
                 getRequestId(loginProperties,new Result<AuthRequestResponseEntity>() {
                     @Override
                     public void success(AuthRequestResponseEntity result) {
                         getSocialLoginURL(result.getData().getRequestId(),provider,callback);
                     }

                     @Override
                     public void failure(WebAuthError error) {
                         callback.failure(error);
                     }
                 },extraParams);
             }

             @Override
             public void failure(WebAuthError error) {

             }
         });

     }
     catch (Exception e)
     {

     }
    }

    //Get Social Login URL
    public void getSocialLoginURL(final String requestId, final String provider, final Result<String> callback) {
        try {
            //Check requestId is not null

            checkSavedProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {
                    LoginController.getShared(context).getSocialLoginURL(DomainURL,provider,requestId, new Result<String>() {
                        @Override
                        public void success(String result) {

                            callback.success(result);
                        }

                        @Override
                        public void failure(WebAuthError error) {
                            callback.failure(error);
                            String loggerMessage = "Login URL service failure : " + "Error Code - "
                                    + error.errorCode + ", Error Message - " + error.ErrorMessage + ", Status Code - " + error.statusCode;
                            LogFile.addRecordToLog(loggerMessage);
                        }
                    });
                }

                @Override
                public void failure(WebAuthError error) {
                    callback.failure(error);
                }
            });


        } catch (Exception ex) {
            //Todo Handle Error
            Timber.d(ex.getMessage());
        }
    }


    //get loginurl by passing Domain URl.......as Arguments

    public void getLoginURL(@NonNull String DomainUrl, @NonNull String ClientId, @NonNull String RedirectURL,
                            @NonNull String ClientSecret, final Result<String> callback) {
        try {
            //Get Request ID
            if ( DomainUrl != null && !Objects.equals(DomainUrl, "")
                    && RedirectURL != null && !Objects.equals(RedirectURL, "")) {
                getRequestId(DomainUrl, ClientId, RedirectURL, ClientSecret, new Result<AuthRequestResponseEntity>() {
                    @Override
                    public void success(AuthRequestResponseEntity result) {
                        // if request ID is valid we call get LoginURl
                        //getLoginURL(result.getData().getRequestId(), callback);
                    }

                    @Override
                    public void failure(WebAuthError error) {
                        callback.failure(error);
                    }
                });
            }
        } catch (Exception ex) {
            Timber.d(ex.getMessage());
            //Todo Handle Exception
        }
    }


    //ReadFromXML File
    private void readFromFile(final Result<Dictionary<String, String>> loginPropertiesResult) {
        FileHelper fileHelper = FileHelper.getShared(context);
        fileHelper.readProperties(context.getAssets(), "Cidaas.xml", new Result<Dictionary<String, String>>() {
            @Override
            public void success(final Dictionary<String, String> loginProperties) {

                //on successfully completion of file reading add it to LocalDB(shared Preference) and call requestIdByloginProperties
                checkPKCEFlow(loginProperties, new Result<Dictionary<String, String>>() {
                    @Override
                    public void success(Dictionary<String, String> savedLoginProperties) {
                        loginPropertiesResult.success(savedLoginProperties);
                        DomainURL=savedLoginProperties.get("DomainURL");
                        DBHelper.getShared().addLoginProperties(savedLoginProperties);

                    }

                    @Override
                    public void failure(WebAuthError error) {
                        loginPropertiesResult.failure(error);
                        String loggerMessage = "Read From File failure : " + "Error Code - " + error.errorCode +
                                ", Error Message - " + error.ErrorMessage + ", Status Code - " + error.statusCode;
                        LogFile.addRecordToLog(loggerMessage);
                    }
                });


            }

            @Override
            public void failure(WebAuthError error) {
                //Return File Reading Error
                String loggerMessage = "Read From File failure : "
                        + "Error Code - " + error.errorCode + ", Error Message - " + error.ErrorMessage + ", Status Code - " + error.statusCode;
                LogFile.addRecordToLog(loggerMessage);
                loginPropertiesResult.failure(error);
            }
        });
    }

    //Resume After open App From Broswer
    public void handleToken(String code){   /*,Result<AccessTokenEntity> callbacktoMain*/

        if (logincallback != null) {

            getLoginCode(code, logincallback);
        }


        /* else if(callbacktoMain!=null)
       {
        *//*logincallback=()callbacktoMain;
        getLoginCode(code,callbacktoMain);*//*
       }*/
        //Todo Handle Else part and give Exception

    }

    private void checkLoginProperties(final Dictionary<String, String> loginproperties,Result<String> result)
    {
        WebAuthError webAuthError = null;

        //WebError Code instance Creation
        webAuthError= WebAuthError.getShared(context);
        // Global Checking
        //Check all the login Properties are Correct
        if (loginproperties.get("DomainURL") == null || loginproperties.get("DomainURL") == ""
                || !((Hashtable) loginproperties).containsKey("DomainURL")) {
            webAuthError = webAuthError.propertyMissingException();
            String loggerMessage = "Request-Id readProperties failure : " + "Error Code - "
                    +webAuthError.errorCode + ", Error Message - " + webAuthError.ErrorMessage + ", Status Code - " +  webAuthError.statusCode;
            LogFile.addRecordToLog(loggerMessage);
            result.failure(webAuthError);

            return;
        }
        if (loginproperties.get("ClientId").equals(null) || loginproperties.get("ClientId").equals("")
                || !((Hashtable) loginproperties).containsKey("ClientId")) {
            webAuthError = webAuthError.propertyMissingException();
            String loggerMessage = "Request-Id readProperties failure : " + "Error Code - "
                    +webAuthError.errorCode + ", Error Message - " + webAuthError.ErrorMessage + ", Status Code - " +  webAuthError.statusCode;
            LogFile.addRecordToLog(loggerMessage);
            result.failure(webAuthError);
            return;
        }
        if (!((Hashtable) loginproperties).containsKey("RedirectURL") || loginproperties.get("RedirectURL").equals(null)
                || loginproperties.get("RedirectURL").equals("")) {
            webAuthError = webAuthError.propertyMissingException();
            String loggerMessage = "Request-Id readProperties failure : " + "Error Code - "
                    +webAuthError.errorCode + ", Error Message - " + webAuthError.ErrorMessage + ", Status Code - " +  webAuthError.statusCode;
            LogFile.addRecordToLog(loggerMessage);
            result.failure(webAuthError);
            return;
        }

    }


    public void setURL(@NonNull final Dictionary<String, String> loginproperties)
    {
        try
        {
            if(loginproperties!=null) {
                DomainURL=loginproperties.get("DomainURL");
                Cidaas.baseurl=DomainURL;


                if(loginproperties.get("userDeviceId")!=null && loginproperties.get("userDeviceId")!="") {
                    String userDeviceId = loginproperties.get("userDeviceId");
                    DBHelper.getShared().setUserDeviceId(userDeviceId, DomainURL);
                }

                DBHelper.getShared().addLoginProperties(loginproperties);


            }
            else
            {
                String loggerMessage = "SetURL File : " + " Error Message -Login properties in null " ;
                LogFile.addRecordToLog(loggerMessage);
            }
        }
        catch (Exception e)
        {
            String loggerMessage = "SetURL File : " + " Error Message - " + e.getMessage();
            LogFile.addRecordToLog(loggerMessage);

        }

    }


    //------------------------------------------------------------------------------------------Local Authentication----------------------------------------

    //Cidaas Set OnActivity Result For Handling Device Authentication
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {

            if(activityFromCidaas!=null && localAuthenticationEntityCallback!=null ) {
                if (requestCode == LOCAL_REQUEST_CODE || requestCode == LOCAL_AUTH_REQUEST_CODE) {

                    if (requestCode == LOCAL_REQUEST_CODE || requestCode == LOCAL_AUTH_REQUEST_CODE) {
                        switch (requestCode) {
                            case LOCAL_REQUEST_CODE:
                                if (resultCode == RESULT_OK) {

                                    //Send Positive callback
                                    String user = "User Authenticated";

                                    LocalAuthenticationEntity localAuthenticationEntity=new LocalAuthenticationEntity();
                                    localAuthenticationEntity.setMessage(user);
                                    localAuthenticationEntity.setRequestCode(LOCAL_REQUEST_CODE);
                                    localAuthenticationEntity.setResultCode(resultCode);
                                    localAuthenticationEntityCallback.success(localAuthenticationEntity);


                                } else {
                                    // user did not authenticate so send failure callback


                                    String user = "User Cancelled the Authentication";


                                    localAuthenticationEntityCallback.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.LOCAL_AUHTHENTICATION_CANCELLED,user,417));

                                    Timber.d("User" + user);

                                }
                                break;
                            case LOCAL_AUTH_REQUEST_CODE:
                                localAuthentication(activityFromCidaas,localAuthenticationEntityCallback);
                                break;
                            default:
                                //.onActivityResult(requestCode, resultCode, data);
                        }
                    }

                }
            }
            else
            {
                localAuthenticationEntityCallback.failure(new WebAuthError(context).customException(WebAuthErrorCode.LOCAL_AUHTHENTICATION_FAILED, "Call back must not be null", 417));
            }
        } catch (Exception e) {
            localAuthenticationEntityCallback.failure(new WebAuthError(context).customException(WebAuthErrorCode.LOCAL_AUHTHENTICATION_FAILED, "Local Authentication Exception"+e.getMessage(), 417));
        }
    }

    //Show the Alert Dilog Which is go to settings
    private void showDialogToSetupLock(final Activity activity,Result<LocalAuthenticationEntity> result) {

        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
            LayoutInflater inflater = activity.getLayoutInflater();
            final View dialog = inflater.inflate(R.layout.lock_setting_dialog, null);
            alertDialogBuilder.setView(dialog);
            alertDialogBuilder.setCancelable(false);
            Button btn_ok = dialog.findViewById(R.id.btn_ok);
            final AlertDialog alertDialog = alertDialogBuilder.create();

            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (alertDialog != null) {
                        alertDialog.dismiss();
                    }


                    String manufacturer = "xiaomi";
                    if (manufacturer.equalsIgnoreCase(android.os.Build.MANUFACTURER)) {
                        //this will open auto start screen where user can enable permission for your app
                        activity.startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), LOCAL_AUTH_REQUEST_CODE);
                    } else {

                        activity.startActivityForResult(new Intent(Settings.ACTION_SECURITY_SETTINGS), LOCAL_AUTH_REQUEST_CODE);
                    }
                }
            });
            alertDialog.show();
        }
        catch (Exception e)
        {
            result.failure(new WebAuthError(context).customException(WebAuthErrorCode.LOCAL_AUHTHENTICATION_FAILED, "Bad document or no document", 417));
        }
    }



    //Method for Local Authentocation
    public void localAuthentication(final Activity activity, Result<LocalAuthenticationEntity> result) {
        try {


            activityFromCidaas=activity;
            localAuthenticationEntityCallback=result;

            KeyguardManager keyguardManager = (KeyguardManager) activity.getSystemService(Context.KEYGUARD_SERVICE);
            boolean isSecure = keyguardManager.isKeyguardSecure();

            if (isSecure) {

                Intent intent = keyguardManager.createConfirmDeviceCredentialIntent(null, null);
                activity.startActivityForResult(intent, LOCAL_REQUEST_CODE);
            } else {
                // tabPager.setVisibility(View.GONE);
                // no lock screen set, show the new lock needed screen
                //showDialogToSetupLock(activity,result);
                result.failure(new WebAuthError(context).customException(WebAuthErrorCode.NO_LOCAL_AUHTHENTICATION_FOUND, "NO LOCAL AUTHENTICATION FOUND", 417));
            }
        }
        catch (Exception e)
        {
            result.failure(new WebAuthError(context).customException(WebAuthErrorCode.LOCAL_AUHTHENTICATION_FAILED, "Bad document or no document", 417));
        }
    }



    //------------------------------------------------------------------------------------------XXXXXXX----------------------------------------

    public static String getSDKVersion(){
        String version="";
        try {

            version ="("+ BuildConfig.VERSION_NAME+")";
        } catch (Exception e) {
            return "";
        }

        return  version;
    }

    public String getUserAgent()
    {
        try
        {
            return DBHelper.getShared().getUserAgent();
        }
        catch (Exception e)
        {
            String loggerMessage = "get UserAgent : " + " Error Message - " + e.getMessage();
            LogFile.addRecordToLog(loggerMessage);
            return "";
        }
    }



    public void setAccessToken(final AccessTokenEntity accessTokenEntity, final Result<LoginCredentialsResponseEntity> result){
        try
        {

            if(accessTokenEntity.getSub()!=null && accessTokenEntity.getSub()!="" &&
                    accessTokenEntity.getAccess_token()!=null && accessTokenEntity.getAccess_token()!="" &&
                    accessTokenEntity.getRefresh_token()!=null && accessTokenEntity.getRefresh_token()!="") {
                EntityToModelConverter.getShared().accessTokenEntityToAccessTokenModel(accessTokenEntity, accessTokenEntity.getSub(), new Result<AccessTokenModel>() {
                    @Override
                    public void success(AccessTokenModel accessTokenModel) {
                      DBHelper.getShared().setAccessToken(accessTokenModel);
                        LoginCredentialsResponseEntity loginCredentialsResponseEntity=new LoginCredentialsResponseEntity();
                        loginCredentialsResponseEntity.setData(accessTokenEntity);
                        loginCredentialsResponseEntity.setStatus(200);
                        loginCredentialsResponseEntity.setSuccess(true);
                        result.success(loginCredentialsResponseEntity);
                    }

                    @Override
                    public void failure(WebAuthError error) {
                        String loggerMessage = "Set Access Token : " + " Error Message - "+error.getErrorMessage();
                        LogFile.addRecordToLog(loggerMessage);

                        result.failure(error);
                    }
                });





            }
            else
            {
                String loggerMessage = "Set Access Token : " + " Error Message - Sub or accessToken or refreshToken must not be null";
                LogFile.addRecordToLog(loggerMessage);
                result.failure(WebAuthError.getShared(context).customException(417," Sub or accessToken or refreshToken must not be null",417));
            }

        }
        catch (Exception e){

            String loggerMessage = "Set Access Token : " + " Error Message - " + e.getMessage();
            LogFile.addRecordToLog(loggerMessage);
            result.failure(WebAuthError.getShared(context).customException(417,"Something Went wrong please try again",417));
        }
    }




    //------------------------------------------------------------------------------------------CallFingerPrint----------------------------------------

    public void callFingerPrint(final Context context, FingerPrintEntity fingerPrintEntity, final Result<String> result)
    {
        try
        {
            if (Build.VERSION.SDK_INT >= 23) {

                FingerPrintEntity fingerPrintEntityForPassing = new FingerPrintEntity();
                if (fingerPrintEntity == null) {
                    fingerPrintEntity = fingerPrintEntityForPassing;
                }

                new BiometricManager.BiometricBuilder(context)
                        .setTitle(fingerPrintEntity.getTitle())
                        .setSubtitle(fingerPrintEntity.getSubtitle())
                        .setDescription(fingerPrintEntity.getDescription())
                        .setNegativeButtonText(fingerPrintEntity.getNegativeButtonString())
                        .build()
                        .authenticate(new BiometricCallback() {
                            @Override
                            public void onSdkVersionNotSupported() {
                                result.failure(WebAuthError.getShared(context).fingerPrintError(WebAuthErrorCode.FINGERPRINT_SDK_VERSION_NOT_SUPPORTED,"SDK Version Not Supported"));
                            }

                            @Override
                            public void onBiometricAuthenticationNotSupported() {
                                result.failure(WebAuthError.getShared(context).fingerPrintError(WebAuthErrorCode.FINGERPRINT_BIOMETRIC_AUTHENTICATION_NOT_SUPPORTED,"Biometric Authentication  Not Supported"));

                            }

                            @Override
                            public void onBiometricAuthenticationNotAvailable() {
                                result.failure(WebAuthError.getShared(context).fingerPrintError(WebAuthErrorCode.FINGERPRINT_BIOMERTIC_AUTHENTICATION_NOT_AVAILABLE,"Biometric Authentication  Not Available"));
                            }

                            @Override
                            public void onBiometricAuthenticationPermissionNotGranted() {
                                result.failure(WebAuthError.getShared(context).fingerPrintError(WebAuthErrorCode.FINGERPRINT_BIOMERTIC_AUTHENTICATION_PERMISSION_NOT_GRANTED,"Biometric Authentication  Permission Not Granted"));
                            }

                            @Override
                            public void onBiometricAuthenticationInternalError(String error) {

                                result.failure(WebAuthError.getShared(context).fingerPrintError(WebAuthErrorCode.FINGERPRINT_BIOMERTIC_AUTHENTICATION_INTERNAL_ERROR,"Biometric Authentication  Internal Error"));
                            }

                            @Override
                            public void onAuthenticationFailed() {
                               // result.failure(WebAuthError.getShared(context).fingerPrintError(WebAuthErrorCode.FINGERPRINT_AUTHENTICATION_FAILED,"Biometric Authentication  Failed"));
                            }

                            @Override
                            public void onAuthenticationCancelled() {
                                result.failure(WebAuthError.getShared(context).fingerPrintError(WebAuthErrorCode.FINGERPRINT_AUTHENTICATION_CANCELLED,"Biometric Authentication  Cancelled"));
                            }

                            @Override
                            public void onAuthenticationSuccessful() {
                                result.success("Success");
                            }

                            @Override
                            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {

                                String errorMessage = helpString.toString();
                                result.failure(WebAuthError.getShared(context).fingerPrintError(helpCode, errorMessage));
                            }

                            @Override
                            public void onAuthenticationError(int errorCode, CharSequence errString) {

                                String errorMessage = errString.toString();
                                result.failure(WebAuthError.getShared(context).fingerPrintError(errorCode, errorMessage));
                            }
                        });
            }
            else
            {
                String ErrorMessage="Fingerprint doesnot Support in your mobile";
                result.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.FINGERPRINT_AUTHENTICATION_FAILED,ErrorMessage,HttpStatusCode.EXPECTATION_FAILED));

            }
        }
        catch (Exception e)
        {
           result.failure( WebAuthError.getShared(context).customException(WebAuthErrorCode.FINGERPRINT_AUTHENTICATION_FAILED,""+e.getMessage(),HttpStatusCode.EXPECTATION_FAILED));
        }

    }



    //----------------------------------LocationHistory------------------------------------------------------------------------------------------------------
   //Todo Add Logs
    public void getUserLoginInfo(final UserLoginInfoEntity userLoginInfoEntity, final Result<UserLoginInfoResponseEntity> result)
    {
        try
        {
            if(userLoginInfoEntity.getSub()!=null && userLoginInfoEntity.getSub()!="") {
                checkSavedProperties(new Result<Dictionary<String, String>>() {
                    @Override
                    public void success(final Dictionary<String, String> lpresult) {
                        final String baseurl = lpresult.get("DomainURL");

                        //Get AccessToken From Sub
                        AccessTokenController.getShared(context).getAccessToken(userLoginInfoEntity.getSub(), new Result<AccessTokenEntity>() {
                            @Override
                            public void success(AccessTokenEntity accessTokenresult) {
                                UserLoginInfoController.getShared(context).getUserLoginInfo(baseurl,accessTokenresult.getAccess_token(), userLoginInfoEntity,result);
                            }

                            @Override
                            public void failure(WebAuthError error) {
                                 result.failure(error);
                            }
                        });

                    }

                    @Override
                    public void failure(WebAuthError error) {
                        result.failure(WebAuthError.getShared(context).propertyMissingException());
                    }
                });
            }
            else {
                // handle Faliure
                result.failure(WebAuthError.getShared(context).
                        customException(WebAuthErrorCode.USER_LOGIN_INFO_SERVICE_FAILURE,"Sub must not be empty",
                                HttpStatusCode.EXPECTATION_FAILED));
            }
        }
        catch (Exception e)
        {
            // handle Faliure Exception
            result.failure(WebAuthError.getShared(context).
                    customException(WebAuthErrorCode.USER_LOGIN_INFO_SERVICE_FAILURE,e.getMessage(),
                            HttpStatusCode.EXPECTATION_FAILED));
        }
    }
}
