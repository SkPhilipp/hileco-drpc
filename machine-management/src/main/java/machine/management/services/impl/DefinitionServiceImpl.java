package machine.management.services.impl;

import machine.management.model.Definition;
import machine.management.services.DefinitionService;

import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/definitions")
public class DefinitionServiceImpl extends AbstractModelService<Definition> implements DefinitionService {

    @GET
    @Path("/all")
    @Consumes(MediaType.APPLICATION_JSON)
    @SuppressWarnings("unchecked")
    @Override
    public List<Definition> findAll(String bottableFilter, String typeFilter) {
        Query query = this.getEntityManager().createNativeQuery("SELECT * FROM Definition");
        return query.getResultList();
    }

}
