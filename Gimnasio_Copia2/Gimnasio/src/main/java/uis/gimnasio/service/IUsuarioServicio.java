package uis.gimnasio.service;

import uis.gimnasio.modelo.Usuario;

import java.util.List;

/**
 * Interfaz que define los servicios para la gestión de usuarios.
 */
public interface IUsuarioServicio {

    /**
     * Obtiene la lista de todos los usuarios.
     * @return Lista de objetos Usuario.
     */
    List<Usuario> getUsuario();

    /**
     * Registra un nuevo usuario en el sistema.
     * @param usuario Objeto Usuario con los datos a registrar.
     * @return El usuario registrado.
     */
    Usuario nuevoUsuario(Usuario usuario);

    /**
     * Busca un usuario por su ID.
     * @param id Identificador único del usuario.
     * @return El usuario encontrado o null si no existe.
     */
    Usuario buscarUsuario(Long id);

    /**
     * Elimina un usuario por su ID.
     * @param id Identificador único del usuario.
     * @return true si se eliminó con éxito, false de lo contrario.
     */
    boolean borrarUsuario(Long id);

    /**
     * Busca un usuario por su correo electrónico.
     * @param email Correo electrónico del usuario.
     * @return El usuario encontrado o null si no existe.
     */
    Usuario buscarUsuarioPorEmail(String email);
}