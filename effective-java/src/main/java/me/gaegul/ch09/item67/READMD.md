# 아이템 67. 최적화는 신중히 하라

### 최적화의 어두운 진실

- 최적화는 좋은 결과보다는 해로운 결과로 이어지기 쉽고, 섣불리 진행하면 더 그렇다.
- 빠르지도 않고 제대로 동작하지도 않으면서 수정하기 어려운 소프트웨어를 탄생시키는 것이다.

### 빠른 프로그램보다는 좋은 프로그램을 작성하라

- 좋은 프로그램이지만 원하는 성능이 나오지 않는다면 아키텍처 자체가 최적화 할 수 있는 길을 안내해줄 것이다.
    - 좋은 프로그램은 정보은닉의 원칙을 따르므로 개별 구성 요소의 내부를 독립적으로 설계할 수 있다.
    - *시스템의 나머지에 영향을 주지 않고도 각 요소를 다시 설계할 수 있다.*
        - 성능 문제를 무시하는 것이 아니라 아키텍처의 결함이 성능에 영향을 준다면 시스템 전체를 다시 작성하지 않고는 해결하기 불가능할 수 있다.

### 성능을 제한하는 설계를 피하라

- 완성 후 변경하기 가장 어려운 설계 요소는 컴포넌트끼리, 외부 시스템과의 소통 방식이다.
    - API, 네트워크 프로토콜, 영구 저장용 데이터 포맷 등

### API를 설계할 때 성능에 주는 영향을 고려하라

- public 타입을 가변으로 만들면 불필요한 방어적 복사를 유발한다.(아이템 50)
- 컴포지션으로 해결할 수 있는 상속 방식으로 설계한 public 클래스는 상위 클래스에 종속되어 성능 제약도 물려받는다.(아이템 18)
- 구현 타입 대신 인터페이스를 사용하는 것이 좋다.(아이템 64)
- 잘 설계된 API는 성능도 좋은게 보통이다.
    - API를 왜곡하도록 만든 성능 문제는 다음 버전에서 사라질 수 있지만 왜곡된 API와 사용된 코드는 수정해야 한다는 문제가 있다.

### 각각의 최적화 시도 전후로 성능을 측정하라

- 프로파일링 도구는 최적화 노력을 어디에 집중해야 할지 찾는데 도움을 준다.
    - 개별 메서드의 소비 시간과 호출 횟수 같은 런타임 정보를 제공한다.
    - 변경해야 하는 알고리즘도 알려준다.
- JMH → 자바의 상세 성능을 알기 쉽게 보여주는 프레임 워크

<aside>
💡 빠른 프로그램을 작성하려 안달하지 말자. 좋은 프로그램을 작성하다보면 성능은 따라 오기 마련이다.

</aside>