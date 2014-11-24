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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import com.ehensin.tunnel.client.util.LogUtil;

public class EhensinFileReader implements ICheckPoint{
    private RandomAccessFile file;
    private RandomAccessFile checkPointF;
    private long offset = 0;
    private String fileName;
    private String checkPointFileName;
	public EhensinFileReader(File f, String checkPointFile) {
		if( f == null || f.isDirectory() )
			throw new IllegalArgumentException(" invalide file ");
		try {
			
			if( checkPointFile == null || checkPointFile.trim().equals("") || !(new File(checkPointFile)).exists()){
				/*创建checkpoint文件*/
				checkPointFileName = f.getAbsolutePath();
				checkPointFileName = checkPointFileName.substring(0, checkPointFileName.lastIndexOf(".")) + ".checkpoint";
				File checkPoint = new File(checkPointFileName);
				checkPoint.createNewFile();
				checkPointF = new RandomAccessFile(checkPoint, "rw");
				checkPointF.writeLong(0l);
			}else{
				checkPointFileName = checkPointFile;
				File checkPoint = new File(checkPointFile);
				checkPointF = new RandomAccessFile(checkPoint, "rw");
				offset = checkPointF.readLong();
				LogUtil.info(EhensinFileReader.class, "init reader with checkpoint: {0} ,offset:{1}  ", new Object[]{checkPointFile, offset});
			}
            file = new RandomAccessFile(f, "r");
			if( offset > 0 )
			    file.seek(offset);
			this.fileName = f.getAbsolutePath();
		} catch (FileNotFoundException e) {
			LogUtil.error(EhensinFileReader.class, "Failed to read file .", e);
		} catch (IOException e) {
			LogUtil.error(EhensinFileReader.class, "Failed to locat, offset " + offset, e);
			e.printStackTrace();
		}
	}
    public String getFileName(){
    	return fileName;
    }
	public final String readLine() throws IOException {
		List<Byte> bytes = new ArrayList<Byte>();
		byte c = -1;
		boolean eol = false;

		while (!eol) {
			switch (c = (byte) file.read()) {
			case -1:
			case '\n':
				eol = true;
				break;
			case '\r':
				eol = true;
				long cur = file.getFilePointer();
				if ((file.read()) != '\n') {
					file.seek(cur);
				}
				break;
			default:
				bytes.add(c);
				break;
			}
		}

		if ((c == -1) && (bytes.size() == 0)) {
			return null;
		}
		byte[] bs = new byte[bytes.size()];
		for (int i = 0; i < bytes.size(); i++) {
			bs[i] = bytes.get(i);
		}
		offset = file.getFilePointer();
		String result = new String(bs, "utf-8");
		return result;
	}
	
	public List<String> readLines(int start, int max){
		if( start < 1 || max < 1 ){
			throw new IllegalArgumentException(" start must > 0, max > 0");
		}
		/*reset to start of file*/
		try {
			file.seek(0);
			int startLine = 1;
			/*locate to start*/
			boolean isValidate = true;
			while( startLine < start ){
				String line = this.readLine();
				if( line == null ){
					isValidate = false;
					break;
				}
				startLine++;
			}
			if( isValidate ){
				List<String> lines = new ArrayList<String>(max);
				
				int index = 0;
				String line = null;
				while( index < max && (line = this.readLine()) != null ){
				    lines.add(line);
				    index++;
				}
				return lines;
			}
			
		} catch (IOException e) {
			LogUtil.error(EhensinFileReader.class, "failed to locat to start of file", e);
		}
		return null;
	}
	
	public List<String> search(List<String> condition, int start, int size){
		if( start < 1 || size < 1 || condition == null || condition.size() <= 0){
			throw new IllegalArgumentException(" start must > 0, max > 0, condition must not be empty");
		}
		/*reset to start of file*/
		try {
			file.seek(0);
			int machedLineNum = 0;
			List<String> machedLines = new ArrayList<String>();
			int endLine = start + size;
			while( true ){
				String line = this.readLine();
				if( line != null ){
					boolean result = checkLine(condition, line);
					if( result ){
						machedLineNum++;
						if( machedLineNum < start ){
						    continue;	
						}else{
							if( machedLineNum < endLine )
							    machedLines.add(line);
							else
								break;
						}
					}	
				}else
					break;
			}
			return machedLines;
			
			
		} catch (IOException e) {
			LogUtil.error(EhensinFileReader.class, "failed to locat to start of file", e);
		}
		return null;
	}

	public void reset() {
		try {
			file.seek(0);
		} catch (Exception e) {
			LogUtil.error(EhensinFileReader.class, "Failed to reset", e);
		}
	}
	
	public void close(){
		try {
			file.close();
			checkPointF.close();
		} catch (Exception e) {
			LogUtil.error(EhensinFileReader.class, "Failed to reset", e);
		}
	}
	
	private boolean checkLine(List<String> condition, String line){
		if( condition == null || line == null )
			return false;
		boolean result = true;
		for(String c : condition){
			result = result && line.indexOf(c) > -1;
			if( !result )
				break;
		}
		return result;
	}
    /*check point code*/
	@Override
	public long readCheckPoint() {
		return offset;
	}

	@Override
	public void storeCheckPoint() {
		try {
			checkPointF.seek(0);
			checkPointF.writeLong(offset);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	public void rmCheckPoint(){
		(new File(checkPointFileName)).delete();
	}
}
