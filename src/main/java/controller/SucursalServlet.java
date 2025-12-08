package controller;

import dao.SucursalDao;
import model.Sucursal;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Time;
import java.util.List;

@WebServlet("/SucursalServlet")
public class SucursalServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private SucursalDao sucursalDao;

    @Override
    public void init() {
        sucursalDao = new SucursalDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if (action == null || action.isEmpty()) {
            action = "listar";
        }

        switch (action) {
            case "listar":
                listarSucursales(request, response);
                break;
            case "listarActivas":
                listarSucursalesActivas(request, response);
                break;
            default:
                listarSucursales(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        if (action == null || action.isEmpty()) {
            response.sendRedirect("SucursalServlet?action=listar");
            return;
        }

        switch (action) {
            case "crear":
                crearSucursal(request, response);
                break;
            case "actualizar":
                actualizarSucursal(request, response);
                break;
            case "eliminar":
                eliminarSucursal(request, response);
                break;
            case "cambiarEstado":
                cambiarEstado(request, response);
                break;
            default:
                listarSucursales(request, response);
                break;
        }
    }

    // ========================================================================
    // LISTAR SUCURSALES
    // ========================================================================
    private void listarSucursales(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String criterio = request.getParameter("criterio");
            List<Sucursal> sucursales;

            if (criterio != null && !criterio.trim().isEmpty()) {
                sucursales = sucursalDao.buscarSucursales(criterio.trim());
                request.setAttribute("criterio", criterio);
            } else {
                sucursales = sucursalDao.listarTodas();
            }

            request.setAttribute("sucursales", sucursales);
            request.getRequestDispatcher("gestionSucursales.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al listar sucursales: " + e.getMessage());
            request.getRequestDispatcher("gestionSucursales.jsp").forward(request, response);
        }
    }

    // ========================================================================
    // LISTAR SUCURSALES ACTIVAS
    // ========================================================================
    private void listarSucursalesActivas(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Sucursal> sucursales = sucursalDao.listarActivas();
            request.setAttribute("sucursales", sucursales);
            request.getRequestDispatcher("gestionSucursales.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al listar sucursales activas: " + e.getMessage());
            request.getRequestDispatcher("gestionSucursales.jsp").forward(request, response);
        }
    }

    // ========================================================================
    // CREAR SUCURSAL
    // ========================================================================
    private void crearSucursal(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Obtener datos del formulario
            String nombre = request.getParameter("nombre");
            String direccion = request.getParameter("direccion");
            String telefono = request.getParameter("telefono");
            String correo = request.getParameter("correo");
            String ciudad = request.getParameter("ciudad");
            String horarioApertura = request.getParameter("horarioApertura");
            String horarioCierre = request.getParameter("horarioCierre");

            // Preservar datos en caso de error
            request.setAttribute("formNombre", nombre);
            request.setAttribute("formDireccion", direccion);
            request.setAttribute("formTelefono", telefono);
            request.setAttribute("formCorreo", correo);
            request.setAttribute("formCiudad", ciudad);
            request.setAttribute("formHorarioApertura", horarioApertura);
            request.setAttribute("formHorarioCierre", horarioCierre);
            request.setAttribute("mostrarModal", "crear");

            // Validaciones
            if (nombre == null || nombre.trim().isEmpty()) {
                request.setAttribute("error", "El nombre de la sucursal es obligatorio");
                listarSucursales(request, response);
                return;
            }

            if (direccion == null || direccion.trim().isEmpty()) {
                request.setAttribute("error", "La dirección es obligatoria");
                listarSucursales(request, response);
                return;
            }

            if (telefono != null && !telefono.trim().isEmpty()) {
                if (!util.ValidacionUtil.esTelefonoValido(telefono)) {
                    request.setAttribute("error", "El teléfono debe tener exactamente 10 dígitos");
                    listarSucursales(request, response);
                    return;
                }
            }

            if (correo != null && !correo.trim().isEmpty()) {
                if (!util.ValidacionUtil.esEmailValido(correo)) {
                    request.setAttribute("error", "El formato del correo no es válido");
                    listarSucursales(request, response);
                    return;
                }
            }

            // Crear objeto Sucursal
            Sucursal sucursal = new Sucursal();
            sucursal.setNombre(nombre.trim());
            sucursal.setDireccion(direccion.trim());
            sucursal.setTelefono(telefono != null ? telefono.trim() : "");
            sucursal.setCorreo(correo.trim());
            sucursal.setCiudad(ciudad != null ? ciudad.trim() : "");
            
            // Convertir horarios (ya validados como no vacíos)
            sucursal.setHorarioApertura(Time.valueOf(horarioApertura + ":00"));
            sucursal.setHorarioCierre(Time.valueOf(horarioCierre + ":00"));
            
            sucursal.setActivo(true);

            // Guardar en BD
            boolean creado = sucursalDao.crear(sucursal);

            if (creado) {
                // Limpiar atributos
                request.removeAttribute("formNombre");
                request.removeAttribute("formDireccion");
                request.removeAttribute("formTelefono");
                request.removeAttribute("formCorreo");
                request.removeAttribute("formCiudad");
                request.removeAttribute("formHorarioApertura");
                request.removeAttribute("formHorarioCierre");
                request.removeAttribute("mostrarModal");

                request.setAttribute("success", "✅ Sucursal '" + nombre + "' creada exitosamente");
                System.out.println("✅ Sucursal creada: " + nombre);
            } else {
                request.setAttribute("error", "Error al crear la sucursal");
                System.out.println("❌ Error al crear sucursal: " + nombre);
            }

        } catch (Exception e) {
            request.setAttribute("error", "Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }

        listarSucursales(request, response);
    }

    // ========================================================================
    // ACTUALIZAR SUCURSAL
    // ========================================================================
    private void actualizarSucursal(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("sucursalId"));
            String nombre = request.getParameter("nombre");
            String direccion = request.getParameter("direccion");
            String telefono = request.getParameter("telefono");
            String correo = request.getParameter("correo");
            String ciudad = request.getParameter("ciudad");
            String horarioApertura = request.getParameter("horarioApertura");
            String horarioCierre = request.getParameter("horarioCierre");
            boolean activo = request.getParameter("activo") != null;

            // Preservar datos
            request.setAttribute("formSucursalId", id);
            request.setAttribute("formNombre", nombre);
            request.setAttribute("formDireccion", direccion);
            request.setAttribute("formTelefono", telefono);
            request.setAttribute("formCorreo", correo);
            request.setAttribute("formCiudad", ciudad);
            request.setAttribute("formHorarioApertura", horarioApertura);
            request.setAttribute("formHorarioCierre", horarioCierre);
            request.setAttribute("formActivo", activo);
            request.setAttribute("mostrarModal", "editar");

            // Validaciones
            if (nombre == null || nombre.trim().isEmpty()) {
                request.setAttribute("error", "El nombre de la sucursal es obligatorio");
                listarSucursales(request, response);
                return;
            }

            if (direccion == null || direccion.trim().isEmpty()) {
                request.setAttribute("error", "La dirección es obligatoria");
                listarSucursales(request, response);
                return;
            }

            if (correo == null || correo.trim().isEmpty()) {
                request.setAttribute("error", "El correo electrónico es obligatorio");
                listarSucursales(request, response);
                return;
            }

            if (!util.ValidacionUtil.esEmailValido(correo)) {
                request.setAttribute("error", "El formato del correo no es válido");
                listarSucursales(request, response);
                return;
            }

            if (horarioApertura == null || horarioApertura.trim().isEmpty()) {
                request.setAttribute("error", "El horario de apertura es obligatorio");
                listarSucursales(request, response);
                return;
            }

            if (horarioCierre == null || horarioCierre.trim().isEmpty()) {
                request.setAttribute("error", "El horario de cierre es obligatorio");
                listarSucursales(request, response);
                return;
            }

            if (telefono != null && !telefono.trim().isEmpty()) {
                if (!util.ValidacionUtil.esTelefonoValido(telefono)) {
                    request.setAttribute("error", "El teléfono debe tener exactamente 10 dígitos");
                    listarSucursales(request, response);
                    return;
                }
            }

            // Actualizar
            Sucursal sucursal = new Sucursal();
            sucursal.setId(id);
            sucursal.setNombre(nombre.trim());
            sucursal.setDireccion(direccion.trim());
            sucursal.setTelefono(telefono != null ? telefono.trim() : "");
            sucursal.setCorreo(correo.trim());
            sucursal.setCiudad(ciudad != null ? ciudad.trim() : "");
            
            // Convertir horarios (ya validados como no vacíos)
            sucursal.setHorarioApertura(Time.valueOf(horarioApertura + ":00"));
            sucursal.setHorarioCierre(Time.valueOf(horarioCierre + ":00"));
            
            sucursal.setActivo(activo);

            boolean actualizado = sucursalDao.actualizar(sucursal);

            if (actualizado) {
                // Limpiar atributos
                request.removeAttribute("formSucursalId");
                request.removeAttribute("formNombre");
                request.removeAttribute("formDireccion");
                request.removeAttribute("formTelefono");
                request.removeAttribute("formCorreo");
                request.removeAttribute("formCiudad");
                request.removeAttribute("formHorarioApertura");
                request.removeAttribute("formHorarioCierre");
                request.removeAttribute("formActivo");
                request.removeAttribute("mostrarModal");

                request.setAttribute("success", "Sucursal '" + nombre + "' actualizada exitosamente");
                System.out.println("✅ Sucursal actualizada: ID " + id);
            } else {
                request.setAttribute("error", "Error al actualizar la sucursal");
                System.out.println("❌ Error al actualizar sucursal: ID " + id);
            }

        } catch (Exception e) {
            request.setAttribute("error", "Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }

        listarSucursales(request, response);
    }

    // ========================================================================
    // ELIMINAR SUCURSAL
    // ========================================================================
    private void eliminarSucursal(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("sucursalId"));

            boolean eliminado = sucursalDao.eliminar(id);

            if (eliminado) {
                request.setAttribute("success", "Sucursal eliminada exitosamente");
                System.out.println("✅ Sucursal eliminada: ID " + id);
            } else {
                request.setAttribute("error", "Error al eliminar la sucursal");
                System.out.println("❌ Error al eliminar sucursal: ID " + id);
            }

        } catch (Exception e) {
            request.setAttribute("error", "Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }

        listarSucursales(request, response);
    }

    // ========================================================================
    // CAMBIAR ESTADO
    // ========================================================================
    private void cambiarEstado(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("sucursalId"));
            boolean nuevoEstado = Boolean.parseBoolean(request.getParameter("estado"));

            boolean actualizado = sucursalDao.cambiarEstado(id, nuevoEstado);

            if (actualizado) {
                String estadoTexto = nuevoEstado ? "activada" : "desactivada";
                request.setAttribute("success", "Sucursal " + estadoTexto + " exitosamente");
                System.out.println("✅ Estado cambiado - Sucursal ID " + id + ": " + estadoTexto);
            } else {
                request.setAttribute("error", "Error al cambiar estado de la sucursal");
                System.out.println("❌ Error al cambiar estado - Sucursal ID " + id);
            }

        } catch (Exception e) {
            request.setAttribute("error", "Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }

        listarSucursales(request, response);
    }
}
