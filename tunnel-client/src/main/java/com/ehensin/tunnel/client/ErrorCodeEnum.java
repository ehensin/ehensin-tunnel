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
package com.ehensin.tunnel.client;

public enum ErrorCodeEnum {
	/*tunnel error */
	TunnelInterceptFailed("100001","tunnel invoke failed because intercepter preInvoke return false"),
	TunnelFlowControl("100002","flow control error"),
	TunnelChannelUnavailable("100003","channel is unavailable,cannot create a connection to server,or server close connection"),
	TunnelChannelInvalid("100004","channel is in invalid status"),
	TunnelChannelSendFailed("100005","send message to remote server failed"),
	TunnelChannelReceiveFailed("100006","receive message from remote server failed, socket timeout or others, receive failed will "
			+ "make client not know server status"),
			
	TunnelChannelHttpReponseEroor("100007","http server response error status"),
	/*codec error*/		
	CodecEncodeError("200001","cannot encode message to target type"),
	CodecDecodeError("200002","cannot decode message to target type"),
    ;
	
	private String value;
    private String des;
    private ErrorCodeEnum(String code, String des){
    	this.value = code;
    	this.des = des;
    }
    
    public String getCode(){
    	return value;
    }
    public String getDes(){
    	return this.des;
    }
    
    public static ErrorCodeEnum getErrorCodeEnum(String errorCode){
    	for (ErrorCodeEnum e : ErrorCodeEnum.values()){
    		if( e.getCode().equals(errorCode) )
    			return e;
    	}
    	return null;
    }
}
