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
| `System.Collections.Immutable.ImmutableInterlocked` | `com.tvl.util.ImmutableAtomic` |
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

#### `ImmutableArray` &rarr; `Immutables`

| .NET Member | Java Member | Notes |
| --- | --- | --- |
| `ToImmutableArray<TSource>(this IEnumerable<TSource>)` | `<T>toImmutableArrayList(Iterable<T>)` | &check; |

### Collection

#### `ImmutableArray<T>` &rarr; `ImmutableArrayList<T>`

| .NET Member | Java Member | Notes |
| --- | --- | --- |
| `Empty` | `<T>empty()` | &check; |
| `this[int]` | `get(int)` | &check; |
| `IsEmpty` | `isEmpty()` | &check; |
| `Length` | `size()` | &check; |
| `IndexOf(T)` | `indexOf(T)` | Inherited |
| `IndexOf(T, int start)` | `indexOf(T, int fromIndex)` | Inherited |
| `IndexOf(T, int start, int length)` | `indexOf(T, int fromIndex, int toIndex)` | Inherited |
| `IndexOf(T, int start, int length, IEqualityComparer<T>)` | `indexOf(T, int fromIndex, int toIndex, EqualityComparator<? super T>)` | &check; |
| `LastIndexOf(T)` | `lastIndexOf(T)` | Inherited |
| `LastIndexOf(T, int start)` | `lastIndexOf(T, int fromIndex)` | Inherited |
| `LastIndexOf(T, int start, int length)` | `lastIndexOf(T, int fromIndex, int toIndex)` | Inherited |
| `LastIndexOf(T, int start, int length, IEqualityComparer<T>)` | `lastIndexOf(T, int fromIndex, int toIndex, EqualityComparator<? super T>)` | &check; 1 |
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
| `RemoveRange(int start, int length)` | `removeAll(int fromIndex, int toIndex)` | &check; 1 |
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
| `IndexOf(T, int start, int length, IEqualityComparer<? super T>)` | `indexOf(?, int fromIndex, int toIndex, ?)` | 1 |
| `LastIndexOf(T)` | `lastIndexOf(Object)` | |
| `LastIndexOf(T, int)` | `lastIndexOf(?, int)` | |
| `LastIndexOf(T, int start, int length)` | `lastIndexOf(?, int start, int end)` | 1 |
| `LastIndexOf(T, int start, int length, IEqualityComparer<? super T>)` | `lastIndexOf(?, int start, int end, ?)` | 1 |
| `Reverse` | `reverse()` | &check; |
| `Sort()` | `sort()` | &check; |
| `Sort(IComparer<T>)` | `sort(Comparator<? super T>)` | &check; |
| `Sort(int start, int length, IComparer<T>)` | `sort(int fromIndex, int toIndex, Comparator<? super T>)` | &check; 1 |
| `GetEnumerator()` | `iterator()` | &check; |

## `ImmutableList<T>` &rarr; `ImmutableTreeList<T>`

### Factory

#### `ImmutableList` &rarr; `ImmutableTreeList<T>`

| .NET Member | Java Member | Notes |
| --- | --- | --- |
| `Create<T>()` | `<T>create()` | &check; |
| `Create<T>(T)` | `<T>create(T)` | &check; |
| `Create<T>(params T[])` | `<T>create(T...)` | &check; |
| `CreateRange<T>(IEnumerable<T>)` | `<T>createAll(Iterable<? extends T>)` | &check; |
| `CreateBuilder<T>()` | `<T>createBuilder()` | &check; |

#### `ImmutableList` &rarr; `Immutables`

| .NET Member | Java Member | Notes |
| --- | --- | --- |
| `ToImmutableList<TSource>(this IEnumerable<TSource>)` | `<T>toImmutableTreeList(Iterable<T>)` | &check; |

#### `ImmutableList` &rarr; `AbstractImmutableList<T>`

