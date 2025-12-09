-- Tabla de historial clínico para registrar consultas médicas veterinarias
-- EJECUTA ESTE SCRIPT EN TU BASE DE DATOS MYSQL

-- Primero elimina la tabla si existe (solo para desarrollo)
DROP TABLE IF EXISTS historial_clinico;

-- Crear la tabla historial_clinico
CREATE TABLE historial_clinico (
    id_historial INT AUTO_INCREMENT PRIMARY KEY,
    id_cita INT NOT NULL DEFAULT 0,
    id_mascota INT NOT NULL,
    id_veterinario INT NOT NULL,
    fecha_consulta DATETIME NOT NULL,
    motivo_consulta TEXT NOT NULL,
    diagnostico TEXT NOT NULL,
    tratamiento TEXT,
    medicamentos TEXT,
    peso_kg DECIMAL(5,2),
    temperatura_c DECIMAL(4,2),
    observaciones TEXT,
    proxima_cita DATE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_mascota) REFERENCES mascotas(id_mascota) ON DELETE CASCADE,
    FOREIGN KEY (id_veterinario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    INDEX idx_mascota (id_mascota),
    INDEX idx_veterinario (id_veterinario),
    INDEX idx_fecha (fecha_consulta)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Verificar que se creó correctamente
DESCRIBE historial_clinico;
