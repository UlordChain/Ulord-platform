package one.ulord.upaas.uauth.server.contentauth.services.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import one.ulord.upaas.common.api.PagingResult;
import one.ulord.upaas.uauth.server.contentauth.dao.SceneMapper;
import one.ulord.upaas.uauth.server.contentauth.services.SceneService;
import one.ulord.upaas.uauth.server.contentauth.vo.SceneVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author ShenL
 * @since 09/07/2018
 */
@Component
public class SceneServiceImpl implements SceneService {

    @Autowired
    SceneMapper sceneMapper;

    @Override
    public SceneVO selectScene(String scene){
        SceneVO sceneVO = sceneMapper.selectScene(scene);
        return sceneVO;
    }

    public SceneVO selectSceneById(long id){
        SceneVO sceneVO = sceneMapper.selectSceneById(id);
        return sceneVO;
    }

    @Override
    public int  selectSceneBySc(String scene){
        int sc = sceneMapper.selectSceneBySc(scene);
        return sc;
    }

    @Override
    public int selectSceneBySy(String symbol){
        int sy = sceneMapper.selectSceneBySy(symbol);
        return sy;
    }

    @Override
    public SceneVO selectSceneBySymbol(String symbol){
        SceneVO sceneVO = sceneMapper.selectSceneBySymbol(symbol);
        return sceneVO;
    }

    @Override
    public int createScene(SceneVO value){
        return sceneMapper.createScene(value);
    }

    @Override
    public int updateScene(SceneVO value){return sceneMapper.updateScene(value);}

    @Override
    public int deleteScene(String scene){return sceneMapper.deleteScene(scene);}

    @Override
    public PagingResult retrieve(int pageIdx, int pageSize, Map<String, List<String>> criteria) {
        PageHelper.startPage(pageIdx, pageSize);
        List<SceneVO> whiteLists = sceneMapper.retrieve(criteria);

        Page page = (Page)whiteLists;
        return PagingResult.buildResult(whiteLists,
                page.getPageNum(), page.getPageSize(),
                page.getPages(), page.getTotal());
    }

    @Override
    public PagingResult findAll(int pageIdx, int pageSize, Map<String, List<String>> criteria , String symbol, String scene) {
        PageHelper.startPage(pageIdx, pageSize);
        List<SceneVO> whiteLists = sceneMapper.findAll(symbol, scene ,criteria);

        Page page = (Page)whiteLists;
        return PagingResult.buildResult(whiteLists,
                page.getPageNum(), page.getPageSize(),
                page.getPages(), page.getTotal());
    }

    @Override
    public List<SceneVO> getAll(){
        return sceneMapper.getAll();
    }
}
