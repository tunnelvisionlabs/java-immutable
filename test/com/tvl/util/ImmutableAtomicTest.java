// Licensed under the MIT license. See LICENSE file in the project root for full license information.
package com.tvl.util;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.google.common.util.concurrent.Uninterruptibles;
import com.tvl.util.function.BiFunction;
import com.tvl.util.function.Function;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ImmutableAtomicTest {
    private interface Consumer<T> {
        void accept(T t);
    }

    private interface UnaryOperator<T> extends Function<T, T> {
    }

    private interface UpdateFunction<T> {
        boolean apply(AtomicReference<T> location, Function<T, T> transformer);
    }

    @Test
    public void update_StartWithNull() {
        updateHelper(new Consumer<UpdateFunction<ImmutableTreeList<Integer>>>() {
            @Override
            public void accept(UpdateFunction<ImmutableTreeList<Integer>> func) {
                AtomicReference<ImmutableTreeList<Integer>> list = new AtomicReference<ImmutableTreeList<Integer>>(null);
                assertTrue(func.apply(
                    list,
                    new UnaryOperator<ImmutableTreeList<Integer>>() {
                        @Override
                        public ImmutableTreeList<Integer> apply(ImmutableTreeList<Integer> l) {
                            assertNull(l);
                            return ImmutableTreeList.create(1);
                        }
                    }));
                assertEquals(1, list.get().size());
                assertEquals(1, (int)list.get().get(0));
            }
        });
    }

    @Test
    public void update_IncrementalUpdate() {
        updateHelper(new Consumer<UpdateFunction<ImmutableTreeList<Integer>>>() {
            @Override
            public void accept(UpdateFunction<ImmutableTreeList<Integer>> func) {
                AtomicReference<ImmutableTreeList<Integer>> list = new AtomicReference<ImmutableTreeList<Integer>>(ImmutableTreeList.create(1));
                assertTrue(func.apply(
                    list,
                    new UnaryOperator<ImmutableTreeList<Integer>>() {
                        @Override
                        public ImmutableTreeList<Integer> apply(ImmutableTreeList<Integer> l) {
                            return l.add(2);
                        }
                    }));
                assertEquals(2, list.get().size());
                assertEquals(1, (int)list.get().get(0));
                assertEquals(2, (int)list.get().get(1));
            }
        });
    }

    @Test
    public void update_FuncThrowsThrough() {
        updateHelper(new Consumer<UpdateFunction<ImmutableTreeList<Integer>>>() {
            @Override
            public void accept(UpdateFunction<ImmutableTreeList<Integer>> func) {
                AtomicReference<ImmutableTreeList<Integer>> list = new AtomicReference<ImmutableTreeList<Integer>>(ImmutableTreeList.create(1));
                final UnsupportedOperationException expected = new UnsupportedOperationException();
                try {
                    func.apply(
                        list,
                        new UnaryOperator<ImmutableTreeList<Integer>>() {
                            @Override
                            public ImmutableTreeList<Integer> apply(ImmutableTreeList<Integer> l) {
                                throw expected;
                            }
                        });
                    fail("Expected an exception");
                } catch (UnsupportedOperationException ex) {
                    assertSame(expected, ex);
                }
            }
        });
    }

    @Test
    public void update_NoEffectualChange() {
        updateHelper(new Consumer<UpdateFunction<ImmutableTreeList<Integer>>>() {
            @Override
            public void accept(UpdateFunction<ImmutableTreeList<Integer>> func) {
                AtomicReference<ImmutableTreeList<Integer>> list = new AtomicReference<ImmutableTreeList<Integer>>(ImmutableTreeList.create(1));
                assertFalse(func.apply(
                    list,
                    new UnaryOperator<ImmutableTreeList<Integer>>() {
                        @Override
                        public ImmutableTreeList<Integer> apply(ImmutableTreeList<Integer> l) {
                            return l;
                        }
                    }));
            }
        });
    }

    @Test
    public void update_HighConcurrency() {
        final ListeningExecutorService executor = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
        try {
            updateHelper(new Consumer<UpdateFunction<ImmutableTreeList<Integer>>>() {
                @Override
                public void accept(final UpdateFunction<ImmutableTreeList<Integer>> func) {
                    final AtomicReference<ImmutableTreeList<Integer>> list = new AtomicReference<ImmutableTreeList<Integer>>(ImmutableTreeList.<Integer>create());
                    int concurrencyLevel = Runtime.getRuntime().availableProcessors();
                    final int iterations = 500;
                    List<ListenableFuture<Void>> tasks = new ArrayList<ListenableFuture<Void>>();
                    for (int i = 0; i < concurrencyLevel; i++) {
                        tasks.add(null);
                    }

                    final CountDownLatch latch = new CountDownLatch(tasks.size());
                    for (int i = 0; i < tasks.size(); i++) {
                        tasks.set(i, executor.<Void>submit(new Runnable() {
                            @Override
                            public void run() {
                                // Maximize concurrency by blocking this thread until all the other threads are ready to go as well.
                                latch.countDown();
                                Uninterruptibles.awaitUninterruptibly(latch);

                                for (int j = 0; j < iterations; j++) {
                                    assertTrue(func.apply(
                                        list,
                                        new UnaryOperator<ImmutableTreeList<Integer>>() {
                                            @Override
                                            public ImmutableTreeList<Integer> apply(ImmutableTreeList<Integer> l) {
                                                return l.add(l.size());
                                            }
                                        }));
                                }
                            }
                        }, null));
                    }

                    try {
                        Uninterruptibles.getUninterruptibly(Futures.allAsList(tasks));
                    } catch (ExecutionException ex) {
                        throw new UncheckedExecutionException(ex);
                    }

                    assertEquals(concurrencyLevel * iterations, list.get().size());
                    for (int i = 0; i < list.get().size(); i++) {
                        assertEquals(i, (int)list.get().get(i));
                    }
                }
            });
        } finally {
            executor.shutdownNow();
        }
    }

//        [Fact]
//        public void Update_CarefullyScheduled()
//        {
//            UpdateHelper<ImmutableHashSet<int>>(func =>
//            {
//                var set = ImmutableHashSet.Create<int>();
//                var task2TransformEntered = new AutoResetEvent(false);
//                var task1TransformExited = new AutoResetEvent(false);
//
//                var task1 = Task.Run(delegate
//                {
//                    int transform1ExecutionCounter = 0;
//                    func(
//                        ref set,
//                        s =>
//                        {
//                            Assert.Equal(1, ++transform1ExecutionCounter);
//                            task2TransformEntered.WaitOne();
//                            return s.Add(1);
//                        });
//                    task1TransformExited.Set();
//                    Assert.Equal(1, transform1ExecutionCounter);
//                });
//
//                var task2 = Task.Run(delegate
//                {
//                    int transform2ExecutionCounter = 0;
//                    func(
//                        ref set,
//                        s =>
//                        {
//                            switch (++transform2ExecutionCounter)
//                            {
//                                case 1:
//                                    task2TransformEntered.Set();
//                                    task1TransformExited.WaitOne();
//                                    Assert.True(s.IsEmpty);
//                                    break;
//                                case 2:
//                                    Assert.True(s.Contains(1));
//                                    Assert.Equal(1, s.Count);
//                                    break;
//                            }
//
//                            return s.Add(2);
//                        });
//
//                    // Verify that this transform had to execute twice.
//                    Assert.Equal(2, transform2ExecutionCounter);
//                });
//
//                // Wait for all tasks and rethrow any exceptions.
//                Task.WaitAll(task1, task2);
//                Assert.Equal(2, set.Count);
//                Assert.True(set.Contains(1));
//                Assert.True(set.Contains(2));
//            });
//        }

    @Test
    public void getOrAddMapValue() {
        AtomicReference<ImmutableHashMap<Integer, String>> map = new AtomicReference<ImmutableHashMap<Integer, String>>(ImmutableHashMap.<Integer, String>create());
        String value = ImmutableAtomic.getOrAdd(map, 1, "a");
        assertEquals("a", value);
        value = ImmutableAtomic.getOrAdd(map, 1, "b");
        assertEquals("a", value);
    }

    @Test
    public void getOrAddMapValueFactory() {
        AtomicReference<ImmutableHashMap<Integer, String>> map = new AtomicReference<ImmutableHashMap<Integer, String>>(ImmutableHashMap.<Integer, String>create());
        String value = ImmutableAtomic.getOrAdd(
            map,
            1,
            new Function<Integer, String>() {
                @Override
                public String apply(Integer key) {
                    assertEquals(1, (int)key);
                    return "a";
                }
            });
        assertEquals("a", value);
        value = ImmutableAtomic.getOrAdd(
            map,
            1,
            new Function<Integer, String>() {
                @Override
                public String apply(Integer key) {
                    fail("should never be invoked");
                    return "b";
                }
            });
        assertEquals("a", value);
    }

    @Test
    public void getOrAddMapValueFactoryWithArg() {
        AtomicReference<ImmutableHashMap<Integer, String>> map = new AtomicReference<ImmutableHashMap<Integer, String>>(ImmutableHashMap.<Integer, String>create());
        String value = ImmutableAtomic.getOrAdd(
            map,
            1,
            new BiFunction<Integer, Boolean, String>() {
                @Override
                public String apply(Integer key, Boolean arg) {
                    assertTrue(arg);
                    assertEquals(1, (int)key);
                    return "a";
                }
            },
            true);
        assertEquals("a", value);
        value = ImmutableAtomic.getOrAdd(
            map,
            1,
            new BiFunction<Integer, Boolean, String>() {
                @Override
                public String apply(Integer key, Boolean arg) {
                    fail("should never be invoked");
                    return "b";
                }
            },
            true);
        assertEquals("a", value);
    }

    @Test
    public void addOrUpdateMapAddValue() {
        AtomicReference<ImmutableHashMap<Integer, String>> map = new AtomicReference<ImmutableHashMap<Integer, String>>(ImmutableHashMap.<Integer, String>create());
        String value = ImmutableAtomic.addOrUpdate(
            map,
            1,
            "a",
            new BiFunction<Integer, String, String>() {
                @Override
                public String apply(Integer k, String v) {
                    fail();
                    return "b";
                }
            });
        assertEquals("a", value);
        assertEquals("a", map.get().get(1));

        value = ImmutableAtomic.addOrUpdate(
            map,
            1,
            "c",
            new BiFunction<Integer, String, String>() {
                @Override
                public String apply(Integer k, String v) {
                    assertEquals("a", v);
                    return "b";
                }
            });
        assertEquals("b", value);
        assertEquals("b", map.get().get(1));
    }

    @Test
    public void addOrUpdateMapAddValueFactory() {
        AtomicReference<ImmutableHashMap<Integer, String>> map = new AtomicReference<ImmutableHashMap<Integer, String>>(ImmutableHashMap.<Integer, String>create());
        String value = ImmutableAtomic.addOrUpdate(
            map,
            1,
            new Function<Integer, String>() {
                @Override
                public String apply(Integer k) {
                    return "a";
                }
            },
            new BiFunction<Integer, String, String>() {
                @Override
                public String apply(Integer k, String v) {
                    fail();
                    return "b";
                }
            });
        assertEquals("a", value);
        assertEquals("a", map.get().get(1));

        value = ImmutableAtomic.addOrUpdate(
            map,
            1,
            new Function<Integer, String>() {
                @Override
                public String apply(Integer k) {
                    fail();
                    return "c";
                }
            },
            new BiFunction<Integer, String, String>() {
                @Override
                public String apply(Integer k, String v) {
                    assertEquals("a", v);
                    return "b";
                }
            });
        assertEquals("b", value);
        assertEquals("b", map.get().get(1));
    }

    @Test
    public void tryAddMap() {
        AtomicReference<ImmutableHashMap<Integer, String>> map = new AtomicReference<ImmutableHashMap<Integer, String>>(ImmutableHashMap.<Integer, String>create());

        assertTrue(ImmutableAtomic.tryAdd(map, 1, "a"));
        assertEquals("a", map.get().get(1));

        assertFalse(ImmutableAtomic.tryAdd(map, 1, "a"));
        assertFalse(ImmutableAtomic.tryAdd(map, 1, "b"));
        assertEquals("a", map.get().get(1));
    }

    @Test
    public void tryUpdateMap() {
        AtomicReference<ImmutableHashMap<Integer, String>> map = new AtomicReference<ImmutableHashMap<Integer, String>>(ImmutableHashMap.<Integer, String>create());

        // missing
        ImmutableHashMap<Integer, String> before = map.get();
        assertFalse(ImmutableAtomic.tryUpdate(map, 1, "b", "a"));
        assertSame(before, map.get());

        // mismatched existing value
        map.set(map.get().put(1, "b"));
        before = map.get();
        assertFalse(ImmutableAtomic.tryUpdate(map, 1, "c", "a"));
        assertSame(before, map.get());

        // match
        assertTrue(ImmutableAtomic.tryUpdate(map, 1, "b", "c"));
        assertNotSame(before, map.get());
        assertEquals("c", map.get().get(1));
    }

    @Test
    public void tryRemoveMap() {
        AtomicReference<ImmutableHashMap<Integer, String>> map = new AtomicReference<ImmutableHashMap<Integer, String>>(ImmutableHashMap.<Integer, String>create());

        assertNull(ImmutableAtomic.tryRemove(map, 1));

        map.set(map.get().add(1, "a"));
        assertEquals("a", ImmutableAtomic.tryRemove(map, 1));
        assertTrue(map.get().isEmpty());
    }

    @Test
    public void pushStack() {
        AtomicReference<ImmutableLinkedStack<Integer>> stack = new AtomicReference<ImmutableLinkedStack<Integer>>(ImmutableLinkedStack.<Integer>create());
        ImmutableAtomic.push(stack, 5);
        assertFalse(stack.get().isEmpty());
        assertEquals(5, (int)stack.get().peek());
        assertTrue(stack.get().pop().isEmpty());

        ImmutableAtomic.push(stack, 8);
        assertEquals(8, (int)stack.get().peek());
        assertEquals(5, (int)stack.get().pop().peek());
    }

    @Test
    public void tryPopStack() {
        AtomicReference<ImmutableLinkedStack<Integer>> stack = new AtomicReference<ImmutableLinkedStack<Integer>>(ImmutableLinkedStack.<Integer>create());

        assertNull(ImmutableAtomic.tryPop(stack));
        stack.set(stack.get().push(2).push(3));
        assertEquals(3, (int)ImmutableAtomic.tryPop(stack));
        assertEquals(2, (int)stack.get().peek());
        assertTrue(stack.get().pop().isEmpty());
    }

    @Test
    public void tryPollQueue() {
        AtomicReference<ImmutableLinkedQueue<Integer>> queue = new AtomicReference<ImmutableLinkedQueue<Integer>>(ImmutableLinkedQueue.<Integer>create());
        assertNull(ImmutableAtomic.tryPoll(queue));

        queue.set(queue.get().add(1).add(2));
        assertEquals(1, (int)ImmutableAtomic.tryPoll(queue));
        assertEquals(2, (int)ImmutableAtomic.tryPoll(queue));
        assertNull(ImmutableAtomic.tryPoll(queue));
    }

    @Test
    public void addQueue() {
        AtomicReference<ImmutableLinkedQueue<Integer>> queue = new AtomicReference<ImmutableLinkedQueue<Integer>>(ImmutableLinkedQueue.<Integer>create());
        ImmutableAtomic.add(queue, 1);
        assertEquals(1, (int)queue.get().peek());
        assertTrue(queue.get().poll().isEmpty());

        ImmutableAtomic.add(queue, 2);
        assertEquals(1, (int)queue.get().peek());
        assertEquals(2, (int)queue.get().poll().peek());
        assertTrue(queue.get().poll().poll().isEmpty());
    }

    /**
     * Executes a test against both {@link ImmutableAtomic#update(AtomicReference, Function)} and
     * {@link ImmutableAtomic#update(AtomicReference, BiFunction, Object)}.
     *
     * @param <T> The type of value under test.
     * @param test The test to execute. Invoke the parameter instead of calling the {@link ImmutableAtomic} method so
     * that the delegate can test both overloads by being executed twice.
     */
    private static <T> void updateHelper(Consumer<UpdateFunction<T>> test) {
        test.accept(new UpdateFunction<T>() {
            @Override
            public boolean apply(AtomicReference<T> location, Function<T, T> transformer) {
                return ImmutableAtomic.update(location, transformer);
            }
        });
        test.accept(new UpdateFunction<T>() {
            @Override
            public boolean apply(AtomicReference<T> location, Function<T, T> transformer) {
                return updateWrapper(location, transformer);
            }
        });
    }

    /**
     * A wrapper that makes one overload look like another so the same test delegate can execute against both.
     *
     * @param <T> The type of value being changed.
     * @param location The variable or field to be changed.
     * @param transformer The function that transforms the value.
     * @return The result of the replacement function.
     */
    private static <T> boolean updateWrapper(AtomicReference<T> location, final Function<T, T> transformer) {
        return ImmutableAtomic.update(
            location,
            new BiFunction<T, Integer, T>() {
                @Override
                public T apply(T t, Integer u) {
                    assertEquals(1, (int)u);
                    return transformer.apply(t);
                }
            },
            1);
    }
}
