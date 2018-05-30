/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.uauth.server.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author haibo
 * @since 5/29/18
 */
@Slf4j
public class RequestUtils {
    public static final String HTTP_CLIENT_IP = "HTTP_CLIENT_IP";
    public static final String X_Forwarded_For = "X-Forwarded-For";
    public static final String Proxy_Client_IP = "Proxy-Client-IP";
    public static final String WL_Proxy_Client_IP = "WL-Proxy-Client-IP";
    public static final String HTTP_X_FORWARDED_FOR = "HTTP_X_FORWARDED_FOR";
    public static final String PROXY_HEADERS[] = new String[]{
            X_Forwarded_For, Proxy_Client_IP, WL_Proxy_Client_IP, HTTP_CLIENT_IP, HTTP_X_FORWARDED_FOR
    };

    public static final String CRITERIA_SPLITER = "[;]+";
    public static final String CRITERIA_VALUE_SPLITER = "[,]+";
    public static final String CRITERIA_ALL_KEY = "ALL";
    public static final String CRITERIA_PAGE_NUM = "pageNum";
    public static final String CRITERIA_PAGE_SIZE = "pageSize";

    /**
     * Resolve criteria string.
     * (1) value1,value2 ==> ALL=>value1, value2
     * key1=value;key2=value ==> KEY1=>value1, KEY2=>value2
     * key=value1,value2,value3 ==> KEY=>value1, value2, value3
     * @param criteria
     * @return
     */
    public static Map<String, List<String>> resolveCriteria(String criteria){
        Map<String, List<String>> criteriaMap = new HashMap<>();
        if (criteria != null){
            String[] criteriaItem = criteria.split(CRITERIA_SPLITER);
            if (criteriaItem != null){
                for (String item : criteriaItem){
                    int equalPos = item.indexOf("=");
                    if (equalPos >= 0){
                        String key = item.substring(0, equalPos).trim();
                        String value = item.substring(equalPos+1).trim();
                        if(!StringUtils.isEmpty(key) && !StringUtils.isEmpty(value)){
                            String[] criteriaValues = value.split(CRITERIA_VALUE_SPLITER);
                            addCriteria(criteriaMap, key, criteriaValues);
                        }else{
                            log.warn("Empty criteria item: {}, skiped.", item);
                        }
                    }else{
                        String value = item.trim();
                        if (!StringUtils.isEmpty(value)) {
                            String[] criteriaValues = value.split(CRITERIA_VALUE_SPLITER);
                            addCriteria(criteriaMap, CRITERIA_ALL_KEY, criteriaValues);
                        }
                    }
                }
            }
        }

        return criteriaMap;
    }

    private static void addCriteria(Map<String, List<String>> criteriaMap, String key, String[] criteriaValues) {
        if (criteriaMap.containsKey(key)){
            List<String> values = criteriaMap.get(key);
            values.addAll(Arrays.asList(criteriaValues));
        }else{
            List<String> values = new ArrayList<>();
            values.addAll(Arrays.asList(criteriaValues));
            criteriaMap.put(key, values);
        }
    }

    /**
     * Get client ip address, even back the nginx or apache
     * @param request
     * @return
     */
    public static String getClientIpAddr(HttpServletRequest request) {
        for (int i = 0; i < PROXY_HEADERS.length; i++){
            String headValue = request.getHeader(PROXY_HEADERS[i]);
            if (headValue != null && "unknown".equalsIgnoreCase(headValue)){
                if (headValue.indexOf(",") >= 0){
                    // Get last item for the true user ip
                    String[] ips = headValue.split(",");
                    for (int j = ips.length - 1; j > 0; j--){
                        if (!StringUtils.isEmpty(ips[j])){
                            return ips[j];
                        }
                    }
                }
            }
        }

        return request.getRemoteAddr();
    }

    public static int copyRequestIPHeader(HttpServletRequest request, HttpHeaders httpHeaders){
        int count = 0;

        for (int i = 0; i < PROXY_HEADERS.length; i++){
            String headValue = request.getHeader(PROXY_HEADERS[i]);
            if (headValue != null){
                httpHeaders.set(PROXY_HEADERS[i], headValue);
                count++;
            }
        }

        if (count == 0){
            // No invalid a client ip, using remote ip
            httpHeaders.set(HTTP_CLIENT_IP, request.getRemoteAddr());
        }

        return count;
    }
}