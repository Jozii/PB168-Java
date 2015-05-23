package PropertyManager;

import PropertyManager.common.DBUtils;
import PropertyManager.common.IllegalEntityException;
import PropertyManager.manager.*;
import org.apache.commons.dbcp2.BasicDataSource;
import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.*;

public class TitleDeedManagerImplTest {

    private OwnerManagerImpl ownerManager;
    private PropertyManagerImpl propertyManager;
    private TitleDeedManagerImpl manager;
    private DataSource ds;
    private static DataSource prepareDataSource() throws SQLException {
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl("jdbc:derby:memory:titledeedmanagerimpltest;create=true");
        return ds;
    }

    @Before
    public void setUp() throws SQLException {
        ds = prepareDataSource();
        DBUtils.executeSqlScript(ds, TitleDeedManager.class.getResourceAsStream("/createTables.sql"));
        ownerManager = new OwnerManagerImpl(ds);
        propertyManager = new PropertyManagerImpl(ds);
        manager = new TitleDeedManagerImpl(ds);
    }

    @After
    public void tearDown() throws SQLException {
        DBUtils.executeSqlScript(ds,TitleDeedManager.class.getResourceAsStream("/dropTables.sql"));
    }
    @Test
    public void createTitleDeed() {
        Owner owner = createOwner("Peter", "Novak", LocalDate.of(1985,12,15), "+421948456789","Vladimirova 4", "Trencin");
        Property property = createProperty("Klusackova 50", "Brno", new BigDecimal(3000000), "byt", 100, LocalDate.of(2006, 10, 10), "");
        ownerManager.createOwner(owner);
        propertyManager.createProperty(property);
        Long ownerId =owner.getId();
        Long propertyId = property.getId();
        TitleDeed titleDeed = createTitleDeed(ownerId, propertyId, LocalDate.of(2008, 10, 8), LocalDate.of(1000, 1, 1));
        manager.createTitleDeed(titleDeed);
        Long id = titleDeed.getId();
        TitleDeed returnedTitleDeed = manager.getTitleDeedById(id);
        assertNotNull(returnedTitleDeed);
        Long returnedId = returnedTitleDeed.getId();
        assertEquals(id,returnedId);
        assertEquals(titleDeed,returnedTitleDeed);
        assertNotSame(titleDeed,returnedTitleDeed);
    }

    @Test
    public void createTitleDeedWithWrongAttributes() {
        try {
            manager.createTitleDeed(null);
            fail();
        }catch (IllegalArgumentException ex) {
            // Everything is ok
        }
        Owner owner = createOwner("Peter", "Novak", LocalDate.of(1985,12,15), "+421948456789","Vladimirova 4", "Trencin");
        Property property = createProperty("Klusackova 50", "Brno", new BigDecimal(3000000), "byt", 100, LocalDate.of(2005, 3, 5), "");
        ownerManager.createOwner(owner);
        propertyManager.createProperty(property);
        Long ownerId = owner.getId();
        Long propertyId = property.getId();
        TitleDeed titleDeed = createTitleDeed(ownerId, propertyId, LocalDate.of(2008,10,8), LocalDate.of(1000, 1, 1));
        titleDeed.setId(1L);
        try {
            manager.createTitleDeed(titleDeed);
            fail();
        }catch (IllegalEntityException ex) {
            // ok
        }
    }

