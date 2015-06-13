# High-Level Type Mapping

## Imported Interfaces

| .NET Type | Java Type |
| --- | --- |
| `System.Collections.Generic.IReadOnlyCollection<T>` | `com.tvl.util.ReadOnlyCollection<T>` |
| `System.Collections.Generic.IReadOnlyDictionary<TKey, TValue>` | `com.tvl.util.ReadOnlyMap<K, V>` |
| `System.Collections.Generic.IReadOnlyList<T>` | `com.tvl.util.ReadOnlyList<T>` |

## Immutable Collections Interfaces

| .NET Type | Java Type |
| --- | --- |
| `System.Collections.Immutable.IImmutableDictionary<TKey, TValue>` | `com.tvl.util.ImmutableMap<K, V>` |
| `System.Collections.Immutable.IImmutableList<T>` | `com.tvl.util.ImmutableList<T>` |
| `System.Collections.Immutable.IImmutableQueue<T>` | `com.tvl.util.ImmutableQueue<T>` |
| `System.Collections.Immutable.IImmutableSet<T>` | `com.tvl.util.ImmutableSet<T>` |
| `System.Collections.Immutable.IImmutableStack<T>` | `com.tvl.util.ImmutableStack<T>` |

## Immutable Collections

| .NET Type | Java Type |
| --- | --- |
| `System.Collections.Immutable.ImmutableArray<T>` | `com.tvl.util.ImmutableArrayList<T>` |
| `System.Collections.Immutable.ImmutableDictionary<TKey, TValue>` | `com.tvl.util.ImmutableHashMap<K, V>` |
| `System.Collections.Immutable.ImmutableHashSet<T>` | `com.tvl.util.ImmutableHashSet<T>` |
| `System.Collections.Immutable.ImmutableList<T>` | `com.tvl.util.ImmutableTreeList<T>` |
| `System.Collections.Immutable.ImmutableQueue<T>` | `com.tvl.util.ImmutableLinkedQueue<T>` |
| `System.Collections.Immutable.ImmutableSortedDictionary<TKey, TValue>` | `com.tvl.util.ImmutableTreeMap<K, V>` |
| `System.Collections.Immutable.ImmutableSortedSet<T>` | `com.tvl.util.ImmutableTreeSet<T>` |
| `System.Collections.Immutable.ImmutableStack<T>` | `com.tvl.util.ImmutableLinkedStack<T>` |

## Immutable Collection Factories

| .NET Type | Java Type |
| --- | --- |
| `System.Collections.Immutable.ImmutableArray` | `com.tvl.util.ImmutableArrayList<T>` |
| `System.Collections.Immutable.ImmutableDictionary` | `com.tvl.util.ImmutableHashMap<K, V>` |
| `System.Collections.Immutable.ImmutableHashSet` | `com.tvl.util.ImmutableHashSet<T>` |
| `System.Collections.Immutable.ImmutableList` | `com.tvl.util.ImmutableTreeList<T>` |
| `System.Collections.Immutable.ImmutableQueue` | `com.tvl.util.ImmutableLinkedQueue<T>` |
| `System.Collections.Immutable.ImmutableSortedDictionary` | `com.tvl.util.ImmutableTreeMap<K, V>` |
| `System.Collections.Immutable.ImmutableSortedSet` | `com.tvl.util.ImmutableTreeSet<T>` |
| `System.Collections.Immutable.ImmutableStack` | `com.tvl.util.ImmutableLinkedStack<T>` |

## Immutable Collection Utilities

| .NET Type | Java Type |
| --- | --- |
| `System.Collections.Immutable.ImmutableInterlocked` | *unknown* |
| `System.Linq.ImmutableArrayExtensions` | *unknown* |

# Detailed API Mapping

Throughout the API mapping details, the presence of a check mark (&check;) in the **Notes** column indicates APIs which
have already been implemented (with documentation).

## `ImmutableArray<T>` &rarr; `ImmutableArrayList<T>`

### Factory

