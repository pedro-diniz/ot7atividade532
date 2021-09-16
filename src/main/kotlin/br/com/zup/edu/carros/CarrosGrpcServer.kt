package br.com.zup.edu.carros

import br.com.zup.edu.Carros532Request
import br.com.zup.edu.Carros532Response
import br.com.zup.edu.Carros532ServiceGrpc
import io.grpc.stub.StreamObserver
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

@Singleton
class CarrosGrpcServer(val carroRepository: CarroRepository) : Carros532ServiceGrpc.Carros532ServiceImplBase() {

    private val logger = LoggerFactory.getLogger(CarrosGrpcServer::class.java)

    override fun cadastra(request: Carros532Request?, responseObserver: StreamObserver<Carros532Response>?) {

        logger.info("Cadastrando o carro $request")

        val carro = Carro(request!!.placa, request.modelo)
        carroRepository.save(carro)

        val response = Carros532Response.newBuilder()
            .setId(carro.id!!)
            .build()

        logger.info("Carro salvo. Id: ${carro.id}")

        responseObserver!!.onNext(response)
        responseObserver.onCompleted()

    }

}

// devia ter feito essa extension function, mas não fiz :(
fun Carros532Request.toModel() : Carro {
    return Carro(
        placa = this.placa,
        modelo = this.modelo
    )
}