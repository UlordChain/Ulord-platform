/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.common;

/**
 * @author haibo
 * @since 5/24/18
 */
public class UPaaSErrorCode {
    // Success
    public static final int SUCCESS                   =     0; // Success

    // System error
    public static final int SYSERR_KEY_INVALID        = 10001; //Invalid request key
    public static final int SYSERR_KEY_NO_AUTH        = 10002; //Invalid request key
    public static final int SYSERR_EXPIRED_KEY        = 10003; // Key has expired
    public static final int SYSERR_IP_DENY            = 10004; // IP address has excess access limit
    public static final int SYSERR_KEY_DENY           = 10005; // Key has denied
    public static final int SYSERR_REQ_FREQ           = 10006; // Request too frequency
    public static final int SYSERR_REQ_OUT_OF_LIMIT   = 10007; // Request out of limit
    public static final int SYSERR_SERVER_EXCEPTION   = 10008; // Inner server exception
    public static final int SYSERR_SERVER_MAINTAIN    = 10009; // Server maintain
    public static final int SYSERR_API_OUT_OF_DATE    = 10010; // API out of date
    public static final int SYSERR_NOT_LOGIN          = 10011; // User has not login in
    public static final int SYSERR_NO_APP_KEY         = 10012; // No app key
    public static final int SYSERR_NO_PERMISSION      = 10013; // No permission
    public static final int SYSERR_MANAGE_NEED        = 10014; // Manage permission need
    public static final int SYSERR_USER_DISABLED      = 10015; // User disabled
    public static final int SYSERR_APP_OUT_OF_LIMIT   = 10016; // App amount out of limit
    public static final int SYSERR_REQ_EXPIRED        = 10017; // Request has expired

    // Service error
    public static final int SERVER_USER_NAME_EXIST    = 20000; // Username has exist
    public static final int SERVER_MAIL_EXIST         = 20001; // Mail has exist
    public static final int SERVER_APP_NAME_EXIST     = 20002; // App name has exist
    public static final int SERVER_USER_NOT_EXIST     = 20003; // User not exist
    public static final int SERVER_PASSWORD_ERROR     = 20004; // Password error
    public static final int SERVER_NO_RECORD          = 20005; // Request's record does not exist
    public static final int SERVER_USER_DISABLED      = 20006; // User disabled
    public static final int SERVER_RESOURCE_NOT_PAY   = 20007; // Resource not pay
    public static final int SERVER_PASSWORD_WEAK      = 20008; // Password too weak
    public static final int SERVER_PASSWORD_LEN_ERROR = 20009; // Password length error, 3~128 chars

    // Request and Validate error
    public static final int SERVER_INVALID_PARAMETER  = 20100; // Invalid parameter
    public static final int SERVER_INVALID_JSON_FMT   = 20101; // Invalid JSON format
    public static final int SERVER_SIGNATURE_ERROR    = 20102; // Signature error

}
