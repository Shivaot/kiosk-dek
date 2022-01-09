package com.hitachi.kioskdesk.bootstrap;

import com.hitachi.kioskdesk.domain.ProductModel;
import com.hitachi.kioskdesk.domain.User;
import com.hitachi.kioskdesk.repository.ProductModelRepository;
import com.hitachi.kioskdesk.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


/**
 * Shiva Created on 04/01/22
 */
@Component
public class DataLoader implements ApplicationRunner {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ProductModelRepository productModelRepository;


    @Override
    public void run(ApplicationArguments args) throws Exception {


        if (productModelRepository.count() < 3) {
            ProductModel productModel1 = new ProductModel();
            ProductModel productModel2 = new ProductModel();
            ProductModel productModel3 = new ProductModel();
            productModel1.setModelName("Model 1");
            productModel2.setModelName("Model 2");
            productModel3.setModelName("Model 3");
            productModelRepository.save(productModel1);
            productModelRepository.save(productModel2);
            productModelRepository.save(productModel3);
        }

        if (userRepository.count() < 3) {
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

            User operator = new User();
            operator.setEmail("operator@mail.in");
            operator.setEnabled(true);
            operator.setPassword(passwordEncoder.encode("pass"));
            operator.setRole("ROLE_OPERATOR");
            userRepository.save(operator);

            User qc = new User();
            qc.setEmail("qc@mail.in");
            qc.setEnabled(true);
            qc.setPassword(passwordEncoder.encode("pass"));
            qc.setRole("ROLE_QC");
            userRepository.save(qc);

            User admin = new User();
            admin.setEmail("admin@mail.in");
            admin.setEnabled(true);
            admin.setPassword(passwordEncoder.encode("pass"));
            admin.setRole("ROLE_ADMIN");
            userRepository.save(admin);

        }
    }
}
