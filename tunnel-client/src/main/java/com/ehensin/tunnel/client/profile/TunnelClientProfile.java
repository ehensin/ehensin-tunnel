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
package com.ehensin.tunnel.client.profile;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *client profile class 
 * 
 * */
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "profile")
public class TunnelClientProfile {
    @XmlAttribute(name="id")
    private long id;
    @XmlAttribute(name="name")
    private String name;
    
    @XmlElement(name="portions")
    private PortionsProfile portions;
    
    
    @XmlElement(name="cache")
    private CacheProfile cache;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PortionsProfile getPortions() {
		return portions;
	}

	public void setPortions(PortionsProfile portions) {
		this.portions = portions;
	}


	public CacheProfile getCache() {
		return cache;
	}

	public void setCache(CacheProfile cache) {
		this.cache = cache;
	}
    
}
