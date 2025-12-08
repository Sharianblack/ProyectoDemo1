-- =====================================================
-- TABLA: Sucursales
-- Descripción: Almacena información de las sucursales
-- =====================================================

CREATE TABLE IF NOT EXISTS Sucursales (
    id_sucursal INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    direccion VARCHAR(200) NOT NULL,
    telefono VARCHAR(15),
    correo VARCHAR(100),
    ciudad VARCHAR(50),
    horario_apertura TIME,
    horario_cierre TIME,
    activo TINYINT(1) DEFAULT 1,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_nombre (nombre),
    INDEX idx_ciudad (ciudad),
    INDEX idx_activo (activo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================
-- DATOS DE EJEMPLO
-- =====================================================

INSERT INTO Sucursales (nombre, direccion, telefono, correo, ciudad, horario_apertura, horario_cierre, activo) VALUES
('Sucursal Centro', 'Av. Principal #123, Centro', '0999999001', 'centro@veterinaria.com', 'Quito', '08:00:00', '18:00:00', 1),
('Sucursal Norte', 'Calle Los Pinos #456, Sector Norte', '0999999002', 'norte@veterinaria.com', 'Quito', '09:00:00', '19:00:00', 1),
('Sucursal Sur', 'Av. Quitumbe #789, Sur', '0999999003', 'sur@veterinaria.com', 'Quito', '08:30:00', '17:30:00', 1);

-- =====================================================
-- MODIFICAR TABLA Citas (Opcional - para vincular)
-- =====================================================
-- Si quieres vincular citas con sucursales, ejecuta esto:

-- ALTER TABLE Citas ADD COLUMN id_sucursal INT AFTER id_mascota;
-- ALTER TABLE Citas ADD FOREIGN KEY (id_sucursal) REFERENCES Sucursales(id_sucursal);

-- =====================================================
-- CONSULTAS ÚTILES
-- =====================================================

-- Ver todas las sucursales activas
-- SELECT * FROM Sucursales WHERE activo = 1 ORDER BY nombre;

-- Contar sucursales por ciudad
-- SELECT ciudad, COUNT(*) as total FROM Sucursales GROUP BY ciudad;