| .NET Member | Java Member | Notes |
| --- | --- | --- |
| `Replace<T>(this IImmutableList<T>, T, T)` | `replace(T, T)` | &check; |
| `Remove<T>(this IImmutableList<T>, T)` | `remove(T)` | &check; |
| `RemoveRange<T>(this IImmutableList<T>, IEnumerable<T>)` | `removeAll(Iterable<? extends T>)` | &check; |
| `IndexOf(this IImmutableList<T>, T)` | `indexOf(T)` | &check; |
| `IndexOf(this IImmutableList<T>, T, IEqualityComparer<? super T>)` | `indexOf(T, EqualityComparator<? super T>)` | &check; |
| `IndexOf(this IImmutableList<T>, T, int)` | `indexOf(T, int)` | &check; |
| `IndexOf(this IImmutableList<T>, T, int start, int length)` | `indexOf(T, int fromIndex, int toIndex)` | &check; 1 |
| `LastIndexOf(this IImmutableList<T>, T)` | `lastIndexOf(T)` | &check; |
| `LastIndexOf(this IImmutableList<T>, T, IEqualityComparer<? super T>)` | `lastIndexOf(T, EqualityComparator<? super T>)` | &check; |
| `LastIndexOf(this IImmutableList<T>, T, int)` | `lastIndexOf(T, int fromIndex)` | &check; |
| `LastIndexOf(this IImmutableList<T>, T, int start, int length)` | `lastIndexOf(T, int fromIndex, int toIndex)` | &check; 1 |

### Collection

#### `ImmutableList<T>` &rarr; `ImmutableTreeList<T>`

| .NET Member | Java Member | Notes |
| --- | --- | --- |
| `Empty` | `<T>empty()` | &check; |
| `Clear()` | `clear()` | &check; |
| `BinarySearch<T>(T)` | `binarySearch(T)` | &check; |
| `BinarySearch<T>(T, IComparer<T>)` | `binarySearch(T, Comparator<? super T>)` | &check; |
| `BinarySearch<T>(int start, int length, T, IComparer<T>)` | `binarySearch(int fromIndex, int toIndex, T, Comparator<? super T>)` | &check; 1 |
| `IsEmpty` | `isEmpty()` | &check; |
| `Count` | `size()` | &check; |
| `this[int]` | `get(int)` | &check; |
| `ToBuilder()` | `toBuilder()` | &check; |
| `Add(T)` | `add(T)` | &check; |
| `AddRange(IEnumerable<T>)` | `addAll(Iterable<? extends T>)` | &check; |
| `Insert(int, T)` | `add(int, T)` | &check; |
| `InsertRange(int, IEnumerable<T>)` | `addAll(int, Iterable<? extends T>)` | &check; |
| `Remove(T)` | `remove(T)` | &check; |
| `Remove(T, IEqualityComparer<T>)` | `remove(T, EqualityComparator<? super T>)` | &check; |
| `RemoveRange(int start, int length)` | `removeAll(int fromIndex, int toIndex)` | &check; 1 |
| `RemoveRange(IEnumerable<T>)` | `removeAll(Iterable<? extends T>)` | &check; |
| `RemoveRange(IEnumerable<T>, IEqualityComparer<T>)` | `removeAll(Iterable<? extends T>, EqualityComparator<? super T>)` | &check; |
| `RemoveAt(int)` | `remove(int)` | &check; |
| `RemoveAll(Predicate<T>)` | `removeIf(Predicate<? super T>)` | &check; |
| `SetItem(int, T)` | `set(int, T)` | &check; |
| `Replace(T, T)` | `replace(T, T)` | &check; |
| `Replace(T, T, IEqualityComparer<T>)` | `replace(T, T, EqualityComparator<? super T>)` | &check; |
| `Reverse()` | `reverse()` | &check; |
| `Reverse(int start, int length)` | `reverse(int fromIndex, int toIndex)` | &check; 1 |
| `Sort()` | `sort()` | &check; |
| `Sort(IComparer<T>)` | `sort(Comparator<? super T>)` | &check; |
| `Sort(int start, int length, IComparer<T>)` | `sort(int fromIndex, int toIndex, Comparator<? super T>)` | &check; 1 |
| `CopyTo(T[])` | ? | |
| `CopyTo(T[], int)` | ? | |
| `CopyTo(int, T[], int, int)` | ? | |
| `GetRange(int start, int length)` | `subList(int fromIndex, int toIndex)` | &check; 1 |
| `ConvertAll<TOutput>(Func<T, TOutput>)` | `<U>convertAll(Function<? super T, U>)` | &check; |
| `Exists(Predicate<T>)` | `exists(Predicate<? super T>)` | &check; |
| `Find(Predicate<T>)` | `find(Predicate<? super T>)` | &check; |
| `FindAll(Predicate<T>)` | `retainIf(Predicate<? super T>)` | &check; |
| `FindIndex(Predicate<T>)` | `findIndex(Predicate<? super T>)` | &check; |
| `FindIndex(int, Predicate<T>)` | `findIndex(int, Predicate<? super T>)` | &check; |
| `FindIndex(int start, int length, Predicate<T>)` | `findIndex(int fromIndex, int toIndex, Predicate<? super T>)` | &check; 1 |
| `FindLast(Predicate<T>)` | `findLast(Predicate<? super T>)` | &check; |
| `FindLastIndex(Predicate<T>)` | `findLastIndex(Predicate<? super T>)` | &check; |
| `FindLastIndex(int, Predicate<T>)` | `findLastIndex(int, Predicate<? super T>)` | |
| `FindLastIndex(int start, int length, Predicate<T>)` | `findLastIndex(int fromIndex, int toIndex, Predicate<? super T>)` | 1 |
| `IndexOf(T, int start, int length, IEqualityComparer<T>)` | `indexOf(T, int fromIndex, int toIndex, EqualityComparator<? super T>)` | &check; 1 |
| `LastIndexOf(T, int start, int length, IEqualityComparer<T>)` | `lastIndexOf(T, int fromIndex, int toIndex, EqualityComparator<? super T>)` | &check; 1 |
| `TrueForAll(Predicate<T>)` | `trueForAll(Predicate<? super T>)` | &check; |
| `Contains(T)` | `contains(T)` | &check; |
| `IndexOf(T)` | `indexOf(T)` | &check; |
| `GetEnumerator()` | `iterator()` | &check; |

