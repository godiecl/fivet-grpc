/*
 * Copyright (c) 2022 Diego Urrutia-Astorga.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 *
 */

package cl.ucn.disc.pdis.fivet;

import cl.ucn.disc.pdis.fivet.model.Persona;
import cl.ucn.disc.pdis.fivet.services.FivetController;
import cl.ucn.disc.pdis.fivet.services.FivetControllerImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Testing the Service.
 *
 * @author Diego Urrutia-Astorga.
 */
@Slf4j
public final class TestService {

    /**
     * The Service test.
     */
    @Test
    @DisplayName("The Service Test")
    void theMain() {
        log.debug("Starting the test");

        FivetController controller = new FivetControllerImpl("jdbc:sqlite:fivet.db", true);

        // Save the persona
        {
            Persona persona = Persona.builder()
                    .rut("130144918")
                    .nombre("Diego Urrutia-Astorga")
                    .email("durrutia@ucn.cl")
                    .direccion("Angamos #0610")
                    .build();

            controller.add(persona, "durrutia123");
        }

        // Authenticate
        {
            controller.authenticate("durrutia@ucn.cl", "durrutia123").orElseThrow();
            controller.authenticate("130144918", "durrutia123").orElseThrow();
        }

        // Retrieve by email
        {
            log.debug("Retrieving ..");
            Persona persona = controller.retrieveByLogin("durrutia@ucn.cl").orElseThrow();
            log.debug("Persona: {}", ToStringBuilder.reflectionToString(persona,
                    ToStringStyle.JSON_STYLE));
        }

        // Retrieve by rut
        {
            log.debug("Retrieving ..");
            Persona persona = controller.retrieveByLogin("130144918").orElseThrow();
            log.debug("Persona: {}", ToStringBuilder.reflectionToString(persona,
                    ToStringStyle.JSON_STYLE));
        }

        // Delete
        {
            log.debug("Deleting ..");
            Persona persona = controller.retrieveByLogin("durrutia@ucn.cl").orElseThrow();
            controller.delete(persona.getId());
        }

        // Retrieve
        {
            log.debug("Retrieving ..");
            Persona persona = controller.retrieveByLogin("durrutia@ucn.cl").orElse(null);
            if (persona != null) {
                log.debug("Persona: {}", ToStringBuilder.reflectionToString(persona, ToStringStyle.JSON_STYLE));
                Assertions.fail("Persona was != null !");
            }
        }

        log.debug("Done.");
    }

}
