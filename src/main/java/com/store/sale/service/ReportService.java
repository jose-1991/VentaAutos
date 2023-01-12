package com.store.sale.service;

import com.store.sale.dao.ReportsDAO;
import com.store.sale.exceptions.RecordsNotFoundException;
import com.store.sale.models.StateAndQuantity;

import java.util.*;
import java.util.stream.Collectors;

import static com.store.sale.helper.FileHelper.createPdfReport;
import static com.store.sale.helper.ValidationHelper.*;

public class ReportService {
    private ReportsDAO reportsDAO = new ReportsDAO();
    private String date;
    private int year;
    private String productName;
    private String state;

    public void generateDailyReport() {
        System.out.println("===== Enter the date for the report   (dd/mm/yyyy) =====");
        date = validateDate(scanner.nextLine());
        List<Double> totalSales = findDailyTotalSales();

        double dailyTotal = computeTotal(totalSales);
        String name = "Daily Report (" + date.replace('/', '_') + ")";
        String content = "============== Date: " + date + " ==============\n" +
                "Total Sales = " + dailyTotal;
        createPdfReport(name, content);
        System.out.println(content);
    }

    public void generateTopTenProductPerYear() {
        System.out.println("====== Enter the year =====");
        year = validateIsPositiveInteger(scanner.nextLine(), MIN_YEAR, getCurrentYear());
        List<String> topTenProducts = findTopTenProducts();
        String name = "Top ten product (" + year + ")";
        StringBuilder content = new StringBuilder();
        content.append("============= Top 10 product in year: ").append(year).append(" =============\n");
        topTenProducts.forEach(content::append);
        createPdfReport(name, content.toString());
        System.out.println(content);
    }

    public void generateTopStateReportPerProduct() {
        System.out.println("====== Enter Product Name =====");
        productName = validateIsNotEmpty(scanner.nextLine());
        List<StateAndQuantity> stateAndQuantityList = findTopState();

        String topState = computeTopState(stateAndQuantityList);
        String name = "Top state report (" + productName + ")";
        String content = "======= Top state for product: " + productName + " ========\n" + topState;
        createPdfReport(name, content);
        System.out.println(content);

    }

    public void generateTopCustomerReportPerState() {
        System.out.println("======== Enter State =======");
        state = validateOnlyLetters(scanner.nextLine());
        List<String> customerList = findTopCustomer();
        String topCustomer = computeTopCustomer(customerList);
        String name = "Top customer Report (" + state + ")";
        String content = "======== Top customer for state: " + state + " ==========\n" + topCustomer;
        createPdfReport(name, content);
        System.out.println(content);
    }

    private String computeTopCustomer(List<String> customerList) {
        Map<String, Integer> topCustomerMap = new HashMap<>();
        for (String c : customerList) {
            Integer newValue = 1;
            if (topCustomerMap.containsKey(c)) {
                newValue = topCustomerMap.get(c) + 1;
            }
            topCustomerMap.put(c, newValue);
        }
        Map<String, Integer> topCustomerMapSorted = mapSortedByValueReversed(topCustomerMap);

        return (String) topCustomerMapSorted.keySet().toArray()[0];
    }

    private Map<String, Integer> mapSortedByValueReversed(Map<String, Integer> map) {
        return map.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    private List<String> findTopCustomer() {
        while (true) {
            try {
                return reportsDAO.findTopCustomerPerStateInDb(state);
            } catch (RecordsNotFoundException e) {
                System.out.println(TRY_AGAIN_MESSAGE);
                state = validateOnlyLetters(scanner.nextLine());
            }
        }
    }

    private String computeTopState(List<StateAndQuantity> stateAndQuantityList) {
        Map<String, Integer> stateAndQuantityMap = new HashMap<>();
        for (StateAndQuantity s : stateAndQuantityList) {
            if (stateAndQuantityMap.containsKey(s.getState())) {
                Integer currentQuantity = stateAndQuantityMap.get(s.getState());
                s.setQuantity(s.getQuantity() + currentQuantity);
            }
            stateAndQuantityMap.put(s.getState(), s.getQuantity());
        }
        Map<String, Integer> stateAndQuantityMapSorted = mapSortedByValueReversed(stateAndQuantityMap);

        return (String) stateAndQuantityMapSorted.keySet().toArray()[0];
    }

    private List<StateAndQuantity> findTopState() {
        while (true) {

            try {
                return reportsDAO.findStateAndQuantityPerProductInDb(productName);
            } catch (RecordsNotFoundException e) {
                System.out.println(TRY_AGAIN_MESSAGE);
                productName = validateIsNotEmpty(scanner.nextLine());
            }
        }
    }

    private List<String> findTopTenProducts() {
        while (true) {

            try {
                return reportsDAO.findTopTenProductPerYearInDb(String.valueOf(year));
            } catch (RecordsNotFoundException e) {
                System.out.println(TRY_AGAIN_MESSAGE);
                year = validateIsPositiveInteger(scanner.nextLine(), MIN_YEAR, getCurrentYear());
            }
        }
    }

    private double computeTotal(List<Double> totalSales) {
        double total = 0;
        for (Double d : totalSales) {
            total += d;
        }
        return total;
    }

    private List<Double> findDailyTotalSales() {
        while (true) {

            try {
                return reportsDAO.findDailyTotalSalesInDb(date);

            } catch (RecordsNotFoundException e) {
                System.out.println(TRY_AGAIN_MESSAGE);
                date = validateDate(scanner.nextLine());
            }
        }
    }
}
