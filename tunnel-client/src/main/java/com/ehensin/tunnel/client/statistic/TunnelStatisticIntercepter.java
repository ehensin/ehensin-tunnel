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
package com.ehensin.tunnel.client.statistic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.atomic.AtomicLong;

import com.ehensin.tunnel.client.tunnel.ITunnelIntercepter;
import com.ehensin.tunnel.client.tunnel.TunnelRequest;
/**
 * tunnel statistic, there are five quota:
 * whole invoking count
 * latest day invoking count
 * latest 1 hour invoking count
 * */
public class TunnelStatisticIntercepter extends Observable implements ITunnelIntercepter{
	Calendar day = Calendar.getInstance();
	Calendar hour = Calendar.getInstance();
	List<StatisticItem<AtomicLong>> statistiItems = new ArrayList<StatisticItem<AtomicLong>>();
	/**
     * total invoking count
     * */
	StatisticItem<AtomicLong> totalStatistic;
    /**
     * latest day invoking count
     * */
	StatisticItem<AtomicLong> dayStatistic;
	
	/**
     * latest 1 hour invoking count
     * */
	StatisticItem<AtomicLong> hourStatistic;
	
	public TunnelStatisticIntercepter(String targetName){
		this.addObserver(StatisticCenter.getInstance());
		totalStatistic = new StatisticItem<AtomicLong>("tunnel", targetName, "total");
	    dayStatistic = new StatisticItem<AtomicLong>("tunnel", targetName, "day");
		hourStatistic = new StatisticItem<AtomicLong>("tunnel",targetName, "one hour");
		totalStatistic.setValue(new AtomicLong(0));
		dayStatistic.setValue(new AtomicLong(0));
		hourStatistic.setValue(new AtomicLong(0));
		
		statistiItems.add(totalStatistic);
		statistiItems.add(dayStatistic);
		statistiItems.add(hourStatistic);
	}
	@Override
	public boolean preInvoke(TunnelRequest req) {
		return true;
	}

	@Override
	public void postInvoke() {
		totalStatistic.getValue().incrementAndGet();
		/*update day */
		Calendar now = Calendar.getInstance();
		if( now.get(Calendar.DAY_OF_YEAR) != day.get(Calendar.DAY_OF_YEAR)) {
			dayStatistic.getValue().set(0);
			day = now;
		}
		dayStatistic.getValue().incrementAndGet();
		if( (now.get(Calendar.DAY_OF_YEAR) != hour.get(Calendar.DAY_OF_YEAR)) || (now.get(Calendar.HOUR_OF_DAY) != hour.get(Calendar.HOUR_OF_DAY))) {
			hourStatistic.getValue().set(0);
			hour = now;
		}
		hourStatistic.getValue().incrementAndGet();
		this.setChanged();
		this.notifyObservers(statistiItems);
	}

	@Override
	public void exceptionCaught(Exception e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String getIntercepterName() {
		return "tunnel intercepter";
	}
	
	
	
	public StatisticItem<AtomicLong> getTotalStatistic() {
		return totalStatistic;
	}

	public StatisticItem<AtomicLong> getDayStatistic() {
		return dayStatistic;
	}

	public StatisticItem<AtomicLong> getHourStatistic() {
		return hourStatistic;
	}
	
	

}
