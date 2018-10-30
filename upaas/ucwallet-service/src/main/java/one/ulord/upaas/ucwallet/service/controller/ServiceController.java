/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.ucwallet.service.controller;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import one.ulord.upaas.ucwallet.service.base.common.ResultUtil;
import one.ulord.upaas.ucwallet.service.base.contract.Provider;
import one.ulord.upaas.ucwallet.service.service.SUTService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import one.ulord.upaas.ucwallet.service.base.common.JsonResult;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

/**
 * Service Center
 * 
 * @author chenxin
 * @since 2018-08-10
 */
@Api(value = "Service Center")
@RestController
@RequestMapping(value = "api")
public class ServiceController{
	private static final Logger logger = LoggerFactory.getLogger(ServiceController.class);

	@Autowired
    private SUTService sutService;

    @Autowired
    private Provider provider;

	/**
	 * Get balance by address
	 * @param address
	 * @return
	 */
	@ApiOperation(value = "Get balance by address", notes = "Get balance by address")
	@RequestMapping(value = "getBalance/{address}", method = RequestMethod.GET)
	public ResponseEntity<String> getSutBalance(@PathVariable(value="address") String address) {
		JsonResult resultJson = new JsonResult();
		logger.info("address:"+address);

		String gasBalance = "";
		try {
			gasBalance = sutService.getBalance(address).toString();
			logger.info("balance:" + gasBalance);
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
	public ResponseEntity<String> getTokenBalance(@PathVariable(value="address") String address, String token) {
		JsonResult resultJson = new JsonResult();
		logger.info("address:{}, token:{}", address, token);

		if (token == null){
		    // using default contract address
            token = provider.getContractAddress();
        }
		String tokenBalance;
		try {
			tokenBalance = sutService.getTokenBalance(token, address).toString();
			logger.info("Token balance:" + tokenBalance);
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
		JsonResult resultJson = new JsonResult();
		logger.debug("address:{}", address);

		String nonce;
		try {
			nonce = sutService.getTransactionCount(address).toString();
			logger.debug("nonce:{}", nonce);
		} catch (Exception e) {
			e.printStackTrace();
			resultJson.setResult(e.getMessage());
			return ResultUtil.GoResponseFailure(resultJson);
		}

		// Return transaction count as same as current nonce
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
		JsonResult resultJson = new JsonResult();
		logger.debug("hexValue:{}", hexValue);

		String hash;
		try {
			hash = sutService.sendRawTransaction(hexValue);
		} catch (Exception e) {
			e.printStackTrace();
			resultJson.setResult(e.getMessage());
			return ResultUtil.GoResponseFailure(resultJson);
		}

		resultJson.setResult(hash);
		return ResultUtil.GoResponseSuccess(resultJson);
	}


	/**
	 * query transaction
	 * @param txhash transaction hash
	 * @return
	 */
	@ApiOperation(value = "query transaction by transaction hash", notes = "query transaction")
	@RequestMapping(value = "queryTransaction", method = RequestMethod.GET)
	public ResponseEntity<String> queryTransaction(@RequestParam String txhash) {
		JsonResult resultJson = new JsonResult();
		logger.info("getTransaction:{}", txhash);

		Transaction hash;
		try {
			hash = sutService.getTransaction(txhash);
		} catch (Exception e) {
			e.printStackTrace();
			resultJson.setResult(e.getMessage());
			return ResultUtil.GoResponseFailure(resultJson);
		}

		resultJson.setResult(JSON.toJSONString(hash));
		return ResultUtil.GoResponseSuccess(resultJson);
	}

	/**
	 * query transaction
	 * @param txhash transaction hash
	 * @return
	 */
	@ApiOperation(value = "query transaction receipt by transaction hash", notes = "query transaction receipt")
	@RequestMapping(value = "queryTransactionReceipt", method = RequestMethod.GET)
	public ResponseEntity<String> queryTransactionReceipt(@RequestParam String txhash) {
		JsonResult resultJson = new JsonResult();
		logger.info("getTransactionReceipt" + txhash);

		TransactionReceipt hash = null;
		try {
			hash = sutService.getTransactionReceipt(txhash);
		} catch (Exception e) {
			e.printStackTrace();
			resultJson.setResult(e.getMessage());
			return ResultUtil.GoResponseFailure(resultJson);
		}

		resultJson.setResult(JSON.toJSONString(hash));
		return ResultUtil.GoResponseSuccess(resultJson);
	}

}
