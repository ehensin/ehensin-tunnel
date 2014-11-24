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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import com.ehensin.tunnel.client.util.LogUtil;

/**

 * 按时间周期进行切分的文件处理
 * 

 **/
public class RollingTimeFileAppender implements IFileAppender{
	// The code assumes that the following constants are in a increasing
	// sequence.
	public static final int TOP_OF_TROUBLE = -1;
	public static final int TOP_OF_SECOND = 0;
	public static final int TOP_OF_MINUTE = 1;
	public static final int TOP_OF_HOUR = 2;
	public static final int HALF_DAY = 3;
	public static final int TOP_OF_DAY = 4;
	public static final int TOP_OF_WEEK = 5;
	public static final int TOP_OF_MONTH = 6;

	private String fileDir;
	private Date now = new Date();
	private String fileExt;
	/* move log file to backup path */
	private String backupPath;
	/* old file name */
	private String oldFileName;
	/* need to backup file */
	private boolean needBackup;

	LsmpRollingCalendar rc = new LsmpRollingCalendar();
	/**
	 * The next time we estimate a rollover should occur.
	 */
	private long nextCheck = System.currentTimeMillis() - 1;
	private int rollNum;
	private EhensinFileWriter writer;
	private String fileNamePrefix;
	/* 得到IP地址 */
	public String getIp() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			LogUtil.error(RollingTimeFileAppender.class, e.getMessage(), e);
		}
		return "127.0.0.1";
	}
	
	public RollingTimeFileAppender(String dir, String extName, int rollType,
			int rollNum, String backupPath, boolean needBackup)
			throws IOException {
		this.rollNum = rollNum;
		rc.setType(rollType);
		if (dir == null || dir.trim().isEmpty()) {
			dir = System.getProperty("user.dir");
		}
		fileDir = dir + System.getProperty("file.separator") ;
		fileExt = extName;
		this.backupPath = backupPath;
		this.needBackup = needBackup;
		this.fileNamePrefix = fileDir + "log-" + this.getIp() + "-";
	}

	/**
	 * <p>
	 * Sets and <i>opens</i> the file where the log output will go. The
	 * specified file must be writable.
	 * 
	 * <p>
	 * If there was already an opened file, then the previous file is closed
	 * first.
	 * 
	 * <p>
	 * <b>Do not use this method directly. To configure a FileAppender or one of
	 * its subclasses, set its properties one by one and then call
	 * activateOptions.</b>
	 * 
	 * @param fileName
	 *            The path to the log file.
	 */
	public synchronized void setFile(String fileName) throws IOException {
		if( LogUtil.isDebugEnable() )
			LogUtil.debug(RollingTimeFileAppender.class,"setFile called: " + fileName, null);
		if (writer != null)
			writer.close();
		writer = new EhensinFileWriter(new File(fileName), -1, false);		
	}

	public void append(String data) throws IOException {
		long n = System.currentTimeMillis();
		if (n >= nextCheck) {
			now.setTime(n);
			nextCheck = rc.getNextCheckMillis(now);
			try {
				if (this.writer != null)
					this.writer.write("end");
				rollOver();
			} catch (IOException ioe) {
				if (ioe instanceof InterruptedIOException) {
					Thread.currentThread().interrupt();
				}
				throw ioe;
			}
		}
		if (data == null)
			return;

		/* 写文件 */
		if (writer != null) {
			try {
				writer.write(data);
			} catch (IOException e) {
				if (e instanceof InterruptedIOException) {
					Thread.currentThread().interrupt();
				}
				LogUtil.error(RollingTimeFileAppender.class,"write date failed:" + data, e);
				throw e;
			}

		}
	}

	private void rollOver() throws IOException {
		String datedFilename = fileNamePrefix + System.currentTimeMillis() + "." + fileExt;
		File target = new File(datedFilename);
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
			LogUtil.error(RollingTimeFileAppender.class, "setFile(" + datedFilename
					+ ", true) call failed.",e);
		}
	}

	public void close() throws IOException {
		if (writer != null){
				writer.end();
				writer.close();
				if (backupPath != null && this.needBackup) {
					if (oldFileName != null) {
						LogUtil.info(RollingTimeFileAppender.class,"move old file " + oldFileName
								+ " to  : " + backupPath, null);
						FileUtil.moveFile(backupPath, new File(oldFileName));
					}
				}
		}
	}

	/**
	 * RollingCalendar is a helper class to DailyRollingFileAppender. Given a
	 * periodicity type and the current time, it computes the start of the next
	 * interval.
	 * */
	class LsmpRollingCalendar extends GregorianCalendar {
		private static final long serialVersionUID = -3560331770601814177L;

		int type = RollingTimeFileAppender.TOP_OF_TROUBLE;

		LsmpRollingCalendar() {
			super();
		}

		LsmpRollingCalendar(TimeZone tz, Locale locale) {
			super(tz, locale);
		}

		void setType(int type) {
			this.type = type;
		}

		public long getNextCheckMillis(Date now) {
			return getNextCheckDate(now).getTime();
		}

		public Date getNextCheckDate(Date now) {
			this.setTime(now);

			switch (type) {
			case RollingTimeFileAppender.TOP_OF_SECOND:
				this.set(Calendar.MILLISECOND, 0);
				this.add(Calendar.SECOND, rollNum);
				break;
			case RollingTimeFileAppender.TOP_OF_MINUTE:
				this.set(Calendar.SECOND, 0);
				this.set(Calendar.MILLISECOND, 0);
				this.add(Calendar.MINUTE, rollNum);
				break;
			case RollingTimeFileAppender.TOP_OF_HOUR:
				this.set(Calendar.MINUTE, 0);
				this.set(Calendar.SECOND, 0);
				this.set(Calendar.MILLISECOND, 0);
				this.add(Calendar.HOUR_OF_DAY, rollNum);
				break;
			case RollingTimeFileAppender.HALF_DAY:
				this.set(Calendar.MINUTE, 0);
				this.set(Calendar.SECOND, 0);
				this.set(Calendar.MILLISECOND, 0);
				int hour = get(Calendar.HOUR_OF_DAY);
				if (hour < 12) {
					this.set(Calendar.HOUR_OF_DAY, 12);
				} else {
					this.set(Calendar.HOUR_OF_DAY, 0);
					this.add(Calendar.DAY_OF_MONTH, rollNum);
				}
				break;
			case RollingTimeFileAppender.TOP_OF_DAY:
				this.set(Calendar.HOUR_OF_DAY, 0);
				this.set(Calendar.MINUTE, 0);
				this.set(Calendar.SECOND, 0);
				this.set(Calendar.MILLISECOND, 0);
				this.add(Calendar.DATE, rollNum);
				break;
			case RollingTimeFileAppender.TOP_OF_WEEK:
				this.set(Calendar.DAY_OF_WEEK, getFirstDayOfWeek());
				this.set(Calendar.HOUR_OF_DAY, 0);
				this.set(Calendar.MINUTE, 0);
				this.set(Calendar.SECOND, 0);
				this.set(Calendar.MILLISECOND, 0);
				this.add(Calendar.WEEK_OF_YEAR, rollNum);
				break;
			case RollingTimeFileAppender.TOP_OF_MONTH:
				this.set(Calendar.DATE, 1);
				this.set(Calendar.HOUR_OF_DAY, 0);
				this.set(Calendar.MINUTE, 0);
				this.set(Calendar.SECOND, 0);
				this.set(Calendar.MILLISECOND, 0);
				this.add(Calendar.MONTH, rollNum);
				break;
			default:
				throw new IllegalStateException("Unknown periodicity type.");
			}
			return getTime();
		}
	}

}
