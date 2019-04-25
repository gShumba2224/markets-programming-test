package application;

import service.OrderService;
import service.OrderServiceImpl;
import source.DataSourceImpl;

import java.util.Scanner;


public class App {

    private static Scanner scanner = new Scanner(System.in);

    private static OrderService orderService = new OrderServiceImpl(new DataSourceImpl());


    private static ConsoleInterface consoleInterface = new ConsoleInterface(orderService, scanner);

    public static void main(String[] args) {

        consoleInterface.start();

    }
}
