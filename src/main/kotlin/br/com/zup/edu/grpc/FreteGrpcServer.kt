package br.com.zup.edu.grpc

import br.com.zup.edu.CalculaFreteRequest
import br.com.zup.edu.CalculaFreteResponse
import br.com.zup.edu.ErrorDetails
import br.com.zup.edu.FretesServiceGrpc
import com.google.protobuf.Any
import com.google.rpc.Code
import io.grpc.Status
import io.grpc.protobuf.StatusProto
import io.grpc.stub.StreamObserver
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import java.lang.IllegalStateException
import kotlin.random.Random

@Singleton
class FretesGrpcServer : FretesServiceGrpc.FretesServiceImplBase() {

    private val logger = LoggerFactory.getLogger(FretesGrpcServer::class.java)

    override fun calculaFrete(request: CalculaFreteRequest?, responseObserver: StreamObserver<CalculaFreteResponse>?) {

        logger.info("Calculando valor do frete para: $request")

        val cep = request?.cep
        if (cep == null || cep.isBlank()) {
            val e = Status.INVALID_ARGUMENT
                .withDescription("CEP deve ser informado!")
                .asRuntimeException()

            responseObserver?.onError(e)
        }

        if (!cep!!.matches("[0-9]{5}-[0-9]{3}".toRegex())) {
            val e = Status.INVALID_ARGUMENT
                .withDescription("CEP Invalido")
                .augmentDescription("Formato esperado xxxxx-xxx")
                .asRuntimeException()

            responseObserver?.onError(e)
        }

        var valor = 0.0
        try {
            valor = Random.nextDouble(from = 0.1, until = 140.0)
            if (valor > 100.0) {
                throw IllegalStateException("Erro inesperado ao executar logica de negocio!")
            }
        } catch (e: Exception) {
            responseObserver?.onError(Status.INTERNAL
                .withDescription(e.message)
                .withCause(e)
                .asRuntimeException()
            )
        }

        //Simulação: verificação de segurança
        if (cep.endsWith("333")) {

            val statusProto = com.google.rpc.Status.newBuilder()
                .setCode(Code.PERMISSION_DENIED.number)
                .setMessage("Usuario não pode acessar esse recurso")
                .addDetails(Any.pack(ErrorDetails.newBuilder()
                    .setCode(401)
                    .setMessage("token experiado")
                    .build()
                ))
                .build()

            val e = StatusProto.toStatusRuntimeException(statusProto)
            responseObserver?.onError(e)
        }

        val response = CalculaFreteResponse
            .newBuilder()
            .setCep(request!!.cep)
            .setValor(valor)
            .build()

        logger.info("Valor do frete calculado: ${response.valor}")

        responseObserver!!.onNext(response)
        responseObserver.onCompleted()
    }
}