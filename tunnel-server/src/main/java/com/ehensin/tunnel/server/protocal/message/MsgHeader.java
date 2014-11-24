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
package com.ehensin.tunnel.server.protocal.message;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * message header
 * */
public class MsgHeader {
	/*current message version*/
    private String version = "1.0";
    /*unique message id*/
    private String msgId;
    /* method : request or response */
    private String method;
    /* message timestamp */
    private long timestamp;
    /*source ip*/
    private String srcIp;
    /*target ip*/
    private String targetIp;
    /* body format: now only support json format */
    private String format = "json";
    /*message type*/
    @JsonProperty("msgtype")
	private int msgType ;

	public int getMsgType() {
		return msgType;
	}

	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}
    
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getMsgId() {
		return msgId;
	}
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public String getSrcIp() {
		return srcIp;
	}
	public void setSrcIp(String srcIp) {
		this.srcIp = srcIp;
	}
	public String getTargetIp() {
		return targetIp;
	}
	public void setTargetIp(String targetIp) {
		this.targetIp = targetIp;
	}
        
}
