package org.example.safety.repo;

import org.example.safety.domain.ElderChildLink;
import org.example.safety.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ElderChildLinkRepository extends JpaRepository<ElderChildLink, Long> {
    List<ElderChildLink> findByChild(User child);
}
