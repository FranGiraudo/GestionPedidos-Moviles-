package com.undef.gestionpedidos.data.mock

import com.undef.gestionpedidos.domain.model.Cliente
import com.undef.gestionpedidos.domain.model.EstadoPedido
import com.undef.gestionpedidos.domain.model.LineaPedido
import com.undef.gestionpedidos.domain.model.Pedido
import com.undef.gestionpedidos.domain.model.Producto
import java.time.LocalDate

object MockData {

    // -------------------------------------------------------------------
    // Clientes
    // -------------------------------------------------------------------

    val clientes: List<Cliente> = listOf(
        Cliente(
            id = 1,
            razonSocial = "Supermercados del Sur S.A.",
            cuit = "30-71234567-9",
            direccion = "Av. Colón 1450",
            localidad = "Córdoba",
            telefono = "351-4123456",
            email = "compras@sursa.com.ar"
        ),
        Cliente(
            id = 2,
            razonSocial = "Almacén El Progreso",
            cuit = "20-25678901-3",
            direccion = "Bv. San Juan 890",
            localidad = "Villa María",
            telefono = "353-4567890",
            email = "elprogreso@gmail.com"
        ),
        Cliente(
            id = 3,
            razonSocial = "Distribuidora Norte S.R.L.",
            cuit = "30-68901234-5",
            direccion = "Ruta 9 Km 12, Parque Industrial",
            localidad = "Río Cuarto",
            telefono = "358-4789012",
            email = "admin@distnorte.com.ar"
        ),
        Cliente(
            id = 4,
            razonSocial = "Minimarket La Esquina",
            cuit = "27-34567890-1",
            direccion = "Independencia 234",
            localidad = "Alta Gracia",
            telefono = "354-4234567",
            email = "laesquina.ag@hotmail.com"
        ),
        Cliente(
            id = 5,
            razonSocial = "Restaurante El Fogón",
            cuit = "30-78234567-4",
            direccion = "Caseros 1102",
            localidad = "Córdoba",
            telefono = "351-4901234",
            email = "administracion@elfogon.com.ar",
            activo = false
        )
    )

    // -------------------------------------------------------------------
    // Productos
    // -------------------------------------------------------------------

    val productos: List<Producto> = listOf(
        Producto(
            id = 1,
            codigo = "ACE-001",
            descripcion = "Aceite de girasol 1.5 L",
            unidadMedida = "Botella",
            precioUnitario = 1850.00,
            stockActual = 240
        ),
        Producto(
            id = 2,
            codigo = "ACE-002",
            descripcion = "Aceite de oliva extra virgen 500 ml",
            unidadMedida = "Botella",
            precioUnitario = 4200.00,
            stockActual = 80
        ),
        Producto(
            id = 3,
            codigo = "HAR-001",
            descripcion = "Harina 0000 x 1 kg",
            unidadMedida = "Bolsa",
            precioUnitario = 620.00,
            stockActual = 500
        ),
        Producto(
            id = 4,
            codigo = "AZU-001",
            descripcion = "Azucar blanco x 1 kg",
            unidadMedida = "Bolsa",
            precioUnitario = 780.00,
            stockActual = 380
        ),
        Producto(
            id = 5,
            codigo = "ARR-001",
            descripcion = "Arroz largo fino x 1 kg",
            unidadMedida = "Bolsa",
            precioUnitario = 950.00,
            stockActual = 420
        ),
        Producto(
            id = 6,
            codigo = "FID-001",
            descripcion = "Fideos spaghetti x 500 g",
            unidadMedida = "Paquete",
            precioUnitario = 510.00,
            stockActual = 600
        ),
        Producto(
            id = 7,
            codigo = "TOM-001",
            descripcion = "Tomate triturado x 400 g",
            unidadMedida = "Lata",
            precioUnitario = 430.00,
            stockActual = 350
        ),
        Producto(
            id = 8,
            codigo = "GAS-001",
            descripcion = "Gaseosa cola 2.25 L",
            unidadMedida = "Botella",
            precioUnitario = 1100.00,
            stockActual = 180,
            activo = false
        )
    )

