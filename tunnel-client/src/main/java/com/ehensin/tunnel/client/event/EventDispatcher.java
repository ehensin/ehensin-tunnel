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
package com.ehensin.tunnel.client.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * event dispatcher used in tunnel client
 * 
 * */
public class EventDispatcher {
    private final static EventDispatcher instance = new EventDispatcher();
    /*map event to listener*/
    private Map<EventTypeEnum, List<IEventListener>> listenerMap;
    
    private EventDispatcher(){
    	listenerMap = new java.util.concurrent.ConcurrentHashMap<EventTypeEnum, List<IEventListener>>();
    }
    
    public static EventDispatcher getInstance(){
    	return instance;
    }
    
    synchronized public void register(EventTypeEnum et, IEventListener listener){
    	List<IEventListener> listeners = listenerMap.get(et);
    	if( listeners == null ){
    		listeners = new ArrayList<IEventListener>();
    		listenerMap.put(et, listeners);
    	}
    	listeners.add(listener);
    }
    
    synchronized public void unregister(EventTypeEnum et, IEventListener listener){
    	List<IEventListener> listeners = listenerMap.get(et);
    	if( listeners == null ){
    		return;
    	}
    	listeners.remove(listener);
    }
    
    public void dispatch(Object src, ITunnelEvent event)throws Exception{
    	List<IEventListener> listeners = listenerMap.get(event.getEventType());
    	if( listeners == null || listeners.size() <= 0 )
    		return;
    	for( IEventListener listener : listeners ){
    		listener.onEvent(src, event);
    	}
    }
    
}
