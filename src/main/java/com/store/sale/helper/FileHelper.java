package com.store.sale.helper;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.store.sale.exceptions.DifferentExtensionException;
import com.store.sale.exceptions.EmptyFileException;
import com.store.sale.exceptions.ReportPdfNotFoundException;
import com.store.sale.models.Address;
import com.store.sale.models.Customer;
import com.store.sale.models.Order;
import com.store.sale.models.Product;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHelper {
    public static final String FILE_NAME = "C:\\Users\\JoSe\\Desktop\\Proyecto1\\StoreData.csv";
    public static final String LOCATION_REPORT = "C:\\Users\\JoSe\\Desktop\\CursoJava\\IdeaProjects\\proyectoN1\\src\\main\\java\\org\\jflores\\project\\reports\\";
    public static final String SEPARATOR = ";";
    public static final String EXTENSION_CSV = ".csv";
    public static final String EXTENSION_PDF = ".pdf";
    static int nRow = 1;
    public static List<Customer> customerList = new ArrayList<>();
    public static List<Product> productList = new ArrayList<>();
    public static List<Order> orderList = new ArrayList<>();
    public static List<Address> addressList = new ArrayList<>();

    public static void convertCsvToObjectLists() throws IOException {

        if (!FILE_NAME.endsWith(EXTENSION_CSV)) {
            throw new DifferentExtensionException("file with different extension, select a .csv file");
        }
        FileReader file = new FileReader(FILE_NAME);
        BufferedReader bufferedReader = new BufferedReader(file);
        bufferedReader.readLine();
        String line = bufferedReader.readLine();
        if (line == null) {
            throw new EmptyFileException("The selected file is empty");
        }
        while (line != null) {
            nRow++;
            String[] content = line.split(SEPARATOR);
            if (content.length > 0) {
                fillOutLists(content);
            } else {
                System.out.println("content in row" + nRow + " is empty");
            }
            line = bufferedReader.readLine();
        }
    }

    private static void fillOutLists(String[] content) {
        boolean isValidOrder = true;
        boolean isEmptyValue;
        for (String l : content) {
            isEmptyValue = l.isEmpty() || content.length < 20;
            if (isEmptyValue) {
                isValidOrder = false;
                break;
            }
        }
        if (isValidOrder) {
            Customer customer = new Customer();
            Address address = new Address();
            Product product = new Product();
            Order order = new Order();

            order.setOrderId(content[0]);
            order.setOrderDate(content[1]);
            order.setAddressId(Integer.parseInt(content[9]));
            order.setCustomerId(content[3]);
            order.setProductId(content[11]);
            order.setPrice(Double.parseDouble(content[15].replace(',', '.')));
            order.setQuantity(Integer.parseInt(content[16]));
            order.setDiscount(Double.parseDouble(content[17].replace(',', '.')));
            order.setTotal(Double.parseDouble(content[18].replace(',', '.')));
            order.setProfit(Double.parseDouble(content[19].replace(',', '.')));
            customer.setCustomerId(content[3]);
            customer.setCustomerName(content[4]);
            address.setAddressId(order.getAddressId());
            address.setCountry(content[6]);
            address.setCity(content[7]);
            address.setState(content[8]);
            product.setProductId(content[11]);
            product.setCategory(content[12]);
            product.setSubCategory(content[13]);
            product.setProductName(content[14]);

            if (!customerList.contains(customer)) {
                customerList.add(customer);
            }
            if (!addressList.contains(address)) {
                addressList.add(address);
            }
            if (!productList.contains(product)) {
                productList.add(product);
            }
            if (orderList.contains(order)) {
                System.out.println(
                        "orderId:" + order.getOrderId() + "  duplicate, this order will not be registered");
            } else {
                orderList.add(order);
            }
        } else {
            System.out.println(
                    "an empty value was found in a row" + nRow + ", this order will not be registered");
        }
    }

    public static void createPdfReport(String name, String content) {
        Document document = new Document();
        try {
            String location = LOCATION_REPORT + name + EXTENSION_PDF;
            PdfWriter.getInstance(document, new FileOutputStream(location));

            document.open();
            Paragraph paragraph = new Paragraph(content);
            document.add(paragraph);
            document.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new ReportPdfNotFoundException("Error when updating report, the pdf report is in use");
        } catch (DocumentException e) {
            System.out.println("There was an error trying to create a PDF report");
            e.printStackTrace();
        }
    }

}
