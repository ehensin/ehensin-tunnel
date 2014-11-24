package com.ehensin.tunnel.server.listener.netty.http;

import static org.jboss.netty.channel.Channels.pipeline;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.http.HttpContentCompressor;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;

import com.ehensin.tunnel.server.listener.netty.ChannelConnectEventHandler;

public class HttpServerPipelineFactory implements ChannelPipelineFactory {

	public HttpServerPipelineFactory() {
	}

	public ChannelPipeline getPipeline() throws Exception {
		// Create a default pipeline implementation.
		ChannelPipeline pipeline = pipeline();
		/*add accept event handler*/
		pipeline.addLast("acceptEventHandler", new ChannelConnectEventHandler());
		/*add http related handler*/
		pipeline.addLast("decoder", new HttpRequestDecoder());
		pipeline.addLast("encoder", new HttpResponseEncoder());
		pipeline.addLast("deflater", new HttpContentCompressor());
		/*add http message handler*/
		pipeline.addLast("message_handler", new HttpServerUpstreamHandler());
		return pipeline;
	}

}
