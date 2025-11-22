
# --- CONFIGURACIÓN DE RAILWAY (LLENA ESTOS DATOS) ---
import logging
import time
import os
from urllib.parse import urlparse
from spyne import Application, rpc, ServiceBase, Integer, Unicode
from spyne.protocol.soap import Soap11
import logging
import time
import os
from urllib.parse import urlparse
from spyne import Application, rpc, ServiceBase, Integer, Unicode
from spyne.protocol.soap import Soap11
from spyne.server.wsgi import WsgiApplication
import mysql.connector
from wsgiref.simple_server import make_server


# Construye la configuración de conexión leyendo variables de entorno.
# Si Railway proporciona MYSQL_PUBLIC_URL (mysql://user:pass@host:port/db) la parseamos.
def get_db_config():
    # Prioridad: MYSQL_PUBLIC_URL -> variables individuales -> fallback hardcoded ejemplo
    public = os.getenv('MYSQL_PUBLIC_URL') or os.getenv('MYSQL_URL')
    if public:
        try:
            parsed = urlparse(public)
            return {
                'host': parsed.hostname,
                'port': parsed.port or 3306,
                'user': parsed.username,
                'password': parsed.password,
                'database': parsed.path.lstrip('/'),
                'connection_timeout': int(os.getenv('DB_CONN_TIMEOUT', 10))
            }
        except Exception:
            pass

    # Variables individuales (comunes en Railway: MYSQLHOST, MYSQLPORT, MYSQLUSER, MYSQLPASSWORD, MYSQLDATABASE)
    try:
        return {
            'host': os.getenv('MYSQLHOST', os.getenv('DB_HOST', 'roundhouse.proxy.rlwy.net')),
            'port': int(os.getenv('MYSQLPORT', os.getenv('DB_PORT', 3306))),
            'user': os.getenv('MYSQLUSER', os.getenv('DB_USER', 'root')),
            'password': os.getenv('MYSQLPASSWORD', os.getenv('MYSQL_ROOT_PASSWORD', '')),
            'database': os.getenv('MYSQLDATABASE', os.getenv('DB_NAME', 'inscripciones')),
            'connection_timeout': int(os.getenv('DB_CONN_TIMEOUT', 10))
        }
    except Exception:
        # Fallback mínimo
        return {
            'host': 'roundhouse.proxy.rlwy.net',
            'port': 3306,
            'user': 'root',
            'password': '',
            'database': 'inscripciones',
            'connection_timeout': 10
        }


# Instancia global de configuración (evaluada al importar)
DB_CONFIG = get_db_config()

logging.basicConfig(level=logging.DEBUG)

# Función para crear la tabla automáticamente si no existe
def inicializar_db():
    print("--- Verificando Base de Datos en Railway ---")
    try:
        conn = mysql.connector.connect(**DB_CONFIG)
        cursor = conn.cursor()
        
        # SQL para crear la tabla SOLO si no existe
        sql_create_table = """
        CREATE TABLE IF NOT EXISTS inscripciones (
            id INT AUTO_INCREMENT PRIMARY KEY,
            nombre_alumno VARCHAR(100),
            matricula VARCHAR(20) UNIQUE,
            curso VARCHAR(50),
            fecha_inscripcion DATETIME DEFAULT CURRENT_TIMESTAMP
        );
        """
        cursor.execute(sql_create_table)
        conn.commit()
        print("✅ Tabla 'inscripciones' verificada/creada exitosamente en Railway.")
        
        cursor.close()
        conn.close()
    except Exception as e:
        print(f"❌ Error fatal al conectar con Railway: {e}")
        # Es recomendable detener el script si no hay BD, pero para pruebas seguimos
        pass

class InscripcionesService(ServiceBase):
    
    @rpc(Unicode, Unicode, Unicode, _returns=Unicode)
    def inscribir_alumno(ctx, nombre, matricula, curso):
        try:
            # Usamos la configuración global DB_CONFIG
            mydb = mysql.connector.connect(**DB_CONFIG)
            cursor = mydb.cursor()
            
            sql = "INSERT INTO inscripciones (nombre_alumno, matricula, curso) VALUES (%s, %s, %s)"
            val = (nombre, matricula, curso)
            
            cursor.execute(sql, val)
            mydb.commit()
            
            row_id = cursor.lastrowid
            cursor.close()
            mydb.close()
            
            return f"Éxito: Alumno {nombre} inscrito en Railway con ID {row_id}."

        except mysql.connector.Error as err:
            # Tip: Si la matricula ya existe, capturamos el error de duplicidad
            if err.errno == 1062: 
                return f"Error: La matrícula {matricula} ya está registrada."
            return f"Error de Base de Datos: {err}"
        except Exception as e:
            return f"Error del Servidor: {str(e)}"

application = Application(
    [InscripcionesService], 
    tns='uni.veracruzana.soap',
    in_protocol=Soap11(validator='lxml'), 
    out_protocol=Soap11()
)

wsgi_application = WsgiApplication(application)

if __name__ == '__main__':
    # 1. Primero ejecutamos la creación de tablas
    inicializar_db()
    
    # 2. Luego iniciamos el servidor
    print("🚀 Servidor SOAP corriendo. Esperando peticiones...")
    # Nota: Si subes este Python a Railway también, cambia '127.0.0.1' por '0.0.0.0'
    server = make_server('127.0.0.1', 8000, wsgi_application)
    server.serve_forever()