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
package com.ehensin.tunnel.client.log;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ehensin.tunnel.client.channel.IChannel;
import com.ehensin.tunnel.client.channel.IChannelIntercepter;
import com.ehensin.tunnel.client.profile.Param;
import com.ehensin.tunnel.client.util.file.RollingFixedFileSizeAppender;

public class ChannelLogIntercepter implements IChannelIntercepter{
	private static final Logger logger = LoggerFactory
			.getLogger(ChannelLogIntercepter.class);
	private Map<String, String> params;
	private RollingFixedFileSizeAppender reqLogAppender;
	private RollingFixedFileSizeAppender repLogAppender;
	public ChannelLogIntercepter(){
		params = new HashMap<String,String>();
	}
	
	public void init(List<Param> params, IChannel channel){
		for( Param p : params ){
			this.params.put(p.getName(), p.getValue());
		}
		/*get log dir*/
		String dir = this.params.get("logdir");
		
		String rollType = this.params.get("rolltype");
		if( rollType != null && rollType.equals("fixsize")){
			int size = (this.params.get("filesize") == null) ? -1 : Integer.valueOf(this.params.get("filesize"));
			try {
				reqLogAppender = new  RollingFixedFileSizeAppender(dir + System.getProperty("file.separator") + "request", "req",
						System.getProperty("file.separator") + "request\backup", true, size, "-" + channel.getTunnelId() + "-" + channel.getId());
				repLogAppender = new  RollingFixedFileSizeAppender(dir + System.getProperty("file.separator") + "response", "rep", 
						System.getProperty("file.separator") + "response\backup", true, size, "-" + channel.getTunnelId() + "-" + channel.getId());
			} catch (IOException e) {
				logger.error("create logger appender failed ", e);
			}
			
			
		}
	}

	@Override
	public boolean preInvoke(String req) {
		if( reqLogAppender != null ){
			try {
				reqLogAppender.append(req);
			} catch (IOException e) {
				logger.error("failed to append request : {}  ", req, e);
			}
		}
		return true;
	}

	@Override
	public void postInvoke(String rep) {
		if( repLogAppender != null ){
			try {
				repLogAppender.append(rep);
			} catch (IOException e) {
				logger.error("failed to append request : {}  ", rep, e);
			}
		}
	}

	@Override
	public void exceptionCaught(Exception e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getIntercepterName() {
		return "channel log intercepter";
	}

}
