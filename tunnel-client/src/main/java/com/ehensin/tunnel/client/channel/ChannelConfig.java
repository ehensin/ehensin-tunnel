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
package com.ehensin.tunnel.client.channel;

import com.ehensin.tunnel.client.profile.ChannelIntercepter;



public class ChannelConfig {
    /*working thread counts for a connection*/
	private int workThreads;
	/*communication protocol, now only support udp or tcp*/
	private String protocalType;
	/*keep alive for long connection*/
	private boolean keepalive;
	/*blocking or nio mode*/
	private boolean isBlocking;
	/*target device ip*/
	private String ip;
	/*target device port*/
	private int port;
    /*connection timeout*/
	private long connectionTimeout;
    /*socket timeout*/
	private long socketTimeout;
	
	private boolean isSSLSupport = false;
	/*keystore path*/
	private String keyStorePath;
	/*keystore password*/
	private String keyStorePwd;
	
	private boolean isSyncCall;
	
	private ChannelIntercepter intercepters;

	public int getWorkThreads() {
		return workThreads;
	}

	public void setWorkThreads(int workThreads) {
		this.workThreads = workThreads;
	}

	public String getProtocalType() {
		return protocalType;
	}

	public void setProtocalType(String protocalType) {
		this.protocalType = protocalType;
	}

	public boolean isKeepalive() {
		return keepalive;
	}

	public void setKeepalive(boolean keepalive) {
		this.keepalive = keepalive;
	}

	public boolean isBlocking() {
		return isBlocking;
	}

	public void setBlocking(boolean isBlocking) {
		this.isBlocking = isBlocking;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
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

	public boolean isSSLSupport() {
		return isSSLSupport;
	}

	public void setSSLSupport(boolean isSSLSupport) {
		this.isSSLSupport = isSSLSupport;
	}

	public String getKeyStorePath() {
		return keyStorePath;
	}

	public void setKeyStorePath(String keyStorePath) {
		this.keyStorePath = keyStorePath;
	}

	public String getKeyStorePwd() {
		return keyStorePwd;
	}

	public void setKeyStorePwd(String keyStorePwd) {
		this.keyStorePwd = keyStorePwd;
	}

	public boolean isSyncCall() {
		return isSyncCall;
	}

	public void setSyncCall(boolean isSyncCall) {
		this.isSyncCall = isSyncCall;
	}

	public ChannelIntercepter getIntercepters() {
		return intercepters;
	}

	public void setIntercepters(ChannelIntercepter intercepters) {
		this.intercepters = intercepters;
	}
    
}
