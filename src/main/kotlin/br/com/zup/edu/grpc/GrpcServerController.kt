package br.com.zup.edu.grpc

import io.micronaut.grpc.server.GrpcEmbeddedServer
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import jakarta.inject.Inject

@Controller
class GrpcServerController(@Inject val grpcServer: GrpcEmbeddedServer) {

    /**
     * Simula a queda do servidor
     * */
    @Get("grpc-server/stop")
    fun stop(): HttpResponse<String> {
        grpcServer.stop()
        return HttpResponse.ok("is-running? ${grpcServer.isRunning}")
    }

}