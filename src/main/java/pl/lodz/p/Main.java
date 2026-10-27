package pl.lodz.p;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.Persistence;
import jakarta.persistence.PessimisticLockException;
import pl.lodz.p.adapter.JpaGuestRepositoryAdapter;
import pl.lodz.p.adapter.JpaRentRepositoryAdapter;
import pl.lodz.p.adapter.JpaRoomRepositoryAdapter;
import pl.lodz.p.adapter.mapper.RentMapper;
import pl.lodz.p.adapter.mapper.RoomMapper;
import pl.lodz.p.exception.ConcurrentOperationException;
import pl.lodz.p.exception.EntityNotFoundException;
import pl.lodz.p.exception.RepositoryException;
import pl.lodz.p.manager.GuestManager;
import pl.lodz.p.manager.RentManager;
import pl.lodz.p.manager.RoomManager;
import pl.lodz.p.model.domain.*;
import pl.lodz.p.repository.JpaGuestRepository;
import pl.lodz.p.repository.JpaRentRepository;
import pl.lodz.p.repository.JpaRoomRepository;
import pl.lodz.p.view.ConsoleInterface;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("nbd-pu");
        GuestManager guestManager = new GuestManager(emf);
        RoomManager roomManager = new RoomManager(emf);
        RentManager rentManager = new RentManager(emf);

        ConsoleInterface consoleInterface = new ConsoleInterface(guestManager, roomManager, rentManager);

        consoleInterface.run();
    }
}
