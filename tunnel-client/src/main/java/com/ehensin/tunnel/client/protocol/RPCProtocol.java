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

public class RPCProtocol extends MsgReqProtocol{
	@JsonProperty("sn")
    private String serviceName;
	@JsonProperty("sf")
    private String serviceFunc;
	/*parameter is json format*/
	@JsonProperty("params")
    private String[] params;
	@JsonProperty("result")
    private Class result;
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getServiceFunc() {
		return serviceFunc;
	}
	public void setServiceFunc(String serviceFunc) {
		this.serviceFunc = serviceFunc;
	}
	public String[] getParams() {
		return params;
	}
	public void setParams(String[] params) {
		this.params = params;
	}
	public Class getResult() {
		return result;
	}
	public void setResult(Class result) {
		this.result = result;
	}
	
	public String toString(){
		StringBuffer str = new StringBuffer("service name:").append(this.serviceName).append(", service func:")
				.append(this.serviceFunc).append("parameters:").append(this.params).append(", result class:").append(this.result.toString());
		return str.toString();
	}
}
