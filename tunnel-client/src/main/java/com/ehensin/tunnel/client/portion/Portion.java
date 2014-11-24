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
package com.ehensin.tunnel.client.portion;

import java.util.List;

import com.ehensin.tunnel.client.event.EventDispatcher;
import com.ehensin.tunnel.client.event.EventTypeEnum;
import com.ehensin.tunnel.client.info.IInfoCollect;
import com.ehensin.tunnel.client.info.InfoHub;
import com.ehensin.tunnel.client.info.InfoItem;
import com.ehensin.tunnel.client.locator.GradeHashLocator;
import com.ehensin.tunnel.client.locator.IGradeSupport;
import com.ehensin.tunnel.client.locator.INameSupport;
import com.ehensin.tunnel.client.locator.Locator;
import com.ehensin.tunnel.client.locator.LocatorAlgEnum;
import com.ehensin.tunnel.client.locator.LocatorFactor;
import com.ehensin.tunnel.client.locator.NameLocator;
import com.ehensin.tunnel.client.locator.RRLocator;
import com.ehensin.tunnel.client.profile.LocatorPolicy;
import com.ehensin.tunnel.client.profile.PortionProfile;
import com.ehensin.tunnel.client.profile.TunnelDes;
import com.ehensin.tunnel.client.tunnel.Tunnel;

/**
 * portion is a virtual group, which have some tunnels
 * 
 * */
public class Portion implements INameSupport, IInfoCollect{
    private long id;
    private String name;
    private PortionProfile profile;
    private Locator<Tunnel> tunnelLocator;
    
    public Portion(long id, String name, PortionProfile pp){
    	this.id = id;
    	this.name = name;
    	this.profile = pp;	
    	/*init portion*/
    	init();
    	if( this.tunnelLocator instanceof IGradeSupport )
    	    EventDispatcher.getInstance().register(EventTypeEnum.TUNNAL_GRADE, tunnelLocator);
    	
    	InfoHub.getInfoHub().register(this);
    }
    
    private void init(){
    	/*init locator*/
    	/*init locator policy */
    	LocatorPolicy lp = this.profile.getPolicy().getLocatorPolicy();
		if( lp.getLocatorAlg().getType().equalsIgnoreCase(LocatorAlgEnum.HASH.getName()) ){
			tunnelLocator = new GradeHashLocator<Tunnel>(lp, LocatorAlgEnum.HASH, lp.getFactorType(), null);
    	}else if ( lp.getLocatorAlg().getType().equalsIgnoreCase(LocatorAlgEnum.RR.getName()) ){
    		tunnelLocator = new RRLocator<Tunnel>(lp, LocatorAlgEnum.RR, lp.getFactorType(), null);
    	}else if ( lp.getLocatorAlg().getType().equalsIgnoreCase(LocatorAlgEnum.NAME.getName()) ){
    		tunnelLocator = new NameLocator<Tunnel>(lp, LocatorAlgEnum.NAME, lp.getFactorType(), null);
    	}else{
    		throw new java.lang.IllegalArgumentException("unsupported locator policy " + this.profile.getPolicy().getLocatorPolicy().getLocatorAlg().getType());
    	}
    	if(this.profile.getPolicy().getDiscover().equalsIgnoreCase("fix") ){
    		List<TunnelDes> tunnelDess = this.profile.getTunnelList().getTunnels();
    		if( tunnelDess == null || tunnelDess.size() <= 0)
    			throw new java.lang.IllegalArgumentException("fixed tunnel discover policy must have more than one tunnel description");
     		for(TunnelDes td : tunnelDess){
    			Tunnel tunnel = new Tunnel(td.getId(), td.getName(),td.getIp(), td.getPort(), this.profile.getTunnelProfile());
    			tunnelLocator.add(tunnel);
    		}
    	}else{
    		/*need to be filled with tunnels by auto discovering algorithm*/
    	}
    			
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

	public Tunnel getTunnel(LocatorFactor<?> factor) {
		if( tunnelLocator == null )
			return null;
		return tunnelLocator.locate(factor);
	}
	
	public void addTunnel(TunnelDes td){
		Tunnel tunnel = new Tunnel(td.getId(), td.getName(), td.getIp(), td.getPort(), this.profile.getTunnelProfile());
		tunnelLocator.add(tunnel);
	}

	@Override
	public InfoItem getInfo() {
		return new InfoItem("portion", this.getName());
	}    
	public String getType(){
		return "portion";
	}
	public String getCollectName(){
		return name;
	}
    
}
