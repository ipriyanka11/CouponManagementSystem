package personal.couponmgmt.domain;

import personal.couponmgmt.exception.CouponManagementException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Batch {
    private Integer id;
    private String type;
    private BatchState state;
    private Date startTime;
    private Date endTime;
    private Distributor distributor;
    private CouponType couponType;
    private double worth;
    private Integer maxAllowedGrants;
    private Map<Integer, Coupon> coupons;

    public Batch(Integer id, String type, Date startTime, Date endTime, Distributor distributor, CouponType couponType, double worth, Integer maxAllowedGrants) {
        this.id = id;
        this.type = type;
        this.startTime = startTime;
        this.endTime = endTime;
        this.distributor = distributor;
        this.couponType = couponType;
        this.worth = worth;
        this.maxAllowedGrants = maxAllowedGrants;
        this.state = BatchState.CREATED;
        this.coupons = new HashMap<Integer, Coupon>();
    }

    public void ingestCoupons(Set<Coupon> couponSet) throws CouponManagementException {
        if(couponSet==null || couponSet.isEmpty()) {
            throw new CouponManagementException("INVALID_REQUEST","no coupons specified to ingest");
        }
        if(this.couponType==CouponType.OPEN) {
            if(couponSet.size()>1) {
                throw new CouponManagementException("INVALID_REQUEST","Single Coupon code to be specified for OPEN Coupon type");
            }
            synchronized (this) {
                coupons = new HashMap<Integer, Coupon>();
                var c = couponSet.iterator().next();
                for(int i=0;i<maxAllowedGrants;i++) {
                    coupons.put(i,c );
                }
            }

        } else {
            synchronized (this) {

                for(Coupon c : couponSet) {
                    coupons.put(c.getId(),c);
                }
            }
        }

    }

    public Coupon grantCoupon() throws CouponManagementException {

        Coupon res = null;
        if(this.getEndTime().before(Calendar.getInstance().getTime())) {
            setState(BatchState.EXPIRED);
        }
        if(this.getState()!=BatchState.ACTIVE) {
            throw new CouponManagementException("BATCH_NOT_ACTIVE","batch is not active for granting coupons");
        }
        if(this.coupons.isEmpty()) {
            throw new CouponManagementException("BATCH_EXHAUSTED", "batch empty - cant grant coupons");
        }
        if(this.getStartTime().after(Calendar.getInstance().getTime())) {
            throw new CouponManagementException("NATCH_NOT_STARTED","start time not yet reached = please come back later for coupons ");
        }
        synchronized (this.coupons) {
            res = coupons.entrySet().iterator().next().getValue();
            coupons.remove(res.getId());
            System.out.println("removed coupon with id : "+res.getId()+" batch : "+id);
        }
        return res;

    }

    public Integer getCouponCount() {
        return this.getCoupons().size();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BatchState getState() {
        return state;
    }

    public void setState(BatchState state) throws CouponManagementException {

        if(state.allowedFrom(this.state))
            this.state = state;
        else {
            throw new CouponManagementException("INVALID_TRANSITION", "state transition from "+this.state+" to "+state+" not allowed");
        }

    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Distributor getDistributor() {
        return distributor;
    }

    public void setDistributor(Distributor distributor) {
        this.distributor = distributor;
    }

    public CouponType getCouponType() {
        return couponType;
    }

    public void setCouponType(CouponType couponType) {
        this.couponType = couponType;
    }

    public double getWorth() {
        return worth;
    }

    public void setWorth(double worth) {
        this.worth = worth;
    }

    public Integer getMaxAllowedGrants() {
        return maxAllowedGrants;
    }

    public void setMaxAllowedGrants(Integer maxAllowedGrants) {
        this.maxAllowedGrants = maxAllowedGrants;
    }

    public Map<Integer, Coupon> getCoupons() {
        return coupons;
    }

    @Override
    public String toString() {
        return "Batch{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", state=" + state +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", distributor=" + distributor +
                ", couponType=" + couponType +
                ", worth=" + worth +
                ", maxAllowedGrants=" + maxAllowedGrants +
                ", couponsNumber=" + coupons.size() +
                '}';
    }
}
