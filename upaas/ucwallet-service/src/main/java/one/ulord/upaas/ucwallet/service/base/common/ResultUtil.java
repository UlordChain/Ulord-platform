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
	/**
	 * Return JSON: successful operation(Date formatted yyyy-MM-dd HH:mm:ss)
	 * @param resultJson
	 * @return
	 */
	public static ResponseEntity<String> GoResponseFullSuccess(JsonResult resultJson){
		resultJson.setResultCode(Constants.SUCCESSFUL);
		resultJson.setResultMsg(SUCCEEDED);
		return new ResponseEntity<>(JSON.toJSONString(resultJson), HttpStatus.OK);
	}
	
	/**
	 * Return JSON：successful(Date not formatted)
	 * @param resultJson
	 * @return
	 */
	public static ResponseEntity<String> GoFullSuccess(JsonResult resultJson){
		resultJson.setResultCode(Constants.SUCCESSFUL);
		resultJson.setResultMsg(SUCCEEDED);
		return new ResponseEntity<String>(JSON.toJSONString(resultJson), HttpStatus.OK);
	}
	
	/**
	 * Return JSON：successful(Date formatted yyyy-MM-dd HH:mm:ss)
	 * @param resultJson
	 * @return
	 */
	public static ResponseEntity<String> GoResponseSuccess(JsonResult resultJson){
		resultJson.setResultCode(Constants.SUCCESSFUL);
		resultJson.setResultMsg(SUCCEEDED);
		return new ResponseEntity<>(JSON.toJSONString(resultJson), HttpStatus.OK);
	}
	
	/**
	 * Return JSON：successful(Date not formatted)
	 * @param resultJson
	 * @return
	 */
	public static ResponseEntity<String> GoSuccess(JsonResult resultJson){
		resultJson.setResultCode(Constants.SUCCESSFUL);
		resultJson.setResultMsg(SUCCEEDED);
		return new ResponseEntity<>(JSON.toJSONString(resultJson), HttpStatus.OK);
	}
	
	/**
	 * Return json：failured
	 * @param resultJson
	 * @return
	 */
	public static ResponseEntity<String> GoResponseFailure(JsonResult resultJson){
		resultJson.setResultCode(Constants.FAILURE);
		resultJson.setResultMsg(SUCCEEDED);
		return new ResponseEntity<>(JSON.toJSONString(resultJson), HttpStatus.OK);
	}
	
	/**
	 * Return json：param validate error
	 * 
	 * @param resultJson
	 * @param message
	 * @return
	 */
	public static ResponseEntity<String> GoResponseValidateError(JsonResult resultJson, String message){
		resultJson.setResultCode(Constants.VALIDATE_ERROR);
		resultJson.setResultMsg(message);
		return new ResponseEntity<>(JSON.toJSONString(resultJson), HttpStatus.OK);
	}
	
	
	public static ResponseEntity<String> GoResponse(String result){
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
}
