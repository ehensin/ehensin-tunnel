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
package com.ehensin.tunnel.client.channel;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ehensin.tunnel.client.ErrorCodeEnum;
import com.ehensin.tunnel.client.channel.codec.CodecException;
import com.ehensin.tunnel.client.channel.codec.JsonCodec;
import com.ehensin.tunnel.client.event.EventDispatcher;
import com.ehensin.tunnel.client.locator.GradeEnum;
import com.ehensin.tunnel.client.locator.IGradeSupport;
import com.ehensin.tunnel.client.locator.INameSupport;
import com.ehensin.tunnel.client.profile.ChannelIntercepter;
import com.ehensin.tunnel.client.profile.Intercepter;
import com.ehensin.tunnel.client.protocol.MsgProtocol;

abstract public class TunnelChannel implements IChannel, INameSupport, IGradeSupport{
	private static final Logger logger = LoggerFactory
			.getLogger(TunnelChannel.class);
	private long id ;
	private long tunnelId;
	private ChannelConfig config;
	private GradeEnum grade;
	private List<IChannelIntercepter> intercepters;
	/*codec*/
	private JsonCodec jsonCodec = new JsonCodec();
	
	/*channel status*/
	protected ChannelStatus status;
	public TunnelChannel(long id, long tunnelId, ChannelConfig config){
		this.id = id;
		this.tunnelId = tunnelId;
		this.config = config;
		this.intercepters = new ArrayList<IChannelIntercepter>();
		this.grade = GradeEnum.Normal;
		this.initIntercepters(config.getIntercepters());
	}
	
	 private void initIntercepters(ChannelIntercepter intercepter){
	    	if( intercepter == null || intercepter.getIntercepters() == null || intercepter.getIntercepters().size() <= 0)
	    		return;

	    	for(Intercepter i : intercepter.getIntercepters() ){
	    		String clazz = null;
	    	    try {
	    	    	clazz = i.getClazz();
					IChannelIntercepter inter = (IChannelIntercepter)Class.forName(clazz).newInstance();
					inter.init(i.getParams(), this);
					this.addChannelIntercepter(inter);
				} catch (InstantiationException e) {
					logger.error("cannot create channel intercepter {} instance", clazz, e);
				} catch (IllegalAccessException e) {
					logger.error("cannot create channel intercepter {} instance", clazz, e);
				} catch (ClassNotFoundException e) {
					logger.error("cannot create channel intercepter {} instance", clazz, e);
				}
	    	}
	    }

	@Override
	public long getId() {
		return id;
	}
	
	@Override
	public String getName() {
		return tunnelId + "" + id;
	}

	@Override
	public long getTunnelId() {
		return tunnelId;
	}

	@Override
	public ChannelStatus getStatus() {
		return status;
	}

	@Override
	public List<IChannelIntercepter> getChannelIntercepters() {
		return intercepters;
	}
	
	public void addChannelIntercepter(IChannelIntercepter intercepter){
		intercepters.add(intercepter);
	}
	
	@Override
	public ChannelConfig getChannelConfig() {
		return config;
	}
	
	@Override
	public MsgProtocol send(MsgProtocol msg, boolean isSyncCall)throws ChannelException {
		if( logger.isDebugEnabled() )
			logger.debug("channel({} {}) send a message:{} ",new Object[]{this.getTunnelId(), this.getId(), msg});
		
		/*encode and decode*/
		String callMsg = null;
		MsgProtocol invokeResult = null;
		try {
			callMsg = jsonCodec.encode(msg);
			/*previous invoke*/
			for(IChannelIntercepter intercepter : intercepters){
				boolean b = intercepter.preInvoke(callMsg);
				if( !b ){
					throw new ChannelException("intercept handle failed " + intercepter.getIntercepterName(),null, ErrorCodeEnum.TunnelInterceptFailed.getCode() );
				}
			}
			String result = this.internalSend(callMsg,isSyncCall);
			if( isSyncCall && result != null){
			    invokeResult = jsonCodec.decode(result);
			    /*post invoke*/
				for(IChannelIntercepter intercepter : this.getChannelIntercepters()){
					intercepter.postInvoke(result);
				}
			}
			
		} catch (CodecException e) {
			throw new ChannelException(e.getMessage(), e, e.getErrorCode() );
		}
		if( logger.isDebugEnabled() )
			logger.debug("channel({} {}) get a result:{} ",new Object[]{this.getTunnelId(), this.getId(), invokeResult});

		return invokeResult;
	}

	/*Grade support function*/
	@Override
	public GradeEnum getGrade() {
		return this.grade;
	}

	@Override
	synchronized public GradeEnum setGrade(GradeEnum grade) {
		GradeEnum old = this.grade;
		if( old.getGrade() == grade.getGrade() )
			return this.grade;
		this.grade = grade;
		if( grade.getGrade() == GradeEnum.Bad.getGrade() ){
			this.status = ChannelStatus.Invalid;
		}
		/**
		 * dispatch grade event
		 * */
		try {
			ChannelGradeEvent event = new ChannelGradeEvent(old);
			EventDispatcher.getInstance().dispatch(this, event);
		} catch (Exception e) {
			logger.error("dispatch channel {} grade event failed", this.getId(), e);
		}
		
		
		return this.grade;
	}
	
    protected abstract String internalSend(String msg, boolean isSync)throws ChannelException ;
	

}
