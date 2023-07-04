package personal.couponmgmt.exception;

public class CouponManagementException extends Exception{
    private String errorCode;
    public CouponManagementException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public CouponManagementException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
