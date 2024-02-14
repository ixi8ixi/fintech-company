package com.academy.fintech.pe.core.service.agreement;

import com.academy.fintech.pe.core.service.agreement.db.product.Product;
import com.academy.fintech.pe.core.service.agreement.db.product.ProductService;
import com.academy.fintech.pe.core.service.agreement.operation.result.OperationResult;
import com.academy.fintech.pe.grpc.agreement.v1.dto.AgreementDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ValidationService {
    private final ProductService productService;

    /**
     * Checks that the specified product exists and the credit parameters fit within its constraints.
     */
    public OperationResult<Boolean> checkAgreement(AgreementDto agreementDto) {
        String productCode = agreementDto.getProductId();
        Optional<Product> productCandidate = productService.findById(productCode);
        if (productCandidate.isEmpty()) {
            return OperationResult.of(false, "NO SUCH PRODUCT");
        }

        Product product = productCandidate.get();
        StringBuilder sb = new StringBuilder();
        checkAndMessage(product.getLoanTermMin(), agreementDto.getLoanTerm(),
                product.getLoanTermMax(), sb, "LOAN TERM OUT OF BOUNDS,");
        checkAndMessage(product.getPrincipalAmountMin(), agreementDto.getPrincipalAmount(),
                product.getPrincipalAmountMax(), sb, "PRINCIPAL AMOUNT OUT OF BOUNDS,");
        checkAndMessage(product.getInterestMin(), agreementDto.getInterest(),
                product.getInterestMax(), sb, "INTEREST OUT OF BOUNDS,");
        checkAndMessage(product.getOriginationAmountMin(), agreementDto.getOriginationAmount(),
                product.getOriginationAmountMax(), sb, "ORIGINATION AMOUNT OUT OF BOUNDS,");

        return sb.isEmpty()
                ? OperationResult.of(true)
                : OperationResult.of(false, sb.toString());
    }

    private static <T extends Comparable<T>> void checkAndMessage(
            T left, T value, T right, StringBuilder sb, String message) {
        if (!between(left, value, right)) {
            sb.append(message);
        }
    }

    private static <T extends Comparable<T>> boolean between(T lowerBound, T checkValue, T upperBound) {
        return checkValue.compareTo(lowerBound) >= 0
                && checkValue.compareTo(upperBound) <= 0;
    }
}
