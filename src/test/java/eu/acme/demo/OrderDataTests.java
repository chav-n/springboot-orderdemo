package eu.acme.demo;


import eu.acme.demo.domain.Order;
import eu.acme.demo.domain.OrderItem;
import eu.acme.demo.domain.enums.OrderStatus;
import eu.acme.demo.repository.OrderItemRepository;
import eu.acme.demo.repository.OrderRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@SpringBootTest
public class OrderDataTests {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;

    @Test
    public void testCreateOrder() {
        Order o = new Order();
        o.setStatus(OrderStatus.SUBMITTED);
        o.setClientReferenceCode("ORDER-1");
        o.setDescription("first order");
        o.setItemCount(2);
        o.setItemTotalAmount(BigDecimal.valueOf(100.23));
        orderRepository.save(o);

        // Check if order exists
        Assert.isTrue(orderRepository.findById(o.getId()).isPresent(), "order not found");
        // Check for a non existing order
        Assert.isTrue(!orderRepository.findById(UUID.randomUUID()).isPresent(), "non existing order found");

        OrderItem item1 = new OrderItem();
        item1.setOrder(o);
        item1.setUnitPrice(BigDecimal.valueOf(10));
        item1.setUnits(10);
        item1.setTotalPrice(BigDecimal.valueOf(100));
        
        OrderItem item2 = new OrderItem();
        item2.setOrder(o);
        item2.setUnitPrice(BigDecimal.valueOf(0.23));
        item2.setUnits(1);
        item2.setTotalPrice(BigDecimal.valueOf(0.23));
        
        orderItemRepository.saveAll(Arrays.asList(item1, item2));
        orderItemRepository.flush();
        
        // Retrieve items for particular order
        List<OrderItem> savedItems = orderItemRepository
        		.findAll()
        		.stream()
        		.filter(item -> item.getOrder().getId().equals(o.getId()))
        		.collect(Collectors.toList());
        
        
        // Check if the retrieved items match those saved
        //
        // NOTICE: The equality check is by ID and is provided by
        // the PersistableEntity class
        Assert.isTrue(savedItems.containsAll(Arrays.asList(item1, item2)), "The retrieved items do not match those saved.");

        // Check for a non existing item
        Assert.isTrue(!orderItemRepository.findById(UUID.randomUUID()).isPresent(), "non existing item found");
    }

}
