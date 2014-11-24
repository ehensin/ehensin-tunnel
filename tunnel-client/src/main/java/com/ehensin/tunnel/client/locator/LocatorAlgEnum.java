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

public enum LocatorAlgEnum {
    RR(1,"rr","round robin"),
    NAME(2,"name"," locate directory by name"),
    HASH(3,"hash","locate by hash algorithm");
    
    private int value;
    private String des;
    private String name;
    private LocatorAlgEnum(int type, String name, String des){
    	this.value = type;
    	this.name = name;
    	this.des = des;
    }
    
    public int getType(){
    	return value;
    }
    public String getDes(){
    	return this.des;
    }
    public String getName(){
    	return this.name;
    }
}
