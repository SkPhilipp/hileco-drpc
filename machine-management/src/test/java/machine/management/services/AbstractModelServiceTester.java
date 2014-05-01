package machine.management.services;

import com.google.common.reflect.TypeToken;
import machine.management.model.Model;
import machine.management.services.generic.AbstractModelService;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

abstract public class AbstractModelServiceTester<T extends Model> {

    /**
     * Asserts that all properties on the given expected object equal that of the actual object.
     *
     * @param expected the leading and original object
     * @param actual the object to match the expected object
     */
    abstract public void assertEquals(T expected, T actual);

    /**
     * Randomizes all randomizable properties on a given object.
     *
     * @param original an object to be randomized
     */
    abstract public void randomize(T original);

    /**
     *
     * @return the {@link AbstractModelService} implementation for {@link T}
     */
    abstract public AbstractModelService<T> getModelServiceImpl();

    @Test
    @SuppressWarnings("unchecked")
    public void testCreateReadUpdateDelete() throws Exception {
        AbstractModelService<T> abstractModelService = this.getModelServiceImpl();
        // create and save a random T
        TypeToken<T> typeToken = new TypeToken<T>(getClass()) { };
        Class<? super T> type = typeToken.getRawType();
        T original = (T) type.newInstance();
        this.randomize(original);
        abstractModelService.create(original);
        // read it out by id and assert equality
        T read = abstractModelService.read(original.getId());
        this.assertEquals(original, read);
        // update it by id, read and expect equality
        this.randomize(original);
        abstractModelService.update(original);
        T readUpdated = abstractModelService.read(original.getId());
        this.assertEquals(original, readUpdated);
        // delete it by id, read and expect null
        abstractModelService.delete(original.getId());
        T readDeleted = abstractModelService.read(original.getId());
        Assert.assertNull(readDeleted);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCreateAndQueryByExample() throws Exception {
        AbstractModelService<T> abstractModelService = this.getModelServiceImpl();
        // create a task
        TypeToken<T> typeToken = new TypeToken<T>(getClass()) { };
        Class<? super T> type = typeToken.getRawType();
        T original = (T) type.newInstance();
        this.randomize(original);
        abstractModelService.create(original);
        List<T> results = abstractModelService.query(original);
        Assert.assertEquals(results.size(), 1);
        T read = results.get(0);
        this.assertEquals(original, read);
    }

}
