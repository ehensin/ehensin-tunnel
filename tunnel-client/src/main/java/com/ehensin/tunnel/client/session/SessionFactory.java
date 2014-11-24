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

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.ehensin.tunnel.client.event.EventDispatcher;
import com.ehensin.tunnel.client.event.EventTypeEnum;
import com.ehensin.tunnel.client.locator.LocatorFactor;
import com.ehensin.tunnel.client.session.cache.AsyncCallCache;
import com.ehensin.tunnel.client.session.cache.ISessionCache;
import com.ehensin.tunnel.client.session.cache.SimpleSessionCache;
/**
 * session factory
 * */
public class SessionFactory {
    private final static SessionFactory instance = new SessionFactory();
    private ISessionCache cache = new SimpleSessionCache();
    private ISessionCache asyncCallCache = new AsyncCallCache();
    private AtomicLong sessionIdIndex = new AtomicLong(1);
    private Object lockObj = new Object();
    /* schedule to clean session periodically */
	private ScheduledExecutorService scheduleService;
	private SessionFactory(){
		/* start */
		scheduleService = Executors.newScheduledThreadPool(1);
		scheduleService.scheduleAtFixedRate(new SessionCLeaner(), 0,10,
				TimeUnit.SECONDS);
		/*register callback event*/
		EventDispatcher.getInstance().register(EventTypeEnum.ASYNC_CALLBACK, new CallBackEventListener());
	}
    public static SessionFactory getFactory(){
    	return instance;
    }
    
    /**
     * create a new stateful session
     * @param timeout session timeout
     * @return return ISession object
     * */
    public ISession createStatefulSession(long timeout){
    	synchronized(lockObj){
	    	ISession session = new StatefulSession(sessionIdIndex.incrementAndGet());
    		if( timeout > 0 )
    		    session.setTimeout(timeout);
    		cache.store(session.getId(), session);
	    	return session;
    	}
    	
    }
    /*create statefull session with portion locator factor and tunnel locate factor*/
    public ISession createStatefulSession(long timeout, LocatorFactor<?> pf, LocatorFactor<?> tf){
    	synchronized(lockObj){
	    	ISession session = new StatefulSession(sessionIdIndex.incrementAndGet(),pf, tf);
    		if( timeout > 0 )
    		    session.setTimeout(timeout);
    		cache.store(session.getId(), session);
	    	return session;
    	}
    	
    }
    /**
     * get statuful session by session id;
     * */
    public ISession getStatfulSession(long sessionId)throws SessionException{
    	ISession session = (ISession)cache.find(sessionId);
    	if( session != null && session.isValidate()){
    		return session;
    	}
    	return null;
    }
    /**
     * create StatelessSession
     * @return ISession object
     * */
    public ISession createStatelessSession(){
    	ISession session = new StatelsessSession(sessionIdIndex.incrementAndGet());
    	return session;
    }
    
    public ISession createStatelessSession(LocatorFactor<?> pf, LocatorFactor<?> tf){
    	ISession session = new StatelsessSession(sessionIdIndex.incrementAndGet(),pf, tf);
    	return session;
    }
    
    public ISessionCache getAsyncCallCache(){
    	return this.asyncCallCache;
    }
    
    class SessionCLeaner implements Runnable {

		@Override
		public void run() {
			List keys =  cache.getKeys();
			for(Object key : keys){
				String accountId = (String)key;
				StatefulSession session = (StatefulSession)cache.find(accountId);
				 
				if( !session.isValidate() ){
					cache.remove(session.getId());
					continue;					
				}
				/*超时处理*/
				long now = System.currentTimeMillis();
				if((now - session.getLastAccessTime()) >= session.getTimeout()){
					try {
						cache.remove(session.getId());
						session.invalidate();
					} catch (Exception e) {

					}
				}
			}
		}

	}
}
