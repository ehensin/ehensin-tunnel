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

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.ehensin.tunnel.client.TunnelClientManager;
import com.ehensin.tunnel.client.locator.LocatorHelper;
import com.ehensin.tunnel.client.session.ISession;
import com.ehensin.tunnel.client.session.SessionException;

public class OrderServiceClient {
	ISession session;
	@Before
	public void setUp() throws Exception {
		TunnelClientManager manager = new TunnelClientManager();
		session = manager.getSessionFactory().createStatefulSession(1000, 
				LocatorHelper.getNameLocatorFactor("portion1"), LocatorHelper.getHashLocatorFactor(1));
		
	}

	//@Test
	public void test() {
		try {
			
			PaymentOrderVO vo = new PaymentOrderVO("b6b75b0e-ad5b-46f6-a62d-563668ba06fb", "c6c04815-640c-4128-99da-bfd63d27465a",
					1000000l, "11010001", 1,
					"create a new payment order", "http://s", "testing", 1, 
					null);
			PaymentOrderItemVO item = new PaymentOrderItemVO("b6b75b0e-ad5b-46f6-a62d-563668ba06fb",
					"b6b75b0e-ad5b-46f6-a62d-563668ba06fb","7ebab613-0d90-4534-a460-b666bfa28e58", 1000000l, 1);
			List<PaymentOrderItemVO> items = new ArrayList<PaymentOrderItemVO>();
			items.add(item);
			vo.setOrderItems(items);
			
			
			String result = (String)session.syncInvoke("order-service", "directPay",new PaymentOrderVO[]{vo}, String.class);
			System.out.println("==============" + result);
		} catch (SessionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	 public void testSecurityPayUnPayedOrder() throws Exception {
		PaymentOrderItemVO item = new PaymentOrderItemVO("4917a00a-0618-4f66-9f51-e681b25f5c65",
					"be0db185-dd0a-48cd-992a-dfbc1271ed2c","7ebab613-0d90-4534-a460-b666bfa28e58",1000000l, 1);
	 		 List<PaymentOrderItemVO> items = new ArrayList<PaymentOrderItemVO>();
	 		 items.add(item);
	 		
	 		Boolean result = (Boolean)session.syncInvoke("order-service","securityPayUnPayedOrder", new Object[]{"4917a00a-0618-4f66-9f51-e681b25f5c65", "b949b33e-54bf-449a-b63e-85672b1187e6", items}, Boolean.class);
			
		}

}
