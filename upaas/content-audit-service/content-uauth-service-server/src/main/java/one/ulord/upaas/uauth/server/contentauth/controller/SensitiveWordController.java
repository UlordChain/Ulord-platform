/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.uauth.server.contentauth.controller;

import one.ulord.upaas.common.UPaaSErrorCode;
import one.ulord.upaas.common.api.APIResult;
import one.ulord.upaas.common.api.PagingResult;
import one.ulord.upaas.uauth.common.vo.SensitiveWord;
import one.ulord.upaas.uauth.server.contentauth.business.SensitiveWordsRepo;
import one.ulord.upaas.uauth.server.contentauth.services.SceneService;
import one.ulord.upaas.uauth.server.contentauth.services.SensitiveWordService;
import one.ulord.upaas.uauth.server.contentauth.vo.SceneVO;
import one.ulord.upaas.uauth.server.contentauth.vo.SensitiveWordItem;
import one.ulord.upaas.uauth.server.utils.RequestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author haibo
 * @since 5/26/18
 */
@Controller
public class SensitiveWordController {
    @Autowired
    SensitiveWordsRepo sensitiveWordsRepo;
    @Autowired
    SensitiveWordService sensitiveWordService;
    @Autowired
    SceneService sceneService;

    @RequestMapping("/list")
    public String list(Model model, @RequestParam(required = false, defaultValue = "10") int pageSize,
                       @RequestParam(required = false, defaultValue = "1") int pageNum,
                       @RequestParam(required = false) String criteria) {
        Map<String, List<String>> criteriaMap = RequestUtils.resolveCriteria(criteria);
        PagingResult page = sensitiveWordService.retrieve(pageNum, pageSize, criteriaMap);

        List<SceneVO> sceneVOS = sceneService.getAll();

        model.addAttribute("scList", sceneVOS);

        model.addAttribute("pageNum", page.getPageNum());

        model.addAttribute("pageSize", page.getPageSize());

        model.addAttribute("pages", page.getPages());

        model.addAttribute("sensitiveWords", page.getResult());
        return "list";
    }


    @RequestMapping("/toSelect")
    public String select(Model model, SensitiveWordItem value, @RequestParam(required = false, defaultValue = "10") int pageSize,
                         @RequestParam(required = false, defaultValue = "1") int pageNum,
                         @RequestParam(required = false) String criteria ) {
        String scene = value.getScene();
        String keyword = value.getKeyword();
        Map<String, List<String>> criteriaMap = RequestUtils.resolveCriteria(criteria);
        PagingResult page = sensitiveWordService.selectByItem(pageNum, pageSize, criteriaMap, value.getKeyword(), value.getLevel(), value.getDisabled(), value.getScene());

        List<SceneVO> sceneVOS = sceneService.getAll();

        model.addAttribute("scList", sceneVOS);

        model.addAttribute("pageNum", page.getPageNum());

        model.addAttribute("pageSize", page.getPageSize());

        model.addAttribute("pages", page.getPages());

        model.addAttribute("sce", scene);

        model.addAttribute("key", keyword);

        model.addAttribute("sensitiveWords", page.getResult());

        if (keyword != null) {
                return "selectKeywordList";
        }
        return "selectList";
    }


    @RequestMapping("/toAdd")
    public String toAdd() {
        return "add";
    }

    @RequestMapping("/add")
    public String add(Model model, SensitiveWordItem value, String keyword, HttpServletRequest request, RedirectAttributes attr) {
        if (sensitiveWordService.select(keyword) != null) {
            return "addJr";
        }
        sensitiveWordsRepo.addSensitiveWord(value);
        SensitiveWord sensitiveWord = sensitiveWordService.select(value.getKeyword());
        List<SceneVO> sceneVOS = sceneService.getAll();

        model.addAttribute("scList", sceneVOS);

        model.addAttribute("sensitiveWords", sensitiveWord);
        return "sensitiveWord";
    }

    @RequestMapping("/delete")
    public String delete(Long uid) {
        sensitiveWordService.deleteItem(uid);
        return "redirect:/list";
    }

