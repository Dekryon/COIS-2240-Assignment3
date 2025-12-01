public abstract class Vehicle {
    private String licensePlate;
    private String make;
    private String model;
    private int year;
    private VehicleStatus status;

    public enum VehicleStatus { Available, Held, Rented, UnderMaintenance, OutOfService }

    public Vehicle(String make, String model, int year) {
        if (make == null || make.trim().isEmpty()) {
            this.make = "Unknown";
        } else {
            this.make = capitalize(make);
        }

        if (model == null || model.trim().isEmpty()) {
            this.model = "Unknown";
        } else {
            this.model = capitalize(model);
        }

        this.year = year;
        this.status = VehicleStatus.Available;
        this.licensePlate = null;
    }

    public Vehicle() {
        this(null, null, 0);
    }

    public void setLicensePlate(String licensePlate) {
        if (!isValidPlate(licensePlate)) {
            throw new IllegalArgumentException("Invalid license plate: " + licensePlate);
        }
        this.licensePlate = licensePlate.toUpperCase();
    }


    public void setStatus(VehicleStatus status) {
    	this.status = status;
    }

    public String getLicensePlate() { return licensePlate; }

    public String getMake() { return make; }

    public String getModel() { return model;}

    public int getYear() { return year; }

    public VehicleStatus getStatus() { return status; }

    public String getInfo() {
        return "| " + licensePlate + " | " + make + " | " + model + " | " + year + " | " + status + " |";
    }
    
    private String capitalize(String input) {
        if (input == null) {
            return null;
        }
        String trimmed = input.trim();
        if (trimmed.isEmpty()) {
            return trimmed;
        }
        if (trimmed.length() == 1) {
            return trimmed.toUpperCase();
        }
        return trimmed.substring(0, 1).toUpperCase() + trimmed.substring(1).toLowerCase();
    }
    private boolean isValidPlate(String plate) {
        if (plate == null) {
            return false;
        }
        String trimmed = plate.trim();
        if (trimmed.isEmpty()) {
            return false;
        }
        if (trimmed.length() != 6) {
            return false;
        }
        String letters = trimmed.substring(0, 3);
        String numbers = trimmed.substring(3, 6);
        return letters.chars().allMatch(Character::isLetter)
            && numbers.chars().allMatch(Character::isDigit);
    }



}
