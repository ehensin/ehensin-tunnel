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

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.oio.OioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ehensin.tunnel.client.ErrorCodeEnum;
import com.ehensin.tunnel.client.channel.ChannelConfig;
import com.ehensin.tunnel.client.channel.ChannelException;
import com.ehensin.tunnel.client.channel.ChannelStatus;
import com.ehensin.tunnel.client.channel.InvokeResult;
import com.ehensin.tunnel.client.locator.GradeEnum;
import com.ehensin.tunnel.client.util.EhensinThreadFactory;
import com.ehensin.tunnel.client.util.EhensinThreadGroup;
/**
 * channel based on http protocol,
 * 
 * */
public class HttpShortChannel extends TCPChannel{
	private static final Logger logger = LoggerFactory
			.getLogger(HttpShortChannel.class);
	private URI uri;
	/* netty boolt strap */
	private ClientBootstrap bootstrap;
	
	public HttpShortChannel(long id, long tunnelId, ChannelConfig config) {
		super(id, tunnelId, config);
		if( logger.isDebugEnabled() ){
			logger.debug("create a new short http channel, {} {} {} {}", new Object[]{tunnelId, id,  config.getIp(), config.getPort()});
		}
		this.status = ChannelStatus.New;
		try {
			if(super.getChannelConfig().isSSLSupport()){
			    uri = new URI("https://" + super.getChannelConfig().getIp() + ":" + super.getChannelConfig().getPort());
			}else{
				uri = new URI("http://" + super.getChannelConfig().getIp() + ":" + super.getChannelConfig().getPort());
			}
		} catch (URISyntaxException e) {
			logger.error("invalide ip {} or port {} information", super.getChannelConfig().getIp(), super.getChannelConfig().getIp());
			this.status = ChannelStatus.Invalid;
			throw new IllegalArgumentException("invalide ip or port information");
		}	

		try {
			this.renew();
		} catch (ChannelException e) {
			logger.error("cannot connect remote server", e);
		}
	}

	@Override
	public void renew() throws ChannelException {
		if( bootstrap != null ){
			bootstrap.shutdown();
			bootstrap.releaseExternalResources();
		}
		if( !super.isBlocking() )
		    bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(Executors.newCachedThreadPool(),
				Executors.newFixedThreadPool(super.getChannelConfig().getWorkThreads(),new EhensinThreadFactory(new EhensinThreadGroup("ShortChannel"))),super.getChannelConfig().getWorkThreads()));
		else
			bootstrap = new ClientBootstrap(new OioClientSocketChannelFactory(
					Executors.newFixedThreadPool(super.getChannelConfig().getWorkThreads())));
		
		String scheme = uri.getScheme() == null ? "http" : uri.getScheme();
		/* config SSL */
		boolean isSsl = false;
		if ("https".equals(scheme)) {
			bootstrap.setOption("sslContext", SslContextFactory
					.getClientContext());
			isSsl = true;
		} 
		
		bootstrap.setPipelineFactory(new HttpClientPipelineFactory(isSsl));
		bootstrap.setOption("serverName", uri.getHost());
		bootstrap.setOption("serverPath", uri.getRawPath());
		bootstrap.setOption("tcpNoDelay", true);
		bootstrap.setOption("keepAlive", true);
		bootstrap.setOption("reuseAddress", true);
		if (super.getChannelConfig().getConnectionTimeout() > 0) {
			bootstrap.setOption("connectTimeoutMillis", super.getChannelConfig().getConnectionTimeout());
		}
		
