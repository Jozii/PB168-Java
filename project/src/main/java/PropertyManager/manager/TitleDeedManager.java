package PropertyManager.manager;

import java.util.List;

/**
 * Created by Jozef Živčic on 10. 3. 2015.
 */
public interface TitleDeedManager {
    void createTitleDeed(TitleDeed titleDeed);//ja
    void updateTitleDeed(TitleDeed titleDeed);//ja
    void deleteTitleDeed(TitleDeed titleDeed);
    TitleDeed getTitleDeedById(Long id);
    List<TitleDeed> findAllTitleDeed();
    List<TitleDeed> findAllTitleDeedForOwner(Long owner);
    List<TitleDeed> findAllTitleDeedForProperty(Long property);
    List<Property> findPropertiesForOwner(Long owner);//ja
    List<Owner> findOwnersForProperty(Long property);//ja
}
