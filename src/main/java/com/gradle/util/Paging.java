/*
 * Copyright (c) 13/3/18 2:27 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.util;

import com.gradle.util.constants.ConstantProperties;
import org.apache.log4j.Logger;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;

public class Paging {

    public static final Logger logger = Logger.getLogger(Paging.class);

    private Long pageNo;

    private Long maxCount = ConstantProperties.PAGING_MAX_PER_PAGE;

    private Long count;

    private Long currentPage;

    private Long totalPages;

    private String url;

    private String showing;

    private String ajaxUrl;

    private String type;
    /**
     *
     * @param pageNo        : Page Number
     * @param count         : Total records
     * @param url           : Custom url if required
     */
    public Paging(Long pageNo, Long count, String... url) {

        // Get request object
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        // Get current requested path
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);

        // Get base path without parameters
        String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        AntPathMatcher apm = new AntPathMatcher();

        // Generate url from base path and path parameters
        String finalPath = (url.length>0) ? url[0] : DEFAULT_PATH_SEPARATOR + this.extractPathWithinPattern(bestMatchPattern, path);

        // Get first record to display
        Long firstRecord = (pageNo * maxCount) - maxCount;

        // Get last record to display
        Long lastRecord = (count-firstRecord<maxCount) ? count : (firstRecord+maxCount);

        //String restOfTheUrl = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        this.setMaxCount(getMaxCount());
        this.setCount(count);
        this.setCurrentPage(pageNo);
        this.setUrl(finalPath);
        Long totalPage = (this.getCount() % this.getMaxCount() == 0) ? (this.getCount() / this.getMaxCount()) : ((this.getCount() / this.getMaxCount()) + 1);
        this.setTotalPages(totalPage);
        if (count==0){
            this.setShowing("No records to display");
        }
        else if (firstRecord+1!= lastRecord)
            this.setShowing("Displaying records " + (firstRecord+1 ) + " to " + lastRecord + " [total records : " + count + "]");
        else
            this.setShowing("Displaying record "+lastRecord+" [total records : " + count + "]" );
    }


    /**
     * Same as above constructor just adds field for max records
     * @param pageNo
     * @param count
     * @param maxCount
     * @param url
     */
    public Paging(Long pageNo, Long count, Long maxCount ,String... url) {

        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                        .getRequest();

        this.setMaxCount((maxCount > 0) ? maxCount : getMaxCount());

        String path = (String) request.getAttribute(
                HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);

        AntPathMatcher apm = new AntPathMatcher();
        String finalPath = (url.length>0) ? url[0] : DEFAULT_PATH_SEPARATOR + this.extractPathWithinPattern(bestMatchPattern, path);

        Long firstRecord = (pageNo * this.getMaxCount()) - this.getMaxCount();
        Long lastRecord = (count-firstRecord<this.getMaxCount()) ? count : (firstRecord+this.getMaxCount());
        //String restOfTheUrl = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);

        this.setCount(count);
        this.setCurrentPage(pageNo);
        this.setUrl(finalPath);
        Long totalPage = (this.getCount() % this.getMaxCount() == 0) ? (this.getCount() / this.getMaxCount()) : ((this.getCount() / this.getMaxCount()) + 1);
        this.setTotalPages(totalPage);
        if (count==0){
            this.setShowing("No records to display");
        }
        else if (firstRecord+1!= lastRecord)
            this.setShowing("Displaying records " + (firstRecord+1 ) + " to " + lastRecord + " [total records : " + count + "]");
        else
            this.setShowing("Displaying record "+lastRecord+" [total records : " + count + "]" );
    }


    /**
     * Same as above constructor just adds field for max records
     * @param pageNo
     * @param count
     * @param maxCount
     * @param url
     */
    public Paging(Long pageNo, Long count, Long maxCount , String ajaxUrl , String type,String... url) {

        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                        .getRequest();

        this.setMaxCount((maxCount > 0) ? maxCount : getMaxCount());

        String path = (String) request.getAttribute(
                HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);

        AntPathMatcher apm = new AntPathMatcher();
        String finalPath = (url.length>0) ? url[0] : DEFAULT_PATH_SEPARATOR + this.extractPathWithinPattern(bestMatchPattern, path);

        Long firstRecord = (pageNo * this.getMaxCount()) - this.getMaxCount();
        Long lastRecord = (count-firstRecord<this.getMaxCount()) ? count : (firstRecord+this.getMaxCount());
        //String restOfTheUrl = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);

        this.setCount(count);
        this.setCurrentPage(pageNo);
        this.setUrl(finalPath);
        Long totalPage = (this.getCount() % this.getMaxCount() == 0) ? (this.getCount() / this.getMaxCount()) : ((this.getCount() / this.getMaxCount()) + 1);
        this.setTotalPages(totalPage);
        this.setAjaxUrl(ajaxUrl);
        this.setType(type);
        if (count==0){
            this.setShowing("No records to display");
        }
        else if (firstRecord+1!= lastRecord)
            this.setShowing("Displaying records " + (firstRecord+1 ) + " to " + lastRecord + " [total records : " + count + "]");
        else
            this.setShowing("Displaying record "+lastRecord+" [total records : " + count + "]" );
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getPageNo() {
        return pageNo;
    }

    public void setPageNo(Long pageNo) {
        this.pageNo = pageNo;
    }

    public Long getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(Long maxCount) {
        this.maxCount = maxCount;
    }

    public Long getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Long currentPage) {
        this.currentPage = currentPage;
    }

    public Long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Long totalPages) {
        this.totalPages = totalPages;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public String getShowing() {
        return showing;
    }

    public void setShowing(String showing) {
        this.showing = showing;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAjaxUrl() {
        return ajaxUrl;
    }

    public void setAjaxUrl(String ajaxUrl) {
        this.ajaxUrl = ajaxUrl;
    }

    public static final String DEFAULT_PATH_SEPARATOR = "/";
    private boolean trimTokens = false;

    /**
     *
     * @param pattern
     * @param path
     * @return
     */
    public String extractPathWithinPattern(String pattern, String path) {
        try {
            String[] patternParts = StringUtils.tokenizeToStringArray(pattern, DEFAULT_PATH_SEPARATOR, this.trimTokens, true);
            String[] pathParts = StringUtils.tokenizeToStringArray(path, DEFAULT_PATH_SEPARATOR, this.trimTokens, true);
            StringBuilder builder = new StringBuilder();
            for (int segment = 0; segment < patternParts.length; segment++) {
                if (pathParts[segment].equals(patternParts[segment])) {
                    builder.append(pathParts[segment] + "/");
                } else {
                    break;
                }
            }
            return builder.toString();
        }catch (Exception e){
            logger.error(e.getMessage() + e.getStackTrace());
        }
        return null;
    }
}
