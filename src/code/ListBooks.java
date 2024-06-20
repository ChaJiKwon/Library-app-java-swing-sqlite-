package code;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
/**
 * @author  nguyen minh vu 2101040008
 *
 */
public class ListBooks extends JFrame {
    private JTable books;
    private Connection connection=null;
    private Statement statement=null;

    public ListBooks(){
        connectDB();
        refreshTable();
        setTitle("All books");

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
        JScrollPane jScrollPane = new JScrollPane(books);
        tablePanel.add(jScrollPane,BorderLayout.CENTER);
        panel.add(refresh);

        add(panel,BorderLayout.SOUTH);
        add(tablePanel);
        setVisible(true);

    }
    private void refreshTable(){
        DefaultTableModel table=null ;

        if (books==null) {
            String[] header = {"id","ISBN","title","author","genre","pubYear","numCopiesAvailable"};
            Object[][] data = new Object[0][7];
            table= new DefaultTableModel(data,header);

            books = new JTable(table);
            books.getColumnModel().getColumn(0).setPreferredWidth(80);
            books.getColumnModel().getColumn(1).setPreferredWidth(150);
            books.getColumnModel().getColumn(2).setPreferredWidth(150);
            books.getColumnModel().getColumn(3).setPreferredWidth(150);
            books.getColumnModel().getColumn(4).setPreferredWidth(150);
            books.getColumnModel().getColumn(5).setPreferredWidth(150);
            books.getColumnModel().getColumn(6).setPreferredWidth(150);
        }
        else {
            table= (DefaultTableModel) books.getModel();
            for (int i = table.getRowCount()-1;i>=0;i--){
                table.removeRow(i);
            }
        }
        try{
            ResultSet rs = statement.executeQuery("SELECT* FROM book");
            while (rs.next()){
                table.addRow(new Object[]{
                        rs.getInt("id"), rs.getString("ISBN"),rs.getString("title"),rs.getString("author")
                        ,rs.getString("genre"),rs.getString("pubYear"),rs.getInt("numCopiesAvailable")
                });
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void connectDB() {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:sqlite:library.db");
            statement = connection.createStatement();
            System.out.println("Connected to database!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            shutDown();
        }
    }
    private void shutDown() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        System.exit(0);
    }


}
