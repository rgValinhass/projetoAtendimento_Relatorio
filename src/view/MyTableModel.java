package view;

import model.SwingColumn;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;


public class MyTableModel extends DefaultTableModel {

    private static final long serialVersionUID = 1L;
    private final List entitysToList;
    private final Class entityClassToList;
    private List<Method> fieldToData = new LinkedList<Method>();
    private final JTable tableToControl;

    /**
     * Construtor padrão
     *
     * @author Dyego Souza do Carmo
     * @version 1.0, 
     */
    public MyTableModel(Class entityClassToList, List entitysToList,JTable tableToControl) {
        super();
        this.entitysToList = entitysToList;
        this.entityClassToList = entityClassToList;
        this.tableToControl = tableToControl;
        //changed Fernando       
        this.tableToControl.setAutoCreateRowSorter(true);
        this.tableToControl.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        try {
            startAddTheColumns();
            startAddValues();
        } catch (Exception ex) {
            Logger.getLogger(MyTableModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    //changed Fernando
    @Override
    public boolean isCellEditable(int row,int col){
        return false;
    }
    private void startAddTheColumns() throws NoSuchMethodException, InstantiationException, IllegalAccessException {
        // Here is The Reference Class !
        // Reflection is the man !!!!! (or the method ?)

        //changed by Carolaine 
        if(entityClassToList.getSuperclass().getSuperclass() !=null){
          for (Field field : entityClassToList.getSuperclass().getSuperclass().getDeclaredFields()) {
            SwingColumn theAnnotation = field.getAnnotation(SwingColumn.class);
            if (theAnnotation != null) {
                // Yes , the column is annotated , but and the next ?
                addColumn(theAnnotation.description());
                //tableToControl.getColumnModel().getColumn(getColumnCount()-1).setCellRenderer(theAnnotation.renderer().newInstance());
                String methodName = "get" + field.getName().toUpperCase().charAt(0) + field.getName().substring(1);
                fieldToData.add(entityClassToList.getSuperclass().getSuperclass().getDeclaredMethod(methodName));
            }
          }  
        }

        //changed by Carolaine 
        if(entityClassToList.getSuperclass() !=null){
            for (Field field : entityClassToList.getSuperclass().getDeclaredFields()) {
                SwingColumn theAnnotation = field.getAnnotation(SwingColumn.class);
                if (theAnnotation != null) {
                    // Yes , the column is annotated , but and the next ?
                    addColumn(theAnnotation.description());
                    //tableToControl.getColumnModel().getColumn(getColumnCount()-1).setCellRenderer(theAnnotation.renderer().newInstance());
                    String methodName = "get" + field.getName().toUpperCase().charAt(0) + field.getName().substring(1);
                    fieldToData.add(entityClassToList.getSuperclass().getDeclaredMethod(methodName));
                }
            }
        }

        for (Field field : entityClassToList.getDeclaredFields()) {
            SwingColumn theAnnotation = field.getAnnotation(SwingColumn.class);
            if (theAnnotation != null) {
                // Yes , the column is annotated , but and the next ?
                addColumn(theAnnotation.description());
                //tableToControl.getColumnModel().getColumn(getColumnCount()-1).setCellRenderer(theAnnotation.renderer().newInstance());
                String methodName = "get" + field.getName().toUpperCase().charAt(0) + field.getName().substring(1);
                fieldToData.add(entityClassToList.getDeclaredMethod(methodName));
            }
        }
    }

    private void startAddValues() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        // Manipulates Only the DATA fields

        for (Object entity : entitysToList) {
            List<Object> valuesToAdd = new LinkedList<Object>();

            for (Method method : fieldToData) {
                valuesToAdd.add(method.invoke(entity));
            }

            // Here we add the values in the MODEL !
            addRow(valuesToAdd.toArray());
            
        }
    }
}
