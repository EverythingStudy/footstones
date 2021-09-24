package com.whw.footstones.controller;import com.github.pagehelper.PageHelper;import com.github.pagehelper.PageInfo;import com.whw.footstones.core.page.PageRequest;import com.whw.footstones.core.page.PageResponse;import java.util.ArrayList;import java.util.List;/** * @author cly * @version 1.0 * @description: TODO 分页处理 * @date 2021/9/24 3:07 PM */public class PagerController {    public PageResponse<String> getPageList(PageRequest request) {        PageHelper.startPage(request.getPage(), request.getPageSize());        List<String> reposeList = new ArrayList<>();        PageInfo<String> pageInfo = new PageInfo<>(reposeList);        return new PageResponse<>(pageInfo.getTotal(), reposeList, new PageRequest(pageInfo.getPageNum(), pageInfo.getPageSize()));    }}