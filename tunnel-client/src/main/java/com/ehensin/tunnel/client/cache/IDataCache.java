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

import java.util.List;

import com.ehensin.tunnel.client.profile.CacheProfile;


/** 
 * cache interface
 **/
public interface IDataCache<T> {
	public static final String CACHE_ATTR_READ = "r";
	public static final String CACHE_ATTR_WRITE = "w";
	public static final String CACHE_ATTR_RW = "rw";
	/**init cache
	 * @param profile cache profile description
	 * */
	public void init(CacheProfile profile);
	/**cache object*/
    public void store(Object obj)throws Exception;
    /**read object from cache .
     *@param size the object count
     *@return cached object list
     **/
    public List<T> read(int size);
    
    /**set checkpoint */
    public void checkpoint();
}
