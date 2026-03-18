public class StockTransaction {
    public static void main(String[] args) {

        // Purchase details
        int shares = 1000;
        double purchasePrice = 32.87;
        double purchaseCommissionRate = 0.02;

        // Sale details
        double salePrice = 33.92;
        double saleCommissionRate = 0.02;

        // Calculations
        double amountPaid = shares * purchasePrice;
        double purchaseCommission = amountPaid * purchaseCommissionRate;

        double amountReceived = shares * salePrice;
        double saleCommission = amountReceived * saleCommissionRate;

        double profit = amountReceived - saleCommission - amountPaid - purchaseCommission;

        // Output results
        System.out.printf("Amount paid for the stock: $%.2f%n", amountPaid);
        System.out.printf("Commission paid when buying: $%.2f%n", purchaseCommission);
        System.out.printf("Amount sold the stock for: $%.2f%n", amountReceived);
        System.out.printf("Commission paid when selling: $%.2f%n", saleCommission);
        System.out.printf("Profit after commissions: $%.2f%n", profit);
    }
}
