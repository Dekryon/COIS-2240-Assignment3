import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class VehicleRentalTest {

    @Test
    public void testLicensePlate() {
        Vehicle v = new Car("Toyota", "Corolla", 2019, 5);

        assertDoesNotThrow(() -> v.setLicensePlate("AAA100"));
        assertEquals("AAA100", v.getLicensePlate());

        assertDoesNotThrow(() -> v.setLicensePlate("ABC567"));
        assertEquals("ABC567", v.getLicensePlate());

        assertDoesNotThrow(() -> v.setLicensePlate("ZZZ999"));
        assertEquals("ZZZ999", v.getLicensePlate());

        assertThrows(IllegalArgumentException.class, () -> v.setLicensePlate(""));
        assertThrows(IllegalArgumentException.class, () -> v.setLicensePlate(null));
        assertThrows(IllegalArgumentException.class, () -> v.setLicensePlate("AAA1000"));
        assertThrows(IllegalArgumentException.class, () -> v.setLicensePlate("ZZZ99"));
    }

    @Test
    public void testRentAndReturnVehicle() {
        Vehicle vehicle = new Car("Toyota", "Corolla", 2020, 5);
        vehicle.setLicensePlate("AAA100");
        Customer customer = new Customer(1, "George");

        assertEquals(Vehicle.VehicleStatus.Available, vehicle.getStatus());

        RentalSystem system = RentalSystem.getInstance();
        LocalDate today = LocalDate.now();

        boolean firstRent = system.rentVehicle(vehicle, customer, today, 100.0);
        assertTrue(firstRent);
        assertEquals(Vehicle.VehicleStatus.Rented, vehicle.getStatus());

        boolean secondRent = system.rentVehicle(vehicle, customer, today, 100.0);
        assertFalse(secondRent);

        boolean firstReturn = system.returnVehicle(vehicle, customer, today, 0.0);
        assertTrue(firstReturn);
        assertEquals(Vehicle.VehicleStatus.Available, vehicle.getStatus());

        boolean secondReturn = system.returnVehicle(vehicle, customer, today, 0.0);
        assertFalse(secondReturn);
    }
}
