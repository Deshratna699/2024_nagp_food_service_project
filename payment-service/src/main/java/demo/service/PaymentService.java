package demo.service;

import demo.domain.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Created by Deshratna..
 */
public interface PaymentService {
    Payment save(Payment payment);

    Page<Payment> findAll(Pageable pageable);
}
