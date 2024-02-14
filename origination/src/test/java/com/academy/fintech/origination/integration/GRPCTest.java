package com.academy.fintech.origination.integration;

import com.academy.fintech.application.ApplicationRequest;
import com.academy.fintech.application.ApplicationResponse;
import com.academy.fintech.application.ApplicationServiceGrpc;
import com.academy.fintech.application.CancelApplicationRequest;
import com.academy.fintech.application.CancelApplicationResponse;
import com.academy.fintech.origination.integration.config.DBTestConfiguration;
import com.academy.fintech.origination.utils.RequestUtils;
import com.academy.fintech.test.db.utils.DBSetupExtension;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.HashSet;
import java.util.Set;

@SpringBootTest
@ExtendWith(DBSetupExtension.class)
@Import(DBTestConfiguration.class)
public class GRPCTest {
    @Autowired
    private ApplicationServiceGrpc.ApplicationServiceBlockingStub stub;

    @Test
    public void createOneApplication() {
        ApplicationRequest request = RequestUtils.randomRequest();
        Assertions.assertDoesNotThrow(() -> stub.create(request));
    }

    @Test
    public void createALotOfApplications() {
        Set<String> applications = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            ApplicationRequest request = RequestUtils.randomRequest();
            String newId = stub.create(request).getApplicationId();
            Assertions.assertFalse(applications.contains(newId));
            applications.add(newId);
        }
    }

    @Test
    public void unableToCreateClientsWithEqualEmails() {
        String email = RequestUtils.randomEmail();
        ApplicationRequest first = ApplicationRequest.newBuilder()
                .setDisbursementAmount(RequestUtils.randomMoney())
                .setSalary(RequestUtils.randomMoney())
                .setFirstName("AAA")
                .setLastName(RequestUtils.randomString())
                .setEmail(email)
                .build();

        ApplicationRequest second = ApplicationRequest.newBuilder()
                .setDisbursementAmount(RequestUtils.randomMoney())
                .setSalary(RequestUtils.randomMoney())
                .setFirstName("BBB")
                .setLastName(RequestUtils.randomString())
                .setEmail(email)
                .build();

        Assertions.assertDoesNotThrow(() -> stub.create(first));
        StatusRuntimeException status = Assertions.assertThrows(StatusRuntimeException.class,
                () -> stub.create(second));
        Metadata trailers = status.getTrailers();
        Assertions.assertNotNull(trailers);
        String emailMetadata = trailers.get(Metadata.Key.of("client_email", Metadata.ASCII_STRING_MARSHALLER));
        Assertions.assertEquals(email, emailMetadata);
    }

    @Test
    public void unableToCreateClientWithInvalidEmail() {
        ApplicationRequest request = ApplicationRequest.newBuilder()
                .setDisbursementAmount(RequestUtils.randomMoney())
                .setSalary(RequestUtils.randomMoney())
                .setFirstName(RequestUtils.randomString())
                .setLastName(RequestUtils.randomString())
                .setEmail("random email")
                .build();

        StatusRuntimeException status = Assertions.assertThrows(StatusRuntimeException.class,
                () -> stub.create(request));
        Assertions.assertEquals(Status.Code.FAILED_PRECONDITION, status.getStatus().getCode());
    }

    @Test
    public void cannotCreateDuplicateOfNewApplication() {
        ApplicationRequest request = RequestUtils.randomRequest();
        ApplicationResponse response = stub.create(request);
        StatusRuntimeException status = Assertions.assertThrows(StatusRuntimeException.class,
                () -> stub.create(request));
        Assertions.assertEquals(Status.Code.ALREADY_EXISTS, status.getStatus().getCode());
        Metadata trailers = status.getTrailers();
        Assertions.assertNotNull(trailers);
        String applicationId = trailers.get(Metadata.Key.of("agreement_id", Metadata.ASCII_STRING_MARSHALLER));
        Assertions.assertEquals(response.getApplicationId(), applicationId);
    }

    @Test
    public void cancelApplication() {
        ApplicationRequest request = RequestUtils.randomRequest();
        String applicationId = stub.create(request).getApplicationId();
        Assertions.assertDoesNotThrow(() -> {
            CancelApplicationResponse response = stub.cancel(CancelApplicationRequest.newBuilder()
                    .setApplicationId(applicationId)
                    .build());
            Assertions.assertTrue(response.getSuccess());
        });
    }

    @Test
    public void cannotCancelNonExistentApplication() {
        CancelApplicationRequest request = CancelApplicationRequest.newBuilder()
                .setApplicationId("application does not exist")
                .build();
        StatusRuntimeException status = Assertions.assertThrows(StatusRuntimeException.class,
                () -> stub.cancel(request));
        Assertions.assertEquals(Status.Code.NOT_FOUND, status.getStatus().getCode());
    }

    @Test
    public void cancelALotOfApplications() {
        Set<String> applications = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            ApplicationRequest request = RequestUtils.randomRequest();
            String newId = stub.create(request).getApplicationId();
            applications.add(newId);
        }

        for (String id : applications) {
            CancelApplicationRequest request = CancelApplicationRequest.newBuilder()
                    .setApplicationId(id)
                    .build();
            CancelApplicationResponse response = stub.cancel(request);
            Assertions.assertTrue(response.getSuccess());
        }
    }
}
