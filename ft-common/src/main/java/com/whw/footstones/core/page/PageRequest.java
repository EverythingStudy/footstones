package com.whw.footstones.core.page;import io.swagger.annotations.ApiModelProperty;import javax.validation.constraints.Min;import java.beans.Transient;/** * @version 1.0 * @description: TODO * @date 2021/9/24 2:59 PM */public class PageRequest {    @Min(0)    @ApiModelProperty(value = "页码", example = "1")    private Integer page = 1;    @ApiModelProperty(value = "单页大小", example = "10")    @Min(1)    private Integer pageSize = 10;    @ApiModelProperty("排序字段")    private String sortField;    @ApiModelProperty("排序方式？asc或者desc")    private String sortOrder;    public PageRequest() {    }    public PageRequest(int page, int pageSize) {        this.page = page;        this.pageSize = pageSize;    }    public Integer getPage() {        return page;    }    public void setPage(Integer page) {        this.page = page;    }    public Integer getPageSize() {        return pageSize;    }    public void setPageSize(Integer pageSize) {        this.pageSize = pageSize;    }    public String getSortField() {        return sortField;    }    public void setSortField(String sortField) {        this.sortField = sortField;    }    public String getSortOrder() {        return sortOrder;    }    public void setSortOrder(String sortOrder) {        this.sortOrder = sortOrder;    }    @Transient    public String getSortCondition() {        if (sortField == null || "".equals(sortField)) {            return null;        }        StringBuilder sortStr = new StringBuilder();        sortStr.append(" order by ")                .append(sortField)                .append("asc".equals(sortOrder) ? " asc "                        : " desc ");        return sortStr.toString();    }    @Transient    public String getLimitCondition() {        StringBuilder limitStr = new StringBuilder();        limitStr.append(" limit ").append((page - 1) * pageSize).append(" , ")                .append(pageSize);        return limitStr.toString();    }}