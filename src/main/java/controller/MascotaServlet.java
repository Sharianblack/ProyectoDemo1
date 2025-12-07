package controller;

import model.Mascota;
import dao.MascotaDao;
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

            // Obtener datos del formulario
            String nombre = request.getParameter("nombre");
            String especie = request.getParameter("especie");
            String raza = request.getParameter("raza");
            String sexo = request.getParameter("sexo");
            String fechaNacStr = request.getParameter("fechaNacimiento");

            // Validaciones básicas
            if (nombre == null || nombre.trim().isEmpty() ||
                    especie == null || especie.trim().isEmpty()) {

                request.setAttribute("error", "Nombre y especie son obligatorios");
                listarMascotas(request, response);
                return;
            }

            // Crear mascota
            Mascota nuevaMascota = new Mascota();
            nuevaMascota.setIdUsuarioPropietario(userId); // 🔥 Usa id_usuario directamente
            nuevaMascota.setNombre(nombre.trim());
            nuevaMascota.setEspecie(especie.trim());
            nuevaMascota.setRaza(raza != null ? raza.trim() : "");
            nuevaMascota.setSexo(sexo);

            if (fechaNacStr != null && !fechaNacStr.trim().isEmpty()) {
                nuevaMascota.setFechaNacimiento(Date.valueOf(fechaNacStr));
            }

            boolean creado = mascotaDao.crearMascota(nuevaMascota);

            if (creado) {
                request.setAttribute("success", "✅ Mascota '" + nombre + "' registrada exitosamente");
                System.out.println("✅ Mascota creada: " + nombre);
            } else {
                request.setAttribute("error", "Error al registrar la mascota");
                System.out.println("❌ Error al crear mascota: " + nombre);
            }

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

            int idMascota = Integer.parseInt(request.getParameter("idMascota"));

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

            // Validaciones básicas
            if (nombre == null || nombre.trim().isEmpty() ||
                    especie == null || especie.trim().isEmpty()) {

                request.setAttribute("error", "Nombre y especie son obligatorios");
                listarMascotas(request, response);
                return;
            }

            // Actualizar mascota
            Mascota mascota = new Mascota();
            mascota.setIdMascota(idMascota);
            mascota.setNombre(nombre.trim());
            mascota.setEspecie(especie.trim());
            mascota.setRaza(raza != null ? raza.trim() : "");
            mascota.setSexo(sexo);

            if (fechaNacStr != null && !fechaNacStr.trim().isEmpty()) {
                mascota.setFechaNacimiento(Date.valueOf(fechaNacStr));
            }

            boolean actualizado = mascotaDao.actualizarMascota(mascota);

            if (actualizado) {
                request.setAttribute("success", "✅ Mascota '" + nombre + "' actualizada exitosamente");
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