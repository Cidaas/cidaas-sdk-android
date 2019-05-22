package com.example.cidaasv2.VerificationV2.data.Entity.Setup;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SetupEntity implements Serializable {

    String sub="";
    String verificationType="";

    //Constructors
    public SetupEntity() {
    }

    //Mandatory Fields
    public SetupEntity(String sub, String verificationType) {
        this.sub = sub;
        this.verificationType = verificationType;
    }



    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getVerificationType() {
        return verificationType;
    }

    public void setVerificationType(String verificationType) {
        this.verificationType = verificationType;
    }
}
