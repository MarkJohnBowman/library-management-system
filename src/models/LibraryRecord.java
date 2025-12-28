package models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Library record class - handles both book entries and borrow requests
public class LibraryRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    // Record type - either a new book entry or a borrow request
    public enum RecordType {
        NEW_BOOK_ENTRY,
        BORROW_REQUEST
    }

    // Status of the record
    public enum Status {
        AVAILABLE,
        REQUESTED,
        BORROWED,
        RETURNED
    }

    private RecordType recordType;
    private String recordId;
    private LocalDateTime date;
    private String creatorId;  // ID of person who created this record
    private Status status;
    private String assignedLibrarianId;
    
    // Book details (only used for book entries)
    private String bookTitle;
    private String bookAuthor;
    private String bookISBN;

    // Constructor for borrow requests (students)
    public LibraryRecord(String recordId, String creatorId) {
        this.recordType = RecordType.BORROW_REQUEST;
        this.recordId = recordId;
        this.creatorId = creatorId;
        this.date = LocalDateTime.now();
        this.status = Status.REQUESTED;
        this.assignedLibrarianId = "";
    }

    // Constructor for book entries (librarians)
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

    // Getters
    public RecordType getRecordType() { return recordType; }
    public String getRecordId() { return recordId; }
    public LocalDateTime getDate() { return date; }
    public String getCreatorId() { return creatorId; }
    public Status getStatus() { return status; }
    public String getAssignedLibrarianId() { return assignedLibrarianId; }
    public String getBookTitle() { return bookTitle; }
    public String getBookAuthor() { return bookAuthor; }
    public String getBookISBN() { return bookISBN; }

    // Setters
    public void setStatus(Status status) { this.status = status; }
    public void setAssignedLibrarianId(String librarianId) { 
        this.assignedLibrarianId = librarianId; 
    }

    // Assign request to a librarian and update status
    public void assignToLibrarian(String librarianId) {
        this.assignedLibrarianId = librarianId;
        if (this.status == Status.REQUESTED) {
            this.status = Status.BORROWED;
        }
    }

    // Mark book as returned
    public void markAsReturned() {
        this.status = Status.RETURNED;
    }

    // Get date in readable format
    public String getFormattedDate() {
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public String toString() {
        return "LibraryRecord{recordType=" + recordType + ", recordId='" + recordId + "', date=" + getFormattedDate() + 
               ", creatorId='" + creatorId + "', status=" + status + ", assignedLibrarianId='" + assignedLibrarianId + 
               (recordType == RecordType.NEW_BOOK_ENTRY ? "', bookTitle='" + bookTitle + "', bookAuthor='" + bookAuthor + "', bookISBN='" + bookISBN : "") + "'}";
    }
}