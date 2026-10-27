package pl.lodz.p;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.Persistence;
import jakarta.persistence.PessimisticLockException;
import pl.lodz.p.adapter.JpaGuestRepositoryAdapter;
import pl.lodz.p.adapter.JpaRentRepositoryAdapter;
import pl.lodz.p.adapter.JpaRoomRepositoryAdapter;
import pl.lodz.p.exception.ConcurrentOperationException;
import pl.lodz.p.exception.EntityNotFoundException;
import pl.lodz.p.exception.RepositoryException;
import pl.lodz.p.model.domain.*;
import pl.lodz.p.repository.JpaGuestRepository;
import pl.lodz.p.repository.JpaRentRepository;
import pl.lodz.p.repository.JpaRoomRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
//        Address address = new Address("dasdas", "oooo", "2a");
//        GuestType guestType = new GuestTypeExtra();
//        Guest guest = new Guest("oh", "myszeczko", "1234", address, guestType);
//        Room room = new Room("12", 3, 12.50);


        EntityManagerFactory emf = Persistence.createEntityManagerFactory("nbd-pu");

        JpaGuestRepository rep = new JpaGuestRepository(emf);
        JpaGuestRepositoryAdapter adapter = new JpaGuestRepositoryAdapter(rep);

        JpaRoomRepository repRoom = new JpaRoomRepository(emf);
        JpaRoomRepositoryAdapter adapterRoom = new JpaRoomRepositoryAdapter(repRoom);

        JpaRentRepository repRent = new JpaRentRepository(emf);
        JpaRentRepositoryAdapter adapterRent = new JpaRentRepositoryAdapter(repRent);

        String strBegin = "1986-04-08 12:30";
        String strEnd = "1986-04-10 12:30";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        LocalDateTime dateTimeBegin = LocalDateTime.parse(strBegin, formatter);
        LocalDateTime dateTimeEnd = LocalDateTime.parse(strEnd, formatter);


        Guest guest = adapter.findGuestByPersonalId("1234");
        Room room = adapterRoom.findRoomByNumber("12");


        CyclicBarrier barrier = new CyclicBarrier(2);
        ExecutorService executor = Executors.newFixedThreadPool(2);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger exceptionCount = new AtomicInteger(0);

        Runnable task1 = () -> {
            try {
                Rent rent1 = new Rent(dateTimeBegin, dateTimeEnd, guest, room);
                barrier.await(); // Czeka na drugi wątek, żeby wystartować idealnie równocześnie!
                adapterRent.saveRent(rent1);
                successCount.incrementAndGet();
                System.out.println("Wątek 1: SUKCES - wynajęto pokój!");
            } catch (Exception e) {
                exceptionCount.incrementAndGet();
                System.err.println("Wątek 1: BŁĄD - " + e.getMessage());

                if (e instanceof PessimisticLockException | e.getCause() instanceof PessimisticLockException) {
                    System.err.println("PESYMISTYCZNY");
                }

                if (e instanceof OptimisticLockException | e.getCause() instanceof OptimisticLockException) {
                    System.err.println("OPTYMISTYCZNY");
                }
            }
        };

        Runnable task2 = () -> {
            try {
                Rent rent2 = new Rent(dateTimeBegin, dateTimeEnd, guest, room);
                barrier.await(); // Czeka na pierwszy wątek!
                adapterRent.saveRent(rent2);
                successCount.incrementAndGet();
                System.out.println("Wątek 2: SUKCES - wynajęto pokój!");
            } catch (Exception e) {
                exceptionCount.incrementAndGet();
                System.err.println("Wątek 2: BŁĄD - " + e.getMessage());

                if (e instanceof PessimisticLockException | e.getCause() instanceof PessimisticLockException) {
                    System.err.println("PESYMISTYCZNY");
                }

                if (e instanceof OptimisticLockException | e.getCause() instanceof OptimisticLockException) {
                    System.err.println("OPTYMISTYCZNY");
                }


            }
        };

        executor.submit(task1);
        executor.submit(task2);

        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Udane rezerwacje: " + successCount.get()); // Powinno wynosić 1
        System.out.println("Odrzucone rezerwacje: " + exceptionCount.get());

//        try {
//            adapterRoom.saveRoom(room);
//        } catch (Exception e) {
//            System.err.println(e.getMessage());
//        }
//
//        try {
//            adapter.saveGuest(guest);
//        } catch (Exception e) {
//            System.err.println(e.getMessage());
//        }

//        try {
//            adapterRent.saveRent(rent);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//
//        try {
//            adapterRoom.saveRoom(room);
//        } catch (Exception e) {
//            System.err.println(e.getMessage());
//        }
//
//        try {
//            adapterRoom.setRoomArchivedStatus("12", true);
//            adapterRoom.setRoomArchivedStatus("13", true);
//        } catch (Exception e) {
//            System.err.println(e.getMessage());
//        }
//
//        try {
//            List<Room> rooms = adapterRoom.findAllRooms();
//
//            for (Room room1 : rooms) {
//                IO.println(room1);
//            }
//
//        } catch (Exception e) {
//            System.err.println(e.getMessage());
//        }

//        Guest guest = adapter.findGuestByPersonalId("2137");

//        try {
//            adapter.setGuestArchivedStatus("2138", false);
//        } catch (Exception e) {
//            System.err.println(e.getMessage());
//        }
//
//       IO.println(guest);

//        try {
//            List<Guest> guests = adapter.findAllGuests();
//
//            for (Guest guest : guests) {
//                IO.println(guest);
//            }
//
//        } catch (EntityNotFoundException | ConcurrentOperationException | RepositoryException e) {
//            System.err.println(e.getMessage());
//        }
    }
}
