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
package com.ehensin.tunnel.client.protocol;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.ehensin.tunnel.client.ErrorCodeEnum;
import com.ehensin.tunnel.client.channel.codec.CodecException;
import com.ehensin.tunnel.client.util.UUIDUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ProtocolHelper {
	public final static int REP_STATUS_OK = 1;
	public final static int REP_STATUS_FAILED = 0;
	private static String srcIp = "127.0.0.1";
	static{
		if (null != System.getProperty("local.ip")) {
			srcIp = System.getProperty("local.ip");
		}else{
			try {
				srcIp = InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
    public static MsgProtocol getRPCReqMessage(String serviceName, String function, Object[]params, String remoteIp,
    		Class resultClass){
    	
        MsgProtocol msg = new MsgProtocol();
		
    	MsgHeader header = new MsgHeader();
    	header.setFormat("json");
    	header.setMethod("request");
    	header.setMsgId(UUIDUtil.getUUID());
    	header.setSrcIp(srcIp);
    	header.setTargetIp(remoteIp);
    	header.setTimestamp(System.currentTimeMillis());
    	header.setMsgType(MsgType.RPC.getValue());
    	msg.setMsgHeader(header);
    	
    	RPCProtocol ka = new RPCProtocol();
    	
    	ka.setResult(resultClass);
    	ka.setServiceName(serviceName);
    	ka.setServiceFunc(function);
    	String[] jsonFormatParamas = null;
    	if( params != null && params.length > 0 ){
    		/*change parameters to json format*/
    		jsonFormatParamas = new String[params.length];
    		for(int i = 0 ; i < params.length; i++ ){
    			Object p = params[i];
    			ObjectMapper mapper = new ObjectMapper();
    			try {
    				jsonFormatParamas[i] = mapper.writeValueAsString(p);
    			} catch (JsonProcessingException e) {
    				
    			}
    		}
    	}
    	
    	ka.setParams(jsonFormatParamas);
    	msg.setBody(ka);
    	
    	return msg;
    	
    }
    
    public static MsgProtocol getPlainReqMessage(String plainText, String remoteIp){
    	
        MsgProtocol msg = new MsgProtocol();
		
    	MsgHeader header = new MsgHeader();
    	header.setFormat("json");
    	header.setMethod("request");
    	header.setMsgId(UUIDUtil.getUUID());
    	header.setSrcIp(srcIp);
    	header.setTargetIp(remoteIp);
    	header.setTimestamp(System.currentTimeMillis());
    	header.setMsgType(MsgType.PLAINTEXT.getValue());
    	msg.setMsgHeader(header);
    	
    	PlainTextProtocol ka = new PlainTextProtocol();	
    	ka.setPlainText(plainText);
    	msg.setBody(ka);
    	
    	return msg;
    	
    }
    
    public static MsgProtocol getKeepaliveMessage(String remoteIp){
    	MsgProtocol msg = new MsgProtocol();
		
    	MsgHeader header = new MsgHeader();
    	header.setFormat("json");
    	header.setMethod("request");
    	header.setMsgId(UUIDUtil.getUUID());
    	header.setSrcIp(srcIp);
    	header.setTargetIp(remoteIp);
    	header.setTimestamp(System.currentTimeMillis());
    	header.setMsgType(MsgType.KEEPALIVE.getValue());
    	msg.setMsgHeader(header);
    	
    	KeepAliveProtocol ka = new KeepAliveProtocol();
    	msg.setBody(ka);
    	
    	return msg;
    }
}