#### `ImmutableArray` &rarr; `ImmutableArrayList<T>`

| .NET Member | Java Member | Notes |
| --- | --- | --- |
| `Create<T>()` | `<T>create()` | &check; |
| `Create<T>(T)` | `<T>create(T)` | &check; |
| `Create<T>(T, T)` | `<T>create(T, T)` | &check; |
| `Create<T>(T, T, T)` | `<T>create(T, T, T)` | &check; |
| `Create<T>(T, T, T, T)` | `<T>create(T, T, T, T)` | &check; |
| `Create<T>(params T[])` | `<T>create(T...)` | &check; |
| `Create<T>(T[], int start, int length)` | `<T>createAll(T[], int fromIndex, int toIndex)` | &check; 1, 2 |
| `Create<T>(ImmutableArray<T>, int start, int length)` | `<T>createAll(ImmutableArrayList<T>, int fromIndex, int toIndex)` | &check; 1, 2 |
| `CreateRange<T>(IEnumerable<T>)` | `<T>createAll(Iterable<? extends T>)` | &check; |
| `CreateRange<TSource, TResult>(ImmutableArray<TSource>, Func<TSource, TResult>)` | `<Source, Result>createAll(ImmutableArrayList<Source>, Function<? super Source, Result>)` | &check; |
| `CreateRange<TSource, TResult>(ImmutableArray<TSource>, int start, int length, Func<TSource, TResult>)` | `<Source, Result>createAll(ImmutableArrayList<Source>, int fromIndex, int toIndex, Function<? super Source, Result>)` | &check; 1 |
| `CreateRange<TSource, TArg, TResult>(ImmutableArray<TSource>, Func<TSource, TArg, TResult>, TArg)` | `<Source, Arg, Result>createAll(ImmutableArrayList<Source>, BiFunction<? super Source, Arg, Result>, Arg)` | &check; |
| `CreateRange<TSource, TArg, TResult>(ImmutableArray<TSource>, int start, int length, Func<TSource, TArg, TResult>, TArg)` | `<Source, Arg, Result>createAll(ImmutableArrayList<Source>, int fromIndex, int toIndex, BiFunction<? super Source, Arg, Result>, Arg)` | &check; 1 |
| `CreateBuilder<T>()` | `<T>createBuilder()` | &check; |
| `CreateBuilder<T>(int)` | `<T>createBuilder(int)` | &check; |
| `BinarySearch<T>(ImmutableArray<T>, T)` | `<T>binarySearch(ImmutableArrayList<T>, T)` | &check; |
| `BinarySearch<T>(ImmutableArray<T>, T, IComparer<T>)` | `<T>binarySearch(ImmutableArrayList<T>, T, Comparator<? super T>)` | &check; |
| `BinarySearch<T>(ImmutableArray<T>, int start, int length, T)` | `<T>binarySearch(ImmutableArrayList<T>, int fromIndex, int toIndex, T)` | &check; 1 |
| `BinarySearch<T>(ImmutableArray<T>, int start, int length, T, IComparer<T>)` | `<T>binarySearch(ImmutableArrayList<T>, int fromIndex, int toIndex, T, Comparator<? super T>)` | &check; 1 |

¹ Java convention is to use fromIndex/toIndex instead of start/length for ranges.<br>
² These methods were renamed to `createAll` to avoid conflicts with `create(T...)`.

#### `ImmutableArray` &rarr; `Immutables`

| .NET Member | Java Member | Notes |
| --- | --- | --- |
| `ToImmutableArray<TSource>(this IEnumerable<TSource>)` | `<T>toImmutableArrayList(Iterable<? extends T>)` | &check; |

### Collection

#### `ImmutableArray<T>` &rarr; `ImmutableArrayList<T>`

