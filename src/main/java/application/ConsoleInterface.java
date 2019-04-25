package application;

import service.OrderService;
import source.Order;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class ConsoleInterface {

    private OrderService orderService;

    private Scanner scanner;

    private List<String> validCommands = Arrays.asList("C", "D", "S", "F", "Q");

    private List<Consumer<Void>> commandToHandlers = createCommandHandlers();

    private boolean isRunning = false;

    public ConsoleInterface(OrderService orderService, Scanner scanner) {
        this.orderService = orderService;
        this.scanner = scanner;
    }

    private List<Consumer<Void>> createCommandHandlers(){
        List<Consumer<Void>> handlers = new ArrayList<>(validCommands.size());
        handlers.add((i) -> this.createOrder());
        handlers.add((i) -> this.deleteOrder());
        handlers.add((i) -> this.viewSummarizedOrders());
        handlers.add((i) -> this.viewFullOrderHistory());
        handlers.add((i) -> this.isRunning = false);
        return handlers;
    }


    private void printBorder(String design, int size){
        IntStream.range(0, size).forEach(i -> System.out.print(design));
        System.out.println("");
    }


    private void createOrder(){
        Order order = new Order();
        System.out.println("Enter  0 for BUY order and 1 for SELL orders");
        Order.TYPE orderType = scanner.nextInt() == 0 ? Order.TYPE.BUY : Order.TYPE.SELL;
        order.setOrderType(orderType);
        System.out.println("Enter userID: (no spaces)");
        order.setUserID(scanner.next());
        System.out.println("Enter quantity");
        order.setQuantity(scanner.nextDouble());
        System.out.println("Enter price");
        order.setPrice(scanner.nextDouble());
        orderService.registerOrders(Collections.singletonList(order));
    }

    private void deleteOrder(){
        try {
            System.out.println("Select input an order number to delete the order");
            printTable("All Orders", orderService.getAllOrderHistory(), true);
            orderService.cancelOrders(scanner.nextInt());
        }catch (IndexOutOfBoundsException e){
            System.out.print("Attempted to delete an order does not exist. \n");
        }
    }

    private void printTable(String name, List<Order> orders, boolean printOrderNumbers){
        System.out.println(name.toUpperCase());
        printBorder("*", 60);
        if (printOrderNumbers){
            for(int i = 0; i < orders.size(); i ++){
                System.out.println("order No. " + i +  ":       " +  orders.get(i));
            }
        }else {
            orders.forEach(System.out::println);
        }
        printBorder("_", 60);
    }

    private void printTables (List<Order> buyOrders, List<Order> sellOrders){
        if (buyOrders.size() == 0 && sellOrders.size() == 0){
            System.out.println("No orders currently. Please create an order first!");
        } else {
            printTable("BUY", buyOrders, false);
            printTable("SELL", sellOrders, false);
        }
    }

    private void printHelp(){
        System.out.println("C: create order, D: delete order, S: view summarized orders, F: view full order history, Q: quit \n");
    }

    private void viewSummarizedOrders(){
       printTables(orderService.getSummarizedInfo(Order.TYPE.BUY), orderService.getSummarizedInfo(Order.TYPE.SELL));
    }

    private void viewFullOrderHistory(){
        printTables(orderService.getOrderHistoryByType(Order.TYPE.BUY), orderService.getOrderHistoryByType(Order.TYPE.SELL));
    }

    public void start() {
        isRunning = true;
        while (isRunning){
            printHelp();
            try{
                commandToHandlers.get(validCommands.indexOf(scanner.next().toUpperCase())).accept(null);
            }catch (InputMismatchException | ArrayIndexOutOfBoundsException e){
                System.out.println("Please enter commands without any spaces. Use numbers/text as input where appropriate");
                start();
            }
        }
        System.exit(0);
    }

}
