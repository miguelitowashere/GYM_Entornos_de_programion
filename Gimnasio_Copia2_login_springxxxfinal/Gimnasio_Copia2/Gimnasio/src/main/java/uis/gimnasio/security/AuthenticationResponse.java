package uis.gimnasio.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor // <-- ¡CRÍTICO!
public class AuthenticationResponse {

    // Asegúrate de que NO tenga la palabra 'final'
    private String jwt;

}