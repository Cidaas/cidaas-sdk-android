package de.cidaas.sdk.android.Service.Entity.MFA.AuthenticateMFA.Email;

import org.junit.Test;

import de.cidaas.sdk.android.service.entity.mfa.AuthenticateMFA.Email.AuthenticateEmailRequestDevice;

import static junit.framework.TestCase.assertTrue;

public class AuthenticateEmailRequestDeviceTest {
    @Test
    public void getDeviceId() {

        AuthenticateEmailRequestDevice authenticateEmailRequestDevice = new AuthenticateEmailRequestDevice();
        authenticateEmailRequestDevice.setDeviceId("Device ID");
        assertTrue(authenticateEmailRequestDevice.getDeviceId() == "Device ID");

    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme