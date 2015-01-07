package com.ehensin.tunnel.server.listener.netty.http;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ehensin.tunnel.server.channel.IServerChannel;
import com.ehensin.tunnel.server.channel.codec.CodecException;
import com.ehensin.tunnel.server.channel.codec.CodecFactory;
import com.ehensin.tunnel.server.channel.codec.ICodec;
import com.ehensin.tunnel.server.channel.filter.FilterChain;
import com.ehensin.tunnel.server.channel.filter.FilterChainFactory;
import com.ehensin.tunnel.server.protocal.message.MsgProtocol;
import com.ehensin.tunnel.server.tunnel.ServerTunnel;
import com.ehensin.tunnel.server.tunnel.ServerTunnelFactory;

public class HttpServerUpstreamHandler extends SimpleChannelUpstreamHandler {
	private static Logger logger = LoggerFactory.getLogger(HttpServerUpstreamHandler.class);

	public HttpServerUpstreamHandler() {
	}

	/*system use the keep alive protocol to keep long connection*/
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
		HttpRequest request = (HttpRequest) e.getMessage();
		String content = null;

		ChannelBuffer channelBuffer = request.getContent();
		if (channelBuffer.readable())
			content = channelBuffer.toString(CharsetUtil.UTF_8);
		if( content == null || content.isEmpty() )
			return;
		
		ICodec<String> codec = (ICodec<String>) CodecFactory.getFactory().getCodec("json");
		MsgProtocol msg = null;
		try {
			msg = codec.decode(content);
		} catch (CodecException ce) {
			logger.error("cannot decode message : {} ", content, ce );
			return;
		}
		
		/*construct message filter chain to handle message*/
		String ip = ((InetSocketAddress)ctx.getChannel().getRemoteAddress()).getAddress().getHostAddress();
		ServerTunnel tunnel = ServerTunnelFactory.getFactory().getTunnel(ip);
    	IServerChannel c = tunnel.getChannel(ctx.getChannel().getId());
    	if( c == null ){
    		if( logger.isDebugEnabled() ){
    			logger.debug("cannot find channel for current message, channel id: {}", ctx.getChannel().getId());
    			List<IServerChannel> channels = tunnel.getChannels();
    			for(IServerChannel ch : channels){
    				logger.debug("cached channel: {} " , ch.getId());
    			}
    		}
    		/*sometimes some channel have not been cached on time because of netty nio mechanism*/
    		try {
				TimeUnit.MILLISECONDS.sleep(10);
			} catch (InterruptedException e1) {
				logger.error("cannot sleep current thread", e1);
			}
    		
    	}
    		
		FilterChain filterChain = FilterChainFactory.getFactory().getFilterChain(c);
		filterChain.doFilter(msg);
		
        if( logger.isDebugEnabled() ){
        	logger.debug("Get message from client(ip : {}){}", ctx.getChannel().getRemoteAddress(),content);
        }
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		logger.error(e.getCause().getMessage(), e.getCause());
	}

}
