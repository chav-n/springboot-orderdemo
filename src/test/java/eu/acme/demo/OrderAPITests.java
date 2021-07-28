package eu.acme.demo;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.acme.demo.domain.Order;
import eu.acme.demo.domain.OrderItem;
import eu.acme.demo.domain.enums.OrderStatus;
import eu.acme.demo.repository.OrderItemRepository;
import eu.acme.demo.repository.OrderRepository;
import eu.acme.demo.web.dto.OrderDto;
import eu.acme.demo.web.dto.OrderItemDto;
import eu.acme.demo.web.dto.OrderRequest;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.Assert;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
class OrderAPITests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private ModelMapper modelMapper;
    
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;

    /**
     * Check order submission service: Send an order request and compare
     * stored order against original.
     * 
     * @throws Exception
     */
    @Test
    void testOrderAPI() throws Exception {
        
        OrderRequest request = genTestOrderRequest("ORDER-2", "second order");
    	OrderDto order = request.getOrder();
                
        String orderRequestAsString = objectMapper.writeValueAsString(request);
        MvcResult orderResult = this.mockMvc.perform(post("http://api.okto-demo.eu/orders")
                .content(orderRequestAsString)
                .contentType("application/json")
                .accept("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        OrderDto returnOrder = objectMapper.readValue(
        		orderResult.getResponse().getContentAsString(),
        		OrderDto.class);
        
        Assert.isTrue(order.equals(returnOrder), "Order not found: " + objectMapper.writeValueAsString(order));
    }

    /**
     * Check order submission service: Send same order twice, expect
     * error on second submission.
     * 
     * @throws Exception
     */
    @Test
    void testOrderDoubleSubmission() throws Exception{
        OrderRequest request = genTestOrderRequest("ORDER-3", "third order");

        String orderRequestAsString = objectMapper.writeValueAsString(request);
        
        // Send fresh order
        MvcResult orderResult1 = this.mockMvc.perform(post("http://api.okto-demo.eu/orders")
                .content(orderRequestAsString)
                .contentType("application/json")
                .accept("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        // Resend same order
        MvcResult orderResult2 = this.mockMvc.perform(post("http://api.okto-demo.eu/orders")
                .content(orderRequestAsString)
                .contentType("application/json")
                .accept("application/json"))
                .andExpect(status().isBadRequest())
                .andReturn();
    }
    
    @Test
    void testFetchAllOrders() throws Exception {
        OrderRequest request = genTestOrderRequest("ORDER-4", "fourth order");

        String orderRequestAsString = objectMapper.writeValueAsString(request);
        
        // Send one order so that the return array cannot be empty
        MvcResult orderResult = this.mockMvc.perform(post("http://api.okto-demo.eu/orders")
                .content(orderRequestAsString)
                .contentType("application/json")
                .accept("application/json"))
                .andExpect(status().isOk())
                .andReturn();

    	
        MvcResult result = this.mockMvc.perform(get("http://api.okto-demo.eu/orders")
                .accept("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        
        
        OrderDto[] allOrders = objectMapper.readValue(
        		result.getResponse().getContentAsString(),
        		OrderDto[].class);
        
        Assert.isTrue(Arrays.asList(allOrders).contains(request.getOrder()), "Full list of orders does not contain test order: " + objectMapper.writeValueAsString(request.getOrder()));
    }

    /**
     * create 1 order (by directly saving to database) and then invoke API call to fetch order
     * check response contains the correct order
     * check that when an order not exists, server responds with http 400
     * @throws Exception 
     * 
     */
    @Test
    void testFetchCertainOrder() throws Exception {
        Order o = new Order();
        o.setStatus(OrderStatus.SUBMITTED);
        o.setClientReferenceCode("ORDER-5");
        o.setDescription("fifth order");
        o.setItemCount(2);
        o.setItemTotalAmount(BigDecimal.valueOf(100.23));
        orderRepository.saveAndFlush(o);

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
        
        MvcResult getExistingOrderResult = this.mockMvc.perform(get("http://api.okto-demo.eu/orders/" + o.getId())
                .accept("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        
        OrderDto returnedOrder = objectMapper.readValue(
        		getExistingOrderResult.getResponse().getContentAsString(),
        		OrderDto.class);
        
        OrderDto expectedOrder = modelMapper.map(o, OrderDto.class);
        OrderItemDto itemDto1 = modelMapper.map(item1,  OrderItemDto.class);
        OrderItemDto itemDto2 = modelMapper.map(item2, OrderItemDto.class);
        expectedOrder.setOrderItems(Arrays.asList(itemDto1, itemDto2));
        
        Assert.isTrue(returnedOrder.equals(expectedOrder), "Order not found: " + objectMapper.writeValueAsString(expectedOrder));
        		
        MvcResult getNotExistingOrderResult = this.mockMvc.perform(get("http://api.okto-demo.eu/orders/" + UUID.randomUUID())
                .accept("application/json"))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    OrderRequest genTestOrderRequest(String clientReferenceCode, String description) {    	
        OrderItemDto item1 = new OrderItemDto();
        item1.setUnitPrice(BigDecimal.valueOf(10));
        item1.setUnits(10);
        item1.setTotalPrice(BigDecimal.valueOf(100));
        
        OrderItemDto item2 = new OrderItemDto();
        item2.setUnitPrice(BigDecimal.valueOf(0.23));
        item2.setUnits(1);
        item2.setTotalPrice(BigDecimal.valueOf(0.23));
        
        OrderDto order = new OrderDto();
        order.setStatus(OrderStatus.SUBMITTED);
        order.setClientReferenceCode(clientReferenceCode);
        order.setDescription(description);
        order.setItemCount(2);
        order.setTotalAmount(BigDecimal.valueOf(100.23));
        order.setOrderItems(Arrays.asList(item1, item2));
    	
        OrderRequest request = new OrderRequest();
        
        request.setOrder(order);
        request.setClientReferenceCode(order.getClientReferenceCode());

        return request;
    }
}

