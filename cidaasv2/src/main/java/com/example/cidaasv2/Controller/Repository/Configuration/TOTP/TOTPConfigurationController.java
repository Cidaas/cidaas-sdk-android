package com.example.cidaasv2.Controller.Repository.Configuration.TOTP;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;

import com.example.cidaasv2.Controller.Cidaas;
import com.example.cidaasv2.Controller.Repository.AccessToken.AccessTokenController;
import com.example.cidaasv2.Controller.Repository.Configuration.TOTP.TOTPGenerator.GoogleAuthenticator;
import com.example.cidaasv2.Controller.Repository.Configuration.TOTP.TOTPGenerator.TotpClock;
import com.example.cidaasv2.Controller.Repository.Login.LoginController;
import com.example.cidaasv2.Controller.Repository.ResumeLogin.ResumeLogin;
import com.example.cidaasv2.Helper.AuthenticationType;
import com.example.cidaasv2.Helper.CidaasProperties.CidaasProperties;
import com.example.cidaasv2.Helper.Entity.PasswordlessEntity;
import com.example.cidaasv2.Helper.Enums.HttpStatusCode;
import com.example.cidaasv2.Helper.Enums.Result;
import com.example.cidaasv2.Helper.Enums.UsageType;
import com.example.cidaasv2.Helper.Enums.WebAuthErrorCode;
import com.example.cidaasv2.Helper.Extension.WebAuthError;
import com.example.cidaasv2.Helper.Genral.DBHelper;
import com.example.cidaasv2.Helper.Logger.LogFile;
import com.example.cidaasv2.Helper.pkce.OAuthChallengeGenerator;
import com.example.cidaasv2.Service.Entity.AccessTokenEntity;
import com.example.cidaasv2.Service.Entity.LoginCredentialsEntity.LoginCredentialsResponseEntity;
import com.example.cidaasv2.Service.Entity.LoginCredentialsEntity.ResumeLogin.ResumeLoginRequestEntity;
import com.example.cidaasv2.Service.Entity.MFA.AuthenticateMFA.TOTP.AuthenticateTOTPRequestEntity;
import com.example.cidaasv2.Service.Entity.MFA.AuthenticateMFA.TOTP.AuthenticateTOTPResponseEntity;
import com.example.cidaasv2.Service.Entity.MFA.EnrollMFA.TOTP.EnrollTOTPMFARequestEntity;
import com.example.cidaasv2.Service.Entity.MFA.EnrollMFA.TOTP.EnrollTOTPMFAResponseEntity;
import com.example.cidaasv2.Service.Entity.MFA.InitiateMFA.TOTP.InitiateTOTPMFARequestEntity;
import com.example.cidaasv2.Service.Entity.MFA.InitiateMFA.TOTP.InitiateTOTPMFAResponseEntity;
import com.example.cidaasv2.Service.Entity.MFA.SetupMFA.TOTP.SetupTOTPMFARequestEntity;
import com.example.cidaasv2.Service.Entity.MFA.SetupMFA.TOTP.SetupTOTPMFAResponseEntity;
import com.example.cidaasv2.Service.Entity.MFA.TOTPEntity.TOTPEntity;
import com.example.cidaasv2.Service.Repository.Verification.TOTP.TOTPVerificationService;
import com.example.cidaasv2.Service.Scanned.ScannedRequestEntity;
import com.example.cidaasv2.Service.Scanned.ScannedResponseEntity;

import java.text.DecimalFormat;
import java.util.Dictionary;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import timber.log.Timber;

public class TOTPConfigurationController {

    private String authenticationType,secretWithValue,secret;
    private String verificationType;
    private Context context;
    CountDownTimer countDownTimer;

    public static String logoURLlocal="https://cdn.shortpixel.ai/client/q_glossy,ret_img/https://www.cidaas.com/wp-content/uploads/2018/02/logo.png";

    public static TOTPConfigurationController shared;

    public TOTPConfigurationController(Context contextFromCidaas) {

        verificationType="";
        context=contextFromCidaas;
        authenticationType="";
        //Todo setValue for authenticationType

    }

    String codeVerifier, codeChallenge;
    // Generate Code Challenge and Code verifier
    public void generateChallenge(){
        OAuthChallengeGenerator generator = new OAuthChallengeGenerator();

        codeVerifier=generator.getCodeVerifier();
        codeChallenge= generator.getCodeChallenge(codeVerifier);

    }

    public static TOTPConfigurationController getShared(Context contextFromCidaas )
    {
        try {

            if (shared == null) {
                shared = new TOTPConfigurationController(contextFromCidaas);
            }
        }
        catch (Exception e)
        {
            Timber.i(e.getMessage());
        }
        return shared;
    }


//Todo Configure TOTP by Passing the setupTOTPRequestEntity


    public TOTPEntity generateTOTP(String secret)
    {
        String TOTP="";
        try
        {
            TotpClock totpClock;
            int local_totp, local_totp1;



            totpClock = new TotpClock(context);
            long temp = totpClock.currentTimeMillis();
            local_totp = (int)((temp / 1000) % 30);
            long temp1 = temp - 1000;
            local_totp1 = (int)((temp1 / 100) % 300);

            // set progress state

            DecimalFormat format = new DecimalFormat("00");
            String timercount = format.format(30 - local_totp);

            TOTP = GoogleAuthenticator.getTOTPCode(secret);

            TOTPEntity totpEntity=new TOTPEntity();
            totpEntity.setTimer_count(timercount);
            totpEntity.setTotp_string(TOTP);


          /*  if(local_totp == 0) {

            }
*/
            return totpEntity;
        }
        catch (Exception e) {
          return null;
        }
    }


