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
package com.ehensin.tunnel.server.listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ehensin.tunnel.server.profile.WhiteListProfile;
import com.ehensin.tunnel.server.profile.WhiteProfile;

public class WhiteListController {
	private Map<String,String> whiteListMap;
	private boolean isOn;
    public WhiteListController(WhiteListProfile profile){
    	whiteListMap = new HashMap<String, String>();
    	List<WhiteProfile> wps = profile.getWhites();
    	isOn = profile.getSwitcher() != null && profile.getSwitcher().equalsIgnoreCase("on");
    	if( wps != null && wps.size() > 0 ){
    		for(WhiteProfile wp : wps ){
    			whiteListMap.put(wp.getIp(), wp.getIp());
    		}
    	}
    }
    
    public boolean allow(String ip){
    	if( !isOn )
    	    return true;
    	return whiteListMap.get(ip) != null;
    }
}
