package training.java8.order;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import training.java8.order.dto.OrderDto;
import training.java8.order.entity.Customer;
import training.java8.order.entity.Order;
import training.java8.order.entity.Order.PaymentMethod;
import training.java8.order.entity.OrderLine;
import training.java8.order.entity.Product;

import static java.nio.file.Files.lines;
import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.stream.Collectors.*;

public class TransformStreams {

	/**
	 * Transform all entities to DTOs.
	 * Discussion:.. Make it cleanest!
	 */
	public List<OrderDto> p01_toDtos(List<Order> orders) {
		
		List<OrderDto> dtos = orders.stream().map(order -> {
			OrderDto dto = new OrderDto();
			dto.totalPrice = order.getTotalPrice();
			dto.creationDate = order.getCreationDate();
			return dto;
		}).collect(toList());

		return dtos;
		
	}
	
	/**
	 * Note: Order.getPaymentMethod()
	 */
	public Set<PaymentMethod> p02_getUsedPaymentMethods(Customer customer) {
		return customer.getOrders().stream()
				.map(Order::getPaymentMethod)
				.distinct()
				.collect(toSet());
	}
	
	/**
	 * When did the customer created orders ?
	 * Note: Order.getCreationDate()
	 */
	public SortedSet<LocalDate> p03_getOrderDatesAscending(Customer customer) {
		return customer.getOrders().stream()
				.map(Order::getCreationDate)
				.sorted()
				.collect(toCollection(ConcurrentSkipListSet::new));
	}
	
	
	/**
	 * @return a map order.id -> order
	 */
	public Map<Long, Order> p04_mapOrdersById(Customer customer) {
		return customer.getOrders().stream()
				.collect(toMap(Order::getId, Function.identity()));
	}
	
	/** 
	 * Orders grouped by Order.paymentMethod
	 */
	public Map<PaymentMethod, List<Order>> p05_getProductsByPaymentMethod(Customer customer) {
		return customer.getOrders().stream()
				.collect(groupingBy(Order::getPaymentMethod));
	}
	
	// -------------- MOVIE BREAK :p --------------------
	
	/** 
	 * A hard one !
	 * Get total number of products bought by a customer, across all her orders.
	 * Customer --->* Order --->* OrderLines(.count .product)
	 * The sum of all counts for the same product.
	 * i.e. SELECT PROD_ID, SUM(COUNT) FROM PROD GROUPING BY PROD_ID
	 */
	public Map<Product, Long> p06_getProductCount(Customer customer) {
		return customer.getOrders().stream()
				.flatMap(order -> order.getOrderLines().stream())
				.collect(groupingBy(OrderLine::getProduct, summingLong(OrderLine::getCount)));
		
	}
	
	/**
	 * All the unique products bought by the customer, 
	 * sorted by Product.name.
	 */
	public List<Product> p07_getAllOrderedProducts(Customer customer) {
		return customer.getOrders().stream()
				.flatMap(order -> order.getOrderLines().stream())
				.map(OrderLine::getProduct)
				.distinct()
				.sorted(comparing(Product::getName))
				.collect(toList());
	}
	
	
	/**
	 * The names of all the products bought by Customer,
	 * sorted and then concatenated by ",".
	 * Example: "Armchair,Chair,Table".
	 * Hint: Reuse the previous function.
	 */
	public String p08_getProductsJoined(Customer customer) {
		return customer.getOrders().stream()
				.flatMap(order -> order.getOrderLines().stream())
				.map(orderLine -> orderLine.getProduct().getName())
				.distinct()
				.sorted(naturalOrder())
				.collect(joining(","));
	}
	
	/**
	 * Sum of all Order.getTotalPrice(), truncated to Long.
	 */
	public Long p09_getApproximateTotalOrdersPrice(Customer customer) {
		return customer.getOrders().stream()
				.mapToLong(order -> order.getTotalPrice().longValue())
				.sum();
	}
	
	// ----------- IO ---------------
	
	/**
	 * - Read lines from file as Strings. 
	 * - Where do you close the opened file?
	 * - Parse those to OrderLine using the function bellow
	 * - Validate the created OrderLine. Throw ? :S
	 */
	public List<OrderLine> p10_readOrderFromFile(File file) throws IOException {

		Stream<String> lines = lines(file.toPath()); // ??
		return lines
			.map(line -> line.split(";")) // Stream<String[]>
			.filter(cell -> "LINE".equals(cell[0]))
			.map(this::parseOrderLine) // Stream<OrderLine>
			.peek(this::validateOrderLine)
			.collect(toList());
	}
	
	private OrderLine parseOrderLine(String[] cells) {
		return new OrderLine(new Product(cells[1]), Integer.parseInt(cells[2]));
	}
	
	private void validateOrderLine(OrderLine orderLine) {
		if (orderLine.getCount() < 0) {
			throw new IllegalArgumentException("Negative items");			
		}
	}
	
	
	// TODO print cannonical paths of all files in current directory
	// use Unchecked... stuff
}
