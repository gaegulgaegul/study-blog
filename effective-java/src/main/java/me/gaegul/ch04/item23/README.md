# 아이템 23. 태그 달린 클래스보다는 클래스 계층구조를 활용하라

- 태그 달린 클래스
    - 두 가지 이상 의미를 표현할 수 있다.
    - 현재 표현하는 의미를 태그 값으로 알려주는 클래스

        ```java
        public class Figure {
            
            enum Shape { RECTANGLE, CIRCLE };
            
            // 태그 필드 - 현재 모양을 나타낸다.
            final Shape shape;
            
            // 다음 필드들은 모양이 사각형(RECTANGLE)일 때만 쓰인다.
            double length;
            double width;
            
            // 다음 필드는 모양이 원(CIRCLE)일 때만 쓰인다.
            double radius;
            
            // 원용 생성자
            Figure(double radius) {
                shape = Shape.CIRCLE;
                this.radius = radius;
            }
            
            // 사각형용 생성자
            Figure(double length, double width) {
                shape = Shape.RECTANGLE;
                this.length = length;
                this.width = width;
            }
            
            double area() {
                switch (shape) {
                    case RECTANGLE:
                        return length * width;
                    case CIRCLE:
                        return Math.PI * (radius * radius);
                    default:
                        throw new AssertionError(shape);
                }
            }
        }
        ```

        - 여러 구현이 한 클래스에 있어 가독성이 나쁘다.
        - 다른 의미를 위한 코드도 언제나 함께 하니 메모리도 많이 사용한다.
        - 생성자가 태그 필드를 성정하고 해당 의미에 쓰이는 데이터 필드들을 초기화하는데 컴파일 시에 에러를 잡지 못하고 런타임에서 에러가 발생한다.

- 클래스 계층구조
    - 태그 달린 클래스를 클래스 계층구조로 바꾸는 방법
        - 루트가 될 추상 클래스를 정의
            - 태그 값에 따라 동작이 달라지는 메서드를 루트 클래스의 추상 메서드로 정의
            → Figure의 area 메서드
            - 태그 값에 상관없이 동작이 일정한 메서드를 루트 클래스의 인반 메서드로 추가
        - 루트 클래스를 확장한 구체 클래스르르 의미별로 하나씩 정의
            - 각 하위 클래스에는 각자의 의미에 해당하는 데이터 필드를 넣는다.
            - 루트 클래스가 정의한 추상 메서드를 구현한다.

        ```java
        public abstract class Figure {
            abstract double area();
        }

        public class Circle extends Figure {

            final double radius;

            public Circle(double radius) {
                this.radius = radius;
            }

            @Override
            double area() {
                return Math.PI * (radius * radius);
            }
        }

        public class Rectangle extends Figure {

            final double length;
            final double width;

            public Rectangle(double length, double width) {
                this.length = length;
                this.width = width;
            }

            @Override
            double area() {
                return length * width;
            }
        }
        ```

    - 클래스 계층구조의 장점
        - 태그 달린 클래스의 단점을 모두 없앤다.
            - 코드가 간결하고 명확하다.
            - 모두 final 필드로 선언되어 각 클래스의 생성자가 모든 필드를 초기화하고 추상메서드를 구현했는지 컴파일러가 확인해준다. → 런타임 오류가 없다.
        - 루트 클래스의 코드를 건드리지 않고 다른 프로그래머도 독립적으로 계층구조를 확장하여 사용할 수  있다.