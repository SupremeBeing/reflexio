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

import java.util.List;

public interface ITypeReflection<T> extends IReflection {

    boolean isFinal();

    T instantiate(Object... args);

    List<IConstructorReflection<T>> getConstructors();

    IConstructorReflection<T> findConstructor(Class<?>... types);

    IConstructorReflection<T> findDefaultConstructor();

    List<ITypeReflection<?>> getTypeHierarchy();

    List<IStaticMethodReflection> getStaticTypeMethods();

    List<IInstanceMethodReflection> getInstanceTypeMethods();

    List<IStaticMethodReflection> getStaticMethods();

    List<IInstanceMethodReflection> getInstanceMethods();

    List<IStaticFieldReflection> getStaticTypeFields();

    List<IInstanceFieldReflection> getInstanceTypeFields();

    List<IStaticFieldReflection> getStaticFields();

    List<IInstanceFieldReflection> getInstanceFields();

    IStaticFieldReflection findStaticField(String fieldName);

    IInstanceFieldReflection findInstanceField(String fieldName);

    IInstanceMethodReflection findInstanceMethod(String methodName);

    IStaticMethodReflection findStaticMethod(String methodName);

}
