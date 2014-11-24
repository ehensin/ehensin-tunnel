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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ehensin.tunnel.server.StatusCode;
import com.ehensin.tunnel.server.channel.ChannelException;
import com.ehensin.tunnel.server.channel.IServerChannel;
import com.ehensin.tunnel.server.protocal.message.MsgProtocol;
import com.ehensin.tunnel.server.protocal.message.MsgRepProtocol;
import com.ehensin.tunnel.server.protocal.message.MsgType;
import com.ehensin.tunnel.server.protocal.message.ProtocolHelper;

public class KeepaliveFilter implements IChannelFilter {
	private static Logger logger = LoggerFactory
			.getLogger(KeepaliveFilter.class);
    private IServerChannel channel;
    public KeepaliveFilter(IServerChannel channel){
    	this.channel = channel;
    }
	@Override
	public void init(String name, String desc, IServerChannel channel) {
		this.channel = channel;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Keep Alive Fiter";
	}

	@Override
	public String getDesc() {
		return "Handle keep alive message";
	}

	@Override
	public void doFilter(MsgProtocol msg, FilterChain chain) {
		if (msg.getMsgHeader().getMsgType() == MsgType.KEEPALIVE.getValue()) {
			if (logger.isDebugEnabled()) {
				logger.debug("KeepAlive Filter : Get keep alive message from client:{}",
						msg.getBody());
			}
			/*don't need to continue */
			MsgProtocol response = new MsgProtocol();
			response.setMsgHeader(ProtocolHelper.getKeepaliveRepMessageHeader(msg.getMsgHeader().getSrcIp()));
			try {
				this.getChannel().write(response);
			} catch (ChannelException e) {
				logger.error("filter msg error : ", e);
			}
		}else{
			chain.doFilter(msg);
		}
	}

	@Override
	public IServerChannel getChannel() {
		return this.channel;
	}

}
