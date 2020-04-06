package ru.reflexio;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

abstract class ArrayConstructorReflection<T> extends VirtualReflection implements IConstructorReflection<T> {

    static class Empty<T> extends ArrayConstructorReflection<T> {

        Empty(Class<?> arrayType) {
            super(arrayType);
        }

        @Override
        public List<IParameterReflection> getParameters() {
            return new ArrayList<>();
        }

        @Override
        public boolean canInvoke(Class<?>... types) {
            return types.length == 0;
        }

    }

    static class Length<T> extends ArrayConstructorReflection<T> {

        private final VirtualParameterReflection parameter = new VirtualParameterReflection(int.class, "length");

        Length(Class<?> arrayType) {
            super(arrayType);
        }

        @Override
        public List<IParameterReflection> getParameters() {
            List<IParameterReflection> result = new ArrayList<>();
            result.add(parameter);
            return result;
        }

        @Override
        public boolean canInvoke(Class<?>... types) {
            return types.length == 1 && (types[0] == int.class || types[0] == Integer.class);
        }

    }

    private final Class<?> arrayType;

    ArrayConstructorReflection(Class<?> arrayType) {
        if (arrayType == null || !arrayType.isArray()) {
            throw new IllegalArgumentException();
        }
        this.arrayType = arrayType;
    }

    @Override
    public AccessType getAccessType() {
        return AccessType.Public;
    }

    @Override
    public String getName() {
        return arrayType.getName();
    }

    @Override
    public Class<?> getType() {
        return arrayType;
    }

    @Override
    public int hashCode() {
        return arrayType.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ArrayConstructorReflection.Length) {
            return Objects.equals(arrayType, ((Length<?>) obj).getType());
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T invoke(Object... args) {
        if (args.length == 0) {
            return (T) Array.newInstance(arrayType.getComponentType(), 0);
        } else if (args.length == 1 && args[0] instanceof Integer) {
            return (T) Array.newInstance(arrayType.getComponentType(), (Integer) args[0]);
        } else {
            throw new IllegalArgumentException();
        }
    }

}
