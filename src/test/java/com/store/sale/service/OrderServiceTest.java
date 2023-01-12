package com.store.sale.service;


import com.store.sale.models.Order;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;


@RunWith(MockitoJUnitRunner.class)
public class OrderServiceTest {

    private Order order;
    @InjectMocks
    private OrderService orderService;

//    OrderService orderService;
//    Order order;

    @Before
    public void setUp() {

         order = new Order();
        order.setOrderId(new OrderService().generateOrderId());
        order.setCustomerName("Paul Prost");
        order.setProductName("staples");
        order.setQuantity(5);
        order.setPrice(20.5);
        order.setDiscount(0.1);
        order.setAddressId(30080);

    }

    @Test(expected = RuntimeException.class)
    public void testAddNewOrderThrowsRuntimeExceptionWhenCustomerNameIsNull() {

        order.setCustomerName(null);
        orderService.addNewOrder(order);
    }

    @Test(expected = NumberFormatException.class)
    public void testAddNewOrderThrowsNumberFormatExceptionWhenQuantityIsLessOrEqualThanZero() {

        order.setQuantity(0);
        orderService.addNewOrder(order);

    }
    @Test(expected = RuntimeException.class)
    public void testAddNewOrderThrowsRuntimeExceptionWhenDiscountIsLessThanZero(){

        order.setDiscount(-1);
        orderService.addNewOrder(order);
    }
    @Test
    public void testAddNewOrderComputesTotal(){
        double expectedTotal = 92.25;
        Order actualOrder = orderService.addNewOrder(order);
        Assert.assertEquals(expectedTotal,actualOrder.getTotal(),0);

    }
    @Test
    public void testAddNewOrderComputesProfit(){
        double expectedProfit = 73.8;
        Order actualOrder = orderService.addNewOrder(order);
        Assert.assertEquals(expectedProfit,actualOrder.getProfit(),0);
    }
    @Test
    public void testAddNewOrderGeneratesOrderId(){
        String expectedOrderId = "US-2023-123456";
        Order actualOrder = orderService.addNewOrder(order);
        Assert.assertEquals(expectedOrderId,actualOrder.getOrderId());

    }

//    @Test
//    void deleteOrderTest() {
//
//    }
}