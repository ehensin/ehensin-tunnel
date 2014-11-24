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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ehensin.tunnel.client.portion.PortionFactory;
import com.ehensin.tunnel.client.profile.TunnelClientProfile;
import com.ehensin.tunnel.client.session.SessionFactory;

/**
 * tunnel client manager to init client
 * 
 * */
public class TunnelClientManager {
	private static final Logger logger = LoggerFactory
			.getLogger(TunnelClientManager.class);
	private final static String defaultProfileLocation = "/TunnelClientProfile.xml";
	private TunnelClientProfile clientProfile;
	
    public TunnelClientManager(TunnelClientProfile tcp){
		this.clientProfile = tcp;
		init();
	}
	public TunnelClientManager(){
		this(defaultProfileLocation, null);
	}
	
	public TunnelClientManager(String xmlLocation, Map<String, String> initParam){
		/*load client profile*/
		clientProfile = loadProfile(xmlLocation, initParam);
		init();
	}
	
	public SessionFactory getSessionFactory(){
		return SessionFactory.getFactory();
	}
	
	private void init(){
		/*Initialize session and portion factory */
		SessionFactory.getFactory();
		PortionFactory.getFactory().init(this.clientProfile.getPortions());
		/*initialize coordinate*/
		
	}
	private TunnelClientProfile loadProfile(String xmlLocation, Map<String, String> initParam){
		try {
			InputStream in = TunnelClientManager.class.getResourceAsStream(xmlLocation);
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
			context = JAXBContext.newInstance(TunnelClientProfile.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			return (TunnelClientProfile)unmarshaller.unmarshal(new StringReader(sb.toString()));
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
	
	

}
