package com.academy.fintech.origination.integration;

import com.academy.fintech.application.ApplicationRequest;
import com.academy.fintech.application.ApplicationResponse;
import com.academy.fintech.application.ApplicationServiceGrpc;
import com.academy.fintech.application.CancelApplicationRequest;
import com.academy.fintech.origination.integration.config.DBTestConfiguration;
import com.academy.fintech.origination.integration.db.application.ApplicationTestEntity;
import com.academy.fintech.origination.integration.db.application.ApplicationTestRepository;
import com.academy.fintech.origination.integration.db.client.ClientTestEntity;
import com.academy.fintech.origination.integration.db.client.ClientTestRepository;
import com.academy.fintech.origination.utils.RequestUtils;
import com.academy.fintech.test.db.utils.DBSetupExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@ExtendWith(DBSetupExtension.class)
@Import(DBTestConfiguration.class)
public class DBTest {
    @Autowired
    private ApplicationTestRepository applicationTestRepository;
    @Autowired
    private ClientTestRepository clientTestRepository;
    @Autowired
    private ApplicationServiceGrpc.ApplicationServiceBlockingStub stub;

    @Test
    public void createApplication() {
        ApplicationRequest request = RequestUtils.randomRequest();
        ApplicationResponse response = stub.create(request);

        List<ClientTestEntity> clients = clientTestRepository.findAllByEmail(request.getEmail());
        Assertions.assertEquals(1, clients.size());
        ClientTestEntity client = clients.get(0);
        Assertions.assertEquals(request.getFirstName(), client.getFirstName());
        Assertions.assertEquals(request.getLastName(), client.getLastName());
        Assertions.assertEquals(request.getEmail(), client.getEmail());
        Assertions.assertEquals(0, client.getSalary().compareTo(new BigDecimal(request.getSalary())));

        Optional<ApplicationTestEntity> applicationEntity = applicationTestRepository
                .findById(response.getApplicationId());
        Assertions.assertTrue(applicationEntity.isPresent());
        ApplicationTestEntity application = applicationEntity.get();
        Assertions.assertEquals(client.getId(), application.getClientId());
        Assertions.assertEquals(0, application.getRequestedDisbursementAmount()
                .compareTo(new BigDecimal(request.getDisbursementAmount())));
        Assertions.assertEquals(ApplicationTestEntity.PaymentApplicationStatus.NEW, application.getStatus());
    }

    @Test
    public void cancelApplication() {
        ApplicationRequest request = RequestUtils.randomRequest();
        String applicationId = stub.create(request).getApplicationId();
        stub.cancel(CancelApplicationRequest.newBuilder()
                .setApplicationId(applicationId)
                .build());
        Optional<ApplicationTestEntity> entityOptional = applicationTestRepository.findById(applicationId);
        Assertions.assertTrue(entityOptional.isPresent());
        ApplicationTestEntity application = entityOptional.get();
        Assertions.assertEquals(ApplicationTestEntity.PaymentApplicationStatus.DENIED, application.getStatus());
    }
}
