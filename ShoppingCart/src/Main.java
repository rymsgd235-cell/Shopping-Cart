import static java.lang.Math.abs;

import java.util.ArrayList;
import java.util.List;

public class Main {

	static void main(String[] args) {
		//        Product p1 = new Product(001, "apple", 300.0, 3);
		//        Product p2 = new Product(002, "bananas", 350.0, 2);
		//        Product p3 = new Product(003, "pineapples", 499.0, 5);
		//
		//        ShoppingCart c1 = new ShoppingCart();
		//
		//        try {
		//            c1.addProduct(p1, 2);
		//            c1.addProduct(p2, 2);
		//        } catch (Exception e) {
		//            System.out.println(e);
		//        }
	}
}

class Utility {

	public static int search(List<CartItem> Cart, int productId) {
		for (CartItem c : Cart) {
			if (c.getProduct().getProductId() == productId) return Cart.indexOf(c);
		}
		return -1;
	}
}

class Product {

	protected int productId;
	protected String name;
	protected double price;
	protected int stock;
	protected int reservedStock;

	public Product() {}

	public Product(int productId, String name, double price, int stock) {
		this.productId = productId;
		this.name = name;
		this.price = price;
		this.stock = stock;
		this.reservedStock = 0;
	}

	public double getPrice() {
		return price;
	}

	public int getStock() {
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

	protected Product product;
	protected int quantity;

	public CartItem() {}

	public CartItem(Product product, int quantity) {
		this.product = product;
		this.quantity = quantity;
	}

	public Product getProduct() {
		return this.product;
	}

	public int getQuantity() {
		return this.quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
}

class ShoppingCart {

	List<CartItem> cart = new ArrayList<>();

	void addProduct(Product product, int quantity) throws InsufficientStockException, InvalidQuantityException {
		if (quantity <= 0) throw new InvalidQuantityException(); else if (
			quantity > product.getStock()
		) throw new InsufficientStockException(); else if (quantity > product.getStock() - product.getReservedStock()) {
			System.out.println("Product already reserved");
			System.out.println("Available stock: " + (product.getStock() - product.getReservedStock()));
		} else {
			int i = Utility.search(cart, product.getProductId());
			if (i == -1) {
				cart.add(new CartItem(product, quantity));
				product.addReservedStock(quantity);
			} else {
				cart.get(i).getProduct().addReservedStock(quantity);
				int totalQuantity = cart.get(i).getQuantity() + quantity;
				cart.get(i).setQuantity(totalQuantity);
			}
		}
	}

	void removeProduct(int productId) throws ProductNotFoundException {
		int i = Utility.search(cart, productId);
		if (i == -1) throw new ProductNotFoundException(); else {
			cart.get(i).getProduct().reduceReservedStock(cart.get(i).getQuantity());
			cart.remove(i);
		}
	}

	void updateQuantity(int productId, int quantity)
		throws InvalidQuantityException, ProductNotFoundException, InsufficientStockException {
		if (quantity <= 0) throw new InvalidQuantityException();
		int i = Utility.search(cart, productId);
		if (i == -1) throw new ProductNotFoundException(); else if (
			quantity > cart.get(i).getProduct().getStock()
		) throw new InsufficientStockException(); else if (
			abs(cart.get(i).getQuantity() - quantity) >
			cart.get(i).getProduct().getStock() -
			cart.get(i).getProduct().getReservedStock()
		) {
			System.out.println("Can not update quantity. Product already reserved");
			System.out.println(
				"Available stock: " +
				(
					cart.get(i).getProduct().getStock() -
					cart.get(i).getQuantity() -
					cart.get(i).getProduct().getReservedStock()
				)
			);
		} else {
			cart.get(i).getProduct().reduceReservedStock(cart.get(i).getQuantity());
			cart.get(i).setQuantity(quantity);
			cart.get(i).getProduct().addReservedStock(cart.get(i).getQuantity());
		}
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

		for (CartItem cartItem : cart) {
			int quantityOrdered = cartItem.getQuantity();
			cartItem.getProduct().reduceStock(quantityOrdered);
			cartItem.getProduct().reduceReservedStock(quantityOrdered);
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