    public void configureTOTP(final String sub,@NonNull final String logoURL, final Result<EnrollTOTPMFAResponseEntity> enrollresult) {
        try {
        CidaasProperties.getShared(context).checkCidaasProperties(new Result<Dictionary<String, String>>() {
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


                    configureTOTP(sub, finalBaseurl, setupTOTPMFARequestEntity, enrollresult);

                } else {
                    String errorMessage = "Sub or TOTP cannot be null";
                    enrollresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING, errorMessage,
                            HttpStatusCode.EXPECTATION_FAILED));
                }
            }

            @Override
            public void failure(WebAuthError error) {
                enrollresult.failure(WebAuthError.getShared(context).propertyMissingException("DomainURL or ClientId or RedirectURL must not be empty"));
            }
        });
    } catch (Exception e) {
        LogFile.getShared(context).addRecordToLog("Enroll TOTP  exception" + e.getMessage());
        enrollresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.AUTHENTICATE_VOICE_MFA_FAILURE,"Enroll TOTP exception"+ e.getMessage(),
                HttpStatusCode.EXPECTATION_FAILED));
        Timber.e("Enroll TOTP exception" + e.getMessage());
    }
    }

    //Service call To SetupTOTPMFA
    public void configureTOTP(@NonNull final String sub, @NonNull final String baseurl,
                                 @NonNull final SetupTOTPMFARequestEntity setupTOTPMFARequestEntity,
                                 @NonNull final Result<EnrollTOTPMFAResponseEntity> enrollresult)
    {
        try{

            if(codeChallenge.equals("") || codeVerifier.equals("") || codeChallenge==null || codeVerifier==null) {
                //Generate Challenge
                generateChallenge();
            }
            Cidaas.usagePass ="";

            AccessTokenController.getShared(context).getAccessToken(sub, new Result<AccessTokenEntity>()
            {
                @Override
                public void success(final AccessTokenEntity accessTokenresult) {
                    setupTOTP(baseurl,sub,accessTokenresult.getAccess_token(),setupTOTPMFARequestEntity,enrollresult);
                }

                @Override
                public void failure(WebAuthError error) {
                    enrollresult.failure(error);
                }

            });

        }
        catch (Exception e)
        {
            Timber.e(e.getMessage());
            enrollresult.failure(WebAuthError.getShared(context).serviceException(WebAuthErrorCode.ENROLL_TOTP_MFA_FAILURE));
            LogFile.getShared(context).addRecordToLog("Configure TOTP "+e.getMessage()+WebAuthErrorCode.ENROLL_TOTP_MFA_FAILURE);
        }
    }


    private void setupTOTP(final String baseurl, final String sub, final String accessToken,
                           final SetupTOTPMFARequestEntity setupTOTPMFARequestEntity, final Result<EnrollTOTPMFAResponseEntity> enrollResult)
    {
        try
        {
            if (baseurl != null && !baseurl.equals("") && accessToken != null && !accessToken.equals("") &&
                    setupTOTPMFARequestEntity.getClient_id()!=null && !setupTOTPMFARequestEntity.getClient_id().equals(""))
            {
                //Done Service call

                TOTPVerificationService.getShared(context).setupTOTP(baseurl, accessToken,
                        setupTOTPMFARequestEntity,null,new Result<SetupTOTPMFAResponseEntity>() {
                            @Override
                            public void success(final SetupTOTPMFAResponseEntity setupserviceresult) {


                                secret=setupserviceresult.getData().getSecret();

                                if(secret!=null && !secret.equals(""))
                                {
                                    DBHelper.getShared().addSecret(secret,sub);
                                }
                                else
                                {
                                    String errorMessage="Invalid TOTP Secret";
                                    enrollResult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.ENROLL_TOTP_MFA_FAILURE,errorMessage,HttpStatusCode.EXPECTATION_FAILED));
                                }

                                Cidaas.usagePass ="";

                                new CountDownTimer(5000, 500) {
                                    String usagePassFromService="";
                                    public void onTick(long millisUntilFinished) {
                                        usagePassFromService= Cidaas.usagePass;

                                        if(usagePassFromService!=null && !usagePassFromService.equals(""))
                                        {
                                            this.cancel();
                                            onFinish();
                                        }

                                    }
                                    public void onFinish() {
                                        if(usagePassFromService!=null && !usagePassFromService.equals("") ) {

                                            setUpTOTPAfterDeviceVerification(usagePassFromService,baseurl,sub,accessToken,setupTOTPMFARequestEntity.getClient_id(),enrollResult);
                                        }

                                        else {
                                            enrollResult.failure(WebAuthError.getShared(context).deviceVerificationFailureException());
                                        }
                                    }



                                }.start();

                            }


                            @Override
                            public void failure(WebAuthError error) {
                                enrollResult.failure(error);
                            }
                        });
            }
            else
            {

                enrollResult.failure(WebAuthError.getShared(context).propertyMissingException("BaseURL , AccessToken or ClientId must not be null"));
            }
        }
        catch (Exception e)
        {
            LogFile.getShared(context).addRecordToLog(e.getMessage()+WebAuthErrorCode.SETUP_TOTP_MFA_FAILURE);
            enrollResult.failure(WebAuthError.getShared(context).serviceException(WebAuthErrorCode.SETUP_TOTP_MFA_FAILURE));

        }
    }


    //Setup TOTP After Device Verification
    private void setUpTOTPAfterDeviceVerification(String usagePassFromService, final String baseurl, final String sub, final String accessToken,
                                                  final String clientId, final Result<EnrollTOTPMFAResponseEntity> enrollResult) {
        try {
            SetupTOTPMFARequestEntity setupTOTPMFARequestEntityWithUsagePass = new SetupTOTPMFARequestEntity();
            setupTOTPMFARequestEntityWithUsagePass.setUsage_pass(usagePassFromService);
            // call Scanned Service
            TOTPVerificationService.getShared(context).setupTOTP(baseurl, accessToken,
                    setupTOTPMFARequestEntityWithUsagePass, null, new Result<SetupTOTPMFAResponseEntity>() {
                        @Override
                        public void success(final SetupTOTPMFAResponseEntity result) {
                            DBHelper.getShared().setUserDeviceId(result.getData().getUdi(), baseurl);


                            EnrollTOTPMFARequestEntity enrollTOTPMFARequestEntity = new EnrollTOTPMFARequestEntity();
                            TOTPEntity totp;
                            if (secret != null) {
                                totp = generateTOTP(secret);

                                enrollTOTPMFARequestEntity.setVerifierPassword(totp.getTotp_string());
                                enrollTOTPMFARequestEntity.setStatusId(result.getData().getSt());
                                enrollTOTPMFARequestEntity.setUserDeviceId(result.getData().getUdi());
                                enrollTOTPMFARequestEntity.setClient_id(clientId);

                            }

                            enrollTOTP(baseurl, accessToken, enrollTOTPMFARequestEntity, enrollResult);

                        }

                        @Override
                        public void failure(WebAuthError error) {
                            enrollResult.failure(error);
                        }
                    });
        }
        catch (Exception e)
        {
            LogFile.getShared(context).addRecordToLog("Setup After Device Verification"+e.getMessage()+WebAuthErrorCode.SETUP_TOTP_MFA_FAILURE);
            enrollResult.failure(WebAuthError.getShared(context).serviceException(WebAuthErrorCode.SETUP_TOTP_MFA_FAILURE));

        }
    }

    public void scannedWithTOTP(final String statusId, final String sub, final String secret, final Result<ScannedResponseEntity> scannedResult)
    {
        try
        {

            CidaasProperties.getShared(context).checkCidaasProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {
                    final String baseurl = result.get("DomainURL");
                    String clientId = result.get("ClientId");

                    if (statusId!=null && !statusId.equals("")) {

                        final ScannedRequestEntity scannedRequestEntity = new ScannedRequestEntity();
                        scannedRequestEntity.setStatusId(statusId);
                        scannedRequestEntity.setClient_id(clientId);


                        if(secret!=null && !secret.equals(""))
                        {
                            DBHelper.getShared().addSecret(secret,sub);
                        }
                        else
                        {
                            String errorMessage="Invalid TOTP Secret";
                            scannedResult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.ENROLL_TOTP_MFA_FAILURE,errorMessage,HttpStatusCode.EXPECTATION_FAILED));
                            return;
                        }

                        scannedTOTPService(baseurl, scannedRequestEntity,scannedResult);
                    }
                    else {
                        scannedResult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.SCANNED_TOTP_MFA_FAILURE,
                                "BaseURL or ClientId or StatusID must not be empty", HttpStatusCode.EXPECTATION_FAILED));
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
            LogFile.getShared(context).addRecordToLog("Scanned with TOTP "+e.getMessage()+WebAuthErrorCode.SCANNED_TOTP_MFA_FAILURE);
            scannedResult.failure(WebAuthError.getShared(context).serviceException(WebAuthErrorCode.SCANNED_TOTP_MFA_FAILURE));
        }
    }

    private void scannedTOTPService(final String baseurl, ScannedRequestEntity scannedRequestEntity, final Result<ScannedResponseEntity> scannedResult) {
        try {
            TOTPVerificationService.getShared(context).scannedTOTP(baseurl, scannedRequestEntity, null, new Result<ScannedResponseEntity>() {
                @Override
                public void success(ScannedResponseEntity result) {
                    Cidaas.usagePass = "";

                    new CountDownTimer(5000, 500) {
                        String usagePassFromService = "";

                        public void onTick(long millisUntilFinished) {
                            usagePassFromService = Cidaas.usagePass;

                            if (usagePassFromService != null && !usagePassFromService.equals("")) {
                                this.cancel();
                                onFinish();
                            }

                        }

                        public void onFinish() {

                            if (usagePassFromService != null && !usagePassFromService.equals("")) {

                                ScannedRequestEntity scannedRequestEntity = new ScannedRequestEntity();
                                scannedRequestEntity.setUsage_pass(usagePassFromService);

                                TOTPVerificationService.getShared(context).scannedTOTP(baseurl, scannedRequestEntity, null, scannedResult);
                            } else {
                                scannedResult.failure(WebAuthError.getShared(context).deviceVerificationFailureException());
                            }
                        }
                    }.start();

                }

                @Override
                public void failure(WebAuthError error) {
                    scannedResult.failure(error);
                }
            });
        }
        catch (Exception e)
        {
            LogFile.getShared(context).addRecordToLog("Scanned with TOTP "+e.getMessage()+WebAuthErrorCode.SCANNED_TOTP_MFA_FAILURE);
            scannedResult.failure(WebAuthError.getShared(context).serviceException(WebAuthErrorCode.SCANNED_TOTP_MFA_FAILURE));
        }
    }


    public void enrollTOTP(@NonNull final String baseurl, @NonNull final String accessToken,
                              @NonNull EnrollTOTPMFARequestEntity enrollTOTPMFARequestEntity, final Result<EnrollTOTPMFAResponseEntity> enrollResult)
    {
        try
        {

            if(baseurl!=null && !baseurl.equals("") && accessToken!=null && !accessToken.equals("")) {

                if (enrollTOTPMFARequestEntity.getUserDeviceId() != null && !enrollTOTPMFARequestEntity.getUserDeviceId().equals("") &&
                        enrollTOTPMFARequestEntity.getStatusId() != null && !enrollTOTPMFARequestEntity.getStatusId().equals("") &&
                        enrollTOTPMFARequestEntity.getClient_id() != null && !enrollTOTPMFARequestEntity.getClient_id().equals("") &&
                        enrollTOTPMFARequestEntity.getVerifierPassword() != null && !enrollTOTPMFARequestEntity.getVerifierPassword().equals("")) {

                    // call Enroll Service
                    TOTPVerificationService.getShared(context).enrollTOTP(baseurl, accessToken, enrollTOTPMFARequestEntity,
                            null, new Result<EnrollTOTPMFAResponseEntity>() {

                                @Override
                                public void success(final EnrollTOTPMFAResponseEntity serviceresult) {

                                    Cidaas.usagePass = "";

                                    //Timer
                                    new CountDownTimer(5000, 500) {
                                        String usagePassFromService = "";

                                        public void onTick(long millisUntilFinished) {
                                            usagePassFromService = Cidaas.usagePass;

                                            Timber.e("");
                                            if (usagePassFromService != null && !usagePassFromService.equals("")) {
                                                this.cancel();
                                                onFinish();
                                            }

                                        }

                                        public void onFinish() {
                                            if (usagePassFromService != null && !usagePassFromService.equals("")) {

                                                //enroll
                                                EnrollTOTPMFARequestEntity enrollTOTPMFARequestEntity = new EnrollTOTPMFARequestEntity();
                                                enrollTOTPMFARequestEntity.setUsage_pass(usagePassFromService);

                                                // call Enroll Service
                                                TOTPVerificationService.getShared(context).enrollTOTP(baseurl, accessToken, enrollTOTPMFARequestEntity,
                                                        null, enrollResult);
                                            }
                                            else {
                                                // return Error Message
                                                enrollResult.failure(WebAuthError.getShared(context).deviceVerificationFailureException());
                                            }

                                        }
                                    }.start();
                                }

                                @Override
                                public void failure(WebAuthError error) {
                                    enrollResult.failure(error);
                                    //   Toast.makeText(context, "Error on Scanned"+error.getErrorMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    enrollResult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.ENROLL_TOTP_MFA_FAILURE,
                            "UserdeviceId or Verifierpassword or clientId or StatusID must not be empty", HttpStatusCode.EXPECTATION_FAILED));
                }
            }
            else
            {
                enrollResult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.ENROLL_TOTP_MFA_FAILURE,
                        "BaseURL or accessToken must not be empty", HttpStatusCode.EXPECTATION_FAILED));
            }


        }
        catch (Exception e)
        {
            enrollResult.failure(WebAuthError.getShared(context).serviceException(WebAuthErrorCode.ENROLL_TOTP_MFA_FAILURE));
            LogFile.getShared(context).addRecordToLog(e.getMessage()+WebAuthErrorCode.ENROLL_TOTP_MFA_FAILURE);

        }
    }

    public void LoginWithTOTP(final PasswordlessEntity passwordlessEntity, final Result<LoginCredentialsResponseEntity> loginresult) {
        try {
            CidaasProperties.getShared(context).checkCidaasProperties(new Result<Dictionary<String, String>>() {
                @Override
                public void success(Dictionary<String, String> result) {
                    String baseurl = result.get("DomainURL");
                    String clientId = result.get("ClientId");
                    if (passwordlessEntity.getUsageType() != null && !passwordlessEntity.getUsageType().equals("") &&
                            passwordlessEntity.getRequestId() != null && !passwordlessEntity.getRequestId().equals("")) {

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


                        LoginWithTOTP(baseurl, clientId, passwordlessEntity.getTrackId(), passwordlessEntity.getRequestId(), initiateTOTPMFARequestEntity, loginresult);
                    } else {
                        String errorMessage = "UsageType or requestId must not be empty";

                        loginresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.PROPERTY_MISSING,
                                errorMessage, HttpStatusCode.EXPECTATION_FAILED));
                    }
                }

                @Override
                public void failure(WebAuthError error) {
                    loginresult.failure(WebAuthError.getShared(context).propertyMissingException("DomainURL or ClientId or RedirectURL must not be empty"));
                }
            });
        } catch (Exception e) {
            LogFile.getShared(context).addRecordToLog("Login TOTP exception" + e.getMessage());
            loginresult.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.AUTHENTICATE_VOICE_MFA_FAILURE,"Login TOTP exception"+ e.getMessage(),
                    HttpStatusCode.EXPECTATION_FAILED));
            Timber.e("Login TOTP exception" + e.getMessage());
        }
    }


        //Login with TOTP
    public void LoginWithTOTP(@NonNull final String baseurl, @NonNull final String clientId,
                              @NonNull final String trackId, @NonNull final String requestId,
                              @NonNull final InitiateTOTPMFARequestEntity initiateTOTPMFARequestEntity,
                              final Result<LoginCredentialsResponseEntity> loginresult)
    {
        try{

            if(codeChallenge.equals("") && codeVerifier.equals("")) {
                //Generate Challenge
                generateChallenge();
            }
            Cidaas.usagePass ="";
            if(initiateTOTPMFARequestEntity.getUserDeviceId() != null && !initiateTOTPMFARequestEntity.getUserDeviceId().equals(""))
            {
                //Do nothing
            }
            else
            {
                initiateTOTPMFARequestEntity.setUserDeviceId(DBHelper.getShared().getUserDeviceId(baseurl));
            }
            initiateTOTPMFARequestEntity.setClient_id(clientId);


            if (    initiateTOTPMFARequestEntity.getUsageType() != null && !initiateTOTPMFARequestEntity.getUsageType().equals("") &&
                    initiateTOTPMFARequestEntity.getUserDeviceId() != null && !initiateTOTPMFARequestEntity.getUserDeviceId().equals("") &&
                    baseurl != null && !baseurl.equals("")) {
                //Todo Service call
                TOTPVerificationService.getShared(context).initiateTOTP(baseurl,codeChallenge, initiateTOTPMFARequestEntity,null,
                        new Result<InitiateTOTPMFAResponseEntity>() {

                            @Override
                            public void success(final InitiateTOTPMFAResponseEntity serviceresult) {

                                Cidaas.usagePass ="";
                                new CountDownTimer(5000, 500) {
                                    String usagePassFromService="";
                                    public void onTick(long millisUntilFinished) {
                                        usagePassFromService= Cidaas.usagePass;

                                        Timber.e("");
                                        if(usagePassFromService!=null && !usagePassFromService.equals(""))
                                        {
                                            this.cancel();
                                            onFinish();
                                        }

                                    }
                                    public void onFinish() {
                                        if(usagePassFromService!=null && !usagePassFromService.equals("")) {
                                            inititateTOTPAfterDeviceVerification(usagePassFromService,baseurl,initiateTOTPMFARequestEntity.getSub(),clientId,requestId,trackId,initiateTOTPMFARequestEntity.getUsageType(),loginresult);

                                        }

                                        else {
                                            // return Error Message

                                            loginresult.failure(WebAuthError.getShared(context).deviceVerificationFailureException());
                                        }
                                    }
                                }.start();

                            }

                            @Override
                            public void failure(WebAuthError error) {
                                loginresult.failure(error);
                            }
                        });
            }
            else
            {

                loginresult.failure(WebAuthError.getShared(context).propertyMissingException("Usage Type or Userdeviceid or baseURL must not be empty"));
            }
        }
        catch (Exception e)
        {
            Timber.e(e.getMessage());
            loginresult.failure(WebAuthError.getShared(context).serviceException(WebAuthErrorCode.INITIATE_TOTP_MFA_FAILURE));
            LogFile.getShared(context).addRecordToLog(e.getMessage()+WebAuthErrorCode.INITIATE_TOTP_MFA_FAILURE);
        }
    }

    //Initiate TOTP After Device Verification
    private void inititateTOTPAfterDeviceVerification(String usagePassFromService, final String baseurl, final String sub, final String clientId, final String requestId, final String trackId, final String usageType, final Result<LoginCredentialsResponseEntity> loginresult) {
      try {
          //Todo call initiate
          final InitiateTOTPMFARequestEntity initiateTOTPMFARequestEntityWithUsagePass = new InitiateTOTPMFARequestEntity();
          initiateTOTPMFARequestEntityWithUsagePass.setUsagePass(usagePassFromService);

          final String userDeviceId = DBHelper.getShared().getUserDeviceId(baseurl);

          TOTPVerificationService.getShared(context).initiateTOTP(baseurl, codeChallenge, initiateTOTPMFARequestEntityWithUsagePass, null,
                  new Result<InitiateTOTPMFAResponseEntity>() {

                      @Override
                      public void success(InitiateTOTPMFAResponseEntity result) {
                          if (result.getData().getStatusId() != null &&
                                  !result.getData().getStatusId().equals("")) {


                              AuthenticateTOTPRequestEntity authenticateTOTPRequestEntity = new AuthenticateTOTPRequestEntity();
                              authenticateTOTPRequestEntity.setUserDeviceId(userDeviceId);
                              authenticateTOTPRequestEntity.setStatusId(result.getData().getStatusId());


                              String secretFromDB = DBHelper.getShared().getSecret(sub);
                              String totp = generateTOTP(secretFromDB).getTotp_string();

                              authenticateTOTPRequestEntity.setVerifierPassword(totp);
                              authenticateTOTPRequestEntity.setClient_id(clientId);


                              authenticateTOTP(baseurl, authenticateTOTPRequestEntity, new Result<AuthenticateTOTPResponseEntity>() {
                                  @Override
                                  public void success(AuthenticateTOTPResponseEntity result) {

                                      ResumeLogin.getShared(context).resumeLoginAfterSuccessfullAuthentication(result.getData().getSub(),result.getData().getTrackingCode(),
                                              AuthenticationType.totp,usageType,clientId,requestId,trackId,baseurl,loginresult);

                                  }

                                  @Override
                                  public void failure(WebAuthError error) {
                                      loginresult.failure(error);
                                  }
                              });
                          } else {
                              String errorMessage = "Status Id or TOTP Must not be null";
                              loginresult.failure(WebAuthError.getShared(context).customException(417, errorMessage, HttpStatusCode.EXPECTATION_FAILED));

                          }

                      }

                      @Override
                      public void failure(WebAuthError error) {
                          loginresult.failure(error);
                          //  Toast.makeText(context, "Error on validate Device" + error.getErrorMessage(), Toast.LENGTH_SHORT).show();
                      }
                  });
      }
      catch (Exception e)
      {
          Timber.e(e.getMessage());
          loginresult.failure(WebAuthError.getShared(context).serviceException(WebAuthErrorCode.INITIATE_TOTP_MFA_FAILURE));
          LogFile.getShared(context).addRecordToLog(e.getMessage()+WebAuthErrorCode.INITIATE_TOTP_MFA_FAILURE);
      }
    }

    //Authenticate TOTP

    public void authenticateTOTP(final String baseurl, final AuthenticateTOTPRequestEntity authenticateTOTPRequestEntity, final Result<AuthenticateTOTPResponseEntity> authResult)
    {
        try
        {
            TOTPVerificationService.getShared(context).authenticateTOTP(baseurl, authenticateTOTPRequestEntity,null, new Result<AuthenticateTOTPResponseEntity>() {
                @Override
                public void success(final AuthenticateTOTPResponseEntity serviceresult) {


                    Cidaas.usagePass = "";

                    //Timer
                    new CountDownTimer(5000, 500) {
                        String usagePassFromService = "";

                        public void onTick(long millisUntilFinished) {
                            usagePassFromService = Cidaas.usagePass;

                            Timber.e("");
                            if (usagePassFromService != null && !usagePassFromService.equals("")) {
                                this.cancel();
                                onFinish();
                            }

                        }

                        public void onFinish() {
                            if (usagePassFromService != null && !usagePassFromService.equals("")) {
                                AuthenticateTOTPRequestEntity authenticateTOTPRequestEntity=new AuthenticateTOTPRequestEntity();
                                authenticateTOTPRequestEntity.setUsage_pass(usagePassFromService);

                                TOTPVerificationService.getShared(context).authenticateTOTP(baseurl, authenticateTOTPRequestEntity,null, authResult);
                            }
                            else {
                                // return Error Message
                                authResult.failure(WebAuthError.getShared(context).deviceVerificationFailureException());
                            }

                        }
                    }.start();
                }

                @Override
                public void failure(WebAuthError error) {
                    authResult.failure(error);
                }
            });
        }
        catch (Exception e)
        {
            authResult.failure(WebAuthError.getShared(context).serviceException(WebAuthErrorCode.AUTHENTICATE_TOTP_MFA_FAILURE));

        }
    }

    public void ListenTOTP(String sub) {
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

    public void cancelTOTP() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

    }





   /* //setupTOTPMFA
    public void setupTOTPMFA(@NonNull String sub, @NonNull final Result<SetupTOTPMFAResponseEntity> result){
        try {
            String baseurl="";
            if(savedProperties==null){

                savedProperties= DBHelper.getShared().getLoginProperties();
            }
            if(savedProperties==null){
                //Read from file if localDB is null
                readFromFile(new Result<Dictionary<String, String>>() {
                    @Override
                    public void success(Dictionary<String, String> loginProperties) {
                        savedProperties=loginProperties;
                    }

                    @Override
                    public void failure(WebAuthError error) {
                        result.failure(error);
                    }
                });
            }

            if (savedProperties.get("DomainURL").equals("") || savedProperties.get("DomainURL") == null || savedProperties == null) {
                webAuthError = webAuthError.propertyMissingException();
                String loggerMessage = "Setup TOTP MFA readProperties failure : " + "Error Code - " + webAuthError.errorCode + ", Error Message - " + webAuthError.ErrorMessage
                        + ", Status Code - " + webAuthError.statusCode;
                LogFile.addRecordToLog(loggerMessage);
                result.failure(webAuthError);
            } if (savedProperties.get("ClientId").equals("") || savedProperties.get("ClientId") == null || savedProperties == null) {
                webAuthError = webAuthError.propertyMissingException();
                String loggerMessage = "Accept Consent readProperties failure : " + "Error Code - " + webAuthError.errorCode + ", Error Message - " + webAuthError.ErrorMessage
                        + ", Status Code - " + webAuthError.statusCode;
                LogFile.addRecordToLog(loggerMessage);
                result.failure(webAuthError);
            }
            else {
                baseurl = savedProperties.get("DomainURL");

                if ( sub != null && !sub.equals("") && baseurl != null && !baseurl.equals("")) {

                    final String finalBaseurl = baseurl;
                    getAccessToken(sub, new Result<AccessTokenEntity>() {
                        @Override
                        public void success(AccessTokenEntity accesstokenresult) {

                            String logoUrl= "https://docs.cidaas.de/assets/logoss.png";



                            SetupTOTPMFARequestEntity setupTOTPMFARequestEntity=new SetupTOTPMFARequestEntity();
                            setupTOTPMFARequestEntity.setClient_id( savedProperties.get("ClientId"));
                            setupTOTPMFARequestEntity.setLogoUrl(logoUrl);


                            setupTOTPMFAService(accesstokenresult.getAccess_token(), finalBaseurl,setupTOTPMFARequestEntity,result);
                        }

                        @Override
                        public void failure(WebAuthError error) {
                            result.failure(error);
                        }
                    });


                }

            }


        }
        catch (Exception e)
        {
            LogFile.addRecordToLog("acceptConsent exception"+e.getMessage());
            Timber.e("acceptConsent exception"+e.getMessage());
        }
    }

    //Service call To SetupTOTPMFA
    private void setupTOTPMFAService(@NonNull final String AccessToken, @NonNull String baseurl,
                                     @NonNull SetupTOTPMFARequestEntity setupTOTPMFARequestEntity,
                                     @NonNull final Result<SetupTOTPMFAResponseEntity> result)
    {
        try{

            if (baseurl != null && !baseurl.equals("") && AccessToken != null && !AccessToken.equals("")) {
                //Todo Service call
                OauthService.getShared(context).setupTOTPMFA(baseurl, AccessToken,codeChallenge,setupTOTPMFARequestEntity,
                        new Result<SetupTOTPMFAResponseEntity>() {
                            @Override
                            public void success(final SetupTOTPMFAResponseEntity serviceresult) {

                                new CountDownTimer(5000, 500) {
                                    String usagePassFromService="";
                                    public void onTick(long millisUntilFinished) {
                                        usagePassFromService=getInstanceId();
                                        if(usagePassFromService!=null && usagePassFromService!="")
                                        {
                                            onFinish();
                                        }

                                    }

                                    public void onFinish() {
                                        if(usagePassFromService!=null && usagePassFromService!="")
                                        {
                                            //Todo Call Next Service cALL TO Validate DEVICE
                                            validateDevice(usagePassFromService, serviceresult.getData().getStatusId(), new Result<ValidateDeviceResponseEntity>() {
                                                @Override
                                                public void success(ValidateDeviceResponseEntity result) {
                                                    //Todo call Next service
                                                    scannedTOTP(result.getData().getUsage_pass(), serviceresult.getData().getStatusId(), AccessToken,null,new Result<ScannedResponseEntity>() {
                                                        @Override
                                                        public void success(ScannedResponseEntity result) {
                                                            Timber.i(result.getData().getUserDeviceId()+"USewr Device id");
                                                            Toast.makeText(context, result.getData().getUserDeviceId()+"USewr Device id", Toast.LENGTH_SHORT).show();
                                                        }

                                                        @Override
                                                        public void failure(WebAuthError error) {
                                                            Toast.makeText(context, "Error on Scanned"+error.getErrorMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void failure(WebAuthError error) {
                                                    Toast.makeText(context, "Error on validate Device"+error.getErrorMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                        else {
                                            // return Error Message
                                            webAuthError=WebAuthError.getShared(context).deviceVerificationFailureException();
                                            result.failure(webAuthError);
                                        }
                                    }
                                }.start();


                            }

                            @Override
                            public void failure(WebAuthError error) {
                                result.failure(error);
                            }
                        });
            }
            else
            {
                webAuthError=webAuthError.propertyMissingException();
                webAuthError.ErrorMessage="one of the Login properties missing";
                result.failure(webAuthError);
            }
        }
        catch (Exception e)
        {
            Timber.e(e.getMessage());
        }
    }

    //Scanned TOTP
    private void scannedTOTP(@NonNull String usagePass,@NonNull String statusId,@NonNull String AccessToken, @NonNull final Result<ScannedResponseEntity> result)
    {
        try {
            String baseurl="";
            if(savedProperties==null){

                savedProperties=DBHelper.getShared().getLoginProperties();
            }
            if(savedProperties==null){
                //Read from file if localDB is null
                readFromFile(new Result<Dictionary<String, String>>() {
                    @Override
                    public void success(Dictionary<String, String> loginProperties) {
                        savedProperties=loginProperties;
                    }

                    @Override
                    public void failure(WebAuthError error) {
                        result.failure(error);
                    }
                });
            }



            if (savedProperties.get("DomainURL").equals("") || savedProperties.get("DomainURL") == null || savedProperties == null) {
                webAuthError = webAuthError.propertyMissingException();
                String loggerMessage = "Setup TOTP MFA readProperties failure : " + "Error Code - " + webAuthError.errorCode + ", Error Message - " + webAuthError.ErrorMessage
                        + ", Status Code - " + webAuthError.statusCode;
                LogFile.addRecordToLog(loggerMessage);
                result.failure(webAuthError);
            }
            if (savedProperties.get("ClientId").equals("") || savedProperties.get("ClientId") == null || savedProperties == null) {
                webAuthError = webAuthError.propertyMissingException();
                String loggerMessage = "Accept Consent readProperties failure : " + "Error Code - " + webAuthError.errorCode + ", Error Message - " + webAuthError.ErrorMessage
                        + ", Status Code - " + webAuthError.statusCode;
                LogFile.addRecordToLog(loggerMessage);
                result.failure(webAuthError);
            }
            else {
                baseurl = savedProperties.get("DomainURL");

                if ( statusId != null && !statusId.equals("") && usagePass != null && !usagePass.equals("") && baseurl != null
                        && !baseurl.equals("")) {

                    scannedTOTPService(usagePass, baseurl,statusId,AccessToken,result);


                }

            }

        }
        catch (Exception e)
        {
            LogFile.addRecordToLog("acceptConsent exception"+e.getMessage());
            Timber.e("acceptConsent exception"+e.getMessage());
        }
    }

    //Service call To Scanned TOTP Service
    private void scannedTOTPService(@NonNull String usagePass,@NonNull String baseurl,
                                    @NonNull String statusId,@NonNull String AccessToken,
                                    @NonNull final Result<ScannedResponseEntity> scannedResponseResult)
    {
        try{

            if ( statusId != null && !statusId.equals("") && usagePass != null && !usagePass.equals("") && baseurl != null
                    && !baseurl.equals("")) {
                //Todo Service call

                if(codeChallenge==null){
                    generateChallenge();
                }
                OauthService.getShared(context).scannedTOTP(baseurl, usagePass, statusId,AccessToken,
                        new Result<ScannedResponseEntity>() {
                            @Override
                            public void success(final ScannedResponseEntity serviceresult) {
                                //Todo Call Scanned Service


                                scannedResponseResult.success(serviceresult);
                            }

                            @Override
                            public void failure(WebAuthError error) {
                                scannedResponseResult.failure(error);
                            }
                        });
            }
            else
            {
                webAuthError=webAuthError.propertyMissingException();
                webAuthError.ErrorMessage="one of the Login properties missing";
                scannedResponseResult.failure(webAuthError);
            }
        }
        catch (Exception e)
        {
            Timber.e(e.getMessage());
        }
    }

    //enrollTOTPMFA
    public void enrollTOTPMFA(@NonNull final EnrollTOTPMFARequestEntity enrollTOTPMFARequestEntity, @NonNull final Result<EnrollTOTPMFAResponseEntity> result){
        try {
            String baseurl="";
            if(savedProperties==null){

                savedProperties=DBHelper.getShared().getLoginProperties();
            }
            if(savedProperties==null){
                //Read from file if localDB is null
                readFromFile(new Result<Dictionary<String, String>>() {
                    @Override
                    public void success(Dictionary<String, String> loginProperties) {
                        savedProperties=loginProperties;
                    }

                    @Override
                    public void failure(WebAuthError error) {
                        result.failure(error);
                    }
                });
            }

            if (savedProperties.get("DomainURL").equals("") || savedProperties.get("DomainURL") == null || savedProperties == null) {
                webAuthError = webAuthError.propertyMissingException();
                String loggerMessage = "Setup TOTP MFA readProperties failure : " + "Error Code - " + webAuthError.errorCode + ", Error Message - " + webAuthError.ErrorMessage
                        + ", Status Code - " + webAuthError.statusCode;
                LogFile.addRecordToLog(loggerMessage);
                result.failure(webAuthError);
            } else {
                baseurl = savedProperties.get("DomainURL");

                if ( enrollTOTPMFARequestEntity.getVerifierPassword() != null && !enrollTOTPMFARequestEntity.getVerifierPassword().equals("") &&
                        enrollTOTPMFARequestEntity.getSub() != null && enrollTOTPMFARequestEntity.getSub()  != null &&
                        enrollTOTPMFARequestEntity.getStatusId() != null && enrollTOTPMFARequestEntity.getStatusId()  != null &&
                        baseurl != null && !baseurl.equals("")) {

                    final String finalBaseurl = baseurl;
                    getAccessToken(enrollTOTPMFARequestEntity.getSub(), new Result<AccessTokenEntity>() {
                        @Override
                        public void success(AccessTokenEntity accesstokenresult) {
                            enrollTOTPMFAService(accesstokenresult.getAccess_token(), finalBaseurl,enrollTOTPMFARequestEntity,result);
                        }

                        @Override
                        public void failure(WebAuthError error) {
                            result.failure(error);
                        }
                    });


                }
                else {
                    webAuthError=webAuthError.propertyMissingException();
                    webAuthError.ErrorMessage="one of the Login properties missing";
                    result.failure(webAuthError);
                }

            }

        }
        catch (Exception e)
        {
            LogFile.addRecordToLog("acceptConsent exception"+e.getMessage());
            Timber.e("acceptConsent exception"+e.getMessage());
        }
    }

    //Service call To enrollTOTPMFA
    private void enrollTOTPMFAService(@NonNull String AccessToken, @NonNull String baseurl,
                                      @NonNull final EnrollTOTPMFARequestEntity enrollTOTPMFARequestEntity, @NonNull final Result<EnrollTOTPMFAResponseEntity> result){
        try{

            if (enrollTOTPMFARequestEntity.getVerifierPassword() != null && !enrollTOTPMFARequestEntity.getVerifierPassword().equals("") &&
                    enrollTOTPMFARequestEntity.getSub() != null && enrollTOTPMFARequestEntity.getSub()  != null &&
                    enrollTOTPMFARequestEntity.getStatusId() != null && enrollTOTPMFARequestEntity.getStatusId()  != null &&
                    baseurl != null && !baseurl.equals("") && AccessToken != null && !AccessToken.equals("")) {
                //Todo Service call
                OauthService.getShared(context).enrollTOTPMFA(baseurl, AccessToken, enrollTOTPMFARequestEntity,null,new Result<EnrollTOTPMFAResponseEntity>() {
                    @Override
                    public void success(EnrollTOTPMFAResponseEntity serviceresult) {
                        result.success(serviceresult);
                    }

                    @Override
                    public void failure(WebAuthError error) {
                        result.failure(error);
                    }
                });
            }
            else
            {
                webAuthError=webAuthError.propertyMissingException();
                webAuthError.ErrorMessage="one of the Login properties missing";
                result.failure(webAuthError);
            }
        }
        catch (Exception e)
        {
            Timber.e(e.getMessage());
        }
    }

*/}
