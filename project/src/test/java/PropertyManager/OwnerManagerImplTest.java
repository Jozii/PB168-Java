package PropertyManager;

import PropertyManager.common.DBUtils;
import PropertyManager.manager.Owner;
import PropertyManager.manager.OwnerManagerImpl;
import PropertyManager.manager.TitleDeedManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.sql.DataSource;

import static org.junit.Assert.*;

/**
 * Created by Jozef Živčic on 12. 3. 2015.
 */
public class OwnerManagerImplTest {

    private OwnerManagerImpl manager;
    private DataSource ds;
    private final static Logger log = LoggerFactory.getLogger(OwnerManagerImplTest.class);

    private static DataSource prepareDataSource() throws SQLException {
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl("jdbc:derby:memory:ownermanagerimpltest;create=true");
        return ds;
    }
    @Before
    public void setUp() throws Exception {
        ds = prepareDataSource();
        DBUtils.executeSqlScript(ds, TitleDeedManager.class.getResourceAsStream("/createTables.sql"));
        manager = new OwnerManagerImpl(ds);
    }

    @After
    public void tearDown() throws SQLException {
        DBUtils.executeSqlScript(ds,TitleDeedManager.class.getResourceAsStream("/dropTables.sql"));
    }

    @Test
    public void createOwner() {
        Owner owner = createOwner("Peter","Novak",25,12,1985,"+421948456789","Vladimirova 4", "Trencin");
        manager.createOwner(owner);
        Long id = owner.getId();
        Owner returnedOwner = manager.getOwnerById(id);
        assertNotNull(returnedOwner);
        Long returnedId = returnedOwner.getId();
        assertNotNull(returnedId);
        assertEquals(id, returnedId);
        assertEquals(owner, returnedOwner);
        assertNotSame(owner, returnedOwner);
    }

    @Test
    public void getOwner() {
        Owner owner = createOwner("Peter","Novak",25,12,1985,"+421948456789","Vladimirova 4", "Trencin");
        manager.createOwner(owner);
        assertEquals(manager.findAllOwners().size(), 1);
        Long id = owner.getId();
        Owner returnedOwner = manager.getOwnerById(id);
        assertEquals(owner,returnedOwner);
        assertNotSame(owner,returnedOwner);
        assertDeepEquals(owner,returnedOwner);
        Owner owner2 = createOwner("Ivan","Novak",02,02,1975,"+420948123456","Klusackova", "Brno");
        manager.createOwner(owner2);
        assertEquals(manager.findAllOwners().size(),2);
    }

    @Test
    public void getAllOwners() {
        assertTrue(manager.findAllOwners().isEmpty());
        Owner owner1 = createOwner("Peter","Novak",25,12,1985,"+421948456789","Vladimirova 4", "Trencin");
        Owner owner2 = createOwner("Ivan","Novak",02,02,1975,"+420948123456","Klusackova", "Brno");
        Owner owner3 = createOwner("John","Smith",15,03,1979,"+1917123456789","Wall Street", "New York");
        manager.createOwner(owner1);
        manager.createOwner(owner2);
        manager.createOwner(owner3);
        assertEquals(manager.findAllOwners().size(),3);
        List<Owner> list1 = new ArrayList<>();
        List<Owner> list2;
        list1.add(owner1);
        list1.add(owner2);
        list1.add(owner3);
        list2 = manager.findAllOwners();
        list1.sort(ownerComparator);
        list2.sort(ownerComparator);
        assertEquals(list1,list2);
    }

    @Test
    public void addOwnerWithWrongAttributes() {
        try {
            manager.createOwner(null);
            fail();
        }catch (IllegalArgumentException ex) {
            // Everything is ok
        }
        Owner owner = createOwner("Peter","Novak",25,12,1985,"+421948456789","Vladimirova 4", "Trencin");
        owner.setId(1L);
        try {
            manager.createOwner(owner);
            fail();
        }catch (IllegalArgumentException ex) {
            // ok
        }
    }
    @Test
    public void updateOwner() {
        Owner owner1 = createOwner("Ivan","Novak",02,02,1975,"+420948123456","Klusackova", "Brno");
        Owner owner2 = null;
        manager.createOwner(owner1);
        Long id = owner1.getId();

        owner2 = manager.getOwnerById(id);
        owner2.setName("Pavol");
        manager.updateOwner(owner2);
        owner1 = manager.getOwnerById(id);
        assertDeepEquals(owner1,owner2);

        owner2.setSurname("Pekny");
        manager.updateOwner(owner2);
        owner1 = manager.getOwnerById(id);
        assertDeepEquals(owner1,owner2);

        owner2.setPhoneNumber("+123456");
        manager.updateOwner(owner2);
        owner1 = manager.getOwnerById(id);
        assertDeepEquals(owner1,owner2);

        owner2.setAddressStreet("Street");
        manager.updateOwner(owner2);
        owner1 = manager.getOwnerById(id);
        assertDeepEquals(owner1,owner2);

        owner2.setAddressTown("Praha");
        manager.updateOwner(owner2);
        owner1 = manager.getOwnerById(id);
        assertDeepEquals(owner1,owner2);

        LocalDate localDate = LocalDate.of(1990,5,9);
        owner2.setBorn(localDate);
        manager.updateOwner(owner2);
        owner1 = manager.getOwnerById(id);
        assertDeepEquals(owner1,owner2);
    }

