package me.gaegul.ch11.before;

public class App {

    public static void main(String[] args) {
        Person person = new Person();
        getCarInsuranceName(person);
        getCarInsuranceName2(person);
    }

    public static String getCarInsuranceName(Person person) {
        if (person != null) {
            Car car = person.getCar();
            if (car != null) {
                Insurance insurance = car.getInsurance();
                if (insurance != null) {
                    return insurance.getName();
                }
            }
        }
        return "Unknown";
    }

    public static String getCarInsuranceName2(Person person) {
        if (person == null) {
            return "Unknown";
        }

        Car car = person.getCar();
        if (car == null) {
            return "Unknown";
        }

        Insurance insurance = car.getInsurance();
        if (insurance == null) {
            return "Unknown";
        }
        return insurance.getName();
    }
}