    // -------------------------------------------------------------------
    // Pedidos
    // -------------------------------------------------------------------

    val pedidos: List<Pedido> = listOf(
        Pedido(
            id = 1,
            numeroPedido = "PED-2024-0001",
            cliente = clientes[0],
            estado = EstadoPedido.ENTREGADO,
            fechaCreacion = LocalDate.of(2024, 11, 10),
            fechaEntregaEstimada = LocalDate.of(2024, 11, 13),
            observaciones = "Entregar en deposito trasero. Solicitar firma de remito.",
            lineas = listOf(
                LineaPedido(
                    id = 1,
                    pedidoId = 1,
                    producto = productos[0],
                    cantidad = 48,
                    precioUnitario = productos[0].precioUnitario
                ),
                LineaPedido(
                    id = 2,
                    pedidoId = 1,
                    producto = productos[2],
                    cantidad = 60,
                    precioUnitario = productos[2].precioUnitario
                ),
                LineaPedido(
                    id = 3,
                    pedidoId = 1,
                    producto = productos[4],
                    cantidad = 30,
                    precioUnitario = productos[4].precioUnitario
                )
            )
        ),
        Pedido(
            id = 2,
            numeroPedido = "PED-2024-0002",
            cliente = clientes[1],
            estado = EstadoPedido.DESPACHADO,
            fechaCreacion = LocalDate.of(2024, 11, 18),
            fechaEntregaEstimada = LocalDate.of(2024, 11, 21),
            lineas = listOf(
                LineaPedido(
                    id = 4,
                    pedidoId = 2,
                    producto = productos[3],
                    cantidad = 24,
                    precioUnitario = productos[3].precioUnitario
                ),
                LineaPedido(
                    id = 5,
                    pedidoId = 2,
                    producto = productos[5],
                    cantidad = 36,
                    precioUnitario = productos[5].precioUnitario
                )
            )
        ),
        Pedido(
            id = 3,
            numeroPedido = "PED-2024-0003",
            cliente = clientes[2],
            estado = EstadoPedido.EN_PREPARACION,
            fechaCreacion = LocalDate.of(2024, 11, 20),
            fechaEntregaEstimada = LocalDate.of(2024, 11, 25),
            observaciones = "Cliente solicita factura A.",
            lineas = listOf(
                LineaPedido(
                    id = 6,
                    pedidoId = 3,
                    producto = productos[1],
                    cantidad = 12,
                    precioUnitario = productos[1].precioUnitario
                ),
                LineaPedido(
                    id = 7,
                    pedidoId = 3,
                    producto = productos[6],
                    cantidad = 48,
                    precioUnitario = productos[6].precioUnitario
                )
            )
        ),
        Pedido(
            id = 4,
            numeroPedido = "PED-2024-0004",
            cliente = clientes[3],
            estado = EstadoPedido.CONFIRMADO,
            fechaCreacion = LocalDate.of(2024, 11, 21),
            fechaEntregaEstimada = LocalDate.of(2024, 11, 26),
            lineas = listOf(
                LineaPedido(
                    id = 8,
                    pedidoId = 4,
                    producto = productos[0],
                    cantidad = 12,
                    precioUnitario = productos[0].precioUnitario
                ),
                LineaPedido(
                    id = 9,
                    pedidoId = 4,
                    producto = productos[4],
                    cantidad = 20,
                    precioUnitario = productos[4].precioUnitario
                )
            )
        ),
        Pedido(
            id = 5,
            numeroPedido = "PED-2024-0005",
            cliente = clientes[0],
            estado = EstadoPedido.BORRADOR,
            fechaCreacion = LocalDate.of(2024, 11, 22),
            fechaEntregaEstimada = LocalDate.of(2024, 11, 28),
            lineas = listOf(
                LineaPedido(
                    id = 10,
                    pedidoId = 5,
                    producto = productos[2],
                    cantidad = 100,
                    precioUnitario = productos[2].precioUnitario
                )
            )
        )
    )
}
