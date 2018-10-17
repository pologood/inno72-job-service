package com.inno72.job.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class FileUtil {

	public static boolean deleteRecursively(File root) {
		if (root != null && root.exists()) {
			if (root.isDirectory()) {
				File[] children = root.listFiles();
				if (children != null) {
					for (File child : children) {
						deleteRecursively(child);
					}
				}
			}
			return root.delete();
		}
		return false;
	}

	public static List<File> getFiles(String path) {
		List<File> files = new ArrayList<File>();
		File file = new File(path);
		File[] tempList = file.listFiles();

		for (int i = 0; i < tempList.length; i++) {
			if (tempList[i].isFile()) {
				files.add(tempList[i]);
			}
		}
		return files;
	}

	public static String readToString(File file) {
		String encoding = "UTF-8";
		Long filelength = file.length();
		byte[] filecontent = new byte[filelength.intValue()];
		try {
			FileInputStream in = new FileInputStream(file);
			in.read(filecontent);
			in.close();
		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
		try {
			return new String(filecontent, encoding);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}


	public void deleteDirectory(File dirFile) throws IOException {

		if (!dirFile.exists()) {
			return;
		}

		if (!dirFile.exists() || !dirFile.isDirectory()) {
			throw new IOException(dirFile.getName() + "文件夹不存在");
		}

		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {

			if (files[i].isFile()) {
				deleteFile(files[i]);
			} else {
				deleteDirectory(files[i]);
			}
		}

		if (!dirFile.delete()) {
			throw new IOException(dirFile.getName() + "文件夹删除失败");
		}
	}

	public static void deleteFile(File file) {
		if (file.isFile() && file.exists()) {
			file.delete();
		}
	}

	public static Object processByFileLock(File file, Submittable runable) throws IOException {

		if (!file.exists()) {
			file.createNewFile();
		}

		RandomAccessFile randomAccessFile = null;
		FileChannel channel = null;
		FileLock lock = null;
		try {
			
			randomAccessFile = new RandomAccessFile(file, "rw");
			channel = randomAccessFile.getChannel();
			lock = channel.lock();
			if(runable != null) {
				return runable.submit();
			}
		}finally {
			if (lock != null) {
				lock.release();
			}

			if (channel != null) {
                channel.close();
            }
			
			if (randomAccessFile != null ) {
                randomAccessFile.close();
            }
		}

		return null;
	}
	
	private final static String[] strDigits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d",
			"e", "f" };

	

	public static String GetMD5Code(byte[] bByte) {
		String resultString = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");

			resultString = byteToString(md.digest(bByte));

		} catch (NoSuchAlgorithmException ex) {
			
		}
		return resultString;
	}

	private static String byteToString(byte[] bByte) {
		StringBuffer sBuffer = new StringBuffer();
		for (int i = 0; i < bByte.length; i++) {
			sBuffer.append(byteToArrayString(bByte[i]));
		}
		return sBuffer.toString();
	}

	private static String byteToArrayString(byte bByte) {
		int iRet = bByte;
		// System.out.println("iRet="+iRet);
		if (iRet < 0) {
			iRet += 256;
		}
		int iD1 = iRet / 16;
		int iD2 = iRet % 16;
		return strDigits[iD1] + strDigits[iD2];
	}
	
	public static String getUuid() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	public static boolean deleteFile(String path) {
		try {
			return new File(path).delete();
		}catch(Exception e) {
			return false;
		}
	}
}
