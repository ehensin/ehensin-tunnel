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

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "writer")
public class CacheWriter {
	@XmlAttribute(name = "impl")
	private String implClass;
	@XmlAttribute(name = "attribute")
	private String attribute;
	@XmlElement(name = "param")
	List<Param> parames;
	
	@XmlTransient
	public String getImplClass() {
		return implClass;
	}
	public void setImplClass(String implClass) {
		this.implClass = implClass;
	}
	
	@XmlTransient
	public List<Param> getParames() {
		return parames;
	}
	public void setParames(List<Param> parames) {
		this.parames = parames;
	}
}
