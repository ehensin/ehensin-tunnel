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
@XmlRootElement(name = "policy")
public class TunnelPolicy {
	@XmlElement(name="locator")
	private LocatorPolicy locatorPolicy;
	@XmlElement(name="flow-control")
	private TunnelFlowControl flowControl;
	@XmlElement(name="security")
	private TunnelSecurity security;
	@XmlElement(name="channel")
	private TunnelChannelProfile socket;
	@XmlElement(name="tunnel-intercepter")
	private String interceptors;
	
	public LocatorPolicy getLocatorPolicy() {
		return locatorPolicy;
	}
	public void setLocatorPolicy(LocatorPolicy locatorPolicy) {
		this.locatorPolicy = locatorPolicy;
	}
	public TunnelFlowControl getFlowControl() {
		return flowControl;
	}
	public void setFlowControl(TunnelFlowControl flowControl) {
		this.flowControl = flowControl;
	}
	public TunnelSecurity getSecurity() {
		return security;
	}
	public void setSecurity(TunnelSecurity security) {
		this.security = security;
	}
	public TunnelChannelProfile getSocket() {
		return socket;
	}
	public void setSocket(TunnelChannelProfile socket) {
		this.socket = socket;
	}
	public String getInterceptors() {
		return interceptors;
	}
	public void setInterceptors(String interceptors) {
		this.interceptors = interceptors;
	}
	
	
}
