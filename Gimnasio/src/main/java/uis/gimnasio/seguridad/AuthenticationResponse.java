package uis.gimnasio.seguridad; // O uis.gimnasio.controlador

public class AuthenticationResponse {

    // 🚨 1. Campo de token (DEBE llamarse 'jwt' para tu JS)
    private String jwt;

    // 2. Constructor
    public AuthenticationResponse(String jwt) {
        this.jwt = jwt;
    }

    // 🚨 3. ¡EL GETTER ES OBLIGATORIO! Spring lo usa para crear el JSON.
    public String getJwt() {
        return jwt;
    }
    public void setJwt(String jwt) { this.jwt = jwt; } // 🚨 AÑADE el Setter si no lo tenías
}