    @Test
    public void findPropertiesForOwnerTest() {
        Owner owner = createOwner("Peter", "Novak", LocalDate.of(1985,12,15), "+421948456789","Vladimirova 4", "Trencin");
        Property property = createProperty("Klusackova 50", "Brno", new BigDecimal(3000000), "byt", 100, LocalDate.of(2005,3,5), "");
        ownerManager.createOwner(owner);
        propertyManager.createProperty(property);
        Long ownerId = owner.getId();
        Long propertyId = property.getId();
        TitleDeed titleDeed = createTitleDeed(ownerId, propertyId, LocalDate.of(2008,10,25), LocalDate.of(1000, 1, 1));
        List<Property> list1 = new ArrayList<>();
        List<Property> list2;
        list2 = manager.findPropertiesForOwner(ownerId);
        assertTrue(list2.isEmpty());
        list2.clear();

        manager.createTitleDeed(titleDeed);
        list1.add(property);
        list2 = manager.findPropertiesForOwner(ownerId);
        assertEquals(list1,list2);
        assertFalse(list2.isEmpty());

        Owner owner2 = createOwner("Ivan","Novak",LocalDate.of(1975,02,02),"+420948123456","Klusackova", "Brno");
        Property property2 = createProperty("SNP 15", "Praha", new BigDecimal(4000000), "byt", 120,LocalDate.of(2003,3,5), "byt je komletne vybaveny");
        ownerManager.createOwner(owner2);
        propertyManager.createProperty(property2);
        Long owner2Id = owner2.getId();
        Long property2Id = property2.getId();
        TitleDeed titleDeed2 = createTitleDeed(owner2Id,property2Id,LocalDate.of(2010,12,12),LocalDate.of(1000, 1, 1));
        manager.createTitleDeed(titleDeed2);
        list2 = manager.findPropertiesForOwner(ownerId);
        assertEquals(list1,list2);
        assertFalse(list2.isEmpty());
        list2.clear();

        Property property3 = createProperty("Klusackova 1", "Brno", new BigDecimal(300000), "byt", 25, LocalDate.of(1987,6,5), "po rekonstrukcii");
        propertyManager.createProperty(property3);
        Long property3Id = property3.getId();
        TitleDeed titleDeed3 = createTitleDeed(ownerId,property3Id,LocalDate.of(2013, 5, 5),LocalDate.of(1000, 1, 1));
        manager.createTitleDeed(titleDeed3);
        list2 = manager.findPropertiesForOwner(ownerId);
        list1.add(property3);
        list1.sort(propertyComparator);
        list2.sort(propertyComparator);
        assertEquals(list1,list2);
        assertFalse(list2.isEmpty());
    }

    @Test
    public void findPropertiesForOwnerWithWrongAttributesTest() {
        try {
            manager.findPropertiesForOwner(null);
            fail();
        }catch (IllegalArgumentException ex) {
            // ok
        }
    }

    @Test
    public void findOwnersForPropertyTest() {
        Owner owner = createOwner("Peter", "Novak", LocalDate.of(1985,12,15), "+421948456789","Vladimirova 4", "Trencin");
        Property property = createProperty("Klusackova 50", "Brno", new BigDecimal(3000000), "byt", 100, LocalDate.of(2005,3,5) , "");
        ownerManager.createOwner(owner);
        propertyManager.createProperty(property);
        Long ownerId = owner.getId();
        Long propertyId = property.getId();
        TitleDeed titleDeed = createTitleDeed(ownerId, propertyId, LocalDate.of(2010,10,25), LocalDate.of(1000, 1, 1));
        List<Owner> list1 = new ArrayList<>();
        List<Owner> list2;
        list2 = manager.findOwnersForProperty(propertyId);
        assertTrue(list2.isEmpty());
        list2.clear();

        manager.createTitleDeed(titleDeed);
        list1.add(owner);
        list2 = manager.findOwnersForProperty(propertyId);
        assertEquals(list1,list2);
        assertFalse(list2.isEmpty());

        Owner owner2 = createOwner("Ivan","Novak",LocalDate.of(1975,02,02),"+420948123456","Klusackova", "Brno");
        Property property2 = createProperty("SNP 15", "Praha", new BigDecimal(4000000), "byt", 120, LocalDate.of(2003,3,4), "byt je komletne vybaveny");
        ownerManager.createOwner(owner2);
        propertyManager.createProperty(property2);
        Long owner2Id = owner2.getId();
        Long property2Id = property2.getId();
        TitleDeed titleDeed2 = createTitleDeed(owner2Id,property2Id,LocalDate.of(2010,12,12),LocalDate.of(1000, 1, 1));
        manager.createTitleDeed(titleDeed2);
        list2 = manager.findOwnersForProperty(property2Id);
        list1.clear();
        list1.add(owner2);
        assertEquals(list1, list2);
        assertFalse(list2.isEmpty());
        list1.clear();
        list2.clear();

        Owner owner3 = createOwner("John","Smith",LocalDate.of(1979,3,15),"+1917123456789","Wall Street", "New York");
        ownerManager.createOwner(owner3);
        Long owner3Id = owner3.getId();
        TitleDeed titleDeed3 = createTitleDeed(owner3Id,propertyId,LocalDate.of(2013,5,5),LocalDate.of(1000, 1, 1));
        manager.createTitleDeed(titleDeed3);
        list2 = manager.findOwnersForProperty(propertyId);
        list1.add(owner);
        list1.add(owner3);
        list1.sort(ownerComparator);
        list2.sort(ownerComparator);
        assertEquals(list1,list2);
        assertFalse(list2.isEmpty());
    }


