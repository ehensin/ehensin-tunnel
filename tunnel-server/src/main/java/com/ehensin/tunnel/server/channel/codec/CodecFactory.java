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
package com.ehensin.tunnel.server.channel.codec;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ehensin.tunnel.server.ServerStartup;
import com.ehensin.tunnel.server.profile.CodecProfile;
import com.ehensin.tunnel.server.profile.CodecsProfile;

public class CodecFactory {
	private static Logger logger = LoggerFactory.getLogger(CodecFactory.class);
    private static final CodecFactory factory = new CodecFactory();
    private Map<String, ICodec<?>> supportCodec;
    private CodecFactory(){
    	supportCodec = new HashMap<String, ICodec<?>>();
    	CodecsProfile profile = ServerStartup.getStartup().getProfile().getCodecs();
    	if( profile != null && profile.getCodecs() != null && profile.getCodecs().size() > 0 ){
    		for(CodecProfile codec : profile.getCodecs()){
    			try {
					ICodec<?> c = (ICodec<?>)Class.forName(codec.getCodecClass()).newInstance();
					supportCodec.put(codec.getMsgFormat(), c);
				} catch (InstantiationException e) {
					logger.error("cannot init codec : {} ", codec.getCodecClass(), e);
				} catch (IllegalAccessException e) {
					logger.error("cannot init codec : {} ", codec.getCodecClass(), e);
				} catch (ClassNotFoundException e) {
					logger.error("cannot init codec : {} ", codec.getCodecClass(), e);
				} 
    		}
    	}
    }
    
    public static CodecFactory getFactory(){
    	return factory;
    }
    
    public ICodec<?> getCodec(String format){
    	return supportCodec.get(format);
    }
}
