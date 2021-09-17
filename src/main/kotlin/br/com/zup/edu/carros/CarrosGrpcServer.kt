package br.com.zup.edu.carros

import br.com.zup.edu.Carros532Request
import br.com.zup.edu.Carros532Response
import br.com.zup.edu.Carros532ServiceGrpc
import com.google.protobuf.Any
import com.google.rpc.BadRequest
import com.google.rpc.Code
import io.grpc.Status
import io.grpc.protobuf.StatusProto
import io.grpc.stub.StreamObserver
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import javax.validation.ConstraintViolationException

@Singleton
class CarrosGrpcServer(val carroRepository: CarroRepository) : Carros532ServiceGrpc.Carros532ServiceImplBase() {

    private val logger = LoggerFactory.getLogger(CarrosGrpcServer::class.java)

    override fun cadastra(request: Carros532Request?, responseObserver: StreamObserver<Carros532Response>?) {

        logger.info("Cadastrando o carro $request")

        if (carroRepository.existsByPlaca(request!!.placa)) {
            // tratamento de erro #1 ---- não funciona
//            throw CarroExistenteException("carro com placa existente")

            // fazendo funcionar
            val e = Status.ALREADY_EXISTS
                .withDescription("carro com placa existente")
                .asRuntimeException()
            responseObserver!!.onError(e)

        }

        try {

            val carro = Carro(request.placa, request.modelo)
            carroRepository.save(carro)

            val response = Carros532Response.newBuilder()
                .setId(carro.id!!)
                .build()

            logger.info("Carro salvo. Id: ${carro.id}")

            responseObserver!!.onNext(response)
            responseObserver.onCompleted()

        }
        catch (e: ConstraintViolationException) {
            // tratamento de erro #2 ---- não funciona
//            e.printStackTrace()
//            responseObserver!!.onError(e)
//            return;

            // fazendo funcionar
//            val e = Status.INVALID_ARGUMENT
//                .augmentDescription("Modelo e placa não devem ser nulos. Placa deve estar no padrão Mercosul.")
//                .asRuntimeException()
//            responseObserver!!.onError(e)

            // solucão da proposta, pegando os erros da bean validation
            val badRequest = BadRequest.newBuilder()
                .addAllFieldViolations(e.constraintViolations.map {
                    BadRequest.FieldViolation.newBuilder()
                        .setField(it.propertyPath.last().name)
                        .setDescription(it.message)
                        .build()
                }).build()

            val statusProto = com.google.rpc.Status.newBuilder()
                .setCode(Code.INVALID_ARGUMENT_VALUE)
                .setMessage("dados de entrada inválidos")
                .addDetails(Any.pack(badRequest))
                .build()

            val exception = StatusProto.toStatusRuntimeException(statusProto)
            responseObserver!!.onError(exception)

        }
        catch (e: Exception) {
            // tratamento de erro #3 ---- não funciona
//            throw StatusRuntimeException(Status.INTERNAL
//                .withDescription("erro interno inesperado")
//                .withCause(e))

            // fazendo funcionar
            val ex = Status.INTERNAL
                .withDescription("erro interno inesperado")
                .withCause(e)
                .asRuntimeException()
            responseObserver!!.onError(ex)
        }

    }

}

// devia ter feito essa extension function, mas não fiz :(
fun Carros532Request.toModel() : Carro {
    return Carro(
        placa = this.placa,
        modelo = this.modelo
    )
}