| .NET Member | Java Member | Notes |
| --- | --- | --- |
| `Empty` | `<T>empty()` | &check; |
| `this[int]` | `get(int)` | &check; |
| `IsEmpty` | `isEmpty()` | &check; |
| `Length` | `size()` | &check; |
| `IndexOf(T)` | `indexOf(T)` | |
| `IndexOf(T, int start)` | `indexOf(T, int fromIndex)` | |
| `IndexOf(T, int start, int length)` | `indexOf(T, int fromIndex, int toIndex)` | |
| `IndexOf(T, int start, int length, IEqualityComparer<T>)` | `indexOf(T, int fromIndex, int toIndex, EqualityComparator<? super T>)` | |
| `LastIndexOf(T)` | `lastIndexOf(T)` | |
| `LastIndexOf(T, int start)` | `lastIndexOf(T, int start)` | |
| `LastIndexOf(T, int start, int length)` | `lastIndexOf(T, int start, int end)` | |
| `LastIndexOf(T, int start, int length, IEqualityComparer<T>)` | `lastIndexOf(T, int start, int end, EqualityComparator<? super T>)` | |
| `Contains(T)` | `contains(T)` | &check; |
| `Insert(int, T)` | `add(int, T)` | &check; |
| `InsertRange(int, IEnumerable<T>)` | `addAll(int, Iterable<? extends T>)` | &check; |
| `InsertRange(int, ImmutableArray<T>)` | `addAll(int, ImmutableArrayList<? extends T>)` | &check; |
| `Add(T)` | `add(T)` | &check; |
| `AddRange(IEnumerable<T>)` | `addAll(Iterable<? extends T>)` | &check; |
| `AddRange(ImmutableArray<T>)` | `addAll(ImmutableArrayList<? extends T>)` | &check; |
| `SetItem(int, T)` | `set(int, T)` | &check; |
| `Replace(T, T)` | `replace(T, T)` | &check; |
| `Replace(T, T, IEqualityComparer<T>)` | `replace(T, T, EqualityComparator<? super T>)` | &check; |
| `Remove(T)` | `remove(T)` | &check; |
| `Remove(T, IEqualityComparer<T>)` | `remove(T, EqualityComparator<? super T>)` | &check; |
| `RemoveAt(int)` | `remove(int)` | &check; |
| `RemoveRange(int start, int length)` | `removeAll(int fromIndex, int toIndex)` | &check; |
| `RemoveRange(IEnumerable<T>)` | `removeAll(Iterable<? extends T>)` | &check; |
| `RemoveRange(IEnumerable<T>, IEqualityComparer<T>)` | `removeAll(Iterable<? extends T>, EqualityComparator<? super T>)` | &check; |
| `RemoveRange(ImmutableArray<T>)` | `removeAll(ImmutableArrayList<? extends T>)` | &check; |
| `RemoveRange(ImmutableArray<T>, IEqualityComparer<T>)` | `removeAll(ImmutableArrayList<? extends T>, EqualityComparator<? super T>)` | &check; |
| `RemoveAll(Predicate<T>)` | `removeIf(Predicate<? super T>)` | &check; |
| `Clear()` | `clear()` | &check; |
| `Sort()` | `sort()` | &check; |
| `Sort(IComparer<T>)` | `sort(Comparator<? super T>)` | &check; |
| `Sort(int start, int length, IComparer<T>)` | `sort(int fromIndex, int toIndex, Comparator<? super T>)` | &check; |
| `ToBuilder()` | `toBuilder()` | &check; |
| `GetEnumerator()` | `iterator()` | &check; |
| `GetHashCode()` | `hashCode()` | &check; |
| `Equals(object)` | `equals(Object)` | &check; |
| `Equals(ImmutableArray<T>)` | `equals(ImmutableArrayList<?>)` | &check; |
| `CastUp<TDerived>(ImmutableArray<TDerived>)` | `<T>castUp(ImmutableArrayList<? extends T>)` | &check; |
| `CastArray<TOther>(ImmutableArray<TOther>)` | `<Other>castArray(Class<Other>)` | &check; |
| `As<TOther>()` | `<Other>as(Class<Other>)` | &check; |
| `OfType<TResult>()` | `<Result>ofType(Class<Result> clazz)` | &check; |

