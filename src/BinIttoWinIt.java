import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Abstract class representing a Resident in the system
abstract class Resident {
    private String name;
    private int houseNumber;
    private int zone;

    // Constructor to initialize resident details
    public Resident(String name, int houseNumber, int zone) {
        this.name = name;
        this.houseNumber = houseNumber;
        this.zone = zone;
    }

    // Getter methods for resident details
    public String getName() {
        return name;
    }

    public int getHouseNumber() {
        return houseNumber;
    }

    public int getZone() {
        return zone;
    }

    // Abstract method to display resident information
    public abstract void displayResidentInfo(double totalPayment);
}

// Interface for waste management functionalities
interface WasteManagement {
    void displayWasteSortingInstructions();

    void displayWasteReductionTips();
}

// Class for residents who participate in waste management
class WasteResident extends Resident implements WasteManagement {
    private Map<Integer, String[]> schedule;
    private List<Map<String, Object>> wasteLog;
    private double recyclingDiscount;
    private double wasteAdded;

    // Constructor to initialize waste resident details
    public WasteResident(String name, int houseNumber, int zone) {
        super(name, houseNumber, zone);
        initializeSchedule();
        wasteLog = new ArrayList<>();
        recyclingDiscount = 0;
        wasteAdded = 0;
    }

    // Initialize waste collection schedule based on zone
    private void initializeSchedule() {
        this.schedule = new HashMap<>();
        this.schedule.put(1, new String[]{"Monday", "Wednesday"});
        this.schedule.put(2, new String[]{"Monday", "Wednesday"});
        this.schedule.put(3, new String[]{"Thursday", "Saturday"});
        this.schedule.put(4, new String[]{"Thursday", "Saturday"});
        this.schedule.put(5, new String[]{"Tuesday", "Friday"});
        this.schedule.put(6, new String[]{"Tuesday", "Friday"});
    }

    // Add waste to the resident's account and calculate discount
    public void addWaste(String item, double amount) {
        double extraDiscount = 0;

        // Determine discount based on waste type
        switch (item.toLowerCase()) {
            case "plastic":
            case "paper":
                extraDiscount = 0.8 * Math.min(amount, 10);
                break;
            case "metal":
                extraDiscount = 0.7 * Math.min(amount, 10);
                break;
            case "glass":
                extraDiscount = 1.0 * Math.min(amount, 10);
                break;
            default:
                System.out.println("Invalid waste item. Please enter a valid waste type.");
                return;
        }

        recyclingDiscount += extraDiscount;
        wasteAdded += amount;

        // Log the waste entry
        Map<String, Object> entry = new HashMap<>();
        entry.put("item", item);
        entry.put("amount", amount);
        entry.put("type", "Recyclable");
        wasteLog.add(entry);
    }

    // Getter for total recycling discount
    public double getRecyclingDiscount() {
        return recyclingDiscount;
    }

    // Display detailed summary of the resident's waste management
    @Override
    public void displayResidentInfo(double totalPayment) {
        System.out.println("\n======== WIN IT ========");
        System.out.println("Waste Management Summary");
        System.out.println("========================");
        System.out.println("Name: " + getName());
        System.out.println("House Number: " + getHouseNumber());
        System.out.println("Zone: " + getZone());

        String[] collectionDays = schedule.get(getZone());
        if (collectionDays != null) {
            System.out.println("Collection Days: " + String.join(", ", collectionDays));
        } else {
            System.out.println("Invalid zone. No collection days available.");
        }

        System.out.printf("Total Recyclable Waste Added: %.2f kg\n", wasteAdded);
        System.out.printf("Total Payment Before Discount: PHP %.2f\n", totalPayment);
        System.out.printf("Recycling Discount: PHP %.2f\n", recyclingDiscount);

        double finalPayment = Math.max(0, totalPayment - recyclingDiscount);
        System.out.printf("Final Payment After Discount: PHP %.2f", finalPayment);

        // Display the current timestamp
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        System.out.println("\n" + dtf.format(now));

        // Display log of added waste
        System.out.println("\nWaste Log:");
        for (Map<String, Object> entry : wasteLog) {
            System.out.printf("  - %.2f kg of %s (%s)\n", entry.get("amount"), entry.get("item"), entry.get("type"));
        }
    }

    // Provide instructions for sorting waste
    @Override
    public void displayWasteSortingInstructions() {
        System.out.println("\nWASTE SORTING INSTRUCTIONS");
        System.out.println("\nRECYCLABLE WASTE (use recycling bin");
        System.out.println("    Plastic: Clean plastic bottles and containers.");
        System.out.println("    Paper: Newspapers, magazines, cardboard, and office paper.");
        System.out.println("    Metal: Tin cans and aluminum cans.");
        System.out.println("    Glass: Glass bottles and jars.");
        System.out.println("  Preparation:");
        System.out.println("      Remove food residue, flatten items when possible, and avoid including contaminated materials.\n");

        System.out.println("GENERAL WASTE (use general waste bin)");
        System.out.println("      Food Waste: All food scraps, peelings, and leftovers.");
        System.out.println("      Non-recyclables: Soiled paper, certain plastics, ceramics, and broken household items.");
        System.out.println("      Electronics: Old phones, batteries, and small electronic devices.");
        System.out.println("  Preparation:");
        System.out.println("      Secure in bags to avoid spillage, especially for food waste and sharp objects.\n");
    }

