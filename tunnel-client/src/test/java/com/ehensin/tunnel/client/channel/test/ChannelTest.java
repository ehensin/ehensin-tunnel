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
package com.ehensin.tunnel.client.channel.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
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
import com.ehensin.tunnel.client.protocol.KeepAliveProtocol;
import com.ehensin.tunnel.client.protocol.MsgHeader;
import com.ehensin.tunnel.client.protocol.MsgProtocol;
import com.ehensin.tunnel.client.protocol.MsgType;
import com.ehensin.tunnel.client.protocol.RPCProtocol;
import com.ehensin.tunnel.client.tunnel.Tunnel;
import com.ehensin.tunnel.client.tunnel.TunnelRequest;
import com.ehensin.tunnel.client.tunnel.TunnelResponse;

public class ChannelTest {
	PortionFactory factory;
	@Before
	public void prepare() {
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
		socket.setKeeplive("true");
		socket.setSize(2);
		socket.setType("http");
		socket.setWorkThreads(10);
		socket.setIsSyncCall("sync");
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
		
		factory =  PortionFactory.getFactory();
		factory.init(pp);
		
	}
	
	@Test
	public void test() {
		
		Portion portion = factory.getPortion(LocatorFactor.NULLFactorObj);
		System.out.println("portion id " + portion.getId());
		Tunnel t = portion.getTunnel(LocatorFactor.NULLFactorObj);
		System.out.println("tunnel id "+ t.getId() + " tunnel grade  : " + t.getGrade().getGrade());
		
		MsgProtocol msg = new MsgProtocol();
		
    	MsgHeader header = new MsgHeader();
    	header.setFormat("json");
    	header.setMethod("request");
    	header.setMsgId("1001");
    	header.setSrcIp("127.0.0.1");
    	header.setTargetIp("127.0.0.1");
    	header.setTimestamp(System.currentTimeMillis());
    	header.setMsgType(MsgType.RPC.getValue());
    	msg.setMsgHeader(header);
    	
    	RPCProtocol ka = new RPCProtocol();
    	
    	ka.setResult(java.lang.String.class);
    	ka.setServiceName("test");
    	ka.setServiceFunc("call");
    	ka.setParams(new String[]{"hello", "hello, world"});
    	msg.setBody(ka);
    	
		for( int i = 0; i < 10 ; i ++ ){
			TunnelRequest req = new TunnelRequest(msg, LocatorFactor.NULLFactorObj);
		    TunnelResponse rep = t.invoke(req);
		    if( !rep.isSuccess() ){
		    	System.out.println("cannot inoke : " + rep.getErrorDescription());
		    	System.out.println("tunnel id "+ t.getId() + " tunnel grade  : " + t.getGrade().getGrade());
		    }
		    try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		portion = factory.getPortion(LocatorFactor.NULLFactorObj);
		System.out.println("portion id " + portion.getId());
		
		t = portion.getTunnel(LocatorFactor.NULLFactorObj);
		System.out.println("tunnel id "+ t.getId() + " tunnel grade  : " + t.getGrade().getGrade());
		for( int i = 0; i < 10 ; i ++ ){
			TunnelRequest req = new TunnelRequest(null, LocatorFactor.NULLFactorObj);
		    TunnelResponse rep = t.invoke(req);
		    if( !rep.isSuccess() ){
		    	System.out.println("cannot inoke : " + rep.getErrorDescription());
		    	System.out.println("tunnel id "+ t.getId() + " tunnel grade  : " + t.getGrade().getGrade());
		    }
		    try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		t = portion.getTunnel(LocatorFactor.NULLFactorObj);
		System.out.println("tunnel id "+ t.getId() + " tunnel grade  : " + t.getGrade().getGrade());
	}
	
	
	//@Test
	public void tunnelTest() {
		
		Portion portion = factory.getPortion(LocatorFactor.NULLFactorObj);
		System.out.println("portion id " + portion.getId());
		Tunnel t = portion.getTunnel(LocatorFactor.NULLFactorObj);
		for( int i = 0; i < 10 ; i ++ ){
			TunnelRequest req = new TunnelRequest(null, LocatorFactor.NULLFactorObj);
		    TunnelResponse rep = t.invoke(req);
		    if( !rep.isSuccess() ){
		    	System.out.println("cannot inoke : " + rep.getErrorDescription());
		    	System.out.println("tunnel id "+ t.getId() + " tunnel grade  : " + t.getGrade().getGrade());
		    }
		}
		
		t = portion.getTunnel(LocatorFactor.NULLFactorObj);
		for( int i = 0; i < 10 ; i ++ ){
			TunnelRequest req = new TunnelRequest(null, LocatorFactor.NULLFactorObj);
		    TunnelResponse rep = t.invoke(req);
		    if( !rep.isSuccess() ){
		    	System.out.println("cannot inoke : " + rep.getErrorDescription());
		    	System.out.println("tunnel id "+ t.getId() + " tunnel grade  : " + t.getGrade().getGrade());
		    }
		}
		System.out.println("tunnel id "+ t.getId() + " tunnel grade  : " + t.getGrade().getGrade());
		t = portion.getTunnel(LocatorFactor.NULLFactorObj);
		if( t != null )
		    System.out.println("tunnel id "+ t.getId() + " tunnel grade  : " + t.getGrade().getGrade());
		t = portion.getTunnel(LocatorFactor.NULLFactorObj);
		if( t != null )
		    System.out.println("tunnel id "+ t.getId() + " tunnel grade  : " + t.getGrade().getGrade());
		t = portion.getTunnel(LocatorFactor.NULLFactorObj);
		if( t != null )
		    System.out.println("tunnel id "+ t.getId() + " tunnel grade  : " + t.getGrade().getGrade());
	}
}
