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
package com.ehensin.tunnel.client.session;

import com.ehensin.tunnel.client.DefaultCallBack;
import com.ehensin.tunnel.client.locator.LocatorFactor;

/**
 * session interface for application 
 * */
public interface ISession {
	/**
	 * get session id;
	 * */
    public long getId();
    /**
     * get session timeout, no timeout return -1
     * */
    public long getTimeout();
    /**
     * set session timeout
     * */
	public void setTimeout(long timeout);
	/**
	 * get session creation time
	 * */
	public long getCreationTime();
	/**
	 * set session attribute
	 * */
	public void setAttribute(String name, Object obj);
	/**
	 * get session attribute
	 * */
	public Object getAttribute(String name);
	/**
	 * get session last access time
	 * */
	public long getLastAccessTime();
	/**
	 * invalid current session
	 * */
	public void invalidate()throws SessionException;
	/**
	 * check if current session is valid
	 * */
	public boolean isValidate();

	/**
	 * invoke a remote function, if callback is null, adopt sync call, else async call.
	 * if user adopt async call, the invoke will return a call id to application, which presents an invokation.
	 * when invoke return ,the callback will get a callback whith the same call id 
	 * @param serviceName remote service name
	 * @param function function under this service name
	 * @param params function parameters
	 * @param callback  callback object
	 * @return invoke result : call id when async call, or remote service call result when sync call  
	 * */
	public Object syncInvoke(String serviceName, String function, Object[]params, Class<?> resultClass) throws SessionException;
	public String asyncInvoke(String serviceName, String function, Object[]params, DefaultCallBack<?> callback) throws SessionException;
	
	/**
	 * plain invoke, client customized protocol
	 * */
	public String syncPlainInvoke(String plainText) throws SessionException;
	public String asyncPlainInvoke(String plainText, DefaultCallBack<?> callback) throws SessionException;
	
}
