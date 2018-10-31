/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.ucwallet.service.base.common;

import com.alibaba.fastjson.JSON;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Request return result common help class
 * 
 * @author chenxin
 * @since 2018-08-10
 */
public class ResultUtil {
	private static final String SUCCEEDED = "succeeded";
	private static final String FAILURED = "failed";
	
	public static ResponseEntity<String> GoResponseSuccess(Object result){
		return new ResponseEntity<>(JSON.toJSONString(
				new JsonResult(Constants.SUCCESSFUL, SUCCEEDED, result)), HttpStatus.OK);
	}

	public static ResponseEntity<String> GoSuccess(Object result){
		return new ResponseEntity<>(JSON.toJSONString(
				new JsonResult(Constants.SUCCESSFUL, SUCCEEDED, result)), HttpStatus.OK);
	}
	
	public static ResponseEntity<String> GoResponseFailure(Object result){
		return new ResponseEntity<>(JSON.toJSONString(
				new JsonResult(Constants.FAILURE, FAILURED, result)), HttpStatus.OK);
	}

	public static ResponseEntity<String> GoResponseFailure(int errCode, Object result){
		return new ResponseEntity<>(JSON.toJSONString(
				new JsonResult(errCode, FAILURED, result)), HttpStatus.OK);
	}
	
	public static ResponseEntity<String> GoResponseValidateError(String message){
		return new ResponseEntity<>(JSON.toJSONString(
				new JsonResult(Constants.VALIDATE_ERROR, FAILURED, message)), HttpStatus.OK);
	}
	
	
	public static ResponseEntity<String> GoResponse(String result){
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
}
