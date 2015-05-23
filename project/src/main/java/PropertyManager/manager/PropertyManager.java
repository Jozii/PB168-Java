package PropertyManager.manager;

import java.util.List;

/**
 * Created by Jozef Živčic on 10. 3. 2015.
 */
public interface PropertyManager {
    void createProperty(Property property);
    void updateProperty(Property property);
    void deleteProperty(Long propertyId);
    Property getPropertyById(Long id);
    List<Property> findAllProperties();
    List<Property> findAllPropertiesByTown(String town);
}
