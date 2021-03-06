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
package com.ehensin.tunnel.server.protocol.service.rpc;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ehensin.tunnel.server.ErrorCodeEnum;
import com.ehensin.tunnel.server.channel.codec.CodecException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class ServiceInvoker {
	private static Logger logger = LoggerFactory.getLogger(ServiceInvoker.class);
	private String serviceName;
	private Class<?> serviceClass;
	private Map<String, Method> methodMap;
	private Object service;
	/**
	 * when service class cannot be initialized, constructor will throw exception 
	 * */
    public ServiceInvoker(String serviceName, Class<?> serviceClass) throws Exception{
    	this.serviceName = serviceName;
		this.serviceClass = serviceClass;
		this.methodMap = new HashMap<String, Method>();
		
		Method[] methods = serviceClass.getMethods();
		
		service = this.serviceClass.newInstance();
		/*get all service function */
		if( methods != null && methods.length > 0 ){
			for(Method m : methods ){
				this.methodMap.put(m.getName(), m);
			}
		}
	}
    /**invoke a method from service
     * 
     * @return json format result 
     * */
	public String invoke(String funcName, String[] params) throws Exception{		
		Method m = methodMap.get(funcName);
		if( m == null )
			throw new Exception("cannot find function with this function name : " + funcName);
		Type[] types =(Type[]) m.getGenericParameterTypes();
		/*get method parameters */
		//Class<?>[] types = m.getParameterTypes();
		Object[] callParams = null;
		if( types != null && params != null && types.length > 0  && types.length == params.length ){
			callParams = new Object[params.length];
			for(int i = 0; i < params.length; i++){
				String param = params[i];
				ObjectMapper mapper = new ObjectMapper();
				try {
					if(types[i] instanceof ParameterizedType){
						ParameterizedType t = (ParameterizedType)types[i];
						Type[] ts = t.getActualTypeArguments();
						Class<?>[] jts = new Class[ts.length];
						for(int j = 0 ; j < ts.length; j++ ){
							jts[j] = (Class<?>)ts[j];
					    }
						JavaType jt = mapper.getTypeFactory().constructParametricType((Class<?>)t.getRawType(), jts);
						callParams[i] = mapper.readValue(param, jt);
					}else										
					    callParams[i] = mapper.readValue(param, TypeFactory.rawClass(types[i]));
				}catch (IOException e) {
					logger.error("cannot parser parameters :{}",param + " " + types[i], e);
					throw new CodecException("cannot parser parameters", e, ErrorCodeEnum.CodecDecodeError.getCode());
				}
			}
			
		}
		if( service != null ){
			String jsonFormatResult = null;
			Object obj = m.invoke(service, callParams);
			ObjectMapper mapper = new ObjectMapper();
			try {
				jsonFormatResult = mapper.writeValueAsString(obj);
			}catch (IOException e) {
				logger.error("cannot serialize return value :{}",obj , e);
				throw new CodecException("cannot serialize return value: " + obj, e, ErrorCodeEnum.CodecDecodeError.getCode());
			}
			return jsonFormatResult;
		}
    	return null;
    }

	public String getServiceName() {
		return serviceName;
	}
	
	public JavaType getCollectionType(ObjectMapper mapper, Class<?> collectionClass, Class<?>... elementClasses) {   
	    return mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);   
	} 
}
