/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.uauth.server.contentauth.services.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import one.ulord.upaas.common.api.PagingResult;
import one.ulord.upaas.uauth.server.contentauth.dao.SensitiveWordMapper;
import one.ulord.upaas.uauth.server.contentauth.services.SensitiveWordService;
import one.ulord.upaas.uauth.server.contentauth.vo.SensitiveWordItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author haibo
 * @since 5/26/18
 */
@Component
public class SensitiveWordServiceImpl implements SensitiveWordService {
    @Autowired
    SensitiveWordMapper sensitiveWordMapper;


    @Override
    public int createItem(SensitiveWordItem value) {
        return sensitiveWordMapper.createItem(value);
    }

    @Override
    public int updateItem(SensitiveWordItem value) {
        return sensitiveWordMapper.updateItem(value);
    }

    @Override
    public SensitiveWordItem retrieveItem(long uid) {
        return sensitiveWordMapper.retrieveItem(uid);
    }

    @Override
    public int deleteItem(long uid) {
        return sensitiveWordMapper.deleteItem(uid);
    }

    @Override
    public List<SensitiveWordItem> loadActive() {
        return sensitiveWordMapper.loadActive();
    }

    @Override
    public PagingResult retrieve(int pageIdx, int pageSize, Map<String, List<String>> criteria) {
        PageHelper.startPage(pageIdx, pageSize);
        List<SensitiveWordItem> whiteLists = sensitiveWordMapper.retrieve(criteria);

        Page page = (Page)whiteLists;
        return PagingResult.buildResult(whiteLists,
                page.getPageNum(), page.getPageSize(),
                page.getPages(), page.getTotal());
    }

    @Override
    public int disableItem(long uid) {
        return sensitiveWordMapper.disableItem(uid);
    }

    @Override
    public int enableItem(long uid) {
        return sensitiveWordMapper.enableItem(uid);
    }
}
