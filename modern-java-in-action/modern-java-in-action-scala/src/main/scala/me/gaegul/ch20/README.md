# 20. OOP와 FP의 조화 : 자바와 스칼라 비교

- 스칼라 소개
    - Hello beer
        - 문자열 보간법 → 문자열 자체에 변수와 표현식을 바로 삽입하는 기능

        ```scala
        object Beer {
          def main(args: Array[String]): Unit = {
            var n : Int = 2
            while (n <= 6) {
              println(s"Hello ${n} bottles of beer")
              n += 1
            }
          }
        }
        ```

    - 함수형 스칼라

        ```scala
        object Beer {
          def main(args: Array[String]) {
            2 to 6 foreach { n => println(s"Hello ${n} bottles of beer") }
          }
        }
        ```

    - 기본 자료구조 : 리스트, 집합, 맵, 튜플, 스트림, 옵션
        - 컬렉션 만들기
            - 스칼라의 간결성을 강조하는 특성 덕분에 간단한 컬렉션을 만들 수 있다.

            ```scala
            val authorsToAge = Map("Raoul" -> 23, "Mario" -> 40, "Alen" -> 53)
            ```

        - 불변과 가변
            - 컬렉션은 기본적으로 불변
            - 기존 컬렉션과 많은 자료를 공유하는 새로운 컬렉션을 만드는 방법으로 자료구조를 갱신한다.
        - 컬렉션 사용하기
            - 컬렉션 동작은 스트림 API와 비슷하다.
            - 인픽스 개념 → 언더스코어를 인수로 대치할 수 있다.
            - 병렬 실행

            ```scala
            var fileLines = Source.fromFile("data.txt").getLines.toList()
            val linesLongUpper = fileLines.filter(l => l.length() > 0)
                                          .map(l => l.toUpperCase())

            ==========

            val linesLongUpper = fileLines.filter(_.length() > 0).map(_.toUpperCase())

            ==========

            val linesLongUpper = fileLines.par filter (_.length() > 0) map(_.toUpperCase())
            ```

        - 튜플
            - 형식에 상관없이 집합을 만든다.

            ```scala
            object Tuples {
              def main(args: Array[String]) {
                val book = (2018, "Modern Java in Action");
                val numbers = (42, 1337, 0, 3, 7);
                println(book)
                println(numbers._4)
              }
            }
            ```

        - 스트림
            - 스트림의 인덱스를 제공하여 리스트처럼 인덱스로 스트림의 요소에 접근할 수 있다.
            - 스칼라의 스트림은 자바의 스트림보다 메모리 효율성이 조금 떨어진다.
        - 옵션
            - 자바의 Optional과 같은 기능을 제공한다.

            ```scala
            def getCarInsuranceName(person: Option[Person], minAge: Int) =
                person.filter(_.age >= minAge)
                  .flatMap(_.car)
                  .flatMap(_.insurance)
                  .map(_.name)
                  .getOrElse("Unknown")
            ```

- 함수
    - 스칼라의 일급 함수
        - 스칼라의 함수는 일급값

        ```scala
        object Tweets {

          def isJavaMentioned(tweet: String) : Boolean = tweet.contains("Java")
          def isShortTweet(tweet: String) : Boolean = tweet.length() < 20

          def main(args: Array[String]) {
            val tweets = List(
              "I love the new features in Java 8",
              "How's it going?",
              "An SQL query walks into a bar, sees two tables and says 'Can I join you?'"
            )

            tweets.filter(isJavaMentioned).foreach(println)
            tweets.filter(isShortTweet).foreach(println)
          }

        }
        ```

    - 익명 함수와 클로저
        - 익명 함수 → 람다 표현식과 비슷한 문법 제공

        ```scala
        val isLongTweet : String => Boolean = (tweet : String) => tweet.length() > 60
        tweets.map(isLongTweet).foreach(println)
        ```

        - 클로저
            - 함수의 비지역 변수를 자유롭게 참조할 수 있는 함수의 인스턴스

        ```scala
        object Closure {

          def main(args: Array[String]) {
            var count = 0
            val inc = () => count += 1
            inc()
            println(count)
            inc()
            println(count)
          }

        }
        ```

    - 커링
        - 파라미터 하나를 포함하는 인수 리스트를 받을 수 있다.

        ```scala
        object Currying {

          def main(args: Array[String]) {
            def multiply(x : Int, y : Int) = x * y
            val r1 = multiply(2, 10);
            println(r1)

            def multiplyCurry(x : Int)(y : Int) = x * y
            val r2 = multiplyCurry(2)(10)
            println(r2)

            def multiplyByTwo : Int => Int = multiplyCurry(2)
            val r3 = multiplyByTwo(10)
            println(r3)
          }

        }
        ```

- 클래스와 트레이트
    - 간결성을 제공하는 스칼라 클래스
        - 자바에서 클래스를 만들고 인스턴스화하는 방법과 문법적으로 비슷하다.
        - 생성자, getter, setter는 암시적으로 생성된다.

        ```scala
        class Student(var name: String, var id: Int)

        object Student {
          def main(args: Array[String]) {
            val s = new Student("Raoul", 1)
            println(s.name)
            s.id = 1337
            println(s.id)
          }
        }
        ```

    - 스칼라 트레이트와 자바 인터페이스
        - 트레이트
            - 추상 기능 제공
            - 자바의 인터페이스와 디폴트 메서드 기능이 합쳐진 것으로 이해
            - 인스턴스화 과정에서 조합할 수 있다.

            ```scala
            trait Sized {
              var size : Int = 0
              def isEmpty() = size == 0
            }

            object SizedRunner {
              def main(args: Array[String]) {
                class Empty extends Sized
                println(new Empty().isEmpty())
                
                class Box
                val b1 = new Box() with Sized
                println(b1.isEmpty())
              }
            }
            ```