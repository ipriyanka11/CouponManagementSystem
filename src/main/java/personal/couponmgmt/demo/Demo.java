package personal.couponmgmt.demo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import personal.couponmgmt.application.CouponManagementSystem;
import personal.couponmgmt.domain.BatchState;
import personal.couponmgmt.domain.Coupon;
import personal.couponmgmt.domain.CouponType;
import personal.couponmgmt.domain.Distributor;
import personal.couponmgmt.exception.CouponManagementException;
import personal.couponmgmt.util.MyFileReader;
import personal.couponmgmt.util.PropertiesManager;

import java.util.*;
import java.util.concurrent.*;

public class Demo {

    private static Scanner scanner = new Scanner(System.in);
    private static final Logger LOG = LogManager.getLogger(Demo.class);

    public static void main(String args[]) throws ExecutionException {
        System.out.println("Welcome to Coupon Management System, creating 3 batches with ids 1 , 2 & 3 ");
        CouponManagementSystem app = CouponManagementSystem.getInstance();
        ExecutorService executorService = Executors.newFixedThreadPool(PropertiesManager.getInstance().getNumThreads());
        List<Future<?>> futures = new ArrayList<Future<?>>();


        Future<?> future = executorService.submit(() -> {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -1);
            Date startTime = cal.getTime();
            cal.add(Calendar.DATE, 3);
            Date endTime = cal.getTime();
            Distributor distributor = new Distributor(1, "disBatch1");
            try {
                app.createBatch(1, "batchtype1", startTime, endTime, distributor, CouponType.OPEN, 100, 1000);
            } catch (CouponManagementException e) {
                LOG.error(e.getErrorCode() + " - " + e.getMessage());
            }
        });
        futures.add(future);
        Future<?> future2 = executorService.submit(() -> {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -2);
            Date startTime = cal.getTime();
            cal.add(Calendar.DATE, 5);
            Date endTime = cal.getTime();
            Distributor distributor = new Distributor(2, "disBatch2");
            try {
                app.createBatch(2, "batchtype2", startTime, endTime, distributor, CouponType.CLOSE, 100, 1000);
            } catch (CouponManagementException e) {
                LOG.error(e.getErrorCode() + " - " + e.getMessage());
            }
        });
        futures.add(future2);

        Future<?> future3 = executorService.submit(() -> {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, 10);
            Date startTime = cal.getTime();
            cal.add(Calendar.DATE, -7);
            Date endTime = cal.getTime();
            Distributor distributor = new Distributor(3, "disBatch3");
            try {
                app.createBatch(3, "batchtype3", startTime, endTime, distributor, CouponType.OPEN, 100, 1000);
            } catch (CouponManagementException e) {
                LOG.error(e.getErrorCode() + " - " + e.getMessage());
                //e.printStackTrace(System.out);
            }
        });
        futures.add(future3);

        for (var f : futures) {
            try {
                f.get();
            } catch (InterruptedException e) {
                LOG.error("Batch creation futures got exception : "+e.getMessage());
                //System.out.println("Batch creation futures got exception : "+e.getMessage());
            }
        }
        System.out.println(app.getBatch(1));
        System.out.println(app.getBatch(2));
        System.out.println(app.getBatch(3));
        List<Future<?>> futuresIngest = new ArrayList<Future<?>>();
        for (int i = 1; i < 3; i++) {
            Future<?> f1 = executorService.submit(() -> {
                try {
                    var couponSet = new HashSet<Coupon>();
                    couponSet.add(new Coupon(1, "couponcodeOpenlotbatch1"));
                    app.ingestCoupons(1, couponSet);
                } catch (CouponManagementException e) {
                    System.out.println(e.getErrorCode()+" -"+e.getMessage());
                }
            });
            futuresIngest.add(f1);
        }

        Future<?> f1 = executorService.submit(() -> {
            try {
                var couponSet = new HashSet<Coupon>();
                couponSet.add(new Coupon(1, "cc1batch2"));
                couponSet.add(new Coupon(2, "cc2batch2"));
                couponSet.add(new Coupon(3, "cc3batch2"));
                couponSet.add(new Coupon(4, "cc4batch2"));
                app.ingestCoupons(2, couponSet);
            } catch (CouponManagementException e) {
                System.out.println(e.getErrorCode()+" -"+e.getMessage());
            }
        });
        futuresIngest.add(f1);

        Future<?> fi2 = executorService.submit(() -> {
            try {
                var couponSet = new HashSet<Coupon>();
                couponSet.add(new Coupon(10, "cc10batch2"));
                couponSet.add(new Coupon(20, "cc20batch2"));
                couponSet.add(new Coupon(30, "cc30batch2"));
                couponSet.add(new Coupon(40, "cc40batch2"));
                app.ingestCoupons(2, couponSet);
            } catch (CouponManagementException e) {
                System.out.println(e.getErrorCode()+" -"+e.getMessage());
            }
        });
        futuresIngest.add(fi2);

        Future<?> f13= executorService.submit(() -> {
            try {
                List<String> couponCodes = MyFileReader.readFile("couponcodes.txt");
                Set<Coupon> couponSet = new HashSet<Coupon>();
                for(String line : couponCodes) {
                    var a = line.split(",");
                    int cid = Integer.valueOf(a[0]);
                    Coupon c = new Coupon(cid,a[1]);
                    couponSet.add(c);
                }
                app.ingestCoupons(2,couponSet);
            }catch (CouponManagementException e) {
                System.out.println(e.getErrorCode()+" -"+e.getMessage());
            }
        });
        futuresIngest.add(f13);

        for (var f : futuresIngest) {
            try {
                f.get();
            } catch (InterruptedException e) {
                System.out.println("exception in ingestion future " + " - " + e.getMessage());
            }
        }
        LOG.info(app.getBatch(1));
        LOG.info(app.getBatch(2));

        try {
            app.updateState(1, BatchState.APPROVED);
            app.updateState(2, BatchState.APPROVED);
            app.updateState(1, BatchState.ACTIVE);
            app.updateState(2, BatchState.ACTIVE);
        } catch (CouponManagementException e) {
            System.out.println(e.getErrorCode()+" -"+e.getMessage());
        }


        System.out.println("granting 3 coupons in seq on batch 1 : ");
        try {
            app.grantCoupon(1);
            app.grantCoupon(1);
            app.grantCoupon(1);
        } catch (CouponManagementException e) {
            System.out.println(e.getErrorCode()+" -"+e.getMessage());
        }

        System.out.println(app.getBatch(1));

        List<Future<?>> futuresGrant = new ArrayList<Future<?>>();
        for (int i = 1; i <= 300; i++) {
            Future<?> fg1 = executorService.submit(() -> {
                try {
                    app.grantCoupon(1);
                } catch (CouponManagementException e) {
                    System.out.println(e.getErrorCode()+" -"+e.getMessage());
                }
            });
            futuresGrant.add(fg1);
        }
        for (int i = 1; i < 4; i++) {
            Future<?> fg2 = executorService.submit(() -> {
                try {
                    app.grantCoupon(2);
                } catch (CouponManagementException e) {
                    System.out.println(e.getErrorCode()+" -"+e.getMessage());
                }
            });
            futuresGrant.add(fg2);
        }
        for (var f : futuresGrant) {
            try {
                f.get();
            } catch (InterruptedException e) {
                System.out.println("exception in granting futures " + " - " + e.getMessage());
            }
        }

        System.out.println("Granting done");
        System.out.println(app.getBatch(1));
        System.out.println(app.getBatch(2));

        try {
            app.grantCoupon(2);
        } catch (CouponManagementException e) {
            System.out.println(e.getErrorCode() + e.getMessage());
        }
        System.out.println("Done ");

        executorService.shutdown();
        try {
            if(!executorService.awaitTermination(2, TimeUnit.SECONDS))
                executorService.shutdownNow();
        }catch(InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}
