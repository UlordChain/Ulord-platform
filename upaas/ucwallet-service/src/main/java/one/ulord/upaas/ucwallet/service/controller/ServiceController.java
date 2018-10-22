/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.ucwallet.service.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import one.ulord.upaas.ucwallet.service.base.common.RedisUtil;
import one.ulord.upaas.ucwallet.service.base.common.ResultUtil;
import one.ulord.upaas.ucwallet.service.base.contract.ContentContract;
import one.ulord.upaas.ucwallet.service.base.contract.ContentContractHelper;
import one.ulord.upaas.ucwallet.service.base.contract.TransactionActionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import one.ulord.upaas.ucwallet.service.base.common.JsonResult;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * Service Center
 * 
 * @author chenxin
 * @since 2018-08-10
 */
@Api(value = "Service Center")
@RestController
@RequestMapping(value = "api")
public class ServiceController  implements TransactionActionHandler {

	private static final Logger logger = LoggerFactory.getLogger(ServiceController.class);

//	@Autowired
//	RedisUtil redisUtil;

	@Autowired(required=true)
	private ContentContractHelper contentContractHelper;

	/**
	 * Get balance by address
	 * @param address
	 * @return
	 */
	@ApiOperation(value = "Get balance by address", notes = "Get balance by address")
	@RequestMapping(value = "getBalance/{address}", method = RequestMethod.GET)
	public ResponseEntity<String> getGasBalance(@PathVariable(value="address") String address) {
		JsonResult<String, Object> resultJson = new JsonResult<String, Object>();
		logger.info("======================  ServiceController.getBalance......address:"+address);

		ContentContract cc = contentContractHelper.getContentContract();
		String gasBalance = "";
		try {
			gasBalance = cc.getGasBalance(address).toString();
			logger.info("======================  ServiceController.getBalance......Gas balance:" + gasBalance);
		} catch (Exception e) {
			e.printStackTrace();
			resultJson.setResult(e.getMessage());
			return ResultUtil.GoResponseFailure(resultJson);
		}

		// 返回处理结果
		resultJson.setResult(gasBalance);
		return ResultUtil.GoResponseSuccess(resultJson);
	}

	/**
	 * Get token balance by address
	 * @param address
	 * @return
	 */
	@ApiOperation(value = "Get token balance by address", notes = "Get token balance by address")
	@RequestMapping(value = "getTokenBalance/{address}", method = RequestMethod.GET)
	public ResponseEntity<String> getTokenBalance(@PathVariable(value="address") String address) {
		JsonResult<String, Object> resultJson = new JsonResult<String, Object>();
		logger.info("======================  ServiceController.getTokenBalance......address:"+address);

		ContentContract cc = contentContractHelper.getContentContract();
		String tokenBalance = "";
		try {
			tokenBalance = cc.getTokenBalance(address).toString();
			logger.info("======================  ServiceController.getTokenBalance......Token balance:" + tokenBalance);
		} catch (Exception e) {
			e.printStackTrace();
			resultJson.setResult(e.getMessage());
			return ResultUtil.GoResponseFailure(resultJson);
		}

		resultJson.setResult(tokenBalance);
		return ResultUtil.GoResponseSuccess(resultJson);
	}


	/**
	 * Get transaction count by address
	 * @param address
	 * @return
	 */
	@ApiOperation(value = "Get transaction count by address", notes = "Get transaction count by address")
	@RequestMapping(value = "getTransactionCount/{address}", method = RequestMethod.GET)
	public ResponseEntity<String> getTransactionCount(@PathVariable(value="address") String address) {
		JsonResult<String, Object> resultJson = new JsonResult<String, Object>();
		logger.info("======================  ServiceController.getTransactionCount......address:"+address);

		ContentContract cc = contentContractHelper.getContentContract();
		String nonce = "";
		try {
			nonce = cc.getTransactionCount(address).toString();
			logger.info("======================  ServiceController.getTransactionCount......nonce:" + nonce);
		} catch (Exception e) {
			e.printStackTrace();
			resultJson.setResult(e.getMessage());
			return ResultUtil.GoResponseFailure(resultJson);
		}

		// 返回处理结果
		resultJson.setResult(nonce);
		return ResultUtil.GoResponseSuccess(resultJson);
	}



	/**
	 * Send raw transaction
	 * @param hexValue
	 * @return
	 */
	@ApiOperation(value = "Send raw transaction", notes = "Send raw transaction")
	@RequestMapping(value = "sendRawTransaction", method = RequestMethod.POST)
	public ResponseEntity<String> sendRawTransaction(@RequestParam String hexValue) {
		JsonResult<String, Object> resultJson = new JsonResult<String, Object>();
		logger.info("======================  ServiceController.sendRawTransaction......hexValue:" + hexValue);

		ContentContract cc = contentContractHelper.getContentContract();
		String hash = "";
		try {
			hash = cc.sendRawTransaction(hexValue);
		} catch (Exception e) {
			e.printStackTrace();
			resultJson.setResult(e.getMessage());
			return ResultUtil.GoResponseFailure(resultJson);
		}

		resultJson.setResult(hash);
		return ResultUtil.GoResponseSuccess(resultJson);
	}


	/**
	 * Reset Nonce
	 * @return
	 */
	@ApiOperation(value = "Reset Nonce", notes = "Reset Nonce")
	@RequestMapping(value = "resetNonce", method = RequestMethod.GET)
	public ResponseEntity<String> resetNonce() {
		JsonResult<String, Object> resultJson = new JsonResult<String, Object>();
		logger.info("======================  ServiceController.resetNonce......");

		ContentContract cc = contentContractHelper.getContentContract();
		try {
			cc.resetNonce();
		} catch (Exception e) {
			e.printStackTrace();
			resultJson.setResult(e.getMessage());
			return ResultUtil.GoResponseFailure(resultJson);
		}

		resultJson.setResult("success");
		return ResultUtil.GoResponseSuccess(resultJson);
	}




	@Override
	public void success(String id, String txhash) {
		System.out.println("--->id:" + id + ", txhash:" + txhash);
	}

	@Override
	public void fail(String id, String message) {
		System.out.println("--->id:" + id + ", message:" + message);
	}

}
