package eu.acme.demo.web;

import org.modelmapper.ModelMapper;

import eu.acme.demo.domain.Order;
import eu.acme.demo.domain.OrderItem;
import eu.acme.demo.errorhandling.DuplicateResourceException;
import eu.acme.demo.errorhandling.ResourceNotFoundException;
import eu.acme.demo.repository.OrderItemRepository;
import eu.acme.demo.repository.OrderRepository;
import eu.acme.demo.web.dto.OrderDto;
import eu.acme.demo.web.dto.OrderItemDto;
import eu.acme.demo.web.dto.OrderLiteDto;
import eu.acme.demo.web.dto.OrderRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
public class OrderAPI {

	@Autowired
	private final ModelMapper modelMapper;
	
    @Autowired
    private ObjectMapper objectMapper;
    
	private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;


    
    public OrderAPI(ModelMapper modelMapper, OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
		super();
		this.modelMapper = modelMapper;
		this.orderRepository = orderRepository;
		this.orderItemRepository = orderItemRepository;
	}

    /**
     * Fetch all orders in DB.
     * 
     * @return
     */
	@GetMapping
    public List<OrderLiteDto> fetchOrders() {
    	return orderRepository.findAll().stream().map(order -> generateOrderDtoFromOrder(order)).collect(Collectors.toList());
    }

	/**
	 * Fetch specific order from DB. If order id does not exist then return an
	 * HTTP 400 (bad request) with a proper payload that contains an error
	 * code and an error message 
	 * 
	 * @param orderId
	 * @return
	 * @throws ResourceNotFoundException
	 */
	@GetMapping("/{orderId}")
    public OrderDto fetchOrder(@PathVariable UUID orderId) throws ResourceNotFoundException {
		Optional<Order> result = orderRepository.findById(orderId);
		if (result.isPresent()) {
			return generateOrderDtoFromOrder(result.get());
		} else {
			throw new ResourceNotFoundException("No order found with ID: " + orderId);
		}
    }

    /**
     * Submit a new order. If client reference code already exists then return
     * an HTTP 400 (bad request) with a proper payload that contains an error
     * code and an error message.
     *
     * @param orderRequest
     * @return
     * @throws DuplicateResourceException 
     */
    @PostMapping
    public OrderDto submitOrder(@RequestBody OrderRequest orderRequest) throws DuplicateResourceException {
    	// Check for existing client reference code
    	Optional<Order> duplicateOrder = orderRepository.findAll().stream().filter(order -> order.getClientReferenceCode().equals(orderRequest.getClientReferenceCode())).findFirst();
    	if (duplicateOrder.isPresent()) {
    		throw new DuplicateResourceException("Order not submited because client reference code aready exists: " + orderRequest.getClientReferenceCode()); 
    	}
    	
    	// Save Order data to DB 
    	Order order = modelMapper.map(orderRequest.getOrder(), Order.class);
    	order.setClientReferenceCode(orderRequest.getClientReferenceCode());
    	orderRepository.saveAndFlush(order);

    	// Save OrderItem data to DB
    	List<OrderItem> orderItems = orderRequest.getOrder().getOrderItems().stream().map(
    			orderItem -> {
    				OrderItem mappedOrderItem = modelMapper.map(orderItem, OrderItem.class);
    				mappedOrderItem.setOrder(order);
    				return mappedOrderItem;
    			}).collect(Collectors.toList());
    	orderItemRepository.saveAll(orderItems);
    	orderItemRepository.flush();
    	
    	// Return submitted order
    	return orderRequest.getOrder();
    }

    private OrderDto generateOrderDtoFromOrder(Order order) {
		OrderDto orderDto = modelMapper.map(order, OrderDto.class);
		orderDto.setOrderItems(orderItemRepository
				.findAll()
				.stream()
				.filter(item -> item.getOrder().getId().equals(order.getId()))
				.map(item -> modelMapper.map(item, OrderItemDto.class))
				.collect(Collectors.toList()));
		
		return orderDto;
    }
}
