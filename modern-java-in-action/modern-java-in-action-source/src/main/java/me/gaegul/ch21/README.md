# 21. 결론 그리고 자바의 미래

- 자바 8의 기능 리뷰
    - 변화가 생긴 이유
        - 멀티코어 프로세서의 파워를 충분히 활용 → 코드를 병렬로 실행
        - 선언형으로 데이터를 처리하는 방식 → 데이터 컬렉션, 불변 객체 및 컬렉션
    - 동작 파라미터화
        - 람다, 메서드 참조
    - 스트림
        - 컬렉션과 다른점
            - 컬렉션은 3가지 연산은 각각 3번 탐색해야 한다.
            - 스트림은 연산은 파이프라인이라는 게으른 형식의 연산으로 구성하고 한번의 탐색으로 모든 연산을 수행한다.
        - 큰 데이터 집합일수록 스트림 데이터 처리방식이 효율적이고 메모리 캐시 등의 관점에서 탐색 횟수를 최소하는 것이 중요하다.
    - CompletableFuture 클래스
        - CompletableFuture와 Future의 관계는 스트림과 컬렉션의 관계와 같다.
        - CompletableFuture는 Future와 관련한 공통 디자인 패턴을 함수형 프로그래밍으로 간결하게 표현하도록 thenCompose, thenCombine, allOf 등을 제공
    - Optional 클래스
        - 에러가 발생할 수 있는 계산을 수행하면서 값이 없을 때 에러를 발생시킬 수 있는 null 대싱 정해진 데이터 형식을 제공할 수 있다.
    - Flow API
        - 리액티브 스트림과 리액티브 pull 기반 역압력 프로토콜을 표준화
        - Publisher, Subscriber, Subscription, Processor를 포함한다.
    - 디폴트 메서드
        - 인터페이스에 추가할 수 있고 인터페이스 설계 시 메서드의 기본 구현을 제공할 수 있다.
        - 디폴트 메서드 덕분에 인터페이스가 바뀌어도 사용자는 신경 쓰지 않아도 된다.
- 자바 9 모듈 시스템
    - 자바 모듈 시스템의 장점
        - 안정적 설정 → 모듈 요구사항을 명시적으로 선언하여 의존성 빠짐, 충돌, 순환 등의 문제를 빌드 과정에서 확인
        - 강한 캡슐화 → 특정 패키지만 노출하여 공개할 부분과 내부 구현의 영역 접근을 분리
        - 보안성 개선 → 사용자가 모듈 특정 부분을 사용할 수 없다.
        - 성능 개선 → 적은 수의 컴포넌트를 참조할 때 최적화 기술이 더 효과를 발휘한다.
        - 확장성 → 실행중인 어플리케이션에서 필요한 부분만 사용할 수 있다.
- 자바 10 지역 변수형 추론
    - var
        - 한개의 식별자로 구성된 형식에 형식 추론을 사용할 수 있다.
        - 상속받는 식별자는 형식 추론이 변수의 형식과 초기화의 형식이 같다고 인식한다. → 다형성 사용 불가

    ```java
    var myMap = new HashMap<String, List<String>>();
    ```