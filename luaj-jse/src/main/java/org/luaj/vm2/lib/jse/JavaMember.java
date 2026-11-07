/*******************************************************************************
* Copyright (c) 2011 Luaj.org. All rights reserved.
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
package org.luaj.vm2.lib.jse;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.jse.CoerceLuaToJava.Coercion;

import java.lang.reflect.Array;

/**
 * Java method or constructor.
 * <p>
 * Primarily handles argument coercion for parameter lists including scoring of
 * compatibility and java varargs handling.
 * <p>
 * This class is not used directly. It is an abstract base class for
 * {@link JavaConstructor} and {@link JavaMethod}.
 *
 * @see JavaConstructor
 * @see JavaMethod
 * @see CoerceJavaToLua
 * @see CoerceLuaToJava
 */
abstract class JavaMember extends VarArgFunction {

	static final int METHOD_MODIFIERS_VARARGS = 0x80;

	final Coercion[] fixedargs;
	final Coercion   varargs;
	final Class      exactVarargType;
	final Coercion   exactVarargTypeCoercion;

	protected JavaMember(Class[] params, int modifiers) {
		boolean isvarargs = (modifiers & METHOD_MODIFIERS_VARARGS) != 0;
		fixedargs = new CoerceLuaToJava.Coercion[isvarargs? params.length-1: params.length];
		for (int i = 0; i < fixedargs.length; i++)
			fixedargs[i] = CoerceLuaToJava.getCoercion(params[i]);
		if (isvarargs) {
			Class lastParamType = params[params.length - 1];
			exactVarargType = lastParamType.getComponentType();
			varargs = CoerceLuaToJava.getCoercion(lastParamType);
			exactVarargTypeCoercion = CoerceLuaToJava.getCoercion(exactVarargType);
		} else {
			exactVarargType = null;
			varargs = null;
			exactVarargTypeCoercion = null;
		}
	}

	int score(Varargs args) {
		int n = args.narg();
		int s = n > fixedargs.length? CoerceLuaToJava.SCORE_WRONG_TYPE*(n-fixedargs.length): 0;
		for (int j = 0; j < fixedargs.length; j++)
			s += fixedargs[j].score(args.arg(j+1));
		if (varargs != null)
			for (int k = fixedargs.length; k < n; k++)
				s += varargs.score(args.arg(k+1));
		return s;
	}

	protected Object[] convertArgs(Varargs args) {
		Object[] a;
		if (varargs == null) {
			a = new Object[fixedargs.length];
			for (int i = 0; i < a.length; i++)
				a[i] = fixedargs[i].coerce(args.arg(i+1));
		} else {
			int n = fixedargs.length;
			a = new Object[n + 1];
			for (int i = 0; i < fixedargs.length; i++) {
				a[i] = fixedargs[i].coerce(args.arg(i + 1));
			}
			a[fixedargs.length] = coerceVarArgs(args);
		}
		return a;
	}

	/**
	 * Ported from <a href="https://github.com/ChloePrime/GunsmithLib/blob/master/src/main/java/mod/chloeprime/gunsmithlib/mixin/common/luaj/bugfix/MixinJavaMember.java">GunsmithLib's fix via SpongePowered Mixin</a>
	 */
	private Object coerceVarArgs(Varargs args) {
		// Compatibility path
		Object compat = coerceVarArgsCompat(args);
		if (compat != null) {
			return compat;
		}
		// LuaValue[] -> Object[]
		int base = fixedargs.length;
		int argc = args.narg() - base;
		if (argc == 0) {
			return Array.newInstance(exactVarargType, 0);
		}
		LuaTable argv = new LuaTable();
		for (int i = 0; i < argc; i++) {
			argv.set(i + 1, args.arg(base + i + 1));
		}
		return varargs.coerce(argv);
	}

	/**
	 * Compatibility method for old workable grammar {@code javaMethod(fix_arg, {vararg_0, vararg_1})}
	 */
	private Object coerceVarArgsCompat(Varargs args) {
		// There must be only 1 varargs
		if (args.narg() != fixedargs.length + 1) {
			return null;
		}
		LuaValue tail = args.arg(args.narg());
		if (!(tail instanceof LuaTable)) {
			return null;
		}
		LuaTable argArray = (LuaTable) tail;
		int len = argArray.length();
		if (len == 0) {
			return Array.newInstance(exactVarargType, 0);
		}
		// If type mismatch (e.g. passes {0} to Object[]...)
		if (exactVarargTypeCoercion.coerce(argArray.get(1)) == null) {
			return null;
		}
		Object[] jArgv = (Object[]) Array.newInstance(exactVarargType, len);
		for (int i = 0; i < len; i++) {
			jArgv[i] = exactVarargTypeCoercion.coerce(argArray.get(i + 1));
		}
		return jArgv;
	}
}
