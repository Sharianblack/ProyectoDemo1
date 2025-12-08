package filter;

import model.Usuario;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Filtro de autenticación que intercepta todas las peticiones
 * Valida que el usuario esté autenticado y tenga los permisos necesarios
 */
@WebFilter("/*")
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Inicialización del filtro
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);
        
        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        
        // Rutas públicas que NO requieren autenticación
        boolean esRutaPublica = uri.endsWith("login.jsp") || 
                                uri.endsWith("index.html") ||
                                uri.endsWith("registro.jsp") ||
                                uri.endsWith("verificarEmail.jsp") ||
                                uri.endsWith("solicitarRecuperacion.jsp") ||
                                uri.endsWith("recuperarPassword.jsp") ||
                                uri.contains("/estilos/") ||
                                uri.endsWith("LoginServlet") ||
                                uri.endsWith("UsuarioServlet") && "registrar".equals(req.getParameter("action")) ||
                                uri.endsWith("EmailVerificationServlet") ||
                                uri.endsWith("PasswordRecoveryServlet") ||
                                uri.equals(contextPath + "/") ||
                                uri.equals(contextPath);
        
        // Si es ruta pública, permitir acceso
        if (esRutaPublica) {
            chain.doFilter(request, response);
            return;
        }
        
        // Verificar si hay sesión activa
        if (session == null || session.getAttribute("usuario") == null) {
            resp.sendRedirect(contextPath + "/index.html");
            return;
        }
        
        // Obtener usuario y rol
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        String rol = usuario.getRol();
        
        // Validar permisos específicos por ruta
        if (!validarPermisosPorRuta(uri, rol, req)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, 
                          "No tienes permisos para acceder a este recurso");
            return;
        }
        
        // Si todo está bien, continuar con la petición
        chain.doFilter(request, response);
    }
    
    /**
     * Valida si el usuario tiene permisos para acceder a una ruta específica
     */
    private boolean validarPermisosPorRuta(String uri, String rol, HttpServletRequest request) {
        
        // Páginas solo para Admin
        if (uri.endsWith("PIAdmin.jsp") || uri.endsWith("gestionUsuarios.jsp")) {
            return "Admin".equals(rol);
        }
        
        // Páginas solo para Veterinario
        if (uri.endsWith("PIVeterinario.jsp") || uri.endsWith("gestionClientesVet.jsp")) {
            return "Veterinario".equals(rol);
        }
        
        // Páginas solo para Cliente
        if (uri.endsWith("paginaInicio.jsp") || uri.endsWith("misMascotas.jsp")) {
            return "Cliente".equals(rol);
        }
        
        // misCitas.jsp - accesible para Cliente y Veterinario
        if (uri.endsWith("misCitas.jsp")) {
            return "Cliente".equals(rol) || "Veterinario".equals(rol);
        }
        
        // formCita.jsp - solo Veterinario
        if (uri.endsWith("formCita.jsp")) {
            return "Veterinario".equals(rol);
        }
        
        // Validar acciones de servlets
        String action = request.getParameter("action");
        
        // CitaServlet - validar por acción
        if (uri.contains("CitaServlet") && action != null) {
            // Crear/editar/eliminar citas - solo Veterinario
            if ("formCrear".equals(action) || "crear".equals(action) || 
                "formEditar".equals(action) || "actualizar".equals(action) || 
                "eliminar".equals(action)) {
                return "Veterinario".equals(rol);
            }
            // Ver mis citas - Cliente y Veterinario
            if ("misCitas".equals(action) || "listar".equals(action)) {
                return "Cliente".equals(rol) || "Veterinario".equals(rol);
            }
        }
        
        // MascotaServlet - solo Cliente puede gestionar sus mascotas
        if (uri.contains("MascotaServlet")) {
            return "Cliente".equals(rol);
        }
        
        // UsuarioServlet - solo Admin puede gestionar usuarios
        if (uri.contains("UsuarioServlet") && action != null) {
            if ("listar".equals(action) || "crear".equals(action) || 
                "actualizar".equals(action) || "cambiarEstado".equals(action) || 
                "cambiarPassword".equals(action)) {
                return "Admin".equals(rol);
            }
        }
        
        // VeterinarioClienteServlet - solo Veterinario puede gestionar sus clientes
        if (uri.contains("VeterinarioClienteServlet")) {
            return "Veterinario".equals(rol);
        }
        
        // Por defecto, permitir acceso
        return true;
    }

    @Override
    public void destroy() {
        // Limpieza del filtro
    }
}
