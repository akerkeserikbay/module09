import java.util.*;

// FACADE (HOTEL SYSTEM)

// Room Booking
class RoomBookingSystem {
    public void bookRoom(String name) {
        System.out.println("Room booked for " + name);
    }

    public void cancelBooking(String name) {
        System.out.println("Booking cancelled for " + name);
    }

    public void checkAvailability() {
        System.out.println("Rooms are available");
    }
}

// Restaurant
class RestaurantSystem {
    public void bookTable(String name) {
        System.out.println("Table booked for " + name);
    }

    public void orderFood(String food) {
        System.out.println("Food ordered: " + food);
    }
}

// Event Management
class EventManagementSystem {
    public void bookHall(String event) {
        System.out.println("Hall booked for " + event);
    }

    public void orderEquipment(String equipment) {
        System.out.println("Equipment ordered: " + equipment);
    }
}

// Cleaning
class CleaningService {
    public void scheduleCleaning(String room) {
        System.out.println("Cleaning scheduled for room: " + room);
    }

    public void cleanNow(String room) {
        System.out.println("Cleaning done in room: " + room);
    }
}

// Taxi (доп функция)
class TaxiService {
    public void callTaxi(String name) {
        System.out.println("Taxi called for " + name);
    }
}

// FACADE
class HotelFacade {
    private RoomBookingSystem room = new RoomBookingSystem();
    private RestaurantSystem restaurant = new RestaurantSystem();
    private EventManagementSystem event = new EventManagementSystem();
    private CleaningService cleaning = new CleaningService();
    private TaxiService taxi = new TaxiService();

    // Бронирование номера + услуги
    public void bookRoomWithServices(String name) {
        System.out.println("\n--- Booking Room With Services ---");
        room.bookRoom(name);
        restaurant.orderFood("Dinner");
        cleaning.scheduleCleaning("Room for " + name);
    }

    // Мероприятие
    public void organizeEvent(String eventName, String participants) {
        System.out.println("\n--- Organizing Event ---");
        event.bookHall(eventName);
        event.orderEquipment("Projector");
        room.bookRoom(participants);
    }

    // Ресторан + такси
    public void bookTableWithTaxi(String name) {
        System.out.println("\n--- Restaurant Booking ---");
        restaurant.bookTable(name);
        taxi.callTaxi(name);
    }

    // Отмена
    public void cancelRoom(String name) {
        room.cancelBooking(name);
    }

    // Уборка по запросу
    public void requestCleaning(String roomName) {
        cleaning.cleanNow(roomName);
    }
}

// COMPOSITE (ORGANIZATION)

// Base
abstract class OrganizationComponent {
    protected String name;

    public OrganizationComponent(String name) {
        this.name = name;
    }

    public void add(OrganizationComponent c) {}
    public void remove(OrganizationComponent c) {}

    public abstract void display(int depth);
    public abstract double getBudget();
    public abstract int getEmployeeCount();
}

// Employee
class Employee extends OrganizationComponent {
    private String position;
    private double salary;

    public Employee(String name, String position, double salary) {
        super(name);
        this.position = position;
        this.salary = salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    @Override
    public void display(int depth) {
        System.out.println("  ".repeat(depth) + "- " + name + " (" + position + ", $" + salary + ")");
    }

    @Override
    public double getBudget() {
        return salary;
    }

    @Override
    public int getEmployeeCount() {
        return 1;
    }
}

// Contractor (не входит в бюджет)
class Contractor extends Employee {
    public Contractor(String name, String position, double salary) {
        super(name, position, salary);
    }

    @Override
    public double getBudget() {
        return 0; // не учитывается
    }
}

// Department
class Department extends OrganizationComponent {
    private List<OrganizationComponent> children = new ArrayList<>();

    public Department(String name) {
        super(name);
    }

    @Override
    public void add(OrganizationComponent c) {
        if (c == null) return;
        if (!children.contains(c)) {
            children.add(c);
        }
    }

    @Override
    public void remove(OrganizationComponent c) {
        children.remove(c);
    }

    @Override
    public void display(int depth) {
        System.out.println("  ".repeat(depth) + "+ Department: " + name);
        for (OrganizationComponent c : children) {
            c.display(depth + 1);
        }
    }

    @Override
    public double getBudget() {
        double total = 0;
        for (OrganizationComponent c : children) {
            total += c.getBudget();
        }
        return total;
    }

    @Override
    public int getEmployeeCount() {
        int count = 0;
        for (OrganizationComponent c : children) {
            count += c.getEmployeeCount();
        }
        return count;
    }

    // поиск сотрудника
    public OrganizationComponent find(String name) {
        for (OrganizationComponent c : children) {
            if (c.name.equals(name)) return c;

            if (c instanceof Department) {
                OrganizationComponent result = ((Department) c).find(name);
                if (result != null) return result;
            }
        }
        return null;
    }
}

// MAIN

public class Main {
    public static void main(String[] args) {

        // ===== FACADE =====
        HotelFacade hotel = new HotelFacade();

        hotel.bookRoomWithServices("Akerke");
        hotel.organizeEvent("Conference", "Team A");
        hotel.bookTableWithTaxi("Akerke");

        hotel.cancelRoom("Akerke");
        hotel.requestCleaning("101");

        // ===== COMPOSITE =====
        System.out.println("\n=== ORGANIZATION ===");

        Department company = new Department("Company");

        Department it = new Department("IT Department");
        Department hr = new Department("HR Department");

        Employee emp1 = new Employee("Ali", "Developer", 1000);
        Employee emp2 = new Employee("Dana", "Manager", 1500);
        Contractor emp3 = new Contractor("John", "Consultant", 2000);

        it.add(emp1);
        it.add(emp3);
        hr.add(emp2);

        company.add(it);
        company.add(hr);

        company.display(0);

        System.out.println("Total budget: " + company.getBudget());
        System.out.println("Total employees: " + company.getEmployeeCount());

        // поиск
        OrganizationComponent found = ((Department) company).find("Ali");
        if (found != null) {
            System.out.println("Found employee: " + found.name);
        }
    }
}