/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.uauth.server.contentauth.controller;

import one.ulord.upaas.common.UPaaSErrorCode;
import one.ulord.upaas.common.api.APIResult;
import one.ulord.upaas.uauth.server.contentauth.business.SensitiveWordsRepo;
import one.ulord.upaas.uauth.server.contentauth.vo.SensitiveWordItem;
import one.ulord.upaas.uauth.server.utils.RequestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author haibo
 * @since 5/26/18
 */
@RestController
public class SensitiveWordController {
    @Autowired
    SensitiveWordsRepo sensitiveWordsRepo;

    @PostMapping("/sensitiveword")
    public APIResult addSensitiveWord(@RequestBody SensitiveWordItem value){
        try {
            int rv = sensitiveWordsRepo.addSensitiveWord(value);
            if (rv > 0) {
                return APIResult.buildResult(rv);
            }else{
                return APIResult.buildError(UPaaSErrorCode.SYSERR_SERVER_EXCEPTION, "Save exception");
            }
        }catch (Exception e){
            return APIResult.buildError(UPaaSErrorCode.SYSERR_SERVER_EXCEPTION, e.getMessage());
        }
    }

    @PostMapping("/sensitivewords")
    public APIResult addSensitiveWords(@RequestBody List<SensitiveWordItem> value){
        try {
            int rv = sensitiveWordsRepo.addSensitiveWords(value);
            if (rv > 0) {
                return APIResult.buildResult(rv);
            }else{
                return APIResult.buildError(UPaaSErrorCode.SYSERR_SERVER_EXCEPTION, "Save exception");
            }
        }catch (Exception e){
            return APIResult.buildError(UPaaSErrorCode.SYSERR_SERVER_EXCEPTION, e.getMessage());
        }
    }

    @DeleteMapping("/sensitiveword")
    public APIResult deleteSensitiveWord(@RequestBody SensitiveWordItem value){
        try {
            int rv = sensitiveWordsRepo.removeSenstiveWord(value);
            if (rv > 0) {
                return APIResult.buildResult(rv);
            }else{
                return APIResult.buildError(UPaaSErrorCode.SYSERR_SERVER_EXCEPTION, "Save exception");
            }
        }catch (Exception e){
            return APIResult.buildError(UPaaSErrorCode.SYSERR_SERVER_EXCEPTION, e.getMessage());
        }
    }

    @DeleteMapping("/sensitivewords")
    public APIResult deleteSensitiveWords(@RequestBody List<SensitiveWordItem> value){
        try {
            int rv = sensitiveWordsRepo.removeSensitiveWords(value);
            if (rv > 0) {
                return APIResult.buildResult(rv);
            }else{
                return APIResult.buildError(UPaaSErrorCode.SYSERR_SERVER_EXCEPTION, "Save exception");
            }
        }catch (Exception e){
            return APIResult.buildError(UPaaSErrorCode.SYSERR_SERVER_EXCEPTION, e.getMessage());
        }
    }

    @RequestMapping("/sensitiveword")
    private APIResult querySensitiveWord(@RequestParam(required = false, defaultValue = "10") int pageSize,
                                         @RequestParam(required = false, defaultValue = "1") int pageNum,
                                         @RequestParam(required = false) String criteria){
        Map<String, List<String>> criteriaMap = RequestUtils.resolveCriteria(criteria);
        return sensitiveWordsRepo.retrieve(pageNum, pageSize, criteriaMap);
    }
}
