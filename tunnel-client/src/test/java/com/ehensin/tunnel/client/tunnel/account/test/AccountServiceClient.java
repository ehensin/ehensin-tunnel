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
package com.ehensin.tunnel.client.tunnel.account.test;

import org.junit.Before;
import org.junit.Test;

import com.ehensin.tunnel.client.TunnelClientManager;
import com.ehensin.tunnel.client.locator.LocatorHelper;
import com.ehensin.tunnel.client.session.ISession;
import com.ehensin.tunnel.client.session.SessionException;

public class AccountServiceClient {
	ISession session;
	@Before
	public void setUp() throws Exception {
		TunnelClientManager manager = new TunnelClientManager();
		session = manager.getSessionFactory().createStatefulSession(1000, 
				LocatorHelper.getNameLocatorFactor("portion1"), LocatorHelper.getHashLocatorFactor(1));
		
	}

	@Test
	public void test() {
		try {
			CustomerAccountVO result = (CustomerAccountVO)session.syncInvoke("account-service", "getCustomerAccount",new String[]{"123"}, CustomerAccountVO.class);
			System.out.println("==============" + result.getUuid() + " " + result.getMail() + " " + result.getLoginName() + " " + result.getPhone()
					+ " " + result.getPassword() + " " +result.getStatus() + " " + result.getBalance1() + " " + result.getBalance2());
		} catch (SessionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*@Test
	public void testCreate() {
		try {
			CustomerAccountVO vo = new CustomerAccountVO();
			vo.setUuid("124");
			vo.setLoginName("zzd");
			vo.setPhone("13917310926");
			vo.setMail("hhbzzd@sina.com");
			vo.setBalance1(0);
			vo.setBalance2(0);
			vo.setStatus(0);
			vo.setPassword("123456");

			Boolean result = (Boolean)session.syncInvoke("account-service", "registerCustomer",new Object[]{vo}, Boolean.class);
			
			System.out.println("aaaaaaaaaaaaaa==============" + result.toString() );
		} catch (SessionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/

}
