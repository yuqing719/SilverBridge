package org.example.safety.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "emergency_contacts")
public class EmergencyContact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "elder_id", nullable = false)
    private User elder;

    @Column(nullable = false, length = 32)
    private String name;

    @Column(nullable = false, length = 32)
    private String phone;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    protected EmergencyContact() {
    }

    public EmergencyContact(User elder, String name, String phone, int sortOrder) {
        this.elder = elder;
        this.name = name;
        this.phone = phone;
        this.sortOrder = sortOrder;
    }

    public Long getId() {
        return id;
    }

    public User getElder() {
        return elder;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public int getSortOrder() {
        return sortOrder;
    }
}
