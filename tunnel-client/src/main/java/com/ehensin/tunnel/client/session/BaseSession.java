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
package com.ehensin.tunnel.client.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ehensin.tunnel.client.ICallBack;
import com.ehensin.tunnel.client.locator.LocatorFactor;
import com.ehensin.tunnel.client.locator.LocatorHelper;
import com.ehensin.tunnel.client.portion.Portion;
import com.ehensin.tunnel.client.portion.PortionFactory;
import com.ehensin.tunnel.client.tunnel.Tunnel;

public abstract class BaseSession implements ISession{
	private final Logger logger = LoggerFactory
	.getLogger(BaseSession.class);
   
    protected Tunnel getTunnel(LocatorFactor<?> pf, LocatorFactor<?> tf) {
    	Portion p = PortionFactory.getFactory().getPortion(pf);
    	if( tf == null )
    		return p.getTunnel(LocatorHelper.getRRLocatorFactor());
    	return p.getTunnel(tf);
    }
    
    protected void cacheCall(String msgId, ICallBack callback){
    	SessionFactory.getFactory().getAsyncCallCache().store(msgId, callback);
    }
    protected void removeCacheCall(String msgId){
    	SessionFactory.getFactory().getAsyncCallCache().remove(msgId);
    }

}
