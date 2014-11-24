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
package com.ehensin.tunnel.client.statistic;

import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * statistic center is to collect statistic information from different subsystem for monitor
 * 
 * */
public class StatisticCenter implements Observer{
    private final static StatisticCenter instance = new StatisticCenter();
    
    private Map<String, List<StatisticItem<?>>> targetStatisticInfo ;
    private Map<String, Map<String, List<StatisticItem<?>>>> subjectStatisticInfo ;
    /*private construction for avoiding creation from out*/
    private StatisticCenter(){
    	targetStatisticInfo = new java.util.concurrent.ConcurrentHashMap<String, List<StatisticItem<?>>>();
    	subjectStatisticInfo = new java.util.concurrent.ConcurrentHashMap<String, Map<String, List<StatisticItem<?>>>>();
    }
    
    public static StatisticCenter getInstance(){
    	return instance;
    }
    /**
     * get subject statistic information which is a map from statistic target to {@link StatisticItem}
     * @param subject statistic subject , For example, channel , tunnel and so on 
     * @return {@link StatisticItem} map or null if no subject statistic info exists
     * */
    public Map<String, List<StatisticItem<?>>> getSubjectStatisticInfo(String subject){
    	return subjectStatisticInfo.get(subject);
    }
    
    /**
     * get target statistic information which is a map from statistic quota to {@link StatisticItem}
     * @param subject statistic subject , For example, channel , tunnel and so on 
     * @return {@link StatisticItem} list or null if no subject statistic info exists
     * */
    public List<StatisticItem<?>> getTargetStatisticInfo(String targetName){
    	return targetStatisticInfo.get(targetName);
    }
    /**
     * get all statistic info which is a map from subject to sub map which from target to {@link StatisticItem}
     * */
    public Map<String, Map<String, List<StatisticItem<?>>>> getStatisticInfo(){
    	return subjectStatisticInfo;
    }

	@Override
	public void update(Observable o, Object arg) {
		if( arg != null &&  arg  instanceof  List<?>){
		     List<StatisticItem<?>> items = ( List<StatisticItem<?>> )arg;
		     if( items.size() > 0 ){
		    	 targetStatisticInfo.put(items.get(0).getTargetName(), items);
		    	 Map<String, List<StatisticItem<?>>> subjectMap = subjectStatisticInfo.get(items.get(0).getSubject());
		    	 if( subjectMap == null ){
		    		 subjectMap = new java.util.concurrent.ConcurrentHashMap<String, List<StatisticItem<?>>>();
		    		 subjectStatisticInfo.put(items.get(0).getSubject(), subjectMap);
		    	 }
		    	 subjectMap.put(items.get(0).getTargetName(), items);
		     }
		}
	}
}
