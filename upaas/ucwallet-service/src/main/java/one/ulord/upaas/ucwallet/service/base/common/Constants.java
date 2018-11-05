/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.ucwallet.service.base.common;

/**
 * Constant
 *
 * @author chenxin
 * @since 2018-08-10
 */
public class Constants {

	public static final String CONTENT_TYPE = "application/json; charset=utf-8";
	
	public static final String CHARACTER_ENCODING = "utf-8";

    // sessionId
    public static final String REDIS_MQ_MESSAGES = "redisMqMessages";
	
	// sessionId
	public static final String JSESSIONID = "jsessionid";

	public static final int SUCCESSFUL = 0;
    public static final int NODATA = 1;
    public static final int TIMEOUT = 2;
    public static final int VALIDATE_ERROR = 3;
    public static final int FAILURE = 9;

    /** No enough sUT **/
    public static final int NO_ENOUGH_SUT = 1000;
    /** Nonce value is invalid **/
    public static final int INVALID_NONCE_VALUE   = 1001;


	
}
