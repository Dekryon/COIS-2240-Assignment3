import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;

public class RentalSystem {

    private static RentalSystem instance;

    private static final String VEHICLES_FILE = "vehicles.txt";
    private static final String CUSTOMERS_FILE = "customers.txt";
    private static final String RECORDS_FILE = "rental_records.txt";

    private List<Vehicle> vehicles = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();
    private RentalHistory rentalHistory = new RentalHistory();

    private RentalSystem() {
        loadData();
    }

    public static RentalSystem getInstance() {
        if (instance == null) {
            instance = new RentalSystem();
        }
        return instance;
    }

    private void saveVehicle(Vehicle vehicle) {
        try (FileWriter fw = new FileWriter(VEHICLES_FILE, true);
             PrintWriter out = new PrintWriter(fw)) {

            out.println(
                vehicle.getLicensePlate() + "," +
                vehicle.getMake() + "," +
                vehicle.getModel() + "," +
                vehicle.getYear() + "," +
                vehicle.getClass().getSimpleName()
            );
        } catch (IOException e) {
            System.out.println("Error saving vehicle: " + e.getMessage());
        }
    }

    private void saveCustomer(Customer customer) {
        try (FileWriter fw = new FileWriter(CUSTOMERS_FILE, true);
             PrintWriter out = new PrintWriter(fw)) {

            out.println(
                customer.getCustomerId() + "," +
                customer.getCustomerName()
            );
        } catch (IOException e) {
            System.out.println("Error saving customer: " + e.getMessage());
        }
    }

    private void saveRecord(RentalRecord record) {
        try (FileWriter fw = new FileWriter(RECORDS_FILE, true);
             PrintWriter out = new PrintWriter(fw)) {

            // type,plate,customerId,date,amount
            out.println(
                record.getRecordType() + "," +
                record.getVehicle().getLicensePlate() + "," +
                record.getCustomer().getCustomerId() + "," +
                record.getRecordDate().toString() + "," +
                record.getTotalAmount()
            );
        } catch (IOException e) {
            System.out.println("Error saving rental record: " + e.getMessage());
        }
    }

    public boolean addVehicle(Vehicle vehicle) {
        if (findVehicleByPlate(vehicle.getLicensePlate()) != null) {
            System.out.println("A vehicle with plate " + vehicle.getLicensePlate() + " already exists.");
            return false;
        }

        vehicles.add(vehicle);
        saveVehicle(vehicle);
        return true;
    }
 

    public boolean addCustomer(Customer customer) {
        if (findCustomerById(customer.getCustomerId()) != null) {
            System.out.println("A customer with ID " + customer.getCustomerId() + " already exists.");
            return false;
        }

        customers.add(customer);
        saveCustomer(customer);
        return true;
    }


