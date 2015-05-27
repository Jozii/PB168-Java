/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PropertyManager.frontend;

import PropertyManager.common.DBUtils;
import PropertyManager.common.DataLabelFormater;
import PropertyManager.common.ServiceFailureException;
import PropertyManager.common.SpringConfig;
import PropertyManager.manager.*;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.sql.DataSource;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import org.apache.commons.dbcp2.BasicDataSource;
import org.jdatepicker.JDatePicker;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.security.x509.X500Name;

/**
 * @author jozef
 */
public class MainFrame extends javax.swing.JFrame {

    private final static Logger log = LoggerFactory.getLogger(MainFrame.class);
    private static DataSource ds = AppCommons.getDataSource();
    private static OwnerManager ownerManager = AppCommons.getOwnerManager();
    private static PropertyManager propertyManager = AppCommons.getPropertyManager();
    private static TitleDeedManager titleDeedManager = AppCommons.getTitleDeedManager();
    private OwnerTableModel ownerModel;
    private PropertyTableModel propertyModel;
    private TitleDeedTableModel titleDeedModel;
    private List<JComboBox> comboBoxOwners = new ArrayList<>();
    private List<JComboBox> comboBoxProperties = new ArrayList<>();
    private FindAllOwnersWorker findAllOwnersWorker;
    private FindAllPropertiesWorker findAllPropertiesWorker;
    private FindAllTitleDeedsWorker findAllTitleDeedsWorker;
    private ResourceBundle rb = ResourceBundle.getBundle("texts");
    private JDatePickerImpl fromDatePicker;
    private JDatePickerImpl toDatePicker;
    private DefaultComboBoxModel ownersComboBoxModel = new DefaultComboBoxModel();
    private DefaultComboBoxModel propertiesComboBoxModel = new DefaultComboBoxModel();
    private OwnersComboWorker ownersComboWorker;
    private PropertiesComboWorker propertiesComboWorker;
    private FindAllTitleDeedsForOwnerWorker findAllTitleDeedsForOwnerWorker;
    private FindAllTitleDeedsForPropertyWorker findAllTitleDeedsForPropertyWorker;
    
    public OwnerTableModel getOwnerModel() {
        return ownerModel;
    }

    public PropertyTableModel getPropertyModel() {
        return propertyModel;
    }

    public TitleDeedTableModel getTitleDeeedModel() {
        return titleDeedModel;
    }

    public JTable getJTableOwners() {
        return JTableOwners;
    }

    public JTable getJTableProperty() {
        return JTableProperty;
    }

    public JTable getJTableTitleDeed() {
        return JTableTitleDeed;
    }
    
    
    

