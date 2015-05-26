package PropertyManager.manager;


import PropertyManager.common.ServiceFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import javax.activation.DataSource;
import javax.sql.DataSource;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;


/**
 * Created by Jozef Živčic on 10. 3. 2015.
 */
public class PropertyManagerImpl implements PropertyManager {

    private static Logger log = LoggerFactory.getLogger(PropertyManagerImpl.class);

    private final DataSource dataSource;

    public PropertyManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void createProperty(Property property) {
        //log.debug("Create property ({})", property);
        //log.trace("Property is = {} ", property.getId()  );

        if (property == null) {
            throw new IllegalArgumentException("Property is null.");
        }
        if (property.getId() != null) {
            throw new IllegalArgumentException("ID is already set.");
        }
        if (property.getAddressStreet() == null) {
            throw new IllegalArgumentException("Adress not setted!!");
        }
        if (property.getAddressTown() == null) {
            throw new IllegalArgumentException("Town not setted!!");
        }
        if (property.getPrice().signum() <= 0) {
            throw new IllegalArgumentException("Price cannot be negative or zero!");
        }
        if (property.getSquareMeters() <= 0) {
            throw new IllegalArgumentException("SquareMeters cannot be negative or zero");
        }
        if (property.getDateOfBuild() == null) {
            throw new IllegalArgumentException("Date must be setted");
        }

        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("INSERT INTO property (street,town,price,typeOf,square,dateofBuild,description) VALUES (?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
                st.setString(1, property.getAddressStreet());
                st.setString(2, property.getAddressTown());
                st.setBigDecimal(3, property.getPrice());
                st.setString(4, property.getType());
                st.setInt(5, property.getSquareMeters());
                st.setDate(6, Date.valueOf(property.getDateOfBuild()));
                st.setString(7, property.getDescription());
                int addedRows = st.executeUpdate();
                if (addedRows != 1) {
                    throw new ServiceFailureException("Internal Error: More rows inserted! " + property);
                }
                ResultSet keyRS = st.getGeneratedKeys();
                property.setId(getKey(keyRS, property));
            }
        } catch (SQLException ex) {
            log.error("connection problem", ex);
            throw new ServiceFailureException("Error when retrieving all properties", ex);
        }
    }

    private Long getKey(ResultSet keyRS, Property property) throws ServiceFailureException, SQLException {
        if (keyRS.next()) {
            if (keyRS.getMetaData().getColumnCount() != 1) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retriving failed when trying to insert property " + property
                        + " - wrong key fields count: " + keyRS.getMetaData().getColumnCount());
            }
            Long result = keyRS.getLong(1);
            if (keyRS.next()) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retriving failed when trying to insert property " + property
                        + " - more keys found");
            }
            return result;
        } else {
            throw new ServiceFailureException("Internal Error: Generated key"
                    + "retriving failed when trying to insert property " + property
                    + " - no key found");
        }
    }

    @Override
    public void updateProperty(Property property) {
        if (property == null) throw new IllegalArgumentException("property pointer is null");
        if (property.getId() == null) throw new IllegalArgumentException("grave with null id cannot be updated");
        if (property.getAddressStreet() == null) throw new IllegalArgumentException("Adress cannot be null");
        if (property.getAddressTown() == null) throw new IllegalArgumentException("Town cannot be null");
        if (property.getPrice().signum() <= 0) throw new IllegalArgumentException("Price cannot be negative or zero");
        if (property.getSquareMeters() <= 0)
            throw new IllegalArgumentException("SquareMeters cannot be negative or zero");
        if (property.getDateOfBuild() == null) throw new IllegalArgumentException("Date cannot be null");

        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("UPDATE PROPERTY SET street=?,town=?,price=?,typeOf=?,square=?,dateOfBuild=?,description=? WHERE id=?")) {
                st.setString(1, property.getAddressStreet());
                st.setString(2, property.getAddressTown());
                st.setBigDecimal(3, property.getPrice());
                st.setString(4, property.getType());
                st.setInt(5, property.getSquareMeters());
                st.setDate(6, Date.valueOf(property.getDateOfBuild()));
                st.setString(7, property.getDescription());
                st.setLong(8, property.getId());
                if (st.executeUpdate() != 1) {
                    throw new IllegalArgumentException("cannot update property " + property);
                }
            }
        } catch (SQLException ex) {
            log.error("connection problem", ex);
            throw new ServiceFailureException("Error when retrieving all properties", ex);
        }


    }

    @Override
    public void deleteProperty(Long propertyID) {
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("DELETE FROM property WHERE id=?")) {
                st.setLong(1, propertyID);
                if (st.executeUpdate() != 1) {
                    throw new ServiceFailureException("did not delete property with id =" + propertyID);
                }
            }
        }catch (SQLIntegrityConstraintViolationException ex){
            log.error("Cannot delete property with title deed ");
            throw new ServiceFailureException("Cannot delete owner with titleDeed", ex);
        }catch (SQLException ex) {
            log.error("connection problem", ex);
            throw new ServiceFailureException("Error when retrieving all properties", ex);
        }

    }

    @Override
    public Property getPropertyById(Long id) {
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("SELECT id,street,town,price,typeOf,square,dateofBuild,description FROM PROPERTY WHERE id = ?")) {
                st.setLong(1, id);
                ResultSet rs = st.executeQuery();
                if (rs.next()) {
                    Property property = resultSetToProperty(rs);
                    if (rs.next()) {
                        throw new ServiceFailureException(
                                "Internal error: More entities cannot have same id "
                                        + "(source id: " + id + ", found " + property + " and " + resultSetToProperty(rs));
                    }
                    return property;
                } else {
                    return null;
                }
            }
        } catch (SQLException ex) {
            log.error("db connection problem", ex);
            throw new ServiceFailureException("Error when retrieving all properties", ex);
        }

    }

    @Override
    public List<Property> findAllProperties() {
        log.debug("finding all properties");
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("SELECT id,street,town,price,typeOf,square,dateofBuild,description FROM PROPERTY")) {
                ResultSet rs = st.executeQuery();
                List<Property> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(resultSetToProperty(rs));
                }
                return result;
            }
        } catch (SQLException ex) {
            log.error("db connection problem", ex);
            throw new ServiceFailureException("Error when retrieving all properties", ex);
        }
    }

    @Override
    public List<Property> findAllPropertiesByTown(String town) {
        log.debug("finding all properties by town");
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("SELECT id,street,town,price,typeOf,square,dateofBuild,description FROM PROPERTY WHERE LOCATE(?,TOWN) <> 0")) {
                st.setString(1, town);
                ResultSet rs = st.executeQuery();
                List<Property> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(resultSetToProperty(rs));
                }
                return result;
            }
        } catch (SQLException ex) {
            log.error("db connection problem", ex);
            throw new ServiceFailureException("Error when retrieving all properties", ex);
        }
    }
    
    private Property resultSetToProperty(ResultSet rs) throws SQLException {
        Property property = new Property();
        property.setId(rs.getLong("id"));
        property.setAddressStreet(rs.getString("street"));
        property.setAddressTown(rs.getString("town"));
        property.setPrice(rs.getBigDecimal("price"));
        property.setType(rs.getString("typeOf"));
        property.setSquareMeters(rs.getInt("square"));
        property.setDateOfBuild((rs.getDate("dateOfBuild")).toLocalDate());
        property.setDescription(rs.getString("description"));

        return property;
    }

    public static Calendar DateToCalendar(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

}