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

import com.ehensin.tunnel.client.event.IEventListener;
import com.ehensin.tunnel.client.event.ITunnelEvent;
import com.ehensin.tunnel.client.profile.LocatorFactorType;
import com.ehensin.tunnel.client.profile.LocatorPolicy;

/**
 * abstractor locator implements the ILocator interface
 * 
 * */
abstract public class Locator<T> implements ILocator<T>, IEventListener{
    /*policy */
    protected LocatorPolicy locatorPolicy;
    /*algorithm*/
    protected LocatorAlgEnum alg;
    /*factor type*/
    protected LocatorFactorType factorType;
   
    public Locator(LocatorPolicy p, LocatorAlgEnum alg, LocatorFactorType type){
    	super();
    	this.locatorPolicy = p;
    	this.alg = alg;
    	this.factorType = type;    	
    }
	@Override
	public LocatorPolicy getLocatorPolicy() {
		return locatorPolicy;
	}

	@Override
	public LocatorAlgEnum getLocatorAlg() {
		return alg;
	}

	@Override
	public LocatorFactorType getFactorType() {
		return factorType;
	}
	@Override
	public T locate(LocatorFactor<?> factor){
		/*check if the factor value type match the factor type */
		if( (factor == LocatorFactor.NULLFactorObj) &&  !(this instanceof RRLocator))
			throw new IllegalArgumentException(" factor not match the locator instance type");
		if( (factor != LocatorFactor.NULLFactorObj) && !(factor.getFactorObj().getClass().getName().equalsIgnoreCase(this.getFactorType().getType())))
			throw new IllegalArgumentException(" factor not match the locator instance type");
		return this.ablocate(factor);
	}
	@Override
	public void onEvent(Object src, ITunnelEvent event) throws Exception{
		if( src instanceof IGradeSupport ) {
			GradeEnum oldGrade = (GradeEnum)event.getEventObject();
			IGradeSupport newGrade = (IGradeSupport)src;
			if( newGrade.getGrade().getGrade() == oldGrade.getGrade() )
				return;
			this.updateGrade((IGradeSupport)src, oldGrade);
		}
	}
	/*internal locate function*/
	abstract protected T ablocate(LocatorFactor<?> factor);
	
	abstract public void updateGrade(IGradeSupport newGrade, GradeEnum oldGrade);
}
