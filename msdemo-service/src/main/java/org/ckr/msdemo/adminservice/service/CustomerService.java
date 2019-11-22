package org.ckr.msdemo.adminservice.service;

import org.ckr.msdemo.adminservice.bmo.CustomerBmo;
import org.ckr.msdemo.adminservice.entity.Customer;
import org.ckr.msdemo.adminservice.repository.CustomerRepository;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
public class CustomerService {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    @Transactional(value = "mongoDbTransactionManager")
    public List saveCustomer(Customer customer) {

        for(int i = 0 ; i < 10; i++) {
            Customer cus = new Customer();
            cus.setFirstName("first" + i);
            cus.setLastName("last" + i);
            customerRepository.save(cus);
        }
        customerRepository.save(customer);
        List<Customer> customerList = customerRepository.findAll();


        System.out.println("customerList" + customerList);

//        int i = 0;
//        if(i == 0) {
//            throw new RuntimeException("");
//        }
        return mongoTemplate.findAll(Customer.class);

    }



    public String helloDrools() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer();


        KieSession kSession = null;
        CustomerBmo bmo  = new CustomerBmo();


        try{
            kSession = kContainer.newKieSession("ksession-rules");


            bmo.setName("Jack");

            kSession.insert(bmo);
            kSession.fireAllRules();
        } finally {
            if( kSession != null) {
                kSession.dispose();
            }
        }


        return bmo.getMessage();




    }
}
