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

import com.ehensin.tunnel.server.protocal.message.MsgProtocol;
/**
 * codec interface to transfer request into style which satisfy to communication requires
 * 
 * */
public interface ICodec<T>{
	/**encode MsgReqProtocol message to target type*/
    public T encode(MsgProtocol msg) throws CodecException; 
    /**decode target type message to MsgRepProtocol response*/
    public MsgProtocol decode(T msg) throws CodecException; 
}