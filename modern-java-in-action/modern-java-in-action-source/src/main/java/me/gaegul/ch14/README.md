# 14. 자바 모듈 시스템

- 압력 : 소프트웨어 유추
    - 관심사분리
        - SoC 원칙
            - 모델, 뷰, 컨트롤러 같은 아키텍처 관점, 복구 기법을 비즈니스 로직관 분리하는 등의 하위 수준 접근 등의 상황에 유용
            - 개별 기능을 따로 작업할 수 있으므로 팀이 쉽게 협업할 수 있다.
            - 개별 부분을 재사용하기 쉽다.
            - 전체 시스템을 쉽게 유지보수할 수 있다.
    - 정보 은닉
        - 캡슐화된 코드의 내부적인 변화가 의도치 않게 외부에 영향을 미칠 가능성이 줄어든다.
        - 자바 9 이전까지는 클래스와 패키지가 의도된 대로 공개되었는지를 컴파일러로 확인할 수 없었다.
- 자바 모듈 시스템을 설계한 이유
    - 모듈화의 한계
        - 제한된 가시성 제어
            - 클래스를 다른 패키지에 공개하려면 public으로 선언해야 한다.
            - 인터페이스 기본 구현체를 구현할 때 "impl"이라는 패키지에 구현하게 되는데 이 때 인터페이스의 접근자는 public이 된다.
            - 내부적으로 사용할 목적으로 만든 구현을 다른 프로그래머가 임시적으로 수정하여 정착해버릴 수 있다.
            - 코드가 노출되어 임의로 조작하는 위협이 많아진다.
        - 클래스 경로
            - 같은 클래스를 구분하는 버전 개념이 없다.
            - 명시적인 의존성을 지원하지 않는다.
    - 거대한 JDK
        - 자바 모듈 : 큰 그림
            - 모듈 → module 새 키워드에 이름과 바디를 추가
            - 모듈 디스크립터 → [module-info.java](http://module-info.java) 파일에 저장
- 자바 모듈 시스템으로 어플리케이션 개발하기
    - 어플리케이션 셋업
        - 작업 처리 내역
            - 파일이나 URL에서 비용 목록을 읽는다.
            - 비용의 문자열 표현을 파싱한다.
            - 통계를 계산다.
            - 유용한 요약 정보를 표시한다.
            - 각 태스크의 시작, 마무리 지점을 제공한다.
        - 모듈화
            - 다양한 소스에서 데이터를 읽음 → Reader, HttpReader, FileReader
            - 다양한 포맷으로 구성된 데이터를 파싱 → Parser, JSONParser, ExpenseJSON-Parser)
            - 도메인 객체를 구체화 → Expense
            - 통계를 계산하고 반환 → SummaryCalculator, SummaryStatistics
        - 각 기능 그룹화
            - expenses.readers
            - expenses.readers.http
            - expenses.readers.file
            - expenses.parsers
            - expenses.parsers.json
            - expenses.model
            - expenses.statistics
            - expenses.application
    - 세부적인 모듈화와 거친 모듈화
        - 모듈화의 크기 결정
        - 세부적인 모듈화는 모든 패키지가 자신의 모듈을 갖는다. 이득에 비해 설계 비용이 증가한다.
        - 거친 모듈화는 한 모듈이 시스템의 모든 패키지를 포함한다. 모듈화의 장점을 잃는다.
    - 자바 모듈 시스템 기초
        - 최상위 수준에 module-info.java를 생성한다.
        - 모듈 디스크립터로 모듈의 소스 코드 파일 루트에 위치해야 하며 모듈의 의존성 그리고 어떤 기능을 외부로 노출할지 정의한다.
    - 여러 모듈 이용하기
        - exports 구문
            - 다른 모듈에서 사용할 수 있도록 특정 패키지를 공개 형식으로 만든다.
        - requires 구문
            - 의존하고 있는 모듈을 지정한다.
            - 기본적으로 모든 모듈은 java.base라는 플랫폼 모듈에 의존한다.
    - 컴파일링과 패키징
        - pom.xml을 추가해 maven을 통해 프로젝트 빌드를 조정할 수 있도록 한다.
- 자동 모듈
    - 메이븐 dependency를 추가해 프로젝트 빌드하면 모듈로 등록되어 있지 않아도 자바는 자동 모듈이라는 형태로 적절하게 변환한다.
    - 암묵적으로 자신의 모든 패키지를 노출시킨다.
    - 자동 모듈의 이름은 jar 이름을 이용해 정해진다.
    - jar의 `—describe-module` 인수를 사용해 자동으로 정해지는 이름을 바꿀 수 있다.
- 모듈 정의와 구문들
    - requires
        - 컴파일 타임과 런타임에 한 모듈이 다른 모듈에 의존함을 정의한다.
    - exports
        - 지정한 패키지를 다른 모듈에서 이용할 수 있도록 공개 형식으로 만든다.
        - 어떤 패키지를 공개할지 명시적으로 지정하여 캡슐화를 높일 수 있다.
    - requires transitive
        - 다른 모듈이 제공하는 공개 형식을 한 모듈에서 사용할 수 있다고 지정할 수 있다.
        - 필요로 하는 모듈이 다른 모듈의 형식을 반환하는 상황에서 전이성(transitive) 선언을 유용하게 사용할 수 있다.
    - exports to
        - 사용자에게 공개할 기능을 제한함으로 가시성을 좀 더 정교하게 제어할 수 있다.
    - open
        - 모든 패키지를 반사적으로 접근을 허용할 수 있다.
    - opens
        - 필요한 개별 패키지만 개방할 수 있다.
    - uses와 provides
        - provides 구문으로 서비스 제공자를 uses 구문으로 서비스 소비자를 지정할 수 있는 기능을 제공한다.