package me.gaegul.ch11.Optional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class App {

    public static void main(String[] args) {
        createOptional();
        mapWithOptional();
        getCarInsuranceName(Optional.ofNullable(new Person()));
        getCarInsuranceNames(List.of(new Person()));

        nullSafeFindCheapestInsurance(Optional.ofNullable(new Person()), Optional.ofNullable(new Car()));
        nullSafeFindCheapestInsuranceWithFlatMap(Optional.ofNullable(new Person()), Optional.ofNullable(new Car()));

        Optional.ofNullable(new Insurance()).filter(insurance -> "CambridgeInsurance".equals(insurance.getName()))
                .ifPresent(x -> System.out.println("ok"));
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

    public static Set<String> getCarInsuranceNames(List<Person> persons) {
        return persons.stream()
                .map(Person::getCar)
                .map(optCar -> optCar.flatMap(Car::getInsurance))
                .map(optIns -> optIns.map(Insurance::getName))
                .flatMap(Optional::stream)
                .collect(toSet());
    }

    public static Insurance findCheapestInsurance(Person person, Car car) {
        Insurance CheapestCompany = new Insurance();
        return CheapestCompany;
    }

    public static Optional<Insurance> nullSafeFindCheapestInsurance(Optional<Person> person, Optional<Car> car) {
        if (person.isPresent() && car.isPresent()) {
            return Optional.of(findCheapestInsurance(person.get(), car.get()));
        }
        return Optional.empty();
    }

    public static Optional<Insurance> nullSafeFindCheapestInsuranceWithFlatMap(Optional<Person> person, Optional<Car> car) {
        return person.flatMap(p -> car.map(c -> findCheapestInsurance(p, c)));
    }

   public static Optional<Integer> stringToInt(String s) {
        try {
            return Optional.of(Integer.parseInt(s));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
   }
}
