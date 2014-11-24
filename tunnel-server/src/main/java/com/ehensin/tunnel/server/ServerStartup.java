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
package com.ehensin.tunnel.server;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ehensin.tunnel.server.listener.IServerListener;
import com.ehensin.tunnel.server.listener.WhiteListController;
import com.ehensin.tunnel.server.profile.ListenerProfile;
import com.ehensin.tunnel.server.profile.Param;
import com.ehensin.tunnel.server.profile.ServerProfile;
import com.ehensin.tunnel.server.protocol.service.rpc.ServiceRegistry;

public class ServerStartup {
	private static final Logger logger = LoggerFactory
			.getLogger(ServerStartup.class);
	private static final ServerStartup startup = new ServerStartup();
	private final static String defaultProfileLocation = "/TunnelServerProfile.xml";
	private ServerProfile profile;
    private ServerStartup(){
    	/**/
    }
    public void start(){
    	profile = loadProfile(defaultProfileLocation, null);
    	/*init listener*/
    	ListenerProfile lp = profile.getListener();
    	if( lp == null || lp.getClazz() == null || lp.getClazz().trim().isEmpty() ){
    		logger.error("no listener information");
    		System.exit(1);
    	}else{
    		String listenerClass = lp.getClazz();
    		try {
				IServerListener listener = (IServerListener)Class.forName(listenerClass).newInstance();
				List<Param> params = lp.getParams();
				Map<String, String> pMap = new HashMap<String, String>();
				if( params != null && params.size() > 0 ){
					for(Param p : params )
						pMap.put(p.getName(), p.getValue());
				}
				listener.init(pMap);
				listener.start();
			} catch (Exception e) {
				logger.error("cannot init server listener", e);
	    		System.exit(1);
			}
    	} 
    	/*init service registry*/
    	ServiceRegistry.getRegistry();
    }
    
    public static ServerStartup getStartup(){
    	return startup;
    }
    public ServerProfile getProfile(){
    	return this.profile;
    }

	private ServerProfile loadProfile(String xmlLocation, Map<String, String> initParam){
		try {
			InputStream in = ServerStartup.class.getResourceAsStream(xmlLocation);
			String xml = inputStreamTOString(in);
			StringBuffer sb = new StringBuffer();
			if( initParam != null && initParam.size() > 0){
				Pattern p = Pattern.compile("\\$\\{.*?\\}");
				Matcher matcher = p.matcher(xml);
				while(matcher.find()){
					String param = matcher.group();
					String key = param.replace("${", "").replace("}", "");
					if(initParam.get(key) != null)
					   matcher.appendReplacement(sb, initParam.get(key));
				}
				matcher.appendTail(sb);
			}else{
				sb.append(xml);
			}
	        /*parser*/
			JAXBContext context;
			context = JAXBContext.newInstance(ServerProfile.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			return (ServerProfile)unmarshaller.unmarshal(new StringReader(sb.toString()));
		} catch (Exception e) {
			throw new IllegalArgumentException("cannot load profile : " + xmlLocation + ", please check profile file", e);
		}
	}
	private String inputStreamTOString(InputStream in) throws Exception {

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] data = new byte[4096];
		int count = -1;
		while ((count = in.read(data, 0, 4096)) != -1)
			outStream.write(data, 0, count);

		data = null;
		return new String(outStream.toByteArray(), "UTF-8");
	}
	
	
	
	public static void main(String[] args){
    	ServerStartup.getStartup().start();	
    }
}
