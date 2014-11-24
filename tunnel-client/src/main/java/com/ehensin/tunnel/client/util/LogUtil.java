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
package com.ehensin.tunnel.client.util;
import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * CLASS:
 * 日志工具类
 * 封装的日志工具,如果需要更换日志记录工具,无需修改前端代码,只需修改工具类即可.
 * 
 * RESPONSIBILITIES:
 * High level list of things that the class does
 * -) 
 * 
 * COLABORATORS:
 * List of descriptions of relationships with other classes, i.e. uses, contains, creates, calls...
 * -) class   relationship
 * -) class   relationship
 * 
 * USAGE:
 * Description of typical usage of class.  Include code samples.
 * 
 * 
 **/
@SuppressWarnings("rawtypes")
public final class LogUtil {

	// 默认的logger
	private static Logger defaultLogger = LoggerFactory
			.getLogger(LogUtil.class);

	private static ConcurrentHashMap<Class, Logger> loggerMap = new ConcurrentHashMap<Class, Logger>();

	private LogUtil() {
		// TODO: LogUtil Constructor
	}
    public static boolean isInfoEnable(){
    	return defaultLogger.isInfoEnabled();
    }
    public static boolean isDebugEnable(){
    	return defaultLogger.isDebugEnabled();
    }
	public static void info(String content, Object[] param) {
		info(defaultLogger, content, param);
	}

	public static void info(Class clazz, String content, Object[] param) {
		info(getLogger(clazz), content, param);
	}

	public static void debug(String content, Object[] param) {
		debug(defaultLogger, content, param);
	}

	public static void debug(Class clazz, String content, Object[] param) {
		debug(getLogger(clazz), content, param);
	}

	public static void error(String content, Object[] param) {
		error(defaultLogger, content, param);
	}

	public static void error(Class clazz, String content, Object[] param) {
		error(getLogger(clazz), content, param);
	}

	public static void error(Class clazz, String content, Throwable t) {
		error(getLogger(clazz), content, t);
	}

	public static void warn(String content, Object[] param) {
		warn(defaultLogger, content, param);
	}

	public static void warn(Class clazz, String content, Object[] param) {
		warn(getLogger(clazz), content, param);
	}

	/**
	 * 获取Logger
	 * 
	 * @param clazz
	 *            需要获取的类名
	 * @return Logger
	 */
	public static Logger getLogger(Class clazz) {
		if (null == clazz)
			return defaultLogger;
		if (!loggerMap.contains(clazz)) {
			loggerMap.put(clazz, LoggerFactory.getLogger(clazz));
		}
		return loggerMap.get(clazz);
	}

	private static void info(Logger logger, String content, Object[] param) {
		if (null != param && param.length > 0) {
			content = MessageFormat.format(content, param);
		}
		if (logger.isInfoEnabled())
			logger.info(content);
	}

	private static void debug(Logger logger, String content, Object[] param) {
		if (null != param && param.length > 0) {
			content = MessageFormat.format(content, param);
		}
		if (logger.isDebugEnabled())
			logger.debug(content);
	}

	private static void error(Logger logger, String content, Object[] param) {
		if (null != param && param.length > 0) {
			content = MessageFormat.format(content, param);
		}
		if (logger.isErrorEnabled())
			logger.error(content);
	}

	private static void error(Logger logger, String content, Throwable t) {
		if (logger.isErrorEnabled())
			logger.error(content, t);
	}

	private static void warn(Logger logger, String content, Object[] param) {
		if (null != param && param.length > 0) {
			content = MessageFormat.format(content, param);
		}
		if (logger.isWarnEnabled())
			logger.warn(content);
	}
}
