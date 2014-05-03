package machine.management.services.helpers;

import com.google.common.reflect.TypeToken;
import machine.management.services.lib.dao.Model;
import machine.management.services.lib.services.AbstractModelService;
import machine.management.services.lib.services.AbstractQueryableModelService;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

abstract public class AbstractQueryableModelServiceTester<T extends Model> extends AbstractModelServiceTester<T> {

    /**
     * @return the {@link AbstractQueryableModelService} implementation for {@link T}
     */
    abstract public AbstractQueryableModelService<T> getQueryableModelServiceImpl();

    public AbstractModelService<T> getModelServiceImpl() {
        return this.getQueryableModelServiceImpl();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCreateAndQueryByExample() throws Exception {
        AbstractQueryableModelService<T> abstractModelService = this.getQueryableModelServiceImpl();
        // create a task
        TypeToken<T> typeToken = new TypeToken<T>(getClass()) {
        };
        Class<? super T> type = typeToken.getRawType();
        T original = (T) type.newInstance();
        this.randomizeModel(original);
        abstractModelService.create(original);
        List<T> results = abstractModelService.query(original);
        Assert.assertEquals(results.size(), 1);
        T read = results.get(0);
        this.assertEquals(original, read);
    }

}