    @Test
    public void updateOwnerWithWrongAttributes() {
        Owner owner = createOwner("Ivan","Novak",02,02,1975,"+420948123456","Klusackova", "Brno");
        manager.createOwner(owner);
        Long ownerId = owner.getId();
        try {
            manager.updateOwner(null);
            fail();
        }catch(IllegalArgumentException ex) {
            // ok
        }

        try {
            Owner tempOwner = manager.getOwnerById(ownerId);
            tempOwner.setId(null);
            manager.updateOwner(tempOwner);
            fail();
        }catch (IllegalArgumentException ex) {
            // ok
        }

        try {
            Owner tempOwner = manager.getOwnerById(ownerId);
            tempOwner.setId(ownerId -1);
            manager.updateOwner(tempOwner);
            fail();
        }catch (IllegalArgumentException ex) {
            // ok
        }

        try {
            Owner tempOwner = manager.getOwnerById(ownerId);
            tempOwner.setName(null);
            manager.updateOwner(tempOwner);
            fail();
        }catch (IllegalArgumentException ex) {
            // ok
        }

        try {
            Owner tempOwner = manager.getOwnerById(ownerId);
            tempOwner.setSurname(null);
            manager.updateOwner(tempOwner);
            fail();
        }catch (IllegalArgumentException ex) {
            // ok
        }

        try {
            Owner tempOwner = manager.getOwnerById(ownerId);
            tempOwner.setAddressStreet(null);
            manager.updateOwner(tempOwner);
            fail();
        }catch (IllegalArgumentException ex) {
            // ok
        }

        try {
            Owner tempOwner = manager.getOwnerById(ownerId);
            tempOwner.setAddressTown(null);
            manager.updateOwner(tempOwner);
            fail();
        }catch (IllegalArgumentException ex) {
            // ok
        }

        try {
            Owner tempOwner = manager.getOwnerById(ownerId);
            tempOwner.setBorn(null);
            manager.updateOwner(tempOwner);
            fail();
        }catch (IllegalArgumentException ex) {
            // ok
        }
        /*
        try {
            Owner tempOwner = manager.getOwnerById(ownerId);
            Calendar c = Calendar.getInstance();
            c.clear();
            c.add(Calendar.YEAR,-18);
            c.add(Calendar.DAY_OF_MONTH,1);
            tempOwner.setBorn(c);
            manager.updateOwner(tempOwner);
            fail();
        }catch (IllegalArgumentException ex) {
            // ok
        }
        */
    }

    @Test
    public void deleteOwner() {
        Owner owner1 = createOwner("Ivan","Novak",02,02,1975,"+420948123456","Klusackova", "Brno");
        Owner owner2 = createOwner("John","Smith",15,03,1979,"+1917123456789","Wall Street", "New York");

        manager.createOwner(owner1);
        manager.createOwner(owner2);

        Long id1 = owner1.getId();
        Long id2 = owner2.getId();


        assertNotNull(manager.getOwnerById(id1));
        assertNotNull(manager.getOwnerById(id2));

        manager.deleteOwner(owner1);

        assertNull(manager.getOwnerById(id1));
        assertNotNull(manager.getOwnerById(id2));
    }

    @Test
    public void deleteOwnerWithWrongAttributes() {
        Owner owner = createOwner("Ivan","Novak",02,02,1975,"+420948123456","Klusackova", "Brno");

        try {
            manager.deleteOwner(null);
            fail();
        }catch(IllegalArgumentException ex) {
            // ok
        }

        try {
            owner.setId(null);
            manager.deleteOwner(owner);
            fail();
        }catch (IllegalArgumentException ex) {
            // ok
        }

        try {
            owner.setId(null);
            manager.deleteOwner(owner);
            fail();
        }catch (IllegalArgumentException ex) {
            // ok
        }

        try {
            owner.setId(1L);
            manager.deleteOwner(owner);
            fail();
        }catch (IllegalArgumentException ex) {
            // ok
        }
    }
    
    @Test
    public void testSearchBySurname() {
        Owner owner1 = createOwner("Ivan","Novak",02,02,1975,"+420948123456","Klusackova", "Brno");
        Owner owner2 = createOwner("John","Smith",15,03,1979,"+1917123456789","Wall Street", "New York");
        Owner owner3 = createOwner("John","Novotny",15,03,1979,"+1917123456789","SNP 10", "Bratislava");
        manager.createOwner(owner1);
        manager.createOwner(owner2);
        manager.createOwner(owner3);
        
        assertTrue(manager.findOwnerBySurname("ahoj").isEmpty());
        List<Owner> list1 = new ArrayList<>();
        list1.add(owner1);
        List<Owner> list2 = new ArrayList<>();
        list2 = manager.findOwnerBySurname("Nova");
        assertEquals(list1, list2);
    }
    private void assertDeepEquals(Owner owner1, Owner owner2) {
        assertEquals(owner1.getId(),owner2.getId());
        assertEquals(owner1.getName(),owner2.getName());
        assertEquals(owner1.getSurname(), owner2.getSurname());
        assertEquals(owner1.getBorn(),owner2.getBorn());
        assertEquals(owner1.getPhoneNumber(),owner2.getPhoneNumber());
        assertEquals(owner1.getAddressStreet(),owner2.getAddressStreet());
        assertEquals(owner1.getAddressTown(), owner2.getAddressTown());
    }

    private Owner createOwner(String name, String surname, int day, int month, int year, String phone, String street, String town) {
        LocalDate localDate = LocalDate.of(year,month,day);
        return new Owner(name, surname, localDate, phone, street, town);
    }
    private static Comparator<Owner> ownerComparator = new Comparator<Owner>() {

        @Override
        public int compare(Owner o1, Owner o2) {
            return Long.valueOf(o1.getId()).compareTo(Long.valueOf(o2.getId()));
        }
    };
}
