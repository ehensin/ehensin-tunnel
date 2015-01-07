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

public class CapitalServiceClient {
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
			TransferVO vo = new TransferVO("30001001", null , 0, 0, "b6b75b0e-ad5b-46f6-a62d-563668ba06fb", 1,
					1, 2000000l, "client test recharge" );
			Boolean result = (Boolean)session.syncInvoke("capital-service", "transfer",new TransferVO[]{vo}, Boolean.class);
			System.out.println("==============" + result);
		} catch (SessionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
