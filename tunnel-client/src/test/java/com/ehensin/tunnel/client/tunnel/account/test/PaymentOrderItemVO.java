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


public class PaymentOrderItemVO {

	/**
	 * user uuid
	 * */
	private String userUuid;
	/**
	 * the account uuid
	 * */
	private String accountUuid;

    /**
     * payment amount
     * */
    private Long amount;
    /**
     * if account type, 1 cash account 2 red bag account
     * */
    private Integer accountType;
    
    public PaymentOrderItemVO(){
    	
    }
    
	public PaymentOrderItemVO(String userUuid,
			String accountUuid, Long amount, Integer accountType) {
		super();
		this.userUuid = userUuid;
		this.accountUuid = accountUuid;
		this.amount = amount;
		this.accountType = accountType;
	}

	public String getUserUuid() {
		return userUuid;
	}

	public void setUserUuid(String userUuid) {
		this.userUuid = userUuid;
	}

	public String getAccountUuid() {
		return accountUuid;
	}

	public void setAccountUuid(String accountUuid) {
		this.accountUuid = accountUuid;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public Integer getAccountType() {
		return accountType;
	}

	public void setAccountType(Integer accountType) {
		this.accountType = accountType;
	}

    
}
