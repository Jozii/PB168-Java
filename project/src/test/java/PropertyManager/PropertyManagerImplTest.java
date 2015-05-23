package PropertyManager;

import PropertyManager.common.DBUtils;
import PropertyManager.manager.Property;
import PropertyManager.manager.PropertyManagerImpl;
import PropertyManager.manager.TitleDeedManager;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;


import static org.junit.Assert.*;

public class PropertyManagerImplTest {

    private PropertyManagerImpl manager;
    private DataSource dataSource;

    private static DataSource prepareDataSource() throws SQLException {
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl("jdbc:derby:memory:propertymanagerimpltest;create=true");
        return ds;
    }
    @Before
    public void setUp() throws Exception {

        dataSource = prepareDataSource();
        DBUtils.executeSqlScript(dataSource, TitleDeedManager.class.getResourceAsStream("/createTables.sql"));
        manager = new PropertyManagerImpl(dataSource);
    }

    @After
    public void tearDown() throws SQLException {
        DBUtils.executeSqlScript(dataSource,TitleDeedManager.class.getResourceAsStream("/dropTables.sql"));
    }

    @Test
    public void testCreateProperty() throws Exception {
        Property property =
                createProperty("Nova 5", "Spissky Hrhov", new BigDecimal(3), "Rodinny dom", 350, LocalDate.of(2001, 2, 10), "Opis");
        manager.createProperty(property);
        Long id =  property.getId();
        assertNotNull(id);

        Property test = manager.getPropertyById(id);
        String town = property.getAddressTown();
        BigDecimal value = property.getPrice();
        LocalDate date = property.getDateOfBuild();


        assertEquals(town, test.getAddressTown());
        assertEquals(value, test.getPrice());
        assertEquals(date, test.getDateOfBuild());
        assertEquals(property, test);


    }

    @Test
    public void testUpdateProperty() throws Exception {
        Property property = createProperty("Nova 5", "Spissky Hrhov", new BigDecimal(3), "Rodinny dom", 350, LocalDate.of(2001, 2, 10), "Opis");
        Property test = createProperty("Kounicova", "Brno", new BigDecimal(5), "Kolej", 100, LocalDate.of(2001, 2, 10), "Opis2");
        manager.createProperty(property);
        manager.createProperty(test);
        Long id = property.getId();

        property = manager.getPropertyById(id);
        property.setAddressStreet("Francisciho");
        manager.updateProperty(property);
        assertEquals(property.getAddressStreet(), "Francisciho");

        property.setAddressTown("Levoca");
        manager.updateProperty(property);
        property = manager.getPropertyById(id);
        assertEquals(property.getAddressTown(), "Levoca");

        property.setPrice(new BigDecimal(100));
        manager.updateProperty(property);
        property = manager.getPropertyById(id);
        assertEquals(property.getPrice(), new BigDecimal(100));

        property.setType("Bytovka");
        manager.updateProperty(property);
        property = manager.getPropertyById(id);
        assertEquals(property.getType(), "Bytovka");

        property.setSquareMeters(200);
        manager.updateProperty(property);
        property = manager.getPropertyById(id);
        assertEquals(property.getSquareMeters(), 200);

        property.setDescription("Nieco");
        manager.updateProperty(property);
        property = manager.getPropertyById(id);
        assertEquals(property.getDescription(), "Nieco");

    }

    @Test
    public void testDeleteProperty() throws Exception {
        Property property = createProperty("Nova 5", "Spissky Hrhov", new BigDecimal(3), "Rodinny dom", 20 ,LocalDate.of(2001, 2, 10), "Opis");
        Property test = createProperty("Kounicova", "Brno", new BigDecimal(5), "Kolej", 100, LocalDate.of(2001, 2, 10), "Opis2");
        manager.createProperty(property);
        manager.createProperty(test);
        assertNotNull(property);
        assertNotNull(test);

        manager.deleteProperty(test.getId());

        assertNull(manager.getPropertyById(test.getId()));
        assertNotNull(property);
    }

    @Test
    public void testGetPropertyById() throws Exception {
        Property property = createProperty("Nova 5","Spissky Hrhov", new BigDecimal(3), "Rodinny dom", 350, LocalDate.of(2001, 2, 10), "Opis");
        manager.createProperty(property);
        Long id = property.getId();
        Property test = manager.getPropertyById(id);

        assertEquals(property.getId(), test.getId());
        assertEquals(property, test);
    }

    @Test
    public void testFindAllProperties() throws Exception {
        Property property = createProperty("Nova 5", "Spissky Hrhov", new BigDecimal(3), "Rodinny dom", 350, LocalDate.of(2001, 2, 10), "Opis");
        Property test = createProperty("Kounicova", "Brno", new BigDecimal(5), "Kolej", 100, LocalDate.of(2001, 2, 10), "Opis2");
        manager.createProperty(property);
        manager.createProperty(test);

        List<Property> trueList = Arrays.asList(property, test);
        List<Property> fromMethod = manager.findAllProperties();

        assertEquals(trueList, fromMethod);

    }

    private Property createProperty(String addressStreet, String addressTown, BigDecimal price, String type, int squareMeters, LocalDate dateOfBuild, String description) {
        //Calendar dateS = new GregorianCalendar(day, month, year);

        return new Property(addressStreet, addressTown, price, type, squareMeters, dateOfBuild , description);
    }

}