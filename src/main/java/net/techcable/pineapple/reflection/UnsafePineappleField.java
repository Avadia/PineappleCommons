/*
  The MIT License
  Copyright (c) 2016 Techcable

  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:

  The above copyright notice and this permission notice shall be included in
  all copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
  THE SOFTWARE.
 */
package net.techcable.pineapple.reflection;

import sun.misc.Unsafe;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static net.techcable.pineapple.reflection.Reflection.UNSAFE;

/**
 * Abstract type for all fields implemented using {@link sun.misc.Unsafe}, with default implementations.
 * <p>
 * Subclasses need to override any methods that could possibly work for their type.
 * All types must implement either `getBoxed()` or `getBoxedStatic()`,
 * depending on if they're a static field type.
 * </p>
 */
@SuppressWarnings("restriction")
/* package */ abstract class UnsafePineappleField<T, V> extends PineappleField<T, V> {
    @Nullable
    /* package */ final Object fieldBase;
    /* package */ final long fieldOffset;

    /* package */ UnsafePineappleField(Field field) {
        this(
                field,
                Modifier.isStatic(field.getModifiers())
                        ? Objects.requireNonNull(UNSAFE).staticFieldOffset(field)
                        : Objects.requireNonNull(UNSAFE).objectFieldOffset(field)
        );
    }

    private UnsafePineappleField(Field field, long fieldOffset) {
        super(field);
        checkArgument(fieldOffset != Unsafe.INVALID_FIELD_OFFSET, "Invalid field offset: " + fieldOffset);
        this.fieldOffset = fieldOffset;
        this.fieldBase = isStatic() ? Objects.requireNonNull(UNSAFE).staticFieldBase(field) : null;
    }

    @Override
    public V get(T instance) {
        checkState(!this.isPrimitive(), "Field is primitive!");
        checkState(!this.isStatic(), "Field is static!");
        throw new UnsupportedOperationException(getClass().getTypeName());
    }

    @Override
    public V getStatic() {
        checkState(!this.isPrimitive(), "Field is primitive!");
        checkState(this.isStatic(), "Field is not static!");
        throw new UnsupportedOperationException(getClass().getTypeName());
    }

    @Override
    public int getInt(T instance) {
        checkState(this.primitiveType == PrimitiveType.INT, "Field isn't a primitive integer!");
        checkState(!this.isStatic(), "Field is static!");
        throw new UnsupportedOperationException(getClass().getTypeName());
    }

    @Override
    public int getStaticInt() {
        checkState(this.primitiveType == PrimitiveType.INT, "Field isn't a primitive integer!");
        checkState(this.isStatic(), "Field is not static!");
        throw new UnsupportedOperationException(getClass().getTypeName());
    }

    @Override
    public void forcePutStaticBoxed(@Nullable V value) {
        checkState(this.isStatic(), "Field is not static!");
        throw new UnsupportedOperationException(getClass().getTypeName());
    }

    @Override
    public void forcePutBoxed(T instance, @Nullable V value) {
        checkState(!this.isStatic(), "Field is static!");
        throw new UnsupportedOperationException(getClass().getTypeName());
    }

    @Override
    public void forcePut(T instance, @Nullable V value) {
        checkState(!this.isPrimitive(), "Field is primitive!");
        checkState(!this.isStatic(), "Field is static!");
        throw new UnsupportedOperationException(getClass().getTypeName());
    }

    @Override
    public void forcePutStatic(@Nullable V value) {
        checkState(!this.isPrimitive(), "Field is primitive!");
        checkState(this.isStatic(), "Field is not static!");
        throw new UnsupportedOperationException(getClass().getTypeName());
    }

    @Override
    public void forcePutInt(T instance, int value) {
        checkState(this.primitiveType == PrimitiveType.INT, "Field isn't a primitive integer!");
        checkState(!this.isStatic(), "Field is static!");
        throw new UnsupportedOperationException(getClass().getTypeName());
    }

    @Override
    public void forcePutStaticInt(int value) {
        checkState(this.primitiveType == PrimitiveType.INT, "Field isn't a primitive integer!");
        checkState(this.isStatic(), "Field is not static!");
        throw new UnsupportedOperationException(getClass().getTypeName());
    }

    @Override
    public V getBoxed(T instance) {
        checkState(!this.isStatic(), "Field is static!");
        throw new UnsupportedOperationException(
                "Type "
                        + getClass()
                        + " didn't implement getBoxed(), even though it's an instance field!"
        );
    }

    @Override
    public V getStaticBoxed() {
        checkState(this.isStatic(), "Field is not static!");
        throw new UnsupportedOperationException(
                "Type "
                        + getClass()
                        + " didn't implement getBoxed(), even though it's a static field!"
        );
    }
}
