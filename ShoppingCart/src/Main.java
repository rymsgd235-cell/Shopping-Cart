import static java.lang.Math.abs;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Main {

	private static List<Product> storeInventory = new ArrayList<>();
	private static ShoppingCart cart = new ShoppingCart();
	private static CheckoutManager manager = new CheckoutManager(cart);

	//GUI components
	private static DefaultTableModel productTableModel;
	private static DefaultTableModel cartTableModel;
	private static JLabel lblTotal;
	private static JFrame frame;

	public static void main(String[] args) {
		// Sample non-grocery products
		storeInventory.add(new Product(1, "Gaming Keyboard", 2000.0, 25));
		storeInventory.add(new Product(2, "Wireless Mouse", 950.0, 40));
		storeInventory.add(new Product(3, "27-inch Monitor", 21000.0, 12));
		storeInventory.add(new Product(4, "Mechanical SSD 1TB", 12000.0, 18));
		storeInventory.add(new Product(5, "Gaming Laptop", 120000.0, 5));
		storeInventory.add(new Product(6, "USB Drive 64GB", 900.0, 35));
		storeInventory.add(new Product(7, "Tablet", 12000.0, 10));
		storeInventory.add(new Product(8, "iPhone", 40000.0, 8));
		storeInventory.add(new Product(9, "Bluetooth Speaker", 800.0, 14));
		storeInventory.add(new Product(10, "HDD 1TB", 5000.0, 15));
		storeInventory.add(new Product(11, "Wired Mouse", 300.0, 30));
		storeInventory.add(new Product(12, "KeyBoard", 950.0, 25));
		storeInventory.add(new Product(13, "Earbuds", 1000, 35));
		storeInventory.add(new Product(14, "Type-C Cable", 200, 50));
		storeInventory.add(new Product(15, "Type-B Cable", 150, 50));
		//Main Frame
		frame = new JFrame("Online Shopping Cart");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1000, 600);
		frame.setLayout(new BorderLayout(10, 10));

		//Setup tables
		setupUI();

		// Load initial data
		refreshProductTable();
		frame.setLocationRelativeTo(null); // Center on screen
		frame.setVisible(true);
	}

	//setup tables for both products and cart
	private static void setupUI() {
		// left section: products
		String[] productCols = { "ID", "Name", "Price (PKR)", "Available Stock" };
		productTableModel =
			new DefaultTableModel(productCols, 0) {
				@Override
				public boolean isCellEditable(int row, int column) {
					return false;
				} // Read-only
			};
		JTable productTable = new JTable(productTableModel);
		productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane productScroll = new JScrollPane(productTable);
		productScroll.setBorder(BorderFactory.createTitledBorder("1. Available Products (Click to Add)"));

		// Add Click Listener to Product Table
		productTable.addMouseListener(
			new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 1) {
						int row = productTable.getSelectedRow();
						if (row != -1) {
							handleProductSelection(row);
							productTable.clearSelection(); // Deselect after clicking
						}
					}
				}
			}
		);

		//right section: User's Cart
		String[] cartCols = { "ID", "Name", "Qty Reserved", "Subtotal" };
		cartTableModel =
			new DefaultTableModel(cartCols, 0) {
				@Override
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			};
		JTable cartTable = new JTable(cartTableModel);
		JScrollPane cartScroll = new JScrollPane(cartTable);
		cartScroll.setBorder(BorderFactory.createTitledBorder("2. Your Shopping Cart"));

		// Split Pane to hold both tables side by side
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, productScroll, cartScroll);
		splitPane.setResizeWeight(0.5); // 50/50 split
		frame.add(splitPane, BorderLayout.CENTER);

		// Bottom panel for total and checkout button
		JPanel bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

		lblTotal = new JLabel("Total Amount: PKR 0.0");
		lblTotal.setFont(new Font("Arial", Font.BOLD, 18));

		JButton btnOrder = new JButton("Place Order");
		btnOrder.setFont(new Font("Arial", Font.BOLD, 16));
		btnOrder.setBackground(new Color(34, 139, 34)); // Forest Green
		btnOrder.setForeground(Color.WHITE);

		btnOrder.addActionListener(e -> handleCheckoutFlow());

		bottomPanel.add(lblTotal, BorderLayout.WEST);
		bottomPanel.add(btnOrder, BorderLayout.EAST);
		frame.add(bottomPanel, BorderLayout.SOUTH);
	}

	// handling product selection and adding to cart with input validation

	private static void handleProductSelection(int rowIndex) {
		Product selectedProduct = storeInventory.get(rowIndex);

		String input = JOptionPane.showInputDialog(
			frame,
			"Enter quantity for " + selectedProduct.getName() + ":",
			"Add to Cart",
			JOptionPane.QUESTION_MESSAGE
		);

		if (input != null && !input.trim().isEmpty()) {
			try {
				int quantity = Integer.parseInt(input);
				cart.addProduct(selectedProduct, quantity);
				refreshCartTable();
				refreshProductTable(); // Refresh to show updated reserved stock
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(
					frame,
					"Please enter a valid number.",
					"Invalid Input",
					JOptionPane.ERROR_MESSAGE
				);
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	private static void handleCheckoutFlow() {
		if (cart.cart.isEmpty()) {
			JOptionPane.showMessageDialog(frame, "Your cart is empty!", "Checkout Error", JOptionPane.WARNING_MESSAGE);
			return;
		}

		double total = manager.calculateTotal();

		//Generating and showing final invoice
		StringBuilder invoice = new StringBuilder();
		invoice.append("--- FINAL INVOICE ---\n\n");
		for (CartItem item : cart.cart) {
			invoice
				.append(item.getProduct().getName())
				.append(" (x")
				.append(item.getQuantity())
				.append(") = PKR ")
				.append(item.getProduct().getPrice() * item.getQuantity())
				.append("\n");
		}
		invoice.append("\nGRAND TOTAL: PKR ").append(total).append("\n\n");
		invoice.append("Do you want to proceed to payment?");

		int confirmInvoice = JOptionPane.showConfirmDialog(
			frame,
			invoice.toString(),
			"Invoice Details",
			JOptionPane.YES_NO_OPTION
		);
		if (confirmInvoice != JOptionPane.YES_OPTION) return;

		// determine paymentMethods
		manager.determinePaymentMethods(total);
		List<PaymentMethod> options = manager.getPaymentMethods(); // Ensure CheckoutManager has this getter!

		String[] methodNames = new String[options.size()];
		for (int i = 0; i < options.size(); i++) {
			methodNames[i] = options.get(i).getName();
		}

		// paymentMethod selection
		String selectedMethodName = (String) JOptionPane.showInputDialog(
			frame,
			"Total is PKR " + total + ".\nSelect your payment method:",
			"Payment Gateway",
			JOptionPane.QUESTION_MESSAGE,
			null,
			methodNames,
			methodNames[0]
		);

		if (selectedMethodName == null) return; // User cancelled

		//final confirmation & processing
		int finalConfirm = JOptionPane.showConfirmDialog(
			frame,
			"Confirm order using " + selectedMethodName + "?",
			"Final Confirmation",
			JOptionPane.YES_NO_OPTION
		);

		if (finalConfirm == JOptionPane.YES_OPTION) {
			for (PaymentMethod method : options) {
				if (method.getName().equals(selectedMethodName)) {
					manager.finalizeOrder(method);

					JOptionPane.showMessageDialog(
						frame,
						"Order Placed Successfully!",
						"Success",
						JOptionPane.INFORMATION_MESSAGE
					);

					refreshProductTable();
					refreshCartTable();
					break;
				}
			}
		}
	}

	private static void refreshProductTable() {
		productTableModel.setRowCount(0); // Clear existing rows
		for (Product p : storeInventory) {
			int available = p.getStock() - p.getReservedStock();
			productTableModel.addRow(new Object[] { p.getProductId(), p.getName(), p.getPrice(), available });
		}
	}

	// Refreshes cart to show current items and total amount
	private static void refreshCartTable() {
		cartTableModel.setRowCount(0); // Clear existing rows
		for (CartItem c : cart.cart) {
			double subtotal = c.getProduct().getPrice() * c.getQuantity();
			cartTableModel.addRow(
				new Object[] { c.getProduct().getProductId(), c.getProduct().getName(), c.getQuantity(), subtotal }
			);
		}
		lblTotal.setText("Total Amount: PKR " + manager.calculateTotal());
	}
}

// custom Search function
class Utility {

	public static int search(List<CartItem> Cart, int productId) {
		for (CartItem c : Cart) {
			if (c.getProduct().getProductId() == productId) return Cart.indexOf(c);
		}
		return -1;
	}
}

// Product class
class Product {

	private int productId;
	private String name;
	private double price;
	private int stock;
	private int reservedStock;

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

	public String getName() {
		return name;
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

// CartItem
class CartItem {

	private Product product;
	private int quantity;

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

// ShoppingCart Modified contins only addProduct, removeProduct and updateQuantity methods
// calculateTotal and checkout moved to CheckoutManager
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
}

//Checkout Manager
// manages total, paymentMethods and checkout process
class CheckoutManager {

	private ShoppingCart shoppingcart;
	private ArrayList<PaymentMethod> paymentMethods = new ArrayList<>();

	public CheckoutManager(ShoppingCart shoppingcart) {
		this.shoppingcart = shoppingcart;
	}

	public double calculateTotal() {
		double total = 0;
		for (CartItem c : shoppingcart.cart) {
			total += c.getProduct().getPrice() * c.getQuantity();
		}
		return total;
	}

	public void checkout() throws EmptyCartException {
		if (shoppingcart.cart.isEmpty()) throw new EmptyCartException();

		for (CartItem cartItem : shoppingcart.cart) {
			int quantityOrdered = cartItem.getQuantity();
			cartItem.getProduct().reduceStock(quantityOrdered);
			cartItem.getProduct().reduceReservedStock(quantityOrdered);
		}

		shoppingcart.cart.clear();
	}

	public void determinePaymentMethods(double total) {
		paymentMethods.clear();
		if (total < 5000) paymentMethods.add(new CashOnDilivery());
		paymentMethods.add(new CreditCard());
		paymentMethods.add(new Easypaisa());
	}

	public void processOrder() {
		double total = calculateTotal();
		determinePaymentMethods(total);
	}

	public void finalizeOrder(PaymentMethod paymentMethod) {
		double total = calculateTotal();
		if (paymentMethod.payment(total)) {
			try {
				checkout();
				System.out.println("Order placed successfully!");
			} catch (EmptyCartException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	public ArrayList<PaymentMethod> getPaymentMethods() {
		return paymentMethods;
	}
}

//Payment methods interfadce
interface PaymentMethod {
	String getName();
	boolean payment(double amount);
}

//CashOnDelivery
class CashOnDilivery implements PaymentMethod {

	@Override
	public String getName() {
		return "Cash on Delivery";
	}

	@Override
	public boolean payment(double amount) {
		System.out.println("Payment of " + amount + " will be made upon delivery.");
		return true;
	}
}

//CreditCard
class CreditCard implements PaymentMethod {

	@Override
	public String getName() {
		return "Credit Card";
	}

	@Override
	public boolean payment(double amount) {
		System.out.println("Payment of " + amount + " will be made through Credit Card.");
		return true;
	}
}

//Easypaisa
class Easypaisa implements PaymentMethod {

	@Override
	public String getName() {
		return "Easypaisa";
	}

	@Override
	public boolean payment(double amount) {
		System.out.println("Payment of " + amount + " will be made through Easypaisa.");
		return true;
	}
}

//Exceptions
// when product is not found in cart
class ProductNotFoundException extends Exception {

	public ProductNotFoundException() {
		super("Product not found");
	}
}

// when user enters quantity more than available stock
class InsufficientStockException extends Exception {

	public InsufficientStockException() {
		super("Insufficient Stocks");
	}
}

// when user enters invalid quantity
class InvalidQuantityException extends Exception {

	public InvalidQuantityException() {
		super("Invalid Quantity");
	}
}

// when user tries to checkout with empty cart
class EmptyCartException extends Exception {

	public EmptyCartException() {
		super("cart is empty.");
	}
}
