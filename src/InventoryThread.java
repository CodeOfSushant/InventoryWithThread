import java.sql.*;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryThread {



        // Shared thread-safe collection for communication
        private static final BlockingQueue<Inventory> rawItems = new LinkedBlockingQueue<>();
        // Final collection for processed items
        private static final List<Inventory> processedItems = new ArrayList<>();

        // Poison pill to signal end of data
        private static final Inventory POISON_PILL = new Inventory("EOF" , "EOF", 0);


        public static void main(String[] args) throws InterruptedException {
            Scanner scanner = new Scanner(System.in);
            // Thread 1: Database Reader
            Thread dbReader = new Thread(() -> {
                String url = "jdbc:mysql://localhost:3306/inventory";
                String user = "root";
                String password = "mysql";

                try (Connection conn = DriverManager.getConnection(url, user, password);
                     Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT * FROM Product where Product_Type = 'Raw'")) {

                    while (rs.next()) {
                       Inventory item = new Inventory( rs.getString("Product_Name"), rs.getString("Product_Type"), rs.getDouble("Product_Price"));
                       rawItems.put(item); // Stores in-memory object in shared collection
                    }
                    rawItems.put(POISON_PILL); // Signalt o stop
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            // Thread 2: Tax Calculator
            Thread taxProcessor = new Thread(() -> {
                try {
                    while (true) {
                        Inventory item = rawItems.take(); // Fetch already created object
                        if (item == POISON_PILL) {
                            break;
                        }
                        // Calculate tax and update attribute
                        String name = item.getProductName();
                        String type = item.getProductType();
                        double price = item.getProductPrice();
                        double tax = 0.0;
                        if(type.equalsIgnoreCase("raw")){
                            tax =  InventoryTax.calSalesTax(1,price);

                        }
                        if (type.equalsIgnoreCase("manufactured")) {
                            tax = InventoryTax.calSalesTax(2,price);

                        }
                        if (type.equalsIgnoreCase("imported")) {
                            int inp;
                            while(true) {
                                System.out.println(" What is the type of imported Items\n\t 1. Raw\n\t 2. Manufactured");
                                try {
                                    inp = Integer.parseInt(scanner.nextLine());
                                    if((inp >=1) && (inp <=2)){
                                        break;
                                    }
                                    else {
                                        System.out.println(" Enter valid choice ");
                                    }
                                } catch (NumberFormatException e) {
                                    System.out.println(" Enter valid choice ");
                                }
                            }
                            tax =  InventoryTax.calSalesTax(inp,price);
                            double impSalesTax = InventoryTax.calSalesTax(tax,price,10);
                            tax = impSalesTax;
                        }
                        item.setTax(tax);
                        double finalPrice = price + tax;
                        item.setFinalPrice(finalPrice);

                        // Store in different collection
                        synchronized (processedItems) {
                            processedItems.add(item);
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            // Start both threads
            dbReader.start();
            taxProcessor.start();

            // Wait for completion
            dbReader.join();
            taxProcessor.join();

            // Print final details
            System.out.println("--- Final Item Details ---");
            processedItems.forEach(System.out::println);
        }
    }

