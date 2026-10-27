package pl.lodz.p.model.db;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@MappedSuperclass
public class AbstractEntity {

    @Version
    @Column(name = "VERSION")
    private long version;

    @NotNull
    @Column(name = "ENTITY_UUID", unique = true, updatable = false)
    private UUID uuid;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

}
