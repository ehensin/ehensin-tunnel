/*
 * Copyright 2013 The Ehensin Tunnel Project
 *
 * The Ehensin Tunnel Project licenses this file to you under the Apache License,
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
package com.ehensin.tunnel.client.partion.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.ehensin.tunnel.client.locator.LocatorFactor;
import com.ehensin.tunnel.client.portion.Portion;
import com.ehensin.tunnel.client.portion.PortionFactory;
import com.ehensin.tunnel.client.profile.LocatorAlg;
import com.ehensin.tunnel.client.profile.LocatorFactorType;
import com.ehensin.tunnel.client.profile.LocatorPolicy;
import com.ehensin.tunnel.client.profile.PortionPolicy;
import com.ehensin.tunnel.client.profile.PortionProfile;
import com.ehensin.tunnel.client.profile.PortionsPolicy;
import com.ehensin.tunnel.client.profile.PortionsProfile;
import com.ehensin.tunnel.client.profile.TunnelChannelProfile;
import com.ehensin.tunnel.client.profile.TunnelDes;
import com.ehensin.tunnel.client.profile.TunnelFlowControl;
import com.ehensin.tunnel.client.profile.TunnelList;
import com.ehensin.tunnel.client.profile.TunnelPolicy;
import com.ehensin.tunnel.client.profile.TunnelProfile;
import com.ehensin.tunnel.client.profile.TunnelSecurity;
import com.ehensin.tunnel.client.tunnel.Tunnel;

public class PartionTest {

	@Test
	public void RRTest() {
		System.out.println("round robin testing");
		//fail("Not yet implemented"); 
		PortionsProfile pp = new PortionsProfile();
		PortionsPolicy policy = new PortionsPolicy();
		pp.setPolicy(policy);
		
		LocatorPolicy locatorPolicy = new LocatorPolicy();
		policy.setLocatorPolicy(locatorPolicy);		
		locatorPolicy.setName("portions");
		LocatorAlg locatorAlg = new LocatorAlg();
		locatorAlg.setType("rr");
		locatorPolicy.setLocatorAlg(locatorAlg);
		LocatorFactorType factor = new LocatorFactorType();
		factor.setType("java.lang.Integer");
		locatorPolicy.setFactorType( factor );
		
		List<PortionProfile> portions = new ArrayList<PortionProfile>();
		pp.setPortions(portions );
		PortionProfile p = new PortionProfile();
		portions.add(p);
		p.setId(1);
		p.setName("default");
		PortionPolicy ppolicy = new PortionPolicy();
		ppolicy.setDegrade("auto");
		ppolicy.setDiscover("fix");
		ppolicy.setLocatorPolicy(locatorPolicy);
		p.setPolicy(ppolicy );
		
		TunnelProfile tp = new TunnelProfile();
		p.setTunnelProfile(tp);
		TunnelPolicy tpolicy = new TunnelPolicy();
		tp.setPolicy(tpolicy );
		TunnelFlowControl flowContrl = new TunnelFlowControl();
		flowContrl.setMaxtps(100);
		flowContrl.setSwich("on");
		tpolicy.setFlowControl(flowContrl );
		tpolicy.setLocatorPolicy(locatorPolicy);
		TunnelSecurity security = new TunnelSecurity();
		security.setSslSwich("off");
		tpolicy.setSecurity(security );
		TunnelChannelProfile socket = new TunnelChannelProfile();
		socket.setBlocking("false");
		socket.setKeeplive("false");
		socket.setSize(1);
		socket.setType("http");
		socket.setWorkThreads(10);
		socket.setConnectionTimeout(6000);
		socket.setSocketTimeout(6000);
		tpolicy.setSocket(socket );
		
		TunnelList tunnelList = new TunnelList();
		p.setTunnelList(tunnelList );
		List<TunnelDes> tunnels = new ArrayList<TunnelDes>();
		tunnelList.setTunnels(tunnels );
		TunnelDes tunnelDes = new TunnelDes();
		tunnelDes.setId(1);
		tunnelDes.setName("tunnel1");
		tunnelDes.setIp("127.0.0.1");
		tunnelDes.setPort(9999);
		tunnels.add(tunnelDes);
		tunnelDes = new TunnelDes();
		tunnelDes.setId(2);
		tunnelDes.setName("tunnel2");
		tunnelDes.setIp("127.0.0.1");
		tunnelDes.setPort(9999);
		tunnels.add(tunnelDes);
		
		
		PortionFactory factory =  PortionFactory.getFactory();
		factory.init(pp);
		Portion portion = factory.getPortion(LocatorFactor.NULLFactorObj);
		System.out.println("portion id " + portion.getId());
		
		Tunnel t = portion.getTunnel(LocatorFactor.NULLFactorObj);
		System.out.println("tunnel id " + t.getId());
		t = portion.getTunnel(LocatorFactor.NULLFactorObj);
		System.out.println("tunnel id " + t.getId());
	}
	
	@Test
	public void NameTest() {
		System.out.println("name testing");
		//fail("Not yet implemented"); 
		PortionsProfile pp = new PortionsProfile();
		PortionsPolicy policy = new PortionsPolicy();
		pp.setPolicy(policy);
		
		LocatorPolicy locatorPolicy = new LocatorPolicy();
		policy.setLocatorPolicy(locatorPolicy);		
		locatorPolicy.setName("portions");
		LocatorAlg locatorAlg = new LocatorAlg();
		locatorAlg.setType("name");
		locatorPolicy.setLocatorAlg(locatorAlg);
		LocatorFactorType factor = new LocatorFactorType();
		factor.setType("java.lang.String");
		locatorPolicy.setFactorType( factor );
		
		List<PortionProfile> portions = new ArrayList<PortionProfile>();
		pp.setPortions(portions );
		PortionProfile p = new PortionProfile();
		portions.add(p);
		p.setId(1);
		p.setName("default");
		PortionPolicy ppolicy = new PortionPolicy();
		ppolicy.setDegrade("auto");
		ppolicy.setDiscover("fix");
		ppolicy.setLocatorPolicy(locatorPolicy);
		p.setPolicy(ppolicy );
		
		TunnelProfile tp = new TunnelProfile();
		p.setTunnelProfile(tp);
		TunnelPolicy tpolicy = new TunnelPolicy();
		tp.setPolicy(tpolicy );
		TunnelFlowControl flowContrl = new TunnelFlowControl();
		flowContrl.setMaxtps(100);
		flowContrl.setSwich("on");
		tpolicy.setFlowControl(flowContrl );
		tpolicy.setLocatorPolicy(locatorPolicy);
		TunnelSecurity security = new TunnelSecurity();
		security.setSslSwich("off");
		tpolicy.setSecurity(security );
		TunnelChannelProfile socket = new TunnelChannelProfile();
		socket.setBlocking("false");
		socket.setKeeplive("true");
		socket.setSize(1);
		socket.setType("http");
		socket.setWorkThreads(10);
		socket.setConnectionTimeout(6000);
		socket.setSocketTimeout(6000);
		tpolicy.setSocket(socket );
		
		TunnelList tunnelList = new TunnelList();
		p.setTunnelList(tunnelList );
		List<TunnelDes> tunnels = new ArrayList<TunnelDes>();
		tunnelList.setTunnels(tunnels );
		TunnelDes tunnelDes = new TunnelDes();
		tunnelDes.setId(1);
		tunnelDes.setName("tunnel1");
		tunnelDes.setIp("127.0.0.1");
		tunnelDes.setPort(9999);
		tunnels.add(tunnelDes);
		tunnelDes = new TunnelDes();
		tunnelDes.setId(2);
		tunnelDes.setName("tunnel2");
		tunnelDes.setIp("127.0.0.1");
		tunnelDes.setPort(9999);
		tunnels.add(tunnelDes);
		
		
		PortionFactory factory =  PortionFactory.getFactory();
		factory.init(pp);
		LocatorFactor<String> lf = new LocatorFactor<String>("default");
		Portion portion = factory.getPortion(lf);
		System.out.println("portion id " + portion.getId());
		
		LocatorFactor<String> tunnelLocator = new LocatorFactor<String>("tunnel1");
		Tunnel t = portion.getTunnel(tunnelLocator);
		System.out.println("tunnel id " + t.getId());
		tunnelLocator = new LocatorFactor<String>("tunnel2");
		t = portion.getTunnel(tunnelLocator);
		System.out.println("tunnel id " + t.getId());
		
		tunnelLocator = new LocatorFactor<String>("tunnel3");
		t = portion.getTunnel(tunnelLocator);
		if( t != null )
		    System.out.println("tunnel id " + t.getId());
		else
			System.out.println("cannot locate tunnel with tunnel3");
		
		tunnelDes = new TunnelDes();
		tunnelDes.setId(3);
		tunnelDes.setName("tunnel3");
		tunnelDes.setIp("127.0.0.1");
		tunnelDes.setPort(9999);
		portion.addTunnel(tunnelDes);
		tunnelLocator = new LocatorFactor<String>("tunnel3");
		t = portion.getTunnel(tunnelLocator);
		if( t != null )
		    System.out.println("tunnel id " + t.getId());
		else
			System.out.println("cannot locate tunnel with tunnel3");
	}
	
	
	@Test
	public void HashTest() {
		System.out.println("hash testing");
		//fail("Not yet implemented"); 
		PortionsProfile pp = new PortionsProfile();
		PortionsPolicy policy = new PortionsPolicy();
		pp.setPolicy(policy);
		
		LocatorPolicy locatorPolicy = new LocatorPolicy();
		policy.setLocatorPolicy(locatorPolicy);		
		locatorPolicy.setName("portions");
		LocatorAlg locatorAlg = new LocatorAlg();
		locatorAlg.setType("hash");
		locatorPolicy.setLocatorAlg(locatorAlg);
		LocatorFactorType factor = new LocatorFactorType();
		factor.setType("java.lang.Integer");
		locatorPolicy.setFactorType( factor );
		
		List<PortionProfile> portions = new ArrayList<PortionProfile>();
		pp.setPortions(portions );
		PortionProfile p = new PortionProfile();
		portions.add(p);
		p.setId(1);
		p.setName("default");
		PortionPolicy ppolicy = new PortionPolicy();
		ppolicy.setDegrade("auto");
		ppolicy.setDiscover("fix");
		ppolicy.setLocatorPolicy(locatorPolicy);
		p.setPolicy(ppolicy );
		
		TunnelProfile tp = new TunnelProfile();
		p.setTunnelProfile(tp);
		TunnelPolicy tpolicy = new TunnelPolicy();
		tp.setPolicy(tpolicy );
		TunnelFlowControl flowContrl = new TunnelFlowControl();
		flowContrl.setMaxtps(100);
		flowContrl.setSwich("on");
		tpolicy.setFlowControl(flowContrl );
		tpolicy.setLocatorPolicy(locatorPolicy);
		TunnelSecurity security = new TunnelSecurity();
		security.setSslSwich("off");
		tpolicy.setSecurity(security );
		TunnelChannelProfile socket = new TunnelChannelProfile();
		socket.setBlocking("false");
		socket.setKeeplive("true");
		socket.setSize(1);
		socket.setType("http");
		socket.setWorkThreads(10);
		socket.setConnectionTimeout(6000);
		socket.setSocketTimeout(6000);
		tpolicy.setSocket(socket );
		
		TunnelList tunnelList = new TunnelList();
		p.setTunnelList(tunnelList );
		List<TunnelDes> tunnels = new ArrayList<TunnelDes>();
		tunnelList.setTunnels(tunnels );
		TunnelDes tunnelDes = new TunnelDes();
		tunnelDes.setId(1);
		tunnelDes.setName("tunnel1");
		tunnelDes.setIp("127.0.0.1");
		tunnelDes.setPort(9999);
		tunnels.add(tunnelDes);
		tunnelDes = new TunnelDes();
		tunnelDes.setId(2);
		tunnelDes.setName("tunnel2");
		tunnelDes.setIp("127.0.0.1");
		tunnelDes.setPort(9999);
		tunnels.add(tunnelDes);
		
		
		PortionFactory factory =  PortionFactory.getFactory();
		factory.init(pp);
		LocatorFactor<Integer> lf = new LocatorFactor<Integer>(123);
		Portion portion = factory.getPortion(lf);
		System.out.println("portion id " + portion.getId());
		
		LocatorFactor<Integer> tunnelLocator = new LocatorFactor<Integer>(123);
		Tunnel t = portion.getTunnel(tunnelLocator);
		System.out.println("tunnel id " + t.getId());
		tunnelLocator = new LocatorFactor<Integer>(124);
		t = portion.getTunnel(tunnelLocator);
		System.out.println("tunnel id " + t.getId());
		
		tunnelLocator = new LocatorFactor<Integer>(125);
		t = portion.getTunnel(tunnelLocator);
		System.out.println("tunnel id " + t.getId());
		
		tunnelDes = new TunnelDes();
		tunnelDes.setId(3);
		tunnelDes.setName("tunnel3");
		tunnelDes.setIp("127.0.0.1");
		tunnelDes.setPort(9999);
		portion.addTunnel(tunnelDes);
		tunnelLocator = new LocatorFactor<Integer>(125);
		t = portion.getTunnel(tunnelLocator);
		if( t != null )
		    System.out.println("tunnel id " + t.getId());
		else
			System.out.println("cannot locate tunnel with tunnel3");
	}

}
