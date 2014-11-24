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
import java.util.Map;

import com.ehensin.tunnel.client.profile.LocatorFactorType;
import com.ehensin.tunnel.client.profile.LocatorPolicy;

public class GradeHashLocator<T> extends Locator<T>{
	private List<T> collection;
    private int size = 0;
	private boolean isNeedSupportGrade = false;
	private Map<GradeEnum, List<T>> gradeMap;
    /**
     * construction
     * 
     * */
	public GradeHashLocator(LocatorPolicy p, LocatorAlgEnum alg,
			LocatorFactorType type, List<T> collection)  {
		super(p, alg, type);
		this.collection = collection;
    	if( collection != null )
    		this.size = collection.size();
    	this.collection = collection;
    	
    	gradeMap = new java.util.concurrent.ConcurrentHashMap<GradeEnum, List<T>>();
		if( collection != null ){
			for( T t : collection ){
				if( t instanceof IGradeSupport ){
					IGradeSupport gt = (IGradeSupport)t;
					List<T> gradeList = gradeMap.get(gt.getGrade());
					if( gradeList == null ){
						gradeList = new ArrayList<T>();
						gradeMap.put(gt.getGrade(), gradeList);
					}
					gradeList.add(t);
					isNeedSupportGrade = true;
				}else{
					/*don't need to support grade*/
					break;
				}
			}
		}
			
	}

	@Override
	protected T ablocate(LocatorFactor<?> factor) {
		if( this.size == 0 )
			return null;
		if( !isNeedSupportGrade ){
			return locateWithoutGrade(factor);
		}
		T t = locateNormal(factor);
		if( t == null )
			t = locateMedium(factor);
		if( t == null )
			t = locateLow(factor);
		return t;
		
	}

	@Override
	synchronized public void add(T t) {
		if( collection == null )
			collection = new ArrayList<T>();
		collection.add(t);
		this.size = collection.size();
		
		if( t != null && t instanceof IGradeSupport ){
			IGradeSupport gt = (IGradeSupport)t;
			this.refresh(gt.getGrade(), gt);
		}
	}

	@Override
	public void updateGrade(IGradeSupport t, GradeEnum oldGrade) {
		if( t != null ){
			this.refresh(oldGrade, t);
		}
	}
	
	
	private T locateWithoutGrade(LocatorFactor<?> factor){
		Integer hashCode = (java.lang.Integer)factor.getFactorObj();
		int index = hashCode % size;
		return ((List<T>)this.collection).get(index);
	}
	
	private T locateNormal(LocatorFactor<?> factor){
		List<T> gradeList = gradeMap.get(GradeEnum.Normal);
		if( gradeList == null || gradeList.size() <= 0 )
			return null;
		Integer hashCode = (java.lang.Integer)factor.getFactorObj();
		int index = hashCode % gradeList.size();
		return gradeList.get(index);
	}
	
	private T locateMedium(LocatorFactor<?> factor){
		List<T> gradeList = gradeMap.get(GradeEnum.Medium);
		if( gradeList == null || gradeList.size() <= 0 )
			return null;
		Integer hashCode = (java.lang.Integer)factor.getFactorObj();
		int index = hashCode % gradeList.size();
		return gradeList.get(index);
	}
	
	private T locateLow(LocatorFactor<?> factor){
		List<T> gradeList = gradeMap.get(GradeEnum.Low);
		if( gradeList == null || gradeList.size() <= 0 )
			return null;
		Integer hashCode = (java.lang.Integer)factor.getFactorObj();
		int index = hashCode % gradeList.size();
		return gradeList.get(index);
	}

	/**
	 * update grade map
	 * */
	@SuppressWarnings("unchecked")
	synchronized private void refresh(GradeEnum old, IGradeSupport g){
		List<T> gradeList = gradeMap.get(old);
		if(gradeList != null ){
			gradeList.remove(g);
		}
		List<T> newList = gradeMap.get(g.getGrade());
		if( newList == null ){
			newList = new ArrayList<T>();
			gradeMap.put(g.getGrade(), newList);
		}
		newList.add((T)g);
	}

}
