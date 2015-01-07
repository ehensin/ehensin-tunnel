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


public class CustomerAccountVO {
    private String uuid;
    private String phone;
    private String loginName;
    private String mail;
    private String password;
    private Integer status;
    /*cash account */
    private Integer balance1;
    /*frozen account*/
    private Integer balance2;
    
    public CustomerAccountVO(){
    	
    }
    
	public CustomerAccountVO(String uuid, String phone, String loginName,
			String mail, String password, Integer status, Integer balance1,
			Integer balance2) {
		super();
		this.uuid = uuid;
		this.phone = phone;
		this.loginName = loginName;
		this.mail = mail;
		this.password = password;
		this.status = status;
		this.balance1 = balance1;
		this.balance2 = balance2;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getBalance1() {
		return balance1;
	}

	public void setBalance1(Integer balance1) {
		this.balance1 = balance1;
	}

	public Integer getBalance2() {
		return balance2;
	}

	public void setBalance2(Integer balance2) {
		this.balance2 = balance2;
	}

	

}
