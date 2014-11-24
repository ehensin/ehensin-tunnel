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
package com.ehensin.tunnel.client.protocol;

public enum MsgType {
	PLAINTEXT(0,"plain text remote call"),
    RPC(1,"remote procedure call"),
    KEEPALIVE(2, "keep alive message for keeping long connection with remote server"),
    TRACE(3,"trace app status ");
    
    private int value;
    private String desc;
    
    private MsgType(int value, String desc){
    	this.value = value;
    	this.desc = desc;
    }

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
    
	public String toString(){
		return Integer.toString(this.value);
	}
    
}
