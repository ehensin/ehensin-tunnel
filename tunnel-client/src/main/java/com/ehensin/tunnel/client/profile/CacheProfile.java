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
@XmlRootElement(name = "cache")
public class CacheProfile {
	@XmlAttribute(name="switch")
	private String swich;
	@XmlElement(name = "reader")
	private CacheReader reader;
	@XmlElement(name = "writer")
	private CacheWriter writer;
	public String getSwich() {
		return swich;
	}
	public void setSwich(String swich) {
		this.swich = swich;
	}
	public CacheReader getReader() {
		return reader;
	}
	public void setReader(CacheReader reader) {
		this.reader = reader;
	}
	public CacheWriter getWriter() {
		return writer;
	}
	public void setWriter(CacheWriter writer) {
		this.writer = writer;
	}
	
	
}
