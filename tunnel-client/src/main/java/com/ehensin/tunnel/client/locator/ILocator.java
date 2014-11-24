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

import com.ehensin.tunnel.client.profile.LocatorFactorType;
import com.ehensin.tunnel.client.profile.LocatorPolicy;
/**
 * locator interface , define the specification of a concrete locator implementation
 * 
 * */
public interface ILocator<T> {
	/**
	 * get a locator policy which define the locator behavior.
	 * 
	 * @return LocatorPolicy object
	 * */
    public LocatorPolicy getLocatorPolicy();
    /**
     * get locator algorithm of this locator
     * @return locator algorithm
     * */
    public LocatorAlgEnum getLocatorAlg();
    
    /**
     * get locator factor value must match this  type
     * */
    public LocatorFactorType getFactorType();
    
    /**
     * locate function to locate a object according to the locator factor.
     * the type ? must match the LocatorFactorType
     * @param factor locator factor
     * @return T which the locator support 
     *         Null if the locator dosen't support the type
     * */
    public T locate(LocatorFactor<?> factor);
    /**
     * add a new T object to this locator, which will adjust current location algorithm result
     * @param t the new T object
     * */
    public void add(T t);
}
