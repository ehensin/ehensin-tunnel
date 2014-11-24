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
package com.ehensin.tunnel.server.channel.codec;

import java.io.IOException;

import com.ehensin.tunnel.server.ErrorCodeEnum;
import com.ehensin.tunnel.server.protocal.message.MsgProtocol;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonCodec implements ICodec<String>{

	@Override
	public String encode(MsgProtocol msg) throws CodecException {
		String result ;
		ObjectMapper mapper = new ObjectMapper();
		try {
			result = mapper.writeValueAsString(msg);
		} catch (JsonProcessingException e) {
			throw new CodecException("cannot parser", e, ErrorCodeEnum.CodecEncodeError.getCode());
		}
		return result;
	}

	@Override
	public MsgProtocol decode(String msg) throws CodecException {
		if( msg == null )
			return null;
		MsgProtocol result = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			result = mapper.readValue(msg, MsgProtocol.class);
			JsonNode node = mapper.readTree(msg);
			JsonNode bodyNode = node.get("body");
			result.setBody(bodyNode.toString());
		} catch (IOException e) {
			throw new CodecException("cannot parser", e, ErrorCodeEnum.CodecDecodeError.getCode());
		}
		return result;
	}

}
