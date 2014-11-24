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
package com.ehensin.tunnel.server.channel.http;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.util.CharsetUtil;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import com.ehensin.tunnel.server.channel.ChannelException;
import com.ehensin.tunnel.server.channel.ChannelStatus;
import com.ehensin.tunnel.server.channel.IServerChannel;
import com.ehensin.tunnel.server.channel.codec.CodecException;
import com.ehensin.tunnel.server.channel.codec.JsonCodec;
import com.ehensin.tunnel.server.protocal.message.MsgProtocol;

public class HttpServerChannel implements IServerChannel{
	private Channel nettyChannel;
	private Integer id;
	private JsonCodec codec;
	public HttpServerChannel(Integer id, Channel nettyChannel){
		this.id = id;
		this.nettyChannel = nettyChannel;
		this.codec = new JsonCodec();
		
	}

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public MsgProtocol write(MsgProtocol msg) throws ChannelException {
		try {
			String ecodeMsg = codec.encode(msg);
			
			HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
			response.setHeader(CONTENT_TYPE, "text/plain; charset=UTF-8");
			response.setContent(ChannelBuffers.copiedBuffer(ecodeMsg, CharsetUtil.UTF_8));
			response.setHeader(CONTENT_LENGTH, response.getContent().readableBytes());
			response.setHeader(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
			ChannelFuture f = this.nettyChannel.write(response);
			f.awaitUninterruptibly();
		} catch (CodecException e) {
			throw new ChannelException("cannot encode message", e);
		}
		return msg;
	}

	@Override
	public ChannelStatus getStatus() {
		// TODO Auto-generated method stub
		return null;
	}

}
