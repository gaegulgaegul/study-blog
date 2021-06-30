# 13. 디폴트 메서드

- 인터페이스에 새로운 메서드를 추가할 때 발생하는 문제
    - 기존에 구현체에 구현된 메서드를 수정해야 한다.
    - 자바 8에서 해결하는 기능
        - 인터페이스 내부에 **정적 메서드(static method)**를 사용한다.
        - **디폴트 메서드(default method)** 기능을 사용한다.
- 변화하는 API
    - API 버전 1
        - 초기 버전 인터페이스

        ```java
        public interface Resizable extends Drawable {
            int getWidth();
            int getHeight();
            void setWidth(int width);
            void setHeight(int height);
            void setAbsoluteSize(int width, int height);
        }
        ```

        - 사용자 중 한 명이 Resizable 구현하는 Ellipse 클래스 생성

        ```java
        public class Ellipse implements Resizable {
        	...
        }
        ```

        - Resizable 모양을 처리하는 게임을 작성

        ```java
        public class Game {
            public static void main(String[] args) {
                List<Resizable> resizableShapes = Arrays.asList(new Square(), new Rectangle(), new Ellipse());
                Utils.paint(resizableShapes);
            }
        }

        public class Utils {
            public static void paint(List<Resizable> l) {
                l.forEach(r -> {
                    r.setAbsoluteSize(42, 42);
                    r.draw();
                });
            }
        }
        ```

        - API 버전 2
            - Resizable 개선 요청으로 새로운 메서드 추가

            ```java
            public interface Resizable extends Drawable {
                int getWidth();
                int getHeight();
                void setWidth(int width);
                void setHeight(int height);
                void setAbsoluteSize(int width, int height);
                void setRelativeSize(int wFactor, int hFactor);
            }
            ```

            - 사용자가 겪는 문제
                - Resizable을 구현하는 모든 클래스는 setRelativeSize 메서드를 구현해야 한다. → 바이러니 호환성 유지
                - 사용자가 Ellipse를 포함하는 전체 어플리케이션을 재빌드 할 때 컴파일 에러가 발생한다.
                - 디폴트 메서드를 이용해 문제를 해결한다.
- 디폴트 메서드란 무엇인가?
    - 자바 8의 호환성을 유지하면서 API를 바꿀 수 있는 새로운 기능
    - 인터페이스를 구현하는 클래서에서 구현하지 않은 메서드는 인터페이스 자체에서 기본으로 제공한다.
- 디폴트 메서드 활용 패턴
    - 선택형 메서드
        - 인터페이스를 구현하는 클래서에서 메서드의 내용이 비어있는 상황에서 디폴트 메서드를 통해 기본 구현을 제공한다.

        ```java
        public interface Iterator<T> {
        	boolean hasNext();
        	T next();
        	default void remove() {
        		throw new UnsupportedOperationException();
        	}
        }
        ```

    - 다중 상속 형식
        - 자바 8에서 인터페이스가 구현을 포함할 수 있어 클래스는 여러 인터페이스에서 동작을 상속받을 수 있다.

        ```java
        public class Monster implements Rotatable, Moveable, Resizable {
        	...
        }

        Monster m = new Monster();
        m.rotateBy(180);
        m.moveVertically(10);
        ```

- 해석 규칙
    - 같은 시그니처를 갖는 디폴트 메서드를 상속받을 때

        ```java
        public interface A {
            default void hello() {
                System.out.println("Hello from A");
            }
        }

        public interface B extends A {
            default void hello() {
                System.out.println("Hello from B");
            }
        }

        public class C implements B, A {
            public static void main(String[] args) {
                new C().hello();
            }
        }

        >> Hello from B
        ```

        - 클래스나 슈퍼클래스에서 정의한 메서드가 디폴트 메서드보다 우선권을 갖는다.
        - 상속관계를 갖는 인터페이스에서 같은 시그니처를 갖는 메서드를 정의할 때는 서브 인터페이스가 이긴다.
        - 디폴트 메서드의 우선순위가 결정되지 않았다면 여러 인터페이스를 상속받는 클래스가 명시적으로 디폴트 메서드를 오버라이드하고 호출해야 한다.
    - 디폴트 메서드를 제공하는 서브인터페이스가 이긴다.

        ```java
        public class D implements A {
        }

        public class C extends D implements B, A {
            public static void main(String[] args) {
                new C().hello();
            }
        }

        >> Hello from B
        ```

        - D는 hello를 오버라이드 하지 않고 A만 구현했다. 따라서 D는 A의 디폴트 메서드 구현을 상속받았다.
        - 클래스나 슈퍼클래스에서 메서드 정의가 없을 때는 디폴트 메서드를 정의하는 서브인터페이스가 선택된다.
        - 컴파일러는 A 또는 B를 선택해야 한다. B가 A를 상속받는 관계이므로 B가 선택된다.
    - 충돌 그리고 명시적인 문제해결

        ```java
        public interface A {
        		default void hello() {
        				System.out.println("Hello from A");
        		}
        }

        public interface B {
        		default void hello() {
        				System.out.println("Hello from B");
        		}
        }

        public class C implements B, A {}

        >> 에러 발생
        ```

        - 인터페이스 간에 상속관계가 없으므로 2번 규칙을 적용할 수 없다. 따라서 컴파일러는 hello 메서드를 구분할 기준이 없다.
        - 충돌 해결
            - hello 메서드를 오버라이드 해서 호출하려는 메서드를 명시적으로 선택해야 한다.

            ```java
            public class C implements B, A {
            		void hello() {
            				B.super.hello();
            		}
            }
            ```

    - 다이아몬드 문제

        ```java
        public interface A {
            default void hello() {
                System.out.println("Hello from A");
            }
        }

        public interface B extends A {}

        public interface C extends A {}

        public class D implements B, C {
            public static void main(String[] args) {
                new D().hello();
            }
        }

        >> Hello from A
        ```

        - A만 디폴트 메서드를 정의하고 있으므로 컴파일러는 A를 선택한다.
        - 만약 C 또는 D에 `void hello();` 시그니처를 입력하면 충돌이 발생하므로 D에서 이를 명시적으로 선택하도록 구현해야 한다.