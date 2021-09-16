package br.com.zup.edu.carros

import br.com.zup.edu.Carros532Request
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class Carro(val placa: String, val modelo: String) {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

}