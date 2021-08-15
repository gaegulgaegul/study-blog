# 아이템 17. 변경 가능성을 최소화하라

- 클래스를 불변으로 만드는 5가지 규칙
    - 객체의 상태를 변경하는 메서드(변경자)를 제공하지 않는다.
    - 클래스를 확장할 수 없도록 한다.
    - 모든 필드를 final로 선언한다.
    - 모든 필드를 private으로 선언한다.
    - 자신 외에는 내부의 가변 컴포넌트에 접근할 수 없도록 한다.
- 불변 객체 특징

    ```java
    public final class Complex {
        private final double re;
        private final double im;

        public static final Complex ZERO = new Complex(0,0);
        public static final Complex ONE = new Complex(1,0);
        public static final Complex I = new Complex(0,1);

        public Complex(double re, double im) {
            this.re = re;
            this.im = im;
        }

        public double realPart() {
            return re;
        }

        public double imaginaryPart() {
            return im;
        }

        public Complex plus(Complex c) {
            return new Complex(re + c.re, im + c.im);
        }

        public Complex minus(Complex c) {
            return new Complex(re - c.re, im - c.im);
        }

        public Complex times(Complex c) {
            return new Complex(re * c.re - im * c.im, re * c.im + im*c.re);
        }

        public Complex dividedBy(Complex c) {
            double tmp = c.re * c.re + c.im * c.im;
            return new Complex((re * c.re + im * c.im) / tmp, (im * c.re - re * c.im) / tmp);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Complex)) return false;
            Complex complex = (Complex) o;
            return Double.compare(complex.re, re) == 0 &&
                    Double.compare(complex.im, im) == 0;
        }

        @Override
        public int hashCode() {
            return 31 * Double.hashCode(re) + Double.hashCode(im);
        }

        @Override
        public String toString() {
            return "Complex{" +
                    "re=" + re +
                    ", im=" + im +
                    '}';
        }
    }
    ```

    - 불변 객체는 단순하다.
        - 생성된 시점의 상태를 파괴될 때까지 그대로 간직한다.
    - 스레드 세이프, 안심하고 공유할 수 있다.
        - 불변 객체에서 자주 사용하는 인스턴스는 캐싱하여 정적 팩터리로 제공한다.
            - 자유롭게 공유가 가능하고 방어적 복사도 필요없다.
    - 불변 객체는 자유롭게 공유할 수 있고 불변 객체끼리 내부 데이터를 공유할 수 있다.
    - 객체를 만들 때 다른 불변 객체들을 구성요소로 사용하면 좋다.
    - 불변 객체는 그 자체로 실패 원자성을 제공한다.
- 불변 클래스의 단점
    - 값이 다르면 반드시 독립된 객체로 만들어야 한다
        - 값의 가짓수가 많아지면 모두 만들어야 한다.
        - 원하는 객체를 완성하기까지 단계가 많아지고 중간 단계의 객체들이 모두 버려진다면 성능 문제가 더 커진다.
            - 대처 방법
                - 다단계 연산을 예측하여 기본 기능으로 제공한다.
                - 클라이언트들이 원하는 복잡한 연산을 정확히 예측할 수 있다면 package-private의 가변 동반 클래스를 이용한다.
- 불변 클래스를 만드는 또 다른 설계 방법
    - public 정적 팩터리 제공
        - 다수의 구현 클래스를 활용한 유연성을 제공하고 다음 릴리스에서 객체 캐싱 기능을 추가해 성능을 끌어올릴 수도 있다.

        ```java
        public final class Complex {
            private final double re;
            private final double im;

        		... 중략

            private Complex(double re, double im) {
                this.re = re;
                this.im = im;
            }

            public static Complex valueOf(double re, double im) {
                return new Complex(re, im);
            }

            ... 생략
        }
        ```

    - BigInteger & BigDecimal
        - 두 클래스는 불변 클래스가 아니다.
        - 인수로 사용한다면 진짜 불변 객체로 사용하는지 확인해야 한다.
        - 불변 객체로 사용하지 않는다면 가변이라 가정하고 방어적으로 복사해야 한다.

        ```java
        public static BigInteger safeInstance(BigInteger val) {
        		return val.getClass() == BigInteger.class ?
        						val : new BigInteger(val.toByteArray());
        }
        ```

- 정리
    - 클래스는 꼭 필요한 경우가 아니라면 불변이여야 한다.
        - getter가 있다고 해서 무조건 setter를 만들지 말자
    - 불변으로 만들 수 없는 클래스라도 변경할 수 있는 부분을 최소한으로 줄이자
        - 합당한 이유가 없다면 모든 필드는 private final이어야 한다.
    - 생성자는 불변식 설정이 모두 완료된, 초기화가 완벽히 끝난 상태의 객체를 생성해야 한다.
        - 확실한 이유가 없다면 생성자와 정적 팩터리 외에 초기화 메서드는 public으로 제공하면 안된다.