package PropertyManager.frontend;

import PropertyManager.manager.*;
import java.math.BigInteger;

import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Notebook on 6. 5. 2015.
 */
public class TitleDeedTableModel extends AbstractTableModel {
    final static Logger log = LoggerFactory.getLogger(TitleDeedTableModel.class);
    //private static OwnerManager ownerManager = AppCommons.getOwnerManager();
    //private static PropertyManager propertyManager = AppCommons.getPropertyManager();

    private List<TitleDeed> deeds = new ArrayList<TitleDeed>();

    @Override
    public int getRowCount() {
        return deeds.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }
    
    public TitleDeed getTitleDeed(int index) {
        return deeds.get(index);
    }

    @Override
    public String getColumnName(int columnIndex) {

        ResourceBundle rb = ResourceBundle.getBundle("texts");
        switch (columnIndex) {
            case 0:
                return rb.getString("owner");
            case 1:
                return rb.getString("property");
            case 2:
                return rb.getString("start-date");
            case 3:
                return rb.getString("end-date");
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        TitleDeed deed = deeds.get(rowIndex);
        switch (columnIndex) {
            case 0:
                //return ownerManager.getOwnerById(deed.getOwner()).getName();
                return deed.getOwner();
            case 1:
                //return propertyManager.getPropertyById(deed.getProperty()).getAddressStreet();
                return deed.getProperty();
            case 2:
                return deed.getStartDate();
            case 3:
                return deed.getEndDate();
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch(columnIndex) {
            case 0:
            case 1:
                return Long.class;
            case 2:
            case 3:
                return LocalDate.class;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
        
    }
    
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        TitleDeed deed = deeds.get(rowIndex);
        switch(columnIndex) {
            case 0:
                deed.setOwner((Long) aValue);
                break;
            case 1:
                deed.setProperty((Long) aValue);
                break;
            case 2:
                deed.setStartDate((LocalDate)aValue);
                break;
            case 3:
                deed.setEndDate((LocalDate)aValue);
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
    
    public void updateTitleDeed(TitleDeed titledeed, int rowIndex) {
        deeds.set(rowIndex, titledeed);
        fireTableRowsUpdated(rowIndex, rowIndex);
    }
    
    public void addTitleDeed(TitleDeed titledeed) {
        deeds.add(titledeed);
        int lastRow = deeds.size() - 1;
        fireTableRowsInserted(lastRow, lastRow);
    }
    
    public void deleteTitleDeed(int rowIndex) {
        deeds.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }
    
    public void deleteTitleDeeds(int[] selectedRows) {
        Integer[] rowsToDelete = AppCommons.getSortedDesc(selectedRows);
        for(int i : rowsToDelete) {
            deleteTitleDeed(i);
        }
    }
    
    public void setTitleDeeds(List<TitleDeed> titleDeedsToAdd) {
        deeds = titleDeedsToAdd;
        fireTableDataChanged();
    }
}