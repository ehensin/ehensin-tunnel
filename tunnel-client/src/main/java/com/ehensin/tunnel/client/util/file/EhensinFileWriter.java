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
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

/**
 * 该类是确保内存与磁盘文件的同步
 * */
public class EhensinFileWriter {
	private RandomAccessFile raf;
	private FileChannel fc;
	private FileDescriptor fd;
	private long maxFileSize;

	public EhensinFileWriter(File file, long fileSize,
			boolean isAllocateFirst) throws IOException {
		raf = new RandomAccessFile(file, "rw");
		fc = raf.getChannel();
		fd = raf.getFD();
		maxFileSize = fileSize;
		if (isAllocateFirst && fileSize > 0) {
			raf.setLength(fileSize);
			raf.seek(0);
		}

	}

	public int write(String string) throws IOException {
		if (string != null) {
			byte[] utfstr = string.getBytes("utf-8");
			long pos = raf.getFilePointer();
			if (maxFileSize > 0 && (utfstr.length + pos) > maxFileSize) {
				end();
				return -1;
			}
			raf.write(utfstr);
			raf.write('\n');
		}
		return 1;
	}

	public void end() throws IOException {
		byte[] utfstr = "end".getBytes("utf-8");
		raf.write(utfstr);
		flush();
	}

	public void flush() throws IOException {
		fd.sync();
	}

	public void sync() throws IOException {
		fd.sync();
	}

	public void close() throws IOException {
		fd.sync();
		fc.close();
		raf.close();
	}

}
