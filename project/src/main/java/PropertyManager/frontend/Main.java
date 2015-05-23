package PropertyManager.frontend;

import PropertyManager.common.DBUtils;
import PropertyManager.manager.*;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.sql.DataSource;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

/**
 * Created by Jozef Živčic on 5. 5. 2015.
 */
public class Main {

    private final static Logger log = LoggerFactory.getLogger(Main.class);

    private JPanel panel1;
    private JTabbedPane tabbedPane1;
    private JTable table1;
    private JTable table2;
    private JTable table3;
    private PropertyTableModel modelProperties;
    private JButton vytvoriťButton = new JButton();
    private JButton smazatButton = new JButton();
    private JButton upravitButton1 = new JButton();
    private JButton vytvoriťButton3 = new JButton();
    private JButton smazatButton3 = new JButton();
    private JButton upravitButton = new JButton();
    private JButton vytvoriťButton2 = new JButton();
    private JButton smazatButton1 = new JButton();
    private JButton upravitButton3 = new JButton();

    public Main() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("Texts");
        OwnerManager ownerManager;
        PropertyManager propertyManager;
        TitleDeedManager titleDeedManager;
        DataSource ds = null;

        try {
            ds = prepareDataSource();
        } catch (SQLException e) {
            log.error("DB connection error " + e);
            JOptionPane.showMessageDialog(null, resourceBundle.getString("db-error"));
            System.exit(1);
        } catch (IOException e) {
            log.error("IO excption " + e);
            JOptionPane.showMessageDialog(null, resourceBundle.getString("error-loading-resources"));
            System.exit(1);
        }
        try {
            try{
                DBUtils.executeSqlScript(ds,Main.class.getResourceAsStream("/dropTables.sql"));
            }catch (SQLException e){}
            DBUtils.executeSqlScript(ds, Main.class.getResourceAsStream("/createTables.sql"));
        } catch (SQLException e) {
            System.out.println("1");
            log.error("DB Connnection error " + e);
            JOptionPane.showMessageDialog(null, resourceBundle.getString("db-error"));
            System.exit(1);
        }

        ownerManager = new OwnerManagerImpl(ds);
        propertyManager = new PropertyManagerImpl(ds);
        titleDeedManager = new TitleDeedManagerImpl(ds);
        Owner o = new Owner("a","a", LocalDate.of(2000,12,12),"+451","Ulica","Mesto");
        ownerManager.createOwner(o);
        Long id = o.getId();
        o = ownerManager.getOwnerById(id);
        JOptionPane.showMessageDialog(null, o.toString());
        vytvoriťButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,"Hello");
            }
        });
        JFrame frame = new JFrame(resourceBundle.getString("title"));
        //frame.setContentPane(panel1);
        //panel1.add(vytvoriťButton2);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        try {
            DBUtils.executeSqlScript(ds,Main.class.getResourceAsStream("/dropTables.sql"));
        } catch (SQLException e) {
            log.error("DB Connnection error " + e);
            JOptionPane.showMessageDialog(null, resourceBundle.getString("db-error"));
            System.exit(1);
        }

    }

    public PropertyTableModel getModelProperties() {
        return modelProperties;
    }


    public static void main(String[] args) {
        new Main();
    }

    private void setModelsToTables()
    {
        modelProperties = new PropertyTableModel();
        table1.setModel(modelProperties);
    }


    private void createUIComponents() {
        table1 = new JTable(new PropertyTableModel());
        table1.setPreferredScrollableViewportSize(new Dimension(500, 70));
        table2 = new JTable(new OwnerTableModel());
        table2.setPreferredScrollableViewportSize(new Dimension(500, 70));
        table3 = new JTable(new TitleDeedTableModel());
        table3.setPreferredScrollableViewportSize(new Dimension(500, 70));

    }
    private static DataSource prepareDataSource() throws SQLException, IOException {
        BasicDataSource ds = new BasicDataSource();
        Properties myconf = new Properties();
        myconf.load(Main.class.getResourceAsStream("/conf.properties"));
        ds.setUrl(myconf.getProperty("jdbc.url"));
        ds.setUsername(myconf.getProperty("jdbc.user"));
        ds.setPassword(myconf.getProperty("jdbc.password"));
        return ds;
    }


}
