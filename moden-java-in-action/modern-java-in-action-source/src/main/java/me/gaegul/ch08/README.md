# 8. 컬렉션 API 개선

- 컬렉션 팩토리
    - Arrays.asList() 팩토리 메서드를 사용하면 코드를 간단하게 줄일 수 있다.
    - 요소를 추가하려 하면 `UnsupportedOperationException`이 발생한다.
    - 자바 9에 추가된 **List.of 팩토리 메서드**를 사용한다.
    - 리스트 팩토리
        - List.of
            - immutable 리스트가 만들어진다.
            - null 요소는 금지하여 의도치 않은 버그를 방지한다.
    - 집합 팩토리
        - Set.of
            - List.of와 비슷한 방법으로 바꿀 수 없는 집합
    - 맵 팩토리
        - Map.of
            - 키와 값을 번갈아 제공하는 방법
            - 10개 이하의 키와 값 쌍을 가지는 작은 맵을 만들기에 유용
        - Map.ofEntries
            - entry 객체를 인수로 받는다.
            - 키와 값을 감쌀 추가 객체 할당을 필요로 한다.(Map.entry 팩토리 메서드)
- 리스트와 집합 처리
    - removeIf
        - 프레디케이트를 만족하는 요소를 제거한다.
        - List나 Set을 구현하거나 그 구현을 상속받은 모든 클래스에서 이용할 수 있다.
    - replaceAll
        - 리스트에서 이용할 수 있는 기능
        - UnaryOperator 함수를 이용해 요소를 바꾼다.
    - sort
        - List를 정렬한다.
- 맵 처리
    - forEach
        - BiConsumer를 인수로 받는 forEach
    - 정렬 메서드
        - Entry.comparingByValue
        - Entry.comparingByKey

        ```java
        favouriteMovies
        	.entrySet()
        	.stream()
        	.sorted(Entry.comparingByKey())
        	.forEachOrdered(System.out::println);
        ```

    - getOrDefault
        - 키가 존재하더라고 값이 null인 상황에서는 null을 반환할 수 있다.
        - 키의 값이 존재하는지 여부를 확인하여 존재하지 않으면 기본값을 반환한다.
    - 계산 패턴
        - computeIfAbsent
            - 제공된 키에 해당 값이 없으면 키를 이용해서 새 값을 계산하고 맵에 추가한다.
        - computeIfPresent
            - 제공된 키가 존재하면 새 값을 계산하고 맵에 추가한다.
        - compute
            - 제공된 키로 새 값을 계산하고 맵에 저장한다.
    - 삭제 패턴
        - remove
            - 자바 8에서 오버로드 되었다.
            - 제공된 키와 해당 값까지 일치하는 경우 삭제
            - `favouriteMovies.remove(key, value)`
    - 교체 패턴
        - replaceAll
            - BiFunction을 적용한 결과로 각 항목의 값을 교체한다.
        - replace
            - 키가 존재하면 맵의 값을 바꾼다.
            - 키가 특정 값으로 매핑되었을 때만 값을 교체하도록 오버로드 버전도 있다.
- ConcurrentHashMap
    - 동시성 친화적
    - 내부 자료구조의 특정 부분만 잠궈 동시 추가, 갱신 작업을 허용한다.
    - HashMap에 비해 읽기 쓰기 연산 성능이 월등하다.
    - 리듀스와 검색
        - 연산
            - forEach → 각 (키, 값) 쌍에 주어진 액션을 실행
            - reduce → 모든 (키, 값) 쌍을 제공된 리듀스 함수를 이용해 결과로 합침
            - search → 널이 아닌 값을 반환할 때까지 각 (키, 값) 쌍에 함수를 적용
        - 연산 형태
            - 키, 값으로 연산 → forEach, reduce, search
            - 키로 연산 → forEachkey, reduceKeys, searchKeys
            - 값으로 연산 → forEachValue, reduceValues, searchValues
            - map.Entry 객체로 연산 → forEachEntry, reduceEntries, searchEntries
        - 특징
            - 연산에 제공한 함수는 계산이 진행되는 동안 바뀔 수 있는 객체, 값, 순서 등에 의존하지 않아야 한다.
            - 병렬성 기준값(threshold)을 지정해야 한다.
                - 기준값이 낮을수록 병렬성 극대화
            - reduceValuesToInt, reduceValuesToLong 등을 이용하면 박싱 작업을 할 필요가 없다.