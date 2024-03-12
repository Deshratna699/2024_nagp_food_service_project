package demo.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


@RepositoryRestResource(path = "payments")
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Page<Payment> findAll(Pageable pageable);
}
