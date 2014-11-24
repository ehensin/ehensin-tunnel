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


@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "locator")
public class LocatorPolicy {
	@XmlAttribute(name="name")
	private String name;
	@XmlElement(name="locator-alg")
	private LocatorAlg locatorAlg;
	@XmlElement(name="factor-type")
	private LocatorFactorType factorType;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public LocatorAlg getLocatorAlg() {
		return locatorAlg;
	}
	public void setLocatorAlg(LocatorAlg locatorAlg) {
		this.locatorAlg = locatorAlg;
	}
	public LocatorFactorType getFactorType() {
		return factorType;
	}
	public void setFactorType(LocatorFactorType factorType) {
		this.factorType = factorType;
	}
	
	
}
