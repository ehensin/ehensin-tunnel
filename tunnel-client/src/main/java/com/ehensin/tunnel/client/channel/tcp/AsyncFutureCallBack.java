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
package com.ehensin.tunnel.client.channel.tcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ehensin.tunnel.client.ErrorCodeEnum;
import com.ehensin.tunnel.client.channel.ChannelException;
import com.ehensin.tunnel.client.channel.IChannelIntercepter;
import com.ehensin.tunnel.client.channel.IFutureCallBack;
import com.ehensin.tunnel.client.channel.TunnelChannel;
import com.ehensin.tunnel.client.channel.codec.CodecException;
import com.ehensin.tunnel.client.channel.codec.JsonCodec;
import com.ehensin.tunnel.client.event.EventDispatcher;
import com.ehensin.tunnel.client.locator.GradeEnum;
import com.ehensin.tunnel.client.protocol.MsgProtocol;
import com.ehensin.tunnel.client.protocol.MsgRepProtocol;
import com.ehensin.tunnel.client.protocol.ProtocolHelper;
import com.ehensin.tunnel.client.session.CallbackEvent;

public class AsyncFutureCallBack implements IFutureCallBack{
	private static final Logger logger = LoggerFactory
			.getLogger(AsyncFutureCallBack.class);
	/*codec*/
	private JsonCodec jsonCodec = new JsonCodec();
	
	public AsyncFutureCallBack(){
	}
	public void completed(TunnelChannel channel, String rep)
			throws ChannelException {
		if (rep != null) {
			try {
				/*post invoke*/
				for(IChannelIntercepter intercepter : channel.getChannelIntercepters()){
					intercepter.postInvoke(rep);
				}
				MsgProtocol invokeResult = jsonCodec.decode(rep);
				MsgRepProtocol result = (MsgRepProtocol)invokeResult.getBody();

				CallbackEvent event = new CallbackEvent();
				event.setSuccess(result.getStatusCode() == ProtocolHelper.REP_STATUS_OK);
				event.setErrorCode(result.getErrorCode());
				event.setErrorDes(result.getException());
				event.setResult(result.getResult());
				event.setSrcMsgId(result.getSrcMsgId());
				EventDispatcher.getInstance().dispatch(null, event);
				
			} catch (CodecException e) {
				logger.error("cannot decode response String:{}", rep, e);
			} catch (Exception e) {
				logger.error("dispatch callback event failed, String:{}", rep, e);
			}
			channel.setGrade(channel.getGrade().up(channel.getGrade()));
		}else{
			CallbackEvent event = new CallbackEvent();
			event.setSuccess(false);
			event.setErrorCode(ErrorCodeEnum.TunnelChannelReceiveFailed.getCode());
			event.setErrorDes(ErrorCodeEnum.TunnelChannelReceiveFailed.getDes());
			event.setResult(rep);
			event.setSrcMsgId(null);
			try {
				EventDispatcher.getInstance().dispatch(null, event);
			} catch (Exception e) {
				logger.error("dispatch callback event failed, String:{}", rep, e);
			}
		}
	}

	@Override
	public void exceptionCaught(TunnelChannel channel, Throwable e) {
		logger.error("catch an exception", e);
		/*for channal close exception, need to set grade to bad*/
		if( e instanceof java.nio.channels.ClosedChannelException ){
			channel.setGrade(GradeEnum.Bad);
		}else{
		   /*down grade*/
		   channel.setGrade(channel.getGrade().down(channel.getGrade()));
		}
		
		CallbackEvent event = new CallbackEvent();
		event.setSuccess(false);
		event.setErrorCode(ErrorCodeEnum.TunnelChannelReceiveFailed.getCode());
		event.setErrorDes(ErrorCodeEnum.TunnelChannelReceiveFailed.getDes());
		event.setResult(e);
		event.setSrcMsgId(null);
		try {
			EventDispatcher.getInstance().dispatch(null, event);
		} catch (Exception e1) {
			logger.error("dispatch callback except event failed", e1);
		}
	}
}
