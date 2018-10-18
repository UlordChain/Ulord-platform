package one.ulord.upaas.uauth.server.contentauth.dao;

import one.ulord.upaas.uauth.server.contentauth.vo.SceneVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author ShenL
 * @since 09/07/2018
 */
public interface SceneMapper {
    SceneVO selectScene(String scene);
    SceneVO selectSceneById(long id);
    SceneVO selectSceneBySymbol(String symbol);
    int createScene(SceneVO value);
    int updateScene(SceneVO value);
    int deleteScene(String scene);
    int selectSceneBySc(String scene);
    int selectSceneBySy(String scene);

    List<SceneVO> retrieve(Map<String, List<String>> criteria);


    List<SceneVO> findAll( @Param("scene") String scene, String symbol, Map<String, List<String>> criteria);

    List<SceneVO> getAll();

}
