package cinema;

import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;


class Seat {
    int row;
    int seat;

    public Seat(int row, int seat) {
        this.row = row;
        this.seat = seat;
    }
}

class AlreadyPurchasedSeatException extends Exception{}

class WrongSeatCoordinateException extends Exception{}

public class Cinema {
    private int rows = 0;
    private int seatsInRow = 0;
    private char[][] cinemaSeats;
    private final AtomicInteger numberOfPurchasedTickets = new AtomicInteger();
    private final AtomicInteger sumOfPurchasedTickets = new AtomicInteger();
    private final int totalIncome;

    private Cinema(int rows, int seatsInRow) {
        this.rows = rows;
        this.seatsInRow = seatsInRow;
        this.cinemaSeats = generateEmptyCinemaSeats(rows, seatsInRow);
        this.totalIncome = calculateTotalIncome();
    }

    public static void main(String[] args) {
        Cinema cinema =createCinema();
        cinema.showMenu();
    }

    private static char[][] generateEmptyCinemaSeats(int rows, int seatsInRow) {
        char[][] seats = new char[rows][];
        IntStream.range(0, rows).forEach(i -> {
            char[] row = new char[seatsInRow];
            Arrays.fill(row, 'S');
            seats[i] = row;
        });
        return seats;
    }

    private void showSeats() {
        System.out.println("Cinema:");
        int[] topRow = new int[seatsInRow + 1];
        IntStream.rangeClosed(1, seatsInRow).forEach(i -> topRow[i] = i);
        IntStream.range(0, topRow.length).forEach(i -> {
            if (i == 0) {
                System.out.print(" " + " ");
            } else {
                System.out.print(i + " ");
            }
        });
        System.out.println();
        for (int i = 0; i < cinemaSeats.length; i++) {
            System.out.print((i + 1) + " ");
            for (int j = 0; j < cinemaSeats[i].length; j++) {
                System.out.print(cinemaSeats[i][j] + " ");
            }
            System.out.println();
        }

        System.out.println();
    }

    private int getTotalSeats() {
        return rows * seatsInRow;
    }

    private int calculateTotalIncome() {
        if (getTotalSeats() <= 60) {
            return rows * seatsInRow * 10;
        } else {
            return rows / 2 * seatsInRow * 10 + (rows - rows / 2) * seatsInRow * 8;
        }
    }

    private static Cinema createCinema() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the number of rows:");
        int rows = scanner.nextInt();
        System.out.println("Enter the number of seats in each row:");
        int seats = scanner.nextInt();
        System.out.println();

        return new Cinema(rows, seats);
    }

    private Seat selectSeat() throws WrongSeatCoordinateException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter a row number:");
        int row = scanner.nextInt();
        System.out.println("Enter a seat number in that row:");
        int seat = scanner.nextInt();
        System.out.println();

        if(row < 1 || row > rows || seat < 1 || seat > seatsInRow) {
            throw new WrongSeatCoordinateException();
        }

        return new Seat(row, seat);
    }

    private boolean isFrontHalf(Seat seat) {
        return seat.row <= rows / 2;
    }

    private int calculateTicketPrice(Seat seat) {
        if (getTotalSeats() <= 60 || isFrontHalf(seat)) {
            return 10;
        } else {
            return 8;
        }
    }

    private void printTicketPrice(int price) {
        System.out.println("Ticket price: " + "$" + price);
        System.out.println();
    }

    private void updateSeatsSign(Seat seat) throws AlreadyPurchasedSeatException {
        if (cinemaSeats[seat.row - 1][seat.seat - 1]==('B')) {
            throw new AlreadyPurchasedSeatException();
        }
        cinemaSeats[seat.row-1][seat.seat-1] = 'B';
    }

    private void buyTicket() {
        try {
            Seat seat = selectSeat();
            int price = calculateTicketPrice(seat);
            updateSeatsSign(seat);
            printTicketPrice(price);
            numberOfPurchasedTickets.incrementAndGet();
            sumOfPurchasedTickets.addAndGet(price);
        } catch (AlreadyPurchasedSeatException exception) {
            System.out.println("That ticket has already been purchased!");
            System.out.println();
            buyTicket();
        } catch (WrongSeatCoordinateException exception) {
            System.out.println("Wrong input!");
            System.out.println();
            buyTicket();
        }
    }

    private double getPercentOfSoldTickets() {
        int totalSeats = getTotalSeats();
        int numberOfSoledTickets= numberOfPurchasedTickets.get();
        System.out.println("HERE " + numberOfPurchasedTickets.get()/totalSeats * 100);
        double percentOfSoldTickets = (double)numberOfSoledTickets / (double)totalSeats * 100;
        return percentOfSoldTickets;
    }

    private void showStatistics() {
        System.out.println("Number of purchased tickets: " + numberOfPurchasedTickets.get() );
        System.out.printf("Percentage: %.2f%c %n", getPercentOfSoldTickets(), '%');
        System.out.println("Current income: $" + sumOfPurchasedTickets.get());
        System.out.println("Total income: $" + totalIncome);
        System.out.println();

    }

    private void showMenu() {
        Scanner scanner = new Scanner(System.in);

        while(true) {
            System.out.println("1. Show the seats");
            System.out.println("2. Buy a ticket");
            System.out.println("3. Statistics");
            System.out.println("0. Exit");

            int choose = scanner.nextInt();

            switch(choose) {
                case 0:
                    return;
                case 1:
                    showSeats();
                    break;
                case 2:
                    buyTicket();
                    break;
                case 3:
                    showStatistics();
                    break;
                default:
                    return;
            }
        }
    }

}
