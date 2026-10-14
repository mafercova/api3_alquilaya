package com.api.alquilaya.dto;

import com.api.alquilaya.models.ClienteModel;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Locale;

public record ClienteRequest(
    @NotBlank @Size(max = 100) String nombre,
    @NotBlank @Size(max = 100) String apellido,
    @NotBlank @Email @Size(max = 150) String email,
    @NotBlank @Size(max = 50) String licencia,
    @NotBlank @Pattern(regexp = "^(?=(?:\\D*\\d){7,})\\+?[0-9() -]{7,20}$", message = "El teléfono no tiene un formato válido")
    String telefono
) {
    public ClienteModel toModel() {
        ClienteModel cliente = new ClienteModel();
        cliente.setNombre(nombre.trim());
        cliente.setApellido(apellido.trim());
        cliente.setEmail(email.trim().toLowerCase(Locale.ROOT));
        cliente.setLicencia(licencia.trim());
        cliente.setTelefono(telefono.trim());
        return cliente;
    }
}
