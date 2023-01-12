package com.store.sale.dao;

import com.store.sale.helper.FileHelper;
import com.store.sale.exceptions.IdValueNotFoundException;
import com.store.sale.models.*;

import java.io.IOException;
import java.sql.*;

public class OrderDAO {
    public OrderDAO() {
    }

    private Connection getConnection() throws SQLException {
        return com.store.sale.dao.ConnectionDB.getInstance();
    }


    public void saveListsToDbTables() {
        try {
            FileHelper.convertCsvToObjectLists();
        } catch (IOException e) {
            throw new RuntimeException("There was an error trying to read the file");
        }
        for (Tables t : Tables.values()) {
            try {
                PreparedStatement statement = getConnection().prepareStatement(t.toString());
                getConnection().setAutoCommit(false);

                switch (t) {
                    case CUSTOMER:
                        for (Customer c : FileHelper.customerList) {

                            statement.setString(1, c.getCustomerId());
                            statement.setString(2, c.getCustomerName());
                            statement.addBatch();
                        }
                        System.out.println("Records were saved in customer table successfully!");

                        break;
                    case ADDRESS:
                        for (Address a : FileHelper.addressList) {

                            statement.setInt(1, a.getAddressId());
                            statement.setString(2, a.getCountry());
                            statement.setString(3, a.getState());
                            statement.setString(4, a.getCity());
                            statement.addBatch();
                        }
                        System.out.println("Records were saved in address table successfully!");

                        break;
                    case PRODUCT:
                        for (Product p : FileHelper.productList) {

                            statement.setString(1, p.getProductId());
                            statement.setString(2, p.getCategory());
                            statement.setString(3, p.getSubCategory());
                            statement.setString(4, p.getProductName());
                            statement.addBatch();
                        }
                        System.out.println("Records were saved in product table successfully!");

                        break;
                    case ORDER:
                        for (Order o : FileHelper.orderList) {

                            statement.setString(1, o.getOrderId());
                            statement.setString(2, o.getOrderDate());
                            statement.setString(3, o.getCustomerId());
                            statement.setInt(4, o.getAddressId());
                            statement.setString(5, o.getProductId());
                            statement.setDouble(6, o.getPrice());
                            statement.setInt(7, o.getQuantity());
                            statement.setDouble(8, o.getDiscount());
                            statement.setDouble(9, o.getTotal());
                            statement.setDouble(10, o.getProfit());
                            statement.addBatch();
                        }
                        System.out.println("Records were saved in order table successfully!");
                        break;
                }
                statement.executeBatch();
                getConnection().commit();
                statement.close();

            } catch (SQLException exception) {
                System.out.println("There was an error trying to save records to the Database");
                exception.printStackTrace();
            }
        }
    }

    public void cleanDbTables() {
        try {
            Statement statement = getConnection().createStatement();

            statement.executeUpdate("DELETE FROM store.order");
            statement.executeUpdate("DELETE FROM store.address");
            statement.executeUpdate("DELETE FROM store.product");
            statement.executeUpdate("DELETE FROM store.customer");
            statement.close();

        } catch (SQLException exception) {
            System.out.println("There was an error trying to delete the records");
            exception.printStackTrace();
        }
    }

