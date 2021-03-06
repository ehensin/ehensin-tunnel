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
package com.ehensin.tunnel.server.channel;

import com.ehensin.tunnel.server.event.EventTypeEnum;
import com.ehensin.tunnel.server.event.ITunnelEvent;

public class ChannelClosedEvent implements ITunnelEvent{
	IServerChannel channel;
	public ChannelClosedEvent(IServerChannel channel){
		this.channel = channel;
	}

	@Override
	public EventTypeEnum getEventType() {
		return EventTypeEnum.CHANNEL_CLOSED;
	}

	@Override
	public Object getEventObject() {
		return this.channel;
	}

}
