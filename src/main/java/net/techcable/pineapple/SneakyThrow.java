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
package net.techcable.pineapple;

import net.techcable.pineapple.function.CheckedRunnable;

import java.util.concurrent.Callable;

public final class SneakyThrow {
    private SneakyThrow() {
    }

    public static AssertionError sneakyThrow(Throwable t) {
        return sneakyThrow0(t);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> AssertionError sneakyThrow0(Throwable t) throws T {
        throw (T) t;
    }

    public static <V> V sneakyThrows(Callable<V> callable) {
        try {
            return callable.call();
        } catch (Throwable t) {
            throw sneakyThrow0(t);
        }
    }


    @SuppressWarnings({"rawtypes", "unused"})
    public static void sneakyThrows(CheckedRunnable runnable) {
        try {
            runnable.run();
        } catch (Throwable t) {
            throw sneakyThrow0(t);
        }
    }
}
