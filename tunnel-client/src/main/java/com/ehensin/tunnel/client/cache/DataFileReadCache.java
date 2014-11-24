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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.ehensin.tunnel.client.util.LogUtil;
import com.ehensin.tunnel.client.util.file.EhensinFileReader;
import com.ehensin.tunnel.client.util.file.FileScan;
import com.ehensin.tunnel.client.util.file.FileUtil;

/**
 * <pre>
 * CLASS:
 * 同步数据，通过文件进行缓存，读取文件操作类.
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
public class DataFileReadCache extends DataFileCache implements
		Runnable {
	/* 文件扫描器 */
	private FileScan scan;
	/* 当前正在进行的文件读取 */
	private List<EhensinFileReader> readers;
	private Map<String, EhensinFileReader> readersMap;
	/* 读取文件的数据缓存队列 */
	private BlockingQueue<String> dataCache;
	/* 同时允许多少个读文件存在 */
	private int readThreadSize = 0;
    /*备份路径*/
	private String backup = null;
	public void initForRead(Map<String, String> params) {
		LogUtil.debug("init SyncDataFileCache for read", null);
		readers = new ArrayList<EhensinFileReader>();
		readersMap = new HashMap<String, EhensinFileReader>();
		dataCache = new LinkedBlockingQueue<String>();
		String dir = params.get("scandir");
		backup = params.get("backup");
		String extName = params.get("ext") == null ? "sync" : params.get("ext");
		readThreadSize = params.get("readthreadsize") == null ? 1 : Integer
				.valueOf(params.get("readthreadsize"));

		/* 初始化所有有checkpoint的读文件线程，启动读操作 */
		List<File> files = FileUtil.retrieveFiles(dir);
		Iterator<File> it = files.iterator();
		/* 过滤文件: 过滤后缀，过滤是否已经被放到候选列表 */
		LogUtil.debug("init SyncDataFileCache, load history file", null);
		while (it.hasNext()) {
			File file = it.next();
			if (file.getName().lastIndexOf("." + "checkpoint") <= 0) {
				it.remove();
				continue;
			}			
			/* 获取同步文件名 */
			LogUtil.debug("get a checkpoint file " + file.getName(), null);
			String syncFileName = file.getAbsolutePath().substring(0,
					file.getAbsolutePath().lastIndexOf(".checkpoint"))
					+"." + extName;
			/* 创建读 */
			LogUtil.debug("creat a reader for file " + syncFileName, null);
			EhensinFileReader reader = new EhensinFileReader(new File(syncFileName),
					file.getAbsolutePath());
			readers.add(reader);
			readersMap.put(file.getName(), reader);
		}
		/* 初始化scan */
		LogUtil.debug("init file scan,dir {0} ext {1}", new String[]{dir,extName});
		scan = new FileScan(dir, extName, 1, 1);
		Thread t = new Thread(scan);
		t.start();
		/*等待一下scan初始化*/
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
		}
		
		/* 如果未有未处理完的文件，可以触发scan获取文件 */
		if (readers.size() < readThreadSize) {
			List<File> candidateFiles = scan.getFiles(readThreadSize
					- readers.size());
			for (File file : candidateFiles) {
				if (readersMap.get(file.getName()) == null) {
					/* 创建读 */
					EhensinFileReader reader = new EhensinFileReader(file, null);
					readers.add(reader);
					readersMap.put(file.getName(), reader);
				}
			}
		}
		/*启动读线程处理*/
		Thread readThread = new Thread(this);
		readThread.start();
	}

	@Override
	public List<String> read(int size) {
		List<String> readItems = new ArrayList<String>(size);
		dataCache.drainTo(readItems, size);
		return readItems;
	}

	@Override
	public void checkpoint() {
		for (EhensinFileReader reader : readers) {
			reader.storeCheckPoint();
		}
	}

	@Override
	public void run() {
		while (true) {
			if (readers.size() <= 0){
				File file = scan.getFile();
				if (file != null && readersMap.get(file.getName()) == null) {
					/* 创建读 */
					LogUtil.debug("creat a reader for file" + file.getName(), null);
					EhensinFileReader reader = new EhensinFileReader(file, null);
					readers.add(reader);
					readersMap.put(file.getName(), reader);
				}else{
					try {
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e) {
					}
					continue;
				}
			}
			EhensinFileReader reader = readers.get(0);
			String line = null;
			try {
				while ((line = reader.readLine()) != null) {
					line = line.trim();
					if (line.equalsIgnoreCase("end") || line.isEmpty()) {
						/* 文件结束处理 */
						LogUtil.debug("close reader for handled file " + reader.getFileName(), null);
						readers.remove(reader);
						readersMap.remove(reader.getFileName());
						reader.close();
						backup(new File(reader.getFileName()));
						/*删除checkpoint文件*/
						reader.rmCheckPoint();
						scan.remove(reader.getFileName());
						break;
					}
					dataCache.put(line);
				}
				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
				}
			} catch (IOException e) {
				LogUtil.error(DataFileReadCache.class, "read file error : "
						+ reader.getFileName(), e);
			} catch (InterruptedException e) {
				LogUtil.error(DataFileReadCache.class,
						"put info to queue error : " + line, e);
			}
		}
	}
	
	private void backup(File file){
		/* move file */
		String targetPath = backup;
		LogUtil.debug(DataFileReadCache.class,
				"move old file: {0}, to: {1}.",
				new Object[] { file.getName(), targetPath });
		boolean moveDone = false;
		while (!moveDone) {
			try {
				FileUtil.moveFile(targetPath, file);
				moveDone = true;
			} catch (IOException e) {
				LogUtil.error(DataFileReadCache.class,
						e.getMessage(), e);
				try {
					TimeUnit.SECONDS.sleep(10);
				} catch (InterruptedException e1) {
					LogUtil.error(DataFileReadCache.class,
							e1.getMessage(), e1);
				}
			}
		}
	}
	
}
