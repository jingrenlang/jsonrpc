package com.jsonrpc.core.utils;

import java.io.IOException;
import java.io.InputStream;

public class IoUtils {

	public static byte[] readStream(InputStream input, int length) throws IOException {
		byte[] result = new byte[length];
		int offset = 0;
		while (offset < result.length) {
			int bytesRead = input.read(result, offset, result.length - offset);
			if (bytesRead == -1) {
				break; // end of stream
			}
			offset += bytesRead;
		}
		return result;
	}

}
