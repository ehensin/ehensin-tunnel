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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import com.ehensin.tunnel.client.util.LogUtil;

/**
 * <pre>
 * CLASS:
 * 文件读取并调用相应处理器.
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
public class FileScan implements Runnable {
	
    /*获取文件的目录*/
	String path;
    /*过滤条件，以后缀名为过滤条件*/
	String extName;
	/*扫描风格,随机扫描，还是顺序扫描，顺序扫描需要排序规则*/
	int scanStyle;
	/*排序规则,在扫描方式为顺序扫描，才起作用*/
	int orderBy;
	/*满足条件的后选文件*/
	TreeSet<String> candidateFiles;

	public FileScan(String path, String extName, int scanStyle, int orderBy) {
		this.path = path;
		this.extName = extName;
		this.scanStyle = scanStyle;
		this.orderBy = orderBy;
		candidateFiles = new TreeSet<String>(new Comparator<String>(){
			@Override
			public int compare(String o1, String o2) {
				// TODO Auto-generated method stub
				//log-192.168.11.26-1381481059186.sync
				Timestamp t1 = new Timestamp(Long.valueOf(o1.substring(o1.lastIndexOf("-") + 1, o1.lastIndexOf("."))));
				Timestamp t2 = new Timestamp(Long.valueOf(o2.substring(o2.lastIndexOf("-") + 1, o2.lastIndexOf("."))));
				return t1.compareTo(t2);
			}
			
		});
	}

	synchronized public File getFile(){
		if( candidateFiles.size() <= 0 )
			return null;
		String first = candidateFiles.first();
		return new File(first);
	}
	synchronized public List<File> getFiles(int fetchSize){
		List<File> files = new ArrayList<File>(fetchSize);
		Iterator<String> it = candidateFiles.iterator();
		int count = 0;
		while(it.hasNext()){
			files.add(new File(it.next()));
			count++;
			if( count == fetchSize )
				break;
		}
		
		return files;
	}
	synchronized public void remove(String fileName){
		candidateFiles.remove(fileName);
	}
	@Override
	public void run() {
		while (true) {
			List<File> files = FileUtil.retrieveFiles(path);
			if (files == null || files.size() <= 0) {
				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					LogUtil.error(FileScan.class, e.getMessage(), e);
				}
				continue;
			}
			Iterator<File> it = files.iterator();
			/*过滤文件: 过滤后缀，过滤是否已经被放到候选列表*/
			while (it.hasNext()) {
				File file = it.next();
				if (candidateFiles.contains(file.getAbsolutePath()) || file.getAbsolutePath().lastIndexOf("." + extName) <= 0 )
					it.remove();
				else
					candidateFiles.add(file.getAbsolutePath());
			}
			if (files.size() == 0) {
				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					LogUtil.error(FileScan.class, e.getMessage(), e);
				}
				continue;
			

		}
	  }
	}
}

