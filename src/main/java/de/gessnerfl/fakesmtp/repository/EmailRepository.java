package de.gessnerfl.fakesmtp.repository;

import de.gessnerfl.fakesmtp.model.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface EmailRepository extends JpaRepository<Email,Long>{

    @Transactional
    @Modifying
    @Query(value = "DELETE email o WHERE o.id IN ( SELECT i.id FROM email i ORDER BY i.received_on DESC OFFSET ?1)", nativeQuery = true)
    int deleteEmailsExceedingDateRetentionLimit(int maxNumber);

    @Transactional
    @Query(value = "SELECT o.* FROM email o WHERE o.message_id= ?1", nativeQuery = true)
    Email findByMessageId(String messageId);

    @Transactional
    @Query(value = "SELECT o.* FROM email o WHERE o.from_address= IFNULL(?1, o.from_address) AND o.to_address= IFNULL(?2, o.to_address)", nativeQuery = true)
    List<Email> findEmails(String fromAddress, String toAddress);
}
