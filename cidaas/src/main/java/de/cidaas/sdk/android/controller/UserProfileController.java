package de.cidaas.sdk.android.controller;

import android.content.Context;

import java.util.Dictionary;

import de.cidaas.sdk.android.helper.enums.EventResult;
import de.cidaas.sdk.android.helper.enums.WebAuthErrorCode;
import de.cidaas.sdk.android.helper.extension.WebAuthError;
import de.cidaas.sdk.android.properties.CidaasProperties;
import de.cidaas.sdk.android.service.entity.UserinfoEntity;
import de.cidaas.sdk.android.service.entity.accesstoken.AccessTokenEntity;
import de.cidaas.sdk.android.service.repository.OauthService;
import timber.log.Timber;

public class UserProfileController {


    private Context context;

    public static UserProfileController shared;

    public UserProfileController(Context contextFromCidaas) {

        context = contextFromCidaas;

    }

    public static UserProfileController getShared(Context contextFromCidaas) {
        try {

            if (shared == null) {
                shared = new UserProfileController(contextFromCidaas);
            }
        } catch (Exception e) {
            Timber.i(e.getMessage());
        }
        return shared;
    }


    public void getUserProfile(String sub, final EventResult<UserinfoEntity> callback) {
        String methodName = "UserProfileController :getUserProfile()";
        try {
            if (sub != null && !sub.equals("")) {

                AccessTokenController.getShared(context).getAccessToken(sub, new EventResult<AccessTokenEntity>() {
                    @Override
                    public void success(final AccessTokenEntity accessTokenresult) {
                        CidaasProperties.getShared(context).checkCidaasProperties(new EventResult<Dictionary<String, String>>() {
                            @Override
                            public void success(Dictionary<String, String> result) {
                                OauthService.getShared(context).getUserinfo(accessTokenresult.getAccess_token(), result.get("DomainURL"), callback);
                            }

                            @Override
                            public void failure(WebAuthError error) {
                                callback.failure(error);
                            }
                        });

                    }

                    @Override
                    public void failure(WebAuthError error) {
                        callback.failure(error);
                    }
                });

            } else {
                String errorMessage = "Sub must not be null";
                callback.failure(WebAuthError.getShared(context).propertyMissingException(errorMessage, "Error:" + methodName));
            }

        } catch (Exception e) {
            callback.failure(WebAuthError.getShared(context).methodException("Exception :" + methodName, WebAuthErrorCode.USER_INFO_SERVICE_FAILURE, e.getMessage()));
        }
    }


    public void getUserConfigurationList() {
        try {

        } catch (Exception e) {

        }
    }

}

   /*//Get Internal userProfile
    public void getInternalUserProfile(@NonNull String AccessToken, @NonNull String sub, @NonNull finalEventResult<UserprofileResponseEntity> result){
        try {
            String baseurl="";
            if(savedProperties==null){

                savedProperties= DBHelper.getShared().getLoginProperties();
            }
            if(savedProperties==null){
                //Read from file if localDB is null
                readFromFile(new EventResult<Dictionary<String, String>>() {
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
                String loggerMessage = "Verify Email MFA readProperties failure : " + "Error Code - " + webAuthError.errorCode + ", Error Message - " + webAuthError.ErrorMessage
                        + ", Status Code - " + webAuthError.statusCode;
                LogFile.addFailureLog(loggerMessage);
                result.failure(webAuthError);
            } else {
                baseurl = savedProperties.get("DomainURL");
                getInternalUserProfileService(baseurl,AccessToken,sub,result);
            }

        }
        catch (Exception e)
        {
            LogFile.addFailureLog("acceptConsent exception"+e.getMessage());
            Timber.e("acceptConsent exception"+e.getMessage());
        }
    }

    //Service call To get InternalUserProfile
    private void getInternalUserProfileService(@NonNull String baseurl, @NonNull String AccessToken,@NonNull String sub,finalEventResult<UserprofileResponseEntity> result){
        try{

            if (baseurl != null && !baseurl.equals("") && sub != null && !sub.equals("")) {
                // Change service call to private
                OauthService.getShared(context).getInternalUserProfileInfo(baseurl,AccessToken,sub, new EventResult<UserprofileResponseEntity>() {

                    @Override
                    public void success(UserprofileResponseEntity serviceresult) {
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
                webAuthError.ErrorMessage="one of the  get Tenant Info properties missing";
                result.failure(webAuthError);
            }
        }
        catch (Exception e)
        {
            Timber.e(e.getMessage());
        }
    }

*/