		/*try to create a connection, confirm the target server is available*/
		Channel channel = createNewConnection();
		/*close channel*/
		channel.close().awaitUninterruptibly();
		
	}	
	
	
	Channel createNewConnection() throws ChannelException{
		ChannelFuture future = bootstrap.connect(new InetSocketAddress(uri
				.getHost(), uri.getPort()));
		future.awaitUninterruptibly();
		if (!future.isSuccess()) {
			logger.error("cannot connect remote server", future.getCause());
			future.getChannel().close().awaitUninterruptibly();
			HttpShortChannel.this.status = ChannelStatus.Invalid;
			this.setGrade(GradeEnum.Bad);
			throw new ChannelException("receive message failed", future.getCause() ,ErrorCodeEnum.TunnelChannelUnavailable.getCode());
		}
		/*set status to normal*/
		this.status = ChannelStatus.Normal;
		/*set grade to normal*/
		this.setGrade(GradeEnum.Normal);
		return future.getChannel();
	}

	
	@Override
	public String internalSend(String msg, boolean isSyncCall) throws ChannelException {
		if( logger.isDebugEnabled() )
			logger.debug("channel({} {}) send a message:{} ",new Object[]{this.getTunnelId(), this.getId(), msg});
		Channel channel = createNewConnection();
		HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri.toASCIIString());
		request.setHeader(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
		request.setHeader(HttpHeaders.Names.EXPIRES, 60*1000);
		request.setHeader(HttpHeaders.Names.ACCEPT_CHARSET, CharsetUtil.UTF_8);
		if (super.getChannelConfig().getSocketTimeout() > 0)
			request.setHeader(HttpHeaders.Names.EXPIRES, super.getChannelConfig().getSocketTimeout());
		
		ChannelBuffer buffer = ChannelBuffers.copiedBuffer(msg, CharsetUtil.UTF_8);
		request.setContent(buffer);
		request.setHeader(org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH, request.getContent().readableBytes());
		
		if( isSyncCall ){
			 String result = this.syncRequest(request, channel);
			 if( logger.isDebugEnabled() )
					logger.debug("sync channel( {} {} )get request result:{} ",new Object[]{this.getTunnelId(), this.getId(), result});
			 return result;
		}else{
			asyncRequest(request, channel);
		}
		return null;
	}
	
	private void asyncRequest(HttpRequest req, Channel channel) throws ChannelException{
		channel.getPipeline().addLast("handler",
				new ChannelClientHandler(this, new AsyncFutureCallBack()));
		ChannelFuture future = channel.write(req);
		future.awaitUninterruptibly();
		if (!future.isSuccess()) {
			logger.error("cannot send message to remote server", future.getCause());
			future.getChannel().close().awaitUninterruptibly();
			/*down grade*/
			this.setGrade(this.getGrade().down(this.getGrade()));				
			throw new ChannelException("http async call failed", future.getCause(),ErrorCodeEnum.TunnelChannelSendFailed.getCode());
		}
	}
	
	private String syncRequest(HttpRequest req, Channel channel) throws ChannelException {
		final InvokeResult<String> result = new InvokeResult<String>();
		try {
			result.sendLock.lockInterruptibly();
			channel.getPipeline().addLast("handler",
					new ChannelClientHandler(this, new SyncFutureCallBack(result)));
			ChannelFuture future = channel.write(req);
			future.awaitUninterruptibly();
			if (!future.isSuccess()) {
				logger.error("cannot send message to remote server", future.getCause());
				future.getChannel().close().awaitUninterruptibly();
				this.setGrade(this.getGrade().down(this.getGrade()));
				throw new ChannelException("http sync call failed", future.getCause(),ErrorCodeEnum.TunnelChannelSendFailed.getCode());
			}else
			    result.waitComplete.await(60, TimeUnit.SECONDS);
			return result.getResult();
		} catch (InterruptedException e) {
			result.waitComplete.signal();
			channel.close().awaitUninterruptibly();
			this.setGrade(this.getGrade().down(this.getGrade()));
			throw new ChannelException("http sync call failed", e,ErrorCodeEnum.TunnelChannelSendFailed.getCode());
		} finally {
			channel.close().awaitUninterruptibly();
			result.sendLock.unlock();
		}
	}



}
