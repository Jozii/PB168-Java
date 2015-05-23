/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PropertyManager.frontend;

import PropertyManager.common.SpringConfig;
import PropertyManager.manager.*;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.Locale;
import javax.sql.DataSource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *
 * @author jozef
 */
public abstract class AppCommons {
    protected static ApplicationContext appContext = new AnnotationConfigApplicationContext(SpringConfig.class);
    protected static OwnerManager ownerManager = appContext.getBean("ownerManager", OwnerManager.class);
    protected static PropertyManager propertyManager = appContext.getBean("propertyManager", PropertyManager.class);
    protected static TitleDeedManager titleDeedManager = appContext.getBean("titleDeedManager", TitleDeedManager.class);
    protected static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
    protected static Currency currency = Currency.getInstance("EUR");
    protected static NumberFormat numberFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault());
    protected static DataSource ds = appContext.getBean("dataSource",DataSource.class);
    public static NumberFormat getNumberFormatter() {
        numberFormatter.setCurrency(currency);
        return numberFormatter;
    }    

    public static DataSource getDataSource() {
        return ds;
    }
    
    public static DateTimeFormatter getDateTimeFormatter() {
        return dateTimeFormatter;
    }

    public static ApplicationContext getAppContext() {
        return appContext;
    }

    public static OwnerManager getOwnerManager() {
        return ownerManager;
    }

    public static PropertyManager getPropertyManager() {
        return propertyManager;
    }

    public static TitleDeedManager getTitleDeedManager() {
        return titleDeedManager;
    }

    public static Integer[] getSortedDesc(int[] a) {
        if (a == null) {
            return null;
        }
        Integer[] result = new Integer[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = Integer.valueOf(a[i]);
            
        }
        Arrays.sort(result, Collections.reverseOrder());
        return result;
    }
}
