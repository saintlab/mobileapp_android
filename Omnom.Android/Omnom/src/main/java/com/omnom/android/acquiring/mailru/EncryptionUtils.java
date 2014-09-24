package com.omnom.android.acquiring.mailru;

import com.omnom.android.linker.utils.StringUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.TreeSet;

/**
 * Created by Ch3D on 24.09.2014.
 */
public class EncryptionUtils {
	private static final String ALGORITHM_SHA_1 = "SHA-1";
	private static final String UTF_8 = "UTF-8";
	private static final String HEX_FORMAT = "%02x";

	public static String encryptPassword(String password) {
		String sha1 = StringUtils.EMPTY_STRING;
		try {
			MessageDigest crypt = MessageDigest.getInstance(ALGORITHM_SHA_1);
			crypt.reset();
			crypt.update(password.getBytes(UTF_8));
			sha1 = byteToHex(crypt.digest());
		} catch(NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return sha1;
	}

	public static String byteToHex(final byte[] hash) {
		Formatter formatter = new Formatter();
		for(byte b : hash) {
			formatter.format(HEX_FORMAT, b);
		}
		String result = formatter.toString();
		formatter.close();
		return result;
	}

	public static String getSignature(final String secretKey, final HashMap<String, String> params) {
		final TreeSet<String> keys = new TreeSet<String>(params.keySet());
		final StringBuilder builder = new StringBuilder();
		for(final String key : keys) {
			builder.append(params.get(key));
		}
		final String s = builder.toString();
		return encryptPassword(s + secretKey);
	}
}
