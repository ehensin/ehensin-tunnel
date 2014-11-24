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

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * message protocol: abstract class, any protocol must extends this class.
 * */
public class MsgProtocol {
	@JsonProperty("header")
    private MsgHeader msgHeader;
	@JsonProperty("parameters")
    private Map<String, String> parameters;
	@JsonProperty("body")
	private MsgBody body;
    
	public MsgHeader getMsgHeader() {
		return msgHeader;
	}
	public void setMsgHeader(MsgHeader msgHeader) {
		this.msgHeader = msgHeader;
	}
	public Map<String, String> getParameters() {
		return parameters;
	}
	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}
	public MsgBody getBody() {
		return body;
	}
	public void setBody(MsgBody body) {
		this.body = body;
	}
	
	public String toString(){
		StringBuffer str = new StringBuffer("message type:").append(msgHeader.getMsgType()).append(", message id:")
				.append(msgHeader.getMsgId());
		if(body != null )
			str.append(", message body : ").append(body.toString());
		return str.toString();
	}
    
}
