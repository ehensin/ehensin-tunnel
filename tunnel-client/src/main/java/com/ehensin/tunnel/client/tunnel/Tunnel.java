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
package com.ehensin.tunnel.client.tunnel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ehensin.tunnel.client.ErrorCodeEnum;
import com.ehensin.tunnel.client.channel.ChannelConfig;
import com.ehensin.tunnel.client.channel.ChannelException;
import com.ehensin.tunnel.client.channel.ChannelHelper;
import com.ehensin.tunnel.client.channel.ChannelStatus;
import com.ehensin.tunnel.client.channel.TunnelChannel;
import com.ehensin.tunnel.client.event.EventDispatcher;
import com.ehensin.tunnel.client.event.EventTypeEnum;
import com.ehensin.tunnel.client.info.IInfoCollect;
import com.ehensin.tunnel.client.info.InfoHub;
import com.ehensin.tunnel.client.info.InfoItem;
import com.ehensin.tunnel.client.locator.GradeEnum;
import com.ehensin.tunnel.client.locator.HashLocator;
import com.ehensin.tunnel.client.locator.IGradeSupport;
import com.ehensin.tunnel.client.locator.INameSupport;
import com.ehensin.tunnel.client.locator.Locator;
import com.ehensin.tunnel.client.locator.LocatorAlgEnum;
import com.ehensin.tunnel.client.locator.NameLocator;
import com.ehensin.tunnel.client.locator.RRLocator;
import com.ehensin.tunnel.client.profile.LocatorPolicy;
import com.ehensin.tunnel.client.profile.TunnelProfile;
import com.ehensin.tunnel.client.protocol.MsgProtocol;
import com.ehensin.tunnel.client.protocol.MsgRepProtocol;
import com.ehensin.tunnel.client.statistic.TunnelStatisticIntercepter;
/*
 * tunnel is a point to point communication line. one tunnel has some channels, which is a socket connection.
 * tunnel support grade manager:
 * 1. when tunnel cannot locate a channel, tunnel's grade will be down. or up
 * 2. when tunnel reaches the follow control point, tunnel's grade will be down.  or up
 * */
public class Tunnel implements INameSupport, IGradeSupport, IInfoCollect{
	private static final Logger logger = LoggerFactory
			.getLogger(Tunnel.class);
	
	/*tunnel profile*/
	private TunnelProfile profile;
	/*tunnel id*/
	private long id;
	/*tunnel name*/
    private String name;
    /*remote ip*/
    private String remoteIp;
    private int remotePort;
    /*tunnel grade*/
    private GradeEnum grade;
    private InfoItem info;
    /*tunnel intercepter*/
    private List<ITunnelIntercepter> intercepters;
    /*channel locator*/
    private Locator<TunnelChannel> channelLocator;   
    /*all channels */
    private List<TunnelChannel> channels;
	/* schedule service for renew invalid channel */
	private ScheduledExecutorService renewChannleService;
	private TunnelFlowControllerIntercepter flowController;
	private TunnelStatisticIntercepter statistic;
    public Tunnel(long id, String name, String ip, int port, TunnelProfile profile){
    	this.id = id;
    	this.name = name;
    	this.profile = profile;
    	this.grade = GradeEnum.Normal;
    	this.remoteIp = ip;
    	this.remotePort = port;
    	/*init info*/
    	this.info = new InfoItem("tunnel", name + id);
    	Map<String, String> infoMap = new HashMap<String, String>();
    	infoMap.put("total", "0");
    	infoMap.put("day", "0");
    	infoMap.put("hour", "0");
    	infoMap.put("avg", "0");
    	infoMap.put("grade", grade.getDes());
    	this.info.setInfo(infoMap);
    	InfoHub.getInfoHub().register(this);
    	
    	this.intercepters = new ArrayList<ITunnelIntercepter>();
    	/*add statistic intercepter*/
    	statistic = new TunnelStatisticIntercepter(name + id);
    	this.intercepters.add(statistic);
    	/*add flow controller intercepter*/
    	if( profile.getPolicy().getFlowControl().getSwich().equalsIgnoreCase("on") ){
    		flowController = new TunnelFlowControllerIntercepter(profile.getPolicy().getFlowControl().getMaxtps());
    	    this.intercepters.add(flowController);
    	}
    	
    	/*initialize channel*/
    	int channelCount = this.profile.getPolicy().getSocket().getSize();
    	ChannelConfig config = getChannelConfig(profile, ip, port);
    	if( logger.isDebugEnabled() ){
    		logger.debug("init channels, count : {}", channelCount);
    	}
    	channels = new ArrayList<TunnelChannel>();
    	for( int i = 0 ; i < channelCount; i++) {
    		TunnelChannel channel = ChannelHelper.buildChannel(this.id,config);
    		/*if channel status is not connected, set the channel grade to bad*/
    		if( channel.getStatus() != ChannelStatus.Normal ){
    			channel.setGrade(GradeEnum.Bad);
    		}
			channels.add(channel);
    	}
    	/*init locator*/
    	initLocator(channels);
    	/*init intercepters*/
    	initIntercepters(this.profile.getPolicy().getInterceptors());
    	
    	/*register grade event */
    	if( this.channelLocator instanceof IGradeSupport )
    	    EventDispatcher.getInstance().register(EventTypeEnum.CHANNEL_GRADE, this.channelLocator);
    	
    	/*schedule to renew invalid channel*/
    	renewChannleService = Executors.newScheduledThreadPool(1);
    	renewChannleService.scheduleAtFixedRate(new RenewTask(), 5, 6,
				TimeUnit.SECONDS);
    }

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getRemoteIp() {
		return remoteIp;
	}

