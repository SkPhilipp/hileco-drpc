package machine.management.services;

import machine.management.model.Definition;

import java.util.List;

public interface DefinitionService extends ModelService<Definition> {

    /**
     * Lists a filtered set of definitions.
     *
     * @param bottableFilter filter to use for the {@link Definition#bottable} property
     * @param typeFilter filter to use for the {@link Definition#type} property
     * @return all matching definitions
     */
    public List<Definition > findAll(String bottableFilter, String typeFilter);

}
