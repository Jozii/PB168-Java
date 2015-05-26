/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PropertyManager.frontend;

import PropertyManager.common.DataLabelFormater;
import PropertyManager.manager.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import org.jdatepicker.impl.JDatePickerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jozef
 */
public class PropertyCreateForm extends javax.swing.JFrame {

    private static PropertyManager propertyManager = AppCommons.getPropertyManager();
    private MainFrame context;
    private PropertyTableModel propertiesModel;
    private Property property;
    private String action;
    private int rowIndex;
    private JDatePickerImpl datePicker;
    private DataLabelFormater formater = new DataLabelFormater();
    private final static Logger log = LoggerFactory.getLogger(MainFrame.class);
    private ResourceBundle rb = ResourceBundle.getBundle("texts");
    
    
    public PropertyCreateForm(MainFrame context, Property property, int rowIndex, String action) {
        initComponents();
        this.context = context;
        this.property = property;
        this.rowIndex = rowIndex;
        this.action = action;
        this.propertiesModel = context.getPropertyModel();
        jButton2.setText(action);
        
        datePicker = this.context.setDatePickerBirth();
        datePicker.setVisible(true);
        datePicker.setBounds(130, 210, 200,30);
        datePicker.setLocale(Locale.getDefault());
        jPanel1.add(datePicker);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        if (property != null){
            jTextField1.setText(property.getAddressStreet());
            jTextField2.setText(property.getAddressTown());
            jTextField3.setText(property.getPrice().toString());    // HINT
            jTextField4.setText(property.getType());
            jTextField5.setText(String.valueOf(property.getSquareMeters()));  // HINT
            LocalDate localDate = property.getDateOfBuild();
            Instant instant = localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
            Date time = Date.from(instant);
            Object o = (Object) time;
            datePicker.getModel().setDate(property.getDateOfBuild().getYear(), property.getDateOfBuild().getMonthValue(),property.getDateOfBuild().getDayOfMonth());
            datePicker.getModel().setSelected(true);
            jTextField6.setText(property.getDescription());
        }
        this.setVisible(true);
    }
    
    private class CreatePropertyWorker extends SwingWorker<Property, Integer> {

        @Override
        protected Property doInBackground() throws Exception {
            log.debug("Creating new property in doInBackground " + property);
            System.out.println("OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOo");
            Property o = getPropertyFromForm();
            if (o == null) {
                log.error(rb.getString("wrong-enter-data"));
                throw new IllegalArgumentException("wrong-enter-data");
            }
            propertyManager.createProperty(o);
            return o;
        }

        @Override
        protected void done() {
            try {
                Property o = get();
                propertiesModel.addProperty(o);
                log.info("Property " + o + " has been created");
                PropertyCreateForm.this.dispose();
            } catch (IllegalArgumentException ex) {
                warningMessageBox(ex.getMessage());
                return;
            }catch (ExecutionException ex) {
                log.error("Exception thrown in doInBackground of PropertyWorker: " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("doInBackground of PropertyWorker interrupted: " + ex.getCause());
                throw new RuntimeException("Operation interrupted in creating new property");
            }
        }
        
    }
    
    private class UpdatePropertyWorker extends SwingWorker<Property, Integer> {

        @Override
        protected Property doInBackground() throws Exception {
            log.debug("Creating new property in doInBackground " + property);
            Property o = getPropertyFromForm();
            if (o == null) {
                log.error(rb.getString("wrong-enter-data"));
                throw new IllegalArgumentException("wrong-enter-data");
            }
            propertyManager.updateProperty(o);
            return o;
        }

