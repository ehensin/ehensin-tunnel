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
package com.ehensin.tunnel.server.listener.netty;

import java.net.InetSocketAddress;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ehensin.tunnel.server.ServerStartup;
import com.ehensin.tunnel.server.channel.ChannelConnectEvent;
import com.ehensin.tunnel.server.channel.IServerChannel;
import com.ehensin.tunnel.server.event.EventDispatcher;
import com.ehensin.tunnel.server.listener.WhiteListController;
import com.ehensin.tunnel.server.tunnel.ServerTunnel;
import com.ehensin.tunnel.server.tunnel.ServerTunnelFactory;

/**
 * accept event handler to handle connect from client,
 * if connect count is more than max connection, server will refuse connection requirement
 * if client is not in white list , sever will refuse connection requirement
 * 
 * */
public class ChannelConnectEventHandler extends SimpleChannelUpstreamHandler{
	private static final Logger logger = LoggerFactory
			.getLogger(ChannelConnectEventHandler.class);
	/*init white list controller*/
	private static WhiteListController controller = new WhiteListController(ServerStartup.getStartup().getProfile().getWhiteList());

	
    public void channelConnected(
            ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
    	Channel channel = e.getChannel();
    	if( logger.isInfoEnabled() ){
    		logger.info("get a connection from client: {}, channel id : {} ", channel.getRemoteAddress(), channel.getId());
    	}
    	String ip = ((InetSocketAddress)channel.getRemoteAddress()).getAddress().getHostAddress();
    	if(!controller.allow(ip)){
    		/*remote ip is not in white list, close the connection */
    		channel.close().awaitUninterruptibly();
    		if( logger.isInfoEnabled() ){
    			logger.info("the client {} is not in white list ", channel.getRemoteAddress());
    		}
    		return;
    	}else{
	    	if( logger.isInfoEnabled() ){
	    		logger.info("client ip is in white list : {} ", ip);
	    	}
    	}
    	/*create channel*/
    	ServerTunnel tunnel = ServerTunnelFactory.getFactory().getTunnel(ip);
    	IServerChannel c = tunnel.newChannel(channel);
    	/*post channel connected event*/
    	ChannelConnectEvent event = new ChannelConnectEvent(c);
    	EventDispatcher.getInstance().dispatch(this, event);
    }
    public void channelDisconnected(
            ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
    	Channel channel = e.getChannel();
    	if( logger.isInfoEnabled() ){
    		logger.info("disconnect from client: {} ", channel.getRemoteAddress());
    	}
    	
    }
    public void channelClosed(
            ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
    	Channel channel = e.getChannel();
    	if( logger.isInfoEnabled() ){
    		logger.info("closed from client: {} ", channel.getRemoteAddress());
    	}
    	String ip = ((InetSocketAddress)channel.getRemoteAddress()).getAddress().getHostAddress();
    	ServerTunnel tunnel = ServerTunnelFactory.getFactory().getTunnel(ip);
    	IServerChannel c = tunnel.removeChannel(channel.getId());
    	/*post channel closed event*/
    	ChannelConnectEvent event = new ChannelConnectEvent(c);
    	EventDispatcher.getInstance().dispatch(this, event);
    }
    
    public void exceptionCaught(
            ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
    	logger.error("exception from channel.", e.getCause());
    }
}
