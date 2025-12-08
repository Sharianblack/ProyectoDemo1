-- Tabla de citas veterinarias
-- Asegúrate de que la tabla citas tenga esta estructura

CREATE TABLE IF NOT EXISTS citas (
    id_cita INT AUTO_INCREMENT PRIMARY KEY,
    id_mascota INT NOT NULL,
    id_veterinario INT NOT NULL,
    id_sucursal INT NOT NULL,
    fecha_cita DATETIME NOT NULL,
    estado VARCHAR(50) DEFAULT 'Programada',
    observaciones TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_mascota) REFERENCES mascotas(id_mascota) ON DELETE CASCADE,
    FOREIGN KEY (id_veterinario) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    FOREIGN KEY (id_sucursal) REFERENCES sucursales(id_sucursal) ON DELETE CASCADE,
    INDEX idx_veterinario (id_veterinario),
    INDEX idx_mascota (id_mascota),
    INDEX idx_fecha (fecha_cita),
    INDEX idx_estado (estado)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Si tu tabla tiene columnas fecha_cita (DATE) y hora_cita (TIME) separadas,
-- necesitas ejecutar esta migración:

-- ALTER TABLE citas DROP COLUMN hora_cita;
-- ALTER TABLE citas MODIFY COLUMN fecha_cita DATETIME NOT NULL;

-- Verifica la estructura actual con:
-- DESCRIBE citas;
