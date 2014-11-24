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

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ehensin.tunnel.server.ServerStartup;
import com.ehensin.tunnel.server.listener.IServerListener;
import com.ehensin.tunnel.server.listener.WhiteListController;
import com.ehensin.tunnel.server.listener.netty.http.HttpServerPipelineFactory;

public class NettyServerListener implements IServerListener{
	private static final Logger logger = LoggerFactory
			.getLogger(NettyServerListener.class);
    private String ip;
    private int port = 9999;
    private int workerThread ;
    private String pipelineFactoryClass;
    private ServerBootstrap bootstrap;
    public NettyServerListener(){
    	try {
    		ip = InetAddress.getLocalHost().getHostAddress();
    		workerThread = Runtime.getRuntime().availableProcessors() * 2;
		} catch (UnknownHostException e) {
			logger.error("get default local host information failed.", e);
		}
    }
	@Override
	public void init(Map<String, String> params) throws Exception {
		if ( params != null && params.size() > 0 ){
			String ip = params.get("ip");
			String port = params.get("port");
			String workerThread = params.get("workerthread");
			if( ip != null && !ip.trim().isEmpty() ){
				this.ip = ip;
			}
			if( port != null && !port.trim().isEmpty() ){
				this.port = Integer.valueOf(port);
			}
			if( workerThread != null && !workerThread.trim().isEmpty() ){
				this.workerThread = Integer.valueOf(workerThread);
			}
			pipelineFactoryClass = params.get("pipeline_factory_class");
		}			
	}

	@Override
	public void start() throws Exception {
		if( logger.isInfoEnabled() ){
			logger.info("start listener: NettyServerListener,{}:{}",this.ip, this.port );
		}
		/* Configure the server.*/
		bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(
				Executors.newCachedThreadPool(), Executors.newFixedThreadPool(workerThread), workerThread));

		/*Enable TCP_NODELAY to handle pipelined requests without latency.*/
		bootstrap.setOption("child.tcpNoDelay", true);
		bootstrap.setOption("keepAlive", true);

		/* Set up the event pipeline factory.*/
		ChannelPipelineFactory factory = null;
		if( pipelineFactoryClass != null && !pipelineFactoryClass.trim().isEmpty()){
			
			try{
			    factory = (ChannelPipelineFactory)Class.forName(pipelineFactoryClass).newInstance();
			}catch(Exception e){
				logger.error("init pipeline factory failed: {}", pipelineFactoryClass, e);
			}
		}
		if( factory == null )
		    bootstrap.setPipelineFactory(new HttpServerPipelineFactory());
		else
			bootstrap.setPipelineFactory(factory);
		

		/*Bind and start to accept incoming connections.*/
		bootstrap.bind(new InetSocketAddress(ip,port));
	}

	@Override
	public void destroy() throws Exception {
		bootstrap.releaseExternalResources();
		bootstrap.shutdown();
	}

}