        @Override
        protected void done() {
            try {
                Property o = get();
                propertiesModel.updateProperty(o, rowIndex);
                log.info("Property " + o + " has been updated");
                context.getJTableProperty().getSelectionModel().clearSelection();
                PropertyCreateForm.this.dispose();
            } catch (IllegalArgumentException ex) {
                warningMessageBox(ex.getMessage());
                return;
            }catch (ExecutionException ex) {
                log.error("Exception thrown in doInBackground of UpdatePropertyWorker: " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("doInBackground of UpdatePropertyWorker interrupted: " + ex.getCause());
                throw new RuntimeException("Operation interrupted in updating new property");
            }
        }
        
    }
    
    private Property getPropertyFromForm(){
        
        String street = jTextField1.getText();
        if (street == null || street.length() == 0) {
            warningMessageBox(rb.getString("fill-street"));
            return null;
        }
        
        String town = jTextField2.getText();
        if (town == null || town.length() == 0) {
            warningMessageBox(rb.getString("fill-town"));
            return null;
        }
        
        //BigDecimal price = new BigInteger(jTextField3.getText());  // HINT
        BigDecimal price = new BigDecimal(jTextField3.getText());
        if (price == null) {
            warningMessageBox(rb.getString("fill-price"));
            return null;
        }
        
        String type = jTextField4.getText();
        if (type == null || type.length() == 0) {
            warningMessageBox(rb.getString("fill-type"));
            return null;
        }
        
        int meters = Integer.valueOf(jTextField5.getText());
        if (meters == 0) {
            warningMessageBox(rb.getString("fill-meters"));
            return null;
        }
        String description = jTextField6.getText();
        if (description == null || description.length() == 0) {
            warningMessageBox(rb.getString("fill-description"));
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
        
        
        if (property == null) {
            property = new Property();
        }
        property.setAddressStreet(street);
        property.setAddressTown(town);
        property.setPrice(price);
        property.setType(type);
        property.setSquareMeters(meters);
        property.setDateOfBuild(localDate);
        property.setDescription(description);
        return property;
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
        jButton2 = new javax.swing.JButton();
        jTextField6 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField1 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("texts"); // NOI18N
        setTitle(bundle.getString("create-new-property")); // NOI18N
        setMaximumSize(new java.awt.Dimension(430, 480));
        setMinimumSize(new java.awt.Dimension(430, 480));
        setPreferredSize(new java.awt.Dimension(430, 480));
        setResizable(false);

        jPanel1.setMinimumSize(new java.awt.Dimension(0, 0));
        jPanel1.setPreferredSize(new java.awt.Dimension(366, 450));

        jButton2.setText(bundle.getString("create")); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jTextField6.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jTextField6.setToolTipText("");
        jTextField6.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        jTextField6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField6ActionPerformed(evt);
            }
        });

        jLabel7.setText(bundle.getString("description")); // NOI18N

        jLabel6.setText(bundle.getString("date-of-build")); // NOI18N

        jLabel5.setText(bundle.getString("square-meter")); // NOI18N

        jLabel4.setText(bundle.getString("type")); // NOI18N

        jLabel3.setText(bundle.getString("price")); // NOI18N

        jLabel2.setText(bundle.getString("town")); // NOI18N

        jLabel1.setText(bundle.getString("street")); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jTextField6)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 47, Short.MAX_VALUE)
                                .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(96, 96, 96))))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap(355, Short.MAX_VALUE)
                    .addComponent(jButton2)
                    .addGap(10, 10, 10)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(16, 16, 16)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addGap(17, 17, 17)
                .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(79, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap(382, Short.MAX_VALUE)
                    .addComponent(jButton2)
                    .addContainerGap()))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE)
        );

        setSize(new java.awt.Dimension(446, 454));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField6ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        if (action.equals(rb.getString("create"))) {
            PropertyCreateForm.CreatePropertyWorker worker = new PropertyCreateForm.CreatePropertyWorker();
            worker.execute();
        }else if(action.equals(rb.getString("update"))) {
            PropertyCreateForm.UpdatePropertyWorker worker = new PropertyCreateForm.UpdatePropertyWorker();
            worker.execute();
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    // End of variables declaration//GEN-END:variables
}