    @Test
    public void findOwnersForPropertyWithWrongAttributesTest() {
        try {
            manager.findOwnersForProperty(null);
            fail();
        }catch (IllegalArgumentException ex) {
            // ok
        }
    }

    @Test
    public void testDeleteTitleDeed() throws Exception {   //my work
        Owner owner1 = createOwner("Filip", "Kafka", LocalDate.of(2014, 6, 30), "0973154863", "Kukucinova 5","Kocurkovo");
        Property property = createProperty("Nova 5", "Levoca", new BigDecimal(5), "Kolej", 20 ,LocalDate.of(2014, 6, 30), "Opis2");
        ownerManager.createOwner(owner1);
        propertyManager.createProperty(property);
        Long ownerId = owner1.getId();
        Long propertyId = property.getId();
        TitleDeed deed = createTitleDeed(ownerId, propertyId, LocalDate.of(1999, 8, 1), LocalDate.of(2006, 1, 1));
        TitleDeed test = createTitleDeed(ownerId, propertyId, LocalDate.of(1993, 7, 5), LocalDate.of(2010, 5, 5));
        manager.createTitleDeed(deed);
        manager.createTitleDeed(test);
        Long tId = test.getId();
        assertNotNull(deed);
        assertNotNull(test);

        manager.deleteTitleDeed(test);


        assertNull(manager.getTitleDeedById(tId));
        assertNotNull(deed);
    }

    @Test
    public void testGetTitleDeedById() throws Exception {  // my work
        Owner owner1 = createOwner("Filip", "Kafka", LocalDate.of(2014, 6, 30), "0973154863", "Kukucinova 5","Kocurkovo");
        Property property = createProperty("Nova 5", "Levoca", new BigDecimal(5), "Kolej", 100, LocalDate.of(2014, 6, 30), "Opis2");
        ownerManager.createOwner(owner1);
        propertyManager.createProperty(property);
        Long ownerId = owner1.getId();
        Long propertyId = property.getId();
        TitleDeed deed = createTitleDeed(ownerId, propertyId, LocalDate.of(1993, 7, 5), LocalDate.of(2006, 7, 5));

        manager.createTitleDeed(deed);
        Long tId = deed.getId();
        TitleDeed test = manager.getTitleDeedById(tId);

        assertEquals(deed.getId(), test.getId());
        assertEquals(deed, test);
    }

    @Test
    public void testFindAllTitleDeed() throws Exception {  // my work
        Owner owner1 = createOwner("Filip", "Kafka", LocalDate.of(2014, 6, 30), "0973154863", "Kukucinova 5","Kocurkovo");
        Property property = createProperty("Nova 5", "Levoca", new BigDecimal(5), "Kolej", 100, LocalDate.of(2014, 6, 30), "Opis2");
        ownerManager.createOwner(owner1);
        propertyManager.createProperty(property);
        Long ownerId = owner1.getId();
        Long propertyId = property.getId();
        TitleDeed deed = createTitleDeed(ownerId, propertyId, LocalDate.of(1993, 7, 5), LocalDate.of(2006, 7, 5));
        TitleDeed test = createTitleDeed(ownerId, propertyId, LocalDate.of(1990, 7, 5), LocalDate.of(2010, 1, 5));

        manager.createTitleDeed(deed);
        manager.createTitleDeed(test);

        List<TitleDeed> trueList = Arrays.asList(deed, test);
        List<TitleDeed> fromMethod = manager.findAllTitleDeed();

        assertEquals(trueList, fromMethod);
    }

