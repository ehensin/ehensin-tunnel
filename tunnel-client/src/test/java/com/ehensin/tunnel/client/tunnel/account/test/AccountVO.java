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


public class AccountVO {
	/*user uuid the account related to*/
	private String userUuid;
    private String accountUuid;
    private String accountName;
    private Integer status;
    /*cash account */
    private Long balance;


    public AccountVO(){
    	
    }


	public AccountVO(String userUuid, String accountUuid, String accountName,
			Integer status, Long balance) {
		super();
		this.userUuid = userUuid;
		this.accountUuid = accountUuid;
		this.accountName = accountName;
		this.status = status;
		this.balance = balance;
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


	public String getAccountName() {
		return accountName;
	}


	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}


	public Integer getStatus() {
		return status;
	}


	public void setStatus(Integer status) {
		this.status = status;
	}


	public Long getBalance() {
		return balance;
	}


	public void setBalance(Long balance) {
		this.balance = balance;
	}
	
    
	public String toString(){
		StringBuffer str = new StringBuffer("uuid : ").append(this.accountUuid).append(" balance : ").append(this.balance).
				append(" name : ").append(this.accountName).append(" status : ").append(this.status).append(" user : ").
				append(this.userUuid);
		return str.toString();
	}

	
}
