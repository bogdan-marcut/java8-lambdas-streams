package training.java8.order;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toSet;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import training.java8.order.dto.AuditDto;
import training.java8.order.entity.Customer;
import training.java8.order.entity.Order;
import training.java8.order.entity.OrderLine;
import training.java8.order.repo.OrderLineRepository;
import training.java8.order.entity.Audit;
import training.java8.order.entity.Product;

public class DirtyLambdas {
	
	private OrderLineRepository repo;

	public Set<Customer> getCustomersToNotifyOfOverdueOrders(List<Order> orders, LocalDate warningDate) {
		return orders.stream()
			.filter(order -> order.getDeliveryDueDate().isBefore(warningDate) && 
							 order.getOrderLines().stream()
							 	.anyMatch(line -> line.getStatus() != OrderLine.Status.IN_STOCK))
			.map((Order o) -> o.getCustomer())
			.collect(toSet());
	}
	
	/**
	 * No duplicate DTOs should be returned (cf sorting comparator).
	 */
	public Collection<AuditDto> toDtos(List<Audit> audits) {
		return audits.stream()
				.map(this::mapDto)
				.collect(toSet());
	}

	private AuditDto mapDto(Audit audit) {
		AuditDto dto = new AuditDto();
		dto.username = audit.getUser();
		dto.date = audit.getDate();
		dto.action = audit.getAction();
		return dto;
	}


	public List<Product> getProductsSortedByHits(List<Order> orders) {
		List<OrderLine> lines1 = new ArrayList<>();
		orders.stream()
				.filter(order -> order.getStatus() == Order.Status.ACTIVE || order.getDeliveryDueDate()
						.isAfter(LocalDate.now().minusWeeks(1)))
				.map(Order::getOrderLines).forEach(lines1::addAll);
	
		Map<Product, Integer> productHits = new HashMap<>();
		for (OrderLine line : lines1) {
			int newCount = line.getCount() + productHits.getOrDefault(line.getProduct(), 0);
			productHits.put(line.getProduct(), newCount);
		}
		
		System.out.println("productHits: " + productHits);
		
		Map<Integer, List<Product>> hitsToProducts = new TreeMap<>(Comparator.reverseOrder());
		for (Product key : productHits.keySet()) {
			Integer value = productHits.get(key);
			List<Product> oldList = hitsToProducts.getOrDefault(value, new ArrayList<>());
			oldList.add(key);
			hitsToProducts.put(value, oldList);
		}
		
		System.out.println("hitsToProducts: " + hitsToProducts);
		
		List<Product> topProducts = new ArrayList<>();
		
		for (Integer hits : hitsToProducts.keySet()) {
			List<Product> list = hitsToProducts.get(hits);
			list.sort(comparing(Product::getName));
			topProducts.addAll(list);
		}
		return topProducts;
	}
	
	public void updateOrderLines(Order oldOrder, Order newOrder) {
		// delete unused old lines
		List<OrderLine> toDelete = new ArrayList<>(oldOrder.getOrderLines());
		for (OrderLine newLine: newOrder.getOrderLines()) {
			OrderLine oldLine = toDelete.stream().filter(line -> line.getProduct().equals(newLine.getProduct())).findAny().orElse(null);
			toDelete.remove(oldLine);
		}
		for (OrderLine line : toDelete) {
			repo.delete(line);
		}
		
		// insert new lines
		newOrder.getOrderLines().forEach(newLine -> {
			if (oldOrder.getOrderLines().stream().noneMatch(line -> line.getProduct().equals(newLine.getProduct()))) {
				repo.insert(newLine);
			}
		});
		
		// update old lines
		for (OrderLine oldLine : oldOrder.getOrderLines()) {
			OrderLine newLine = newOrder.getOrderLines().stream()
					.filter(line -> line.getProduct().equals(oldLine.getProduct()))
					.findAny().orElse(null);
			if (newLine == null) {
				continue;
			}
			if (oldLine.getCount() == newLine.getCount()) {
				continue;
			}
			oldLine.setCount(newLine.getCount());
			// can't afford to DELETE and INSERT back, as I want to keep the values of the other fields
			repo.update(oldLine); 
		}
			
	}
	
}
