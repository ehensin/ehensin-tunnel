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
package com.ehensin.tunnel.server.protocal.message;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.ehensin.tunnel.server.util.UUIDUtil;

public class ProtocolHelper {
	public final static int REP_STATUS_OK = 0;
	public final static int REP_STATUS_FAILED = -1;
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
    public static MsgProtocol getRPCReqMessage(String body){
    	
    	RPCProtocol msg = new RPCProtocol();
		
    	
    	return null;
    	
    }
    
   public static MsgHeader getRPCRepMessageHeader(String remoteIp){	    
		MsgHeader header = new MsgHeader();
    	header.setFormat("json");
    	header.setMethod("response");
    	header.setMsgId(UUIDUtil.getUUID());
    	header.setSrcIp(srcIp);
    	header.setTargetIp(remoteIp);
    	header.setTimestamp(System.currentTimeMillis());
    	header.setMsgType(MsgType.RPC.getValue());	
    	return header;   	
    }
   
   public static MsgHeader getPlainTextRepMessageHeader(String remoteIp){    
		MsgHeader header = new MsgHeader();
	   	header.setFormat("json");
	   	header.setMethod("response");
	   	header.setMsgId(UUIDUtil.getUUID());
	   	header.setSrcIp(srcIp);
	   	header.setTargetIp(remoteIp);
	   	header.setTimestamp(System.currentTimeMillis());
	   	header.setMsgType(MsgType.PLAINTEXT.getValue());
	   	return header;
   }
   
   public static MsgHeader getKeepaliveRepMessageHeader(String remoteIp){    
		MsgHeader header = new MsgHeader();
	   	header.setFormat("json");
	   	header.setMethod("response");
	   	header.setMsgId(UUIDUtil.getUUID());
	   	header.setSrcIp(srcIp);
	   	header.setTargetIp(remoteIp);
	   	header.setTimestamp(System.currentTimeMillis());
	   	header.setMsgType(MsgType.KEEPALIVE.getValue());
	   	return header;
  }
    
    public static MsgProtocol getKeepaliveMessage(String remoteIp){
    	MsgProtocol msg = new MsgProtocol();
		
    	
    	
    	return msg;
    }
}
