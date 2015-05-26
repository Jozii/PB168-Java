/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PropertyManager.frontend;

import java.util.Locale;
import javax.swing.DefaultComboBoxModel;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import org.jdatepicker.impl.JDatePickerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import PropertyManager.manager.*;
import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/**
 *
 * @author jozef
 */
public class TitleDeedCreateForm extends javax.swing.JFrame {
    
    
    private final static Logger log = LoggerFactory.getLogger(MainFrame.class);
    private static TitleDeedManager titleDeedManager = AppCommons.getTitleDeedManager();
    private MainFrame context;
    private TitleDeedTableModel titleDeedTableModel;
    private String action;
    private TitleDeed titleDeed;
    private int rowIndex;
    private JDatePickerImpl datePickerStart;
    private JDatePickerImpl datePickerEnd;
    private JDatePickerImpl datePickerReal;
    private DefaultComboBoxModel ownersComboBoxModel = new DefaultComboBoxModel();
    private DefaultComboBoxModel propertiesComboBoxModel = new DefaultComboBoxModel();
    private static OwnerManager ownerManager = AppCommons.getOwnerManager();
    private static PropertyManager propertyManager = AppCommons.getPropertyManager();
    private OwnersComboWorker ownersComboWorker;
    private PropertiesComboWorker propertiesComboWorker;
    private ResourceBundle rb = ResourceBundle.getBundle("texts");

    
    
