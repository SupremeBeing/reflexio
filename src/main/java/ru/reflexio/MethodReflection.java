/*
 * Copyright (c) 2019, Dmitriy Shchekotin
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package ru.reflexio;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

class MethodReflection extends ExecutableReflection<Method, Object> implements IMethodReflection {

	private static final String IS_PERFIX = "is";
	private static final String GET_PREFIX = "get";
	private static final String SET_PREFIX = "set";
	private static final String[] GETTER_PERFIXES = { IS_PERFIX, GET_PREFIX };

	MethodReflection(Method method) {
		super(method);
	}

	@Override
	public Object invokeOn(Object data, Object... args) {
		return forceAccess(() -> {
			try {
				return getElement().invoke(data, args);
			} catch (InvocationTargetException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		});
	}

	@Override
	public Object invoke(Object... args) {
		return invokeOn(null, args);
	}

	@Override
	public boolean isStatic() {
		return Modifier.isStatic(getElement().getModifiers());
	}

	public boolean isFinal() {
		return Modifier.isFinal(getElement().getModifiers());
	}

	@Override
	public Class<?> getType() {
		return getElement().getReturnType();
	}

	@Override
	public boolean isGetter() {
		if (getElement().getParameterCount() == 0) {
			for (String prefix : GETTER_PERFIXES) {
				if (getElement().getName().startsWith(prefix)) {
					return true;
				}
			}
		}
		return false;
	}

	private String getSetterName() {
		for (String prefix : GETTER_PERFIXES) {
			if (getElement().getName().startsWith(prefix)) {
				return SET_PREFIX + getElement().getName().substring(prefix.length());
			}
		}
		return null;
	}

	@Override
	public IMethodReflection getSetter() {
		String setterName = getSetterName();
		if (setterName != null) {
			try {
				Method method = getElement().getDeclaringClass().getDeclaredMethod(setterName, getElement().getReturnType());
				return new MethodReflection(method);
			} catch (NoSuchMethodException ignore) {}
		}
		return null;
	}
}