    public boolean rentVehicle(Vehicle vehicle, Customer customer, LocalDate date, double amount) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.Available) {
            vehicle.setStatus(Vehicle.VehicleStatus.Rented);

            RentalRecord record = new RentalRecord(vehicle, customer, date, amount, "RENT");
            rentalHistory.addRecord(record);
            saveRecord(record);

            System.out.println("Vehicle rented to " + customer.getCustomerName());
            return true;
        } else {
            System.out.println("Vehicle is not available for renting.");
            return false;
        }
    }       

    public boolean returnVehicle(Vehicle vehicle, Customer customer, LocalDate date, double extraFees) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.Rented) {
            vehicle.setStatus(Vehicle.VehicleStatus.Available);

            RentalRecord record = new RentalRecord(vehicle, customer, date, extraFees, "RETURN");
            rentalHistory.addRecord(record);
            saveRecord(record);

            System.out.println("Vehicle returned by " + customer.getCustomerName());
            return true;
        } else {
            System.out.println("Vehicle is not rented.");
            return false;
        }          
    }       
  
    public void displayVehicles(Vehicle.VehicleStatus status) {
        if (status == null) {
            System.out.println("\n=== All Vehicles ===");
        } else {
            System.out.println("\n=== " + status + " Vehicles ===");
        }

        System.out.printf("|%-16s | %-12s | %-12s | %-12s | %-6s | %-18s |%n",
            " Type", "Plate", "Make", "Model", "Year", "Status");
        System.out.println("|--------------------------------------------------------------------------------------------|");

        boolean found = false;
        for (Vehicle vehicle : vehicles) {
            if (status == null || vehicle.getStatus() == status) {
                found = true;
                String vehicleType;
                if (vehicle instanceof Car) {
                    vehicleType = "Car";
                } else if (vehicle instanceof Minibus) {
                    vehicleType = "Minibus";
                } else if (vehicle instanceof PickupTruck) {
                    vehicleType = "Pickup Truck";
                } else {
                    vehicleType = "Unknown";
                }
                System.out.printf("| %-15s | %-12s | %-12s | %-12s | %-6d | %-18s |%n",
                    vehicleType,
                    vehicle.getLicensePlate(),
                    vehicle.getMake(),
                    vehicle.getModel(),
                    vehicle.getYear(),
                    vehicle.getStatus().toString());
            }
        }
        if (!found) {
            if (status == null) {
                System.out.println("  No Vehicles found.");
            } else {
                System.out.println("  No vehicles with Status: " + status);
            }
        }
        System.out.println();
    }

    public void displayAllCustomers() {
        for (Customer c : customers) {
            System.out.println("  " + c.toString());
        }
    }

    public void displayRentalHistory() {
        if (rentalHistory.getRentalHistory().isEmpty()) {
            System.out.println("  No rental history found.");
        } else {
            System.out.printf("|%-10s | %-12s | %-20s | %-12s | %-12s |%n",
                " Type", "Plate", "Customer", "Date", "Amount");
            System.out.println("|-------------------------------------------------------------------------------|");

            for (RentalRecord record : rentalHistory.getRentalHistory()) {
                System.out.printf("| %-9s | %-12s | %-20s | %-12s | $%-11.2f |%n",
                    record.getRecordType(),
                    record.getVehicle().getLicensePlate(),
                    record.getCustomer().getCustomerName(),
                    record.getRecordDate().toString(),
                    record.getTotalAmount()
                );
            }
            System.out.println();
        }
    }

    public Vehicle findVehicleByPlate(String plate) {
        for (Vehicle v : vehicles) {
            if (v.getLicensePlate().equalsIgnoreCase(plate)) {
                return v;
            }
        }
        return null;
    }

    public Customer findCustomerById(int id) {
        for (Customer c : customers) {
            if (c.getCustomerId() == id) {
                return c;
            }
        }
        return null;
    }

    private void loadData() {
        loadVehicles();
        loadCustomers();
        loadRecords();
    }

    private void loadVehicles() {
        File file = new File(VEHICLES_FILE);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    String plate = parts[0];
                    String make = parts[1];
                    String model = parts[2];
                    int year = Integer.parseInt(parts[3]);
                    String type = parts[4];

                    Vehicle v = null;
                    if ("Car".equalsIgnoreCase(type)) {
                        v = new Car(plate, make, model, year);
                    } else if ("Minibus".equalsIgnoreCase(type)) {
                        v = new Minibus(plate, make, model, year);
                    } else if ("PickupTruck".equalsIgnoreCase(type) || "Pickup Truck".equalsIgnoreCase(type)) {
                        v = new PickupTruck(plate, make, model, year);
                    }

                    if (v != null) {
                        vehicles.add(v);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading vehicles: " + e.getMessage());
        }
    }

    private void loadCustomers() {
        File file = new File(CUSTOMERS_FILE);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    int id = Integer.parseInt(parts[0]);
                    String name = parts[1];
                    Customer c = new Customer(id, name);
                    customers.add(c);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading customers: " + e.getMessage());
        }
    }

    private void loadRecords() {
        File file = new File(RECORDS_FILE);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    String type = parts[0];
                    String plate = parts[1];
                    int customerId = Integer.parseInt(parts[2]);
                    LocalDate date = LocalDate.parse(parts[3]);
                    double amount = Double.parseDouble(parts[4]);

                    Vehicle vehicle = findVehicleByPlate(plate);
                    Customer customer = findCustomerById(customerId);

                    if (vehicle != null && customer != null) {
                        RentalRecord record = new RentalRecord(vehicle, customer, date, amount, type);
                        rentalHistory.addRecord(record);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading rental records: " + e.getMessage());
        }
    }
}