    public String findIdValue(String data, Tables table) {
        String idValue = "";
        String columnLabel = "";
        String query = "";
        String errorMessage = "";
        switch (table) {
            case CUSTOMER:
                query = "SELECT customer_ID FROM store.customer WHERE cName = '" + data + "'";
                columnLabel = "customer_ID";
                errorMessage = "An error has occurred!\n" +
                        "customer name: '" + data + "' not found in customer table";
                break;
            case PRODUCT:
                query = "SELECT product_ID FROM store.product WHERE pName = '" + data + "'";
                columnLabel = "product_ID";
                errorMessage = "An error has occurred!\n" +
                        "product name: '" + data + "' not found in product table";
                break;
            case ORDER:
                query = "SELECT order_ID FROM store.order WHERE order_ID = '" + data + "'";
                columnLabel = "order_ID";
                errorMessage = "An error has occurred!\n" +
                        "orderId: '" + data + "' not found in order table";
                break;
        }
        try (Statement statement = getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                idValue = resultSet.getString(columnLabel);
            }
            if (idValue.isEmpty()) {
                throw new IdValueNotFoundException(errorMessage);
            }

        } catch (SQLException exception) {
            System.out.println("There was an error searching for the id value");
            exception.printStackTrace();
        }
        return idValue;
    }

    public boolean addressIdExists(int value) {
        String query = "SELECT address_ID FROM store.address WHERE address_ID = '" + value + "'";
        boolean addressIdExists = false;

        try (Statement statement = getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            addressIdExists = resultSet.next();
        } catch (SQLException e) {
            System.out.println("There was an error searching for the id value");
            e.printStackTrace();
        }

        return addressIdExists;
    }

    public void modifyTableData(Order order) {
        String query = "UPDATE store.order SET quantity = ?, discount = ?, total = ?, profit = ? WHERE order_ID = '" + order.getOrderId() + "'";

        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
            statement.setInt(1, order.getQuantity());
            statement.setDouble(2, order.getDiscount());
            statement.setDouble(3, order.getTotal());
            statement.setDouble(4, order.getProfit());
            statement.executeUpdate();
            System.out.println("The registry was modified successfully!");

        } catch (SQLException exception) {
            System.out.println("There was an error trying to modify a record");
            exception.printStackTrace();
        }
    }

    public void addNewOrderToDb(Order order) {
        String query = "INSERT INTO store.order(order_ID, orderDate, customer_ID, address_ID, product_ID, price, quantity, discount, total, profit)"
                + "VALUES(?,?,?,?,?,?,?,?,?,?)";

        try (PreparedStatement statement = getConnection().prepareStatement(query)) {
            statement.setString(1, order.getOrderId());
            statement.setString(2, order.getOrderDate());
            statement.setString(3, order.getCustomerId());
            statement.setInt(4, order.getAddressId());
            statement.setString(5, order.getProductId());
            statement.setDouble(6, order.getPrice());
            statement.setInt(7, order.getQuantity());
            statement.setDouble(8, order.getDiscount());
            statement.setDouble(9, order.getTotal());
            statement.setDouble(10, order.getProfit());
            statement.executeUpdate();

            System.out.println("The order saved successfully!");

        } catch (SQLException exception) {
            System.out.println("There was an error trying to add a new order");
            exception.printStackTrace();
        }

    }

    private boolean isEmpty() {
        String query = "SELECT count(*) FROM store.order";

        try (Statement statement = getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                int count = resultSet.getInt("count(*)");
                if (count > 0) {
                    return false;
                }
            }
        } catch (SQLException exception) {
            System.out.println("There was an error checking if the database is empty");
        }
        return true;

    }

    public Order getOrderRecord(String id) {
        Order order = new Order();
        String query = "SELECT * FROM store.order WHERE order_ID = '" + id + "'";
        try (Statement statement = getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                order.setOrderId(resultSet.getString("order_ID"));
                order.setOrderDate(resultSet.getString("orderDate"));
                order.setCustomerId(resultSet.getString("customer_ID"));
                order.setAddressId(resultSet.getInt("address_ID"));
                order.setProductId(resultSet.getString("product_ID"));
                order.setPrice(resultSet.getDouble("price"));
                order.setQuantity(resultSet.getInt("quantity"));
                order.setDiscount(resultSet.getDouble("discount"));
                order.setTotal(resultSet.getDouble("total"));
                order.setProfit(resultSet.getDouble("profit"));
            }

        } catch (SQLException exception) {
            System.out.println("There was an error trying to get an order record");
        }
        return order;


    }

    public void deleteOrderOfDb(String orderId) {
        try (PreparedStatement stmt = getConnection().prepareStatement("DELETE FROM store.order WHERE order_ID =?")) {
            stmt.setString(1, orderId);
            stmt.executeUpdate();

            System.out.println("Order removed successfully!");

        } catch (SQLException e) {
            System.out.println("There was an error trying to delete an order");
            e.printStackTrace();
        }
    }
}
