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

import org.apache.commons.lang.ArrayUtils;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.util.CharsetUtil;

import com.ehensin.tunnel.client.ErrorCodeEnum;
import com.ehensin.tunnel.client.channel.ChannelException;
import com.ehensin.tunnel.client.channel.IFutureCallBack;
import com.ehensin.tunnel.client.channel.TunnelChannel;

public class ChannelClientHandler extends SimpleChannelUpstreamHandler {
	private volatile boolean readingChunks;
	private byte[] temp;
	private IFutureCallBack callback;
	private TunnelChannel channel;
	public ChannelClientHandler(TunnelChannel channel, IFutureCallBack callback) {
		this.callback = callback;
		this.channel = channel;
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		if (e.getMessage() instanceof HttpResponse) {
			HttpResponse response = (HttpResponse) e.getMessage();
			readingChunks = response.isChunked();
			if (!readingChunks) {
				ChannelBuffer cb = response.getContent();
				if (cb.readable() && !cb.equals(ChannelBuffers.EMPTY_BUFFER)) {
					if (callback != null) {
						if (response.getStatus().equals(HttpResponseStatus.OK)){
							    callback.completed( channel, response.getContent().toString(CharsetUtil.UTF_8) );
						}else
							callback.exceptionCaught(channel, 
									new ChannelException("response http error information:"
							+ response.getStatus().getCode(), null, ErrorCodeEnum.TunnelChannelHttpReponseEroor.getCode()));
					}
				}
			}
		} else if (e.getMessage() instanceof HttpChunk) {
			HttpChunk chunk = (HttpChunk) e.getMessage();
			if (!chunk.isLast()) {
				if (callback != null) {
					if (temp == null) {
						temp = chunk.getContent().array();
					} else {
						temp = ArrayUtils.addAll(temp, chunk.getContent().array());
					}
				}
			} else {
				readingChunks = false;
				if (temp != null && callback != null) {
					callback.completed(channel,new String(temp,CharsetUtil.UTF_8));
				}else
					callback.completed(channel,null);
				temp = null;
				
			}
		}
		
		
	}

	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		if (callback != null) {
			callback.exceptionCaught(channel,e.getCause());
		}
	}

}
