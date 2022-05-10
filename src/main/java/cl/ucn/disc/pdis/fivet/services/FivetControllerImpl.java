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

package cl.ucn.disc.pdis.fivet.services;

import cl.ucn.disc.pdis.fivet.model.Persona;
import cl.ucn.disc.pdis.fivet.orm.DAO;
import cl.ucn.disc.pdis.fivet.orm.ORMLiteDAO;
import cl.ucn.disc.pdis.fivet.orm.ZonedDateTimeType;
import com.j256.ormlite.field.DataPersisterManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

/**
 * The Controller Implementation.
 */
@Slf4j
public final class FivetControllerImpl implements FivetController {

    /**
     * The Dao.
     */
    private final DAO<Persona> thePersonaDAO;

    /**
     * The Hasher.
     */
    private final static PasswordEncoder PASSWORD_ENCODER = new Argon2PasswordEncoder();

    /**
     * The Constructor.
     *
     * @param databaseUrl to use.
     */
    @SneakyThrows
    public FivetControllerImpl(String databaseUrl, boolean initialize) {
        log.debug("Registering the ZonedDateTimeType ..");
        DataPersisterManager.registerDataPersisters(ZonedDateTimeType.INSTANCE);

        // The Connection
        log.debug("Building the Connection using : {}", databaseUrl);
        ConnectionSource cs = new JdbcConnectionSource(databaseUrl);

        if (initialize) {
            log.debug("Dropping the tables..");
            // Drop the dabase
            TableUtils.dropTable(cs, Persona.class, true);

            log.debug("Creating the tables ..");
            // Create the database
            TableUtils.createTable(cs, Persona.class);
        }

        // The Dao
        log.debug("Build the ORMLiteDAO ..");
        this.thePersonaDAO = new ORMLiteDAO<>(cs, Persona.class);
    }

    /**
     * @param login to use (rut or email).
     * @return the optional Persona.
     */
    @Override
    public Optional<Persona> retrieveByLogin(String login) {
        Optional<Persona> oPersona = this.thePersonaDAO.get("rut", login);

        if (oPersona.isPresent()) {
            return oPersona;
        }
        return this.thePersonaDAO.get("email", login);
    }

    /**
     * Retrieve a Persona from login + password.
     *
     * @param login    to use (rut or email).
     * @param password to check.
     * @return the Persona.
     */
    @Override
    public Optional<Persona> authenticate(String login, String password) {
        Optional<Persona> oPersona = this.retrieveByLogin(login);

        // Empty -> get out of here!
        if (oPersona.isEmpty()) {
            log.warn("Persona with login <{}> not found.", login);
            return Optional.empty();
        }

        // Wrong password -> get out of there!
        if (!PASSWORD_ENCODER.matches(password, oPersona.get().getPassword())) {
            log.warn("Persona with login={} use a wrong password", login);
            return Optional.empty();
        }

        // All ok!
        return oPersona;
    }

    /**
     * Save a Persona into the backend.
     *
     * @param persona  to save.
     * @param password to hash.
     */
    @Override
    public void add(@NonNull Persona persona, @NonNull String password) {
        // Hash password
        persona.setPassword(PASSWORD_ENCODER.encode(password));
        // Save the persona
        this.thePersonaDAO.save(persona);
    }

    /**
     * Delete a Persona from the Backend.
     *
     * @param idPersona to delete.
     */
    @Override
    public void delete(Integer idPersona) {
        this.thePersonaDAO.delete(idPersona);
    }
}
