package code;

import common.Genre;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Book {
    private String ISBN;
    private String title;
    private String author;
    private Genre genre;
    private int publicationYear;
    private int numCopies;
    public Book(String title, String author, Genre genre, int publicationYear, int numCopies) {
        this.ISBN = generateISBN();
        this.title = title;
        this.author = author; // Assign the parameter to the instance variable directly
        this.genre = genre;
        this.publicationYear = publicationYear;
        this.numCopies = numCopies;
    }
    public int getNumCopies()
    {
        return this.numCopies;
    }
    public  String generateISBN()
    {
        try{
            StringBuilder initials= new StringBuilder();
            String []author = this.author.split("\\s");
            for (String s : author){
                initials.append(s.charAt(0));
            }

            String code ;
            List<Genre> genreList= new ArrayList<>();
            Genre [] genres = Genre.values();
            genreList.addAll(Arrays.asList(genres));
            int genreCode= genreList.indexOf(this.genre);
            if(genreCode<10){
                code= 0 +""+genreCode;
            }
            else {
                code= genreCode+"";
            }

            return initials + "-" + code +"-"+this.publicationYear;
        }catch (NullPointerException e){
            return "";
        }
    }

    public String getTitle() {
        return title;
    }

    public Genre getGenre() {
        return genre;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public String getAuthor() {
        return this.author;
    }

    public String getISBN() {
        return generateISBN();
    }
    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }

    public void setNumCopies(int numCopies) {
        this.numCopies = numCopies;
    }

    @Override
    public String toString() {
        return "Book{" +
                "ISBN='" + ISBN + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", genre=" + genre +
                ", publicationYear=" + publicationYear +
                ", numCopies=" + numCopies +
                '}';
    }
}
