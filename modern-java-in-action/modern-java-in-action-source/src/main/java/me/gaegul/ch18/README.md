# 18. 함수형 관점으로 생각하기

- 시스템 구현과 유지보수
    - 함수형 프로그래밍이 제공하는 부작용 없음, 불변성
    - 공유된 가변 데이터
        - 부작용
            - 자료구조를 고치거나 필드에 값을 할당
            - 예외 발생
            - 파일에 쓰기 등의 I/O 동작 수행
        - 순수 메서드(부작용 없는 메서드)
            - 자신을 포함하는 클래스의 상태 그리고 다른 객체의 상태를 바꾸지 않으며 return 문을 통해서만 자신의 결과를 반환하는 메서드
    - 선언형 프로그래밍
        - 스트림을 통한 내부 반복, 질의문 자체로 문제를 어떻게 푸는지 명확하게 보여준다
        - 원하는 것이 무엇이고 시스템이 어떻게 그 목표를 달성할 것인지 등의 규칙을 정하는 선언형 프로그래밍
    - 왜 함수형 프로그래밍인가?
        - 선언형 프로그래밍을 따르는 대표적인 방식
        - 부작용 없는 계산을 지향한다.
- 함수형 프로그래밍이란 무엇인가?
    - 함수, if-then-else 등 수학적 표현만 사용
    - 시스템의 다른 부분에 영향을 미치지만 않는다면 내부적으로는 함수형이 아닌 기능도 사용
    - 함수형 자바
        - 함수나 메서드는 지역 변수만 변경해야 한다.
        - 함수나 메서드에서 참조하는 객체는 불변 객체
    - 참조 투명성
        - 어떤 입력이 주어졌을 때 언제, 어디서 호출하든 같은 결과를 생성해야 한다.
        - 만약 함수가 가변 객체를 반환하면 반환 객체의 주소값은 항상 다르기 때문에 투명한 메서드가 아니다. 반환 객체가 불변이라면 참조적으로 투명하다.
    - 함수형 실전 연습
        - 서브 집합을 만드는 예제

            ```java
            public class FunctionPractice {

                public static void main(String[] args) {
                    List<List<Integer>> subs = subsets(Arrays.asList(1, 4, 9));
                    subs.forEach(System.out::println);
                }

                public static List<List<Integer>> subsets(List<Integer> list) {
                    if (list.isEmpty()) {
                        List<List<Integer>> ans = new ArrayList<>();
                        ans.add(Collections.emptyList());
                        return ans;
                    }

                    Integer first = list.get(0);
                    List<Integer> rest = list.subList(1, list.size());

                    List<List<Integer>> subans = subsets(rest);
                    List<List<Integer>> subans2 = insertAll(first, subans);
                    return concat(subans, subans2);
                }

                private static List<List<Integer>> insertAll(Integer first, List<List<Integer>> lists) {
                    List<List<Integer>> result = new ArrayList<>();
                    for (List<Integer> list : lists) {
                        List<Integer> copyList = new ArrayList<>();
                        copyList.add(first);
                        copyList.addAll(list);
                        result.add(copyList);
                    }
                    return result;
                }

                private static List<List<Integer>> concat(List<List<Integer>> a, List<List<Integer>> b) {
                    List<List<Integer>> r = new ArrayList<>(a);
                    r.addAll(b);
                    return r;
                }

            }
            ```

- 재귀와 반복
    - 순수 함수형 프로그래밍에는 while, for 같은 반복문을 포함하지 않는다. → 반복문 때문에 변화가 자연스럽게 코드에 스며들 수 있기 때문이다.
    - 반복 방식의 팩토리얼
        - 루프를 사용하여 매 반복마다 변수 r과 i가 갱신된다.

        ```java
        public static int factorialIterative(int n) {
            int r = 1;
            for (int i = 1; i <= n; i++) {
                r *= i;
            }
            return r;
        }
        ```

    - 재귀 방식의 팩토리얼
        - 일반적으로 반복보다 재귀 코드가 더 비싸다
        - 함수를 호출할 때마다 호출 스택에 각 호출시 생성되는 정보를 저장할 새로운 스택 프레임이 만들어진다.

            → 큰 값 입력 시 StackOverFlowError 발생 가능성이 높다

        ```java
        public static int factorialRecursion(int n) {
            return n == 1 ? 1 : n * factorialRecursion(n-1);
        }
        ```

    - 스트림 팩토리얼

        ```java
        public static long factorialStreams(long n) {
            return LongStream.rangeClosed(1, n)
                    .reduce(1, (long a, long b) -> a * b);
        }
        ```

    - 꼬리 재귀 팩토리얼
        - 재귀 호출이 마지막에서 이루어진다.
        - 고전 방식의 재귀보다 꼬리 재귀 방식은 추가적인 컴파일러 최적화를 기대할 수 있다.

        ```java
        public static long factorialTailRecursion(long n) {
            return factorialHelper(1, n);
        }

        public static long factorialHelper(long acc, long n) {
            return n == 1 ? acc : factorialHelper(acc * n, n-1);
        }
        ```