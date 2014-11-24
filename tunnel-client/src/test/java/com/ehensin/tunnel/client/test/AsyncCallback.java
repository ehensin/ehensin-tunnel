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
package com.ehensin.tunnel.client.test;

import com.ehensin.tunnel.client.DefaultCallBack;


public class AsyncCallback extends DefaultCallBack<ResultClass>{

	public AsyncCallback(Class<ResultClass> clazz) {
		super(clazz);
	}

	@Override
	public void callback(String srcCallId, Object object) {
		ResultClass result = (ResultClass)object;
		System.out.println("async back===========src call id : " + srcCallId + ", " + result.getTest1());
	}

	@Override
	public void exceptionCaught(String srcCallId, String errorCode,
			String errorDesc) {
		System.out.println("async exception ===========src call id : " + srcCallId + ", " + errorDesc);
		
	}


}
