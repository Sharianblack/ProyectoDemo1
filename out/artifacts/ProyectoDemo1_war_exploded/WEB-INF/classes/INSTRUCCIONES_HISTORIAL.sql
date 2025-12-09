-- ============================================================================
-- INSTRUCCIONES PARA CONFIGURAR EL HISTORIAL CLÍNICO
-- ============================================================================

-- PASO 1: Verificar/Ajustar la tabla de citas
-- Si tu tabla citas tiene columnas separadas fecha_cita (DATE) y hora_cita (TIME),
-- necesitas ejecutar esta migración:

-- DESCOMENTAR ESTAS LÍNEAS SI ES NECESARIO:
-- ALTER TABLE citas DROP COLUMN hora_cita;
-- ALTER TABLE citas MODIFY COLUMN fecha_cita DATETIME NOT NULL;

-- ============================================================================

-- PASO 2: Crear la tabla de historial clínico
-- Esta tabla almacena todos los registros médicos de las consultas

CREATE TABLE IF NOT EXISTS historial_clinico (
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

-- ============================================================================

-- PASO 3 (OPCIONAL): Insertar datos de ejemplo para pruebas
-- Reemplaza los IDs con valores reales de tu base de datos

-- INSERT INTO historial_clinico 
-- (id_cita, id_mascota, id_veterinario, fecha_consulta, motivo_consulta, diagnostico, 
--  tratamiento, medicamentos, peso_kg, temperatura_c, observaciones, proxima_cita)
-- VALUES 
-- (1, 1, 2, NOW(), 
--  'Revisión general y vacunación', 
--  'Animal en buen estado de salud. Sin anomalías detectadas.', 
--  'Aplicación de vacuna antirrábica', 
--  'Ninguno', 
--  15.5, 38.2, 
--  'Recomendar desparasitación en 30 días', 
--  DATE_ADD(CURDATE(), INTERVAL 30 DAY));

-- ============================================================================

-- VERIFICAR QUE TODO ESTÉ CORRECTO:

-- Ver estructura de la tabla citas
DESCRIBE citas;

-- Ver estructura de la tabla historial_clinico
DESCRIBE historial_clinico;

-- Contar registros
SELECT COUNT(*) as total_historial FROM historial_clinico;

-- ============================================================================
