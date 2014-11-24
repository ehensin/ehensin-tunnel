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
import com.ehensin.tunnel.client.channel.InvokeResult;
import com.ehensin.tunnel.client.channel.TunnelChannel;
import com.ehensin.tunnel.client.locator.GradeEnum;

public class SyncFutureCallBack implements IFutureCallBack{
	private static final Logger logger = LoggerFactory
			.getLogger(SyncFutureCallBack.class);
	private InvokeResult<String> result;
	public SyncFutureCallBack(InvokeResult<String> result){
		this.result = result;
	}
	public void completed(TunnelChannel channel, String rep)
			throws ChannelException {
		try {
			result.sendLock.lockInterruptibly();
			if (rep != null) {
				result.setResult(rep);
				channel.setGrade(channel.getGrade().up(channel.getGrade()));
				
				/*post invoke*/
				for(IChannelIntercepter intercepter : channel.getChannelIntercepters()){
					intercepter.postInvoke(rep);
				}
			}
		} catch (Exception e) {
			result.setError(new ChannelException("receive message failed", e ,ErrorCodeEnum.TunnelChannelReceiveFailed.getCode()));
		} finally {
			result.waitComplete.signal();
			result.sendLock.unlock();
		}
	}

	@Override
	public void exceptionCaught(TunnelChannel channel, Throwable e) {
		try {
			result.sendLock.lockInterruptibly();
			logger.error("catch an exception", e);
			/*for channal close exception, need to set grade to bad*/
			if( e instanceof java.nio.channels.ClosedChannelException ){
				channel.setGrade(GradeEnum.Bad);
				result.setError(new ChannelException("receive message failed", e ,ErrorCodeEnum.TunnelChannelUnavailable.getCode()));
			}
			else{
			/*down grade*/
				channel.setGrade(channel.getGrade().down(channel.getGrade()));
			   result.setError(new ChannelException("receive message failed", e ,ErrorCodeEnum.TunnelChannelReceiveFailed.getCode()));
			}
			
		} catch (Exception e1) {
			
		} finally {
			result.waitComplete.signal();
			result.sendLock.unlock();
		}
	}
}
