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

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ehensin.tunnel.client.channel.ChannelStatus;
import com.ehensin.tunnel.client.locator.GradeEnum;

/**
 * detect channel disconnect event
 * */
public class ChannelStatusHandler extends org.jboss.netty.channel.SimpleChannelHandler {
	private static final Logger logger = LoggerFactory
			.getLogger(ChannelStatusHandler.class);
	private HttpLongChannel channel;
	public ChannelStatusHandler(HttpLongChannel channel){
		 this.channel = channel;
	}
	public void channelDisconnected(
	            ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
	        ctx.sendUpstream(e);
	        logger.error("channel ({} {}) disconnected from remote server", this.channel.getTunnelId(), this.channel.getId() );
	        this.channel.setStatus(ChannelStatus.Invalid);
	        this.channel.setGrade(GradeEnum.Bad);
	}
	public void channelUnbound(
            ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        ctx.sendUpstream(e);
        logger.error("channel ({} {}) unbound from remote server", this.channel.getTunnelId(), this.channel.getId() );
        this.channel.setStatus(ChannelStatus.Invalid);
        this.channel.setGrade(GradeEnum.Bad);
    }
	
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		this.channel.exceptionCaught(ctx, e);
	}
}
