/*
 * Copyright 2013 The Ehensin Project
 *
 * The Ehensin Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.ehensin.tunnel.client.tunnel.account.test;

import java.util.List;

public class PaymentOrderVO {
	/**
	 * user uuid
	 * */
	private String userUuid;
	/**
	 * the payee
	 * */
	private String payee;

    /**
     * payment amount
     * */
    private Long amount;
    /**
     * if payment type, 1 security payment 2 direct payment
     * */
    private Integer paymentType;
    
    /**
	 * business order number which can be referenced to original order.
	 * this can be used to check
	 * */
    private String bizNum;
    /**
     * business type
     * */
    private Integer bizType;
    /**
     * business description for this payment
     * */
    private String bizDesc;
    /**
     * client can attach this url for redirecting to original business page
     * */
    private String bizUrl;
    /**
     * remark for this order
     * */
    private String remark;
    /**
     * channel the order from.
     * */
    private Integer channel;
    /**
     * payment order items
     * */
    private List<PaymentOrderItemVO> orderItems;
    
    public PaymentOrderVO(){
    	
    }
    
	public PaymentOrderVO(String userUuid, String payee,
			Long amount, String bizNum, Integer bizType,
			String bizDesc, String bizUrl, String remark, Integer channel, 
			 List<PaymentOrderItemVO> orderItems) {
		 
		super();
		this.userUuid = userUuid;
		this.payee = payee;
		this.amount = amount;
		this.bizNum = bizNum;
		this.bizType = bizType;
		this.bizDesc = bizDesc;
		this.bizUrl = bizUrl;
		this.remark = remark;
		this.channel = channel;
		this.orderItems = orderItems;
	}

	public String getUserUuid() {
		return userUuid;
	}
	public void setUserUuid(String userUuid) {
		this.userUuid = userUuid;
	}
	public String getPayee() {
		return payee;
	}
	public void setPayee(String payee) {
		this.payee = payee;
	}
	public Long getAmount() {
		return amount;
	}
	public void setAmount(Long amount) {
		this.amount = amount;
	}
	public Integer getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(Integer paymentType) {
		this.paymentType = paymentType;
	}
	public String getBizNum() {
		return bizNum;
	}
	public void setBizNum(String bizNum) {
		this.bizNum = bizNum;
	}
	public Integer getBizType() {
		return bizType;
	}
	public void setBizType(Integer bizType) {
		this.bizType = bizType;
	}
	public String getBizDesc() {
		return bizDesc;
	}
	public void setBizDesc(String bizDesc) {
		this.bizDesc = bizDesc;
	}
	public String getBizUrl() {
		return bizUrl;
	}
	public void setBizUrl(String bizUrl) {
		this.bizUrl = bizUrl;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Integer getChannel() {
		return channel;
	}
	public void setChannel(Integer channel) {
		this.channel = channel;
	}
	public List<PaymentOrderItemVO> getOrderItems() {
		return orderItems;
	}
	public void setOrderItems(List<PaymentOrderItemVO> orderItems) {
		this.orderItems = orderItems;
	}
    
    public String toString(){
    	StringBuffer str = new StringBuffer();
    	str.append(this.userUuid).append(this.payee).append(this.amount).append(this.paymentType)
    	   .append(this.channel).append(this.bizType).append(this.bizNum);
    	
    	return str.toString();
    }
    
}
