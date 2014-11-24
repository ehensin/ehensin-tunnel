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
package com.ehensin.tunnel.client.locator;

import java.util.HashMap;
import java.util.Map;

import com.ehensin.tunnel.client.profile.LocatorFactorType;
import com.ehensin.tunnel.client.profile.LocatorPolicy;

public class NameLocator<T extends INameSupport> extends Locator<T>{
	private Map<String, T> collection;
	/**
     * construction 
     * */
	public NameLocator(LocatorPolicy p, LocatorAlgEnum alg,
			LocatorFactorType type, Map<String, T> collection)  {
		super(p, alg, type);
		this.collection = collection;
	}

	@Override
	protected T ablocate(LocatorFactor<?> factor) {
		if( collection == null || collection.size() <= 0 )
			return null;
		String name = (java.lang.String)factor.getFactorObj();
		Map<String, T> map = (Map<String,T>)this.collection;
		return map.get(name);
	}

	@Override
	public void add(T t) {
		if( collection == null )
			collection = new HashMap<String, T>();
		collection.put(t.getName(), t);
	}

	@Override
	public void updateGrade(IGradeSupport t, GradeEnum grade) {
		/*this locator doesn't support grade operation*/
		throw new java.lang.UnsupportedOperationException();
	}

}
