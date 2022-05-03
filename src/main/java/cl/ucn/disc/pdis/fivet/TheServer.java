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

import cl.ucn.disc.pdis.fivet.grpc.Credencial;
import cl.ucn.disc.pdis.fivet.grpc.FivetServiceGrpc;
import cl.ucn.disc.pdis.fivet.grpc.Persona;
import cl.ucn.disc.pdis.fivet.services.FivetController;
import cl.ucn.disc.pdis.fivet.services.FivetControllerImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * The Server of Fivet.
 *
 * @author Diego Urrutia-Astorga.
 */
@Slf4j
public final class TheServer {

    /**
     * The Main.
     */
    @SneakyThrows
    public static void main(String[] args) {

        log.debug("Building the FivetServiceImpl ..");
        FivetServiceImpl fivetService = new FivetServiceImpl("jdbc:h2:mem:fivet");

        log.debug("Building and Starting The Server ..");

        // Build and start the server
        Server server = ServerBuilder.forPort(8080)
                .addService(fivetService)
                .build()
                .start();

        // Awaits
        server.awaitTermination();

        log.debug("Done.");

    }

    /**
     * The Fivet Implementation
     */
    @Slf4j
    private static class FivetServiceImpl extends FivetServiceGrpc.FivetServiceImplBase {

        /**
         * The Controller.
         */
        private final FivetController fivetController;

        /**
         * The FivetService.
         *
         * @param databaseUrl to use.
         */
        public FivetServiceImpl(String databaseUrl) {
            this.fivetController = new FivetControllerImpl(databaseUrl, true);
        }

        /**
         * Authenticate.
         *
         * @param request
         * @param responseObserver
         */
        @Override
        public void autenticar(Credencial request, StreamObserver<Persona> responseObserver) {
            // Retrieve from Controller
            Optional<cl.ucn.disc.pdis.fivet.model.Persona> persona = this.fivetController.retrieveByLogin(request.getLogin());
            if (persona.isPresent()) {
                // Return the observer
                responseObserver.onNext(Persona.newBuilder()
                        .setRut(persona.get().getRut())
                        .setNombre(persona.get().getNombre())
                        .setEmail(persona.get().getEmail())
                        .setDireccion(persona.get().getDireccion())
                        .build());
                responseObserver.onCompleted();
            } else {
                responseObserver.onNext(Persona.newBuilder()
                        .setRut("130144918")
                        .setNombre("Diego Urrutia Astorga")
                        .setEmail("durrutia@ucn.cl")
                        .setDireccion("angamos #0610")
                        .build());
                responseObserver.onCompleted();
                // responseObserver.onError(new RuntimeException("Persona Not Found!"));
            }
        }
    }

}
