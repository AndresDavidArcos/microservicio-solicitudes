### Microservicio de Solicitudes
**URL Base:** `http://localhost:8082`

#### 1. Registrar una nueva solicitud de préstamo
Registra una nueva solicitud de préstamo para un cliente existente.

* **Endpoint:** `POST /api/v1/solicitud`
* **Método:** `POST`

**Parámetros del Body (Request Body)**

Se debe enviar un objeto JSON con la siguiente estructura:
```json
{
    "documentoIdentidadCliente": "123456789",
    "monto": 5000000,
    "plazoEnMeses": 24,
    "tipoPrestamoId": 1
}
```
| Campo | Tipo | Descripción | Obligatorio |
| :--- | :--- | :--- | :--- |
| `documentoIdentidadCliente` | String | Número de identificación del cliente que realiza la solicitud. | Sí |
| `monto` | Number | El monto del préstamo solicitado. Debe ser mayor a 0. | Sí |
| `plazoEnMeses` | Integer | El número de meses para pagar el préstamo. Debe ser mayor a 0. | Sí |
| `tipoPrestamoId` | Long | El ID del tipo de préstamo que se está solicitando. | Sí |

**Posibles Salidas (Responses)**

* **`201 Created`**: La solicitud fue registrada exitosamente.
    * **Cuerpo:** Un objeto JSON con los datos de la solicitud creada, incluyendo su `id` y su `estado` inicial ("Pendiente de revisión").
* **`400 Bad Request`**: Los datos enviados son inválidos o no cumplen las reglas de negocio.
    * **Cuerpo:** Un mensaje de error describiendo el problema (ej: "El cliente no se encuentra registrado.", "El tipo de préstamo seleccionado no es válido.", "Documento, monto, plazo y tipo de préstamo son obligatorios y deben ser válidos.").
