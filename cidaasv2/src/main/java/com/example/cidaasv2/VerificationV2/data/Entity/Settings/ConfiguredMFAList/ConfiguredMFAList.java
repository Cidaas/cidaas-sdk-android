package com.example.cidaasv2.VerificationV2.data.Entity.Settings.ConfiguredMFAList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfiguredMFAList implements Serializable {
    boolean success;
    int status;
    GetMFAListResponseData data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public GetMFAListResponseData getData() {
        return data;
    }

    public void setData(GetMFAListResponseData data) {
        this.data = data;
    }
}
