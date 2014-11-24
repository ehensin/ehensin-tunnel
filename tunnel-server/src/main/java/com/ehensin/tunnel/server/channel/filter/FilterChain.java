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
package com.ehensin.tunnel.server.channel.filter;

import java.util.Iterator;
import java.util.LinkedList;

import com.ehensin.tunnel.server.protocal.message.MsgProtocol;

public class FilterChain {
	java.util.LinkedList<IChannelFilter> filterList;
	IChannelFilter first;
	IChannelFilter last;
	IChannelFilter current;
	Iterator<IChannelFilter> it;
	public FilterChain(){
		filterList = new LinkedList<IChannelFilter>();
	}
	public void doFilter(MsgProtocol msg){
		if( it == null )
			it = filterList.iterator();
		if( it.hasNext() )
		    current = it.next();
		else
			current = null;
		
		if( current == null )
			return;
		current.doFilter(msg, this);
	}
	
	public void addFirst(IChannelFilter filter){
		first = filter;
		filterList.addFirst(filter);
	}
	
	public void addLast(IChannelFilter filter){
		last = filter;
		filterList.addLast(filter);
	}
	
	public IChannelFilter getCurrent(){
		return current;
	}
	
	public void reset(){
		it = filterList.iterator();
	}
	
	
}
