package one.ulord.upaas.uauth.server.contentauth.controller;

import one.ulord.upaas.common.api.PagingResult;
import one.ulord.upaas.uauth.server.contentauth.services.SceneService;
import one.ulord.upaas.uauth.server.contentauth.vo.SceneVO;
import one.ulord.upaas.uauth.server.utils.RequestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import java.util.Map;

/**
 * @author ShenL
 * @since 09/07/2018
 */
@Controller
public class SceneController {
    @Autowired
    SceneService sceneService;

    @GetMapping("/sceneList")
    public String list(Model model, @RequestParam(required = false, defaultValue = "10") int pageSize,
                       @RequestParam(required = false, defaultValue = "1") int pageNum,
                       @RequestParam(required = false) String criteria) {
        Map<String, List<String>> criteriaMap = RequestUtils.resolveCriteria(criteria);
        PagingResult page = sceneService.retrieve(pageNum, pageSize, criteriaMap);

        List<SceneVO> sceneVOS = sceneService.getAll();

        model.addAttribute("scList", sceneVOS);

        model.addAttribute("pageNum", page.getPageNum());

        model.addAttribute("pageSize", page.getPageSize());

        model.addAttribute("pages", page.getPages());

        model.addAttribute("scenes", page.getResult());
        return "sceneManager";
    }

    @GetMapping("/toSceneSelect")
    public String select(Model model, SceneVO value) {
        SceneVO sceneVO = sceneService.selectSceneBySymbol(value.getSymbol());
        List<SceneVO> sceneVOS = sceneService.getAll();
        model.addAttribute("scList", sceneVOS);
        model.addAttribute("scenes", sceneVO);
            return "sceneSelectList";
    }


    @RequestMapping("/toAddSc")
    public String toAdd() {
        return "addSc";
    }

    @RequestMapping("/addSc")
    public String add(SceneVO value) {
        if (sceneService.selectScene(value.getScene()) != null || sceneService.selectSceneBySymbol(value.getSymbol()) != null) {
            return "addScJr";
        }
        sceneService.createScene(value);
        return "redirect:/sceneList";
    }


    @RequestMapping("/deleteSc")
    public String delete(String scene) {
        sceneService.deleteScene(scene);
        return "redirect:/sceneList";
    }


    @RequestMapping("/editSc")
    public String update(Model model, SceneVO value) {
        if (sceneService.selectScene(value.getScene()) != null && sceneService.selectSceneBySymbol(value.getSymbol()) != null) {
            SceneVO sceneVO = sceneService.selectSceneById(value.getId());
            model.addAttribute("scene", sceneVO);
            return "editScJr";
        }
        sceneService.updateScene(value);
        return "redirect:/sceneList";
    }


    @RequestMapping("/toEditSc")
    public String toEdit(Model model, String scene) {
        SceneVO sceneVO = sceneService.selectScene(scene);
        model.addAttribute("scenes", sceneVO);
        return "editSc";
    }

}