#### `ImmutableList<T>` &rarr; No mapping

These members of `ImmutableList<T>` have no equivalent mapping in the Java programming language.

| .NET Member | Notes |
| --- | --- |
| `Sort(Comparison<T>)` | Use `sort(Comparator<? super T>` instead. |
| `ForEach(Action<T>)` | Use enhanced `for` loop instead. |

### Builder

#### `ImmutableList<T>.Builder` &rarr; `ImmutableTreeList.Builder<T>`

| .NET Member | Java Member | Notes |
| --- | --- | --- |
| `Count` | `size()` | &check; |
| `this[int]` | `get(int)`, `set(int, T)` | &check; |
| `IndexOf(T)` | `indexOf(Object)` | &check; |
| `Insert(int, T)` | `add(int, T)` | &check; |
| `RemoveAt(int)` | `remove(int)` | &check; |
| `Add(T)` | `add(T)` | &check; |
| `Clear()` | `clear()` | &check; |
| `Contains(T)` | `contains(Object)` | &check; |
| `Remove(T)` | `remove(Object)` | &check; |
| `GetEnumerator()` | `iterator()` | &check; |
| `CopyTo(T[])` | ? | |
| `CopyTo(T[], int)` | ? | |
| `CopyTo(int, T[], int, int)` | ? | |
| `Exists(Predicate<T>)` | `exists(Predicate<? super T>)` | &check; |
| `Find(Predicate<T>)` | `find(Predicate<? super T>)` | &check; |
| `FindIndex(Predicate<T>)` | `findIndex(Predicate<? super T>)` | &check; |
| `FindIndex(int, Predicate<T>)` | `findIndex(int, Predicate<? super T>)` | &check; |
| `FindIndex(int start, int length, Predicate<T>)` | `findIndex(int fromIndex, int toIndex, Predicate<? super T>)` | &check; 1 |
| `FindLast(Predicate<T>)` | `findLast(Predicate<? super T>)` | |
| `FindLastIndex(Predicate<T>)` | `findLastIndex(Predicate<? super T>)` | |
| `FindLastIndex(int, Predicate<T>)` | `findLastIndex(int, Predicate<? super T>)` | |
| `FindLastIndex(int start, int length, Predicate<T>)` | `findLastIndex(int fromIndex, int toIndex, Predicate<? super T>)` | 1 |
| `IndexOf(T, int)` | `indexOf(?, int)` | |
| `IndexOf(T, int start, int length)` | `indexOf(?, int fromIndex, int toIndex)` | 1 |
| `IndexOf(T, int start, int length, IEqualityComparer<? super T>)` | `indexOf(?, int fromIndex, int toIndex, ?)` | 1 |
| `LastIndexOf(T)` | `lastIndexOf(Object)` | &check; |
| `LastIndexOf(T, int)` | `lastIndexOf(?, int)` | |
| `LastIndexOf(T, int start, int length)` | `lastIndexOf(?, int start, int end)` | 1 |
| `LastIndexOf(T, int start, int length, IEqualityComparer<? super T>)` | `lastIndexOf(?, int start, int end, ?)` | 1 |
| `TrueForAll(Predicate<T>)` | `trueForAll(Predicate<? super T>)` | &check; |
| `AddRange(IEnumerable<T>)` | `addAll(Iterable<? extends T>)` | &check; |
| `InsertRange(int, IEnumerable<T>)` | `addAll(int, Iterable<? extends T>)` | &check; |
| `RemoveAll(Predicate<T>)` | `removeIf(Predicate<? super T>)` | &check; |
| `Reverse()` | `reverse()` | &check; |
| `Reverse(int start, int length)` | `reverse(int fromIndex, int toIndex)` | &check; 1 |
| `Sort()` | `sort()` | &check; |
| `Sort(IComparer<T>)` | `sort(Comparator<? super T>)` | &check; |
| `Sort(int start, int length, IComparer<T>)` | `sort(int fromIndex, int toIndex, Comparator<? super T>)` | &check; 1 |
| `BinarySearch<T>(T)` | `binarySearch(T)` | &check; |
| `BinarySearch<T>(T, IComparer<T>)` | `binarySearch(T, Comparator<? super T>)` | &check; |
| `BinarySearch<T>(int start, int length, T, IComparer<T>)` | `binarySearch(int fromIndex, int toIndex, T, Comparator<? super T>)` | &check; 1 |
| `ToImmutable()` | `toImmutable()` | &check; |

