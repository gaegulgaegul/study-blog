# 아이템 22. 인터페이스는 타입을 정의하는 용도로만 사용하라

- 인터페이스의 용도
    - 자신을 구현한 클래스의 인스턴스를 참조할 수 있는 타입 역할
    - 클래스가 어떤 인터페이스를 구현한다는 것은 자신의 인스턴스로 무엇을 할 수 있는지 클라이언트에 얘기해주는 것

- 인터페이스의 잘못된 사용
    - 상수 인터페이스
        - 메서드 없이, static final 필드만 있는 인터페이스
            - 외부 인터페이스가 아니라 내부 구현에 해당 → 내부 구현 클래스의 API로 노출하는 행위
            - final이 아닌 클래스가 상수 인터페이스를 구현한다면 그 상수 인터페이스의 상수값 이름을 중복해서 사용할 수 없다.

            ```java
            public interface PhysicalConstants {
                
                // 아보가드로 수 (1/몰)
                static final double AVOGADROS_NUMBER = 6.022_140_857e23;
                
                // 볼츠만 상수 (J/K)
                static final double BOLTZMANN_CONSTANT = 1.380_648_52e-23;
                
                // 전자 질량 (kg)
                static final double ELECTRON_MASS = 9.109_383_56e-31;

            }
            ```

- 상수를 공개할 목적일 때 합당한 방법
    - 특정 클래스나 인터페이스에 강하게 연관된 상수라면 클래스나 인터페이스에 추가
        - Integer, Double → MIN_VALUE, MAX_VALUE
    - 열거 타입으로 나타내기 적합한 상수라면 열거 타입으로 공개
    - 유틸리티 클래스에 담아 공개

        ```java
        public class PhysicalConstants {

            private PhysicalConstants() {} // 인스턴스화 방지

            // 아보가드로 수 (1/몰)
            public static final double AVOGADROS_NUMBER = 6.022_140_857e23;

            // 볼츠만 상수 (J/K)
            public static final double BOLTZMANN_CONSTANT = 1.380_648_52e-23;

            // 전자 질량 (kg)
            public static final double ELECTRON_MASS = 9.109_383_56e-31;

        }
        ```