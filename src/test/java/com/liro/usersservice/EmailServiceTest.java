package com.liro.usersservice;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EmailServiceTest {

    @Test
    public void testHtmlFileReading() {
        try {
            // Cargar el archivo HTML
            ClassPathResource resource = new ClassPathResource("templates/emailContent.html");
            StringBuilder content = new StringBuilder();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }

            // Asegurarse de que el contenido no sea nulo o vacío
            assertNotNull(content.toString(), "El contenido no debería ser nulo");
            assertTrue(content.length() > 0, "El contenido debería tener texto");

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al leer el archivo HTML: " + e.getMessage());
        }
    }
}
