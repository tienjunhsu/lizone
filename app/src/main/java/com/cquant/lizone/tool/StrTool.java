/* Copyright (c) 1995-2000, The Hypersonic SQL Group.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the Hypersonic SQL Group nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE HYPERSONIC SQL GROUP,
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * This software consists of voluntary contributions made by many individuals
 * on behalf of the Hypersonic SQL Group.
 *
 *
 * For work added by the HSQL Development Group:
 *
 * Copyright (c) 2001-2005, The HSQL Development Group
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the HSQL Development Group nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL HSQL DEVELOPMENT GROUP, HSQLDB.ORG,
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.cquant.lizone.tool;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Collection of static methods for converting strings between different formats
 * and to and from byte arrays.
 * <p>
 * 
 * New class, with extensively enhanced and rewritten Hypersonic code.
 * 
 * @author Thomas Mueller (Hypersonic SQL Group)
 * @author fredt@users
 * @version 1.8.0
 * @since 1.7.2
 */

// fredt@users 20020328 - patch 1.7.0 by fredt - error trapping
public class StrTool {

	private static final byte[] HEXBYTES = { (byte) '0', (byte) '1',
			(byte) '2', (byte) '3', (byte) '4', (byte) '5', (byte) '6',
			(byte) '7', (byte) '8', (byte) '9', (byte) 'A', (byte) 'B',
			(byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F' };
	private static final String HEXINDEX = "0123456789abcdef0123456789ABCDEF";

	/**
	 * Converts a String into a byte array by using a big-endian two byte
	 * representation of each char value in the string.
	 */
	byte[] stringToFullByteArray(String s) {

		int length = s.length();
		byte[] buffer = new byte[length * 2];
		int c;

		for (int i = 0; i < length; i++) {
			c = s.charAt(i);
			buffer[i * 2] = (byte) ((c & 0x0000ff00) >> 8);
			buffer[i * 2 + 1] = (byte) (c & 0x000000ff);
		}

		return buffer;
	}

	/**
	 * Compacts a hexadecimal string into a byte array
	 * 
	 * 
	 * @param s
	 *            hexadecimal string
	 * 
	 * @return byte array for the hex string
	 * @throws IOException
	 */
	public static byte[] hexToByte(String s) throws IOException {

		int l = s.length() / 2;
		byte[] data = new byte[l];
		int j = 0;

		if (s.length() % 2 != 0) {
			throw new IOException(
					"hexadecimal string with odd number of characters");
		}

		for (int i = 0; i < l; i++) {
			char c = s.charAt(j++);
			int n, b;

			n = HEXINDEX.indexOf(c);

			if (n == -1) {
				throw new IOException(
						"hexadecimal string contains non hex character");
			}

			b = (n & 0xf) << 4;
			c = s.charAt(j++);
			n = HEXINDEX.indexOf(c);
			b += (n & 0xf);
			data[i] = (byte) b;
		}

		return data;
	}

	/**
	 * Converts a byte array into a hexadecimal string
	 * 
	 * 
	 * @param b
	 *            byte array
	 * 
	 * @return hex string
	 */
	public static String byteToHex(byte[] b) {

		int len = b.length;
		char[] s = new char[len * 2];

		for (int i = 0, j = 0; i < len; i++) {
			int c = ((int) b[i]) & 0xff;

			s[j++] = (char) HEXBYTES[c >> 4 & 0xf];
			s[j++] = (char) HEXBYTES[c & 0xf];
		}

		return new String(s);
	}

	/**
	 * Converts a byte array into hexadecimal characters which are written as
	 * ASCII to the given output stream.
	 * 
	 * @param o
	 *            output stream
	 * @param b
	 *            byte array
	 */
	public static void writeHex(byte[] o, int from, byte[] b) {

		int len = b.length;

		for (int i = 0; i < len; i++) {
			int c = ((int) b[i]) & 0xff;

			o[from++] = HEXBYTES[c >> 4 & 0xf];
			o[from++] = HEXBYTES[c & 0xf];
		}
	}

	public static String byteToString(byte[] b, String charset) {

		try {
			return (charset == null) ? new String(b) : new String(b, charset);
		} catch (Exception e) {
		}

		return null;
	}

	/**
	 * Hsqldb specific decoding used only for log files.
	 * 
	 * This method converts the 7 bit escaped ASCII strings in a log file back
	 * into Java Unicode strings. See unicodeToAccii() above,
	 * 
	 * @param s
	 *            encoded ASCII string in byte array
	 * @param offset
	 *            position of first byte
	 * @param length
	 *            number of bytes to use
	 * 
	 * @return Java Unicode string
	 */
	public static String asciiToUnicode(byte[] s, int offset, int length) {

		if (length == 0) {
			return "";
		}

		char[] b = new char[length];
		int j = 0;

		for (int i = 0; i < length; i++) {
			byte c = s[offset + i];

			if (c == '\\' && i < length - 5) {
				byte c1 = s[offset + i + 1];

				if (c1 == 'u') {
					i++;

					// 4 characters read should always return 0-15
					int k = HEXINDEX.indexOf(s[offset + (++i)]) << 12;

					k += HEXINDEX.indexOf(s[offset + (++i)]) << 8;
					k += HEXINDEX.indexOf(s[offset + (++i)]) << 4;
					k += HEXINDEX.indexOf(s[offset + (++i)]);
					b[j++] = (char) k;
				} else {
					b[j++] = (char) c;
				}
			} else {
				b[j++] = (char) c;
			}
		}

		return new String(b, 0, j);
	}

	public static String asciiToUnicode(String s) {

		if ((s == null) || (s.indexOf("\\u") == -1)) {
			return s;
		}

		int len = s.length();
		char[] b = new char[len];
		int j = 0;

		for (int i = 0; i < len; i++) {
			char c = s.charAt(i);

			if (c == '\\' && i < len - 5) {
				char c1 = s.charAt(i + 1);

				if (c1 == 'u') {
					i++;

					// 4 characters read should always return 0-15
					int k = HEXINDEX.indexOf(s.charAt(++i)) << 12;

					k += HEXINDEX.indexOf(s.charAt(++i)) << 8;
					k += HEXINDEX.indexOf(s.charAt(++i)) << 4;
					k += HEXINDEX.indexOf(s.charAt(++i));
					b[j++] = (char) k;
				} else {
					b[j++] = c;
				}
			} else {
				b[j++] = c;
			}
		}

		return new String(b, 0, j);
	}

	public static int getUTFSize(String s) {

		int len = (s == null) ? 0 : s.length();
		int l = 0;

		for (int i = 0; i < len; i++) {
			int c = s.charAt(i);

			if ((c >= 0x0001) && (c <= 0x007F)) {
				l++;
			} else if (c > 0x07FF) {
				l += 3;
			} else {
				l += 2;
			}
		}

		return l;
	}

	/**
	 * Using a Reader and a Writer, returns a String from an InputStream.
	 */
	public static String inputStreamToString(InputStream x, int length)
			throws IOException {

		InputStreamReader in = new InputStreamReader(x);
		StringWriter writer = new StringWriter();
		int blocksize = 8 * 1024;
		char[] buffer = new char[blocksize];

		for (int left = length; left > 0;) {
			int read = in.read(buffer, 0, left > blocksize ? blocksize : left);

			if (read == -1) {
				break;
			}

			writer.write(buffer, 0, read);

			left -= read;
		}

		writer.close();

		return writer.toString();
	}

	/**
	 * Counts Character c in String s
	 * 
	 * @param String
	 *            s
	 * 
	 * @return int count
	 */
	static int count(final String s, final char c) {

		int pos = 0;
		int count = 0;

		if (s != null) {
			while ((pos = s.indexOf(c, pos)) > -1) {
				count++;
				pos++;
			}
		}

		return count;
	}

	/**
	 * <ul>
	 * <li>SysUtils.isEmpty(null) = true</li>
	 * <li>SysUtils.isEmpty("") = true</li>
	 * <li>SysUtils.isEmpty("   ") = true</li>
	 * <li>SysUtils.isEmpty("abc") = false</li>
	 * </ul>
	 */
	public static boolean isEmpty(String value) {
		int strLen;
		if (value == null || (strLen = value.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if ((Character.isWhitespace(value.charAt(i)) == false)) {
				return false;
			}
		}
		return true;
	}

	public static String time(Long time,String format) {
		String date = new SimpleDateFormat(isEmpty(format)?"yyyy-MM-dd  HH:mm":format)
				.format(new java.util.Date(time));// HH:mm:ss
		return date;
	}
	public static String timeformat(Long timeflag) {
		return time(timeflag,"yyyy-MM-dd");
	}

	public static String timeformatLong(Long timeflag) {
		return   time(timeflag,"yyyy-MM-dd HH:mm");
	}

	public static String formatDiskSize(int Size) {
		int kb = Size >> 10;
		if (kb < 1024) {
			return String.format("%.1f KB", kb + (Size % 1024) / (float) 1024);
		} else {
			int mb = kb >> 10;
			return String.format("%.3f  MB", mb + (kb % 1024) / (float) 1024);
		}
	}

	public static String timeState(long _timestamp) {
		long millsGap = System.currentTimeMillis() - _timestamp;
		String[] msgs = null;
		if (millsGap < 0) {
			millsGap = 0 - millsGap;
			msgs = new String[] { "即将", "分钟后", "今天 HH:mm", "明天 HH:mm",
					"d天后 HH:mm" };
		} else {
			msgs = new String[] { "刚刚", "分钟前", "今天 HH:mm", "昨天 HH:mm",
					"d天前 HH:mm" };
		}

		if (millsGap < 1 * 60 * 1000) {
			return msgs[0];
		} else if (millsGap < 30 * 60 * 1000) {
			return (millsGap / 1000 / 60) + msgs[1];
		} else {
			Calendar now = Calendar.getInstance();
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(_timestamp);
			if (c.get(Calendar.YEAR) == now.get(Calendar.YEAR)
					&& c.get(Calendar.MONTH) == now.get(Calendar.MONTH)
					&& c.get(Calendar.DAY_OF_MONTH) == now
							.get(Calendar.DAY_OF_MONTH)) {
				SimpleDateFormat sdf = new SimpleDateFormat(msgs[2]);
				return sdf.format(c.getTime());
			}
			if (c.get(Calendar.YEAR) == now.get(Calendar.YEAR)
					&& c.get(Calendar.MONTH) == now.get(Calendar.MONTH)
					&& Math.abs(c.get(Calendar.DAY_OF_MONTH)
							- now.get(Calendar.DAY_OF_MONTH)) == 1) {
				SimpleDateFormat sdf = new SimpleDateFormat(msgs[3]);
				return sdf.format(c.getTime());
			} else if (c.get(Calendar.YEAR) == now.get(Calendar.YEAR)) {
				SimpleDateFormat sdf = null;
				int datagap = Math.abs(now.get(Calendar.DATE)
						- c.get(Calendar.DATE));
				if (datagap < 8) {
					c.set(Calendar.DATE, datagap);
					sdf = new SimpleDateFormat(msgs[4]);
				} else {
					sdf = new SimpleDateFormat("M月d日 HH:mm");
				}
				return sdf.format(c.getTime());
			} else {
				SimpleDateFormat sdf = new SimpleDateFormat("yy年M月d日 HH");
				return sdf.format(c.getTime());
			}
		}

	}

	public static String formatInt2F(int val, int decimal) {
		if (decimal > 0) {
			int v = 10;
			while (--decimal > 0) {
				v *= 10;
			}
			return formatF((float) val / v, decimal);
		}
		return val + "";
	}

	public static String formatF(float val, int decimal) {
		try {
			if (decimal > 0) {
				String format = "%1$.#f".replace("#", decimal + "");
				String r = String.format(format, val);
				int len = r.length();
				while (r.charAt(--len) == '0')
					;
				if (r.charAt(len) == '.') {
					return r.substring(0, len);
				} else {
					return r.substring(0, ++len);
				}
			}
		} catch (Exception e) {
		}
		return val + "";
	}

	/**
	 */
	public static boolean isNumeric(Object obj) {
		if (obj == null) {
			return false;
		}
		String str = obj.toString();
		int sz = str.length();
		for (int i = 0; i < sz; i++) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public static boolean isNumeric(String str) {
		if (str != null) {
			Pattern pattern = Pattern.compile("[0-9]*");
			return pattern.matcher(str).matches();
		} else {
			return false;
		}
	}

	public static boolean isEmail(String str) {
		Pattern pattern = Pattern
				.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
		return pattern.matcher(str).matches();
	}

	/*
	 * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188 　　
	 * 联通：130、131、132、152、155、156、185、186 　　 电信：133、153、180、189、（1349卫通）
	 */
	public static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9])|(170)|(147))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	public static ArrayList<String> findAllNumber(String input) {
		ArrayList<String> list = new ArrayList<String>();
		Pattern pattern = Pattern.compile("[0-9]+");
		Matcher matcher = pattern.matcher(input);
		while (matcher.find()) {
			list.add(matcher.group());
		}
		return list;
	}

	public static boolean isDate(String str) {
		// "2000-02-29 23:59:59"
		Pattern pattern = Pattern
				.compile("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
		return pattern.matcher(str).matches();
	}

	/**
	 * 
	 * @param values
	 * @return true if all value is empty,
	 */
	public static boolean areEmpty(String... values) {
		boolean result = true;
		if (values == null || values.length == 0) {
			result = true;
		} else {
			for (String value : values) {
				result = isEmpty(value);
				if (result == false) {
					return false;
				}
			}
		}
		return result;
	}

	public static boolean areNotEmpty(String... values) {
		boolean result = true;
		if (values == null || values.length == 0) {
			return false;
		} else {
			for (String value : values) {
				result = isEmpty(value);

				if (result) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 */
	public static String unicodeToChinese(String unicode) {
		StringBuilder out = new StringBuilder();
		if (!isEmpty(unicode)) {
			for (int i = 0; i < unicode.length(); i++) {
				out.append(unicode.charAt(i));
			}
		}
		return out.toString();
	}

	/**
	 */
	public static String stripNonValidXMLCharacters(String input) {
		if (input == null || ("".equals(input)))
			return "";
		StringBuilder out = new StringBuilder();
		char current;
		for (int i = 0; i < input.length(); i++) {
			current = input.charAt(i);
			if ((current == 0x9) || (current == 0xA) || (current == 0xD)
					|| ((current >= 0x20) && (current <= 0xD7FF))
					|| ((current >= 0xE000) && (current <= 0xFFFD))
					|| ((current >= 0x10000) && (current <= 0x10FFFF)))
				out.append(current);
		}
		return out.toString();
	}

	public static String[] insertElement(String original[], String element,
			int index) {
		int length = original.length;
		String destination[] = new String[length + 1];
		System.arraycopy(original, 0, destination, 0, index);
		destination[index] = element;
		System.arraycopy(original, index, destination, index + 1, length
				- index);
		return destination;
	}

	/**
	 * The jce MD5 message digest generator.
	 */
	private static MessageDigest md5;

	/**
	 * Retrieves a hexidecimal character sequence representing the MD5 digest of
	 * the specified character sequence, using the specified encoding to first
	 * convert the character sequence into a byte sequence. If the specified
	 * encoding is null, then ISO-8859-1 is assumed
	 * 
	 * @param string
	 *            the string to encode.
	 * @param encoding
	 *            the encoding used to convert the string into the byte sequence
	 *            to submit for MD5 digest
	 * @return a hexidecimal character sequence representing the MD5 digest of
	 *         the specified string
	 * @throws HsqlUnsupportedOperationException
	 *             if an MD5 digest algorithm is not available through the
	 *             java.security.MessageDigest spi or the requested encoding is
	 *             not available
	 */
	public static final String digest2HexStr(String string, String encoding)
			throws RuntimeException {
		return byteToHex(digest2Byte(string, encoding));
	}

	/**
	 * Retrieves a byte sequence representing the MD5 digest of the specified
	 * character sequence, using the specified encoding to first convert the
	 * character sequence into a byte sequence. If the specified encoding is
	 * null, then ISO-8859-1 is assumed.
	 * 
	 * @param string
	 *            the string to digest.
	 * @param encoding
	 *            the character encoding.
	 * @return the digest as an array of 16 bytes.
	 * @throws HsqlUnsupportedOperationException
	 *             if an MD5 digest algorithm is not available through the
	 *             java.security.MessageDigest spi or the requested encoding is
	 *             not available
	 */
	public static byte[] digest2Byte(String string, String encoding)
			throws RuntimeException {

		byte[] data;

		if (encoding == null) {
			encoding = "ISO-8859-1";
		}

		try {
			data = string.getBytes(encoding);
		} catch (UnsupportedEncodingException x) {
			throw new RuntimeException(x.toString());
		}

		return digestBytes(data);
	}

	/**
	 * Retrieves a byte sequence representing the MD5 digest of the specified
	 * byte sequence.
	 * 
	 * @param data
	 *            the data to digest.
	 * @return the MD5 digest as an array of 16 bytes.
	 * @throws HsqlUnsupportedOperationException
	 *             if an MD5 digest algorithm is not available through the
	 *             java.security.MessageDigest spi
	 */
	public static final byte[] digestBytes(byte[] data) throws RuntimeException {

		synchronized (StrTool.class) {
			if (md5 == null) {
				try {
					md5 = MessageDigest.getInstance("MD5");
				} catch (NoSuchAlgorithmException e) {
					throw new RuntimeException(e.toString());
				}
			}

			return md5.digest(data);
		}
	}

	/**
	 * 基本功能：过滤所有以"<"�?���?>"结尾的标�?
	 * 
	 * @param str
	 * @return String
	 */
	public static String filterHtml(String str) {
		// Pattern pattern = Pattern.compile("<([^>]*)>");
		// Matcher matcher = pattern.matcher(str);
		// StringBuffer sb = new StringBuffer();
		// boolean result1 = matcher.find();
		// while (result1) {
		// matcher.appendReplacement(sb, "");
		// result1 = matcher.find();
		// }
		// matcher.appendTail(sb);
		// return sb.toString();
		return str.replaceAll("<([^>]*)>", "");
	}

	/**
	 * 基本功能：过滤指定标�?
	 * 
	 * @param str
	 * @param tag
	 *            指定标签
	 * @return String
	 */
	public static String fiterHtmlTag(String str, String tag) {
		String regxp = "<\\s*/{0,1}\\s*" + tag + "\\s*([^>]*)\\s*>";
		// Pattern pattern = Pattern.compile(regxp);
		// Matcher matcher = pattern.matcher(str);
		// StringBuffer sb = new StringBuffer();
		// boolean result1 = matcher.find();
		// while (result1) {
		// matcher.appendReplacement(sb, "");
		// result1 = matcher.find();
		// }
		// matcher.appendTail(sb);
		// return sb.toString();
		return str.replaceAll(regxp, "");
	}

	/**
	 * 基本功能：替换指定的标签
	 * 
	 * @param str
	 * @param beforeTag
	 *            要替换的标签
	 * @param tagAttrib
	 *            要替换的标签属�?�?
	 * @param startTag
	 *            新标签开始标�?
	 * @param endTag
	 *            新标签结束标�?
	 * @return String
	 * @如：替换img标签的src属�?值为[img]属�?值[/img]
	 */
	public static String replaceHtmlTag(String str, String beforeTag,
			String tagAttrib, String startTag, String endTag) {
		String regxpForTag = "<\\s*" + beforeTag + "\\s+([^>]*)\\s*>";
		String regxpForTagAttrib = tagAttrib + "\\s*=\\s*\"([^\"]+)\"";
		Pattern patternForTag = Pattern.compile(regxpForTag);
		Pattern patternForAttrib = Pattern.compile(regxpForTagAttrib);
		Matcher matcherForTag = patternForTag.matcher(str);
		StringBuffer sb = new StringBuffer();
		boolean result = matcherForTag.find();
		while (result) {
			StringBuffer sbreplace = new StringBuffer();
			Matcher matcherForAttrib = patternForAttrib.matcher(matcherForTag
					.group(1));
			if (matcherForAttrib.find()) {
				matcherForAttrib.appendReplacement(sbreplace, startTag
						+ matcherForAttrib.group(1) + endTag);
			}
			matcherForTag.appendReplacement(sb, sbreplace.toString());
			result = matcherForTag.find();
		}
		matcherForTag.appendTail(sb);
		return sb.toString();
	}

	/**
	 * remove attribution tagAttrib in Tag beforeTag. input is the whole html
	 * text.
	 * 
	 * @param input
	 * @param beforeTag
	 * @param tagAttrib
	 * @return
	 */
	public static String filterTagAttri(String input, String beforeTag,
			String tagAttrib) {
		String regxpTag = "<\\s*" + beforeTag + "\\s+([^>]*)\\s*>";
		String regxpTagAttrib = tagAttrib + "\\s*=\\s*\"[^\"]*\"";
		Pattern patternTag = Pattern.compile(regxpTag);
		Matcher matcherTag = patternTag.matcher(input);
		StringBuffer sb = new StringBuffer();
		boolean result = matcherTag.find();
		while (result) {
			String ss = "<" + beforeTag + " " + matcherTag.group(1) + ">";
			matcherTag.appendReplacement(sb, ss.replaceAll(regxpTagAttrib, ""));
			result = matcherTag.find();
		}
		matcherTag.appendTail(sb);
		return sb.toString();
	}
}