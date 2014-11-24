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
package com.ehensin.tunnel.server.profile;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *server profile sax bean class 
 * 
 * */
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "profile")
public class ServerProfile {
    @XmlAttribute(name="id")
    private long id;
    @XmlAttribute(name="name")
    private String name;
    
    @XmlElement(name="portion")
    private PortionProfile portion;
    
    @XmlElement(name="listener")
    private ListenerProfile listener;
    
    @XmlElement(name="filters")
    private FiltersProfile filters;
    
    @XmlElement(name="white-list")
    private WhiteListProfile whiteList;
    
    @XmlElement(name="codecs")
    private CodecsProfile codecs;
    
    @XmlElement(name="protocol")
    private ProtocolProfile protocol;

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

	public PortionProfile getPortion() {
		return portion;
	}

	public void setPortion(PortionProfile portion) {
		this.portion = portion;
	}

	public ListenerProfile getListener() {
		return listener;
	}

	public void setListener(ListenerProfile listener) {
		this.listener = listener;
	}

	public FiltersProfile getFilters() {
		return filters;
	}

	public void setFilters(FiltersProfile filters) {
		this.filters = filters;
	}

	public WhiteListProfile getWhiteList() {
		return whiteList;
	}

	public void setWhiteList(WhiteListProfile whiteList) {
		this.whiteList = whiteList;
	}

	public CodecsProfile getCodecs() {
		return codecs;
	}

	public void setCodecs(CodecsProfile codecs) {
		this.codecs = codecs;
	}

	public ProtocolProfile getProtocol() {
		return protocol;
	}

	public void setProtocol(ProtocolProfile protocol) {
		this.protocol = protocol;
	}

    
}
