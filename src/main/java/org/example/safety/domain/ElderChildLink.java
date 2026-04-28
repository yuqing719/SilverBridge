package org.example.safety.domain;

import jakarta.persistence.*;

@Entity
@Table(
        name = "elder_child_links",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_elder_child", columnNames = {"elder_id", "child_id"})
        }
)
public class ElderChildLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "elder_id", nullable = false)
    private User elder;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    private User child;

    protected ElderChildLink() {
    }

    public ElderChildLink(User elder, User child) {
        this.elder = elder;
        this.child = child;
    }

    public Long getId() {
        return id;
    }

    public User getElder() {
        return elder;
    }

    public User getChild() {
        return child;
    }
}
