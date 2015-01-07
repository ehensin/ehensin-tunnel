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
/**
 * any transaction connects payer and payee,
 * any account has three account type: cash account, security account, frozen account
 * one transaction can be transfer capital from one account type to another account type which reference to the same
 * account.<p>
 * 
 * different transaction have different situation<p>
 * 1、pay<p>
 *  1) direct payment: source account to target account<p>
 *  2) security payment: source cash account to the security account<p>
 *                       then the source security account to target account<p>
 * 
 * 2、draw <p>
 *     source cash account to the frozen account<p>
 *     the source frozen account to the account which is the external system's account<p>
 * 3、Recharges<p>
 *     the external system's account to the target account 
 * */
public class TransferVO {
	/**
	 * business order number which can be referenced to original order.
	 * this can be used to check
	 * */
    private String bizNum;
    /**
     * source account uuid
     * */
    private int srcIdentityType;
    private String srcAccountUuid;
    private int srcAccountType;
    /**
     * target account uuid
     * */
    private int targetIdentityType;
    private String targetAccountUuid;
    private int targetAccountType;
    /**
     * transfer amount, monetary unit centi, 已厘为单位，也就是正常金额 x 1000
     * */
    private Long amount;
    /**
     * transfer description 
     * */
    private String comments;
    public TransferVO(){
    	
    }
	public TransferVO(String bizNum, String srcAccountUuid, int srcAccountType, int srcIdentityType,
			String targetAccountUuid, int targetAccountType, int targetIdentityType, Long amount,
			String comments) {
		super();
		this.bizNum = bizNum;
		this.srcAccountUuid = srcAccountUuid;
		this.srcAccountType = srcAccountType;
		this.srcIdentityType = srcIdentityType;
		this.targetAccountUuid = targetAccountUuid;
		this.targetAccountType = targetAccountType;
		this.targetIdentityType = targetIdentityType;
		this.amount = amount;
		this.comments = comments;
	}
	public String getBizNum() {
		return bizNum;
	}
	public void setBizNum(String bizNum) {
		this.bizNum = bizNum;
	}
	public String getSrcAccountUuid() {
		return srcAccountUuid;
	}
	public void setSrcAccountUuid(String srcAccountUuid) {
		this.srcAccountUuid = srcAccountUuid;
	}
	public int getSrcAccountType() {
		return srcAccountType;
	}
	public void setSrcAccountType(int srcAccountType) {
		this.srcAccountType = srcAccountType;
	}
	public String getTargetAccountUuid() {
		return targetAccountUuid;
	}
	public void setTargetAccountUuid(String targetAccountUuid) {
		this.targetAccountUuid = targetAccountUuid;
	}
	public int getTargetAccountType() {
		return targetAccountType;
	}
	public void setTargetAccountType(int targetAccountType) {
		this.targetAccountType = targetAccountType;
	}
	public Long getAmount() {
		return amount;
	}
	public void setAmount(Long amount) {
		this.amount = amount;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public int getSrcIdentityType() {
		return srcIdentityType;
	}
	public void setSrcIdentityType(int srcIdentityType) {
		this.srcIdentityType = srcIdentityType;
	}
	public int getTargetIdentityType() {
		return targetIdentityType;
	}
	public void setTargetIdentityType(int targetIdentityType) {
		this.targetIdentityType = targetIdentityType;
	}
    
    
}
