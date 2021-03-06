package de.cidaas.sdk.android.consent.domain.service;

import android.content.Context;

import de.cidaas.sdk.android.consent.data.service.ICidaasConsentSDKService;
import de.cidaas.sdk.android.helper.general.CidaasHelper;
import de.cidaas.sdk.android.service.CidaassdkService;
import okhttp3.OkHttpClient;

public class CidaasConsentSDKService {


    private static Context mcontext;

    public CidaasConsentSDKService(Context context) {
        setContext(context);
    }

    private void setContext(Context context) {
        mcontext = context;
    }

    //For Cidaas Core Service
    public ICidaasConsentSDKService getInstance() {

        String baseurl = CidaasHelper.baseurl;

        if (baseurl == null || baseurl.equals("")) {
            baseurl = "https://www.google.com";
        }

        CidaassdkService cidaassdkService = new CidaassdkService(mcontext);
        OkHttpClient okHttpClient = cidaassdkService.getOKHttpClient();

        ICidaasConsentSDKService iCidaasConsentSDKService = cidaassdkService.getRetrofit(baseurl, okHttpClient).create(ICidaasConsentSDKService.class);
        return iCidaasConsentSDKService;
    }


}
