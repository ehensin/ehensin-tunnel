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
package com.ehensin.tunnel.client.tunnel;

import java.io.Serializable;

import com.ehensin.tunnel.client.ErrorCodeEnum;

/**
 *
 * 
 *
 **/
@SuppressWarnings("serial")
public class TunnelResponse implements Serializable {
	private String srcMsgId;
	/* 成功与否标志 */
	private boolean isSuccess;
	/* 错误码，通过该码可以获取错误信息 */
	private ErrorCodeEnum errorCode;
	/* 错误码对应的错误信息 */
	private String errorDes;
	/* 响应对象，通过响应对象可以获取服务器返回的结果 */
	private Object obj;

	public TunnelResponse(String srcMsgId,boolean isSuccess, ErrorCodeEnum errorCode, String des,
			Object obj) {
		this.srcMsgId = srcMsgId;
		this.isSuccess = isSuccess;
		this.errorCode = errorCode;
		this.errorDes = des;
		this.obj = obj;
	}

	public ErrorCodeEnum getErrorCode() {
		return errorCode;
	}

	public String getErrorDescription() {
		return errorDes;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public Object getResponseObj() {
		return obj;
	}

	public String getSrcMsgId(){
		return this.srcMsgId;
	}
}
