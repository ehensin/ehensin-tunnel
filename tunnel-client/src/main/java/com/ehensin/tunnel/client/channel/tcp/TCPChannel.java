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

import com.ehensin.tunnel.client.channel.ChannelConfig;
import com.ehensin.tunnel.client.channel.ChannelStatus;
import com.ehensin.tunnel.client.channel.TunnelChannel;

abstract public class TCPChannel extends TunnelChannel{
	/*blocking mode or nio mode*/
    private boolean isBlocking;
    /*long connection or short connection mode*/
    private boolean isKeepAlive;
	public TCPChannel(long id, long tunnelId, ChannelConfig config) {
		super(id, tunnelId, config);
		isBlocking = config.isBlocking();
		isKeepAlive = config.isKeepalive();
	}

	public boolean isBlocking(){
		return isBlocking;
	}
	
	public boolean isKeepAlive(){
		return isKeepAlive;
	}

	
	public void setStatus(ChannelStatus status){
		this.status = status;
	}
}
