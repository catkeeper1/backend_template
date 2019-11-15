package org.ckr.msdemo.adminservice.controller;

import org.ckr.msdemo.adminservice.entity.Customer;
import org.ckr.msdemo.adminservice.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CustomerController {

    @Autowired
    CustomerService customerService;

    @PostMapping
    public List saveCustomer(@RequestBody Customer customer) {
        return customerService.saveCustomer(customer);
    }

}
