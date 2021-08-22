package me.gaegul.ch04.item24;

public class NestedNonStaticClass {

    private final String name;

    public NestedNonStaticClass(String name) {
        this.name = name;
    }

    public String getName() {
        // 비정적 멤버 클래스와 바깥 클래스의 관계가 확립되는 부분
        NonStaticClass nonStaticClass = new NonStaticClass("nonStatic : ");
        return nonStaticClass.getNameWithOuter();
    }

    private class NonStaticClass {
        private final String nonStaticName;

        public NonStaticClass(String nonStaticName) {
            this.nonStaticName = nonStaticName;
        }

        public String getNameWithOuter() {
            // 정규화된 this를 이용해서 바깥 클래스의 인스턴스 메서드를 사용한다.
            return nonStaticName + NestedNonStaticClass.this.getName();
        }
    }

    public class NonStaticPublicClass {

    }

    public static void main(String[] args) {
        NestedNonStaticClass nestedNonStaticClass = new NestedNonStaticClass("name");
        nestedNonStaticClass.new NonStaticPublicClass();
    }
}
