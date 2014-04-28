package machine.management.services;

import machine.management.model.Model;

import java.util.UUID;

public interface ModelService<T extends Model> {

    public UUID create(T instance);

    public T read(UUID id);

    public void update(UUID id, T instance);

    public void delete(UUID id);

}
