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

import com.ehensin.tunnel.server.channel.IServerChannel;
import com.ehensin.tunnel.server.protocal.message.MsgProtocol;

public class LoggerFilter implements IChannelFilter{
	private static Logger logger = LoggerFactory.getLogger(LoggerFilter.class);
	@Override
	public void init(String name, String desc, IServerChannel channel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "logger filter";
	}

	@Override
	public String getDesc() {
		// TODO Auto-generated method stub
		return "logger message from client";
	}

	@Override
	public void doFilter(MsgProtocol msg, FilterChain chain) {
		
        logger.debug("Logger Filter : Get message from client:{}", msg.getBody());
       
		chain.doFilter(msg);
	}

	@Override
	public IServerChannel getChannel() {
		// TODO Auto-generated method stub
		return null;
	}

	

}
