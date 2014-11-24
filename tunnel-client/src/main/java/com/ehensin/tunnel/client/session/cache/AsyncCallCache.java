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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AsyncCallCache  implements ISessionCache {
	/* map cache object id to cache object */
	private Map<Object, Object> cacheObjMap;

	public AsyncCallCache() {
		cacheObjMap = new java.util.concurrent.ConcurrentHashMap<Object, Object>();
	}

	@Override
	public Object find(Object id) {
		Object obj =  cacheObjMap.get(id);
		return obj;
	}

	@Override
	public void store(Object id, Object cachedObj) {
		cacheObjMap.put(id, cachedObj);
	}

	@Override
	public void remove(Object id) {
		cacheObjMap.remove(id);
	}

	@Override
	public void clear() {
		cacheObjMap.clear();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> getAll() {
		return new ArrayList(cacheObjMap.values());
	}

	@Override
	public List<Object> getKeys() {
		return new ArrayList(cacheObjMap.keySet());
	}
}