#### `ImmutableArray<T>` &rarr; No mapping

These members of `ImmutableArray<T>` have no equivalent mapping in the Java programming language.

| .NET Member | Notes |
| --- | --- |
| `operator==(ImmutableArray<T>, ImmutableArray<T>)` | Use `equals` instead |
| `operator!=(ImmutableArray<T>, ImmutableArray<T>)` | Use `equals` instead |
| `operator==(ImmutableArray<T>?, ImmutableArray<T>?)` | Use `equals` instead |
| `operator!=(ImmutableArray<T>?, ImmutableArray<T>?)` | Use `equals` instead |
| `IsDefault` | Only relevant for value types |
| `IsDefaultOrEmpty` | Only relevant for value types |

### Builder

#### `ImmutableArray<T>.Builder` &rarr; `ImmutableArrayList.Builder<T>`

| .NET Member | Java Member | Notes |
| --- | --- | --- |
| `Capacity` | `getCapacity()`, `setCapacity(int)` | &check; |
| `Count` | `size()` | &check; |
| `this[int]` | `get(int)`, `set(int, T)` | &check; |
| `ToImmutable()` | `toImmutable()` | &check; |
| `MoveToImmutable()` | `moveToImmutable()` | &check; |
| `Clear()` | `clear()` | &check; |
| `Insert(int, T)` | `add(int, T)` | &check; |
| `Add(T)` | `add(T)` | &check; |
| `AddRange(IEnumerable<T>)` | `addAll(Iterable<? extends T>)` | &check; |
| `AddRange(params T[])` | `addAll(T...)` | &check; |
| `AddRange<TDerived>(TDerived[])` | Not required | |
| `AddRange(T[], int)` | `addAll(T[], int)` | |
| `AddRange(ImmutableArray<T>)` | `addAll(ImmutableArrayList<? extends T>)` | &check; |
| `AddRange(ImmutableArray<T>, int)` | `addAll(ImmutableArrayList<? extends T>, int)` | |
| `AddRange<TDerived>(ImmutableArray<TDerived>)` | Not required | |
| `AddRange(ImmutableArray<T>.Builder)` | `addAll(ImmutableArrayList.Builder<? extends T>)` | &check; |
| `AddRange<TDerived>(ImmutableArray<TDerived>.Builder)` | Not required | |
| `Remove(T)` | `remove(Object)` | &check; |
| `RemoveAt(int)` | `remove(int)` | &check; |
| `Contains(T)` | `contains(Object)` | &check; |
| `ToArray()` | ? | |
| `CopyTo(T[], int)` | ? | |
| `IndexOf(T)` | `indexOf(Object)` | |
| `IndexOf(T, int)` | `indexOf(?, int)` | |
| `IndexOf(T, int start, int length)` | `indexOf(?, int fromIndex, int toIndex)` | 1 |
| `IndexOf(T, int start, int length, IEqualityComparator<? super T>)` | `indexOf(?, int fromIndex, int toIndex, ?)` | 1 |
| `LastIndexOf(T)` | `lastIndexOf(Object)` | |
| `LastIndexOf(T, int)` | `lastIndexOf(?, int)` | |
| `LastIndexOf(T, int start, int length)` | `lastIndexOf(?, int start, int end)` | 1 |
| `LastIndexOf(T, int start, int length, IEqualityComparator<? super T>)` | `lastIndexOf(?, int start, int end, ?)` | 1 |
| `Reverse` | `reverse()` | &check; |
| `Sort()` | `sort()` | &check; |
| `Sort(IComparer<T>)` | `sort(Comparator<? super T>)` | &check; |
| `Sort(int start, int length, IComparer<T>)` | `sort(int fromIndex, int toIndex, Comparator<? super T>)` | 1 |
| `GetEnumerator()` | `iterator()` | |

¹ Java convention is to use fromIndex/toIndex instead of start/length for ranges.
