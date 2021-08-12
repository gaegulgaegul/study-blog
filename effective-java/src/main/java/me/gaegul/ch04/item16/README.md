# 아이템 16. public 클래스에서는 public 필드가 아닌 접근자 메서드를 사용하라

- 클래스의 필드는 private 선언, public 접근자(getter)를 추가한다.
    - 패키지 밖에서 접근할 수 있는 클래스라면 접근자를 제공하여 유연성을 가진다.
    - 접근자(getter)와 변경자(setter) 메서드를 활용해 데이터를 캡슐화

        ```java
        public class Point {
            
            private double x;
            private double y;

            public Point(double x, double y) {
                this.x = x;
                this.y = y;
            }

            public double getX() {
                return x;
            }

            public void setX(double x) {
                this.x = x;
            }

            public double getY() {
                return y;
            }

            public void setY(double y) {
                this.y = y;
            }
        }
        ```

- package-private 클래스 혹은 private 중첩 클래스라면 데이터 필드를 노출한다 해도 하등의 문제가 없다.
    - 클래스의 추상 개념을 올바르게 표현
    - 클래스 선언 면에서나 클라이언트 코드 면에서나 접근자 방식(getter)보다 깔끔하다.
    - private 중첩 클래스는 수정 범위가 좁아져서 해당 클래스를 포함하는 외부 클래스까지로 제한된다.

    ```java
    @AllArgsConstructor
    public class Animal {

        private String type;
        private String name;

        public static void main(String[] args) {
            Animal cat = new Animal("고양이", "나비");
            System.out.println("종류 : " + cat.type + ", 이름 : " + cat.name);
        }
    }
    ```