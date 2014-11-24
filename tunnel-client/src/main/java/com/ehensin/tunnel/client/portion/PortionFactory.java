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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ehensin.tunnel.client.locator.HashLocator;
import com.ehensin.tunnel.client.locator.Locator;
import com.ehensin.tunnel.client.locator.LocatorAlgEnum;
import com.ehensin.tunnel.client.locator.LocatorFactor;
import com.ehensin.tunnel.client.locator.NameLocator;
import com.ehensin.tunnel.client.locator.RRLocator;
import com.ehensin.tunnel.client.profile.LocatorPolicy;
import com.ehensin.tunnel.client.profile.PortionProfile;
import com.ehensin.tunnel.client.profile.PortionsProfile;

public class PortionFactory {
	private final static PortionFactory factory = new PortionFactory();
    private PortionsProfile profile;
	private Locator<Portion> portionLocator;
	/**
	 * construction 
	 * throw IllegalArgumentException when locator policy is not supported
	 * */
	private PortionFactory() {
	}
	
	public static PortionFactory getFactory(){
		return factory;
	}
	
	public void init(PortionsProfile profile){
		this.profile = profile;
		/*init partion*/
		List<PortionProfile> pportions = this.profile.getPortions();
		List<Portion> portions = new ArrayList<Portion>();
		for(PortionProfile pp : pportions ){
			Portion p = new Portion(pp.getId(), pp.getName(), pp);
			portions.add(p);
		}	
		/*init locator policy */
		if( this.profile.getPolicy().getLocatorPolicy().getLocatorAlg().getType().equalsIgnoreCase(LocatorAlgEnum.HASH.getName()) ){
			initHashLocator(this.profile.getPolicy().getLocatorPolicy(), portions);
    	}else if ( this.profile.getPolicy().getLocatorPolicy().getLocatorAlg().getType().equalsIgnoreCase(LocatorAlgEnum.RR.getName()) ){
    		initRRLocator(this.profile.getPolicy().getLocatorPolicy(), portions);
    	}else if ( this.profile.getPolicy().getLocatorPolicy().getLocatorAlg().getType().equalsIgnoreCase(LocatorAlgEnum.NAME.getName()) ){
    		initNameLocator(this.profile.getPolicy().getLocatorPolicy(), portions);
    	}else{
    		throw new java.lang.IllegalArgumentException("unsupported locator policy " + this.profile.getPolicy().getLocatorPolicy().getLocatorAlg().getType());
    	}
		
		
	}
	private void initHashLocator(LocatorPolicy lp, List<Portion> portions){
		portionLocator = new HashLocator<Portion>(lp, LocatorAlgEnum.HASH, lp.getFactorType(), portions);
	}
    private void initNameLocator(LocatorPolicy lp, List<Portion> portions){
    	Map<String, Portion> namePortions = Collections.synchronizedMap(new HashMap<String, Portion>());
    	for(Portion p : portions)
    		namePortions.put(p.getName(), p);
    	portionLocator = new NameLocator<Portion>(lp, LocatorAlgEnum.NAME, lp.getFactorType(), namePortions);
	}
    private void initRRLocator(LocatorPolicy lp, List<Portion> portions){
    	portionLocator = new RRLocator<Portion>(lp, LocatorAlgEnum.RR, lp.getFactorType(), portions);
   	}
    /**
     * get portion according to {@link LocatorFactor} 
     * @param LocatorFactor
     * @return portion instance or null if cannot locate a portion
     * */
	public Portion getPortion(LocatorFactor<?> lf) {
		return portionLocator.locate(lf);
	}
	/**
	 * add a new portion to this factory, which will affect the locator algorithm
	 * */
	public void addPortion(PortionProfile pp){
		Portion p = new Portion(pp.getId(), pp.getName(), pp);
		portionLocator.add(p);
	}

}
