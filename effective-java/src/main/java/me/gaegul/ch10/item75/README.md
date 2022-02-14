# 아이템 75. 예외의 상세 메시지에 실패 관련 정보를 담으라

### 스택 추적

- 예외 객체의 `toString` 메서드를 호출해 얻는 문자열, 보통 예외의 클래스 이름 뒤에 상세 메시지가 붙는 형태
- 예외를 잡지 못해 프로그램이 실패하면 자바 시스템은 그 예외의 스택 추적 정보를 자동 출력한다.
- 실패 원인을 분석해야 하는 프로그래머 또는 SRE(사이트 신뢰성 엔지니어)가 얻을 수 있는 유일한 정보인 경우가 많다.
- 실패를 재현하기 어렵다면 더 자세한 정보를 얻기가 어렵거나 불가능하다.

### 실패 순간을 포착하려면 발생한 예외에 관여된 모든 매개변수와 필드의 값을 실패 메시지에 담아야 한다.

- `IndexOutOfBoundsException`의 상세 메시지는 범위의 최솟값, 최댓값, 범위를 벗어났다는 인덱스 값을 담아야 한다.
- 이상 현상은 모두 원인이 다르니 현상을 보면 무엇을 고쳐야 할지 분석하는데 도움이 된다.
    - 상세 메시지에 비밀번호나 암호 키 같은 정보를 담으면 안 된다.
    - 관련 데이터는 장황하면 안 된다. → 문제를 분석할 때 스택 추적과 소스 코드를 함께 확인한다.

### 예외의 상세 메시지와 최종 사용자에게 보여줄 오류 메시지를 혼동하면 안된다.

- 최종 사용자에게는 친절한 안내 메시지를 보여줘야 한다.
- 예외 메시지는 가독성보다는 담긴 내용이 중요하다.

### 실패를 포착하려면 필요한 정보를 예외 생성자에 모두 받아서 상세 메시지까지 미리 생성해놓는 방법도 괜찮다.

```java
/**
 * CustomIndexOutOfBoundsException 생성한다.
 * 
 * @param lowerBound 인덱스의 최솟값
 * @param upperBound 인덱스의 최댓값 + 1
 * @param index 인덱스의 실젯값
 */
public CustomIndexOutOfBoundsException(int lowerBound, int upperBound, int index) {
    // 실패를 포착하는 상세 메시지를 생성한다.
    super(String.format("최솟값: %d, 최댓값: %d, 인덱스: %d", lowerBound, upperBound, index));
    
    // 프로그램에서 이용할 수 있는 실패 정보를 저장해둔다.
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
    this.index = index;
}
```