    // Provide tips for reducing waste
    @Override
    public void displayWasteReductionTips() {
        System.out.println("WASTE REDUCTION TIPS");
        System.out.println("Use reusable bags and containers.");
        System.out.println("Compost food waste.");
        System.out.println("Opt for minimal packaging.\n");
    }
}

// Class to manage user registration, login, and actions
class UserManager {
    private Map<Integer, WasteResident> users;
    private WasteResident loggedInUser;

    // Constructor to initialize the user manager
    public UserManager() {
        users = new HashMap<>();
    }

    // Register a new user
    public void registerUser(String name, int houseNumber, int zone) {
        if (users.containsKey(houseNumber)) {
            System.out.println("User already registered.");
            return;
        }
        WasteResident newResident = new WasteResident(name, houseNumber, zone);
        users.put(houseNumber, newResident);
        System.out.println("Registration successful.");
    }

    // Login a user
    public boolean loginUser(int houseNumber) {
        if (users.containsKey(houseNumber)) {
            loggedInUser = users.get(houseNumber);
            System.out.println("Login successful. Welcome, " + loggedInUser.getName() + "!");
            return true;
        } else {
            System.out.println("User not found. Please register first.");
            return false;
        }
    }

    // Logout the current user
    public void logoutUser() {
        if (loggedInUser != null) {
            System.out.println("Goodbye, " + loggedInUser.getName() + "!");
            loggedInUser = null;
        }
    }

    // Get the currently logged-in user
    public WasteResident getLoggedInUser() {
        return loggedInUser;
    }
}

// Main class to run the program
public class BinIttoWinIt {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        UserManager userManager = new UserManager();
        boolean running = true;

        while (running) {
            // Main menu for the application
            System.out.println("\n===============================");
            System.out.println("\tBIN IT TO WIN IT");
            System.out.println("===============================");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.println("===============================");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    // Registration process
                    System.out.print("\nEnter Name for Registration: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter House Number: ");
                    int houseNumber = scanner.nextInt();
                    System.out.print("Enter Zone (1-6): ");
                    int zone = scanner.nextInt();
                    scanner.nextLine();
                    userManager.registerUser(name, houseNumber, zone);
                    break;

                case 2:
                    // Login process and user actions
                    System.out.print("\nEnter House Number to Login: ");
                    int loginHouseNumber = scanner.nextInt();
                    scanner.nextLine();
                    if (userManager.loginUser(loginHouseNumber)) {
                        WasteResident resident = userManager.getLoggedInUser();
                        resident.displayWasteSortingInstructions();
                        resident.displayWasteReductionTips();

                        boolean loggedIn = true;
                        while (loggedIn) {
                            // Menu for logged-in user
                            System.out.println("\n================================");
                            System.out.println("\tResident Options");
                            System.out.println("================================");
                            System.out.println("1. Add Waste (Recyclable)");
                            System.out.println("2. View Payment Details");
                            System.out.println("3. Logout");
                            System.out.println("================================");
                            System.out.print("Choose an option: ");
                            int option = scanner.nextInt();
                            scanner.nextLine();

                            switch (option) {
                                case 1:
                                    // Add waste to account
                                    boolean addingWaste = true; // Control variable for the loop
                                    while (addingWaste) {
                                        System.out.println("\n======================");
                                        System.out.println("\tBIN IT");
                                        System.out.println("======================");
                                        System.out.print("Enter Waste Item: ");
                                        String item = scanner.nextLine();
                                        System.out.print("Enter Amount of Waste (in kg): ");
                                        double amount = scanner.nextDouble();
                                        scanner.nextLine();
                                        resident.addWaste(item, amount);

                                        // Ask the user if they want to continue adding waste
                                        System.out.print("Do you want to add more waste? (yes/no): ");
                                        String continueChoice = scanner.nextLine().trim().toLowerCase();
                                        if (!continueChoice.equals("yes")) {
                                            addingWaste = false; // Exit the loop if user chooses "no"
                                        }
                                    }
                                    break;

                                case 2:
                                    // View payment details
                                    System.out.print("\n Enter total payment amount: ");
                                    double totalPayment = scanner.nextDouble();
                                    scanner.nextLine();
                                    resident.displayResidentInfo(totalPayment);
                                    break;

                                case 3:
                                    // Logout
                                    userManager.logoutUser();
                                    loggedIn = false;
                                    break;

                                default:
                                    System.out.println("Invalid option. Please try again.");
                                    break;
                            }
                        }
                    }
                    break;

                case 3:
                    // Exit the application
                    System.out.println("Thank you for using Bin It to Win It!");
                    running = false;
                    break;

                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        }
        scanner.close();
    }
}
