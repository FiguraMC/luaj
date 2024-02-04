/*******************************************************************************
* Copyright (c) 2010 Luaj.org. All rights reserved.
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
* THE SOFTWARE.
******************************************************************************/
package org.luaj.vm2.ast;

import org.luaj.vm2.LuaString;

import java.nio.charset.StandardCharsets;

public class Str {

	private Str() {}

	public static LuaString quoteString(String image) {
		String s = image.substring(1, image.length()-1);
		byte[] bytes = unquote(s);
		return LuaString.valueUsing(bytes);
	}

	public static LuaString charString(String image) {
		String s = image.substring(1, image.length()-1);
		byte[] bytes = unquote(s);
		return LuaString.valueUsing(bytes);
	}

	public static LuaString longString(String image) {
		int i = image.indexOf('[', image.indexOf('[')+1)+1;
		String[] s = image.substring(i, image.length()-i).split("^\\r?\\n?", 2);
		byte[] b = s[s.length - 1].getBytes(StandardCharsets.UTF_8);
		return LuaString.valueUsing(b);
	}

	public static byte[] unquote(String s) {
		StringBuilder builder = new StringBuilder();
		char[] c = s.toCharArray();
		for (int i = 0, n = c.length; i < n; i++) {
			if (c[i] == '\\' && i < n - 1) {
                switch (c[++i]) {
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                        int d = c[i++] - '0';
                        for (int j = 0; i < n && j < 2 && c[i] >= '0' && c[i] <= '9'; i++, j++)
                            d = d * 10 + c[i] - '0';
                        builder.append((char) d);
                        --i;
                        break;
                    case 'a':
                        builder.append('\u0007');
                        break;
                    case 'b':
                        builder.append('\b');
                        break;
                    case 'f':
                        builder.append('\f');
                        break;
                    case 'n':
                        builder.append('\n');
                        break;
                    case 'r':
                        builder.append('\r');
                        break;
                    case 't':
                        builder.append('\t');
                        break;
                    case 'v':
                        builder.append('\u000B');
                        break;
                    case '"':
                        builder.append('"');
                        break;
                    case '\'':
                        builder.append('\'');
                        break;
                    case '\\':
                        builder.append('\\');
                        break;
                    default:
                        builder.append(c[i]);
                        break;
                }
			} else {
				builder.append(c[i]);
			}
		}
		return builder.toString().getBytes(StandardCharsets.UTF_8);
	}
}
