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

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class TypeReflection<T> extends AnnotatedReflection<Class<T>> implements ITypeReflection<T> {
	
	public TypeReflection(Class<T> cl) {
		super(cl);
	}

	@Override
	public boolean isFinal() {
		return Modifier.isFinal(getElement().getModifiers());
	}

	@Override
	public String getName() {
		return getElement().getName();
	}

	@Override
	public Class<?> getType() {
		return getElement();
	}

	@Override
	public T instantiate(Object... args) {
		Class<?>[] types = new Class<?>[args.length];
		for (int i = 0; i < args.length; i++) {
			Object arg = args[i];
			types[i] = arg == null ? null : arg.getClass();
		}
		IConstructorReflection<T> ctor = findConstructor(types);
		if (ctor != null) {
			return ctor.invoke(args);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IConstructorReflection<T>> getConstructors() {
		List<IConstructorReflection<T>> result = new ArrayList<>();
		if (getElement().isArray()) {
			result.add(new ArrayConstructorReflection.Empty<>(getElement()));
			result.add(new ArrayConstructorReflection.Length<>(getElement()));
		} else if (getElement().isPrimitive()) {
			Primitive primitive = Primitive.findByPrimitiveType(getElement());
			if (primitive != null) {
				for (Constructor<?> ctor : primitive.getBoxedType().getDeclaredConstructors()) {
					result.add(new ConstructorReflection<>((Constructor<T>) ctor));
				}
			}
		} else {
			for (Constructor<?> ctor : getElement().getDeclaredConstructors()) {
				result.add(new ConstructorReflection<>((Constructor<T>) ctor));
			}
		}
		return result;
	}

	@Override
	public IConstructorReflection<T> findConstructor(Class<?>... types) {
		for (IConstructorReflection<T> ctor : getConstructors()) {
			if (ctor.canInvoke(types)) {
				return ctor;
			}
		}
		return null;
	}

	@Override
	public IConstructorReflection<T> findDefaultConstructor() {
		IConstructorReflection<T> least = null;
		for (IConstructorReflection<T> ctor : getConstructors()) {
			List<IParameterReflection> params = ctor.getParameters();
			if (params.isEmpty()) {
				return ctor;
			}
			if (least == null || params.size() < least.getParameters().size()) {
				least = ctor;
			}
		}
		return least;
	}

	@Override
	public List<ITypeReflection<?>> getTypeHierarchy() {
		List<ITypeReflection<?>> result = new ArrayList<>();
		walkTypeHierarchy(cl -> result.add(0, new TypeReflection<>(cl)));
		return result;
	}

	@Override
	public List<IStaticMethodReflection> getStaticTypeMethods() {
		List<IStaticMethodReflection> result = new ArrayList<>();
		for (Method method : getElement().getDeclaredMethods()) {
			if (Modifier.isStatic(method.getModifiers())) {
				result.add(new StaticMethodReflection(method));
			}
		}
		return result;
	}

	@Override
	public List<IInstanceMethodReflection> getInstanceTypeMethods() {
		List<IInstanceMethodReflection> result = new ArrayList<>();
		for (Method method : getElement().getDeclaredMethods()) {
			if (!Modifier.isStatic(method.getModifiers())) {
				result.add(new InstanceMethodReflection(method));
			}
		}
		return result;
	}

	@Override
	public List<IStaticMethodReflection> getStaticMethods() {
		List<IStaticMethodReflection> result = new ArrayList<>();
		for (ITypeReflection<?> tr : getTypeHierarchy()) {
			result.addAll(tr.getStaticTypeMethods());
		}
		return result;
	}

	@Override
	public List<IInstanceMethodReflection> getInstanceMethods() {
		List<IInstanceMethodReflection> result = new ArrayList<>();
		for (ITypeReflection<?> tr : getTypeHierarchy()) {
			result.addAll(tr.getInstanceTypeMethods());
		}
		return result;
	}

	@Override
	public List<IStaticFieldReflection> getStaticTypeFields() {
		List<IStaticFieldReflection> result = new ArrayList<>();
		for (Field field : getElement().getDeclaredFields()) {
			if (Modifier.isStatic(field.getModifiers())) {
				result.add(new StaticFieldReflection(field));
			}
		}
		return result;
	}

	@Override
	public List<IInstanceFieldReflection> getInstanceTypeFields() {
		List<IInstanceFieldReflection> result = new ArrayList<>();
		for (Field field : getElement().getDeclaredFields()) {
			if (!Modifier.isStatic(field.getModifiers())) {
				result.add(new InstanceFieldReflection(field));
			}
		}
		return result;
	}

	@Override
	public List<IStaticFieldReflection> getStaticFields() {
		List<IStaticFieldReflection> result = new ArrayList<>();
		for (ITypeReflection<?> tr : getTypeHierarchy()) {
			result.addAll(tr.getStaticTypeFields());
		}
		return result;
	}

	@Override
	public List<IInstanceFieldReflection> getInstanceFields() {
		List<IInstanceFieldReflection> result = new ArrayList<>();
		for (ITypeReflection<?> tr : getTypeHierarchy()) {
			result.addAll(tr.getInstanceTypeFields());
		}
		return result;
	}

	@Override
	public IStaticFieldReflection findStaticField(String fieldName) {
		return searchTypeHierarchy(cl -> findStaticField(cl, fieldName));
	}

	@Override
	public IInstanceFieldReflection findInstanceField(String fieldName) {
		return searchTypeHierarchy(cl -> findInstanceField(cl, fieldName));
	}

	@Override
	public IStaticMethodReflection findStaticMethod(String methodName) {
		return searchTypeHierarchy(cl -> findStaticMethod(cl, methodName));
	}

	@Override
	public IInstanceMethodReflection findInstanceMethod(String methodName) {
		return searchTypeHierarchy(cl -> findInstanceMethod(cl, methodName));
	}

	private IStaticFieldReflection findStaticField(Class<?> cl, String fieldName) {
		try {
			Field field = cl.getDeclaredField(fieldName);
			if (Modifier.isStatic(field.getModifiers())) {
				return new StaticFieldReflection(field);
			}
		} catch (NoSuchFieldException ignore) {}
		return null;
	}

	private IInstanceFieldReflection findInstanceField(Class<?> cl, String fieldName) {
		try {
			Field field = cl.getDeclaredField(fieldName);
			if (!Modifier.isStatic(field.getModifiers())) {
				return new InstanceFieldReflection(field);
			}
		} catch (NoSuchFieldException ignore) {}
		return null;
	}

	private IStaticMethodReflection findStaticMethod(Class<?> cl, String methodName) {
		for (Method m : cl.getDeclaredMethods()) {
			if (Objects.equals(methodName, m.getName()) && Modifier.isStatic(m.getModifiers())) {
				return new StaticMethodReflection(m);
			}
		}
		return null;
	}

	private IInstanceMethodReflection findInstanceMethod(Class<?> cl, String methodName) {
		for (Method m : cl.getDeclaredMethods()) {
			if (Objects.equals(methodName, m.getName()) && !Modifier.isStatic(m.getModifiers())) {
				return new InstanceMethodReflection(m);
			}
		}
		return null;
	}

	private <R> R searchTypeHierarchy(Function<Class<?>, R> function) {
		Class<?> cl = getElement();
		while (cl != null) {
			R result = function.apply(cl);
			if (result != null) {
				return result;
			}
			cl = cl.getSuperclass();
		}
		return null;
	}

	private void walkTypeHierarchy(Consumer<Class<?>> consumer) {
		Class<?> cl = getElement();
		while (cl != null) {
			consumer.accept(cl);
			cl = cl.getSuperclass();
		}
	}

}
