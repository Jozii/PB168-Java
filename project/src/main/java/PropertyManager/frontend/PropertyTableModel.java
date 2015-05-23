package PropertyManager.frontend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import PropertyManager.manager.Property;
import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

/**
 * 
 */
public class PropertyTableModel extends AbstractTableModel{

    final static Logger log = LoggerFactory.getLogger(PropertyTableModel.class);

    private List<Property> properties = new ArrayList<Property>();

    @Override
    public int getRowCount() {
        return properties.size();
    }

    @Override
    public int getColumnCount() {
        return 7;
    }
    
    public Property getProperty(int index) {
        return properties.get(index);
    }

    @Override
    public String getColumnName(int columnIndex) {

        ResourceBundle rb = ResourceBundle.getBundle("texts");
        switch (columnIndex) {
            case 0:
                return rb.getString("street");
            case 1:
                return rb.getString("town");
            case 2:
                return rb.getString("price");
            case 3:
                return rb.getString("type");
            case 4:
                return rb.getString("square-meter");
            case 5:
                return rb.getString("date-of-build");
            case 6:
                return rb.getString("description");
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Property property = properties.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return property.getAddressStreet();
            case 1:
                return property.getAddressTown();
            case 2:
                return property.getPrice();
            case 3:
                return property.getType();
            case 4:
                return property.getSquareMeters();
            case 5:
                return property.getDateOfBuild();
            case 6:
                return property.getDescription();
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return String.class;
            case 1:
                return String.class;
            case 2:
                return BigDecimal.class;
            case 3:
                return String.class;
            case 4:
                return Integer.class;
            case 5:
                return LocalDate.class;
            case 6:
                return String.class;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }
    
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Property property = properties.get(rowIndex);
        switch (columnIndex) {
            case 0:
                property.setAddressStreet((String) aValue);
                break;
            case 1:
                property.setAddressTown((String) aValue);
                break;
            case 2:
                property.setPrice((BigDecimal) aValue);
                break;
            case 3:
                property.setType((String) aValue);
                break;
            case 4:
                property.setSquareMeters((Integer) aValue);
                break;
            case 5:
                property.setDateOfBuild((LocalDate) aValue);
                break;
            case 6:
                property.setDescription((String) aValue);
                break;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
        fireTableCellUpdated(rowIndex, columnIndex);
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
            case 6:
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    public void addProperty(Property input) {
        properties.add(input);
        int lastRow = properties.size() - 1;

        fireTableRowsInserted(lastRow, lastRow);
    }
    
    public void updateProperty(Property property, int rowIndex) {
        properties.set(rowIndex, property);
        fireTableRowsUpdated(rowIndex, rowIndex);
    }
    
    public void deleteProperty(int rowIndex) {
        properties.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }
    
    public void deleteProperties(int[] selectedRows) {
        Integer[] rowsToDelete = AppCommons.getSortedDesc(selectedRows);
        for(int i : rowsToDelete) {
            deleteProperty(i);
        }
    }
    
    public void setProperties(List<Property> propertiesToAdd) {
        properties = propertiesToAdd;
        fireTableDataChanged();
    }

    /*public void removeAll()
    {
        this.properties.clear();
    }*/

    /*public List<Property> getAll()
    {
        return this.properties;
    }*/

    /*public void removeAt(int index)
    {
        this.properties.remove(index);

        fireTableRowsDeleted(index, index);
    }*/

    /*public void removeAt(int[] indexes)
    {
        if(indexes.length > 0)
        {
            log.info("Property: " + properties.size());
            log.info("Selected: " + indexes.length);

            for(Integer i =indexes.length-1;i>=0;i--){
                properties.remove(indexes[i]);
            }

            int firstRow, lastRow;
            if(indexes.length == 1)
                firstRow = lastRow = indexes[0];
            else
            {
                firstRow = indexes[0];
                lastRow = indexes[indexes.length-1];
            }

            fireTableRowsDeleted(firstRow, lastRow);
        }
    }*/

    /*public void editProperty(Property oldP, Property newP)
    {
        if(properties.remove(oldP))
        {
            properties.add(newP);
            fireTableDataChanged();
        }
    }

    public Property getPropertyAt(int index)
    {
        return properties.get(index);
    }/*

    /*@Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Property property = properties.get(rowIndex);
        switch (columnIndex) {
            case 0:
                property.setAddressStreet((String) aValue);
                break;
            case 1:
                property.setAddressTown((String) aValue);
                break;
            case 2:
                property.setPrice((BigDecimal) aValue);
                break;
            case 3:
                property.setType((String) aValue);
                break;
            case 4:
                property.setSquareMeters((Integer) aValue);
                break;
            case 5:
                property.setDateOfBuild((LocalDate) aValue);
                break;
            case 6:
                property.setDescription((String) aValue);
                break;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }*/

   /* @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                return true;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }*/

}
