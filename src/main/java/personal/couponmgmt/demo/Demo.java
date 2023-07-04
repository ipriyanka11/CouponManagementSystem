package personal.couponmgmt.demo;

import personal.couponmgmt.application.CouponManagementSystem;
import personal.couponmgmt.domain.BatchState;
import personal.couponmgmt.domain.Coupon;
import personal.couponmgmt.domain.CouponType;
import personal.couponmgmt.domain.Distributor;
import personal.couponmgmt.exception.CouponManagementException;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Demo {

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String args[]) throws ExecutionException, InterruptedException, CouponManagementException {
        System.out.println("Welcome to Coupon Management System, creating 3 batches with ids 1 , 2 & 3 ");
        CouponManagementSystem app = new CouponManagementSystem();
        ExecutorService executorService = Executors.newFixedThreadPool(8);
        List<Future<?>> futures = new ArrayList<Future<?>>();

        
            Future<?> future = executorService.submit(() -> {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -1 );
                Date startTime = cal.getTime();
                cal.add(Calendar.DATE, 3);
                Date endTime = cal.getTime();
                Distributor distributor = new Distributor(1, "disBatch1");
                try {
                    app.createBatch(1, "batchtype1", startTime, endTime, distributor, CouponType.OPEN, 100, 1000);
                } catch (CouponManagementException e) {
                    System.out.println(e);
                }
            });
            futures.add(future);
        Future<?> future2 = executorService.submit(() -> {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -2 );
            Date startTime = cal.getTime();
            cal.add(Calendar.DATE, 5);
            Date endTime = cal.getTime();
            Distributor distributor = new Distributor(2, "disBatch2");
            try {
                app.createBatch(2, "batchtype2", startTime, endTime, distributor, CouponType.CLOSE, 100, 1000);
            } catch (CouponManagementException e) {
                System.out.println(e);
            }
        });
        futures.add(future2);

        Future<?> future3 = executorService.submit(() -> {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, 10 );
            Date startTime = cal.getTime();
            cal.add(Calendar.DATE, 7);
            Date endTime = cal.getTime();
            Distributor distributor = new Distributor(3, "disBatch3");
            try {
                app.createBatch(3, "batchtype3", startTime, endTime, distributor, CouponType.OPEN, 100, 1000);
            } catch (CouponManagementException e) {
                System.out.println(e);
            }
        });
        futures.add(future3);

        for(var f : futures) {
            f.get();
        }
        System.out.println(app.getBatch(1));
        System.out.println(app.getBatch(2));
        List<Future<?>> futuresIngest = new ArrayList<Future<?>>();
        for(int i=1;i<3;i++) {
            Future<?> f1 = executorService.submit(() -> {
                try {
                    var couponSet = new HashSet<Coupon>();
                    couponSet.add(new Coupon(1,"couponcodeOpenlotbatch1"));
                    app.ingestCoupons(1, couponSet);
                } catch (CouponManagementException e) {
                    e.printStackTrace();
                }
            });
            futuresIngest.add(f1);
        }
        for(int i=1;i<3;i++) {
            Future<?> f1 = executorService.submit(() -> {
                try {
                    var couponSet = new HashSet<Coupon>();
                    couponSet.add(new Coupon(1,"cc1batch2"));
                    couponSet.add(new Coupon(2,"cc2batch2"));
                    couponSet.add(new Coupon(3,"cc3batch2"));
                    app.ingestCoupons(2, couponSet);
                } catch (CouponManagementException e) {
                    e.printStackTrace();
                }
            });
            futuresIngest.add(f1);
        }
        for(var f : futuresIngest) {
            f.get();
        }
        System.out.println(app.getBatch(1));
        System.out.println(app.getBatch(2));
        app.updateState(1, BatchState.APPROVED);
        app.updateState(2, BatchState.APPROVED);
        app.updateState(1, BatchState.ACTIVE);
        app.updateState(2, BatchState.ACTIVE);
        List<Future<?>> futuresGrant = new ArrayList<Future<?>>();
        for(int i=1;i<3;i++) {
            Future<?> f1 = executorService.submit(() -> {
                try {
                    app.grantCoupon(1);
                } catch (CouponManagementException e) {
                    e.printStackTrace();
                }
            });
            futuresIngest.add(f1);
        }
        for(int i=1;i<4;i++) {
            Future<?> f1 = executorService.submit(() -> {
                try {
                    app.grantCoupon(2);
                } catch (CouponManagementException e) {
                    e.printStackTrace();
                }
            });
            futuresIngest.add(f1);
        }
        for(var f : futuresGrant) {
            f.get();
        }
        System.out.println(app.getBatch(1));
        System.out.println(app.getBatch(2));

        System.out.println("Done ");

        executorService.shutdown();
    }
}
