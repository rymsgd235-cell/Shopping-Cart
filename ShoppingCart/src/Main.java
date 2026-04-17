
import java.util.*;

public class Main {
    public static void main(String[] args) {

        Product p1 = new Product(001, "apple", 300.0, 3);
        Product p2 = new Product(002, "bananas", 350.0, 2);
        Product p3 = new Product(003, "pineapples", 499.0, 5);

        ShoppingCart c1 = new ShoppingCart();

        try {
            c1.addProduct(p1, 2);
            c1.addProduct(p2, 2);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
class Product {
    private int productId;
    private String name;
    private double price;
    private int stock;
    private int reservedStock;

    public Product(int productId, String name, double price, int stock) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.reservedStock=0;
    }

    public double getPrice() {
        return price;
    }

    public int getStock(){
        return stock;
    }

    public int getProductId() {
        return productId;
    }

    public void reduceStock(int quantity) {
        stock -= quantity;
    }

    public void addStock(int quantity) {
        stock += quantity;
    }
    public int getReservedStock() {
        return reservedStock;
    }
    public void addReservedStock(int reservedStock) {
        this.reservedStock += reservedStock;
    }
    public void reduceReservedStock(int reservedStock) {
        this.reservedStock -= reservedStock;
    }
}

class CartItem {
    private Product product;
    private int q;

    public CartItem(Product product, int q) {
        this.product = product;
        this.q = q;
    }

    public Product getProduct() {
        return this.product;
    }

    public int getQuantity() {
        return this.q;
    }

    public void setQuantity(int q) {
        this.q = q;
    }
}

class ShoppingCart {

    List<CartItem> cart = new ArrayList<>();

    void addProduct(Product product, int quantity) throws InsufficientStockException, InvalidQuantityException{
        if (quantity <= 0)
            throw new InvalidQuantityException();
        else if (quantity > product.getStock())
            throw new InsufficientStockException();
        else if(quantity > product.getStock()-product.getReservedStock()){
            System.out.println("Product already reserved");
            System.out.println("Available stock: " + (product.getStock() - product.getReservedStock()));
        }
        else {
            cart.add(new CartItem(product, quantity));
            product.reduceReservedStock(quantity);
        }
    }

    void removeProduct(int productId) throws ProductNotFoundException {
        boolean found = false;
        for (int i = 0; i < cart.size(); i++) {
            if (cart.get(i).getProduct().getProductId() == productId) {
                found = true;
                cart.get(i).getProduct().addStock(cart.get(i).getQuantity());
                cart.remove(i);
            }
        }
        if (!found) throw new ProductNotFoundException();
    }

    void updateQuantity(int productId, int quantity)
            throws InvalidQuantityException, ProductNotFoundException {
        boolean found = false;
        if (quantity <= 0) throw new InvalidQuantityException();
        for (int i = 0; i < cart.size(); i++) {

            if (cart.get(i).getProduct().getProductId() == productId) {
                cart.get(i).setQuantity(quantity);
                found = true;
                return;
            }
        }
        if (!found) throw new ProductNotFoundException();


    }

    double calculateTotal() {
        double total = 0;
        for (CartItem c : cart) {
            total += c.getProduct().getPrice() * c.getQuantity();
        }
        return total;
    }

    void checkout() throws EmptyCartException {
        if (cart.isEmpty()) throw new EmptyCartException();

        for (int i = 0; i < cart.size(); i++) {
            int quantityOrdered = cart.get(i).getQuantity();
            cart.get(i).getProduct().reduceStock(quantityOrdered);
        }

        cart.clear();
    }
}


class ProductNotFoundException extends Exception {
    public ProductNotFoundException() {
        super("Product not found");
    }
}

class InsufficientStockException extends Exception {
    public InsufficientStockException() {
        super("Insufficient Stocks");
    }
}

class InvalidQuantityException extends Exception {
    public InvalidQuantityException() {
        super("Invalid Quantity");
    }
}

class EmptyCartException extends Exception {
    public EmptyCartException() {
        super("cart is empty.");
    }
}
//class AlreadyReservedException extends Exception{
//    public AlreadyReservedException(){
//        super("Product is already reserved.");
//    }
//}
