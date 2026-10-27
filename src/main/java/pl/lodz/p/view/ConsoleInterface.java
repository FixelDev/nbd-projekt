package pl.lodz.p.view;

import pl.lodz.p.manager.GuestManager;
import pl.lodz.p.manager.RentManager;
import pl.lodz.p.manager.RoomManager;
import pl.lodz.p.model.domain.Guest;
import pl.lodz.p.model.domain.Rent;
import pl.lodz.p.model.domain.Room;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class ConsoleInterface {
    private final GuestManager guestManager;
    private final RoomManager roomManager;
    private final RentManager rentManager;
    private final Scanner scanner;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ConsoleInterface(GuestManager guestManager, RoomManager roomManager, RentManager rentManager) {
        this.guestManager = guestManager;
        this.roomManager = roomManager;
        this.rentManager = rentManager;
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        while (true) {
            displayMainMenu();

            int choice;
            try {
                choice = inputToInt();
            } catch (IllegalArgumentException e) {
                continue;
            }

            switch (choice) {
                case 1 -> handleGuestOperations();
                case 2 -> handleRoomOperations();
                case 3 -> handleRentOperations();
                case 0 -> {
                    return;
                }
                default -> System.out.println("Nieprawidłowy wybór!");
            }
        }
    }

    // ==================== MENU ====================

    private void displayMainMenu() {
        displayTitle("SYSTEM HOTELOWY");
        System.out.print("""
                1. Zarządzaj gośćmi
                2. Zarządzaj pokojami
                3. Zarządzaj wypożyczeniami
                0. Wyjście
                Wybierz opcję:\s""");
    }

    private void displayGuestOperationsMenu() {
        displayTitle("ZARZĄDZANIE GOŚĆMI");
        System.out.print("""
                1. Zarejestruj nowego gościa
                2. Wyrejestruj gościa
                3. Aktywuj gościa
                4. Wyświetl informacje o wszystkich gościach
                5. Wyświetl informacje o wybranym gościu
                0. Powrót
                Wybierz opcję:\s""");
    }

    private void displayRoomOperationsMenu() {
        displayTitle("ZARZĄDZANIE POKOJAMI");
        System.out.print("""
                1. Zarejestruj nowy pokój
                2. Wyrejestruj pokój
                3. Aktywuj pokój
                4. Wyświetl informacje o wszystkich pokojach
                5. Wyświetl informacje o wybranym pokoju
                0. Powrót
                Wybierz opcję:\s""");
    }

    private void displayGuestTypeChoiceMenu() {
        displayTitle("TYP GOŚCIA");
        System.out.print("""
                1. Domyślny (Default)
                2. Extra
                3. Premium
                Wybierz opcję:\s""");
    }

    private void displayRentOperationsMenu() {
        displayTitle("ZARZĄDZANIE WYPOŻYCZENIAMI");
        System.out.print("""
                1. Wypożycz pokój
                2. Zakończ wypożyczenie (zwróć pokój)
                3. Wyświetl informacje o wszystkich wypożyczeniach
                4. Wyświetl informacje o wybranym wypożyczeniu (po UUID)
                5. Wyświetl informacje o wypożyczeniach wybranego gościa
                0. Powrót
                Wybierz opcję:\s""");
    }

    private void displayTitle(String title) {
        System.out.println("\n==== " + title + " ====");
    }

    // ==================== POBIERANIE I WALIDACJA DANYCH ====================

    private String readNonEmptyString(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            System.err.println("Błąd: Wartość nie może być pusta!");
            throw new IllegalArgumentException("Puste wejście");
        }
        return input;
    }

    private int inputToInt() {
        String input = scanner.nextLine().trim();
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.err.println("Błąd: Nie można przekonwertować wprowadzonej wartości na liczbę całkowitą!");
            throw new IllegalArgumentException(e);
        }
    }

    private double inputToDouble() {
        String input = scanner.nextLine().trim();
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            System.err.println("Błąd: Nie można przekonwertować wprowadzonej wartości na liczbę zmiennoprzecinkową!");
            throw new IllegalArgumentException(e);
        }
    }

    private UUID inputToUUID(String prompt) {
        String input = readNonEmptyString(prompt);
        try {
            return UUID.fromString(input);
        } catch (IllegalArgumentException e) {
            System.err.println("Błąd: Nieprawidłowy format identyfikatora UUID!");
            throw e;
        }
    }

    private LocalDateTime inputToDateTime(String prompt) {
        String input = readNonEmptyString(prompt);
        try {
            return LocalDateTime.parse(input, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            System.err.println("Błąd: Nieprawidłowy format daty! Oczekiwany format to yyyy-MM-dd HH:mm (np. 2026-10-28 14:00)");
            throw new IllegalArgumentException(e);
        }
    }

    // ==================== OPERACJE NA GOŚCIACH ====================

    private void handleGuestOperations() {
        while (true) {
            displayGuestOperationsMenu();

            int choice;
            try {
                choice = inputToInt();
            } catch (IllegalArgumentException e) {
                continue;
            }

            switch (choice) {
                case 1 -> registerNewGuest();
                case 2 -> unregisterGuest();
                case 3 -> activateGuest();
                case 4 -> displayAllGuests();
                case 5 -> displayGuest();
                case 0 -> {
                    return;
                }
                default -> System.out.println("Nieprawidłowy wybór!");
            }
        }
    }

    private void registerNewGuest() {
        displayTitle("REJESTRACJA NOWEGO GOŚCIA");

        String firstName, lastName, personalId, city, street, number;
        try {
            firstName = readNonEmptyString("Imię: ");
            lastName = readNonEmptyString("Nazwisko: ");
            personalId = readNonEmptyString("PESEL / ID osobiste: ");
            city = readNonEmptyString("Miasto: ");
            street = readNonEmptyString("Ulica: ");
            number = readNonEmptyString("Numer domu/mieszkania: ");
        } catch (IllegalArgumentException e) {
            System.err.println("Rejestracja gościa została przerwana!");
            return;
        }

        displayGuestTypeChoiceMenu();

        int choice;
        try {
            choice = inputToInt();
        } catch (IllegalArgumentException e) {
            System.err.println("Rejestracja gościa została przerwana!");
            return;
        }

        String guestTypeStr = switch (choice) {
            case 1 -> "default";
            case 2 -> "extra";
            case 3 -> "premium";
            default -> {
                System.out.println("Nieprawidłowy wybór! Wybrano domyślny typ (default).");
                yield "default";
            }
        };

        try {
            guestManager.registerGuest(firstName, lastName, personalId, city, street, number, guestTypeStr);
            System.out.println("Gość został pomyślnie zarejestrowany.");
        } catch (RuntimeException e) {
            System.err.println("Błąd: " + e.getMessage());
        }
    }

    private void unregisterGuest() {
        displayTitle("WYREJESTROWANIE GOŚCIA");
        try {
            String personalId = readNonEmptyString("PESEL / ID osobiste: ");
            guestManager.unregisterGuest(personalId);
            System.out.println("Gość został pomyślnie wyrejestrowany!");
        } catch (IllegalArgumentException e) {
            System.err.println("Wyrejestrowanie gościa zostało przerwane!");
        } catch (RuntimeException e) {
            System.err.println("Błąd: " + e.getMessage());
        }
    }

    private void activateGuest() {
        displayTitle("AKTYWACJA GOŚCIA");
        try {
            String personalId = readNonEmptyString("PESEL / ID osobiste: ");
            guestManager.activateGuest(personalId);
            System.out.println("Gość został pomyślnie aktywowany!");
        } catch (IllegalArgumentException e) {
            System.err.println("Aktywacja gościa została przerwana!");
        } catch (RuntimeException e) {
            System.err.println("Błąd: " + e.getMessage());
        }
    }

    private void displayAllGuests() {
        displayTitle("INFORMACJE O WSZYSTKICH GOŚCIACH");
        try {
            List<Guest> guests = guestManager.findAllGuests();
            if (guests.isEmpty()) {
                System.out.println("Brak zarejestrowanych gości.");
                return;
            }
            for (Guest guest : guests) {
                System.out.println(guest + "\n---");
            }
        } catch (RuntimeException e) {
            System.err.println("Błąd: " + e.getMessage());
        }
    }

    private void displayGuest() {
        displayTitle("INFORMACJE O WYBRANYM GOŚCIU");
        try {
            String personalId = readNonEmptyString("PESEL / ID osobiste: ");
            Guest guest = guestManager.findGuestByPersonalId(personalId);
            System.out.println(guest + "\n---");
        } catch (IllegalArgumentException e) {
            System.err.println("Wyświetlanie informacji o gościu zostało przerwane!");
        } catch (RuntimeException e) {
            System.err.println("Błąd: " + e.getMessage());
        }
    }

    // ==================== OPERACJE NA POKOJACH ====================

    private void handleRoomOperations() {
        while (true) {
            displayRoomOperationsMenu();

            int choice;
            try {
                choice = inputToInt();
            } catch (IllegalArgumentException e) {
                continue;
            }

            switch (choice) {
                case 1 -> registerNewRoom();
                case 2 -> unregisterRoom();
                case 3 -> activateRoom();
                case 4 -> displayAllRooms();
                case 5 -> displayRoom();
                case 0 -> {
                    return;
                }
                default -> System.out.println("Nieprawidłowy wybór!");
            }
        }
    }

    private void registerNewRoom() {
        displayTitle("REJESTRACJA NOWEGO POKOJU");

        String number;
        int bedsAmount;
        double basePrice;

        try {
            number = readNonEmptyString("Numer pokoju: ");

            System.out.print("Liczba łóżek: ");
            bedsAmount = inputToInt();
            if (bedsAmount <= 0) {
                System.err.println("Błąd: Liczba łóżek musi być większa od 0!");
                System.err.println("Rejestracja pokoju została przerwana!");
                return;
            }

            System.out.print("Cena bazowa: ");
            basePrice = inputToDouble();
            if (basePrice <= 0) {
                System.err.println("Błąd: Cena bazowa musi być większa od 0!");
                System.err.println("Rejestracja pokoju została przerwana!");
                return;
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Rejestracja pokoju została przerwana!");
            return;
        }

        try {
            roomManager.registerRoom(number, bedsAmount, basePrice);
            System.out.println("Pokój został pomyślnie zarejestrowany.");
        } catch (RuntimeException e) {
            System.err.println("Błąd: " + e.getMessage());
        }
    }

    private void unregisterRoom() {
        displayTitle("WYREJESTROWANIE POKOJU");
        try {
            String number = readNonEmptyString("Numer pokoju: ");
            roomManager.unregisterRoom(number);
            System.out.println("Pokój został pomyślnie wyrejestrowany!");
        } catch (IllegalArgumentException e) {
            System.err.println("Wyrejestrowanie pokoju zostało przerwane!");
        } catch (RuntimeException e) {
            System.err.println("Błąd: " + e.getMessage());
        }
    }

    private void activateRoom() {
        displayTitle("AKTYWACJA POKOJU");
        try {
            String number = readNonEmptyString("Numer pokoju: ");
            roomManager.activateRoom(number);
            System.out.println("Pokój został pomyślnie aktywowany!");
        } catch (IllegalArgumentException e) {
            System.err.println("Aktywacja pokoju została przerwana!");
        } catch (RuntimeException e) {
            System.err.println("Błąd: " + e.getMessage());
        }
    }

    private void displayAllRooms() {
        displayTitle("INFORMACJE O WSZYSTKICH POKOJACH");
        try {
            List<Room> rooms = roomManager.findAllRooms();
            if (rooms.isEmpty()) {
                System.out.println("Brak zarejestrowanych pokoi.");
                return;
            }
            for (Room room : rooms) {
                System.out.println(room + "\n---");
            }
        } catch (RuntimeException e) {
            System.err.println("Błąd: " + e.getMessage());
        }
    }

    private void displayRoom() {
        displayTitle("INFORMACJE O WYBRANYM POKOJU");
        try {
            String number = readNonEmptyString("Numer pokoju: ");
            Room room = roomManager.findRoomByNumber(number);
            System.out.println(room + "\n---");
        } catch (IllegalArgumentException e) {
            System.err.println("Wyświetlanie informacji o pokoju zostało przerwane!");
        } catch (RuntimeException e) {
            System.err.println("Błąd: " + e.getMessage());
        }
    }

    // ==================== OPERACJE NA WYPOŻYCZENIACH ====================

    private void handleRentOperations() {
        while (true) {
            displayRentOperationsMenu();

            int choice;
            try {
                choice = inputToInt();
            } catch (IllegalArgumentException e) {
                continue;
            }

            switch (choice) {
                case 1 -> rentRoom();
                case 2 -> returnRoom();
                case 3 -> displayAllRents();
                case 4 -> displayRent();
                case 5 -> displayAllGuestRents();
                case 0 -> {
                    return;
                }
                default -> System.out.println("Nieprawidłowy wybór!");
            }
        }
    }

    private void rentRoom() {
        displayTitle("WYPOŻYCZENIE POKOJU");

        String personalId, roomNumber;
        LocalDateTime beginTime, endTime;

        try {
            personalId = readNonEmptyString("PESEL / ID osobiste gościa: ");
            roomNumber = readNonEmptyString("Numer pokoju: ");
            beginTime = inputToDateTime("Data rozpoczęcia (yyyy-MM-dd HH:mm): ");
            endTime = inputToDateTime("Data zakończenia (yyyy-MM-dd HH:mm): ");
        } catch (IllegalArgumentException e) {
            System.err.println("Wypożyczanie pokoju zostało przerwane!");
            return;
        }

        try {
            Guest guest = guestManager.findGuestByPersonalId(personalId);
            Room room = roomManager.findRoomByNumber(roomNumber);
            Rent rent = rentManager.rentRoom(beginTime, endTime, guest, room);
            System.out.println("Pokój został pomyślnie wypożyczony! Szczegóły wypożyczenia:\n" + rent);
        } catch (RuntimeException e) {
            System.err.println("Błąd: " + e.getMessage());
        }
    }

    private void returnRoom() {
        displayTitle("ZWROT POKOJU (ZAKOŃCZENIE WYPOŻYCZENIA)");
        try {
            UUID uuid = inputToUUID("UUID wypożyczenia: ");
            rentManager.endRent(uuid);
            System.out.println("Pokój został zwrócony (wypożyczenie zakończone) pomyślnie!");
        } catch (IllegalArgumentException e) {
            System.err.println("Zwrot pokoju został przerwany!");
        } catch (RuntimeException e) {
            System.err.println("Błąd: " + e.getMessage());
        }
    }

    private void displayAllRents() {
        displayTitle("INFORMACJE O WSZYSTKICH WYPOŻYCZENIACH");
        try {
            List<Rent> rents = rentManager.findAllRents();
            if (rents.isEmpty()) {
                System.out.println("Brak zarejestrowanych wypożyczeń.");
                return;
            }
            for (Rent rent : rents) {
                System.out.println(rent + "\n---");
            }
        } catch (RuntimeException e) {
            System.err.println("Błąd: " + e.getMessage());
        }
    }

    private void displayRent() {
        displayTitle("INFORMACJE O WYBRANYM WYPOŻYCZENIU");
        try {
            UUID uuid = inputToUUID("UUID wypożyczenia: ");
            Rent rent = rentManager.findRentByUuid(uuid);
            System.out.println(rent + "\n---");
        } catch (IllegalArgumentException e) {
            System.err.println("Wyświetlanie informacji o wypożyczeniu zostało przerwane!");
        } catch (RuntimeException e) {
            System.err.println("Błąd: " + e.getMessage());
        }
    }

    private void displayAllGuestRents() {
        displayTitle("INFORMACJE O WYPOŻYCZENIACH GOŚCIA");
        try {
            String personalId = readNonEmptyString("PESEL / ID osobiste gościa: ");
            List<Rent> rents = rentManager.findAllGuestRents(personalId);
            if (rents.isEmpty()) {
                System.out.println("Brak wypożyczeń dla gościa o ID = " + personalId);
                return;
            }
            for (Rent rent : rents) {
                System.out.println(rent + "\n---");
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Wyświetlanie wypożyczeń gościa zostało przerwane!");
        } catch (RuntimeException e) {
            System.err.println("Błąd: " + e.getMessage());
        }
    }
}