package com.lapalma.management.shared.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//Carga solo los componentes necesarios para probar la capa web
@WebMvcTest(SystemController.class)
public class SystemControllerTest {

    //Permite que Spring nos proporcione MockMvc
    @Autowired 
    private MockMvc mockMvc;

    @Test 
    void shouldReturnNotFoundForUnknownEndpoint() throws Exception{
        mockMvc.perform(get("/api/v1/nonexistent"))
        .andExpect(status().isNotFound());
    }

    @Test 
    void shouldReturnSystemInformation() throws Exception{

        //Act & Assert
        /*
            perform -> Ejecuta la petición HTTP simulada
            get() -> Construye una solicitud Get
            andExpect() -> Comprueba que la respuesta cumpla una condición
            status().isOk() -> Verifica que el código HTTP sea 200
            jsonPath() -> Comprueba un valor dentro del JSON, donde $ representa la raíz del documento json y .status la propiedadS
        */
        mockMvc.perform(get("/api/v1/system"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(
                MediaType.APPLICATION_JSON
            ))
            .andExpect(jsonPath("$.application")
                    .value("La palma management"))
            .andExpect(jsonPath("$.status")
                    .value("running"))
            .andExpect(jsonPath("$.apiVersion")
                    .value("v1"));
            
    }
}
