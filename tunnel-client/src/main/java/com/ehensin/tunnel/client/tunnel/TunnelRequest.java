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

import java.io.Serializable;

import com.ehensin.tunnel.client.locator.LocatorFactor;
import com.ehensin.tunnel.client.protocol.MsgProtocol;

/**
 * 
 **/
@SuppressWarnings("serial")
public class TunnelRequest implements Serializable {
	/* request  */
	private MsgProtocol msgProtocal;
	private Boolean isSyncCall;
	private LocatorFactor<?> channelLocatorFactor;

	public TunnelRequest( MsgProtocol msgProtocal, LocatorFactor<?> locatorFactor) {
		this.msgProtocal = msgProtocal;
		this.channelLocatorFactor = locatorFactor;
	}

	public MsgProtocol getMsgProtocal() {
		return msgProtocal;
	}

	public void setMsgProtocal(MsgProtocol msgProtocal) {
		this.msgProtocal = msgProtocal;
	}

	public LocatorFactor<?> getChannelLocatorFactor() {
		return channelLocatorFactor;
	}

	public void setChannelLocatorFactor(LocatorFactor<?> channelLocatorFactor) {
		this.channelLocatorFactor = channelLocatorFactor;
	}

	public Boolean getIsSyncCall() {
		return isSyncCall;
	}

	public void setIsSyncCall(Boolean isSyncCall) {
		this.isSyncCall = isSyncCall;
	}

    

}
