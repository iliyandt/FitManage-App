package demos.springdata.fitmanage.service.impl;

import com.onlinepayments.ClientInterface;
import com.onlinepayments.Factory;
import com.onlinepayments.domain.*;
import com.onlinepayments.merchant.MerchantClientInterface;
import demos.springdata.fitmanage.domain.dto.payment.CheckoutResponse;
import demos.springdata.fitmanage.domain.dto.payment.TerminalPaymentResponse;
import demos.springdata.fitmanage.service.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Objects;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final MerchantClientInterface merchantClient;

    public PaymentServiceImpl(
            @Value("${worldline.api.key}") String apiKey,
            @Value("${worldline.api.secret}") String apiSecret,
            @Value("${worldline.pspid}") String pspid
    ) throws Exception {

        URI propertiesUri = Objects.requireNonNull(
                getClass().getClassLoader().getResource("paymentprovider.properties")
        ).toURI();

        ClientInterface client = Factory.createClient(propertiesUri, apiKey, apiSecret);
        this.merchantClient = client.merchant(pspid);
    }


    @Override
    public CheckoutResponse startHostedCheckout(Long amount, String currency) {
        try {
            CreateHostedCheckoutRequest request = new CreateHostedCheckoutRequest()
                    .withOrder(new Order()
                            .withAmountOfMoney(new AmountOfMoney()
                                    .withAmount(amount)
                                    .withCurrencyCode(currency)))
                    .withHostedCheckoutSpecificInput(
                            new HostedCheckoutSpecificInput()
                                    .withReturnUrl("https://https:/dam-il.netlify.app/payment-success")
                    );

            CreateHostedCheckoutResponse response =
                    merchantClient.hostedCheckout().createHostedCheckout(request);

            return new CheckoutResponse(
                    response.getRedirectUrl(),
                    response.getHostedCheckoutId()
            );
        } catch (Exception e) {
            throw new RuntimeException("Payment creation failed", e);
        }
    }

    @Override
    public String checkPaymentStatus(String hostedCheckoutId) {
        try {
            GetHostedCheckoutResponse statusResponse = merchantClient.hostedCheckout()
                    .getHostedCheckout(hostedCheckoutId);

            if (statusResponse.getCreatedPaymentOutput() == null ||
                    statusResponse.getCreatedPaymentOutput().getPayment() == null) {
                return "PENDING";
            }

            return statusResponse.getCreatedPaymentOutput().getPayment().getStatus();

        } catch (Exception e) {
            throw new RuntimeException("Failed to check payment status", e);
        }
    }

    @Override
    public TerminalPaymentResponse startTerminalPayment(Long amount, String currency) {
        //todo
        return null;
    }

    @Override
    public String checkTerminalPaymentStatus(String transactionId) {
        //todo
        return "";
    }
}
