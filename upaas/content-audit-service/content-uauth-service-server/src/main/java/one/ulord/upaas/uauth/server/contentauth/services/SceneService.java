package one.ulord.upaas.uauth.server.contentauth.services;

import one.ulord.upaas.common.api.PagingResult;
import one.ulord.upaas.uauth.server.contentauth.vo.SceneVO;

import java.util.List;
import java.util.Map;

/**
 * @author ShenL
 * @since 09/07/2018
 */
public interface SceneService {
    SceneVO selectScene(String scene);
    SceneVO selectSceneById(long id);
    SceneVO selectSceneBySymbol(String symbol);
    int createScene(SceneVO value);
    int updateScene(SceneVO value);
    int deleteScene(String scene);
    int selectSceneBySc(String scene);
    int selectSceneBySy(String scene);

    PagingResult retrieve(int pageIdx, int pageSize, Map<String, List<String>> criteria);


    PagingResult findAll(int pageIdx, int pageSize, Map<String, List<String>> criteria, String scene, String symbol);

    List<SceneVO> getAll();
}
