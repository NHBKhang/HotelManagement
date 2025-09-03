package com.team.hotelmanagementapp.pojo;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "invoice")
@NamedQueries({
    @NamedQuery(name = "Invoice.findAll", query = "SELECT u FROM Invoice u"),
    @NamedQuery(name = "Invoice.findById", query = "SELECT u FROM Invoice u WHERE u.id = :id")})
public class Invoice implements Serializable {

    public enum Status {
        UNPAID("Chưa trả", "badge bg-danger"),
        PARTIALLY_PAID("Trả một phần", "badge bg-warning"),
        PAID("Đã trả", "badge bg-success");

        private final String label;
        private final String badgeClass;

        private Status(String label, String badgeClass) {
            this.label = label;
            this.badgeClass = badgeClass;
        }

        public String getLabel() {
            return label;
        }

        public String getBadgeClass() {
            return badgeClass;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();
    
    @Column(name = "issue_at")
    private LocalDateTime issueAt;

    @Column(name = "invoice_number")
    private String invoiceNumber;

    @Column(name = "sent_to_email")
    private String sentToEmail;

    @Basic(optional = false)
    private Status status;

    public Invoice() {
    }

    public Invoice(Integer id, LocalDateTime issueAt, String invoiceNumber, String sentToEmail, Status status) {
        this.id = id;
        this.issueAt = issueAt;
        this.invoiceNumber = invoiceNumber;
        this.sentToEmail = sentToEmail;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getIssueAt() {
        return issueAt;
    }

    public void setIssueAt(LocalDateTime issueAt) {
        this.issueAt = issueAt;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getSentToEmail() {
        return sentToEmail;
    }

    public void setSentToEmail(String sentToEmail) {
        this.sentToEmail = sentToEmail;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.booking);
        hash = 29 * hash + Objects.hashCode(this.issueAt);
        hash = 29 * hash + Objects.hashCode(this.invoiceNumber);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Invoice other = (Invoice) obj;
        if (!Objects.equals(this.invoiceNumber, other.invoiceNumber)) {
            return false;
        }
        if (!Objects.equals(this.booking, other.booking)) {
            return false;
        }
        return Objects.equals(this.issueAt, other.issueAt);
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

}
