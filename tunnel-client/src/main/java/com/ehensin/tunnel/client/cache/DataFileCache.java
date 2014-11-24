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
package com.ehensin.tunnel.client.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ehensin.tunnel.client.profile.Param;
import com.ehensin.tunnel.client.profile.CacheProfile;
import com.ehensin.tunnel.client.profile.CacheReader;
import com.ehensin.tunnel.client.profile.CacheWriter;
import com.ehensin.tunnel.client.util.LogUtil;


/** 
 * 
 * 
 **/
public abstract class DataFileCache implements IDataCache<String>{
	protected IDataCacheListener listener;
	public DataFileCache(){
	}
	@Override
	public void init(CacheProfile profile) {
		LogUtil.debug("init DataFileCache", null);
		/*initialize cache writer*/
		CacheWriter writer = profile.getWriter();
		Map<String, String> paramMap = new HashMap<String, String>();
		for( Param param : writer.getParames() ){
			LogUtil.debug("init cache writer , Param : {0} : {1}", new String[]{param.getName(), param.getValue()});	
			paramMap.put(param.getName(), param.getValue());
		}
		this.initForWrite(paramMap);
		
		/*initialize cache reader*/
		CacheReader reader = profile.getReader();
		paramMap = new HashMap<String, String>();
		for( Param param : reader.getParames() ){
			LogUtil.debug("init cache reader , Param : {0} : {1}", new String[]{param.getName(), param.getValue()});	
			paramMap.put(param.getName(), param.getValue());
		}
		this.initForRead(paramMap);
		
	}
    protected void initForRead(Map<String, String> params){
		
	}
	protected void initForWrite(Map<String, String> params){
		
	}
	@Override
	public List<String> read(int size) {
		return null;
	}

	@Override
	public void store(Object obj) throws Exception {
		
	}
	@Override
	public void checkpoint(){
		
	}


}