#### `ImmutableList<T>.Builder` &rarr; `ImmutableTreeList.Builder<T>.QueriesWrapper` (internal API)

| .NET Member | Java Member | Notes |
| --- | --- | --- |
| `GetRange(int start, int length)` | `subList(int fromIndex, int toIndex)` | &check; 1 |
| `ConvertAll<TOutput>(Func<T, TOutput>)` | `<U>convertAll(Function<? super T, U>)` | &check; |
| `FindAll(Predicate<T>)` | `retainIf(Predicate<? super T>)` | &check; |

#### `ImmutableList<T>.Builder` &rarr; No mapping

These members of `ImmutableList<T>.Builder` have no equivalent mapping in the Java programming language.

| .NET Member | Notes |
| --- | --- |
| `Sort(Comparison<T>)` | Use `sort(Comparator<? super T>` instead. |
| `ForEach(Action<T>)` | Use enhanced `for` loop instead. |

## `ImmutableQueue<T>` &rarr; `ImmutableLinkedQueue<T>`

### Factory

#### `ImmutableQueue` &rarr; `ImmutableLinkedQueue<T>`

| .NET Member | Java Member | Notes |
| --- | --- | --- |
| `Create<T>()` | `<T>create()` | &check; |
| `Create<T>(T)` | `<T>create(T)` | &check; |
| `CreateRange<T>(IEnumerable<T>)` | `<T>createAll(Iterable<? extends T>)` | &check; |
| `Create<T>(params T[])` | `<T>create(T...)` | &check; |

#### `ImmutableQueue` &rarr; No mapping

These members of `ImmutableQueue` have no equivalent mapping in the Java programming language.

| .NET Member | Notes |
| --- | --- |
| `Dequeue<T>(IImmutableQueue<T>, out T)` | Java does not support `out` parameters |

### Collection

#### `ImmutableQueue<T>` &rarr; `ImmutableLinkedQueue<T>`

| .NET Member | Java Member | Notes |
| --- | --- | --- |
| `Clear()` | `clear()` | &check; |
| `IsEmpty` | `isEmpty()` | &check; |
| `Empty` | `<T>empty()` | &check; |
| `Peek()` | `peek()` | &check; |
| `Enqueue(T)` | `add(T)` | &check; |
| `Dequeue()` | `poll()` | &check; |
| `GetEnumerator()` | `iterator()` | &check; |

#### `ImmutableQueue<T>` &rarr; No mapping

These members of `ImmutableQueue<T>` have no equivalent mapping in the Java programming language.

| .NET Member | Notes |
| --- | --- |
| `Dequeue(out T)` | Java does not support `out` parameters |

## Utility Methods

### `ImmutableInterlocked`

#### `ImmutableInterlocked` &rarr; `ImmutableAtomic`