    public JDatePickerImpl setDatePickerStart() {
        UtilDateModel model = new UtilDateModel();
        model.setDate(1980, 01, 01);
        Properties p = new Properties();
        p.put("text.today", rb.getString("day"));
        p.put("text.month", rb.getString("month"));
        p.put("text.year", rb.getString("year"));
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DataLabelFormater());
        return datePicker;
    }

    public JDatePickerImpl setDatePickerEnd() {
        UtilDateModel model = new UtilDateModel();
        model.setDate(1980, 01, 01);
        Properties p = new Properties();
        p.put("text.today", rb.getString("day"));
        p.put("text.month", rb.getString("month"));
        p.put("text.year", rb.getString("year"));
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DataLabelFormater());
        return datePicker;
    }

    private void initDatePickers() {
        fromDatePicker = setDateTitleDeedFromTo();
        toDatePicker = setDateTitleDeedFromTo();
        
        LocalDate localDate = LocalDate.now();        
        
        fromDatePicker.getModel().setDate(localDate.getYear(), localDate.getMonthValue() - 1,localDate.getDayOfMonth());
        toDatePicker.getModel().setDate(localDate.getYear(), localDate.getMonthValue() - 1,localDate.getDayOfMonth());
        
        fromDatePicker.setBounds(90, 340, 200, 30);
        fromDatePicker.setVisible(true);
        
        toDatePicker.setBounds(300, 340, 200, 30);
        toDatePicker.setVisible(true);
        
        fromDatePicker.getModel().setSelected(true);
        toDatePicker.getModel().setSelected(true);
        
        jPanel3.add(fromDatePicker);
        jPanel3.add(toDatePicker);
    }
    
    /**
     * ################# START SECTION #################
     * There follow classes of all BackGroundWorkers
     */
    private class FindAllOwnersWorker extends SwingWorker<List<Owner>, Integer> {

        @Override
        protected List<Owner> doInBackground() throws Exception {
            return ownerManager.findAllOwners();
    
        }

        @Override
        protected void done() {
            try{
                log.debug("Changing owner model - all owners are loaded from database.");
                ownerModel.setOwners(get());
            }catch(ExecutionException ex) {
                log.error("Exception was thrown in FindAllOwnersWorker in method doInBackGround " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("Method doInBackground has been interrupted in FindAllOwnersWorker " + ex.getCause());
                throw new RuntimeException("Operation interrupted in FindAllOwnersWorker");
            }
        }
    }
    
    private class FindAllPropertiesWorker extends SwingWorker<List<Property>, Integer> {
        
        @Override
        protected List<Property> doInBackground() throws Exception {
            return propertyManager.findAllProperties();
        }

        @Override
        protected void done() {
            try{
                log.debug("Changing property model - all properties are loaded from database.");
                propertyModel.setProperties(get());
            }catch(ExecutionException ex) {
                log.error("Exception was thrown in FindAllPropertiesWorker in method doInBackGround " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("Method doInBackground has been interrupted in FindAllPropertiesWorker " + ex.getCause());
                throw new RuntimeException("Operation interrupted in FindAllPropertiesWorker");
            }
        }
    }
    
    private class FindAllTitleDeedsWorker extends SwingWorker<List<TitleDeed>, Integer> {
        
        @Override
        protected List<TitleDeed> doInBackground() throws Exception {
            return titleDeedManager.findAllTitleDeed();
        }

        @Override
        protected void done() {
            try{
                log.debug("Changing title deed model - all title deeds are loaded from database.");
                titleDeedModel.setTitleDeeds(get());
            }catch(ExecutionException ex) {
                log.error("Exception was thrown in FindAllTitleDeedsWorker in method doInBackGround " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("Method doInBackground has been interrupted in FindAllTitleDeedsWorker " + ex.getCause());
                throw new RuntimeException("Operation interrupted in FindAllTitleDeedsWorker");
            }
        }
    }
    
    private class FindAllOwnersBySurnameWorker extends SwingWorker<List<Owner>, Integer> {

        private String surname;
        
        public FindAllOwnersBySurnameWorker(String surname) {
            this.surname = surname;
        }
        
        @Override
        protected List<Owner> doInBackground() throws Exception {
            return ownerManager.findOwnerBySurname(surname);
        }

        @Override
        protected void done() {
            try{
                log.debug("Changing owner model - owners with surname are loaded from database.");
                ownerModel.setOwners(get());
            }catch(ExecutionException ex) {
                log.error("Exception was thrown in FindAllOwnersBySurnameWorker in method doInBackGround with surname " + jTextField1.getText() + "  " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("Method doInBackground has been interrupted in FindAllOwnersBySurnameWorker " + ex.getCause());
                throw new RuntimeException("Operation interrupted in FindAllOwnersBySurnameWorker");
            }
        }
    }
    
    private class FindAllPropertiesByTownWorker extends SwingWorker<List<Property>, Integer> {
        
        private String town;

        public FindAllPropertiesByTownWorker(String town) {
            this.town = town;
        }
        
        @Override
        protected List<Property> doInBackground() throws Exception {
            return propertyManager.findAllPropertiesByTown(town);
        }

        @Override
        protected void done() {
            try{
                log.debug("Changing property model - all properties  from town " + town + " are loaded from database.");
                propertyModel.setProperties(get());
            }catch(ExecutionException ex) {
                log.error("Exception was thrown in FindAllPropertiesByTownWorker in method doInBackGround " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("Method doInBackground has been interrupted in FindAllPropertiesByTownWorker " + ex.getCause());
                throw new RuntimeException("Operation interrupted in FindAllPropertiesByTownWorker");
            }
        }
    }
    
    private int[] convert(List<Integer> o) {
        int[] result = new int[o.size()];
        for (int i = 0; i < o.size(); i++) {
            result[i] = o.get(i);
        }
        return result;
    }
    
    private class DeleteOwnerWorker extends SwingWorker<int[], Void> {

        @Override
        protected int[] doInBackground() {
            int[] selectedRows = JTableOwners.getSelectedRows();
            List<Integer> toDeleteRows = new ArrayList<>();
            if (selectedRows.length >= 0) {
                for (int selectedRow : selectedRows) {
                    Owner owner = ownerModel.getOwner(selectedRow);
                    try {
                        ownerManager.deleteOwner(owner);
                        toDeleteRows.add(selectedRow);
                    }catch(Exception x) {
                        throw new ServiceFailureException(owner.toString());
                    }   
                }
                return convert(toDeleteRows);
            }
            return null;
        }

        @Override
        protected void done() {
            try {
                int[] indexes = get();
                if (indexes != null && indexes.length != 0) {
                    ownerModel.deleteOwners(indexes);
                }
            }catch (ExecutionException ex) {
                JOptionPane.showMessageDialog(rootPane, rb.getString("cannot-delete-owner") + " " + ex.getCause(), null, JOptionPane.INFORMATION_MESSAGE);
                log.error("Exception thrown in doInBackground of DeleteOwnerWorker: " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("doInBackground of DeleteOwnerWorker interrupted: " + ex.getCause());
                throw new RuntimeException("Operation interrupted.. DeleteOwnerWorker");
            }
        }
    }
    
    private class DeletePropertyWorker extends SwingWorker<int[], Void> {

        @Override
        protected int[] doInBackground() {
            int[] selectedRows = JTableProperty.getSelectedRows();
            List<Integer> toDeleteRows = new ArrayList<>();
            if (selectedRows.length >= 0) {
                for (int selectedRow : selectedRows) {
                    Property property = propertyModel.getProperty(selectedRow);
                    try {
                        propertyManager.deleteProperty(property.getId());
                        toDeleteRows.add(selectedRow);
                    }catch (Exception ex) {
                        //log.error("Cannot delete property." + ex);
                        throw new ServiceFailureException(property.toString());
                        //JOptionPane.showMessageDialog(null, rb.getString("cannot-delete-property"));
                    }
                }
                return convert(toDeleteRows);
            }
            return null;
        }

        @Override
        protected void done() {
            try {
                int[] indexes = get();
                if (indexes != null && indexes.length != 0) {
                    propertyModel.deleteProperties(indexes);
                }
            } catch (ExecutionException ex) {
                JOptionPane.showMessageDialog(rootPane, rb.getString("cannot-delete-property") + " " + ex.getCause(), null, JOptionPane.INFORMATION_MESSAGE);
                log.error("Exception thrown in doInBackground of DeletePropertyWorker: " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("doInBackground of DeletePropertyWorker interrupted: " + ex.getCause());
                throw new RuntimeException("Operation interrupted.. DeletePropertyWorker");
            }
        }
    }
    
    private class DeleteTitleDeedWorker extends SwingWorker<int[], Void> {

        @Override
        protected int[] doInBackground() {
            int[] selectedRows = JTableTitleDeed.getSelectedRows();
            List<Integer> toDeleteRows = new ArrayList<>();
            if (selectedRows.length >= 0) {
                for (int selectedRow : selectedRows) {
                    TitleDeed titleDeed = titleDeedModel.getTitleDeed(selectedRow);
                    try {
                        titleDeedManager.deleteTitleDeed(titleDeed);
                        toDeleteRows.add(selectedRow);
                    }catch (Exception ex) {
                        log.error("Cannot delete titleDeed." + ex);
                    }
                }
                return convert(toDeleteRows);
            }
            return null;
        }

        @Override
        protected void done() {
            try {
                int[] indexes = get();
                if (indexes != null && indexes.length != 0) {
                    titleDeedModel.deleteTitleDeeds(indexes);
                }
            } catch (ExecutionException ex) {
                log.error("Exception thrown in doInBackground of DeleteTitleDeedWorker: " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("doInBackground of DeleteTitleDeedWorker interrupted: " + ex.getCause());
                throw new RuntimeException("Operation interrupted.. DeleteTitleDeedWorker");
            }
        }
    }
    
    private class FindAllTitleDeedsFromToWorker extends SwingWorker<List<TitleDeed>, Integer> {
        
        private LocalDate from;
        private LocalDate to;

        public FindAllTitleDeedsFromToWorker(LocalDate from, LocalDate to) {
            this.from = from;
            this.to = to;
        }
        
        @Override
        protected List<TitleDeed> doInBackground() throws Exception {
            return titleDeedManager.findTitleDeedsFromTo(from, to);
        }

        @Override
        protected void done() {
            try{
                log.debug("Changing title deed model - title deeds from " + from + " to " + to +" are loaded from database.");
                titleDeedModel.setTitleDeeds(get());
            }catch(IllegalArgumentException ex) {
                log.error(ex.getMessage());
            }catch(ExecutionException ex) {
                log.error("Exception was thrown in FindAllTitleDeedsFromToWorker in method doInBackGround " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("Method doInBackground has been interrupted in FindAllTitleDeedsFromToWorker " + ex.getCause());
                throw new RuntimeException("Operation interrupted in FindAllTitleDeedsFromToWorker");
            }
        }
    }
    
    public class OwnersComboWorker extends SwingWorker<List<Owner>, Integer> {

        @Override
        protected List<Owner> doInBackground() throws Exception {
            return ownerManager.findAllOwners();
        }

        @Override
        protected void done() {
            try {
                List<Owner> owners = get();
                ownersComboBoxModel.removeAllElements();
                for (Owner owner : owners) {
                    ownersComboBoxModel.addElement(owner);
                }
            } catch (ExecutionException ex) {
                log.error("Exception thrown in doInBackground of OwnersComboWorker: " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("doInBackground of OwnersComboWorker interrupted: " + ex.getCause());
                throw new RuntimeException("Operation interrupted.. OwnersComboWorker");
            }
        }
    }
    
    public DefaultComboBoxModel getPropertiesCombo() {
        return propertiesComboBoxModel;
    }

    public DefaultComboBoxModel getOwnersCombo() {
        return ownersComboBoxModel;
    }

    public class PropertiesComboWorker extends SwingWorker<List<Property>, Integer> {

        @Override
        protected List<Property> doInBackground() throws Exception {
            return propertyManager.findAllProperties();
        }

        @Override
        protected void done() {
            try {
                List<Property> properties = get();
                propertiesComboBoxModel.removeAllElements();
                for (Property property : properties) {
                    propertiesComboBoxModel.addElement(property);
                }
            } catch (ExecutionException ex) {
                log.error("Exception thrown in doInBackground of PropertiesComboWorker: " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("doInBackground of PropertiesComboWorker interrupted: " + ex.getCause());
                throw new RuntimeException("Operation interrupted.. PropertiessComboWorker");
            }
        }
    }
    
    private class FindAllTitleDeedsForOwnerWorker extends SwingWorker<List<TitleDeed>, Integer> {

        private Owner owner;
        
        public FindAllTitleDeedsForOwnerWorker(Owner owner) {
            this.owner = owner;
        }
        
        @Override
        protected List<TitleDeed> doInBackground() throws Exception {
            return titleDeedManager.findAllTitleDeedForOwner(owner.getId());
        }

        @Override
        protected void done() {
            try{
                log.debug("Changing titleDeed model - titleDeeds with specified owner loaded from database");
                titleDeedModel.setTitleDeeds(get());
            }catch(ExecutionException ex) {
                log.error("Exception was thrown in FindAllTitleDeedsForOwnerWorker in method doInBackGround with owner " + jTextField1.getText() + "  " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("Method doInBackground has been interrupted in FindAllTitleDeedsForOwnerWorker " + ex.getCause());
                throw new RuntimeException("Operation interrupted in FindAllTitleDeedsForOwnerWorker");
            }
        }
    }
    
    private class FindAllTitleDeedsForPropertyWorker extends SwingWorker<List<TitleDeed>, Integer> {

        private Property property;
        
        public FindAllTitleDeedsForPropertyWorker(Property property) {
            this.property = property;
        }
        
        @Override
        protected List<TitleDeed> doInBackground() throws Exception {
            return titleDeedManager.findAllTitleDeedForProperty(property.getId());
        }

        @Override
        protected void done() {
            try{
                log.debug("Changing titleDeed model - titleDeeds with specified property loaded from database");
                titleDeedModel.setTitleDeeds(get());
            }catch(ExecutionException ex) {
                log.error("Exception was thrown in FindAllTitleDeedsForPropertyWorker in method doInBackGround with owner " + jTextField1.getText() + "  " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("Method doInBackground has been interrupted in FindAllTitleDeedsForPropertyWorker " + ex.getCause());
                throw new RuntimeException("Operation interrupted in FindAllTitleDeedsForPropertyWorker");
            }
        }
    }
    
    /**
     * ################# END SECTION #################
     */
    
    private void createDB() {
        try {
            DBUtils.executeSqlScript(ds, SpringConfig.class.getResourceAsStream("/createTables.sql"));
        } catch (SQLException ex) {
            log.error("DB creation problem " + ex.getCause());
            JOptionPane.showMessageDialog(null, rb.getString("db-error"));
            System.exit(1);
        }
    }
    private void insertIntoDB() {
        try {
            DBUtils.executeSqlScript(ds, SpringConfig.class.getResourceAsStream("/insertValues.sql"));
        } catch (SQLException ex) {
            log.error("DB insert values problem " + ex.getCause());
            deleteDB();
            JOptionPane.showMessageDialog(null, rb.getString("db-error"));
            System.exit(1);
        }
    }
    private static void deleteDB() {
        try {
            DBUtils.executeSqlScript(ds, SpringConfig.class.getResourceAsStream("/dropTables.sql"));
        } catch (SQLException ex) {
            log.error("DB drop problem " + ex.getCause());
        }
    }
    public JDatePickerImpl setDatePickerBirth() {
        UtilDateModel model = new UtilDateModel();
        model.setDate(1980, 01, 01);
        Properties p = new Properties();
        p.put("text.today", rb.getString("day"));
        p.put("text.month", rb.getString("month"));
        p.put("text.year", rb.getString("year"));
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DataLabelFormater());
        return datePicker;
    }
    
    public JDatePickerImpl setDateTitleDeedFromTo() {
        UtilDateModel model = new UtilDateModel();
        model.setDate(2000, 01, 01);
        Properties p = new Properties();
        p.put("text.today", rb.getString("day"));
        p.put("text.month", rb.getString("month"));
        p.put("text.year", rb.getString("year"));
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DataLabelFormater());
        return datePicker;
    }
    
    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        initComponents();
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        createDB();
        insertIntoDB();
        ownerModel = (OwnerTableModel)JTableOwners.getModel();
        findAllOwnersWorker = new FindAllOwnersWorker();
        findAllOwnersWorker.execute();
        
        propertyModel = (PropertyTableModel)JTableProperty.getModel();
        findAllPropertiesWorker = new FindAllPropertiesWorker();
        findAllPropertiesWorker.execute();
        
        titleDeedModel = (TitleDeedTableModel)JTableTitleDeed.getModel();
        findAllTitleDeedsWorker = new FindAllTitleDeedsWorker();
        findAllTitleDeedsWorker.execute();
        
        jComboBox1.setModel(getOwnersCombo());
        ownersComboWorker = new OwnersComboWorker();
        ownersComboWorker.execute();
        jComboBox2.setModel(getPropertiesCombo());
        propertiesComboWorker = new PropertiesComboWorker();
        propertiesComboWorker.execute();
        
        initDatePickers();
        
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        JTableOwners = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        JTableProperty = new javax.swing.JTable();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jButton10 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        JTableTitleDeed = new javax.swing.JTable();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jButton17 = new javax.swing.JButton();
        jComboBox1 = new javax.swing.JComboBox();
        jComboBox2 = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("texts"); // NOI18N
        setTitle(bundle.getString("title")); // NOI18N
        setMinimumSize(new java.awt.Dimension(960, 530));
        setPreferredSize(new java.awt.Dimension(960, 530));

        jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.LEFT);

        JTableOwners.setModel(new OwnerTableModel());
        JTableOwners.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                JTableOwnersMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(JTableOwners);

        jButton1.setText(bundle.getString("list-all")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText(bundle.getString("update-selected")); // NOI18N
        jButton2.setEnabled(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText(bundle.getString("delete-selected")); // NOI18N
        jButton3.setEnabled(false);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText(bundle.getString("create-new-owner")); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jLabel1.setText(bundle.getString("search-by-surname")); // NOI18N

        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField1KeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1)
                    .addComponent(jLabel1))
                .addGap(201, 201, 201)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton4)
                        .addGap(35, 35, 35)
                        .addComponent(jButton2))
                    .addComponent(jTextField1))
                .addGap(35, 35, 35)
                .addComponent(jButton3)
                .addGap(37, 37, 37))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2)
                    .addComponent(jButton3)
                    .addComponent(jButton4))
                .addGap(42, 42, 42)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 137, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(bundle.getString("owners"), jPanel1); // NOI18N

        jPanel2.setToolTipText("");

        JTableProperty.setModel(new PropertyTableModel());
        JTableProperty.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                JTablePropertyMouseReleased(evt);
            }
        });
        jScrollPane2.setViewportView(JTableProperty);

        jButton6.setText(bundle.getString("list-all")); // NOI18N
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setText(bundle.getString("create-new-property")); // NOI18N
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setText(bundle.getString("update-selected")); // NOI18N
        jButton8.setEnabled(false);
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton9.setText(bundle.getString("delete-selected")); // NOI18N
        jButton9.setEnabled(false);
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jLabel2.setText(bundle.getString("search-by-city")); // NOI18N

        jTextField2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField2KeyReleased(evt);
            }
        });

        jButton10.setText(bundle.getString("search")); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton6)
                    .addComponent(jLabel2))
                .addGap(220, 220, 220)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jButton7)
                        .addGap(35, 35, 35)
                        .addComponent(jButton8))
                    .addComponent(jTextField2))
                .addGap(35, 35, 35)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton9)
                    .addComponent(jButton10))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton6)
                    .addComponent(jButton7)
                    .addComponent(jButton8)
                    .addComponent(jButton9))
                .addGap(41, 41, 41)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton10))
                .addContainerGap(136, Short.MAX_VALUE))
        );

        jButton8.getAccessibleContext().setAccessibleName("");

        jTabbedPane1.addTab(bundle.getString("properties"), jPanel2); // NOI18N

        JTableTitleDeed.setModel(new TitleDeedTableModel());
        JTableTitleDeed.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                JTableTitleDeedMouseReleased(evt);
            }
        });
        jScrollPane3.setViewportView(JTableTitleDeed);

        jButton11.setText(bundle.getString("list-all")); // NOI18N
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jButton12.setText(bundle.getString("create-new-titledeed")); // NOI18N
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jButton13.setText(bundle.getString("update-selected")); // NOI18N
        jButton13.setEnabled(false);
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        jButton14.setText(bundle.getString("delete-selected")); // NOI18N
        jButton14.setEnabled(false);
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        jLabel3.setText(bundle.getString("search-from-to")); // NOI18N

        jButton17.setText(bundle.getString("search")); // NOI18N
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel4.setText(bundle.getString("show-titledeed-for-owner")); // NOI18N

        jLabel5.setText(bundle.getString("show-titledeed-for-property")); // NOI18N

        jButton5.setText(bundle.getString("Show")); // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton15.setText(bundle.getString("Show")); // NOI18N
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton11)
                            .addComponent(jLabel3))
                        .addGap(190, 190, 190)
                        .addComponent(jButton12)
                        .addGap(35, 35, 35)
                        .addComponent(jButton13)
                        .addGap(35, 35, 35)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton17)
                            .addComponent(jButton14)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addGap(45, 45, 45)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jComboBox1, 0, 218, Short.MAX_VALUE)
                            .addComponent(jComboBox2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(26, 26, 26)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(54, 54, 54))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton11)
                    .addComponent(jButton12)
                    .addComponent(jButton13)
                    .addComponent(jButton14))
                .addGap(35, 35, 35)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jButton17))
                .addGap(45, 45, 45)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5)
                            .addComponent(jButton15)))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton5)))
                .addContainerGap(49, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(bundle.getString("title-deeds"), jPanel3); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        jTabbedPane1.getAccessibleContext().setAccessibleName(bundle.getString("owners")); // NOI18N

        setSize(new java.awt.Dimension(816, 544));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        jTextField1.setText(null);
        findAllOwnersWorker = new FindAllOwnersWorker();
        findAllOwnersWorker.execute();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PropertyCreateForm(MainFrame.this, null, -1, rb.getString("create")).setVisible(true);
            }
        });
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                int selectedRow = JTableTitleDeed.getSelectedRow();
                new TitleDeedCreateForm(MainFrame.this, titleDeedModel.getTitleDeed(selectedRow), selectedRow, rb.getString("update")).setVisible(true);
            }
        });
        jButton2.setEnabled(false);
    }//GEN-LAST:event_jButton13ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new OwnerCreateForm(MainFrame.this, null, -1, rb.getString("create")).setVisible(true);
            }
        });
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TitleDeedCreateForm(MainFrame.this, null, -1, rb.getString("create")).setVisible(true);
            }
        });
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        DeleteOwnerWorker w = new DeleteOwnerWorker();
        w.execute();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void JTablePropertiesMouseReleased(java.awt.event.MouseEvent evt) {                                           
        if(JTableProperty.getSelectedRowCount() != 1)
            jButton8.setEnabled(false);
        jButton8.setEnabled(true);
    }    
    
    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                int selectedRow = JTableProperty.getSelectedRow();
                new PropertyCreateForm(MainFrame.this, propertyModel.getProperty(selectedRow), selectedRow, rb.getString("update")).setVisible(true);
            }
        });
        jButton8.setEnabled(false);
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jTextField1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyReleased
        String surname = jTextField1.getText();
        if (surname == null || surname.length() == 0) {
            findAllOwnersWorker = new FindAllOwnersWorker();
            findAllOwnersWorker.execute();
            return;
        }
        FindAllOwnersBySurnameWorker worker = new FindAllOwnersBySurnameWorker(surname);
        worker.execute();
    }//GEN-LAST:event_jTextField1KeyReleased

    private void jTextField2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField2KeyReleased
        String town = jTextField2.getText();
        if (town == null || town.length() == 0) {
            findAllPropertiesWorker = new FindAllPropertiesWorker();
            findAllPropertiesWorker.execute();
            return;
        }
        FindAllPropertiesByTownWorker worker = new FindAllPropertiesByTownWorker(town);
        worker.execute();
    }//GEN-LAST:event_jTextField2KeyReleased

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        jTextField2.setText(null);
        findAllPropertiesWorker = new FindAllPropertiesWorker();
        findAllPropertiesWorker.execute();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void JTableOwnersMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_JTableOwnersMouseReleased
        if(JTableOwners.getSelectedRowCount() != 1){
            jButton2.setEnabled(false);
            jButton3.setEnabled(false);
        }
        jButton2.setEnabled(true);
        jButton3.setEnabled(true);
    }//GEN-LAST:event_JTableOwnersMouseReleased

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                int selectedRow = JTableOwners.getSelectedRow();
                new OwnerCreateForm(MainFrame.this, ownerModel.getOwner(selectedRow), selectedRow, rb.getString("update")).setVisible(true);
            }
        });
        jButton2.setEnabled(false);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void JTablePropertyMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_JTablePropertyMouseReleased
        if(JTableProperty.getSelectedRowCount() != 1){
            jButton8.setEnabled(false);
            jButton9.setEnabled(false);
        }
        jButton9.setEnabled(true);
        jButton8.setEnabled(true);
    }//GEN-LAST:event_JTablePropertyMouseReleased

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        DeletePropertyWorker w = new DeletePropertyWorker();
        w.execute();
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        DeleteTitleDeedWorker w = new DeleteTitleDeedWorker();
        w.execute();
    }//GEN-LAST:event_jButton14ActionPerformed

    private void JTableTitleDeedMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_JTableTitleDeedMouseReleased
        if(JTableTitleDeed.getSelectedRowCount() != 1){
            jButton13.setEnabled(false);
            jButton14.setEnabled(false);
        }
        jButton13.setEnabled(true);
        jButton14.setEnabled(true);
    }//GEN-LAST:event_JTableTitleDeedMouseReleased

    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed
        //search titledeed from to
        LocalDate from = LocalDate.of(fromDatePicker.getModel().getYear(), fromDatePicker.getModel().getMonth() + 1, fromDatePicker.getModel().getDay());
        LocalDate to = LocalDate.of(toDatePicker.getModel().getYear(), toDatePicker.getModel().getMonth() + 1, toDatePicker.getModel().getDay());
        if (from.compareTo(to) > 0) {
            JOptionPane.showMessageDialog(rootPane, rb.getString("from-less-than-to"),null,JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        FindAllTitleDeedsFromToWorker worker = new FindAllTitleDeedsFromToWorker(from, to);
        worker.execute();
    }//GEN-LAST:event_jButton17ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        findAllTitleDeedsWorker = new FindAllTitleDeedsWorker();
        findAllTitleDeedsWorker.execute();
        //FindAllTitleDeedsForOwnerWorker
        
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        Owner owner = (Owner)jComboBox1.getSelectedItem();
        findAllTitleDeedsForOwnerWorker = new FindAllTitleDeedsForOwnerWorker(owner);
        findAllTitleDeedsForOwnerWorker.execute();
        //FindAllTitleDeedsForOwnerWorker
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        Property property = (Property)jComboBox2.getSelectedItem();
        findAllTitleDeedsForPropertyWorker = new FindAllTitleDeedsForPropertyWorker(property);
        findAllTitleDeedsForPropertyWorker.execute();
//FindAllTitleDeedsForPropertyWorker
    }//GEN-LAST:event_jButton15ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        deleteDB();
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
        
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable JTableOwners;
    private javax.swing.JTable JTableProperty;
    private javax.swing.JTable JTableTitleDeed;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
}
