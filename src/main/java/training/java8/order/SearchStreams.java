package training.java8.order;

import static java.util.stream.Collectors.toList;

import java.util.Comparator;
import java.util.List;

import training.java8.order.entity.Customer;
import training.java8.order.entity.Order;
import training.java8.order.entity.OrderLine;

public class SearchStreams {
	
	/**
	 * FIRST OF ALL: Add the following "Favourite" static imports:
	 * Eclipse: Window > Preferences > type "Favo" > Favorites >
	 * 					> New Type > Browse > and type the class name for:
		java.util.stream.Collectors
	 */
	
	/**
	 * - shorten/clean it up
	 */
	public List<Order> p1_getActiveOrders(Customer customer) {
		return customer.getOrders().stream().filter((order) -> order.getStatus().equals(Order.Status.ACTIVE)).collect(toList());
	}
	
	/**
	 * @return the Order in the list with the given id  
	 * - ...Any or ...First ?
	 * - what do you do when you don't find it ? null/throw/Optional ?
	 */
	public Order p2_getOrderById(List<Order> orders, long orderId) {
		return orders.stream().filter(order -> order.getId() == orderId).findFirst().orElseGet(Order::new); // orders.stream()
	}
	
	/**
	 * @return true if customer has at least one order with status ACTIVE
	 */
	public boolean p3_hasActiveOrders(Customer customer) {
		return customer.getOrders().stream().anyMatch(order -> order.getStatus().equals(Order.Status.ACTIVE));
	}

	/**
	 * An Order can be returned if it doesn't contain 
	 * any OrderLine with isSpecialOffer()==true
	 */
	public boolean p4_canBeReturned(Order order) {
		return order.getOrderLines().stream().noneMatch(OrderLine::isSpecialOffer); // order.getOrderLines().stream()
	}
	
	// ---------- select the best ------------
	
	/**
	 * The Order with maximum getTotalPrice. 
	 * i.e. the most expensive Order, or null if no Orders
	 * - Challenge: return an Optional<creationDate>
	 */
	public Order p5_getMaxPriceOrder(Customer customer) {
		return customer.getOrders().stream().max(Comparator.comparing(Order::getTotalPrice)).orElse(null);
	}
	
	/**
	 * last 3 Orders sorted descending by creationDate
	 */
	public List<Order> p6_getLast3Orders(Customer customer) {
		return customer.getOrders().stream().sorted(Comparator.comparing(Order::getCreationDate).reversed()).limit(3).collect(toList());
	}
	
	
}
