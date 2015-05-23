package PropertyManager.manager;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import PropertyManager.common.IllegalEntityException;
import PropertyManager.common.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;


/**
 * Created by Jozef Živčic on 10. 3. 2015.
 */
public class TitleDeedManagerImpl implements TitleDeedManager {
    final static Logger log = LoggerFactory.getLogger(TitleDeedManagerImpl.class);

    private final JdbcTemplate jdbc;

    public TitleDeedManagerImpl(DataSource dataSource) {
        this.jdbc = new JdbcTemplate(dataSource);
    }

    public static final RowMapper<TitleDeed> titleDeedMapper = (rs, rowNum)->
            new TitleDeed(rs.getLong("id"),rs.getLong("ownerId"), rs.getLong("propertyId"),rs.getDate("startDate").toLocalDate(),rs.getDate("endDate").toLocalDate());
    public static final RowMapper<Property> propertyMapper = (rs, rowNum)->
            new Property(rs.getLong("id"), rs.getString("street"), rs.getString("town"), rs.getBigDecimal("price"), rs.getString("typeOf"), rs.getInt("square"), rs.getDate("dateOfBuild").toLocalDate(), rs.getString("description"));
    public static final RowMapper<Owner> ownerMapper = (rs, rowNum)->
            new Owner(rs.getLong("id"), rs.getString("name"), rs.getString("surname"), rs.getDate("born").toLocalDate(), rs.getString("phoneNumber"), rs.getString("addressStreet"), rs.getString("addressTown"));
    @Override
    public void createTitleDeed(TitleDeed titleDeed) {
        log.debug("creating title deed " + titleDeed);
        validate(titleDeed);
        if (titleDeed.getId() != null) throw new IllegalEntityException("Id is already setted " + titleDeed);

        Map<String,Object> map = new HashMap<>();
        map.put("ownerId", titleDeed.getOwner());
        map.put("propertyId", titleDeed.getProperty());
        map.put("startDate", Date.valueOf(titleDeed.getStartDate()));
        if (titleDeed.getEndDate() != null)
            map.put("endDate", Date.valueOf(titleDeed.getEndDate()));
        else
            map.put("endDate", null);
        Long id = new SimpleJdbcInsert(jdbc).withTableName("titledeed").usingGeneratedKeyColumns("id").executeAndReturnKey(map).longValue();
        titleDeed.setId(id);
    }

    @Override
    public void updateTitleDeed(TitleDeed titleDeed) {
        log.debug("updating title deed " + titleDeed);
        validate(titleDeed);
        if (titleDeed.getId() == null) throw new IllegalEntityException("title deed with null id " + titleDeed);
        int n = jdbc.update("UPDATE titledeed SET ownerId=?, propertyId=?, startDate=?, endDate=?",
                titleDeed.getOwner(), titleDeed.getProperty(), Date.valueOf(titleDeed.getStartDate()),Date.valueOf(titleDeed.getEndDate()));
        if (n != 1) throw new IllegalEntityException("title deed " + titleDeed + " not updated");

    }

    @Override
    public void deleteTitleDeed(TitleDeed titleDeed) {
        log.debug("deleteTitleDeed({})", titleDeed);
        if (titleDeed == null) throw new IllegalArgumentException("titleDeed is null");
        if (titleDeed.getId() == null) throw new IllegalEntityException("titleDeed id is null");
        int n = jdbc.update("DELETE FROM titledeed WHERE id = ?", titleDeed.getId());
        if (n != 1) throw new IllegalEntityException("titleDeed " + titleDeed + " not deleted");
    }

    @Override
    public TitleDeed getTitleDeedById(Long id) {
        log.debug("getTitleDeed({})", id);
        if (id == null) throw new IllegalArgumentException("id is null");
        List<TitleDeed> list = jdbc.query("SELECT id, ownerId, propertyId, startDate, endDate  FROM titledeed WHERE id = ?", titleDeedMapper , id);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<TitleDeed> findAllTitleDeed() {
        log.debug("findAllTitleDeed()");
        return jdbc.query("SELECT id, ownerId, propertyId, startDate, endDate FROM TitleDeed", titleDeedMapper);
    }

    @Override
    public List<TitleDeed> findAllTitleDeedForOwner(Long owner) {
        log.debug("findAllTitleDeedForOwner({})", owner);
        if (owner == null) throw new IllegalEntityException("owner id is null");
        return jdbc.query(
                "SELECT id, ownerId, propertyId, startDate, endDate " +
                        "FROM TitleDeed  " +
                        "WHERE ownerId = ?", titleDeedMapper, owner);

    }

    @Override
    public List<TitleDeed> findAllTitleDeedForProperty(Long property) {
        log.debug("findAllTitleDeedForProperty({})", property);
        if (property == null) throw new IllegalEntityException("property id is null");
        return jdbc.query(
                "SELECT id, ownerId, propertyId, startDate, endDate " +
                        "FROM TitleDeed " +
                        "WHERE propertyId = ?", titleDeedMapper, property);

    }

    @Override
    public List<Property> findPropertiesForOwner(Long owner) {
        log.debug("finding properties for owner with id " + owner);
        if (owner == null) throw new IllegalArgumentException("owner id is null ");
        List<Property> properties = jdbc.query("SELECT property.id, street,town, price, typeOf, square, dateOfBuild, description" +
                " FROM owner JOIN titledeed ON owner.id = titledeed.ownerId JOIN property on property.id = titledeed.propertyId" +
                " WHERE owner.id=?", propertyMapper, owner);
        return properties;
    }

    @Override
    public List<Owner> findOwnersForProperty(Long property) {
        log.debug("finding owners for properties with id " + property);
        if (property == null) throw new IllegalArgumentException("property is null " + property);
        List<Owner> owners = jdbc.query("SELECT owner.id, name, surname, born, phoneNumber, addressStreet, addressTown" +
                " FROM owner JOIN titledeed ON owner.id = titledeed.ownerId JOIN property on property.id = titledeed.propertyId" +
                " WHERE property.id=?", ownerMapper,property);
        return owners;
    }

    private void validate(TitleDeed titleDeed) {
        if (titleDeed == null) throw new IllegalArgumentException("title deed is null " + titleDeed);
        if (titleDeed.getOwner() == null) throw new ValidationException("owner is null " + titleDeed);
        if (titleDeed.getProperty() == null) throw new ValidationException("property is null " + titleDeed);
        if (titleDeed.getStartDate() == null) throw new ValidationException("start date is null " + titleDeed);
        //if (titleDeed.getEndDate() == null) throw new ValidationException("end date is null " + titleDeed);
    }
}
