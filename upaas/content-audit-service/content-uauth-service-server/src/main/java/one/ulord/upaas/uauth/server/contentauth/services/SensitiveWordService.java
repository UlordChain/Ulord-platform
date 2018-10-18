/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.uauth.server.contentauth.services;

import one.ulord.upaas.common.api.PagingResult;
import one.ulord.upaas.uauth.server.contentauth.vo.SensitiveWordItem;

import java.util.List;
import java.util.Map;

/**
 * @author haibo
 * @since 5/26/18
 */
public interface SensitiveWordService {
    int createItem(SensitiveWordItem value);
    int updateItem(SensitiveWordItem value);
    SensitiveWordItem retrieveItem(long uid);
    int deleteItem(long uid);

    List<SensitiveWordItem> loadActive();
    PagingResult retrieve(int pageIdx, int pageSize, Map<String, List<String>> criteria);


    int disableItem(long uid);
    int enableItem(long uid);

    SensitiveWordItem select(String keyword);

    PagingResult selectByItem(int pageIdx, int pageSize, Map<String, List<String>> criteria, String keyword, int level, int disabled, String scene);
}
