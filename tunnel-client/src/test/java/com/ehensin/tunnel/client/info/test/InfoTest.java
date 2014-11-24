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
package com.ehensin.tunnel.client.info.test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import com.ehensin.tunnel.client.TunnelClientManager;
import com.ehensin.tunnel.client.info.InfoHub;
import com.ehensin.tunnel.client.info.InfoItem;
import com.ehensin.tunnel.client.locator.LocatorHelper;
import com.ehensin.tunnel.client.session.ISession;
import com.ehensin.tunnel.client.session.SessionException;
import com.ehensin.tunnel.client.test.ResultClass;

public class InfoTest {

	@Before
	public void prepare(){
		TunnelClientManager manager = new TunnelClientManager();
		ISession session = manager.getSessionFactory().createStatefulSession(1000, 
				LocatorHelper.getNameLocatorFactor("portion1"), LocatorHelper.getHashLocatorFactor(1));
		try {
			ResultClass result = (ResultClass)session.syncInvoke("test", "test",new String[]{"hello", "world"}, ResultClass.class);
			System.out.println("==============" + result.getTest1() + " " + result.getTest2());
		} catch (SessionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		InfoHub.getInfoHub();
		
		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	public void test() {
		InfoItem item = InfoHub.getInfoHub().getInfo("portion", "portion1");
		if( item != null )
		    System.out.println("portion : " + item.getType() + "," + item.getName() + ", " + item.getInfo());
		
		item = InfoHub.getInfoHub().getInfo("tunnel", "tunnel111");
		if( item != null )
			System.out.println("tunnel : " + item.getType() + "," + item.getName() + ", " + item.getInfo());

		try {
			TimeUnit.SECONDS.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<InfoItem> items = InfoHub.getInfoHub().getInfos("tunnel");
		
		for(InfoItem i : items ){
			System.out.println("tunnel : " + i.getType() + "," + i.getName() + ", " + i.getInfo());
		}
	}

}
