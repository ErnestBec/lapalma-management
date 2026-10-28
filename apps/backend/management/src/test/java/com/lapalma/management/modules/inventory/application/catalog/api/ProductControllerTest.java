package com.lapalma.management.modules.inventory.application.catalog.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import javax.print.attribute.standard.Media;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.lapalma.management.catalog.api.ProductResponse;
import com.lapalma.management.catalog.api.ProductController;
import com.lapalma.management.catalog.api.CreateProductRequest;
import com.lapalma.management.modules.inventory.application.ProductService;
import com.lapalma.management.shared.exception.GlobalExceptionHandler;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//Indica que se usara ProductController
@WebMvcTest(ProductController.class)
//Indica que usara el manejador global de Spring 
@Import(GlobalExceptionHandler.class) 
public class ProductControllerTest {

    @Autowired 
    private MockMvc mockMvc;

    //Se usa para que Spring reconozca como controlador dentro de su propio contexto y con @MocktoBean se registra alli un servicio simulado para que se inyecte al controlador
    @MockitoBean
    private ProductService productService;

    @Test 
    void shouldCreateProductSuccessfully() throws Exception{

        //Arrange
        when(productService.create(any(CreateProductRequest.class)))
            .thenReturn(new ProductResponse(
                1L,
                "Cloro",
                new BigDecimal("25.50")
            ));

        String json = """
            {
                "name": "Cloro",
                "price": 25.50
            }
            """;        
        
        //Act Assert
        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Cloro"))
            .andExpect(jsonPath("$.price").value(25.50));
        
        verify(productService).create(any(CreateProductRequest.class));

    }
    @Test 
    void shouldRejectProductWithEmptyName() throws Exception{

        String json = """
            {
                "name": "",
                "price": 25.50
            }
            """; 
        
        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON) 
                .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("Error de validación"))
            .andExpect(jsonPath("$.errors.name").value("El nombre es obligatorio"));

        verifyNoInteractions(productService);
    }

     @Test 
    void shouldRejectProductWithNegativePrice() throws Exception{

        //Arrange
        String json = """
            {
                "name": "Cloro",
                "price": -25.50
            }
            """; 
        
        //Act & Assert    
        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON) 
                .content(json))
            .andExpect(status().isBadRequest());
        
    }

    @Test 
    void shouldRejectProductWithoutPrice() throws Exception{

        //Arrange
        String  json = """
            {
                "name": "Cloro"
            }
        """;

        //Act & Assert
        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("Error de validación"))
            .andExpect(jsonPath("$.errors.price").value("El precio es obligatorio"));
            
        verifyNoInteractions(productService);
    }

    @Test 
    void shouldReturnMultipleValidationErrors() throws Exception{

        //Arrange
        String json = """
            {
                "name": "",
                "price": -10
            }
        """;

        //Act & Assert
        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors.name").value("El nombre es obligatorio"))
            .andExpect(jsonPath("$.errors.price").value("El precio debe ser mayor que cero"));

        verifyNoInteractions(productService);
    }

    @Test 
    void shouldRejectMalformedJson() throws Exception{

        //Arrange: JSON intencionalente incorrecto
        String json= """
            {
                "name": "Cloro
            }    
            """;

        //Act & Assert
        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest());

        verifyNoInteractions(productService);
    }

    @Test
    void shouldRejectProductWithZeroPrice() throws Exception {

        // Arrange
        String json = """
            {
                "name": "Detergente líquido",
                "price": 0
            }
            """;

        // Act & Assert
        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.errors.price").value("El precio debe ser mayor que cero"));

            
        verify(productService, never()).create(any(CreateProductRequest.class));
    }
}
