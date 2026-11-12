package com.driveeasy.locationservice.client;

import com.driveeasy.locationservice.model.ClientDTO;
import com.driveeasy.locationservice.model.EligibiliteDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface ClientClient {

    @GetExchange("/clients/{id}")
    ClientDTO getClient(@PathVariable Long id);

    @GetExchange("/clients/{id}/eligibilite")
    EligibiliteDTO verifierEligibilite(@PathVariable Long id);
}