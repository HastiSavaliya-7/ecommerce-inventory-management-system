package com.ecommerce.Ecommerce.service;

import com.ecommerce.Ecommerce.model.Order;
import com.ecommerce.Ecommerce.model.Product;
import com.ecommerce.Ecommerce.repository.OrderRepository;
import com.ecommerce.Ecommerce.repository.ProductRepository;
import com.opencsv.CSVWriter;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    OrderRepository orderRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    NotificationService notificationService;
    @Value("${app.admin.phone}")
    private String adminPhone;
    @Value("${app.admin.email}")
    private String adminEmail;
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);


    @Transactional
    public Order orderSave(Order order) {
        Product product = productRepository.findById(order.getProductId()).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "no product"));
        //validation
        validationOrder(order);
        logger.info("Validation checked.");

        //check stock
        checkStock(product, order);
        logger.info("Stock checked.");

        //payment
        if (!makePayment(order)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Payment Failed!");
        }
        logger.info("Payment Successfully");

        //bill create and send bill
        sendBill(order);
        logger.info("bill sent to customer.");

        //update stock and check threshold
        updateStock(product, order);
        logger.info("stock updated successfully.");

        return orderRepository.save(order);
    }

    private void validationOrder(Order order) {
        String mno = order.getMobileNo();
        if (!mno.matches("^\\+91\\d{10}$")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Mobile Number is not valid");
        }
        if (order.getQuantity() <= 0) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "quantity can't be 0");
        }
    }

    private void updateStock(Product product, Order order) {
        product.decStock(order.getQuantity());

        //check threshold
        if (product.getThreshold() > product.getStock()) {
            notificationService.sendTextEmail(adminEmail, createRestockAlert(product));
        }

        //for current stock csv send email
        try {
            String path = exportCsv();
            notificationService.sendEmail(adminEmail, path);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sending Mail Failed.!");
        }
    }

    private boolean makePayment(Order order) {
        if (!payment()) {
            notificationService.sendTextEmail(adminEmail, createPaymentFailureMessage(order));
            return false;
        }
        return true;
    }

    private void sendBill(Order order) {
        String bill = createBill(order);
        notificationService.sendMessage(order.getMobileNo(),bill);
        notificationService.sendTextEmail(adminEmail, bill);
    }

    private void checkStock(Product product, Order order) {
        if (product.getStock() < order.getQuantity()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "insufficient stock");
        }
    }

    public String createBill(Order order) {
        Product product = productRepository.findById(order.getProductId()).orElseThrow();


//        BillDTO billDTO = new BillDTO();
//        billDTO.setCustomerName(orderSave.getCustomerName());
//        billDTO.setProductName(product.getName());
//        billDTO.setActualPrice(product.getPrice());
//        billDTO.setQuantity(orderSave.getQuantity());
//        billDTO.setAmount(orderSave.getQuantity()* product.getPrice());
//        billDTO.setGST(product.getGST());
//
//        double gst = (orderSave.getQuantity()* product.getPrice()) * product.getGST()/100;
//        billDTO.setTotalAmount(orderSave.getQuantity()* product.getPrice()+gst);
//
//        Gson gson = new Gson();
//        return gson.toJson(billDTO);

        double amount = order.getQuantity() * product.getPrice();
        double gst = amount * product.getGST() / 100;
        double total = amount + gst;


        return "🧾 Order Bill" +
                "\n---------------------------" +
                "\n👤 Customer Name: " + order.getCustomerName() +
                "\n📦 Product Name: " + product.getName() +
                "\n💰 Price: ₹" + product.getPrice() +
                "\n🔢 Quantity: " + order.getQuantity() +
                "\n💵 Amount: ₹" + amount +
                "\n📊 GST (" + product.getGST() + "%): ₹" + gst +
                "\n---------------------------" +
                "\n✅ Total: ₹" + total;
    }

    public String createPaymentFailureMessage(Order order) {
        return "❌ Payment Failed" +
                "\n---------------------------" +
                "\n👤 Customer Name: " + order.getCustomerName() +
                "\n📱 Mobile: " + order.getMobileNo() +
                "\n📦 Product ID: " + order.getProductId() +
                "\n🔢 Quantity: " + order.getQuantity() +
                "\n---------------------------" +
                "\n⚠️ Your payment could not be processed." +
                "\nPlease try again or contact support.";
    }

    public String createRestockAlert(Product product) {
        return "⚠️ Restock Alert" +
                "\n📦 Product: " + product.getName() +
                "\n📉 Current Stock: " + product.getStock() +
                "\n⚠️ Threshold: " + product.getThreshold() +
                "\n🔴 Stock is below threshold. Please restock immediately!";

    }

    public boolean payment() {
        return Math.random() < 0.8;
    }

    public String exportCsv() throws Exception {
        List<Product> productList = productRepository.findAll();
        String path = "reports/" + LocalDate.now() + "_stock.csv";
        new File("reports").mkdir();

        try (CSVWriter writer = new CSVWriter(new FileWriter(path))) {
            writer.writeNext(new String[]{"ID", "Name", "Price", "GST", "Stock", "Threshold"});

            for (Product product : productList) {
                writer.writeNext(
                        new String[]{
                                String.valueOf(product.getId()),
                                product.getName(),
                                String.valueOf(product.getPrice()),
                                String.valueOf(product.getGST()),
                                String.valueOf(product.getStock()),
                                String.valueOf(product.getThreshold())
                        }
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("csv generation failed..");
        }

        return new File(path).getAbsolutePath();

    }

    @Scheduled(cron = "0 25 14 * * *")
    public void scheduleEmail() throws Exception {
        notificationService.sendEmail(adminEmail, exportCsv());
    }
}