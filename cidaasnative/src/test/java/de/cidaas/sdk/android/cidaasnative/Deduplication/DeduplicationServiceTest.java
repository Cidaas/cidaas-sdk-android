package de.cidaas.sdk.android.cidaasnative.Deduplication;

import android.content.Context;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import de.cidaas.sdk.android.cidaasnative.data.entity.deduplication.DeduplicationResponseEntity;
import de.cidaas.sdk.android.cidaasnative.data.entity.deduplication.registerdeduplication.RegisterDeduplicationEntity;
import de.cidaas.sdk.android.cidaasnative.domain.service.Deduplication.DeduplicationService;
import de.cidaas.sdk.android.helper.enums.EventResult;
import de.cidaas.sdk.android.helper.extension.WebAuthError;


@RunWith(RobolectricTestRunner.class)
@Ignore
public class DeduplicationServiceTest {

    Context context;
    DeduplicationService deduplicationService;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.application;
        deduplicationService = new DeduplicationService(context);
    }

    @Test
    public void testGetShared() throws Exception {
        DeduplicationService result = DeduplicationService.getShared(null);
        Assert.assertTrue(result instanceof DeduplicationService);
    }

    @Test
    public void testGetDeduplicationList() throws Exception {

        deduplicationService.getDeduplicationList("baseurl", "trackId", null);
    }

    @Test
    public void testGetDeduplicationListnul() throws Exception {

        deduplicationService.getDeduplicationList("", "trackId", new EventResult<DeduplicationResponseEntity>() {
            @Override
            public void success(DeduplicationResponseEntity result) {

            }

            @Override
            public void failure(WebAuthError error) {

            }
        });
    }

    @Test
    public void testRegisterDeduplication() throws Exception {

        deduplicationService.registerDeduplication("baseurl", "trackId", new EventResult<RegisterDeduplicationEntity>() {
            @Override
            public void success(RegisterDeduplicationEntity result) {

            }

            @Override
            public void failure(WebAuthError error) {

            }
        });
    }

    @Test
    public void testRegisterDeduplicationNull() throws Exception {

        deduplicationService.registerDeduplication("", "trackId", new EventResult<RegisterDeduplicationEntity>() {
            @Override
            public void success(RegisterDeduplicationEntity result) {

            }

            @Override
            public void failure(WebAuthError error) {

            }
        });
    }

    @Test
    public void testLoginDeduplication() throws Exception {

        deduplicationService.registerDeduplication("baseurl", null, null);
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme