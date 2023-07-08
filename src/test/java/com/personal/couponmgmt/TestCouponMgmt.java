package com.personal.couponmgmt;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import personal.couponmgmt.application.CouponManagementSystem;
import personal.couponmgmt.domain.Batch;
import personal.couponmgmt.domain.BatchState;
import personal.couponmgmt.domain.CouponType;
import personal.couponmgmt.domain.Distributor;
import personal.couponmgmt.exception.CouponManagementException;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore("jdk.internal.reflect.*")
public class TestCouponMgmt {

    @InjectMocks
    CouponManagementSystem controller;

    @Mock
    Batch batch;

    @Test
    public void testCreateBatchAndUpdate() throws CouponManagementException {

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        Date startTime = cal.getTime();
        cal.add(Calendar.DATE, 3);
        Date endTime = cal.getTime();
        Distributor distributor = new Distributor(1, "disBatch1");
        controller.createBatch(1, "batchtype1", startTime, endTime, distributor, CouponType.OPEN, 100, 1000);
        Assert.assertEquals(controller.getBatch(1).getState(),BatchState.CREATED);
        controller.updateState(1, BatchState.APPROVED);
        Assert.assertEquals(controller.getBatch(1).getState(),BatchState.APPROVED);
    }
}