    @RequestMapping("/edit")
    public String update(Model model, SensitiveWordItem value) {

        String keyword = value.getKeyword();
        SensitiveWord sensitiveWord1 = sensitiveWordService.select(keyword);
        if (sensitiveWordService.select(keyword) != null && ((SensitiveWordItem) sensitiveWord1).getUid() != value.getUid()) {
            SensitiveWord sensitiveWord = sensitiveWordService.retrieveItem(value.getUid());
            model.addAttribute("sensitiveWord", sensitiveWord);
            return "editJr";
        }
        sensitiveWordService.updateItem(value);
        SensitiveWordItem sensitiveWordItem = sensitiveWordService.select(value.getKeyword());
        if (value.getDisabled() == sensitiveWordItem.getDisabled()){
            model.addAttribute("sensitiveWords", sensitiveWordItem);
            model.addAttribute("key", keyword);
        }else if (value.getDisabled() != sensitiveWordItem.getDisabled() && value.getDisabled() == 1){
            sensitiveWordService.disableItem(sensitiveWordItem.getUid());
            SensitiveWordItem sensitiveWordItem1 = sensitiveWordService.select(sensitiveWordItem.getKeyword());
            model.addAttribute("sensitiveWords", sensitiveWordItem1);
            model.addAttribute("key", keyword);
        }else{
            sensitiveWordService.enableItem(sensitiveWordItem.getUid());
            SensitiveWordItem sensitiveWordItem1 = sensitiveWordService.select(sensitiveWordItem.getKeyword());
            model.addAttribute("sensitiveWords", sensitiveWordItem1);
            model.addAttribute("key", keyword);
        }
        List<SceneVO> sceneVOS = sceneService.getAll();
        model.addAttribute("scList", sceneVOS);
        return "sensitiveWord";
}


    @RequestMapping("/toEdit")
    public String toEdit(Model model, int uid) {
        SensitiveWord sensitiveWord = sensitiveWordService.retrieveItem(uid);
        model.addAttribute("sensitiveWords", sensitiveWord);
        return "edit";
    }

    @PostMapping("/sensitiveword")
    public APIResult addSensitiveWord(@RequestBody SensitiveWordItem value) {
        try {
            int rv = sensitiveWordsRepo.addSensitiveWord(value);
            if (rv > 0) {
                return APIResult.buildResult(rv);
            } else {
                return APIResult.buildError(UPaaSErrorCode.SYSERR_SERVER_EXCEPTION, "Save exception");
            }
        } catch (Exception e) {
            return APIResult.buildError(UPaaSErrorCode.SYSERR_SERVER_EXCEPTION, e.getMessage());
        }
    }

    @PostMapping("/sensitivewords")
    public APIResult addSensitiveWords(@RequestBody List<SensitiveWordItem> value) {
        try {
            int rv = sensitiveWordsRepo.addSensitiveWords(value);
            if (rv > 0) {
                return APIResult.buildResult(rv);
            } else {
                return APIResult.buildError(UPaaSErrorCode.SYSERR_SERVER_EXCEPTION, "Save exception");
            }
        } catch (Exception e) {
            return APIResult.buildError(UPaaSErrorCode.SYSERR_SERVER_EXCEPTION, e.getMessage());
        }
    }

    @DeleteMapping("/sensitiveword")
    public APIResult deleteSensitiveWord(@RequestBody SensitiveWordItem value) {
        try {
            int rv = sensitiveWordsRepo.removeSensitiveWord(value);
            if (rv > 0) {
                return APIResult.buildResult(rv);
            } else {
                return APIResult.buildError(UPaaSErrorCode.SERVER_NO_RECORD, "No record");
            }
        } catch (Exception e) {
            return APIResult.buildError(UPaaSErrorCode.SYSERR_SERVER_EXCEPTION, e.getMessage());
        }
    }

    @DeleteMapping("/sensitivewords")
    public APIResult deleteSensitiveWords(@RequestBody List<SensitiveWordItem> value) {
        try {
            int rv = sensitiveWordsRepo.removeSensitiveWords(value);
            if (rv > 0) {
                return APIResult.buildResult(rv);
            } else {
                return APIResult.buildError(UPaaSErrorCode.SERVER_NO_RECORD, "No record");
            }
        } catch (Exception e) {
            return APIResult.buildError(UPaaSErrorCode.SYSERR_SERVER_EXCEPTION, e.getMessage());
        }
    }

    @GetMapping("/sensitiveword")
    private APIResult querySensitiveWord(@RequestParam(required = false, defaultValue = "10") int pageSize,
                                         @RequestParam(required = false, defaultValue = "1") int pageNum,
                                         @RequestParam(required = false) String criteria) {
        Map<String, List<String>> criteriaMap = RequestUtils.resolveCriteria(criteria);
        return sensitiveWordsRepo.retrieve(pageNum, pageSize, criteriaMap);
    }

    @GetMapping("/sensitiverepo")
    public APIResult querySensitiveRepo() {
        return sensitiveWordsRepo.retrieveRepo();
    }
}
