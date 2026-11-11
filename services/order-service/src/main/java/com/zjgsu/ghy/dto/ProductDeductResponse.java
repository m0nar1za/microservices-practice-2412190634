package com.zjgsu.ghy.dto;

public class ProductDeductResponse {

    private String instanceId;
    private Integer remainingStock;

    public ProductDeductResponse() {
    }

    public ProductDeductResponse(String instanceId, Integer remainingStock) {
        this.instanceId = instanceId;
        this.remainingStock = remainingStock;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public Integer getRemainingStock() {
        return remainingStock;
    }

    public void setRemainingStock(Integer remainingStock) {
        this.remainingStock = remainingStock;
    }
}
