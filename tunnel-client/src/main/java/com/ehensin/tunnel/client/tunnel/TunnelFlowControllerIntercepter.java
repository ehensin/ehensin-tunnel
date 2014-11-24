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
package com.ehensin.tunnel.client.tunnel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * flow controller is objective to control number of concurrent access to server.
 * when average number of call is more than max tps, system will refuse to invoke.
 * system will compute average number of call every six seconds
 * */
public class TunnelFlowControllerIntercepter implements ITunnelIntercepter {
	private static final Logger logger = LoggerFactory
			.getLogger(TunnelFlowControllerIntercepter.class);
    private long maxtps;
    private long averageTps = 0;
    private long lastCount = 0;
    private long currentCount = 0;
    private long lastTime = System.currentTimeMillis();
	public TunnelFlowControllerIntercepter(long maxtps){
		this.maxtps = maxtps;
	}
	@Override
	public boolean preInvoke(TunnelRequest req) {
		long now = System.currentTimeMillis();
		if( ( now - lastTime ) >= 6000){
			lastTime = now;
			averageTps = ( currentCount - lastCount )/ 6 ;
			lastCount = currentCount;
		}
		if(averageTps > maxtps){
			if( logger.isDebugEnabled() )
				logger.debug("{} average tps more than max tps {}", averageTps, maxtps);
			return false;
		}
		currentCount++;
		return true;
	}

	@Override
	public void postInvoke() {
		// TODO Auto-generated method stub

	}

	@Override
	public void exceptionCaught(Exception e) {
		// TODO Auto-generated method stub

	}
	@Override
	public String getIntercepterName() {
		return "flow controller intercepter";
	}
	public long getAverageTps() {
		return averageTps;
	}
	
	
}
