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

import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class TypeReflectionTest {

	@Test
	public void testClassHierarchy() {
		TypeReflection<GrandChild> cr = new TypeReflection<>(GrandChild.class);
		List<ITypeReflection<?>> hierarchy = cr.getTypeHierarchy();
		Assert.assertEquals(4, hierarchy.size());
		Assert.assertEquals(Object.class, hierarchy.get(0).getType());
		Assert.assertEquals(Parent.class, hierarchy.get(1).getType());
		Assert.assertEquals(Child.class, hierarchy.get(2).getType());
		Assert.assertEquals(GrandChild.class, hierarchy.get(3).getType());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullClass() {
		new TypeReflection<>(null);
	}

	@Test
	public void testVoidHierarchy() {
		TypeReflection<Void> cr = new TypeReflection<>(void.class);
		List<ITypeReflection<?>> hierarchy = cr.getTypeHierarchy();
		Assert.assertEquals(1, hierarchy.size());
		Assert.assertEquals(void.class, hierarchy.get(0).getType());
	}

	@Test
	public void testArrayHierarchy() {
		TypeReflection<int[]> cr = new TypeReflection<>(int[].class);
		List<ITypeReflection<?>> hierarchy = cr.getTypeHierarchy();
		Assert.assertEquals(2, hierarchy.size());
		Assert.assertEquals(Object.class, hierarchy.get(0).getType());
		Assert.assertEquals(int[].class, hierarchy.get(1).getType());
	}

	@Test
	public void testGetConstructors() {
		TypeReflection<GrandChild> cr = new TypeReflection<>(GrandChild.class);
		List<IConstructorReflection<GrandChild>> ctors = cr.getConstructors();
		Assert.assertEquals(3, ctors.size());
	}

	@Test
	public void testGetArrayConstructors() {
		TypeReflection<int[]> cr = new TypeReflection<>(int[].class);
		List<IConstructorReflection<int[]>> ctors = cr.getConstructors();
		Assert.assertEquals(2, ctors.size());
	}

	@Test
	public void testGetEnumConstructors() {
		TypeReflection<ElementType> cr = new TypeReflection<>(ElementType.class);
		List<IConstructorReflection<ElementType>> ctors = cr.getConstructors();
		Assert.assertEquals(1, ctors.size());
	}

	@Test
	public void testGetPrimitiveConstructors() {
		TypeReflection<Integer> cr = new TypeReflection<>(int.class);
		List<IConstructorReflection<Integer>> ctors = cr.getConstructors();
		Assert.assertEquals(2, ctors.size());
	}

	@Test(expected = RuntimeException.class)
	public void testAbstractInstantiate() {
		TypeReflection<Parent> cr = new TypeReflection<>(Parent.class);
		cr.instantiate();
	}

	@Test
	public void testNullInstantiate() {
		TypeReflection<GrandChild> cr = new TypeReflection<>(GrandChild.class);
		GrandChild grandChild = cr.instantiate((String) null);
		Assert.assertNotNull(grandChild);
		Assert.assertNull(grandChild.getMessage()); // string constructor was called
	}

	@Test
	public void testInstantiate() {
		TypeReflection<GrandChild> cr = new TypeReflection<>(GrandChild.class);
		GrandChild grandChild = cr.instantiate("Message");
		Assert.assertEquals(GrandChild.class, grandChild.getClass());
		Assert.assertEquals("Message", grandChild.getMessage());
	}

	@Test
	public void testArrayInstantiate() {
		TypeReflection<int[]> cr = new TypeReflection<>(int[].class);
		int[] intArray = cr.instantiate(2);
		Assert.assertNotNull(intArray);
		Assert.assertEquals(2, intArray.length);
	}

	@Test
	public void testInnerInstantiate() {
		TypeReflection<Child> cr = new TypeReflection<>(Child.class);
		Child child = cr.instantiate("Nothing");
		TypeReflection<Child.Inner> ir = new TypeReflection<>(Child.Inner.class);
		Child.Inner inner = ir.instantiate(child);
		Assert.assertNotNull(inner);
	}

	@Test
	public void testInterfaceInstantiate() {
		TypeReflection<List> cr = new TypeReflection<>(List.class);
		List<?> list = cr.instantiate();
		Assert.assertNull(list);
	}

	@Test
	public void testAnnotationInstantiate() {
		TypeReflection<SuppressWarnings> cr = new TypeReflection<>(SuppressWarnings.class);
		SuppressWarnings a = cr.instantiate();
		Assert.assertNull(a);
	}

	@Test
	public void testPrimitiveInstantiate() {
		TypeReflection<Integer> cr = new TypeReflection<>(int.class);
		Integer obj = cr.instantiate(10);
		Assert.assertNotNull(obj);
		Assert.assertEquals(Integer.valueOf(10), obj);
	}

	@Test
	public void testSupertypeArgsInstantiate() {
		Collection<String> list = new ArrayList<>();
		TypeReflection<GrandChild> cr = new TypeReflection<>(GrandChild.class);
		GrandChild grandChild = cr.instantiate(list, "Custom");
		Assert.assertNotNull(grandChild);
		Assert.assertEquals(list, grandChild.getCollection());
		Assert.assertEquals("Custom", grandChild.getMessage());
	}
	
	@Test
	public void testPrimitiveArgsInstantiate() {
		TypeReflection<GrandChild> cr = new TypeReflection<>(GrandChild.class);
		int value = 10;
		GrandChild grandChild = cr.instantiate(value);
		Assert.assertNotNull(grandChild);
		Assert.assertEquals(value, grandChild.getCode());
	}

	@Test
	public void testPrimitiveCastInstantiate() {
		TypeReflection<GrandChild> cr = new TypeReflection<>(GrandChild.class);
		short value = 10;
		GrandChild grandChild = cr.instantiate(value);
		Assert.assertNull(grandChild);
	}

	@Test
	public void testFindDefaultConstructor() {
		TypeReflection<GrandChild> cr = new TypeReflection<>(GrandChild.class);
		IConstructorReflection<GrandChild> ctor = cr.findDefaultConstructor();
		Assert.assertNotNull(ctor);
		Assert.assertEquals(1, ctor.getParameters().size());
	}

	@Test
	public void testFindField() {
		TypeReflection<GrandChild> cr = new TypeReflection<>(GrandChild.class);
		IFieldReflection field = cr.findInstanceField("code");
		Assert.assertNotNull(field);
		Assert.assertEquals("code", field.getName());
	}
	
	@Test
	public void testFindMissingField() {
		TypeReflection<GrandChild> cr = new TypeReflection<>(GrandChild.class);
		IFieldReflection field = cr.findInstanceField("code2");
		Assert.assertNull(field);
	}
	
	@Test
	public void testFindStaticField() {
		TypeReflection<GrandChild> cr = new TypeReflection<>(GrandChild.class);
		IFieldReflection field = cr.findStaticField("CONSTANT");
		Assert.assertNotNull(field);
		Assert.assertEquals("CONSTANT", field.getName());
		Assert.assertTrue(field.isStatic());
	}
	
	@Test
	public void testFindInnerField() {
		TypeReflection<Child.Inner> cr = new TypeReflection<>(Child.Inner.class);
		IFieldReflection field = cr.findInstanceField("string");
		Assert.assertNotNull(field);
		Assert.assertEquals("string", field.getName());
	}
	
	@Test
	public void testFindMethod() {
		TypeReflection<GrandChild> cr = new TypeReflection<>(GrandChild.class);
		IMethodReflection method = cr.findInstanceMethod("getCode");
		Assert.assertNotNull(method);
		Assert.assertEquals("getCode", method.getName());
	}

	@Test
	public void testFindOverrideMethod() {
		GrandChild grandChild = new GrandChild("visible");
		grandChild.setMessage("shadowed");
		TypeReflection<Child> cr = new TypeReflection<>(Child.class);
		IInstanceMethodReflection method = (IInstanceMethodReflection) cr.findInstanceMethod("getMessage");
		Assert.assertNotNull(method);
		Object value = method.invoke(grandChild);
		Assert.assertEquals("visible", value);
	}

	@Test
	public void testFindMissingMethod() {
		TypeReflection<GrandChild> cr = new TypeReflection<>(GrandChild.class);
		IMethodReflection method = cr.findInstanceMethod("getCode2");
		Assert.assertNull(method);
	}
	
	@Test
	public void testFindStaticMethod() {
		TypeReflection<GrandChild> cr = new TypeReflection<>(GrandChild.class);
		IMethodReflection method = cr.findStaticMethod("setStatic");
		Assert.assertNotNull(method);
		Assert.assertEquals("setStatic", method.getName());
		Assert.assertTrue(method.isStatic());
	}
	
	@Test
	public void testFindInnerMethod() {
		TypeReflection<Child.Inner> cr = new TypeReflection<>(Child.Inner.class);
		IMethodReflection method = cr.findInstanceMethod("setString");
		Assert.assertNotNull(method);
		Assert.assertEquals("setString", method.getName());
	}

	@Test
	public void testGetInstanceMethods() {
		TypeReflection<GrandChild> cr = new TypeReflection<>(GrandChild.class);
		List<IInstanceMethodReflection> methods = cr.getInstanceMethods();
		Assert.assertFalse(methods.isEmpty());
		Assert.assertTrue(methods.stream().anyMatch(m -> "getCode".equals(m.getName())));
	}

	@Test
	public void testGetStaticMethods() {
		TypeReflection<GrandChild> cr = new TypeReflection<>(GrandChild.class);
		List<IStaticMethodReflection> methods = cr.getStaticMethods();
		Assert.assertFalse(methods.isEmpty());
		Assert.assertTrue(methods.stream().anyMatch(m -> "setStatic".equals(m.getName())));
	}

	@Test
	public void testGetInstanceFields() {
		TypeReflection<GrandChild> cr = new TypeReflection<>(GrandChild.class);
		List<IInstanceFieldReflection> fields = cr.getInstanceFields();
		Assert.assertEquals(5, fields.size());
		Assert.assertEquals("code", fields.get(1).getName());
	}

	@Test
	public void testGetStaticFields() {
		TypeReflection<GrandChild> cr = new TypeReflection<>(GrandChild.class);
		List<IStaticFieldReflection> fields = cr.getStaticFields();
		Assert.assertEquals(2, fields.size());
		Assert.assertEquals("CONSTANT", fields.get(0).getName());
		Assert.assertEquals("STATIC", fields.get(1).getName());
	}

}
