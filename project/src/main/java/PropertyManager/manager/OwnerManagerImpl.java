package PropertyManager.manager;

import PropertyManager.common.ServiceFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jozef Živčic on 10. 3. 2015.
 */
public class OwnerManagerImpl implements OwnerManager {
    private final DataSource dataSource;
    private final static Logger log = LoggerFactory.getLogger(OwnerManagerImpl.class);

    public OwnerManagerImpl(DataSource ds) {
        dataSource = ds;
    }

    public void createOwner(Owner owner) {
        if (owner == null) throw new IllegalArgumentException("Owner is null");
        if (owner.getId() != null) throw new IllegalArgumentException("Id is not null");
        try(Connection connection = dataSource.getConnection()) {
            try(PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO owner " +
                    "(name, surname,born,phoneNumber,addressStreet,addressTown) VALUES " +
                    "(?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1,owner.getName());
                preparedStatement.setString(2, owner.getSurname());
                preparedStatement.setDate(3,Date.valueOf(owner.getBorn()));
                preparedStatement.setString(4, owner.getPhoneNumber());
                preparedStatement.setString(5,owner.getAddressStreet());
                preparedStatement.setString(6,owner.getAddressTown());
                if (preparedStatement.executeUpdate() != 1)
                    throw new ServiceFailureException("Internal Error: More rows  were added when trying to insert one owner" + owner);
                ResultSet rs = preparedStatement.getGeneratedKeys();
                owner.setId(getKey(rs,owner));
            }
        } catch (SQLException ex) {
            log.error("db connection problem", ex);
            throw new ServiceFailureException("An error occured when trying to get all owners", ex);
        }
    }
    private Long getKey(ResultSet resultSet, Owner owner) throws ServiceFailureException, SQLException {
        if (resultSet.next()) {
            if (resultSet.getMetaData().getColumnCount() != 1) {
                throw new ServiceFailureException("Internal Error: Generating of key"
                        + "failed when trying to insert owner " + owner
                        + " - wrong key fields count: " + resultSet.getMetaData().getColumnCount());
            }
            Long result = resultSet.getLong(1);
            if (resultSet.next()) {
                throw new ServiceFailureException("Internal Error: Generating of key"
                        + "failed when trying to insert owner " + owner
                        + " - more keys found");
            }
            return result;
        } else {
            throw new ServiceFailureException("Internal Error: Generating of key"
                    + "failed when trying to insert owner " + owner
                    + " - no key was found");
        }
    }
    @Override
    public void updateOwner(Owner owner) {
        if (owner == null) throw new IllegalArgumentException("Owner is null" + owner);
        if (owner.getId() == null) throw new IllegalArgumentException("Owner " + owner + " has id null");
        if (owner.getName() == null) throw new IllegalArgumentException("Owner " + owner +  "has name null");
        if (owner.getSurname() == null) throw new IllegalArgumentException("Owner " + owner +  "has surname null");
        if (owner.getBorn() == null) throw new IllegalArgumentException("Owner " + owner +  "has date of birth null");
        if (owner.getPhoneNumber() == null) throw new IllegalArgumentException("Owner " + owner +  "has phone number null");
        if (owner.getAddressStreet() == null) throw new IllegalArgumentException("Owner " + owner +  "has street null");
        if (owner.getAddressTown() == null) throw new IllegalArgumentException("Owner " + owner +  "has town null");
        try(Connection connection = dataSource.getConnection()) {
            try(PreparedStatement preparedStatement = connection.prepareStatement("UPDATE OWNER SET " +
                    "NAME=?, SURNAME=?, BORN=?, PHONENUMBER=?, ADDRESSSTREET=?, ADDRESSTOWN=? where ID=?")){
                preparedStatement.setString(1,owner.getName());
                preparedStatement.setString(2, owner.getSurname());
                preparedStatement.setDate(3,Date.valueOf(owner.getBorn()));
                preparedStatement.setString(4, owner.getPhoneNumber());
                preparedStatement.setString(5,owner.getAddressStreet());
                preparedStatement.setString(6,owner.getAddressTown());
                preparedStatement.setLong(7,owner.getId());
                if (preparedStatement.executeUpdate() != 1)
                    throw new IllegalArgumentException("Cannot update owner" + owner);
            }
        } catch (SQLException e) {
            log.error("DB connection error ", e);
            throw new ServiceFailureException("Error when retrieving all owners", e);
        }
    }

