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
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Description:
 * 
 * 
 * @author huanhaib
 * @version 1.0, 2009-9-13
 * @since 1.0
 */
public final class FileUtil {
	private FileUtil() {

	}

	public static File createFile(String dir, final String name)
			throws IOException {
		File pdir = new File(dir);
		if (!pdir.exists()) {
			pdir.mkdirs();
		}
		final File file = new File(pdir, name);
		if (!file.exists()) {
			file.createNewFile();
		}
		return file;
	}

	public static File createDir(final String dir) throws IOException {
		File pdir = new File(dir);
		if (!pdir.exists()) {
			pdir.mkdirs();
		}
		return pdir;
	}

	public static void moveFile(String dir, File file) throws IOException {
		if (file != null && file.exists()) {
			createDir(dir);
			// check the file is exists
			File target = new File(dir, file.getName());
			if (target.exists())
				target.delete();
			boolean result = file.renameTo(new File(dir, file.getName()));
			if (!result)
				throw new IOException("cannot rename this file");
		}
	}

	public static boolean tryMoveLockFile(String dir, File file)
			throws IOException {
		if (file != null && file.exists()) {
			/* check if file is locked */
			RandomAccessFile raf = new RandomAccessFile(file, "rw");
			FileLock lock = raf.getChannel().tryLock();
			if (lock == null) {
				System.out.println("file locked");
				raf.close();
				return false;
			}
			lock.release();
			raf.close();

			raf = null;

			createDir(dir);
			// check the file is exists
			File target = new File(dir, file.getName());
			if (target.exists())
				target.delete();
			return file.renameTo(new File(dir, file.getName()));
		}
		return false;
	}

	public static File moveFile(File dir, File file) throws IOException {
		if (dir != null && dir.exists() && file != null && file.exists()) {
			// check the file is exists
			File target = new File(dir, file.getName());
			if (target.exists())
				target.delete();
			File targetFile = new File(dir, file.getName());
			boolean result = file.renameTo(targetFile);
			if (!result)
				throw new IOException("cannot rename this file");
			return targetFile;
		}
		return null;
	}

	public static void moveFile(String tagetDir, String srcDir)
			throws IOException {
		List<File> files = FileUtil.retrieveFiles(srcDir);
		if (files != null && files.size() > 0) {
			createDir(tagetDir);
			// check the file is exists
			for (File file : files) {
				File target = new File(tagetDir, file.getName());
				if (target.exists())
					target.delete();
				file.renameTo(new File(tagetDir, file.getName()));
			}
		}
	}

	public static void moveFile(String dir, File file, String fileName)
			throws IOException {
		if (file != null && file.exists()) {
			createDir(dir);
			file.renameTo(new File(dir, fileName));
		}
	}

	public static File createTmpFile(String prefix, final String suffix)
			throws IOException {
		return File.createTempFile(prefix, suffix);
	}

