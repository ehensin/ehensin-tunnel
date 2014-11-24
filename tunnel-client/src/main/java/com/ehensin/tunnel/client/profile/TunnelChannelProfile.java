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
package com.ehensin.tunnel.client.profile;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "channel")
public class TunnelChannelProfile {
	@XmlElement(name="size")
	private int size;
	
	@XmlElement(name="workthread")
	private int workThreads;
	
	@XmlElement(name="type")
	private String type;
	
	@XmlElement(name="keepalive")
	private String keeplive;
	
	@XmlElement(name="blocking")
	private String blocking;
	
	@XmlElement(name="connection-timeout")
	private long connectionTimeout;
	
	@XmlElement(name="socket-timeout")
	private long socketTimeout;
	
	@XmlElement(name="msg-mode")
	private String isSyncCall;
	
	@XmlElement(name="channel-intercepter")
	private ChannelIntercepter interceptors;
	
	public String getKeeplive() {
		return keeplive;
	}
	public void setKeeplive(String keeplive) {
		this.keeplive = keeplive;
	}
	public String getBlocking() {
		return blocking;
	}
	public void setBlocking(String blocking) {
		this.blocking = blocking;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public int getWorkThreads() {
		return workThreads;
	}
	public void setWorkThreads(int workThreads) {
		this.workThreads = workThreads;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public long getConnectionTimeout() {
		return connectionTimeout;
	}
	public void setConnectionTimeout(long connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}
	public long getSocketTimeout() {
		return socketTimeout;
	}
	public void setSocketTimeout(long socketTimeout) {
		this.socketTimeout = socketTimeout;
	}
	public String getIsSyncCall() {
		return isSyncCall;
	}
	public void setIsSyncCall(String isSyncCall) {
		this.isSyncCall = isSyncCall;
	}
	public ChannelIntercepter getInterceptors() {
		return interceptors;
	}
	public void setInterceptors(ChannelIntercepter interceptors) {
		this.interceptors = interceptors;
	}

}