    @Override
    public void deleteOwner(Owner owner) {
        if (owner == null) throw new IllegalArgumentException("Owner is null" + owner);
        if (owner.getId() == null) throw new IllegalArgumentException("Owner " + owner + " has id null");
        try(Connection connection = dataSource.getConnection()) {
            try(PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM OWNER WHERE id=?")){
                preparedStatement.setLong(1,owner.getId());
                if (preparedStatement.executeUpdate() != 1)
                    throw new IllegalArgumentException("Cannot update owner" + owner);
            }
        }catch (SQLIntegrityConstraintViolationException ex){
            log.error("Cannot delete owner while he has title deed ");
        }
        catch (SQLException e) {
            log.error("DB connection error ", e);
            throw new ServiceFailureException("Error when retrieving all owners", e);
        }
    }

    @Override
    public Owner getOwnerById(Long id) {
        if (id == null)
            throw new IllegalArgumentException("Id is null");
        try(Connection connection = dataSource.getConnection()) {
            try(PreparedStatement preparedStatement = connection.prepareStatement("SELECT ID, NAME, SURNAME, BORN, PHONENUMBER, ADDRESSSTREET, ADDRESSTOWN FROM owner WHERE id = ?"))
            {
                preparedStatement.setLong(1,id);
                ResultSet rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    Owner owner = resultSetToOwner(rs);
                    if (rs.next()) {
                        throw new ServiceFailureException("Internal error: More owners were found with id: " + id +
                        " owner: " + owner + " and " + resultSetToOwner(rs));
                    }
                    return owner;
                }
            }
        } catch (SQLException e) {
            log.error("DB connection error");
            throw new ServiceFailureException("Internal error when retrieving all owners");
        }
        return null;
    }

    @Override
    public List<Owner> findAllOwners() {
        List<Owner> list = new ArrayList<>();
        try(Connection connection = dataSource.getConnection()) {
            try(PreparedStatement preparedStatement = connection.prepareStatement("SELECT ID, NAME, SURNAME, BORN, PHONENUMBER, ADDRESSSTREET, ADDRESSTOWN FROM owner"))
            {
                ResultSet resultSet = preparedStatement.executeQuery();
                while(resultSet.next())
                    list.add(resultSetToOwner(resultSet));
            }
        } catch (SQLException e) {
            log.error("DB connection error");
            throw new ServiceFailureException("Internal error when retrieving all owners");
        }
        return list;
    }

    @Override
    public List<Owner> findOwnerBySurname(String surname) {
        List<Owner> list = new ArrayList<Owner>();
        try(Connection connection = dataSource.getConnection()) {
            try(PreparedStatement preparedStatement = connection.prepareStatement("SELECT ID, NAME, SURNAME, BORN, PHONENUMBER, ADDRESSSTREET, ADDRESSTOWN FROM owner WHERE LOCATE(?,SURNAME)<>0"))
            {
                preparedStatement.setString(1, surname);
                ResultSet resultSet = preparedStatement.executeQuery();
                while(resultSet.next())
                    list.add(resultSetToOwner(resultSet));
            }
        } catch (SQLException e) {
            log.error("DB connection error");
            throw new ServiceFailureException("Internal error when retrieving owners by surname");
        }
        return list;
    }
    
    private Owner resultSetToOwner(ResultSet resultSet) throws SQLException {
        Owner owner = new Owner();
        owner.setId(resultSet.getLong("id"));
        owner.setName(resultSet.getString("name"));
        owner.setSurname(resultSet.getString("surname"));
        owner.setBorn(resultSet.getDate("born").toLocalDate());
        owner.setPhoneNumber(resultSet.getString("phoneNumber"));
        owner.setAddressStreet(resultSet.getString("addressStreet"));
        owner.setAddressTown(resultSet.getString("addressTown"));
        return owner;
    }
}
