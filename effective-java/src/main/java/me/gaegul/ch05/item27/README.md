# 아이템 27. 비검사 경고를 제거하라

- 비검사 경고
    - 제네릭을 사용할 때 나타나는 컴파일러 경고
- 할 수 있는한 모든 비검사 경고를 제거하라
    - 모두 제거하면 해당 코드는 타입 안정성을 보장한다.
- 경고를 제거할 수는 없지만 타입 안전하다고 확신할 수 있다면 `@SuppressWarnings("unchecked")` 어노테이션을 달아 경고를 숨기자
    - `@SuppressWarnings` 어노테이션은 항상 가능한 좁은 범위에 적용
    - 어노테이션은 선언에만 달 수 있기 때문에 return문에는 `@SuppressWarnings`를 사용할 수 없다.

        ```java
        public <T> T[] toArray(T[] a) {
            if (a.length < size) {
                @SuppressWarnings("unchecked")
                T[] result = (T[]) Arrays.copyOf(elementData, size, a.getClass())
                return result;
            }
            System.arraycopy(elementData, 0, a, 0, size);
            if (a.length > size)
                a[size] = null;
            return a;
        }
        ```

- `@SuppressWarnings("unchecked")` 어노테이션을 사용할 때 경고를 무시해도 안전한 이유를 주석으로 남겨야 한다.
    - 다른 사람이 코드를 이해하는데 도움이 되고 코드를 잘못 수정하여 타입 안전성을 잃는 상황을 줄여준다.