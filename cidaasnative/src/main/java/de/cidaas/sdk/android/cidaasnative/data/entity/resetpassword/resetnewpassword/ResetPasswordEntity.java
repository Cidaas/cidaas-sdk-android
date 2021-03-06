package de.cidaas.sdk.android.cidaasnative.data.entity.resetpassword.resetnewpassword;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResetPasswordEntity implements Serializable {

    String exchangeId;
    String resetRequestId;
    String password;
    String confirmPassword;

    public String getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(String exchangeId) {
        this.exchangeId = exchangeId;
    }

    public String getResetRequestId() {
        return resetRequestId;
    }

    public void setResetRequestId(String resetRequestId) {
        this.resetRequestId = resetRequestId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
