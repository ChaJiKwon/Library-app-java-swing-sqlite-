package code;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;


/**
 * @author  nguyen minh vu 2101040008
 *
 */
public class PatronTable extends JFrame {
    private JTable patrons;
    private ArrayList<Integer> ids;
    private Connection conn= null;
    private Statement stmt=null;
    public PatronTable() {
        connectDB();
        refreshTable();
        setTitle("Patron Table");

        setSize(500, 500);
        setLocationRelativeTo(null);
        JButton refresh = new JButton("Refresh");
        JPanel panel = new JPanel();
        refresh.setPreferredSize(new Dimension(80, 40));
        refresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshTable();
            }
        });
        JPanel tablePanel = new JPanel(new BorderLayout());
        JScrollPane jScrollPane = new JScrollPane(patrons);
        tablePanel.add(jScrollPane,BorderLayout.CENTER);
        panel.add(refresh);

        add(panel,BorderLayout.SOUTH);
        add(tablePanel);
        setVisible(true);
    }

    private void connectDB() {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:sqlite:library.db");
            stmt = conn.createStatement();
            System.out.println("Connected to database!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            shutDown();
        }
    }
    private void shutDown() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        System.exit(0);
    }

    private void refreshTable()
    {
        DefaultTableModel table= null;
        //if table null, create new table
        if (patrons==null){
            String[] headers ={"id","name","dob", "email", "phone", "patronType"};
            Object[][] data = new Object[0][6];
            table= new DefaultTableModel(data, headers);

            patrons= new JTable(table);
            patrons.getColumnModel().getColumn(0).setPreferredWidth(100);
            patrons.getColumnModel().getColumn(1).setPreferredWidth(120);
            patrons.getColumnModel().getColumn(2).setPreferredWidth(120);
            patrons.getColumnModel().getColumn(3).setPreferredWidth(150);
            patrons.getColumnModel().getColumn(4).setPreferredWidth(120);
            patrons.getColumnModel().getColumn(5).setPreferredWidth(120);
        }
        else {
            table= (DefaultTableModel) patrons.getModel();
            for (int i = table.getRowCount()-1;i>=0;i--){
                table.removeRow(i);
            }
        }
        try{
            ResultSet rs= stmt.executeQuery("SELECT* FROM patron");
            while (rs.next()){
                table.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("dob"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("patronType"),
                });
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
