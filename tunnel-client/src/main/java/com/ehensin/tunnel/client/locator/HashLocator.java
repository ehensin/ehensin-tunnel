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

import java.util.ArrayList;
import java.util.List;

import com.ehensin.tunnel.client.profile.LocatorFactorType;
import com.ehensin.tunnel.client.profile.LocatorPolicy;

public class HashLocator<T> extends Locator<T>{
	private List<T> collection;
    private int size = 0;
    /**
     * construction
     * 
     * */
	public HashLocator(LocatorPolicy p, LocatorAlgEnum alg,
			LocatorFactorType type, List<T> collection)  {
		super(p, alg, type);
		this.collection = collection;
    	if( collection != null )
    		this.size = collection.size();
    	this.collection = collection;
			
	}

	@Override
	protected T ablocate(LocatorFactor<?> factor) {
		if( this.size == 0 )
			return null;
		Integer hashCode = (java.lang.Integer)factor.getFactorObj();
		int index = hashCode % size;
		return ((List<T>)this.collection).get(index);
	}

	@Override
	synchronized public void add(T t) {
		if( collection == null )
			collection = new ArrayList<T>();
		collection.add(t);
		this.size = collection.size();
	}

	@Override
	public void updateGrade(IGradeSupport t, GradeEnum grade) {
		/*this locator doesn't support grade operation*/
		throw new java.lang.UnsupportedOperationException();
		
	}

}
