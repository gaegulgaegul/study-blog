# 아이템 2. 생성자에 매개변수가 많다면 빌더를 고려하라

- 정적 팩터리 메서드와 생성자의 똑같은 제약
    - 점층적 생성자 패턴
        - 확장하기 어렵다.
        - 매개변수가 많아지면 클라이언트 코드를 작성하거나 읽기가 어렵다.

        ```java
        public class NutritionFacts {

            private final int servingSize;  // (ml, 1회 제공량)      필수
            private final int servings;     // (회, 총 n회 제공량)    필수
            private final int calories;     // (1회 제공량당)         선택
            private final int fat;          // (g/1회 제공량)        선택
            private final int sodium;       // (mg/1회 제공량)       선택
            private final int carbohydrate; // (g/1회 제공량)        선택

            public NutritionFacts(int servingSize, int servings) {
                this(servingSize, servings, 0);
            }

            public NutritionFacts(int servingSize, int servings, int calories) {
                this(servingSize, servings, calories, 0);
            }

            public NutritionFacts(int servingSize, int servings, int calories, int fat) {
                this(servingSize, servings, calories, fat, 0);
            }

            public NutritionFacts(int servingSize, int servings, int calories, int fat, int sodium) {
                this(servingSize, servings, calories, fat, sodium, 0);
            }

            public NutritionFacts(int servingSize, int servings, int calories, int fat, int sodium, int carbohydrate) {
                this.servingSize = servingSize;
                this.servings = servings;
                this.calories = calories;
                this.fat = fat;
                this.sodium = sodium;
                this.carbohydrate = carbohydrate;
            }
        }
        ```

    - 자바빈즈 패턴
        - 생성자의 매개변수가 많을 때 활용할 수 있다.
        - 매개변수가 없는 생성자를 객체로 만들어 setter로 매개변수의 값을 설정하는 방식
        - 객체 하나를 만들고 메서드를 여러 개 호출해야 하고, 객체가 완전히 생성되기 전까지는 일관성이 무너진 상태다.
        - 객체를 불변으로 만들 수 없다. 스레드 안정성을 가지려면 프로그래머가 추가 작업을 해줘야 한다.

        ```java
        public class NutritionFacts {

            // 매개변수들은 기본값으로 초기화
            private int servingSize = -1;  // (ml, 1회 제공량)      필수
            private int servings = -1;     // (회, 총 n회 제공량)    필수
            private int calories = 0;     // (1회 제공량당)         선택
            private int fat = 0;          // (g/1회 제공량)        선택
            private int sodium = 0;       // (mg/1회 제공량)       선택
            private int carbohydrate = 0; // (g/1회 제공량)        선택

            public NutritionFacts() {
            }

            // setter method
            public void setServingSize(int servingSize) {
                this.servingSize = servingSize;
            }

            public void setServings(int servings) {
                this.servings = servings;
            }

            public void setCalories(int calories) {
                this.calories = calories;
            }

            public void setFat(int fat) {
                this.fat = fat;
            }

            public void setSodium(int sodium) {
                this.sodium = sodium;
            }

            public void setCarbohydrate(int carbohydrate) {
                this.carbohydrate = carbohydrate;
            }
        }
        ```

    - 빌더 패턴
        - 점층적 생성자 패턴과 자바빈즈 패턴의 장점만 가진다.
        - 클라이언트 코드에서 사용할 때 가독성이 좋다.
        - 잘못된 매개변수를 발견하려면
            - 빌더의 생성자와 메서드에서 입력 매개변수를 검사한다.
            - build 메서드가 호출되는 생성자에서 여러 매개변수에 걸친 불변식을 검사한다.

        ```java
        public class NutritionFacts {

            private final int servingSize;  // (ml, 1회 제공량)      필수
            private final int servings;     // (회, 총 n회 제공량)    필수
            private final int calories;     // (1회 제공량당)         선택
            private final int fat;          // (g/1회 제공량)        선택
            private final int sodium;       // (mg/1회 제공량)       선택
            private final int carbohydrate; // (g/1회 제공량)        선택

            public static class Builder {
                private final int servingSize;
                private final int servings;

                private int calories = 0;
                private int fat = 0;
                private int sodium = 0;
                private int carbohydrate = 0;

                public Builder(int servingSize, int servings) {
                    this.servingSize = servingSize;
                    this.servings = servings;
                }

                public Builder calories(int val) {
                    calories = val;
                    return this;
                }

                public Builder fat(int val) {
                    fat = val;
                    return this;
                }

                public Builder sodium(int val) {
                    sodium = val;
                    return this;
                }

                public Builder carbohydrate(int val) {
                    carbohydrate = val;
                    return this;
                }

                public NutritionFacts build() {
                    return new NutritionFacts(this);
                }
            }

            private NutritionFacts(Builder builder) {
                servingSize = builder.servingSize;
                servings = builder.servings;
                calories = builder.calories;
                fat = builder.fat;
                sodium = builder.sodium;
                carbohydrate =builder.carbohydrate;
            }
        }
        ```

    - 계층적으로 성계된 클래스와 함께 쓰는 것이 좋다.
        - 재귀적 타입 한정을 이용하는 제네릭 타입
        - 추상 메서드 self를 더해 하위 클래스에서는 형변환을 하지 않고 메서드 연쇄를 지원할 수 있다.

        ```java
        public abstract class Pizza {
            public enum Topping {
                HAM, MUSHROOM, ONION, PEPPER, SAUSAGE
            }
            final Set<Topping> toppings;
            
            abstract static class Builder<T extends Builder<T>> {
                EnumSet<Topping> toppings = EnumSet.noneOf(Topping.class);
                public T addTopping(Topping topping) {
                    toppings.add(Objects.requireNonNull(topping));
                    return self();
                }
                
                abstract Pizza Build();
                
                // 하위 클래스는 이 메서드를 재정의 하여
                // "this"를 반환하도록 해야 한다.
                protected abstract T self();
            }
            
            Pizza(Builder<?> builder) {
                toppings = builder.toppings.clone();
            }
            
        }
        ```

        ```java
        public class NyPizza extends Pizza {
            public enum Size {
                SMALL, MEDIUM, LARGE
            }
            private final Size size;

            public static class Builder extends Pizza.Builder<Builder> {
                private final Size size;

                public Builder(Size size) {
                    this.size = Objects.requireNonNull(size);
                }

                @Override
                Pizza build() {
                    return new NyPizza(this);
                }

                @Override
                protected Builder self() {
                    return this;
                }
            }

            private NyPizza(Builder builder) {
                super(builder);
                size = builder.size;
            }
        }
        ```

        ```java
        public class Calzone extends Pizza {
            private final boolean sauceInside;

            public static class Builder extends Pizza.Builder<Builder> {
                private boolean sauceInside = false;

                public Builder sauceInside() {
                    sauceInside = true;
                    return this;
                }

                @Override
                Pizza build() {
                    return new Calzone(this);
                }

                @Override
                protected Builder self() {
                    return this;
                }
            }

            private Calzone(Builder builder) {
                super(builder);
                this.sauceInside = builder.sauceInside;
            }
        }
        ```

- 빌더 패턴 특징
    - 빌더 패턴은 상당히 유연하다.
        - 여러 객체를 순회하면서 만들 수 있고, 빌더에 넘기는 매개변수에 따라 다른 객체를 만들 수 있다.
    - 빌더 생성 비용이 크지 않지만 성능에 민감한 상황에서는 문제가 될 수 있다.
    - 매개변수가 4개 이상일 경우 값어치가 좋다.