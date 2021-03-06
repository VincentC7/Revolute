package fr.miage.choquert.boundary;

import java.math.BigDecimal;

import fr.miage.choquert.entity.PaymentResponseBean;
import fr.miage.choquert.entity.PaymentInput;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.RoundRobinLoadBalancer;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;

import javax.validation.Valid;

@RestController
public class MerchantController {

	RestTemplate template;
	LoadBalancerClientFactory clientFactory;

	public MerchantController(RestTemplate rt, LoadBalancerClientFactory lbcf) {
		this.template = rt;
		this.clientFactory = lbcf;
	}


    @CircuitBreaker(name = "Merchant-service", fallbackMethod = "fallbackBankCall")
    @Retry(name = "fallbackBank", fallbackMethod = "fallbackBankCall")
    @PostMapping("/pay")
    public PaymentResponseBean bankCallPOST(@RequestBody @Valid PaymentInput paymentInput) {
        RoundRobinLoadBalancer lb = clientFactory.getInstance("Banque-service", RoundRobinLoadBalancer.class);
        ServiceInstance instance = lb.choose().block().getServer();
        String url = "http://" + instance.getHost() + ":" + instance.getPort() + "/pay";
        PaymentResponseBean response = template.postForObject(url, paymentInput, PaymentResponseBean.class);
        return PaymentResponseBean.builder()
                .message(response.getMessage())
				.ammout(response.getAmmout())
				.currency(response.getCurrency())
                .port(response.getPort())
                .build();
    }

	private PaymentResponseBean fallbackBankCall(RuntimeException re){
		return PaymentResponseBean.builder()
				.message("Unable to access the service")
				.ammout(new BigDecimal(0))
				.build();
	}

}