package com.lyp.net;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.util.Log;

public class HttpSession {

	public static final String TAG = "HttpSession";
	public final String baseUrlString = "";

	public static String getRequestResult(String actionUrl, Map<String, String> params)
			throws Exception {

		URL uri = new URL(actionUrl);
		HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
		conn.setReadTimeout(5 * 1000);
		conn.setDoInput(true);//
		conn.setDoOutput(true);//
		conn.setUseCaches(false);//
		conn.setRequestMethod("POST");// post
		conn.setRequestProperty("Charset", "UTF-8"); //

		StringBuffer sb = new StringBuffer();
		if (params != null) {

			for (Entry<String, String> e : params.entrySet()) {
				sb.append(e.getKey());
				sb.append("=");
				sb.append(e.getValue());
				sb.append("&");
			}
			sb.substring(0, sb.length() - 1);
		}
		// send
		DataOutputStream outStream = new DataOutputStream(
				conn.getOutputStream());
		outStream.write(sb.toString().getBytes());
		Log.i(TAG, sb.toString());
		// receive
		int rescode = conn.getResponseCode();
		Log.i(TAG, "rescode=" + rescode);
		InputStream in = null;
		if (rescode == 200) {// success
			in = conn.getInputStream();

			StringBuffer sBuffer = new StringBuffer();
			int len = 0;
			byte[] b = new byte[1024];
			byte[] temp = null;
			while ((len = in.read(b)) != -1) {
				temp = new byte[len];
				System.arraycopy(b, 0, temp, 0, len);

				sBuffer.append(new String(temp));
			}

			return sBuffer.toString();
		} else {
			return null;
		}

	}

	/**
	 * 
	 * @param url
	 *            Service net address
	 * @param params
	 *            text content
	 * @param files
	 *            pictures
	 * @return String result of Service response
	 * @throws IOException
	 */
	public static String getPostResult(String url, Map<String, String> params,
			Map<String, File> files) throws IOException {
		String BOUNDARY = java.util.UUID.randomUUID().toString();
		String PREFIX = "--", LINEND = "\r\n";
		String MULTIPART_FROM_DATA = "multipart/form-data";
		String CHARSET = "UTF-8";

		URL uri = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
		conn.setReadTimeout(10 * 1000);
		conn.setDoInput(true);//
		conn.setDoOutput(true);//
		conn.setUseCaches(false); //
		conn.setRequestMethod("POST");
		conn.setRequestProperty("connection", "keep-alive");
		conn.setRequestProperty("Charset", "UTF-8");
		conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
				+ ";boundary=" + BOUNDARY);

