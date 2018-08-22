/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.ucwallet.service.base.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Request return result common help class
 * 
 * @author chenxin
 * @since 2018-08-10
 */
public class ResultUtil {

	/**
	 * Return JSON: successful operation(Date formatted yyyy-MM-dd HH:mm:ss)
	 * @param resultJson
	 * @return
	 */
	public static ResponseEntity<String> GoResponseFullSuccess(JsonResult<String, Object> resultJson){
		resultJson.setResultCode(Constants.SUCCESSFUL);
		resultJson.setResultMsg("successed");
		Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd HH:mm:ss").create();;
		return new ResponseEntity<String>(gson.toJson(resultJson).replaceAll("null","\"\""), HttpStatus.OK);
	}
	
	/**
	 * Return JSON：successful(Date not formatted)
	 * @param resultJson
	 * @return
	 */
	public static ResponseEntity<String> GoFullSuccess(JsonResult<String, Object> resultJson){
		resultJson.setResultCode(Constants.SUCCESSFUL);
		resultJson.setResultMsg("successed");
		Gson gson = new GsonBuilder().serializeNulls().create();;
		return new ResponseEntity<String>(gson.toJson(resultJson).replaceAll("null","\"\""), HttpStatus.OK);
	}
	
	/**
	 * Return JSON：successful(Date formatted yyyy-MM-dd HH:mm:ss)
	 * @param resultJson
	 * @return
	 */
	public static ResponseEntity<String> GoResponseSuccess(JsonResult<String, Object> resultJson){
		resultJson.setResultCode(Constants.SUCCESSFUL);
		resultJson.setResultMsg("successed");
		return new ResponseEntity<String>(JsonUtil.toJson(resultJson,""), HttpStatus.OK);
	}
	
	/**
	 * Return JSON：successful(Date not formatted)
	 * @param resultJson
	 * @return
	 */
	public static ResponseEntity<String> GoSuccess(JsonResult<String, Object> resultJson){
		resultJson.setResultCode(Constants.SUCCESSFUL);
		resultJson.setResultMsg("successed");
		return new ResponseEntity<String>(JsonUtil.toJson(resultJson), HttpStatus.OK);
	}
	
	/**
	 * Return json：failured
	 * @param resultJson
	 * @return
	 */
	public static ResponseEntity<String> GoResponseFailure(JsonResult<String, Object> resultJson){
		resultJson.setResultCode(Constants.FAILURE);
		resultJson.setResultMsg("failured");
		return new ResponseEntity<String>(JsonUtil.toJson(resultJson), HttpStatus.OK);
	}
	
	/**
	 * Return json：param validate error
	 * 
	 * @param resultJson
	 * @param message
	 * @return
	 */
	public static ResponseEntity<String> GoResponseValidateError(JsonResult<String, Object> resultJson,String message){
		resultJson.setResultCode(Constants.VALIDATE_ERROR);
		resultJson.setResultMsg(message);
		return new ResponseEntity<String>(JsonUtil.toJson(resultJson), HttpStatus.OK);
	}
	
	
	public static ResponseEntity<String> GoResponse(String result){
		return new ResponseEntity<String>(result, HttpStatus.OK);
	}
	
//	public static ResponseEntity<String> GoResponseJson(JSONArray resultJson){
//		return new ResponseEntity<String>(JsonUtil.toJson(resultJson), HttpStatus.OK);
//	}
   
}
