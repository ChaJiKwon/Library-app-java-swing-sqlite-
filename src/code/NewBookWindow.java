package code;

import common.Genre;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
/**
 * @author  nguyen minh vu 2101040008
 *
 */
public class NewBookWindow extends JFrame {
    private JPanel panel;
    private JTextField title;
    private JTextField author;
    private JComboBox<String> genre;
    private JTextField year;
    private JTextField numCopiesAvailable;
    private Connection connection=null;
    private Statement statement=null;
    public NewBookWindow(){
        connectDB();

        setSize(400, 400);
        setLocationRelativeTo(null);
        setTitle("Adding a new book");

        title= new JTextField(20);
        author= new JTextField(20);
        String [] bookGenre = {"Fiction","Non-fiction", "Mystery","Romance","Science-fiction","Fantasy","Thriller",
                "Biography","History","Self-help","Horror","Adventure","Poetry"};
        genre= new JComboBox<>(bookGenre);
        year= new JTextField(20);
        numCopiesAvailable= new JTextField(20);
        JLabel windowTitle= new JLabel("BOOK");
        windowTitle.setBackground(Color.WHITE);
        windowTitle.setOpaque(true);
        add(windowTitle,BorderLayout.NORTH);


        panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.add(new JLabel("Title"));
        panel.add(title);
        panel.add(new JLabel("Author"));
        panel.add(author);
        panel.add(new JLabel("Genre"));
        panel.add(genre);
        panel.add(new JLabel("Publication Year"));
        panel.add(year);
        panel.add(new JLabel("Number of copies available"));
        panel.add(numCopiesAvailable);

        JPanel buttons = new JPanel();

        JButton addBook= new JButton("Add book");
        addBook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String []options= {"Yes", "No"};
                int select= JOptionPane.showOptionDialog(null, "Do you want to add a book ? :", "Confirmation",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
                if (select==0){
                    addBook();
                }
            }
        });
        JButton clear= new JButton("Clear");
        clear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField[] inputs ={title,author, year,numCopiesAvailable};
                for (int i=0;i<inputs.length;i++){
                    inputs[i].setText("");
                }
            }
        });
        buttons.add(addBook);
        buttons.add(clear);
        buttons.setLayout(new FlowLayout());
        add(buttons,BorderLayout.SOUTH);


        add(panel, BorderLayout.CENTER);
        setVisible(true);
    }
    public void connectDB(){
        try{
            connection= DriverManager.getConnection("jdbc:sqlite:library.db");
            statement= connection.createStatement();
            System.out.println("Connected to database");
        } catch (SQLException e) {
            shutDown();
            throw new RuntimeException(e);
        }
    }
    private void shutDown() {
        if (connection!=null){
            try{
                connection.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }
    public void addBook(){
        Genre g;
        if (Objects.equals(genre.getSelectedItem(), "Fiction")){
            g= Genre.FICTION;
        } else if (Objects.equals(genre.getSelectedItem(), "Non-fiction")) {
            g=Genre.NON_FICTION;
        } else if (Objects.equals(genre.getSelectedItem(), "Romance")) {
            g=Genre.ROMANCE;
        }
        else if (Objects.equals(genre.getSelectedItem(), "Science-fiction")) {
            g=Genre.SCIENCE_FICTION;
        }
        else if (Objects.equals(genre.getSelectedItem(), "Fantasy")) {
            g=Genre.FANTASY;
        }
        else if (Objects.equals(genre.getSelectedItem(), "Thriller")) {
            g=Genre.THRILLER;
        }
        else if (Objects.equals(genre.getSelectedItem(), "Biography")) {
            g=Genre.BIOGRAPHY;
        }
        else if (Objects.equals(genre.getSelectedItem(), "History")) {
            g=Genre.HISTORY;
        }
        else if (Objects.equals(genre.getSelectedItem(), "Self-help")) {
            g=Genre.SELF_HELP;
        }
        else if (Objects.equals(genre.getSelectedItem(), "Horror")) {
            g=Genre.HORROR;
        }
        else if (Objects.equals(genre.getSelectedItem(), "Adventure")) {
            g=Genre.ADVENTURE;
        }
        else{
            g=Genre.POETRY;
        }
        Book b = new Book(title.getText(),author.getText(),g, Integer.parseInt(year.getText()),Integer.parseInt(numCopiesAvailable.getText()));
        b.setISBN(b.generateISBN());
        System.out.println(b);
        try{
            statement.execute("INSERT INTO book (ISBN,title,author,genre,pubYear,numCopiesAvailable) VALUES ('" + b.getISBN() +"','"+b.getTitle()+"','" +
                    b.getAuthor()+"','" + b.getGenre().toString()+"','"+b.getPublicationYear()+"','"+b.getNumCopies()+"')");
            JOptionPane.showMessageDialog(this, "Book added successfully", "Added book",JOptionPane.INFORMATION_MESSAGE );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
