public class InventoryTax {

    // function to calculate sales tax for Raw and Manufactured.
    static double calSalesTax(int choice, double productPrice) {
        double tax = 0.00;
        if (choice == 1)
            tax = (productPrice * 12.5 / 100);

        if (choice == 2)
            tax = (productPrice * 12.5 / 100) + ((productPrice + (productPrice * 12.5 / 100)) * 2 / 100);

        return tax;
    }

    // function to calculate sales tax for Imported items
    static double calSalesTax(double salesTax, double productPrice, int importDuty) {
        double surCharge;
        double finalCost;
        finalCost = (salesTax + productPrice * importDuty / 100);
        if (finalCost <= 100)
            surCharge = 5;
        else if (finalCost > 100 && finalCost <= 200) {
            surCharge = 10;
        } else {
            surCharge = finalCost * 5 / 100;
        }
        return finalCost + surCharge;
    }
}
