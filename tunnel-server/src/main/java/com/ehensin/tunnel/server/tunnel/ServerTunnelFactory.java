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
package com.ehensin.tunnel.server.tunnel;

import java.util.HashMap;
import java.util.Map;

import com.ehensin.tunnel.server.event.IEventListener;
import com.ehensin.tunnel.server.event.ITunnelEvent;

/**
 * tunnel factory to create a {@see ServerTunnel} 
 * Tunnel factory will listen the channel close event to release channel of tunnel
 * */
public class ServerTunnelFactory implements IEventListener{
    private static final ServerTunnelFactory factory = new ServerTunnelFactory();
    private Map<String, ServerTunnel> tunnelMap;
    
    private ServerTunnelFactory(){
    	tunnelMap = new HashMap<String,ServerTunnel>();
    	//EventDispatcher.getInstance().register(EventTypeEnum.CHANNEL_CLOSED, this);
    }
    
    public static ServerTunnelFactory getFactory(){
    	return factory;
    }
    
    synchronized public ServerTunnel getTunnel(String ip){
    	ServerTunnel tunnel = tunnelMap.get(ip);
    	if( tunnel == null ){
    		tunnel = new ServerTunnel(ip);
    		tunnelMap.put(ip, tunnel);
    	}
    	return tunnel;
    }

	@Override
	public void onEvent(Object src, ITunnelEvent event) throws Exception {
		
		
	}


}
