package org.ckr.msdemo.adminservice.repository;

import java.util.List;

import org.ckr.msdemo.adminservice.entity.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CustomerRepository extends MongoRepository<Customer, String> {

    public Customer findByFirstName(String firstName);
    public List<Customer> findByLastName(String lastName);

}