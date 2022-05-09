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

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

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
        log.debug("Starting Main ..{}", 1 << 12);

        String thePassword = "This is my password";

        StopWatch sw = StopWatch.createStarted();
        {
            // String hash = BCrypt.hashpw(thePassword, BCrypt.gensalt(13));
            // log.debug("Hash: {}", hash);

            PasswordEncoder pe = new Argon2PasswordEncoder();
            String hash = pe.encode(thePassword);
            log.debug("Hash: {}", hash);
        }

        log.debug("Time: {}", sw.formatTime());

        log.debug("Done.");
    }

}
