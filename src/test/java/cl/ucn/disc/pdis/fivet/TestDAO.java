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

import cl.ucn.disc.pdis.fivet.model.BaseEntity;
import cl.ucn.disc.pdis.fivet.orm.DAO;
import cl.ucn.disc.pdis.fivet.orm.ORMLiteDAO;
import cl.ucn.disc.pdis.fivet.orm.ZonedDateTimeType;
import com.j256.ormlite.field.DataPersisterManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.ZonedDateTime;

/**
 * Testing the DAO.
 *
 * @author Diego Urrutia-Astorga.
 */
@Slf4j
public final class TestDAO {

    /**
     * The Main DAO.
     */
    @SneakyThrows
    @Test
    @DisplayName("The Main Test")
    void theMain() {
        log.debug("Starting the MainTest ..");

        log.debug("Registering the ZonedDateTimeType ..");
        DataPersisterManager.registerDataPersisters(ZonedDateTimeType.INSTANCE);

        // The driver connection
        String databaseUrl = "jdbc:h2:mem:fivet";
        // String databaseUrl = "jdbc:postgresql:fivet";
        // String databaseUrl = "jdbc:sqlite:fivet.db";
        // String databaseUrl = "jdbc:mysql://localhost:3306/fivet";

        log.debug("Building the Connection, using: {}", databaseUrl);
        // Build the Connection with auto close (clean up)
        @Cleanup
        ConnectionSource cs = new JdbcConnectionSource(databaseUrl);

        log.debug("Dropping the tables..");
        // Drop the dabase
        TableUtils.dropTable(cs, TheEntity.class, true);

        log.debug("Creating the tables ..");
        // Create the database
        TableUtils.createTable(cs, TheEntity.class);

        log.debug("Building the ORMLiteDAO ..");
        // The dao
        DAO<TheEntity> dao = new ORMLiteDAO<>(cs, TheEntity.class);

        // Create ..
        log.debug("Creating entities ..");
        {
            TheEntity theEntityA = TheEntity.builder()
                    .theString("The String A")
                    .theInteger(128)
                    .theDouble(128.0)
                    .theBoolean(Boolean.TRUE)
                    .build();
            dao.save(theEntityA);
            log.debug("To db: {}", ToStringBuilder.reflectionToString(theEntityA, ToStringStyle.MULTI_LINE_STYLE));

            TheEntity theEntityB = TheEntity.builder()
                    .theString("The String B")
                    .theInteger(1024)
                    .theDouble(1024.0)
                    .theBoolean(Boolean.FALSE)
                    .build();
            dao.save(theEntityB);
            log.debug("To db: {}", ToStringBuilder.reflectionToString(theEntityB, ToStringStyle.MULTI_LINE_STYLE));
        }

        // Retrieve ..
        log.debug("Retrieving ..");
        {
            TheEntity theEntity = dao.get(1).orElseThrow();
            log.debug("from db: {}", ToStringBuilder.reflectionToString(theEntity, ToStringStyle.MULTI_LINE_STYLE));
        }

        // Fake delete ..
        log.debug("Deleting ..");
        {
            dao.delete(1);
            Assertions.assertThrows(SQLException.class, () -> dao.delete(10));
        }

        // Retrieve ..
        log.debug("Retrieving deleted entities ..");
        {
            Assertions.assertTrue(dao.get(1).isEmpty(), "DAO 1 was not null");
            Assertions.assertTrue(dao.get(0).isEmpty(), "DAO 1 was not null");
        }

        // Drop the dabase
        TableUtils.dropTable(cs, TheEntity.class, true);

        log.debug("Done.");
    }

    /**
     * Inner Entity Class.
     */
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @DatabaseTable(tableName = "the_entity")
    private static class TheEntity extends BaseEntity {

        /**
         * The Name
         */
        @Getter
        @DatabaseField(canBeNull = false, unique = true)
        private String theString;

        /**
         * The ZonedDateTime
         */
        @Getter
        @DatabaseField(canBeNull = false)
        private final ZonedDateTime theDate = ZonedDateTime.now();

        /**
         * The Integer.
         */
        @Getter
        @DatabaseField(canBeNull = false)
        private Integer theInteger;

        /**
         * The Double.
         */
        @Getter
        @DatabaseField(canBeNull = false)
        private Double theDouble;

        /**
         * The Boolean.
         */
        @Getter
        @DatabaseField(canBeNull = false)
        private Boolean theBoolean;

    }

}
