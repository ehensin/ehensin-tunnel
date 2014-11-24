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

import com.ehensin.tunnel.client.event.EventTypeEnum;
import com.ehensin.tunnel.client.event.ITunnelEvent;
import com.ehensin.tunnel.client.locator.GradeEnum;

public class TunnelGradeEvent implements ITunnelEvent{
    private GradeEnum grade; 
	public TunnelGradeEvent(GradeEnum grade){
		this.grade = grade;
	}
	@Override
	public EventTypeEnum getEventType() {
		return EventTypeEnum.TUNNAL_GRADE;
	}

	@Override
	public Object getEventObject() {
		return this.grade;
	}

}
