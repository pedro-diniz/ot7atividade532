package br.com.zup.edu.carros

import br.com.zup.edu.Carros532Request
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Entity
class Carro(
    @field:NotBlank val placa: String,
    @field:NotBlank @field:Size(max=42) val modelo: String
    ) {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

}