	public static boolean writeALine(File file, boolean isNewLine, String line) {
		if (file == null)
			return false;
		if (!file.exists() || file.isDirectory())
			return false;
		// FileReader reader = new FileReader(file);
		OutputStreamWriter writer = null;
		try {
			writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
			if (isNewLine)
				writer.append('\n');
			writer.write(line);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null) {
					writer.flush();
					writer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return false;

	}

	public static boolean writeLines(File file, boolean isNewLine,
			List<StringBuffer> lines, boolean append) {
		if (file == null)
			return false;
		if (!file.exists() || file.isDirectory())
			return false;
		if (lines != null) {
			OutputStreamWriter writer = null;
			try {
				writer = new OutputStreamWriter(new FileOutputStream(file,
						append), "UTF-8");
				for (StringBuffer str : lines) {

					if (isNewLine)
						writer.append('\n');
					writer.write(str.toString());
				}
				return true;
			} catch (IOException e) {
				e.printStackTrace();

			} finally {
				try {
					if (writer != null) {
						writer.flush();
						writer.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return false;

	}

	/**
	 * Description: This method will return a list of files names contained in
	 * the past in directory. If the directory does not exist the method will
	 * return null.
	 * 
	 * @param paoDirName
	 *            - The directory in which to search for files.
	 * 
	 * @return a collection of files absolute path names
	 */
	public static List<String> retrieveFileNames(String paoDirName) {
		List<String> rtFileNames = null;
		File crtFile = new File(paoDirName);
		File[] files = crtFile.listFiles();

		if (files != null) {
			int numFiles = files.length;
			rtFileNames = new ArrayList<String>();

			for (int index = 0; index < numFiles; index++) {
				if (files[index].isFile()) {
					rtFileNames.add(files[index].getAbsolutePath());
				}
			}
		}

		return rtFileNames;
	}

	public static List<File> retrieveFiles(String paoDirName) {
		List<File> rtFiles = null;
		File crtFile = new File(paoDirName);
		File[] files = crtFile.listFiles();

		if (files != null) {
			int numFiles = files.length;
			rtFiles = new ArrayList<File>();

			for (int index = 0; index < numFiles; index++) {
				if (files[index].isFile()) {
					rtFiles.add(files[index]);
				}
			}
		}

		return rtFiles;
	}

	/**
	 * Description: This method will return to the user a list of directories
	 * within a path if the type specified is SUBDIRS. If the type specified is
	 * FILES then the method will return to the user a list of files within the
	 * specified path
	 * 
	 * @param paoType
	 *            : This is a values representing either SUBDIRS or FILES.
	 * 
	 * @param paoPathName
	 *            : This is the directory which will be searched.
	 * 
	 * @return An arraylist of names or null if no data is to be returned.
	 */
	public static List<String> retrieveList(int paoType, String paoPathName) {
		int SUBDIRS = 10;
		int FILES = 20;

		List<String> rtFileNames = new ArrayList<String>();
		File crtFile = new File(paoPathName);
		File[] files = crtFile.listFiles();

		if (files != null) {
			int numFiles = files.length;
			for (int i = 0; i < numFiles; i++) {
				if (paoType == SUBDIRS) { // We will pass back all
					// subdirectories within the path
					if (files[i].isDirectory()) {
						rtFileNames.add(files[i].getName());
					}
				} else if (paoType == FILES) { // We will pass back all files
					// within the path
					if (files[i].isFile()) {
						rtFileNames.add(files[i].getName());
					}
				}
			}
		}

		return rtFileNames;
	}

	/**
	 * Description: This method will return whether or not the file passed in
	 * exists in the specified directory.
	 * 
	 * @param paoFullPathName
	 *            - The filename including directory path being searched for.
	 * 
	 * @return a boolean stating whether or not the file exists.
	 */
	public static boolean doesFileExist(String paoFullPathName) {
		boolean rtVal = false;
		File crtFile = new File(paoFullPathName);

		if (crtFile.exists()) {
			rtVal = true;
		}

		return rtVal;
	}

	/**
	 * Description: This method will delete the existing file if possible.
	 * 
	 * @param paoFullPathName
	 *            - The filename including the directory path being used.
	 * 
	 * @return a boolean stating whether or not the file was deleted.
	 */
	public static boolean deleteFile(String paoFullPathName) {
		boolean rtVal = false;
		File crtFile = new File(paoFullPathName);

		try {
			rtVal = crtFile.delete();
		} catch (Exception err) {

		}

		return rtVal;
	}

	/**
	 * Description: This method will delete the existing folder if possible.
	 * 
	 * @param dir
	 *            - The folder path.
	 * 
	 * @return .
	 */
	static public void deleteDirectory(File dir) throws IOException {
		/*
		 * if dir is not exists , don't do anything
		 */
		if (!dir.exists())
			return;

		/*
		 * if the directory is not a valid directory, throw an IOExcepton
		 */
		if (!dir.isDirectory()) {
			/* dir is a file,delete the file */
			if (!dir.delete())
				throw new IOException("can not delete the file: " + dir);
			return;
		}
		/* using recursion to delete all things in the directory */
		String[] list = dir.list();
		if (list != null) {
			for (int i = 0; i < list.length; i++) {
				/* using recursion to delete every thing under the direcry */
				File subFile = new File(dir, list[i]);
				deleteDirectory(subFile);
			}
		}

		/* delete the directory itself */
		if (!dir.delete())
			throw new IOException("delete the dir failed:  " + dir);
	}

	public static void zipFiles(List filesList, String outFilePath)
			throws IOException {
		// Create a buffer for reading the files
		byte[] buf = new byte[1024];

		try {
			// Create the ZIP file
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
					outFilePath));

			// Compress the files
			for (Iterator fileIter = filesList.iterator(); fileIter.hasNext();) {
				File file = (File) fileIter.next();
				FileInputStream in = new FileInputStream(file);

				// Add ZIP entry to output stream.
				out.putNextEntry(new ZipEntry(file.getName()));

				// Transfer bytes from the file to the ZIP file
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}

				// Complete the entry
				out.closeEntry();
				in.close();
			}
			// Complete the ZIP file
			out.close();
		} catch (IOException e) {
			throw e;
		}
	}

	public static List<File> unzipFiles(FileInputStream file, String outFilePath)
			throws IOException {
		// Create a buffer for reading the files
		byte[] buf = new byte[1024];
		List<File> fileList = new ArrayList<File>();
		ZipInputStream in = null;
		FileOutputStream fos = null;
		try {
			// Create the ZIP file
			in = new ZipInputStream(file);
			// get entry
			ZipEntry entry;

			while ((entry = in.getNextEntry()) != null) {
				int BUFFER = 1024;

				if (!entry.isDirectory()) {
					String fileName = (String) entry.getName();
					String path = getRealPath(outFilePath)
							+ getRealFileName(fileName);
					fos = new FileOutputStream(path);
					BufferedOutputStream dest = new BufferedOutputStream(fos,
							BUFFER);
					int count = 0;
					while ((count = in.read(buf, 0, BUFFER)) != -1) {
						dest.write(buf, 0, count);
					}
					dest.flush();
					dest.close();
					File f = new File(path);
					fileList.add(f);
				}
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			if (in != null)
				in.close();
			if (fos != null)
				fos.close();
			throw e;
		} finally {
			if (in != null)
				in.close();
			if (fos != null)
				fos.close();
		}
		return fileList;
	}

	public static boolean moveto(String src, String backup, String fileName)
			throws IOException {
		boolean rtVal = false;
		byte[] buf = new byte[1024];
		int BUFFER = 1024;
		FileInputStream in = new FileInputStream(src);
		backup = getRealPath(backup);
		FileOutputStream fos = new FileOutputStream(backup + fileName);
		BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);
		int count = 0;
		while ((count = in.read(buf, 0, BUFFER)) != -1) {
			dest.write(buf, 0, count);
		}
		dest.flush();
		dest.close();
		in.close();
		/* 删除原文件 */
		File crtFile = new File(src);
		rtVal = crtFile.delete();

		return rtVal;
	}

	public static boolean copyto(String src, String backup, String fileName)
			throws IOException {
		boolean rtVal = false;
		byte[] buf = new byte[1024];
		int BUFFER = 1024;
		File newFile = new File(src);
		if( newFile.exists() == false ){
			newFile.createNewFile();
		}
		FileInputStream in = new FileInputStream(newFile);
		backup = getRealPath(backup);
		FileOutputStream fos = new FileOutputStream(backup + fileName);
		BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);
		int count = 0;
		while ((count = in.read(buf, 0, BUFFER)) != -1) {
			dest.write(buf, 0, count);
		}
		dest.flush();
		dest.close();
		in.close();

		return rtVal;
	}

	public static boolean copyTo(File src, String targetDir, String fileName)
			throws IOException {
		boolean rtVal = false;
		byte[] buf = new byte[1024];
		int BUFFER = 1024;
		FileInputStream in = new FileInputStream(src);
		File target;
		if (fileName != null && !fileName.trim().equals("")) {
			target = createFile(targetDir, fileName);
		} else {
			target = createFile(targetDir, src.getName());
		}
		FileOutputStream fos = new FileOutputStream(target);
		BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);
		int count = 0;
		while ((count = in.read(buf, 0, BUFFER)) != -1) {
			dest.write(buf, 0, count);
		}
		dest.flush();
		dest.close();
		in.close();
		rtVal = true;
		return rtVal;
	}

	public static String getRealPath(String path) {
		int index = path.lastIndexOf(System.getProperty("file.separator"));
		if (index == path.length())
			return path;
		return path + System.getProperty("file.separator");
	}

	public static String getRealFileName(String fileName) {
		int index = fileName.lastIndexOf("/");
		if (index > 0)
			fileName = fileName.substring(index);
		else {
			index = fileName.lastIndexOf("\\");
			if (index > 0)
				fileName = fileName.substring(index + 1);
		}
		return fileName;
	}

	public static InputStream getInputStream(String fileName) {
		InputStream in = FileUtil.class.getResourceAsStream(fileName);
		if (in == null) {
			File file = new File("metadata/" + fileName);
			try {
				in = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else
			return null;
		return in;
	}

}
