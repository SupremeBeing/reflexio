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

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class FieldReflectionTest {

	@Test
	public void testGetValue() {
		GrandChild grandChild = new GrandChild(10);
		TypeReflection<GrandChild> cr = new TypeReflection<>(GrandChild.class);
		IInstanceFieldReflection field = cr.findInstanceField("code");
		Object value = field.getValue(grandChild);
		Assert.assertNotNull(value);
		Assert.assertTrue(value instanceof Integer);
		Assert.assertEquals(10, value);
	}
	
	@Test(expected = RuntimeException.class)
	public void testGetInvalidField() {
		TypeReflection<GrandChild> cr = new TypeReflection<>(GrandChild.class);
		IStaticFieldReflection field = cr.findStaticField("code");
		field.getValue();
	}

	@Test
	public void testGetStaticField() {
		TypeReflection<GrandChild> cr = new TypeReflection<>(GrandChild.class);
		IStaticFieldReflection field = cr.findStaticField("CONSTANT");
		Object value = field.getValue();
		Assert.assertNotNull(value);
		Assert.assertTrue(value instanceof String);
		Assert.assertEquals("the name", value);
	}
	
	@Test
	public void testFindShadowField() {
		TypeReflection<Child> cr = new TypeReflection<>(Child.class);
		GrandChild grandChild = new GrandChild("visible");
		grandChild.setMessage("shadowed");
		IInstanceFieldReflection field = cr.findInstanceField("message");
		Assert.assertNotNull(field);
		Object value = field.getValue(grandChild);
		Assert.assertEquals("shadowed", value);
	}
	
	@Test
	public void testSetValue() {
		GrandChild grandChild = new GrandChild(10);
		TypeReflection<GrandChild> cr = new TypeReflection<>(GrandChild.class);
		IInstanceFieldReflection field = cr.findInstanceField("code");
		field.setValue(grandChild, 20);
		Assert.assertEquals(20, grandChild.getCode());
	}
	
	@Test(expected = RuntimeException.class)
	public void testSetInvalidField() {
		TypeReflection<GrandChild> cr = new TypeReflection<>(GrandChild.class);
		IInstanceFieldReflection field = cr.findInstanceField("code");
		field.setValue(null, 0);
	}

	@Test(expected = RuntimeException.class)
	public void testSetInvalidTypeField() {
		TypeReflection<GrandChild> cr = new TypeReflection<>(GrandChild.class);
		GrandChild grandChild = new GrandChild(10);
		IInstanceFieldReflection field = cr.findInstanceField("code");
		field.setValue(grandChild, "string");
	}

	@Test(expected = RuntimeException.class)
	public void testSetStaticFinalField() {
		TypeReflection<GrandChild> cr = new TypeReflection<>(GrandChild.class);
		IStaticFieldReflection field = cr.findStaticField("CONSTANT");
		field.setValue("ALTERED");
	}

	@Test
	public void testSetFinalField() {
		TypeReflection<GrandChild> cr = new TypeReflection<>(GrandChild.class);
		GrandChild grandChild = new GrandChild(10);
		IInstanceFieldReflection field = cr.findInstanceField("message");
		field.setValue(grandChild, "Altered");
		Assert.assertEquals("Altered", grandChild.getMessage());
	}

	@Test
	public void testSetStaticField() {
		TypeReflection<GrandChild> cr = new TypeReflection<>(GrandChild.class);
		IStaticFieldReflection field = cr.findStaticField("STATIC");
		field.setValue(200);
		Assert.assertEquals(200, GrandChild.STATIC);
	}

	@Test
	public void testGenericType() {
		TypeReflection<GrandChild> cr = new TypeReflection<>(GrandChild.class);
		IInstanceFieldReflection field = cr.findInstanceField("map");
		List<ITypeReflection<?>> generics = field.getGenericClasses();
		Assert.assertEquals(2, generics.size());
		Assert.assertEquals(String.class, generics.get(0).getType());
		Assert.assertEquals(Parent.class, generics.get(1).getType());
	}
}
