/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.uauth.client.contentauth.service.impl;

import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.IndexTokenizer;
import lombok.extern.slf4j.Slf4j;
import one.ulord.upaas.common.api.APIResult;
import one.ulord.upaas.uauth.client.contentauth.bo.ContentAuthBody;
import one.ulord.upaas.uauth.client.contentauth.bo.UPaaSUAuthErrorCode;
import one.ulord.upaas.uauth.client.contentauth.business.SensitiveWordSyncer;
import one.ulord.upaas.uauth.client.contentauth.service.ContentAuthService;
import one.ulord.upaas.uauth.common.vo.SensitiveWord;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Content auth service implements
 *
 * @author haibo
 * @since 5/30/18
 */
@Service
@Slf4j
public class ContentAuthServiceImpl implements ContentAuthService {
    @Autowired
    SensitiveWordSyncer sensitiveWordSyncer;

    @Override
    public APIResult contentAuth(ContentAuthBody content) {
        List<Term> terms = null;
        switch (content.getFormat()) {
            case TEXT:
                terms = IndexTokenizer.segment(content.getContent());
                break;
            case HTML:
                Document doc = Jsoup.parse(content.getContent());
                terms = IndexTokenizer.segment(doc.body().text());
                break;
        }

        if (terms != null) {
            List<SensitiveWord> hitWords = new ArrayList<>();
            for (Term term : terms) {
                SensitiveWord hit = sensitiveWordSyncer.hitSensitiveWord(term.word);
                if (hit != null && hit.hitScene(content.getScene())) {
                    hitWords.add(hit);
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("keywords", hitWords);
            result.put("violateCount", hitWords.size());
            return APIResult.buildResult(result);
        } else {
            log.warn("Cannot parse content:format:{}, content:{}", content.getFormat(), content.getContent());
        }
        return APIResult.buildError(UPaaSUAuthErrorCode.UAUTH_CONTENT_PARSE_FAILURE, "Cannot parse content.");
    }
}
