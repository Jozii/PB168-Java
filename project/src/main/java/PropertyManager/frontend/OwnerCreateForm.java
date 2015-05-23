/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PropertyManager.frontend;

import PropertyManager.common.DataLabelFormater;
import PropertyManager.manager.*;
import java.time.DateTimeException;
import java.time.Instant;
import java.util.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import org.jdatepicker.impl.JDatePickerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 *
 * @author jozef
 */
public class OwnerCreateForm extends javax.swing.JFrame {
   
    private static OwnerManager ownerManager = AppCommons.getOwnerManager();
    private MainFrame context;
    private OwnerTableModel ownersModel;
    private Owner owner;
    private String action;
    private int rowIndex;
    private JDatePickerImpl datePicker;
    private DataLabelFormater formater = new DataLabelFormater();
    private final static Logger log = LoggerFactory.getLogger(MainFrame.class);
    private ResourceBundle rb = ResourceBundle.getBundle("texts");
    
    public OwnerCreateForm(MainFrame context, Owner owner, int rowIndex, String action) {
        initComponents();
        this.context = context;
        this.owner = owner;
        this.rowIndex = rowIndex;
        this.action = action;
        this.ownersModel = context.getOwnerModel();
        jButton2.setText(action);
        
        datePicker = this.context.setDatePickerBirth();
        datePicker.setVisible(true);
        datePicker.setBounds(200, 240, 200,30);
        datePicker.setLocale(Locale.getDefault());
        jPanel1.add(datePicker);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        if (owner != null){
            jTextField1.setText(owner.getName());
            jTextField2.setText(owner.getSurname());
            jTextField3.setText(owner.getPhoneNumber());
            jTextField4.setText(owner.getAddressStreet());
            jTextField5.setText(owner.getAddressTown());
            LocalDate localDate = owner.getBorn();
            Instant instant = localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
            Date time = Date.from(instant);
            Object o = (Object) time;
            datePicker.getModel().setDate(owner.getBorn().getYear(), owner.getBorn().getMonthValue(),owner.getBorn().getDayOfMonth());
            datePicker.getModel().setSelected(true);
        }
        this.setVisible(true);
    }
    
    private class CreateOwnerWorker extends SwingWorker<Owner, Integer> {

        @Override
        protected Owner doInBackground() throws Exception {
            log.debug("Creating new owner in doInBackground " + owner);
            Owner o = getOwnerFromForm();
            if (o == null) {
                log.error(rb.getString("wrong-enter-data"));
                throw new IllegalArgumentException("wrong-enter-data");
            }
            ownerManager.createOwner(o);
            return o;
        }

        @Override
        protected void done() {
            try {
                Owner o = get();
                ownersModel.addOwner(o);
                log.info("Owner " + o + " has been created");
                OwnerCreateForm.this.dispose();
            } catch (IllegalArgumentException ex) {
                warningMessageBox(ex.getMessage());
                return;
            }catch (ExecutionException ex) {
                log.error("Exception thrown in doInBackground of CreateOwnerWorker: " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("doInBackground of CreateOwnerWorker interrupted: " + ex.getCause());
                throw new RuntimeException("Operation interrupted in creating new owner");
            }
        }
        
    }
    
    private class UpdateOwnerWorker extends SwingWorker<Owner, Integer> {

        @Override
        protected Owner doInBackground() throws Exception {
            log.debug("Creating new owner in doInBackground " + owner);
            Owner o = getOwnerFromForm();
            if (o == null) {
                log.error(rb.getString("wrong-enter-data"));
                throw new IllegalArgumentException("wrong-enter-data");
            }
            ownerManager.updateOwner(o);
            return o;
        }

        @Override
        protected void done() {
            try {
                Owner o = get();
                ownersModel.updateOwner(o, rowIndex);
                log.info("Owner " + o + " has been updated");
                context.getJTableOwners().getSelectionModel().clearSelection();
                OwnerCreateForm.this.dispose();
            } catch (IllegalArgumentException ex) {
                warningMessageBox(ex.getMessage());
                return;
            }catch (ExecutionException ex) {
                log.error("Exception thrown in doInBackground of UpdateOwnerWorker: " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("doInBackground of UpdateOwnerWorker interrupted: " + ex.getCause());
                throw new RuntimeException("Operation interrupted in updating new owner");
            }
        }
        
    }
    private Owner getOwnerFromForm(){
        
        String name = jTextField1.getText();
        if (name == null || name.length() == 0) {
            warningMessageBox(rb.getString("fill-name"));
            return null;
        }
        
        String surname = jTextField2.getText();
        if (surname == null || surname.length() == 0) {
            warningMessageBox(rb.getString("fill-surname"));
            return null;
        }
        
        String phoneNumber = jTextField3.getText();
        if (phoneNumber == null || phoneNumber.length() == 0) {
            warningMessageBox(rb.getString("fill-phone"));
            return null;
        }
        
        String addressStreet = jTextField4.getText();
        if (addressStreet == null || addressStreet.length() == 0) {
            warningMessageBox(rb.getString("fill-street"));
            return null;
        }
        
        String addressTown = jTextField5.getText();
        if (addressTown == null || addressTown.length() == 0) {
            warningMessageBox(rb.getString("fill-town"));
            return null;
        }
        LocalDate localDate;
        try{
            Date date = (Date)datePicker.getModel().getValue();
            LocalDate tempDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            String stringDate = tempDate.toString();
            localDate = LocalDate.parse(stringDate);
        }catch(DateTimeException ex) {
            log.debug("An error occured when parsing date in wrong format " + ex.getCause());
            warningMessageBox(rb.getString("wrong-date-format-entered"));
            localDate = null;
            return null;
        }catch(NullPointerException ex) {
            warningMessageBox(rb.getString("select-date"));
            localDate = null;
            return null;
        }
        if (owner == null) {
            owner = new Owner();
        }
        owner.setName(name);
        owner.setSurname(surname);
        owner.setBorn(localDate);
        owner.setPhoneNumber(phoneNumber);
        owner.setAddressStreet(addressStreet);
        owner.setAddressTown(addressTown);
        return owner;
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
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("texts"); // NOI18N
        setTitle(bundle.getString("create-new-owner")); // NOI18N
        setMaximumSize(new java.awt.Dimension(430, 360));
        setMinimumSize(new java.awt.Dimension(430, 360));
        setPreferredSize(new java.awt.Dimension(430, 360));
        setResizable(false);

        jLabel2.setText(bundle.getString("surname")); // NOI18N

        jLabel4.setText(bundle.getString("phone-number")); // NOI18N

        jLabel5.setText(bundle.getString("street")); // NOI18N

        jLabel3.setText(bundle.getString("born")); // NOI18N

        jLabel1.setText(bundle.getString("name")); // NOI18N

        jLabel6.setText(bundle.getString("town")); // NOI18N

        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jButton2.setText(bundle.getString("create")); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton2))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4)
                            .addComponent(jLabel1)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 69, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                .addComponent(jButton2)
                .addGap(67, 67, 67))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        if (action.equals(rb.getString("create"))) {
            CreateOwnerWorker worker = new CreateOwnerWorker();
            worker.execute();
        }else if(action.equals(rb.getString("update"))) {
            UpdateOwnerWorker worker = new UpdateOwnerWorker();
            worker.execute();
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    /**
     * @param args the command line arguments
     */
    /*public static void main(String args[]) {
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new OwnerCreateForm().setVisible(true);
            }
        });
    }*/

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    // End of variables declaration//GEN-END:variables
}
