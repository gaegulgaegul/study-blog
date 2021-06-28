package me.gaegul.ch11.Optional;

import java.util.Optional;

public class App {

    public static void main(String[] args) {
        createOptional();
        mapWithOptional();
        getCarInsuranceName(Optional.ofNullable(new Person()));
    }

    public static void createOptional() {
        Optional<Car> optCar = Optional.empty();

        Car car = new Car();
        Optional<Car> car1 = Optional.of(car);

        Optional<Car> car2 = Optional.ofNullable(car);
    }

    public static void mapWithOptional() {
        Insurance insurance = new Insurance();
        Optional<Insurance> optInsurance = Optional.ofNullable(insurance);
        Optional<String> name = optInsurance.map(Insurance::getName);
    }

    public static String getCarInsuranceName(Optional<Person> person) {
        return person.flatMap(Person::getCar)
                .flatMap(Car::getInsurance)
                .map(Insurance::getName)
                .orElse("Unknown");
    }
}
