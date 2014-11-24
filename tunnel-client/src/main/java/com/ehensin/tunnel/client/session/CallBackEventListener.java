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

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ehensin.tunnel.client.DefaultCallBack;
import com.ehensin.tunnel.client.ErrorCodeEnum;
import com.ehensin.tunnel.client.channel.codec.CodecException;
import com.ehensin.tunnel.client.event.IEventListener;
import com.ehensin.tunnel.client.event.ITunnelEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CallBackEventListener implements IEventListener{
	private final Logger logger = LoggerFactory
			.getLogger(CallBackEventListener.class);
    public CallBackEventListener(){
    	
    }
	@Override
	public void onEvent(Object src, ITunnelEvent event) throws Exception {
        if( event != null && event instanceof CallbackEvent){
        	if( logger.isDebugEnabled() ){
    			logger.debug("get a call back event: message id {}, callback result {}", src, event.getEventObject());
    		}
        	CallbackEvent e = (CallbackEvent)event;
        	String msgId = e.getSrcMsgId();
        	DefaultCallBack<?> callback = null;
        	if( msgId != null )
    		    callback = (DefaultCallBack<?>)SessionFactory.getFactory().getAsyncCallCache().find(msgId);
        	if(e.isSuccess()){
    		    if( callback != null ){
    		    	if(!callback.getCallbackResultClass().equals(String.class)){
	    				ObjectMapper mapper = new ObjectMapper();
	    				Object result = null;
	    				try {
	    					result = mapper.readValue((String)e.getResult(), callback.getCallbackResultClass());
	    				} catch (IOException ex) {
	    					throw new CodecException("cannot decode result ", ex, ErrorCodeEnum.CodecDecodeError.getCode());
	    				}
	    			    callback.callback(msgId, result);
    		    	}else{
    		    		callback.callback(msgId, (String)e.getResult());
    		    	}
    			}else{
    				logger.error("cannot find callback with this message id {} ", msgId); 
    			}
    		}else{
    			if( callback != null )
    			    callback.exceptionCaught(msgId, e.getErrorCode(), e.getErrorDes());
    			else{
    				logger.error("get an failed callback event,{},{}", new Object[]{e.getErrorCode(), e.getErrorDes()}, e.getResult());
    			}
    		}
		}
		
		
	}

}
