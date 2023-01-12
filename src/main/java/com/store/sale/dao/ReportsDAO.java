package com.store.sale.dao;

import com.store.sale.exceptions.RecordsNotFoundException;
import com.store.sale.models.StateAndQuantity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ReportsDAO {

    private Connection getConnection() throws SQLException {
        return ConnectionDB.getInstance();
    }

    public List<Double> findDailyTotalSalesInDb(String date) {
        double total;
        List<Double> totalSales = new ArrayList<>();
        String query = "SELECT total FROM store.order WHERE orderDate = '" + date + "'";

        try (Statement statement = getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                total = resultSet.getDouble("total");
                totalSales.add(total);
            }
            if (totalSales.isEmpty()) {
                throw new RecordsNotFoundException("No records was found on date: " + date);
            }
        } catch (SQLException e) {
            System.out.println("There was an error searching for the total sales");
            e.printStackTrace();
        }
        return totalSales;
    }

    public List<String> findTopTenProductPerYearInDb(String year) {
        List<String> productsPerYear = new ArrayList<>();
        String productName;
        int quantity;
        int id = 0;
        String query = "SELECT p.pName, sum(quantity) as totalQuantity FROM store.order AS o INNER JOIN  product AS p on o.product_ID = p.product_ID \n" +
                "WHERE orderDate LIKE '%" + year + "%' GROUP BY p.pName ORDER BY SUM(quantity) DESC LIMIT 0,10";

        try (Statement statement = getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                quantity = resultSet.getInt("totalQuantity");
                productName = resultSet.getString("pName");
                productsPerYear.add("\nProduct " + (++id) + ": " + productName + "   Quantity: " + quantity);
            }
            if (productsPerYear.isEmpty()) {
                throw new RecordsNotFoundException("No records was found on year: " + year);
            }
        } catch (SQLException e) {
            System.out.println("There was an error when trying to find the best-selling products per year");
            e.printStackTrace();
        }
        return productsPerYear;
    }

    public List<StateAndQuantity> findStateAndQuantityPerProductInDb(String productName) {
        List<StateAndQuantity> stateAndQuantityList = new ArrayList<>();
        String query = "SELECT a.state, o.quantity FROM store.order AS o INNER JOIN product AS p ON o.product_ID = p.product_ID INNER JOIN address AS a\n" +
                " ON o.address_ID = a.address_ID WHERE p.pName ='" + productName + "'";

        try (Statement statement = getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                StateAndQuantity stateAndQuantity = new StateAndQuantity();
                stateAndQuantity.setState(resultSet.getString("state"));
                stateAndQuantity.setQuantity(resultSet.getInt("quantity"));

                stateAndQuantityList.add(stateAndQuantity);
            }
            if (stateAndQuantityList.isEmpty()){
                throw new RecordsNotFoundException("No records was found for product: " + productName);
            }
        } catch (SQLException e) {
            System.out.println("There was an error when trying to find state and quantity per product in Data Base");
            e.printStackTrace();
        }
        return stateAndQuantityList;
    }

    public List<String> findTopCustomerPerStateInDb(String state){
        List<String> customerList = new ArrayList<>();
        String query = "SELECT c.cName FROM store.order AS o INNER JOIN customer AS c ON o.customer_ID = c.customer_ID INNER JOIN address AS a\n" +
                "ON o.address_ID = a.address_ID  WHERE a.state ='"+state +"'";

        try (Statement statement = getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery(query)){
            while (resultSet.next()){
                customerList.add(resultSet.getString("cName"));
            }
            if (customerList.isEmpty()){
                throw new RecordsNotFoundException("No records was found for state: " + state);
            }
        } catch (SQLException e) {
            System.out.println("There was an error when trying to find top customer per state in Data Base");
            e.printStackTrace();
        }
        return customerList;
    }
}