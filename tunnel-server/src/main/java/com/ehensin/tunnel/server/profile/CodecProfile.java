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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "codec")
public class CodecProfile {
	@XmlAttribute(name="msg_format")
	 private String msgFormat;
	@XmlAttribute(name="codec_class")
	 private String codecClass;
	public String getMsgFormat() {
		return msgFormat;
	}
	public void setMsgFormat(String msgFormat) {
		this.msgFormat = msgFormat;
	}
	public String getCodecClass() {
		return codecClass;
	}
	public void setCodecClass(String codecClass) {
		this.codecClass = codecClass;
	}
	
}
