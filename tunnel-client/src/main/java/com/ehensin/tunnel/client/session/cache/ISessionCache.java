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
package com.ehensin.tunnel.client.session.cache;

import java.util.List;

/**
 * 
 **/
public interface ISessionCache {

	/**
	 * find object from cache
	 * 
	 * @param id
	 *            , the unique identity for cached object, this parameter must
	 *            implement the equal and hashCode functions for store and get
	 *            operation.
	 * @return cached object, or null if no object find.
	 * @author hhbzzd
	 * */
	public Object find(Object id);
	
	

	/**
	 * cache object
	 * 
	 * @param id
	 *            , the unique identity for cached object, this parameter must
	 *            implement the equal and hashCode functions for store and get
	 *            operation.
	 * @param cachedObj
	 *            , the cached object which cannot be null.
	 * @author hhbzzd
	 * */
	public void store(Object id, Object cachedObj);

	/**
	 * remove cached data
	 * */
	public void remove(Object id);

	/**
	 * clear cache
	 * */
	public void clear();

	/**
	 * get all
	 * */
	public List<Object> getAll();
	/**
	 * get all keys
	 * */
	public List<Object> getKeys();
	
}
