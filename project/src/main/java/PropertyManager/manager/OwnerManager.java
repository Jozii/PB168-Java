package PropertyManager.manager;



import java.util.List;

/**
 * Created by Jozef Živčic on 10. 3. 2015.
 */
public interface OwnerManager {
    void createOwner(Owner owner);
    void updateOwner(Owner owner);
    void deleteOwner(Owner owner);
    Owner getOwnerById(Long id);
    List<Owner> findAllOwners();
    List<Owner> findOwnerBySurname(String surname);
}
