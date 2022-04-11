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

import cl.ucn.disc.pdis.fivet.model.DAO;
import cl.ucn.disc.pdis.fivet.model.Persona;
import cl.ucn.disc.pdis.fivet.orm.ORMLiteDAO;
import com.j256.ormlite.field.DataPersisterManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * The Main.
 *
 * @author Diego Urrutia-Astorga.
 */
@Slf4j
public final class Main {

    /**
     * The Main.
     *
     * @param args to use.
     */
    @SneakyThrows(Exception.class)
    public static void main(String[] args) {

        log.debug("Starting Main ..");

        // Register ZonedDateTimeType
        DataPersisterManager.registerDataPersisters(ORMLiteDAO.ZonedDateTimeType.INSTANCE);

        // H2 database
        String databaseUrl = "jdbc:h2:mem:fivet";
        // String databaseUrl = "jdbc:sqlite:fivet.db";

        // Build the Connection with auto close (clean up)
        @Cleanup
        JdbcConnectionSource cs = new JdbcConnectionSource(databaseUrl);

        // Create the database
        TableUtils.createTable(cs, Persona.class);

        // The dao
        DAO<Persona> daoPersona = new ORMLiteDAO<>(cs, Persona.class);

        // Create a Persona
        {
            daoPersona.save(Persona.builder()
                    .rut("130144918")
                    .nombre("Diego Urrutia Astorga")
                    .email("durrutia@ucn.cl")
                    .build());
        }

        // Find the Persona
        {
            Persona persona = daoPersona.get(1).orElseThrow();
        }

        // Delete the Persona
        {
            daoPersona.delete(1);
        }

        log.debug("Done.");
    }

}
