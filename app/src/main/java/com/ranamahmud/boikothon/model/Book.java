package com.ranamahmud.boikothon.model;

public class Book {
    private String bookImageUrl;
    private String bookTitle;
    private String bookOwner;
    private String bookGenre;
    private String bookUid;
    private String bookOwnerUid;
    private boolean bookAvailable;
    private int bookRating;

    public Book(String bookImageUrl, String bookTitle, String bookOwner, String bookGenre, String bookUid,String bookOwnerUid, boolean bookAvailable, int bookRating) {
        this.bookImageUrl = bookImageUrl;
        this.bookTitle = bookTitle;
        this.bookOwner = bookOwner;
        this.bookGenre = bookGenre;
        this.bookUid = bookUid;
        this.bookOwnerUid = bookOwnerUid;
        this.bookAvailable = bookAvailable;
        this.bookRating = bookRating;
    }

    public String getBookImageUrl() {
        return bookImageUrl;
    }

    public void setBookImageUrl(String bookImageUrl) {
        this.bookImageUrl = bookImageUrl;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getBookOwner() {
        return bookOwner;
    }

    public void setBookOwner(String bookOwner) {
        this.bookOwner = bookOwner;
    }

    public String getBookGenre() {
        return bookGenre;
    }

    public void setBookGenre(String bookGenre) {
        this.bookGenre = bookGenre;
    }

    public String getBookUid() {
        return bookUid;
    }

    public void setBookUid(String bookUid) {
        this.bookUid = bookUid;
    }

    public String getBookOwnerUid() {
        return bookOwnerUid;
    }

    public void setBookOwnerUid(String bookOwnerUid) {
        this.bookOwnerUid = bookOwnerUid;
    }

    public boolean isBookAvailable() {
        return bookAvailable;
    }

    public void setBookAvailable(boolean bookAvailable) {
        this.bookAvailable = bookAvailable;
    }

    public int getBookRating() {
        return bookRating;
    }

    public void setBookRating(int bookRating) {
        this.bookRating = bookRating;
    }
}
