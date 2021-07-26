package me.gaegul.ch02.item02.builder;

import lombok.ToString;

import java.util.Objects;

@ToString
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
