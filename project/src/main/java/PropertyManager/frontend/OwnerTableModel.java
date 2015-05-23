package PropertyManager.frontend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import PropertyManager.manager.Owner;
import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Notebook on 6. 5. 2015.
 */
public class OwnerTableModel extends AbstractTableModel {
    
    final static Logger log = LoggerFactory.getLogger(OwnerTableModel.class);
    
    private List<Owner> owners = new ArrayList<Owner>();

    @Override
    public int getRowCount() {
        return owners.size();
    }

    @Override
    public int getColumnCount() {
        return 6;
    }
    
    public Owner getOwner(int index) {
        return owners.get(index);
    }
    
    @Override
    public String getColumnName(int columnIndex) {

        ResourceBundle rb = ResourceBundle.getBundle("texts");
        switch (columnIndex) {
            case 0:
                return rb.getString("name");
            case 1:
                return rb.getString("surname");
            case 2:
                return rb.getString("born");
            case 3:
                return rb.getString("phone-number");
            case 4:
                return rb.getString("street");
            case 5:
                return rb.getString("town");
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Owner owner = owners.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return owner.getName();
            case 1:
                return owner.getSurname();
            case 2:
                return owner.getBorn();
            case 3:
                return owner.getPhoneNumber();
            case 4:
                return owner.getAddressStreet();
            case 5:
                return owner.getAddressTown();
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch(columnIndex) {
            case 0:
            case 1:
            case 3:
            case 4:
            case 5:
                return String.class;
            case 2:
                return LocalDate.class;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
        
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Owner owner = owners.get(rowIndex);
        switch(columnIndex) {
            case 0:
                owner.setName((String) aValue);
                break;
            case 1:
                owner.setSurname((String)aValue);
                break;
            case 2:
                owner.setBorn((LocalDate)aValue);
                break;
            case 3:
                owner.setPhoneNumber((String)aValue);
                break;
            case 4:
                owner.setAddressStreet((String)aValue);
                break;
            case 5:
                owner.setAddressTown((String)aValue);
                break;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        switch(columnIndex) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                return false;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }
    
    public void updateOwner(Owner owner, int rowIndex) {
        owners.set(rowIndex, owner);
        fireTableRowsUpdated(rowIndex, rowIndex);
    }
    
    public void addOwner(Owner owner) {
        owners.add(owner);
        int lastRow = owners.size() - 1;
        fireTableRowsInserted(lastRow, lastRow);
    }
    
    public void deleteOwner(int rowIndex) {
        owners.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }
    
    public void deleteOwners(int[] selectedRows) {
        Integer[] rowsToDelete = AppCommons.getSortedDesc(selectedRows);
        for(int i : rowsToDelete) {
            deleteOwner(i);
        }
    }
    
    public void setOwners(List<Owner> ownersToAdd) {
        owners = ownersToAdd;
        fireTableDataChanged();
    }
}
