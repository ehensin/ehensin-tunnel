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
package com.ehensin.tunnel.server.channel.filter;

import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ehensin.tunnel.server.ErrorCodeEnum;
import com.ehensin.tunnel.server.ServerStartup;
import com.ehensin.tunnel.server.StatusCode;
import com.ehensin.tunnel.server.channel.IServerChannel;
import com.ehensin.tunnel.server.profile.PlainTextProfile;
import com.ehensin.tunnel.server.protocal.message.MsgProtocol;
import com.ehensin.tunnel.server.protocal.message.MsgRepProtocol;
import com.ehensin.tunnel.server.protocal.message.MsgType;
import com.ehensin.tunnel.server.protocal.message.PlainTextProtocol;
import com.ehensin.tunnel.server.protocal.message.ProtocolHelper;
import com.ehensin.tunnel.server.protocal.message.RPCProtocol;
import com.ehensin.tunnel.server.protocol.service.plaintext.IPlainTextService;
import com.ehensin.tunnel.server.protocol.service.rpc.ServiceInvoker;
import com.ehensin.tunnel.server.protocol.service.rpc.ServiceRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;

class ProtocolFilter implements IChannelFilter{
	private static Logger logger = LoggerFactory.getLogger(ProtocolFilter.class);
	private IServerChannel channel;
	private IPlainTextService plainTextService = null;
	public ProtocolFilter(IServerChannel channel){
		this.channel = channel;
		PlainTextProfile ptp = ServerStartup.getStartup().getProfile().getProtocol().getPlaintext();
		String handleClass = ptp.getHandleClass();
		if( handleClass != null && !handleClass.trim().isEmpty()){
			try {
				plainTextService = (IPlainTextService)Class.forName(handleClass).newInstance();
			} catch (InstantiationException e) {
				logger.error("cannot initialize plain text service", e);
			} catch (IllegalAccessException e) {
				logger.error("cannot initialize plain text service", e);
			} catch (ClassNotFoundException e) {
				logger.error("cannot initialize plain text service", e);
			}
		}
	}
	@Override
	public void init(String name, String desc, IServerChannel channel) {
		this.channel = channel;
	}

	@Override
	public String getName() {
		return "protocal filter";
	}

	@Override
	public String getDesc() {
		return "handle high level protocal";
	}

	@Override
	public void doFilter(MsgProtocol msg, FilterChain chain) {
		if( logger.isDebugEnabled() ){
        	logger.debug("Protocol Filter : Get message from client, protocol type : {} , body:{}",msg.getMsgHeader().getMsgType(), msg.getBody());
        }
		if( msg.getMsgHeader().getMsgType() == MsgType.RPC.getValue()){
			String body = msg.getBody().toString();
			ObjectMapper mapper = new ObjectMapper();
			try {
				RPCProtocol rpc = mapper.readValue(body, RPCProtocol.class);
				ServiceInvoker invoker = ServiceRegistry.getRegistry().getService(rpc.getServiceName());
				MsgProtocol response = new MsgProtocol();
				response.setMsgHeader(ProtocolHelper.getRPCRepMessageHeader(msg.getMsgHeader().getSrcIp()));
				MsgRepProtocol rep = new MsgRepProtocol();
				response.setBody(rep);
				if( invoker != null ){
					try {
						long start = System.currentTimeMillis();
						String result = invoker.invoke(rpc.getServiceFunc(), rpc.getParams());
						long end = System.currentTimeMillis();
						if( logger.isDebugEnabled() ){
							logger.debug("call method : {} , span : {} ", rpc.getServiceFunc(), (end-start) );
						}
				    	rep.setSrcMsgId(msg.getMsgHeader().getMsgId());
				    	rep.setStatusCode(StatusCode.RPC_CALL_SUCCESS);
						rep.setResult(result);
					} catch (Exception e) {
						logger.error("invoke error : ", e.getCause());
						rep.setSrcMsgId(msg.getMsgHeader().getMsgId());
				    	rep.setStatusCode(StatusCode.RPC_CALL_FAILED);
				    	rep.setErrorCode(ErrorCodeEnum.RpcInvokeError.getCode());
				    	if( e instanceof InvocationTargetException ){
				    		rep.setException((((InvocationTargetException) e).getTargetException()).getMessage());
				    	}else
				    	    rep.setException(e.getMessage());
					}
				}else{
					/*write exception to client by channel*/
					rep.setSrcMsgId(msg.getMsgHeader().getMsgId());
			    	rep.setStatusCode(StatusCode.RPC_CALL_FAILED);
			    	rep.setErrorCode(ErrorCodeEnum.RpcInvokerLocationError.getCode());
			    	rep.setException("cannot find service with service name : " + rpc.getServiceName());
				}
				this.getChannel().write(response);
			} catch (Exception e) {
				logger.error("filter msg error : ", e);
			}
		}else if ( msg.getMsgHeader().getMsgType() == MsgType.PLAINTEXT.getValue() ){
			String body = msg.getBody().toString();
			ObjectMapper mapper = new ObjectMapper();
			try {
				PlainTextProtocol plainText = mapper.readValue(body, PlainTextProtocol.class);
				MsgProtocol response = new MsgProtocol();
				response.setMsgHeader(ProtocolHelper.getPlainTextRepMessageHeader(msg.getMsgHeader().getSrcIp()));
				MsgRepProtocol rep = new MsgRepProtocol();
				response.setBody(rep);
				if( plainTextService != null ){
					try {
						String result = plainTextService.doService(plainText.getPlainText());
				    	rep.setSrcMsgId(msg.getMsgHeader().getMsgId());
				    	rep.setStatusCode(StatusCode.PLAINTEXT_CALL_SUCCESS);
						rep.setResult(result);
					} catch (Exception e) {
						rep.setSrcMsgId(msg.getMsgHeader().getMsgId());
				    	rep.setStatusCode(StatusCode.PLAINTEXT_CALL_FAILED);
				    	rep.setErrorCode(ErrorCodeEnum.PlaintextInvokeError.getCode());
				    	rep.setException(e.getMessage());
					}
				}else{
					/*write exception to client by channel*/
					rep.setSrcMsgId(msg.getMsgHeader().getMsgId());
			    	rep.setStatusCode(StatusCode.RPC_CALL_FAILED);
			    	rep.setErrorCode(ErrorCodeEnum.PlaintextInvokerLocationError.getCode());
			    	rep.setException("cannot find plain text service . ");
				}
				this.getChannel().write(response);
			} catch (Exception e) {
				logger.error("filter msg error : ", e);
			}
		}
	}

	@Override
	public IServerChannel getChannel() {
		return this.channel;
	}

}