| .NET Member | Java Member | Notes |
| --- | --- | --- |
| `Update<T>(ref T, Func<T, T>)` | `<T>update(AtomicReference<T>, Function<? super T, ? extends T>)` | &check; |
| `Update<T, TArg>(ref T, Func<T, TArg, T>, TArg)` | `<T, State>update(AtomicReference<T>, BiFunction<? super T, ? super State, ? extends T>, State)` | &check; |
| `InterlockedExchange<T>(ref ImmutableArray<T>, ImmutableArray<T>)` | `<T>getAndSet(AtomicReference<ImmutableArrayList<T>>, ImmutableArrayList<T>)` | &check; |
| `InterlockedCompareExchange<T>(ref ImmutableArray<T>, ImmutableArray<T>, ImmutableArray<T>)` | `<T>interlockedCompareExchange(AtomicReference<ImmutableArrayList<T>>, ImmutableArrayList<T>, ImmutableArrayList<T>)` | &check; |
| `InterlockedInitialize<T>(ref ImmutableArray<T>, ImmutableArray<T>)` | `<T>interlockedInitialize(AtomicReference<ImmutableArrayList<T>>, ImmutableArrayList<T>)` | &check; |
| `GetOrAdd<TKey, TValue, TArg>(ref ImmutableDictionary<TKey, TValue>, TKey, Func<TKey, TArg, TValue>, TArg)` | `<K, V, State>getOrAdd(AtomicReference<ImmutableHashMap<K, V>>, K, BiFunction<? super K, ? super State, ? extends V>, State)` | &check; |
| `GetOrAdd<TKey, TValue>(ref ImmutableDictionary<TKey, TValue>, TKey, Func<TKey, TValue>)` | `<K, V>getOrAdd(AtomicReference<ImmutableHashMap<K, V>>, K, Function<? super K, ? extends V>)` | &check; |
| `GetOrAdd<TKey, TValue>(ref ImmutableDictionary<TKey, TValue>, TKey, TValue)` | `<K, V>getOrAdd(AtomicReference<ImmutableHashMap<K, V>>, K, V)` | &check; |
| `AddOrUpdate<TKey, TValue>(ref ImmutableDictionary<TKey, TValue>, TKey, Func<TKey, TValue>, Func<TKey, TValue, TValue>)` | `<K, V>addOrUpdate(AtomicReference<ImmutableHashMap<K, V>>, K, Function<? super K, ? extends V>, BiFunction<? super K, ? super V, ? extends V>)` | &check; |
| `AddOrUpdate<TKey, TValue>(ref ImmutableDictionary<TKey, TValue>, TKey, TValue, Func<TKey, TValue, TValue>)` | `<K, V>addOrUpdate(AtomicReference<ImmutableHashMap<K, V>>, K, V, BiFunction<? super K, ? super V, ? extends V>)` | &check; |
| `TryAdd<TKey, TValue>(ref ImmutableDictionary<TKey, TValue>, TKey, TValue)` | `<K, V>tryAdd(AtomicReference<ImmutableHashMap<K, V>>, K, V)` | &check; |
| `TryUpdate<TKey, TValue>(ref ImmutableDictionary<TKey, TValue>, TKey, TValue, TValue)` | `<K, V>tryUpdate(AtomicReference<ImmutableHashMap<K, V>>, K, V, V)` | &check; |
| `TryRemove<TKey, TValue>(ref ImmutableDictionary<TKey, TValue>, TKey, out TValue)` | `<K, V>tryRemove(AtomicReference<ImmutableHashMap<K, V>>, K)` | &check; |
| `TryPop<T>(ref ImmutableStack<T>, out T)` | `<T>tryPop(AtomicReference<ImmutableLinkedStack<T>>)` | &check; |
| `Push<T>(ref ImmutableStack<T>, T)` | `<T>push(AtomicReference<ImmutableLinkedStack<T>>, T)` | &check; |
| `TryDequeue<T>(ref ImmutableQueue<T>, out T)` | `<T>tryPoll(AtomicReference<ImmutableLinkedQueue<T>>)` | &check; |
| `Enqueue<T>(ref ImmutableQueue<T>, T)` | `<T>add(AtomicReference<ImmutableLinkedQueue<T>>, T)` | &check; |

## Footnotes

&check; The API is implemented and documented as described.

1. Java convention is to use fromIndex/toIndex instead of start/length for ranges.<br>
2. These methods were renamed to `createAll` to avoid conflicts with `create(T...)`.
