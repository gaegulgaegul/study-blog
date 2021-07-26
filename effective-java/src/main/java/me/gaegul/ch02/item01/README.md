# 아이템 1. 생성자 대신 팩터리 메서드를 고려하라

- 정적 팩터리 메서드가 생성자보다 좋은 장점
    - 이름을 가질 수 있다.
        - 생성자에 넘기는 매개변수와 생성자 자체만으로는 반환될 객체의 특성을 제대로 설명하지 못 한다.
        - 이름을 잘 지으면 반환될 객체의 특성을 쉽게 묘사할 수 있다.
        - 클래스에 시그니처가 같은 생성자가 여러 개 필요할 것 같으면 생성자를 정적 팩터리 메서드로 바꾸고 각각의 차이를 잘 들어내는 이름을 지어준다.
    - 호출될 때마다 인스턴스를 새로 생성하지 않아도 된다.
        - 불변 클래스는 인스턴스를 만들어 놓거나 새로 생성한 인스턴스를 캐싱하여 재활용한다. → 불필요한 객체 생성을 피한다.
        - 언제 어느 인스턴스를 살아 있게 할지 통제할 수 있다.
            - 싱글턴
            - 인스턴스화 불가
            - 불변 값 클래스에서 인스턴스가 단 하나뿐임을 보장
    - 반환 타입의 하위 타입 객체를 반환할 수 있는 능력이 있다.
        - 자바 8부터 인터페이스가 정적 메서드를 가질 수 없다는 제한이 없어 인스턴스화 불가 동반 클래스를 둘 이유가 없다.
        - 동반 클래스의 정적 멤버들을 인터페이스에 두면 된다.
    - 입력 매개변수에 따라 매번 다른 클래스의 객체를 반환할 수 있다.
        - 반환 타입의 하위 타입이기만 하면 어떤 클래스의 객체를 반환하든 상관없다.
        - 클라이언트는 팩터리가 건네주는 객체가 어느 클래스의 인스턴스인지 알 수 없고 알 필요도 없다.
    - 정적 팩터리 메서드를 작성하는 시점에는 반환할 객체의 클래스가 존재하지 않아도 된다.
        - 서비스 제공자 프레임워크
            - 서비스 인터페이스 → 구현체의 동작을 정의
            - 제공자 등록 API → 제공자가 등록할 때 사용
            - 서비스 접근 API → 클라이언트가 서비스의 인스턴스를 얻을 때 사용
        - 클라이언트는 서비스 접근 API를 사용할 때 원하는 구현체의 조건을 명시한다. 명시하지 않으면 기본 구현체를 반환하거나 지원하는 구현체들을 하나씩 돌아가며 반환한다.
- 정적 팩터리 메서드의 단점
    - 상속을 하려면 public이나 protected 생성자가 필요하니 정적 팩터리 메서드만 제공하면 하위 클래스를 만들 수 없다.
    - 정적 팩터리 메서드는 프로그래머가 찾기 어렵다.
        - 사용자가 정적 팩터리 메서드 방식 클래스를 인스턴스화할 방법을 알아내야 한다.