package com.store.sale.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Scanner;

public class ValidationHelper {
    public static final int MAX_VALUE_POSTAL_CODE = 99999;
    public static final String TRY_AGAIN_MESSAGE = "Please try again";
    public static final String ONLY_LETTERS = "^[A-Za-z\\s']+$";
    public static final int MAX_OPTIONS = 7;
    public static final String DATE_FORMAT = "^(\\d{1,2})(/)(\\d{1,2})(/)(\\d{4})$";
    public static final int MIN_YEAR = 2014;
    public static final int MIN_VALUE_INTEGER = 1;

    public static Scanner scanner = new Scanner(System.in);
    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d/M/yyyy");

    public static String validateIsNotEmpty(String value) {
        while (true) {
            if (value.trim().isEmpty()) {
                System.out.println("Error! the data entered is empty\n" +
                        TRY_AGAIN_MESSAGE);
                value = scanner.nextLine();
            } else {
                return value;
            }
        }
    }

    public static String validateOnlyLetters(String value) {
        while (true) {
            value = validateIsNotEmpty(value);
            if (value.matches(ONLY_LETTERS)) {
                return value;
            } else {
                System.out.println("Error! the data entered must be only letters\n" +
                        TRY_AGAIN_MESSAGE);
                value = scanner.nextLine();
            }
        }
    }

    public static int validateIsPositiveInteger(String value, int min, int max) {
        int number;
        while (true) {

            try {
                value = validateIsNotEmpty(value);
                number = Integer.parseInt(value);
                if (number >= min && number <= max) {
                    return number;
                } else {
                    System.out.println("Error! the number entered must be positive and must be between " + min + " to " + max +
                            "\n" + TRY_AGAIN_MESSAGE);
                    value = scanner.nextLine();
                }
            } catch (NumberFormatException e) {
                System.out.println("Error! you must enter only numbers\n" +
                        TRY_AGAIN_MESSAGE);
                value = scanner.nextLine();
            }
        }
    }

    public static double validatePositiveDecimal(String value) {
        double number;
        while (true) {

            try {
                value = validateIsNotEmpty(value);
                number = Double.parseDouble(value);
                if (number < 0) {
                    System.out.println("Error! the number entered must be positive\n" +
                            TRY_AGAIN_MESSAGE);
                    value = scanner.nextLine();
                } else {
                    return Math.round(number * 100.0) / 100.0;
                }
            } catch (NumberFormatException e) {
                System.out.println("Error! the data is invalid (use '.' for decimal)\n" +
                        TRY_AGAIN_MESSAGE);
                value = scanner.nextLine();
            }
        }
    }

    public static double validatePercentage(String value) {
        double number;
        while (true) {
            number = validatePositiveDecimal(value);
            if (number < 1) {
                return number;
            } else {
                System.out.println("Error! the discount is out to range (from 0.0 to 0.9)\n" +
                        TRY_AGAIN_MESSAGE);
                value = scanner.nextLine();
            }
        }
    }

    public static String validateDate(String value) {
        while (true) {
            value = validateDateFormat(value);
            simpleDateFormat.setLenient(false);

            try {
                Date date = simpleDateFormat.parse(value);
                return simpleDateFormat.format(date);
            } catch (ParseException e) {
                System.out.println("Error! the date entered does not exist\n" + TRY_AGAIN_MESSAGE);
                value = scanner.nextLine();
            }
        }
    }

    private static String validateDateFormat(String value) {
        while (true) {
            value = validateIsNotEmpty(value);
            if (value.matches(DATE_FORMAT)) {
                return value;
            } else {
                System.out.println("Error! the format must be: dd/mm/yyyy\n" + TRY_AGAIN_MESSAGE);
                value = scanner.nextLine();
            }
        }
    }
    public static int getCurrentYear() {
        LocalDate date = LocalDate.now();
        return date.getYear();
    }
}