    @Test
    public void testFindAllTitleDeedForOwner() throws Exception { // my work
        Owner owner1 = createOwner("Filip", "Kafka", LocalDate.of(2014, 6, 30), "0973154863", "Kukucinova 5","Kocurkovo");
        Owner owner2 = createOwner("Mikulas", "Ludep", LocalDate.of(2014, 6, 30), "095315488", "Farna 8","Lasickovo");
        Property property = createProperty("Nova 5", "Levoca", new BigDecimal(5), "Kolej", 100, LocalDate.of(2014, 6, 30), "Opis2");
        ownerManager.createOwner(owner1);
        ownerManager.createOwner(owner2);
        propertyManager.createProperty(property);
        Long owner1Id = owner1.getId();
        Long propertyId = property.getId();
        Long owner2Id = owner2.getId();
        TitleDeed deed = createTitleDeed(owner1Id, propertyId, LocalDate.of(1990, 1, 5), LocalDate.of(2001, 4, 7));
        TitleDeed test = createTitleDeed(owner1Id, propertyId, LocalDate.of(1991, 2, 5), LocalDate.of(2004, 6, 5));
        TitleDeed test2 = createTitleDeed(owner2Id, propertyId, LocalDate.of(1993, 3, 5), LocalDate.of(2007, 8, 1));

        manager.createTitleDeed(deed);
        manager.createTitleDeed(test);
        manager.createTitleDeed(test2);

        List<TitleDeed> trueList = Arrays.asList(deed, test);
        List<TitleDeed> fromMethod = manager.findAllTitleDeedForOwner(owner1Id);

        assertEquals(trueList, fromMethod);
    }

    @Test
    public void testFindAllTitleDeedForProperty() throws Exception { // my work
        Owner owner1 = createOwner("Filip", "Kafka", LocalDate.of(2014, 6, 30), "0973154863", "Kukucinova 5","Kocurkovo");
        Property property = createProperty("Nova 5", "Levoca", new BigDecimal(5), "Kolej", 30 ,LocalDate.of(2014, 6, 30), "Opis2");
        Property property2 = createProperty("Milackova", "LFilakovo", new BigDecimal(222), "Bytovka", 30 ,LocalDate.of(2014, 6, 30), "Opis3");
        ownerManager.createOwner(owner1);
        propertyManager.createProperty(property);
        propertyManager.createProperty(property2);
        Long owner1Id = owner1.getId();
        Long propertyId = property.getId();
        Long property2Id = property2.getId();
        TitleDeed deed = createTitleDeed(owner1Id, propertyId, LocalDate.of(1993, 7, 5), LocalDate.of(2001, 2, 10));
        TitleDeed test = createTitleDeed(owner1Id, propertyId, LocalDate.of(2002, 2, 1), LocalDate.of(2001, 2, 10));
        TitleDeed test2 = createTitleDeed(owner1Id, property2Id, LocalDate.of(1894, 5, 10), LocalDate.of(2001, 2, 10));

        manager.createTitleDeed(deed);
        manager.createTitleDeed(test);
        manager.createTitleDeed(test2);

        Long id = property.getId();

        List<TitleDeed> trueList = Arrays.asList(deed, test);
        List<TitleDeed> fromMethod = manager.findAllTitleDeedForProperty(id);

        assertEquals(trueList, fromMethod);

    }

    private TitleDeed createTitleDeed(Long owner, Long property, LocalDate startDate,
                                      LocalDate endDate) {
        return new TitleDeed(owner,property,startDate,endDate);
    }

    private Owner createOwner(String name, String surname, LocalDate bornDate, String phone, String street, String town) {
        return new Owner(name, surname, bornDate, phone, street, town);
    }

    private Property createProperty(String addressStreet, String addressTown, BigDecimal price, String type, int squareMeters,
                                    LocalDate dateOfBuild, String description) {

        return new Property(addressStreet,addressTown,price,type,squareMeters,dateOfBuild,description);
    }

    private static Comparator<Owner> ownerComparator = new Comparator<Owner>() {

        @Override
        public int compare(Owner o1, Owner o2) {
            return Long.valueOf(o1.getId()).compareTo(Long.valueOf(o2.getId()));
        }
    };

    public static Comparator<Property> propertyComparator = new Comparator<Property>() {
        @Override
        public int compare(Property o1, Property o2) {
            return Long.valueOf(o1.getId()).compareTo(Long.valueOf(o2.getId()));
        }
    };
}