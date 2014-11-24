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
package com.ehensin.tunnel.client.util.file;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.ehensin.tunnel.client.util.LogUtil;

/**
 * <pre>
 * CLASS:
 * 按文件大小进行切分的文件处理
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
public class RollingFixedFileSizeAppender implements IFileAppender {
	/**
	 * 缺省文件大小500MB.
	 */
	private long maxFileSize = 500 * 1024 * 1024;

	private String fileDir;
	private String fileExt; // 后缀名
	/* move log file to backup path */
	private String backupPath;
	/* old file name */
	private String oldFileName;
	/* need to backup file */
	private boolean needBackup;
	private int seq = 0;

	private EhensinFileWriter writer;
	private String fileNamePrefix;
	private String fileNameSuffix;

	public String getIp() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			LogUtil
					.error(RollingFixedFileSizeAppender.class, e.getMessage(),
							e);
		}
		return "127.0.0.1";
	}

	public RollingFixedFileSizeAppender(String dir, String extName,
			String backupPath, boolean needBackup, long maxFileSize, 
			String fileNameSuffix)
			throws IOException {

		if (dir == null || dir.trim().isEmpty()) {
			dir = System.getProperty("user.dir");
		}
		fileDir = dir + System.getProperty("file.separator");
		fileExt = extName;
		this.backupPath = backupPath;
		this.needBackup = needBackup;
		if (maxFileSize > 0)
			this.maxFileSize = maxFileSize;

		this.fileNamePrefix = fileDir + "log-" + this.getIp() + "-";
		this.fileNameSuffix = fileNameSuffix == null ?"" : fileNameSuffix;
		this.rollOver();
	}

	/**
	 * 文件切换
	 */
	private void rollOver() throws IOException {
		seq++;
		// 文件名
		StringBuilder datedFilename = new StringBuilder(fileNamePrefix);
		datedFilename.append(System.currentTimeMillis()).append(this.fileNameSuffix).append(".").append(
				fileExt);
		if (LogUtil.isInfoEnable())
			LogUtil.info(RollingFixedFileSizeAppender.class,
					"audit file name : " + datedFilename.toString(), null);
		File target = new File(datedFilename.toString());
		if (target.exists()) {
			target.delete();
		} else {
			File dir = new File(fileDir);
			if (!dir.exists())
				dir.mkdirs();
		}
		try {
			// This will also close the file. This is OK since multiple
			// close operations are safe.
			try {
				if (backupPath != null && this.needBackup) {
					if (oldFileName != null) {
						LogUtil.info("move old file " + oldFileName + " to  : "
								+ backupPath, null);
						File sourceFile = new File(oldFileName);
						FileUtil.copyTo(sourceFile, backupPath, sourceFile
								.getName());
					}
				}
			} catch (Exception e) {
				LogUtil.error(RollingFixedFileSizeAppender.class,
						"backup file failed : " + datedFilename, e);
			}
			oldFileName = datedFilename.toString();
			this.setFile(datedFilename.toString());

		} catch (IOException e) {
			LogUtil.error(RollingFixedFileSizeAppender.class, "setFile("
					+ datedFilename + ", true) call failed.", e);
			throw e;
		}
	}

	public synchronized void setFile(String fileName) throws IOException {
		if (LogUtil.isDebugEnable())
			LogUtil.debug("setFile called: " + fileName, null);
		if (writer != null)
			writer.close();
		writer = new EhensinFileWriter(new File(fileName), maxFileSize, false);
		if (LogUtil.isDebugEnable())
			LogUtil.debug("setFile ended", null);
	}

	/**
	 * append data.
	 * 
	 */
	synchronized public void append(String data) throws IOException {
		if (data == null)
			return;
		/* 写文件 */
		if (writer != null) {
			int result;
			try {
				result = writer.write(data);
				if (result < 0) {
					/* 达到最大文件尺寸上限 */
					this.rollOver();
					writer.write(data);
				}
			} catch (IOException e) {
				if (e instanceof InterruptedIOException) {
					Thread.currentThread().interrupt();
				}
				LogUtil.error(RollingFixedFileSizeAppender.class, data, e);
				throw e;
			}

		}
	}

	/**
	 * 获取文件最大尺寸
	 * 
	 * 
	 */
	public long getMaximumFileSize() {
		return maxFileSize;
	}

	public void setMaximumFileSize(long maxFileSize) {
		this.maxFileSize = maxFileSize;
	}

	public void close() throws IOException {
		if (writer != null) {
			writer.end();
			writer.close();
			if (backupPath != null && this.needBackup) {
				if (oldFileName != null) {
					if (LogUtil.isInfoEnable())
						LogUtil.info("move old file " + oldFileName + " to  : "
								+ backupPath, null);
					FileUtil.moveFile(backupPath, new File(oldFileName));
				}
			}
		}
	}
}