	public void setRemoteIp(String remoteIp) {
		this.remoteIp = remoteIp;
	}

	public int getRemotePort() {
		return remotePort;
	}

	public void setRemotePort(int remotePort) {
		this.remotePort = remotePort;
	}

	public TunnelProfile getTunnelProfile(){
		return this.profile;
	}
	
	public TunnelResponse invoke(TunnelRequest req){
		/*previous invoke*/
		for(ITunnelIntercepter intercepter : intercepters){
			boolean b = intercepter.preInvoke(req);
			if( !b ){
				if( intercepter instanceof TunnelFlowControllerIntercepter){
					/*flow control will make this tunnel grade to low,but not to bad */
					GradeEnum g = this.grade.down(this.grade);
					if( g.getGrade() == GradeEnum.Bad.getGrade() )
						g = GradeEnum.Low;
					this.setGrade(g);
					return new TunnelResponse(req.getMsgProtocal().getMsgHeader().getMsgId(), false, ErrorCodeEnum.TunnelFlowControl,
							"flow control" , null);
				}
				return new TunnelResponse(req.getMsgProtocal().getMsgHeader().getMsgId(), false, ErrorCodeEnum.TunnelInterceptFailed,
						intercepter.getIntercepterName() + " handle preInvoke failed" , null);
			}
		}
		TunnelChannel channel = channelLocator.locate(req.getChannelLocatorFactor());
		if( channel == null ){
			/*grade down*/
			this.setGrade(this.grade.down(this.grade));
			return new TunnelResponse(req.getMsgProtocal().getMsgHeader().getMsgId(), false, ErrorCodeEnum.TunnelChannelUnavailable,
					"no channel available" , null); 
		}
		this.setGrade(this.grade.up(this.grade));
		
		if( channel.getStatus().getStatus() != ChannelStatus.Normal.getStatus() ){
			return new TunnelResponse(req.getMsgProtocal().getMsgHeader().getMsgId(), false, ErrorCodeEnum.TunnelChannelInvalid,
					"channel is in invalid status" , null); 
		}
		MsgProtocol rep = null;
		try {
			if( req.getIsSyncCall() != null )
			    rep = channel.send(req.getMsgProtocal(),req.getIsSyncCall());
			else
				rep = channel.send(req.getMsgProtocal(),channel.getChannelConfig().isSyncCall());
		} catch (ChannelException e) {
			return new TunnelResponse(req.getMsgProtocal().getMsgHeader().getMsgId(),false, ErrorCodeEnum.getErrorCodeEnum(e.getErrorCode()),
					"channel exception " , e); 
		}
		
		/*post invoke*/
		for(ITunnelIntercepter intercepter : intercepters){
			intercepter.postInvoke();
		}
		/*if async call the rep is null*/
		if( req.getIsSyncCall() )
		    return new TunnelResponse(((MsgRepProtocol)rep.getBody()).getSrcMsgId(), true, null, null, rep);
		else
			return new TunnelResponse(null, true, null, null, rep);
	}
	
	private ChannelConfig getChannelConfig(TunnelProfile profile, String ip, int port){
    	ChannelConfig config = new ChannelConfig();
    	config.setBlocking(this.profile.getPolicy().getSocket().getBlocking().equalsIgnoreCase("bio"));
    	config.setConnectionTimeout(this.profile.getPolicy().getSocket().getConnectionTimeout());
    	config.setKeepalive(this.profile.getPolicy().getSocket().getKeeplive().equalsIgnoreCase("true"));
    	if(this.profile.getPolicy().getSecurity().getSslSwich().equalsIgnoreCase("on")){
    	    config.setKeyStorePath(this.profile.getPolicy().getSecurity().getSslCofig().getKeyStore());
    	    config.setKeyStorePwd(this.profile.getPolicy().getSecurity().getSslCofig().getPassword());
    	}
    	config.setProtocalType(this.profile.getPolicy().getSocket().getType());
    	config.setSocketTimeout(this.profile.getPolicy().getSocket().getSocketTimeout());
    	config.setSSLSupport(this.profile.getPolicy().getSecurity().getSslSwich().equalsIgnoreCase("on"));
    	config.setWorkThreads(this.profile.getPolicy().getSocket().getWorkThreads());
    	config.setIp(ip);
    	config.setPort(port);
    	if( this.profile.getPolicy().getSocket().getIsSyncCall() != null )
    	    config.setSyncCall(this.profile.getPolicy().getSocket().getIsSyncCall().equalsIgnoreCase("sync"));
    	else
    		config.setSyncCall(false);
    	config.setIntercepters(profile.getPolicy().getSocket().getInterceptors());
    	return config;
    }

