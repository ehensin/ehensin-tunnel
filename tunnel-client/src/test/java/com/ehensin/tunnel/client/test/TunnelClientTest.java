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

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.ehensin.tunnel.client.TunnelClientManager;
import com.ehensin.tunnel.client.channel.codec.CodecException;
import com.ehensin.tunnel.client.channel.codec.JsonCodec;
import com.ehensin.tunnel.client.locator.LocatorHelper;
import com.ehensin.tunnel.client.protocol.MsgHeader;
import com.ehensin.tunnel.client.protocol.MsgProtocol;
import com.ehensin.tunnel.client.protocol.MsgRepProtocol;
import com.ehensin.tunnel.client.session.ISession;
import com.ehensin.tunnel.client.session.SessionException;
import com.ehensin.tunnel.client.util.UUIDUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TunnelClientTest {

	//@Test
	public void syncTest() {
		TunnelClientManager manager = new TunnelClientManager();
		ISession session = manager.getSessionFactory().createStatefulSession(1000, 
				LocatorHelper.getNameLocatorFactor("portion1"), LocatorHelper.getHashLocatorFactor(1));
		try {
			ResultClass result = (ResultClass)session.syncInvoke("test", "test",new String[]{"hello world"}, ResultClass.class);
			System.out.println("==============" + result.getTest1() + " " + result.getTest2());
		} catch (SessionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	public void syncPlaintextTest() {
		TunnelClientManager manager = new TunnelClientManager();
		final ISession session = manager.getSessionFactory().createStatefulSession(1000, 
				LocatorHelper.getNameLocatorFactor("portion1"), LocatorHelper.getHashLocatorFactor(1));
		/*final ISession session = manager.getSessionFactory().createStatelessSession(
				LocatorHelper.getNameLocatorFactor("portion1"), LocatorHelper.getHashLocatorFactor(1));*/
		
			final AsyncStringCallback callback = new AsyncStringCallback(String.class);
			for(int j = 0; j<10; j++){
				Thread t = new Thread(new Runnable(){

					@Override
					public void run() {
						long start = System.currentTimeMillis();
						try {
						   for( int i = 0 ; i < 1000; i++){
						    //String result = session.asyncPlainInvoke("{\"plaintext\":\"hello world\"}", callback);
						    String result = session.syncPlainInvoke("{\"plaintext\":\"hello world\"}");
						    //System.out.println("==============" + result );
						   }
						} catch (SessionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						long end = System.currentTimeMillis();
						
						System.out.println(" span : " + (end-start));
					}
					
				});
				t.start();
				
				
			}
			
		
		try {
			Thread.currentThread().join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//@Test
	public void asyncTest() {
		TunnelClientManager manager = new TunnelClientManager();
		ISession session = manager.getSessionFactory().createStatelessSession(
				LocatorHelper.getNameLocatorFactor("portion1"), LocatorHelper.getHashLocatorFactor(1));
		try {
			AsyncStringCallback callback = new AsyncStringCallback(String.class);
			String result = (String)session.asyncInvoke("test", "test", new Object[]{"hello", new Param("1", "2")}, callback);
			System.out.println("async ==============" + result);
			ResultClass r = (ResultClass)session.syncInvoke("test", "test1", new Object[]{"test1", new Param("3", "4")}, ResultClass.class);
			System.out.println("sync ==============" + r);
			
		} catch (SessionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		try {
			TimeUnit.SECONDS.sleep(3);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//@Test
	public void testJson(){
		MsgProtocol msg = new MsgProtocol();
			
    	MsgHeader header = new MsgHeader();
    	header.setFormat("json");
    	header.setMethod("request");
    	header.setMsgId(UUIDUtil.getUUID());
    	header.setSrcIp("1");
    	header.setTargetIp("2");
    	header.setTimestamp(System.currentTimeMillis());
    	header.setMsgType(1);
    	msg.setMsgHeader(header);
    	
    	MsgRepProtocol body = new MsgRepProtocol();
    	body.setErrorCode("0");
    	body.setStatusCode(0);
    	body.setException("");
    	
    	body.setSrcMsgId("1001");
    	//body.setResult("{\"test1\":\"hello\",\"test2\":\"world\"}");
    	
    	
    	
    	JsonCodec codec = new JsonCodec();
    	try {
    		ObjectMapper mapper = new ObjectMapper();
        	ResultClass result = new ResultClass();
        	result.setTest1("hello");
        	result.setTest2("world");
        	String str = mapper.writeValueAsString(result);
        	body.setResult(str);
        	msg.setBody(body);
    		
    		String encode = codec.encode(msg);
			System.out.println(encode);
			MsgProtocol decode = codec.decode(encode);
			MsgRepProtocol b = (MsgRepProtocol)decode.getBody();
			
			mapper = new ObjectMapper();
			ResultClass r = (ResultClass)mapper.readValue(b.getResult(), ResultClass.class);
			System.out.println(r.getTest1());
		} catch (CodecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		    	
		    	
	}

}
