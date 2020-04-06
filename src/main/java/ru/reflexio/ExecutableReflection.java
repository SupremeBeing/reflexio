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

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

abstract class ExecutableReflection<T extends Executable> extends MemberReflection<T> implements IExecutableReflection {

	ExecutableReflection(T executable) {
		super(executable);
	}

	@Override
	public List<IParameterReflection> getParameters() {
		List<IParameterReflection> result = new ArrayList<>();
		for (Parameter param : getElement().getParameters()) {
			result.add(new ParameterReflection(param));
		}
		return result;
	}

	@Override
	public boolean canInvoke(Class<?>... types) {
		Parameter[] params = getElement().getParameters();
		if (types.length != params.length) {
			return false;
		}
		for (int i = 0; i < params.length; i++) {
			Class<?> type = types[i];
			Class<?> paramType = params[i].getType();
			if (type == null) {
				if (paramType.isPrimitive()) {
					return false;
				}
			} else if (type.isPrimitive() && paramType.isPrimitive()) {
				if (paramType != type) {
					return false;
				}
			} else if (!type.isPrimitive() && !paramType.isPrimitive()) {
				if (!paramType.isAssignableFrom(type)) {
					return false;
				}
			} else {
				if (!Primitive.canAssign(type, paramType)) {
					return false;
				}
			}
		}
		return true;
	}

}