    /**
     * Creates new form TitleDeedCreateForm
     */
    public TitleDeedCreateForm(MainFrame context, TitleDeed titleDeed, int rowIndex, String action) {
        initComponents();
        this.action = action;
        this.titleDeed = titleDeed;
        this.rowIndex = rowIndex;
        this.context = context;
        titleDeedTableModel = context.getTitleDeeedModel();

        datePickerStart = this.context.setDatePickerStart();
        datePickerStart.setVisible(true);
        datePickerStart.setBounds(120, 72, 200, 30);
        datePickerStart.setLocale(Locale.getDefault());
        jPanel1.add(datePickerStart);

        datePickerEnd = this.context.setDatePickerEnd();
        datePickerEnd.setVisible(true);
        datePickerEnd.setBounds(120, 100, 200, 30);
        datePickerEnd.setLocale(Locale.getDefault());
        jPanel1.add(datePickerEnd);


        jButton1.setText(action);
        jComboBox1.setModel(getOwnersComboBoxModel());
        ownersComboWorker = new OwnersComboWorker();
        ownersComboWorker.execute();
        jComboBox2.setModel(getPropertiesComboBoxModel());
        propertiesComboWorker = new PropertiesComboWorker();
        propertiesComboWorker.execute();
        

        if (titleDeed != null) {
            datePickerStart.getModel().setDate(titleDeed.getStartDate().getYear(), titleDeed.getStartDate().getMonthValue(), titleDeed.getStartDate().getDayOfMonth());
            datePickerStart.getModel().setSelected(true);
            
            datePickerEnd.getModel().setDate(titleDeed.getStartDate().getYear(), titleDeed.getStartDate().getMonthValue(), titleDeed.getStartDate().getDayOfMonth());
            datePickerEnd.getModel().setSelected(true);
            
            jComboBox1.setSelectedItem(titleDeed.getOwner());
            jComboBox2.setSelectedItem(titleDeed.getProperty());
        }

        //context.setVisible(false);
        this.setVisible(true);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
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
    
    public DefaultComboBoxModel getPropertiesComboBoxModel() {
        return propertiesComboBoxModel;
    }

    public DefaultComboBoxModel getOwnersComboBoxModel() {
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
    
    
    private class CreateTitleDeedWorker extends SwingWorker<TitleDeed, Integer> {

        @Override
        protected TitleDeed doInBackground() throws Exception {
            log.debug("Creating new titleDeed in doInBackground " + titleDeed);
            TitleDeed titleDeed = getTitleDeedFromForm();
            if (titleDeed == null) {
                log.error(rb.getString("wrong-enter-data"));
                throw new IllegalArgumentException("wrong-enter-data");
            }

            titleDeedManager.createTitleDeed(titleDeed);
 
            return titleDeed;
        }

        @Override
        protected void done() {
            try {
                TitleDeed titleDeed = get();
                titleDeedTableModel.addTitleDeed(titleDeed);
                log.info("TitleDeed " + titleDeed + " has been created");
                TitleDeedCreateForm.this.dispose();
            } catch (IllegalArgumentException ex) {
                warningMessageBox(ex.getMessage());
                return;
            }catch (ExecutionException ex) {
                log.error("Exception thrown in doInBackground of TitleDeedWorker: " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("doInBackground of TitleDeedWorker interrupted: " + ex.getCause());
                throw new RuntimeException("Operation interrupted in creating new titleDeed");
            }
        }
        
    }
    
    private class UpdateTitleDeedWorker extends SwingWorker<TitleDeed, Integer> {

        @Override
        protected TitleDeed doInBackground() throws Exception {
        
            log.debug("Updating titleDeed in doInBackground " + titleDeed);
            TitleDeed titleDeed = getTitleDeedFromForm();
            if (titleDeed == null) {
                log.error(rb.getString("wrong-enter-data"));
                throw new IllegalArgumentException("wrong-enter-data");
            }
            titleDeedManager.updateTitleDeed(titleDeed);
            return titleDeed;
        }

        @Override
        protected void done() {
            try {
                TitleDeed titleDeed = get();
                System.out.println("BOZEEEEEEEEEE");
                titleDeedTableModel.updateTitleDeed(titleDeed, rowIndex);
                log.info("TitleDeed " + titleDeed + " has been updated");
                context.getJTableTitleDeed().getSelectionModel().clearSelection();
                TitleDeedCreateForm.this.dispose();
            } catch (IllegalArgumentException ex) {
                warningMessageBox(ex.getMessage());
                return;
            }catch (ExecutionException ex) {
                log.error("Exception thrown in doInBackground of UpdateTitleDeedWorker: " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("doInBackground of UpdateTitleDeedWorker interrupted: " + ex.getCause());
                throw new RuntimeException("Operation interrupted in updating new titleDeed");
            }
        }
        
    }
    
    private TitleDeed getTitleDeedFromForm(){
        
        LocalDate localDateStart;
        try{
            Date date = (Date)datePickerStart.getModel().getValue();
            LocalDate tempDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            String stringDate = tempDate.toString();
            localDateStart = LocalDate.parse(stringDate);
        }catch(DateTimeException ex) {
            log.debug("An error occured when parsing date in wrong format " + ex.getCause());
            warningMessageBox(rb.getString("wrong-date-format-entered"));
            localDateStart = null;
            return null;
        }catch(NullPointerException ex) {
            warningMessageBox(rb.getString("select-date"));
            localDateStart = null;
            return null;
        }
        
        
        LocalDate localDateEnd;
        try{
            Date date = (Date)datePickerEnd.getModel().getValue();
            LocalDate tempDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            String stringDate = tempDate.toString();
            localDateEnd = LocalDate.parse(stringDate);
        }catch(DateTimeException ex) {
            log.debug("An error occured when parsing date in wrong format " + ex.getCause());
            warningMessageBox(rb.getString("wrong-date-format-entered"));
            localDateEnd = null;
            return null;
        }catch(NullPointerException ex) {
            warningMessageBox(rb.getString("select-date"));
            localDateEnd = null;
            return null;
        }
        
        Owner owner = (Owner)jComboBox1.getSelectedItem();
        Property property = (Property)jComboBox2.getSelectedItem();
        
        
        if (titleDeed == null) {
            titleDeed = new TitleDeed();
        }
        
        titleDeed.setOwner(owner.getId());
        titleDeed.setProperty(property.getId());
        titleDeed.setStartDate(localDateStart);
        titleDeed.setEndDate(localDateEnd);
        
 

        return titleDeed;
    }
    
    private void warningMessageBox(String message) {
        log.debug("Showed warning message box with message " + message);
        JOptionPane.showMessageDialog(rootPane, message,null,JOptionPane.INFORMATION_MESSAGE);
    }
   

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jComboBox2 = new javax.swing.JComboBox();
        jButton1 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("texts"); // NOI18N
        setTitle(bundle.getString("create-new-titledeed")); // NOI18N
        setMinimumSize(new java.awt.Dimension(390, 190));
        setPreferredSize(new java.awt.Dimension(350, 190));
        setResizable(false);

        jPanel1.setPreferredSize(new java.awt.Dimension(370, 190));

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jButton1.setText(bundle.getString("create")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel4.setText(bundle.getString("end-date")); // NOI18N

        jLabel3.setText(bundle.getString("start-date")); // NOI18N

        jLabel2.setText(bundle.getString("property")); // NOI18N

        jLabel1.setText(bundle.getString("owner")); // NOI18N

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.setMinimumSize(new java.awt.Dimension(80, 20));
        jComboBox1.setPreferredSize(new java.awt.Dimension(80, 20));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(158, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jComboBox1, 0, 200, Short.MAX_VALUE)
                    .addComponent(jComboBox2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addGap(0, 0, Short.MAX_VALUE)
                            .addComponent(jButton1))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel3)
                                .addComponent(jLabel4)
                                .addComponent(jLabel1)
                                .addComponent(jLabel2))
                            .addGap(0, 235, Short.MAX_VALUE)))
                    .addContainerGap()))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(128, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabel1)
                    .addGap(18, 18, 18)
                    .addComponent(jLabel2)
                    .addGap(18, 18, 18)
                    .addComponent(jLabel3)
                    .addGap(15, 15, 15)
                    .addComponent(jLabel4)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                    .addComponent(jButton1)
                    .addContainerGap()))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
        );

        setSize(new java.awt.Dimension(386, 228));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (action.equals(rb.getString("create"))) {
            TitleDeedCreateForm.CreateTitleDeedWorker worker = new TitleDeedCreateForm.CreateTitleDeedWorker();
            worker.execute();
        }else if(action.equals(rb.getString("update"))) {
            TitleDeedCreateForm.UpdateTitleDeedWorker worker = new TitleDeedCreateForm.UpdateTitleDeedWorker();
            worker.execute();
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
