package controller;

import model.Mascota;
import dao.MascotaDao;
import util.ValidacionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

@WebServlet("/MascotaServlet")
public class MascotaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private MascotaDao mascotaDao;

    @Override
    public void init() {
        mascotaDao = new MascotaDao();
    }

    // ========================================================================
    // MÉTODO GET - Listar mascotas
    // ========================================================================
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Verificar que sea Cliente
        if (!esCliente(request, response)) {
            return;
        }

        String action = request.getParameter("action");

        if (action == null) {
            action = "listar";
        }

        switch (action) {
            case "listar":
                listarMascotas(request, response);
                break;
            case "ver":
                verDetalleMascota(request, response);
                break;
            default:
                listarMascotas(request, response);
                break;
        }
    }

    // ========================================================================
    // MÉTODO POST - Crear, actualizar, eliminar mascotas
    // ========================================================================
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Verificar que sea Cliente
        if (!esCliente(request, response)) {
            return;
        }

        String action = request.getParameter("action");

        if (action == null) {
            response.sendRedirect("MascotaServlet?action=listar");
            return;
        }

        switch (action) {
            case "crear":
                crearMascota(request, response);
                break;
            case "actualizar":
                actualizarMascota(request, response);
                break;
            case "eliminar":
                eliminarMascota(request, response);
                break;
            default:
                response.sendRedirect("MascotaServlet?action=listar");
                break;
        }
    }

    // ========================================================================
    // LISTAR MASCOTAS DEL CLIENTE
    // ========================================================================
    private void listarMascotas(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");

        List<Mascota> mascotas = mascotaDao.listarMascotasCliente(userId);

        request.setAttribute("mascotas", mascotas);
        request.getRequestDispatcher("misMascotas.jsp").forward(request, response);
    }

    // ========================================================================
    // VER DETALLE DE MASCOTA
    // ========================================================================
    private void verDetalleMascota(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            HttpSession session = request.getSession();
            Integer userId = (Integer) session.getAttribute("userId");

            int idMascota = Integer.parseInt(request.getParameter("id"));

            // Verificar que la mascota pertenezca al usuario
            if (!mascotaDao.mascotaPerteneceAUsuario(idMascota, userId)) {
                request.setAttribute("error", "No tienes permiso para ver esta mascota");
                listarMascotas(request, response);
                return;
            }

            Mascota mascota = mascotaDao.obtenerPorId(idMascota);

            if (mascota == null) {
                request.setAttribute("error", "Mascota no encontrada");
                listarMascotas(request, response);
                return;
            }

            request.setAttribute("mascota", mascota);
            request.getRequestDispatcher("detalleMascota.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID de mascota inválido");
            listarMascotas(request, response);
        }
    }

    // ========================================================================
    // CREAR NUEVA MASCOTA
    // ========================================================================
    private void crearMascota(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            HttpSession session = request.getSession();
            Integer userId = (Integer) session.getAttribute("userId");

            // Validar sesión
            if (userId == null) {
                request.setAttribute("error", "Sesión inválida");
                response.sendRedirect("login.jsp");
                return;
            }

            // Obtener datos del formulario
            String nombre = request.getParameter("nombre");
            String especie = request.getParameter("especie");
            String raza = request.getParameter("raza");
            String sexo = request.getParameter("sexo");
            String fechaNacStr = request.getParameter("fechaNacimiento");

            // Preservar datos del formulario
            request.setAttribute("formNombre", nombre);
            request.setAttribute("formEspecie", especie);
            request.setAttribute("formRaza", raza);
            request.setAttribute("formSexo", sexo);
            request.setAttribute("formFechaNacimiento", fechaNacStr);
            request.setAttribute("mostrarModal", "crear");

            // Validar campos obligatorios
            if (!ValidacionUtil.noEstaVacio(nombre)) {
                request.setAttribute("error", "El nombre de la mascota es requerido");
                listarMascotas(request, response);
                return;
            }

            if (!ValidacionUtil.esTextoValido(nombre, 2, 50)) {
                request.setAttribute("error", "El nombre debe tener entre 2 y 50 caracteres");
                listarMascotas(request, response);
                return;
            }

            if (!ValidacionUtil.esNombreValido(nombre)) {
                request.setAttribute("error", "El nombre solo debe contener letras y espacios");
                listarMascotas(request, response);
                return;
            }

            if (!ValidacionUtil.noEstaVacio(especie)) {
                request.setAttribute("error", "La especie es requerida");
                listarMascotas(request, response);
                return;
            }

            if (!ValidacionUtil.esTextoValido(especie, 2, 50)) {
                request.setAttribute("error", "La especie debe tener entre 2 y 50 caracteres");
                listarMascotas(request, response);
                return;
            }

            // Validar sexo (ahora es obligatorio)
            if (!ValidacionUtil.noEstaVacio(sexo)) {
                request.setAttribute("error", "El sexo es obligatorio");
                listarMascotas(request, response);
                return;
            }

            if (!sexo.equals("M") && !sexo.equals("H")) {
                request.setAttribute("error", "El sexo debe ser M (Macho) o H (Hembra)");
                listarMascotas(request, response);
                return;
            }

            // Validar fecha de nacimiento si se proporciona
            if (ValidacionUtil.noEstaVacio(fechaNacStr) && !ValidacionUtil.esFechaValida(fechaNacStr)) {
                request.setAttribute("error", "Fecha de nacimiento inválida. Use formato YYYY-MM-DD");
                listarMascotas(request, response);
                return;
            }

            // Sanitizar datos
            String nombreSanitizado = ValidacionUtil.sanitizar(nombre.trim());
            String especieSanitizada = ValidacionUtil.sanitizar(especie.trim());
            String razaSanitizada = ValidacionUtil.sanitizar(raza != null ? raza.trim() : "");

            // Crear mascota
            Mascota nuevaMascota = new Mascota();
            nuevaMascota.setIdUsuarioPropietario(userId); // 🔥 Usa id_usuario directamente
            nuevaMascota.setNombre(nombreSanitizado);
            nuevaMascota.setEspecie(especieSanitizada);
            nuevaMascota.setRaza(razaSanitizada);
            nuevaMascota.setSexo(sexo);

            if (fechaNacStr != null && !fechaNacStr.trim().isEmpty()) {
                nuevaMascota.setFechaNacimiento(Date.valueOf(fechaNacStr));
            }

            boolean creado = mascotaDao.crearMascota(nuevaMascota);

            if (creado) {
                // Limpiar atributos del formulario
                request.removeAttribute("formNombre");
                request.removeAttribute("formEspecie");
                request.removeAttribute("formRaza");
                request.removeAttribute("formSexo");
                request.removeAttribute("formFechaNacimiento");
                request.removeAttribute("mostrarModal");

                request.setAttribute("success", "✅ Mascota '" + nombreSanitizado + "' registrada exitosamente");
                System.out.println("✅ Mascota creada: " + nombreSanitizado);
            } else {
                request.setAttribute("error", "Error al registrar la mascota");
                System.out.println("❌ Error al crear mascota: " + nombreSanitizado);
            }

        } catch (IllegalArgumentException e) {
            request.setAttribute("error", "Fecha de nacimiento inválida");
            e.printStackTrace();
        } catch (Exception e) {
            request.setAttribute("error", "Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }

        listarMascotas(request, response);
    }

    // ========================================================================
    // ACTUALIZAR MASCOTA
    // ========================================================================
    private void actualizarMascota(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            HttpSession session = request.getSession();
            Integer userId = (Integer) session.getAttribute("userId");

            String idMascotaStr = request.getParameter("idMascota");

            // Validar ID
            if (!ValidacionUtil.noEstaVacio(idMascotaStr)) {
                request.setAttribute("error", "ID de mascota es requerido");
                listarMascotas(request, response);
                return;
            }

            int idMascota = Integer.parseInt(idMascotaStr);

            if (!ValidacionUtil.esIdValido(idMascota)) {
                request.setAttribute("error", "ID de mascota inválido");
                listarMascotas(request, response);
                return;
            }

            // Verificar que la mascota pertenezca al usuario
            if (!mascotaDao.mascotaPerteneceAUsuario(idMascota, userId)) {
                request.setAttribute("error", "No tienes permiso para editar esta mascota");
                listarMascotas(request, response);
                return;
            }

            // Obtener datos del formulario
            String nombre = request.getParameter("nombre");
            String especie = request.getParameter("especie");
            String raza = request.getParameter("raza");
            String sexo = request.getParameter("sexo");
            String fechaNacStr = request.getParameter("fechaNacimiento");

            // Preservar datos del formulario
            request.setAttribute("formIdMascota", idMascotaStr);
            request.setAttribute("formNombre", nombre);
            request.setAttribute("formEspecie", especie);
            request.setAttribute("formRaza", raza);
            request.setAttribute("formSexo", sexo);
            request.setAttribute("formFechaNacimiento", fechaNacStr);
            request.setAttribute("mostrarModal", "editar");

            // Validar campos obligatorios
            if (!ValidacionUtil.noEstaVacio(nombre) || !ValidacionUtil.esTextoValido(nombre, 2, 50)) {
                request.setAttribute("error", "El nombre debe tener entre 2 y 50 caracteres");
                listarMascotas(request, response);
                return;
            }

            if (!ValidacionUtil.esNombreValido(nombre)) {
                request.setAttribute("error", "El nombre solo debe contener letras y espacios");
                listarMascotas(request, response);
                return;
            }

            if (!ValidacionUtil.noEstaVacio(especie) || !ValidacionUtil.esTextoValido(especie, 2, 50)) {
                request.setAttribute("error", "La especie debe tener entre 2 y 50 caracteres");
                listarMascotas(request, response);
                return;
            }

            // Validar sexo (ahora es obligatorio)
            if (!ValidacionUtil.noEstaVacio(sexo)) {
                request.setAttribute("error", "El sexo es obligatorio");
                listarMascotas(request, response);
                return;
            }

            if (!sexo.equals("M") && !sexo.equals("H")) {
                request.setAttribute("error", "El sexo debe ser M (Macho) o H (Hembra)");
                listarMascotas(request, response);
                return;
            }

            // Validar fecha
            if (ValidacionUtil.noEstaVacio(fechaNacStr) && !ValidacionUtil.esFechaValida(fechaNacStr)) {
                request.setAttribute("error", "Fecha de nacimiento inválida");
                listarMascotas(request, response);
                return;
            }

            // Sanitizar datos
            String nombreSanitizado = ValidacionUtil.sanitizar(nombre.trim());
            String especieSanitizada = ValidacionUtil.sanitizar(especie.trim());
            String razaSanitizada = ValidacionUtil.sanitizar(raza != null ? raza.trim() : "");

            // Actualizar mascota
            Mascota mascota = new Mascota();
            mascota.setIdMascota(idMascota);
            mascota.setNombre(nombreSanitizado);
            mascota.setEspecie(especieSanitizada);
            mascota.setRaza(razaSanitizada);
            mascota.setSexo(sexo);

            if (fechaNacStr != null && !fechaNacStr.trim().isEmpty()) {
                mascota.setFechaNacimiento(Date.valueOf(fechaNacStr));
            }

            boolean actualizado = mascotaDao.actualizarMascota(mascota);

            if (actualizado) {
                // Limpiar atributos del formulario
                request.removeAttribute("formIdMascota");
                request.removeAttribute("formNombre");
                request.removeAttribute("formEspecie");
                request.removeAttribute("formRaza");
                request.removeAttribute("formSexo");
                request.removeAttribute("formFechaNacimiento");
                request.removeAttribute("mostrarModal");

                request.setAttribute("success", "✅ Mascota '" + nombreSanitizado + "' actualizada exitosamente");
                System.out.println("✅ Mascota actualizada: ID " + idMascota);
            } else {
                request.setAttribute("error", "Error al actualizar la mascota");
                System.out.println("❌ Error al actualizar mascota: ID " + idMascota);
            }

        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID de mascota inválido");
            e.printStackTrace();
        } catch (Exception e) {
            request.setAttribute("error", "Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }

        listarMascotas(request, response);
    }

    // ========================================================================
    // ELIMINAR MASCOTA
    // ========================================================================
    private void eliminarMascota(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            HttpSession session = request.getSession();
            Integer userId = (Integer) session.getAttribute("userId");

            int idMascota = Integer.parseInt(request.getParameter("idMascota"));

            // Verificar que la mascota pertenezca al usuario
            if (!mascotaDao.mascotaPerteneceAUsuario(idMascota, userId)) {
                request.setAttribute("error", "No tienes permiso para eliminar esta mascota");
                listarMascotas(request, response);
                return;
            }

            boolean eliminado = mascotaDao.eliminarMascota(idMascota);

            if (eliminado) {
                request.setAttribute("success", "✅ Mascota eliminada exitosamente");
                System.out.println("✅ Mascota eliminada: ID " + idMascota);
            } else {
                request.setAttribute("error", "Error al eliminar la mascota");
                System.out.println("❌ Error al eliminar mascota: ID " + idMascota);
            }

        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID de mascota inválido");
            e.printStackTrace();
        } catch (Exception e) {
            request.setAttribute("error", "Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }

        listarMascotas(request, response);
    }

    // ========================================================================
    // VERIFICAR QUE EL USUARIO SEA CLIENTE
    // ========================================================================
    private boolean esCliente(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return false;
        }

        String rol = (String) session.getAttribute("rol");

        if (rol == null || !rol.equalsIgnoreCase("Cliente")) {
            response.sendRedirect("login.jsp");
            return false;
        }

        return true;
    }
}