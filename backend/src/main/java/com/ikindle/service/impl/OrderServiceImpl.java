package com.ikindle.service.impl;

import com.ikindle.common.BusinessException;
import com.ikindle.common.ErrorCode;
import com.ikindle.dto.CreateOrderRequest;
import com.ikindle.entity.Book;
import com.ikindle.entity.Order;
import com.ikindle.entity.OrderItem;
import com.ikindle.entity.User;
import com.ikindle.repository.BookRepository;
import com.ikindle.repository.OrderItemRepository;
import com.ikindle.repository.OrderRepository;
import com.ikindle.repository.UserRepository;
import com.ikindle.service.AccountService;
import com.ikindle.service.OrderService;
import com.ikindle.service.UserBookshelfService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class OrderServiceImpl extends BaseServiceImpl<Order, Long> implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final AccountService accountService;

    @Autowired(required = false)
    private UserBookshelfService userBookshelfService;

    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderItemRepository orderItemRepository,
                            UserRepository userRepository,
                            BookRepository bookRepository,
                            AccountService accountService) {
        super(orderRepository);
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.accountService = accountService;
    }

    @Override
    public Order createOrder(CreateOrderRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Order order = new Order();
        order.setUser(user);
        order.setOrderNo(generateOrderNo());
        order.setStatus(Order.OrderStatus.PENDING);
        order.setRemark(request.getRemark());
        order.setDiscountAmount(BigDecimal.ZERO);

        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> items = new ArrayList<>();

        for (CreateOrderRequest.OrderItemRequest itemReq : request.getItems()) {
            Book book = bookRepository.findById(itemReq.getBookId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.BOOK_NOT_FOUND));
            if (Boolean.FALSE.equals(book.getPublished())) {
                throw new BusinessException(ErrorCode.BOOK_OFF_SHELF, "图书已下架: " + book.getTitle());
            }
            int quantity = itemReq.getQuantity() == null ? 1 : itemReq.getQuantity();

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setBook(book);
            item.setQuantity(quantity);
            item.setUnitPrice(book.getPrice());
            BigDecimal subtotal = book.getPrice().multiply(BigDecimal.valueOf(quantity));
            item.setSubtotal(subtotal);
            items.add(item);

            total = total.add(subtotal);
        }

        order.setTotalAmount(total);
        order.setPayAmount(total);
        order.setOrderItems(items);

        Order saved = orderRepository.save(order);
        log.info("订单创建成功 orderNo={} userId={} payAmount={}", saved.getOrderNo(),
                user.getId(), saved.getPayAmount());
        return saved;
    }

    @Override
    public Order payOrder(Long orderId, Order.PaymentMethod paymentMethod) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        if (order.getStatus() == Order.OrderStatus.PAID) {
            throw new BusinessException(ErrorCode.ORDER_ALREADY_PAID);
        }
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ILLEGAL,
                    "订单状态不允许支付: " + order.getStatus());
        }

        if (paymentMethod == Order.PaymentMethod.BALANCE) {
            accountService.consume(order.getUser().getId(), order.getPayAmount(),
                    order.getOrderNo(), "购书消费");
        }

        order.setStatus(Order.OrderStatus.PAID);
        order.setPaymentMethod(paymentMethod);
        order.setPayTime(LocalDateTime.now());

        for (OrderItem item : order.getOrderItems()) {
            Book book = item.getBook();
            int currentSales = book.getSalesCount() == null ? 0 : book.getSalesCount();
            book.setSalesCount(currentSales + item.getQuantity());
            bookRepository.save(book);

            if (userBookshelfService != null) {
                try {
                    userBookshelfService.addOrUpdate(order.getUser().getId(), book.getId());
                } catch (Exception e) {
                    log.warn("加入书架失败 userId={} bookId={} err={}",
                            order.getUser().getId(), book.getId(), e.getMessage());
                }
            }
        }

        Order saved = orderRepository.save(order);
        log.info("订单支付成功 orderNo={} method={}", saved.getOrderNo(), paymentMethod);
        return saved;
    }

    @Override
    public Order cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ILLEGAL, "只有待支付订单可取消");
        }
        order.setStatus(Order.OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    @Override
    public Order findByOrderNo(String orderNo) {
        return orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
    }

    @Override
    public Page<Order> findByUserId(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable);
    }

    @Override
    public Page<Order> findByUserIdAndStatus(Long userId, Order.OrderStatus status, Pageable pageable) {
        return orderRepository.findByUserIdAndStatus(userId, status, pageable);
    }

    @Override
    public Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable) {
        return orderRepository.findByStatus(status, pageable);
    }

    private String generateOrderNo() {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "ORD" + ts + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }
}
