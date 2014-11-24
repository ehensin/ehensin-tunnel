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
package com.ehensin.tunnel.client.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MsgRepProtocol extends MsgBody{
	/*source request message id*/
	@JsonProperty("smid")
	private String srcMsgId;
	/*request result code : 0 success, 1 failed*/
	@JsonProperty("sc")
	private int statusCode;
	/*request error code, remote server handle request failed and throw error*/
	@JsonProperty("ec")
	private String errorCode;
	/*remote server handle request failed and return exception information*/
	@JsonProperty("e")
    private String exception;
	
	@JsonProperty("result")
	private String result;

	public String getSrcMsgId() {
		return srcMsgId;
	}

	public void setSrcMsgId(String srcMsgId) {
		this.srcMsgId = srcMsgId;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getException() {
		return exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	
	public String toString(){
		StringBuffer str = new StringBuffer("status:");
		str.append(this.statusCode).append(", result : ").append(result);
		return str.toString();
		
	}

}
