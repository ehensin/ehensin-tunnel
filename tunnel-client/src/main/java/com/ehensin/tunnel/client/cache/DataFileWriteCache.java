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
package com.ehensin.tunnel.client.cache;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import com.ehensin.tunnel.client.util.LogUtil;
import com.ehensin.tunnel.client.util.file.FileUtil;
import com.ehensin.tunnel.client.util.file.IFileAppender;
import com.ehensin.tunnel.client.util.file.RollingFixedFileSizeAppender;
import com.ehensin.tunnel.client.util.file.RollingTimeFileAppender;

/**
 * <pre>
 * CLASS:
 * Describe class, extends and implements relationships to other classes.
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
public class DataFileWriteCache extends DataFileCache {
	private IFileAppender appender;
	private String fileDir;
	private String fileExt;

	public void initForWrite(Map<String, String> params) {
		String rollType = params.get("rolltype");
		fileExt = params.get("ext");
		fileDir = params.get("dir");
		try {
			if (rollType.equals("fixsize")) {
				appender = new RollingFixedFileSizeAppender(params.get("dir"),
						params.get("ext"), params.get("backup"), params
								.get("backup") != null,
						params.get("filesize") == null ? -1 : Integer
								.valueOf(params.get("filesize")), null);
			} else if (rollType.equals("fixtime")) {
				appender = new RollingTimeFileAppender(params.get("dir"),
						params.get("ext"),
						RollingTimeFileAppender.TOP_OF_MINUTE, 30, params
								.get("backup"), params.get("backup") != null);
			} else {
				appender = new RollingFixedFileSizeAppender(params.get("dir"),
						params.get("ext"), params.get("backup"), params
								.get("backup") != null,
						params.get("filesize") == null ? -1 : Integer
								.valueOf(params.get("filesize")), null);
			}
		} catch (NumberFormatException e) {
		} catch (IOException e) {
			LogUtil.error(DataFileCache.class, e.getMessage(), e);
		}

		/* 关闭所有未关闭的文件 */
		/* 确保所有的文件都有end结束符 */
		try {
			List<File> files = FileUtil.retrieveFiles(fileDir.toString());
			if (files != null && files.size() > 0) {
				for (File file : files) {
					if (file.getName().lastIndexOf("." + fileExt) >= 0) {
						Writer fw = new BufferedWriter(new OutputStreamWriter(
								new FileOutputStream(file, true)));
						fw.append('\n');
						fw.append('e');
						fw.append('n');
						fw.append('d');
						fw.flush();
						fw.close();
					}
				}

			}
		} catch (FileNotFoundException ex) {
		} catch (IOException e) {
		}
	}

	@Override
	public void store(Object obj) throws Exception {
		this.appender.append((String) obj);
	}
}
