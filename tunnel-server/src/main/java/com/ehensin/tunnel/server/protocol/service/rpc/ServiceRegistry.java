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

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.net.www.protocol.file.FileURLConnection;

import com.ehensin.tunnel.server.ServerStartup;
import com.ehensin.tunnel.server.profile.ProtocolProfile;
import com.ehensin.tunnel.server.profile.RPCProfile;
import com.ehensin.tunnel.server.protocol.service.annotation.parser.ServiceAnnotaionParser;

/**
 * register all rpc service which system provide
 * */
public class ServiceRegistry {
	private static Logger logger = LoggerFactory.getLogger(ServiceRegistry.class);
    private static final ServiceRegistry registry = new ServiceRegistry(); 
    private ServiceAnnotaionParser parser;
    private Map<String, ServiceInvoker> invokerMap;
    /*private constructor to avoid create a new registry from outside*/
    private ServiceRegistry(){
    	parser = new ServiceAnnotaionParser();
    	invokerMap = new HashMap<String, ServiceInvoker>();
    	/*scan all service annotation class and retrieve service*/
    	ProtocolProfile pp = ServerStartup.getStartup().getProfile().getProtocol();
    	RPCProfile rpcp = pp.getRpc();
    	String[] scanPackages = (rpcp.getScanPackages() == null ? null : rpcp.getScanPackages().split(";"));
    	if( scanPackages != null ){    		
	    	for(int i = 0; i < scanPackages.length; i++ ){
	    		String pckgName = scanPackages[i];
	    		try {
					ArrayList<Class<?>> classes = this.getClassesForPackage(pckgName);
					if( classes != null && classes.size() > 0 ){
						for(Class<?> c : classes){
							if( logger.isInfoEnabled() ){
								logger.info("parse class : {} " , c);
							}
							try {
								ServiceInvoker si = parser.parse(c);
								if( si != null )
								    invokerMap.put(si.getServiceName(), si);
								else{
									if( logger.isInfoEnabled() ){
										logger.info("class is not a validate service class : {} " , c);
									}
								}
							} catch (Exception e) {
								logger.error("parse class failed : {}", c, e);
							}
						}
					}
					
				} catch (ClassNotFoundException e) {
					logger.error("load class failed", e);
				}
	    	}
    	}

    }
	public static ServiceRegistry getRegistry() {
		return registry;
	}

	public ServiceInvoker getService(String serviceName){
		return invokerMap.get(serviceName);
	}
	
	public List<ServiceInvoker> getServiceInvokers(){
		return new ArrayList<ServiceInvoker>(invokerMap.values());
	}
	
	/**
	 * Attempts to list all the classes in the specified package as determined
	 * by the context class loader
	 * 
	 * @param packageName
	 *            the package name to search
	 * @return a list of classes that exist within that package
	 * @throws ClassNotFoundException
	 *             if something went wrong
	 */
	public ArrayList<Class<?>> getClassesForPackage(String packageName) throws ClassNotFoundException{
		if( logger.isInfoEnabled() ){
			logger.info("scan service class package : {} ", packageName);
		}
	    final ArrayList<Class<?>> classes = new ArrayList<Class<?>>();

	    try {
	        final ClassLoader cld = Thread.currentThread()
	                .getContextClassLoader();

	        if (cld == null)
	            throw new ClassNotFoundException("Can't get class loader.");

	        final Enumeration<URL> resources = cld.getResources(packageName
	                .replace('.', '/'));
	        URLConnection connection;

	        for (URL url = null; resources.hasMoreElements()
	                && ((url = resources.nextElement()) != null);) {
	            try {
	                connection = url.openConnection();

	                if (connection instanceof JarURLConnection) {
	                    checkJarFile((JarURLConnection) connection, packageName,
	                            classes);
	                } else if (connection instanceof FileURLConnection) {
	                    try {
	                        checkDirectory(
	                                new File(URLDecoder.decode(url.getPath(),
	                                        "UTF-8")), packageName, classes);
	                    } catch (final UnsupportedEncodingException ex) {
	                        throw new ClassNotFoundException(
	                        		packageName
	                                        + " does not appear to be a valid package (Unsupported encoding)",
	                                ex);
	                    }
	                } else
	                    throw new ClassNotFoundException(packageName + " ("
	                            + url.getPath()
	                            + ") does not appear to be a valid package");
	            } catch (final IOException ioex) {
	                throw new ClassNotFoundException(
	                        "IOException was thrown when trying to get all resources for "
	                                + packageName, ioex);
	            }
	        }
	    } catch (final NullPointerException ex) {
	        throw new ClassNotFoundException(
	        		packageName
	                        + " does not appear to be a valid package (Null pointer exception)",
	                ex);
	    } catch (final IOException ioex) {
	        throw new ClassNotFoundException(
	                "IOException was thrown when trying to get all resources for "
	                        + packageName, ioex);
	    }

	    return classes;
	}
	
	/**
	 * Private helper method.
	 * 
	 * @param connection
	 *            the connection to the jar
	 * @param pckgname
	 *            the package name to search for
	 * @param classes
	 *            the current ArrayList of all classes. This method will simply
	 *            add new classes.
	 * @throws ClassNotFoundException
	 *             if a file isn't loaded but still is in the jar file
	 * @throws IOException
	 *             if it can't correctly read from the jar file.
	 */
	private void checkJarFile(JarURLConnection connection,
	        String pckgname, ArrayList<Class<?>> classes)
	        throws ClassNotFoundException, IOException {
	    final JarFile jarFile = connection.getJarFile();
	    final Enumeration<JarEntry> entries = jarFile.entries();
	    String name;

	    for (JarEntry jarEntry = null; entries.hasMoreElements()
	            && ((jarEntry = entries.nextElement()) != null);) {
	        name = jarEntry.getName();

	        if (name.contains(".class")) {
	            name = name.substring(0, name.length() - 6).replace('/', '.');

	            if (name.contains(pckgname)) {
	                classes.add(Class.forName(name));
	            }
	        }
	    }
	}
	
	/**
	 * Private helper method
	 * 
	 * @param directory
	 *            The directory to start with
	 * @param pckgname
	 *            The package name to search for. Will be needed for getting the
	 *            Class object.
	 * @param classes
	 *            if a file isn't loaded but still is in the directory
	 * @throws ClassNotFoundException
	 */
	private void checkDirectory(File directory, String pckgname,
	        ArrayList<Class<?>> classes) throws ClassNotFoundException {
	    File tmpDirectory;

	    if (directory.exists() && directory.isDirectory()) {
	        final String[] files = directory.list();

	        for (final String file : files) {
	            if (file.endsWith(".class")) {
	                try {
	                    classes.add(Class.forName(pckgname + '.'
	                            + file.substring(0, file.length() - 6)));
	                } catch (final NoClassDefFoundError e) {
	                    // do nothing. this class hasn't been found by the
	                    // loader, and we don't care.
	                }
	            } else if ((tmpDirectory = new File(directory, file))
	                    .isDirectory()) {
	                checkDirectory(tmpDirectory, pckgname + "." + file, classes);
	            }
	        }
	    }
	}
    
}