		//
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			sb.append(PREFIX);
			sb.append(BOUNDARY);
			sb.append(LINEND);
			sb.append("Content-Disposition: form-data; name=\""
					+ entry.getKey() + "\"" + LINEND);
			sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
			sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
			sb.append(LINEND);
			sb.append(entry.getValue());
			sb.append(LINEND);
		}

		DataOutputStream outStream = new DataOutputStream(
				conn.getOutputStream());
		outStream.write(sb.toString().getBytes());
		//
		if (files != null)
			for (Map.Entry<String, File> file : files.entrySet()) {

				StringBuilder sb1 = new StringBuilder();
				sb1.append(PREFIX);
				sb1.append(BOUNDARY);
				sb1.append(LINEND);
				// sb1.append("Content-Disposition: form-data; name=\""+file.getKey()+"\";filename=\""
				// + file.getValue().getName() + "\"" + LINEND);
				sb1.append("Content-Disposition: form-data; name=\""
						+ file.getKey() + "\";filename=\""
						+ file.getValue().getName() + "\"" + LINEND);
				sb1.append("Content-Type: application/octet-stream; charset="
						+ CHARSET + LINEND);
				sb1.append(LINEND);
				outStream.write(sb1.toString().getBytes());
				InputStream is = new FileInputStream(file.getValue());
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					outStream.write(buffer, 0, len);
				}
				is.close();
				outStream.write(LINEND.getBytes());
				Log.i(TAG, file.getKey() + " >>> " + file.getValue().getName()
						+ " >>> " + file.getValue());
			}

		byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
		outStream.write(end_data);
		outStream.flush();
		int res = conn.getResponseCode();
		Log.i(TAG, "res=" + res);
		InputStream in = conn.getInputStream();
		StringBuilder sb2 = new StringBuilder();
		if (res == 200) {
			int ch;
			while ((ch = in.read()) != -1) {
				sb2.append((char) ch);
			}
		}
		outStream.close();
		conn.disconnect();
		Log.i(TAG, "getPostResult complete sb=" + sb2.toString());
		return sb2.toString();
	}

	/**
	 * 
	 * @param url
	 *            Service net address
	 * @param params
	 *            text content
	 * @param files
	 *            pictures
	 * @return String result of Service response
	 * @throws IOException
	 */
	public static String getPostResults(String url, Map<String, String> params,
			Map<String, List<File>> fileLists) throws IOException {
		String BOUNDARY = java.util.UUID.randomUUID().toString();
		String PREFIX = "--", LINEND = "\r\n";
		String MULTIPART_FROM_DATA = "multipart/form-data";
		String CHARSET = "UTF-8";

		URL uri = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
		conn.setReadTimeout(10 * 1000);
		conn.setDoInput(true);//
		conn.setDoOutput(true);//
		conn.setUseCaches(false); //
		conn.setRequestMethod("POST");
		conn.setRequestProperty("connection", "keep-alive");
		conn.setRequestProperty("Charset", "UTF-8");
		conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
				+ ";boundary=" + BOUNDARY);

		//
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			sb.append(PREFIX);
			sb.append(BOUNDARY);
			sb.append(LINEND);
			sb.append("Content-Disposition: form-data; name=\""
					+ entry.getKey() + "\"" + LINEND);
			sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
			sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
			sb.append(LINEND);
			sb.append(entry.getValue());
			sb.append(LINEND);
		}

		DataOutputStream outStream = new DataOutputStream(
				conn.getOutputStream());
		outStream.write(sb.toString().getBytes());
		//
		if (fileLists != null) {
			for (Entry<String, List<File>> fileList : fileLists.entrySet()) {
				String key = fileList.getKey();
				for (File file : fileList.getValue()) {
					StringBuilder sb1 = new StringBuilder();
					sb1.append(PREFIX);
					sb1.append(BOUNDARY);
					sb1.append(LINEND);
					// sb1.append("Content-Disposition: form-data; name=\""+file.getKey()+"\";filename=\""
					// + file.getValue().getName() + "\"" + LINEND);
					sb1.append("Content-Disposition: form-data; name=\"" + key
							+ "\";filename=\"" + file.getName() + "\"" + LINEND);
					sb1.append("Content-Type: application/octet-stream; charset="
							+ CHARSET + LINEND);
					sb1.append(LINEND);
					outStream.write(sb1.toString().getBytes());
					InputStream is = new FileInputStream(file);
					byte[] buffer = new byte[1024];
					int len = 0;
					while ((len = is.read(buffer)) != -1) {
						outStream.write(buffer, 0, len);
					}
					is.close();
					outStream.write(LINEND.getBytes());
					Log.i(TAG, key + " >>> " + file.getName() + " >>> " + file);
				}
			}
		}
		byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
		outStream.write(end_data);
		outStream.flush();
		int res = conn.getResponseCode();
		Log.i(TAG, "res=" + res);
		InputStream in = conn.getInputStream();
		StringBuilder sb2 = new StringBuilder();
		if (res == 200) {
			int ch;
			while ((ch = in.read()) != -1) {
				sb2.append((char) ch);
			}
		}
		outStream.close();
		conn.disconnect();
		Log.i(TAG, "getPostResult complete sb=" + sb2.toString());
		return sb2.toString();
	}
}
