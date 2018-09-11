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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
	 * Get gas balance
	 * @return
	 */
	@ApiOperation(value = "Get gas balance", notes = "Get gas balance")
	@RequestMapping(value = "getGasBalance", method = RequestMethod.GET)
	public ResponseEntity<String> getGasBalance() {
		JsonResult<String, Object> resultJson = new JsonResult<String, Object>();

		ContentContract cc = contentContractHelper.getContentContract();
		String gasBalance = "";
		try {
			gasBalance = cc.getGasBalance().toString();
			logger.info("======================  ServiceController.getGasBalance......Gas balance:" + gasBalance);
		} catch (Exception e) {
			e.printStackTrace();
			resultJson.setResult(e.getMessage());
			return ResultUtil.GoResponseFailure(resultJson);
		}

		resultJson.setResult(gasBalance);
		return ResultUtil.GoResponseSuccess(resultJson);
	}


	/**
	 * Get gas balance by address
	 * @param address
	 * @return
	 */
	@ApiOperation(value = "Get gas balance by address", notes = "Get gas balance by address")
	@RequestMapping(value = "getGasBalanceByAddress", method = RequestMethod.GET)
	public ResponseEntity<String> getGasBalanceByAddress(@RequestParam String address) {
		JsonResult<String, Object> resultJson = new JsonResult<String, Object>();
		logger.info("======================  ServiceController.getGasBalanceByAddress......address:"+address);

		ContentContract cc = contentContractHelper.getContentContract();
		String gasBalance = "";
		try {
			gasBalance = cc.getGasBalance(address).toString();
			logger.info("======================  ServiceController.getGasBalanceByAddress......Gas balance:" + gasBalance);
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
	 * Get token balance
	 * @return
	 */
	@ApiOperation(value = "Get token balance", notes = "Get token balance")
	@RequestMapping(value = "getTokenBalance", method = RequestMethod.GET)
	public ResponseEntity<String> getTokenBalance() {
		JsonResult<String, Object> resultJson = new JsonResult<String, Object>();

		ContentContract cc = contentContractHelper.getContentContract();
		String tokenBalance = "";
		try {
			tokenBalance = cc.getTokenBalance().toString();
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
	 * Get token balance by address
	 * @param address
	 * @return
	 */
	@ApiOperation(value = "Get token balance by address", notes = "Get token balance by address")
	@RequestMapping(value = "getTokenBalanceByAddress", method = RequestMethod.GET)
	public ResponseEntity<String> getTokenBalanceByAddress(@RequestParam String address) {
		JsonResult<String, Object> resultJson = new JsonResult<String, Object>();
		logger.info("======================  ServiceController.getTokenBalanceByAddress......address:"+address);

		ContentContract cc = contentContractHelper.getContentContract();
		String tokenBalance = "";
		try {
			tokenBalance = cc.getTokenBalance(address).toString();
			logger.info("======================  ServiceController.getTokenBalanceByAddress......Token balance:" + tokenBalance);
		} catch (Exception e) {
			e.printStackTrace();
			resultJson.setResult(e.getMessage());
			return ResultUtil.GoResponseFailure(resultJson);
		}

		resultJson.setResult(tokenBalance);
		return ResultUtil.GoResponseSuccess(resultJson);
	}



	/**
	 * Send raw transaction
	 * @param toAddress
	 * @param quality
	 * @return
	 */
	@ApiOperation(value = "Send raw transaction", notes = "Send raw transaction")
	@RequestMapping(value = "sendRawTransaction", method = RequestMethod.POST)
	public ResponseEntity<String> sendRawTransaction(@RequestParam String toAddress,String quality) {
		JsonResult<String, Object> resultJson = new JsonResult<String, Object>();
		Map<String, Object> dataMap = new HashMap<String, Object> ();
		logger.info("======================  ServiceController.sendRawTransaction......toAddress:"+toAddress+",quality:"+quality);

		ContentContract cc = contentContractHelper.getContentContract();
		String hash = "";
		try {
			hash = cc.sendRawTransaction(toAddress,new BigInteger(quality));
		} catch (Exception e) {
			e.printStackTrace();
			resultJson.setResult(e.getMessage());
			return ResultUtil.GoResponseFailure(resultJson);
		}

		resultJson.setResult(hash);
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
