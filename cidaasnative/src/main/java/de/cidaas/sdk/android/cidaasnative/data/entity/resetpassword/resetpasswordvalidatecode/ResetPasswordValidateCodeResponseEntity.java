package de.cidaas.sdk.android.cidaasnative.data.entity.resetpassword.resetpasswordvalidatecode;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResetPasswordValidateCodeResponseEntity implements Serializable {
    int status;
    boolean success;
    ResetPasswordValidateCodeDataEntity data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public ResetPasswordValidateCodeDataEntity getData() {
        return data;
    }

    public void setData(ResetPasswordValidateCodeDataEntity data) {
        this.data = data;
    }
}
