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

/**
 * only round robin support grade mode
 * */
public class RRLocator<T> extends Locator<T>{
	private List<T> collection;
	private Map<GradeEnum, List<T>> gradeMap;
	private int index = 0;
	private int normalIndex = 0;
	private int mediumIndex = 0;
	private int lowIndex = 0;
	private boolean isNeedSupportGrade = false;
    /**
     * construction 
     * */
	public RRLocator(LocatorPolicy p, LocatorAlgEnum alg,
			LocatorFactorType type, List<T> collection)  {
		super(p, alg, type);	
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
	synchronized public void add(T t) {
		if( this.collection == null )
			this.collection = new ArrayList<T>();
		this.collection.add(t);
		if( t != null && t instanceof IGradeSupport ){
			IGradeSupport gt = (IGradeSupport)t;
			this.isNeedSupportGrade = true;
			this.refresh(gt.getGrade(), gt);
		}
		
	}

	@Override
	public void updateGrade(IGradeSupport t, GradeEnum oldGrade) {
		if( t != null ){
			this.refresh(oldGrade, t);
		}
	}

	@Override
	protected T ablocate(LocatorFactor<?> factor) {
		if( collection == null || collection.size() <= 0 )
			return null;
		if( !isNeedSupportGrade ){
			return locateWithoutGrade();
		}
		T t = locateNormal();
		if( t == null )
			t = locateMedium();
		if( t == null )
			t = locateLow();
		return t;
	}
	
	private T locateWithoutGrade(){
		List<T> gradeList = this.collection;
		if( gradeList == null || gradeList.size() <= 0 )
			return null;
		int i = index;
		if( i >= gradeList.size() )
			i = index = 0;
		index++;
		T t = null;
		try{
			t = gradeList.get(i);
		}catch(IndexOutOfBoundsException e){
			if( gradeList.size() > 0 ){
				i = index = (gradeList.size() - 1);
				t = gradeList.get(i);
			}
		}
		return t;
	}
	
	private T locateNormal(){
		List<T> gradeList = gradeMap.get(GradeEnum.Normal);
		if( gradeList == null || gradeList.size() <= 0 )
			return null;
		int i = normalIndex;
		if( i >= gradeList.size() )
			i = normalIndex = 0;
		normalIndex++;
		T t = null;
		try{
			t = gradeList.get(i);
		}catch(IndexOutOfBoundsException e){
			if( gradeList.size() > 0 ){
				i = normalIndex = (gradeList.size() - 1);
				t = gradeList.get(i);
			}
		}
		return t;
	}
	
	private T locateMedium(){
		List<T> gradeList = gradeMap.get(GradeEnum.Medium);
		
		if( gradeList == null || gradeList.size() <= 0 )
			return null;
		int i = mediumIndex;
		if( i >= gradeList.size() )
			i = mediumIndex = 0;
		mediumIndex++;
		T t = null;
		try{
			t = gradeList.get(i);
		}catch(IndexOutOfBoundsException e){
			if( gradeList.size() > 0 ){
				i = mediumIndex = (gradeList.size() - 1);
				t = gradeList.get(i);
			}
		}
		return t;
	}
	
	private T locateLow(){
		List<T> gradeList = gradeMap.get(GradeEnum.Low);
		
		if( gradeList == null || gradeList.size() <= 0 )
			return null;
		int i = lowIndex;
		if( i >= gradeList.size() )
			i = lowIndex = 0;
		lowIndex++;
		T t = null;
		try{
			t = gradeList.get(i);
		}catch(IndexOutOfBoundsException e){
			if( gradeList.size() > 0 ){
				i = lowIndex = (gradeList.size() - 1);
				t = gradeList.get(i);
			}
		}
		return t;
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
