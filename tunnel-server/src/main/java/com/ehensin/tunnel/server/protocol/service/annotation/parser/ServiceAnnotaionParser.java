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
package com.ehensin.tunnel.server.protocol.service.annotation.parser;

import com.ehensin.tunnel.server.protocol.service.annotation.Service;
import com.ehensin.tunnel.server.protocol.service.rpc.ServiceInvoker;

/**

 * 
 **/
public class ServiceAnnotaionParser implements IAnnotationParser<ServiceInvoker> {
	@Override
	public ServiceInvoker parse(Class<?> c) throws Exception {
		ServiceInvoker invoker = null; 
		Service s = c.getAnnotation(Service.class);
		if( s != null ){
			String serviceName = s.name();
			invoker = new ServiceInvoker(serviceName, c);
		}
		return invoker;
	}

}
