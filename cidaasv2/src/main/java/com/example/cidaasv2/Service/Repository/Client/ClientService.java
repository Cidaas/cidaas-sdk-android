package com.example.cidaasv2.Service.Repository.Client;

import android.content.Context;

import com.example.cidaasv2.Helper.Entity.CommonErrorEntity;
import com.example.cidaasv2.Helper.Entity.ErrorEntity;
import com.example.cidaasv2.Helper.Enums.HttpStatusCode;
import com.example.cidaasv2.Helper.Enums.Result;
import com.example.cidaasv2.Helper.Enums.WebAuthErrorCode;
import com.example.cidaasv2.Helper.Extension.WebAuthError;
import com.example.cidaasv2.Helper.URLHelper.URLHelper;
import com.example.cidaasv2.Library.LocationLibrary.LocationDetails;
import com.example.cidaasv2.R;
import com.example.cidaasv2.Service.CidaassdkService;
import com.example.cidaasv2.Service.Entity.ClientInfo.ClientInfoEntity;
import com.example.cidaasv2.Service.ICidaasSDKService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class ClientService {
    //Get Client info
    CidaassdkService service;
    private ObjectMapper objectMapper=new ObjectMapper();
    //Local variables

    private Context context;

    public static  ClientService shared;

    public  ClientService(Context contextFromCidaas) {

        if(service==null) {
            service=new CidaassdkService();
        }
        context=contextFromCidaas;

        //Todo setValue for authenticationType

    }


    public static  ClientService getShared(Context contextFromCidaas )
    {
        try {

            if (shared == null) {
                shared = new  ClientService(contextFromCidaas);
            }
        }
        catch (Exception e)
        {
           // Timber.i(e.getMessage());
        }
        return shared;
    }

    public void getClientInfo(String requestId, String baseurl, final Result<ClientInfoEntity> callback)
    {
        //Local Variables
        String clienttUrl = "";
        try{

            if(baseurl!=null && baseurl!=""){
                //Construct URL For RequestId
                if(requestId!=null && requestId!=""){
                    //Construct URL For RequestId

                    clienttUrl=baseurl+ URLHelper.getShared().getClientUrl(requestId);
                }
                else {
                    callback.failure( WebAuthError.getShared(context).serviceFailureException(WebAuthErrorCode.REQUEST_ID_MISSING,context.getString(R.string.REQUEST_ID_MISSING),
                            400,null,null));
                    return;
                }
            }
            else {
                callback.failure( WebAuthError.getShared(context).serviceFailureException(WebAuthErrorCode.PROPERTY_MISSING,context.getString(R.string.PROPERTY_MISSING),
                        400,null,null));
                return;
            }

            Map<String, String> headers = new Hashtable<>();
            headers.put("lat", LocationDetails.getShared(context).getLatitude());
            headers.put("long",LocationDetails.getShared(context).getLongitude());

            //Call Service-getRequestId
            ICidaasSDKService cidaasSDKService = service.getInstance();
            cidaasSDKService.getClientInfo(clienttUrl).enqueue(new Callback<ClientInfoEntity>() {
                @Override
                public void onResponse(Call<ClientInfoEntity> call, Response<ClientInfoEntity> response) {
                    if (response.isSuccessful()) {
                        if(response.code()==200) {
                            callback.success(response.body());
                        }
                        else {
                            callback.failure( WebAuthError.getShared(context).serviceFailureException(WebAuthErrorCode.CLIENT_INFO_FAILURE,
                                    "Service failure but successful response" , 400,null,null));
                        }
                    }
                    else {
                        assert response.errorBody() != null;
                        try {

                            //Todo Handle proper error message
                            String errorResponse=response.errorBody().source().readByteString().utf8();

                            CommonErrorEntity commonErrorEntity;
                            commonErrorEntity=objectMapper.readValue(errorResponse,CommonErrorEntity.class);


                            String errorMessage="";
                            ErrorEntity errorEntity=new ErrorEntity();
                            if(commonErrorEntity.getError()!=null && !commonErrorEntity.getError().toString().equals("") && commonErrorEntity.getError() instanceof  String) {
                                errorMessage=commonErrorEntity.getError().toString();
                            }
                            else
                            {
                                errorMessage = ((LinkedHashMap) commonErrorEntity.getError()).get("error").toString();
                                errorEntity.setCode(((LinkedHashMap) commonErrorEntity.getError()).get("code").toString());
                                errorEntity.setError( ((LinkedHashMap) commonErrorEntity.getError()).get("error").toString());
                                errorEntity.setMoreInfo( ((LinkedHashMap) commonErrorEntity.getError()).get("moreInfo").toString());
                                errorEntity.setReferenceNumber( ((LinkedHashMap) commonErrorEntity.getError()).get("referenceNumber").toString());
                                errorEntity.setStatus((Integer) ((LinkedHashMap) commonErrorEntity.getError()).get("status"));
                                errorEntity.setType( ((LinkedHashMap) commonErrorEntity.getError()).get("type").toString());
                            }



                            callback.failure( WebAuthError.getShared(context).serviceFailureException(WebAuthErrorCode.CLIENT_INFO_FAILURE,errorMessage,
                                    commonErrorEntity.getStatus(),commonErrorEntity.getError(),errorEntity));
                        } catch (Exception e) {
                            e.printStackTrace();
                            callback.failure(WebAuthError.getShared(context).customException(WebAuthErrorCode.CLIENT_INFO_FAILURE,
                                    "ClientInfo Failure Exception:"+ e.getMessage(), HttpStatusCode.EXPECTATION_FAILED));
                        }
                        Timber.e("response"+response.message());
                    }
                }

                @Override
                public void onFailure(Call<ClientInfoEntity> call, Throwable t) {
                    Timber.e("Faliure in Request id service call"+t.getMessage());
                    callback.failure( WebAuthError.getShared(context).serviceFailureException(WebAuthErrorCode.CLIENT_INFO_FAILURE,t.getMessage(), 400,null,null));

                }
            });
        }
        catch (Exception e)
        {
            Timber.d(e.getMessage());callback.failure(WebAuthError.getShared(context).propertyMissingException());
        }
    }


}
