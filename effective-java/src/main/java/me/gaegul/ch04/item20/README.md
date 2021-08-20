# 아이템 20. 추상 클래스보다는 인터페이스를 우선하라

> *추상 클래스 방식은 새로운 타입을 정의하는데 제약을 가지게 된다. 인터페이스가 선언한 메서드를 모두 정의하고 그 일반 규약을 잘 지킨 클래스라면 다른 어떤 클래스를 상속했든 같은 타입으로 취급된다.*

- 인터페이스
    - 기존 클래스에도 새로운 인터페이스를 구현할 수 있다.
        - Comparable, Iterable, AutoCloseable 인터페이스가 추가됐을 때 표준 라이브러리의 기존 클래스가 이 인터페이스를 구현한 채 릴리스됐다.
        - 기존 클래스 위에 새로운 추상 클래스를 넣기는 어렵다.
            - 두 개의 추상 클래스를 상속하려면 두 개의 추상 클래스가 같은 공통 조상 클래스여야 한다.
    - 인터페이스는 믹스인 정의에 안성맞춤이다.
        - 믹스인
            - 클래스가 구현할 수 있는 타입
            - 믹스인을 구현한 클래스에 원래의 '주된 타입' 외에도 특정 선택적 행위를 제공한다고 선언하는 효과를 준다.
                - Comparable은 자신을 구현한 클래스의 인스턴스끼리는 순서를 정할 수 있다고 선언한다.
    - 계층구조가 없는 타입 프레임워크를 만들 수 없다.
        - 클래스 계층구조는 공통 기능을 정의해놓은 타입이 없으니, 매개변수 타입만 다른 메서드를 가진 거대한 클래스를 만들게 된다.
        - 인터페이스를 통해 해결할 수 있다.
        - 가수와 작곡가 인터페이스는 작곡하는 가수를 표현하는 제 3의 인터페이스를 구현할 수 있다.

            ```java
            public interface Singer {
                AudioClip sing(Song s);
            }

            public interface Songwriter {
                Song compose(int chartPosition);
            }

            public interface SingerSongwriter extends Singer, Songwriter {
                AudioClip strum();
                void actSensitive();
            }
            ```

    - 래퍼 클래스 관용구와 같이 사용하면 인터페이스는 기능을 향상 시키고 안전하게 사용할 수 있다.
        - 상속해서 만든 클래스는 래퍼 클래스보다 활용도가 떨어지고 깨지기 쉽다.
    - 인터페이스의 메서드 중 구현방법이 명백한 것은 디폴트 메서드로 제공한다.
        - 디폴트 메서드를 제공할 때 상속하는 사람을 위해 @implSpec자바독 태그를 붙여 문서화 해야한다.
        - 디폴트 메서드에서 equals, hashCode Object 메서드를 제공하면 안된다.
- 골격 구현 클래스
    - 인터페이스와 추상 골격 구현 클래스를 함께 제공하여 인터페이스와 추상 클래스의 장점을 가질 수 있다.
    - 인터페이스로 타입을 정의하고 디폴트 메서드를 제공하고 골격 구현 클래스는 나머지 메서드를 구현한다. → 탬플릿 메서드 패턴
    - 골격 구현 클래스의 이름은 Abstract~~~로 명명한다.
    - 골격 구현을 사용해 완성한 구체 클래스

        ```java
        static List<Integer> intArrayAsList(int[] a) {
            Objects.requireNonNull(a);
            
            return new AbstractList<Integer>() {
                @Override
                public Integer get(int index) {
                    return a[index];
                }

                @Override
                public Integer set(int index, Integer element) {
                    int oldVal = a[index];
                    a[index] = element;
                    return oldVal;
                }

                @Override
                public int size() {
                    return a.length;
                }
            };
        }
        ```

    - 다양한 방식으로 골격 구현 클래스를 제공할 수 있다.
        - 골격 구현을 확장하는 것으로 인터페이스의 구현이 거의 끝난다.
        - 골격 구현을 확장하지 못하는 경우 인터페이스를 직접 구현한다.
            - 디폴트 메서드를 이용한다.
        - 골격 구현 클래스를 우회적으로 사용한다.
            - 골격 구현을 private 내부 클래스로 정의하고 각 메서드 호출을 내부 클래스의 인스턴스에 전달한다.
            → 시뮬레이드한 다중 상속
    - 시뮬레이트한 다중 상속
        - 인터페이스를 확인하여 다른 메서드들의 구현에 사용되는 기반 메서드를 선정한다.
        → 추상 메서드
        - 기반 메서드들을 사용해 직접 구현할 수 있는 메서드를 디폴트 메서드로 제공한다.

            ```java
            public abstract class AbstractMapEntry<K,V> implements Map.Entry<K,V> {

                // 변경 가능한 엔트리는 이 메서드를 반드시 재정의해야 한다.
                @Override
                public V setValue(V value) {
                    throw new UnsupportedOperationException();
                }

                // Map.Entry.equals의 일반 규약을 구현한다.
                @Override
                public boolean equals(Object o) {
                    if (o == this)
                        return true;
                    if (!(o instanceof Map.Entry))
                        return false;
                    Map.Entry<?,?> e = (Map.Entry) o;
                    return Objects.equals(e.getKey(), getKey())
                            && Objects.equals(e.getValue(), getValue());
                }

                // Map.Entry.hashCode의 일반 규약을 구현한다.
                @Override
                public int hashCode() {
                    return Objects.hashCode(getKey())
                            ^ Objects.hashCode(getValue());
                }

                @Override
                public String toString() {
                    return getKey() + "=" + getValue();
                }
            }
            ```

        - 골격 구현은 기본적으로 상속해서 사용하는 것을 가정한다.
        - 동작 방식을 잘 정리해 문서로 남겨야 한다.
- 단순 구현
    - 상속을 위해 인터페이스를 구현한 것이지만 추상 클래스가 아니다.