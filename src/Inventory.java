public class Inventory {
    protected String productName;

    protected String productType;
    protected double productPrice;
    protected double tax;
    protected double finalPrice;

    // Constructor to initiate values to object.
    public Inventory(String productName,String productType, double productPrice){
        this.productName = productName;
        this.productType = productType;
        this.productPrice = productPrice;
    }

    public String getProductName(){

        return productName;
    }

    public String getProductType(){
        return productType;
    }

    public double getProductPrice(){

        return productPrice;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public void setFinalPrice(double finalPrice){
        this.finalPrice = finalPrice;
    }

    @Override
    public String toString() {
        return String.format(" Name: %s | ProductType: %s | Price: %.2f | Tax: %.2f | FinalPrice: %.2f", productName, productType, productPrice, tax, finalPrice);
    }

}


