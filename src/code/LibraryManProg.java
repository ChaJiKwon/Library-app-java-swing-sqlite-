package code;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;


/**
 * @author  nguyen minh vu 2101040008
 *
 */
public class LibraryManProg {
    private JFrame mainWindow;
    private static Date[] checkoutDate = new Date[]{
            new Date(2023 - 1900, Calendar.MARCH, 25),
            new Date(2023 - 1900, Calendar.MAY, 8),
            new Date(2023 - 1900, Calendar.JUNE, 1),
            new Date(2023 - 1900, Calendar.JUNE, 25),
            new Date(2023 - 1900, Calendar.AUGUST, 10)
    };

    private static Date[] dueDate = new Date[]{
            new Date(2023 - 1900, Calendar.APRIL, 25),
            new Date(2023 - 1900, Calendar.MAY, 10),
            new Date(2023 - 1900, Calendar.JUNE, 25),
            new Date(2023 - 1900, Calendar.JULY, 25),
            new Date(2023 - 1900, Calendar.SEPTEMBER, 20)
    };

    public LibraryManProg()
    {
        createMainWindow();
    }
    public void createMainWindow()
    {
        mainWindow= new JFrame("Library Program");
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setSize(500,500);
        JMenuBar menuBar= new JMenuBar();

        JMenu file= new JMenu("File");
        JMenuItem exit= new JMenuItem("Exit");
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        file.add(exit);
        JMenu patronMenu = new JMenu("Patron");
        JMenuItem newPatron= new JMenuItem("New patron");
        newPatron.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    new PatronWindow("Add a new patron");
                } catch (ParseException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        JMenuItem listPatrons= new JMenuItem("List patrons");
        listPatrons.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new PatronTable();
            }
        });
        patronMenu.add(newPatron);
        patronMenu.add(listPatrons);

        JMenu bookMenu= new JMenu("Book");
        JMenuItem newBook= new JMenuItem("New book");
        newBook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new NewBookWindow();
            }
        });
        JMenuItem listBooks= new JMenuItem("List books");
        listBooks.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ListBooks();
            }
        });
        bookMenu.add(newBook);
        bookMenu.add(listBooks);
        JMenu transactionMenu= new JMenu("Transaction");
        JMenuItem checkoutBook= new JMenuItem("Checkout book");
        checkoutBook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    new CheckoutBook();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                } catch (ParseException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        JMenuItem report= new JMenuItem("Transaction report");
        report.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    new TransactionReport();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        JMenuItem returnBook = new JMenuItem("Return book");
        returnBook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    new ReturnBook();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                } catch (ParseException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        transactionMenu.add(checkoutBook);
        transactionMenu.add(report);
        transactionMenu.add(returnBook);

        menuBar.add(file);
        menuBar.add(patronMenu);
        menuBar.add(bookMenu);
        menuBar.add(transactionMenu);

        ImageIcon backgroundImage = createImageIcon("Leeds+Library+-+new.jpg");
        JLabel backgroundLabel = new JLabel(backgroundImage);
        backgroundLabel.setBounds(0, 0, backgroundImage.getIconWidth(), backgroundImage.getIconHeight());
        mainWindow.add(backgroundLabel);
        mainWindow.setSize(backgroundImage.getIconWidth(), backgroundImage.getIconHeight());
        mainWindow.setResizable(false);
        mainWindow.add(menuBar, BorderLayout.NORTH);
    }

    private ImageIcon createImageIcon(String path) {
        URL imageURL = getClass().getResource(path);
        if (imageURL != null) {
            return new ImageIcon(imageURL);
        } else {
            System.err.println("Image file not found: " + path);
            return null;
        }
    }

    public void displayWindow()
    {
        mainWindow.setVisible(true);
        mainWindow.setLocationRelativeTo(null);
    }
    public static void main(String[] args) {

        LibraryManProg prog = new LibraryManProg();
        prog.displayWindow();
    }
}
