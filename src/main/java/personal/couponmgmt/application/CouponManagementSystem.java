package personal.couponmgmt.application;

import personal.couponmgmt.domain.*;
import personal.couponmgmt.exception.CouponManagementException;

import java.sql.SQLOutput;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CouponManagementSystem {
    private Map<Integer, Batch> batchById;

    public CouponManagementSystem() {
        batchById = new ConcurrentHashMap<Integer, Batch>();
    }

    public void createBatch(Integer batchId, String batchType, Date startTime, Date endTime, Distributor distributor, CouponType couponType, double worth, Integer maxAllowedGrants) throws CouponManagementException {
        try {
            validateBatch(batchId, batchType, startTime, endTime, distributor, couponType, worth, maxAllowedGrants);
        } catch (CouponManagementException e) {
            //System.out.println("Error in batch creation : "+e.getErrorCode());
            throw new CouponManagementException("INVALID_BATCH","Invalid batch creation request received", e);
        }

        Batch batch = new Batch(batchId, batchType, startTime, endTime, distributor, couponType, worth, maxAllowedGrants);
        batchById.put(batchId, batch);
    }

    public void updateState(Integer batchId , BatchState state) throws CouponManagementException {
        var batch = batchById.get(batchId);
        try {
            batch.setState(state);
        } catch (CouponManagementException e) {
            throw new CouponManagementException("INVALID_TRANSITION", "Invalid batch state transition", e);
        }
    }

    public Batch getBatch(Integer batchId) {
        return batchById.get(batchId);
    }

    public void ingestCoupons(Integer batchId, Set<Coupon> couponSet) throws CouponManagementException {
        var batch = batchById.get(batchId);
        if(batch==null) {
            throw new CouponManagementException("INVALID_BATCH_ID","batch does not exist");

        }
        batch.ingestCoupons(couponSet);
    }

    public Coupon grantCoupon(Integer batchId) throws CouponManagementException {
        var batch = batchById.get(batchId);
        if(batch==null) {
            throw new CouponManagementException("INVALID_BATCH_ID","batch does not exist");
        }
        return batch.grantCoupon();
    }

    public Coupon getCoupon(Integer couponId) {
        for(Batch batch : batchById.values()) {
            return batch.getCoupons().get(couponId);
        }
        return null;
    }

    public Integer couponCount(Integer batchId) throws CouponManagementException {
        var batch = batchById.get(batchId);
        if(batch==null) {
            throw new CouponManagementException("INVALID_BATCH_ID","batch does not exist");
        }
        return batch.getCouponCount();
    }

    private void validateBatch(Integer batchId, String batchType, Date startTime, Date endTime, Distributor distributor, CouponType couponType, double worth, Integer maxAllowedGrants) throws CouponManagementException {
        if(batchById.get(batchId)!=null) {
            throw new CouponManagementException("BATCH_ALREADY_CREATED","batch with batch id : "+batchId+" is already created ");
        }
        if(batchType==null) {
            throw new CouponManagementException("EMPTY_BATCH_TYPE", "batch type not specified");
        }
        if(startTime.after(endTime)) {
            throw new CouponManagementException("INVALID_PERIOD","start time should be before end time");
        }
        if(endTime.before(Calendar.getInstance().getTime())) {
            throw new CouponManagementException("INVALID_PERIOD", "end time cant be in past");
        }
        if(distributor==null || distributor.getName().isEmpty()) {
            throw new CouponManagementException("INVALID_DISTRIBUTOR", "distributor has to be specified");
        }
        if(couponType==null) {
            throw new CouponManagementException("INVALID_COUPON_TYPE","specify a coupon type");
        }
        if(worth<=0) {
            throw new CouponManagementException("INVALID_WORTH","worth has to be a positive number");
        }
        if(couponType==CouponType.OPEN && maxAllowedGrants==null) {
            throw new CouponManagementException("INVALID_MAXGRANTSALLOWED","max grants allowed value cant be empty for OPEN coupon type");
        }

    }
}
