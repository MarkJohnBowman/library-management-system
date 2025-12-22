package models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LibraryRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum RecordType { NEW_BOOK_ENTRY, BORROW_REQUEST }
    public enum Status { AVAILABLE, REQUESTED, BORROWED, RETURNED }

    private RecordType recordType;
    private String recordId;
    private LocalDateTime date;
    private String creatorId;
    private Status status;
    private String assignedLibrarianId;
    private String bookTitle, bookAuthor, bookISBN;

    public LibraryRecord(String recordId, String creatorId) {
        this.recordType = RecordType.BORROW_REQUEST;
        this.recordId = recordId;
        this.creatorId = creatorId;
        this.date = LocalDateTime.now();
        this.status = Status.REQUESTED;
        this.assignedLibrarianId = "";
    }

    public LibraryRecord(String recordId, String creatorId, String bookTitle, String bookAuthor, String bookISBN) {
        this.recordType = RecordType.NEW_BOOK_ENTRY;
        this.recordId = recordId;
        this.creatorId = creatorId;
        this.date = LocalDateTime.now();
        this.status = Status.AVAILABLE;
        this.assignedLibrarianId = creatorId;
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.bookISBN = bookISBN;
    }

    public RecordType getRecordType() { return recordType; }
    public String getRecordId() { return recordId; }
    public LocalDateTime getDate() { return date; }
    public String getCreatorId() { return creatorId; }
    public Status getStatus() { return status; }
    public String getAssignedLibrarianId() { return assignedLibrarianId; }
    public String getBookTitle() { return bookTitle; }
    public String getBookAuthor() { return bookAuthor; }
    public String getBookISBN() { return bookISBN; }

    public void setStatus(Status status) { this.status = status; }
    public void setAssignedLibrarianId(String librarianId) { this.assignedLibrarianId = librarianId; }

    public void assignToLibrarian(String librarianId) {
        this.assignedLibrarianId = librarianId;
        if (this.status == Status.REQUESTED) this.status = Status.BORROWED;
    }

    public void markAsReturned() { this.status = Status.RETURNED; }

    public String getFormattedDate() {
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}