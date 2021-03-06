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

import com.google.common.base.Verify;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;

import static net.techcable.pineapple.reflection.Reflection.UNSAFE;

@SuppressWarnings("restriction")
/* package */ final class UnsafeInstanceReferenceField<T, V> extends UnsafePineappleField<T, V> {
    /* package */
    @SuppressWarnings("UnstableApiUsage")
    UnsafeInstanceReferenceField(Field field) {
        super(field);
        Verify.verify(!field.getType().isPrimitive());
        Verify.verify(!Modifier.isStatic(field.getModifiers()));
    }

    @Override
    @SuppressWarnings("unchecked") // I do solemly swear not to crash the VM
    public V get(T instance) {
        /*
         * A null instance will not error or crash the VM, but will fail silently (like in C).
         * The `fieldOffset` will be treated as an absolute memory address instead of an offset.
         * This will return seemingly random results, without any detectable error or cause.
         * It also presents a potential security vulnerability,
         * since it allows attackers to read from arbitrary memory addresses.
         * Insert a manual check here, and hope the JIT can optimize it away.
         */
        Objects.requireNonNull(instance, "Null instance");
        /*
         * Check that the instance is instanceof the declaringClass,
         * in order to maintain the type safety guarantees of the JVM.
         * Unsafe doesn't check the type at all,
         * and will simply read from whatever memory location we give,
         * regardless of the type of the object we pass.
         * Although Class.cast uses reflection in the source code,
         * it's actually a very fast intrinsic in the JIT.
         */
        this.declaringClass.cast(instance);
        /*
         * It's perfectly safe that we don't check the object returned by this method.
         * Because of erasure, the return type of this method is technically 'Object'.
         * The caller will have a automatic cast inserted by the compiler,
         * which will catch any issues here.
         */
        return (V) Objects.requireNonNull(UNSAFE).getObject(instance, this.fieldOffset);
    }

    @Override
    public V getBoxed(T instance) {
        return this.get(instance);
    }

    @Override
    public void forcePut(T instance, @Nullable V value) {
        /*
         * A null instance will not error or crash the VM, but will fail silently (like in C).
         * The `fieldOffset` will be treated as an absolute memory address instead of an offset.
         * This will return seemingly random results, without any detectable error or cause.
         * It also presents a potential security vulnerability,
         * since it allows attackers to read from arbitrary memory addresses.
         * Insert a manual check here, and hope the JIT can optimize it away.
         */
        Objects.requireNonNull(instance, "Null instance");
        /*
         * Check that the instance is instanceof the declaringClass,
         * in order to maintain the type safety guarantees of the JVM.
         * Unsafe doesn't check the type at all,
         * and will simply read from whatever memory location we give,
         * regardless of the type of the object we pass.
         * Although Class.cast uses reflection in the source code,
         * it's actually a very fast intrinsic in the JIT.
         */
        this.declaringClass.cast(instance);
        this.fieldType.cast(value);
        Objects.requireNonNull(UNSAFE).putObject(instance, this.fieldOffset, value);
    }

    @Override
    public void forcePutBoxed(T instance, @Nullable V value) {
        this.forcePut(instance, value);
    }
}
