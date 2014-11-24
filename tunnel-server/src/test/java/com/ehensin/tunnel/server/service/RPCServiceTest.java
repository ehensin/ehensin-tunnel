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
package com.ehensin.tunnel.server.service;

import org.junit.Before;
import org.junit.Test;

import com.ehensin.tunnel.server.ServerStartup;
import com.ehensin.tunnel.server.protocol.service.rpc.ServiceInvoker;
import com.ehensin.tunnel.server.protocol.service.rpc.ServiceRegistry;

public class RPCServiceTest {
   @Before
   public void init(){
	   ServerStartup.getStartup().start();
   }
	@Test
	public void test() {
		ServiceInvoker invoker = ServiceRegistry.getRegistry().getService("test");
		String[] params = null;
		try {
			params = new String[]{"hello world"};
			Object result = invoker.invoke("test", params);
			System.out.println(result);
			result = invoker.invoke("test", null);
			System.out.println(result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}