    private void initLocator(List<TunnelChannel> channels){
    	/*init locator policy */
    	LocatorPolicy lp = this.profile.getPolicy().getLocatorPolicy();
		if( lp.getLocatorAlg().getType().equalsIgnoreCase(LocatorAlgEnum.HASH.getName()) ){
			channelLocator = new HashLocator<TunnelChannel>(lp, LocatorAlgEnum.HASH, lp.getFactorType(), channels);
    	}else if ( lp.getLocatorAlg().getType().equalsIgnoreCase(LocatorAlgEnum.RR.getName()) ){
    		channelLocator = new RRLocator<TunnelChannel>(lp, LocatorAlgEnum.RR, lp.getFactorType(), channels);
    	}else if ( lp.getLocatorAlg().getType().equalsIgnoreCase(LocatorAlgEnum.NAME.getName()) ){
    		Map<String, TunnelChannel> nameChannels = Collections.synchronizedMap(new HashMap<String, TunnelChannel>());
        	for(TunnelChannel c : channels)
        		nameChannels.put(c.getName(), c);
    		channelLocator = new NameLocator<TunnelChannel>(lp, LocatorAlgEnum.NAME, lp.getFactorType(), nameChannels);
    	}else{
    		throw new java.lang.IllegalArgumentException("unsupported locator policy " + this.profile.getPolicy().getLocatorPolicy().getLocatorAlg().getType());
    	}
    	    	
    }
    
    private void initIntercepters(String intercepterClass){
    	if( intercepterClass == null || intercepterClass.trim().isEmpty())
    		return;
    	String[] clazzs = intercepterClass.split(";");
    	for(String clazz : clazzs ){
    	    try {
				ITunnelIntercepter intercepter = (ITunnelIntercepter)Class.forName(clazz).newInstance();
				this.addIntercepter(intercepter);
			} catch (InstantiationException e) {
				logger.error("cannot create tunnel intercepter {} insance", clazz, e);
			} catch (IllegalAccessException e) {
				logger.error("cannot create tunnel intercepter {} insance", clazz, e);
			} catch (ClassNotFoundException e) {
				logger.error("cannot create tunnel intercepter {} insance", clazz, e);
			}
    	}
    }
	
	/**
	 * add intercepter
	 * */
    public void addIntercepter(ITunnelIntercepter intercepter){
    	intercepters.add(intercepter);
    }
	/*Grade support function*/
	@Override
	public GradeEnum getGrade() {
		return this.grade;
	}

	@Override
	synchronized public GradeEnum setGrade(GradeEnum grade) {
		GradeEnum oldGrad = this.grade;
		if( oldGrad.getGrade() == grade.getGrade() )
			return this.grade;
		this.grade = grade;
		TunnelGradeEvent event = new TunnelGradeEvent(oldGrad);
		try {
			EventDispatcher.getInstance().dispatch(this, event);
		} catch (Exception e) {
			logger.error("dispatch channel {} grade event failed", this.getId(), e);
		}
		return this.grade;
	}

	/**
	 * IInfoCollect implementation
	 * */
	
	@Override
	public InfoItem getInfo() {
		this.info.getInfo().put("total", statistic.getTotalStatistic().getValue().toString());
		this.info.getInfo().put("day", statistic.getDayStatistic().getValue().toString());
		this.info.getInfo().put("hour", statistic.getHourStatistic().getValue().toString());
		if( flowController != null )
		    this.info.getInfo().put("avg", "" + flowController.getAverageTps());
		
		this.info.getInfo().put("grade", grade.name());
    	
		return info;
	}
	
	public String getType(){
		return "tunnel";
	}
	public String getCollectName(){
		return name + id;
	}
	
   /**
    * renew task to renew invalid channel
    * */
   class RenewTask implements Runnable {
		

		@Override
		public void run() {
			for( TunnelChannel channel : channels){
				if( channel.getStatus().getStatus() == ChannelStatus.Invalid.getStatus() ){
					try {
						if( logger.isDebugEnabled() )
							logger.debug("renew channel({} {})",new Object[]{id, channel.getId()});
						channel.renew();
						setGrade(grade.up(grade));
					} catch (ChannelException e) {
						logger.error("renew channel({} {}) failed",new Object[]{id, channel.getId()}, e);
					}
				}
			}
			
		}
   }




    
}
