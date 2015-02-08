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
	public void testGetConsumer() {
		try {
			ConsumerVO result = (ConsumerVO)session.syncInvoke("account-service", "getConsumer",new Object[]{"99de4b41-d656-48f2-871b-d4f4333ef732", Boolean.TRUE}, ConsumerVO.class);
			System.out.println("==============" + result.getUuid() + " " + result.getMail() + " " + result.getName() + " " + result.getPhone()
					+ " " + result.getPassword() + " " +result.getStatus() + " " + result.getAccount().getBalance());
		} catch (SessionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGetAccounts() {
		try {
			AccountVO[] results = (AccountVO[])session.syncInvoke("account-service", "getAccounts",new Object[]{"99de4b41-d656-48f2-871b-d4f4333ef732"}, AccountVO[].class);
			AccountVO result = results[0];
			System.out.println("==============" + result.getAccountUuid() + " " + result.getAccountName() + " " + result.getBalance());
		} catch (SessionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//@Test
	public void testCreateConsumer() {
		try {
			ConsumerVO vo = new ConsumerVO();			
			vo.setIdentity("22222222211111111111111");
			vo.setMail("zzd@newcosoft.com1");
			vo.setPhone("13917310926");
			vo.setName("zzd");
			vo.setPassword("123456");
			vo.setPayPassword("654321");

			String result = (String)session.syncInvoke("account-service", "registerConsumer",new Object[]{vo}, String.class);
			
			System.out.println("aaaaaaaaaaaaaa==============" + result.toString() );
		} catch (SessionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
