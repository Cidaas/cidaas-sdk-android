package de.cidaas.cidaasv2.Service.Register.RegistrationSetup;

import junit.framework.Assert;

import org.junit.Test;

public class RegistrationSetupAttributesEntityTest {
    RegistrationSetupAttributesEntity registrationSetupAttributesEntity = new RegistrationSetupAttributesEntity();

    @Test
    public void setKey() {
        registrationSetupAttributesEntity.setKey("TestData");
        Assert.assertEquals("TestData", registrationSetupAttributesEntity.getKey());
    }

    @Test
    public void Value() {
        registrationSetupAttributesEntity.setValue("TestData");
        Assert.assertEquals("TestData", registrationSetupAttributesEntity.getValue());
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme