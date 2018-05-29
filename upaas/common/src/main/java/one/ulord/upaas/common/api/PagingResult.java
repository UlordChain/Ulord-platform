/**
 * Copyright(c) 2018
 * Ulord core developers
 */
package one.ulord.upaas.common.api;

import one.ulord.upaas.common.UPaaSErrorCode;

/**
 * @author haibo
 * @since 5/28/18
 */
public class PagingResult extends APIResult {
    // 记录总数量
    private long totalRecords;
    // 总页数
    private int pages;
    // 当前页码
    private int pageNum;
    // 页面元素数
    private int pageSize;
    // 是否有更多
    private boolean hasMore;

    public long getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(long totalRecords) {
        this.totalRecords = totalRecords;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public boolean isHasMore() {
        return pageNum < pages;
    }



    public static PagingResult buildResult(Object result, int pageNum, int pageSize, int pages, long totalRecords){
        PagingResult r = new PagingResult();
        r.setResult(result);
        r.setErrorCode(UPaaSErrorCode.SUCCESS);
        r.setReason("OK");
        r.setPageNum(pageNum);
        r.setPageSize(pageSize);
        r.setPages(pages);
        r.setTotalRecords(totalRecords);

        return r;
    }
}
