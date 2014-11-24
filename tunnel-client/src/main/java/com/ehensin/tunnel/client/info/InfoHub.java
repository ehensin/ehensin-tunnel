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
package com.ehensin.tunnel.client.info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * info hub collect all specific object information which implements IInfoCollect interface.
 * 
 * */
public class InfoHub {
	private static final InfoHub hub = new InfoHub();
	private List<IInfoCollect> allCollector;
    private Map<String, Map<String, IInfoCollect>> typeCollectorMap;
    /*map type to info list*/
    private Map<String, List<InfoItem>>  typeInfoMap;
    /* schedule service for renew invalid channel */
	private ScheduledExecutorService renewService;
    private InfoHub(){
    	typeCollectorMap = new HashMap<String, Map<String, IInfoCollect>>();
    	allCollector = new ArrayList<IInfoCollect>();
    	typeInfoMap = new HashMap<String, List<InfoItem>>();
    	/*schedule to renew invalid channel*/
    	renewService = Executors.newScheduledThreadPool(1);
    	renewService.scheduleAtFixedRate(new RenewTask(), 5, 10,
				TimeUnit.SECONDS);
    }
    
    public static InfoHub getInfoHub(){
    	return hub;
    }
    
    public void register(IInfoCollect collect){
    	allCollector.add(collect);
    	
    	Map<String, IInfoCollect> nameCollect = typeCollectorMap.get(collect.getType());
    	if( nameCollect == null ){
    		nameCollect = new HashMap<String, IInfoCollect>();
    		typeCollectorMap.put(collect.getType(), nameCollect);
    	}
    	nameCollect.put(collect.getCollectName(), collect);
    }
    /**
     * get real information of specific object
     * 
     * @param type the information type
     * @param name the name of specific object
     * @return InfoItem or null if cannot locate the collect named with this type and name.
     * */
    public InfoItem getInfo(String type, String name){
    	Map<String, IInfoCollect> nameMap = typeCollectorMap.get(type);
    	if( nameMap == null )
    		return null;
    	IInfoCollect c = nameMap.get(name);
    	if( c == null )
    		return null;
    	return c.getInfo();
    }
    /**
     * get informations according to specific info type
     * */
    public List<InfoItem> getInfos(String type){
    	return typeInfoMap.get(type);
    }
    
    
    synchronized private void renew(IInfoCollect collect){
    	InfoItem item = collect.getInfo();
    	List<InfoItem> items = typeInfoMap.get(item.getType());
    	if( items == null ){
    		items = new ArrayList<InfoItem>();
    		items.add(item);
    		typeInfoMap.put(item.getType(), items);
    	}else{
    		Iterator<InfoItem> it = items.iterator();
    		int index = -1;
    		while( it.hasNext() ){
    			InfoItem i = it.next();
    			index++;
    			if( i.getName().equals(item.getName()) ){
    				items.set(index, item);
    			}
    		}
    		if( index < 0 )
    		    items.add(item);
    	}
    }
    
    
    /**
     * renew task to renew information
     * */
    class RenewTask implements Runnable {
 		@Override
 		public void run() {
 			if(allCollector != null ){
 				for(IInfoCollect c : allCollector){
 					renew(c);
 				}
 			}
 		}
    }
}
