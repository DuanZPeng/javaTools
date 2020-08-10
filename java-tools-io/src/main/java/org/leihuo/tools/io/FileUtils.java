package org.leihuo.tools.io;

import java.io.*;

/**
 * 读写文件工具类
 * FileUtils
 * @创建人 段志鹏
 * @创建时间 2018年5月21日 上午9:12:43
 */
public class FileUtils {
	public static byte[] readFile(String filename) throws IOException {
		File file = new File(filename);

		return readFile(file);
	}

	public static byte[] readFile(File file) throws IOException {
		byte[] data;
		if (!file.isFile()) {
			return null;
		}
		long len = file.length();
		data = new byte[(int) len];
		FileInputStream fin = null;
		try {
			fin = new FileInputStream(file);
			int r = fin.read(data);
			if ((long) r != len) {
				throw new IOException("Only read " + r + " of " + len + " for " + file);
			}
		} finally {
			if (fin != null) {
				fin.close();
			}
		}
		return data;
	}

	public static byte[] readInfo(InputStream in, int len) throws IOException {
		byte[] data;
		if (in == null) {
			return null;
		}

		data = new byte[len];

		int readBytes = 0;
		while (readBytes < len) {
			int read = in.read(data, readBytes, len - readBytes);
			if (read == -1) {
				break;
			}
			readBytes += read;
		}

		return data;
	}

	public static byte[] readInfo(InputStream in) throws IOException {
		if (in == null) {
			return null;
		}
		ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
		byte[] buff = new byte[100]; //buff用于存放循环读取的临时数据
		int rc = 0;
		while ((rc = in.read(buff, 0, 100)) > 0) {
			swapStream.write(buff, 0, rc);
		}
		return swapStream.toByteArray();
	}

	/*
	 *按行读取文件
	 */
	public static String readFileByLine(String filePath) throws Exception {
		File file = null;
		BufferedReader reader = null;
		try {
			file = new File(filePath);
			if (!file.isFile()) {
				return null;
			}
			reader = new BufferedReader(new FileReader(file));

			StringBuffer buffer = new StringBuffer();
			String strtmp = reader.readLine();
			while (strtmp != null) {

				buffer.append(strtmp);
				strtmp = null;
				strtmp = reader.readLine();
			}
			reader.close();
			return buffer.toString();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	/**
	 * 写文件
	 */
	public static void writeFileByLines(String fileName, String info, String bm) throws Exception {

		File file = new File(fileName);
		writeFileByLines(file, false, info, bm);

	}

	/**
	 *
	 * 方法功能:字符串方式写文件
	 * @param file
	 * @param isappend
	 * @param info
	 * @param bm
	 * @创建人 段志鹏
	 * @创建时间 2018年5月21日 上午9:08:20
	 */
	public static void writeFileByLines(File file, boolean isappend, String info, String bm) throws Exception {
		FileOutputStream fos = null;
		OutputStreamWriter write = null;
		BufferedWriter writer = null;
		try {

			fos = new FileOutputStream(file, isappend);
			if (bm == null) {
				write = new OutputStreamWriter(fos);
			} else {
				write = new OutputStreamWriter(fos, bm);
			}

			writer = new BufferedWriter(write);
			writer.write(info);

			writer.close();
			write.close();
			fos.close();

		} finally {

			if (writer != null) {
				writer.close();
			}
			if (write != null) {
				write.close();
			}
			if (fos != null) {
				fos.close();
			}
		}

	}

	/**
	 * 写文件
	 */
	public static void writeFileByLines(File file, byte[] bytes) throws Exception {

		writeFileByLines(file, false, bytes);

	}

	/**
	 *
	 * 方法功能:是否以追加方式写文件
	 * @param file
	 * @param isappend
	 * @param bytes
	 * @throws Exception
	 * @创建人 段志鹏
	 * @创建时间 2018年5月21日 上午9:03:07
	 */
	public static void writeFileByLines(File file, boolean isappend, byte[] bytes) throws Exception {

		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		try {
			fos = new FileOutputStream(file, isappend);
			bos = new BufferedOutputStream(fos);

			bos.write(bytes);
			bos.flush();

		} finally {

			bos.close();
			fos.close();
		}

	}

	public static void writeFileByLines(String fileName, byte[] bytes) throws Exception {
		File file = new File(fileName);
		writeFileByLines(file, false, bytes);
	}

}
