// ===================================
// CONFIGURACIÓN Y VARIABLES GLOBALES
// ===================================
const API_BASE = 'http://localhost:8094';
let token = null;
let currentUser = null;

// ===================================
// VERIFICACIÓN DE AUTENTICACIÓN
// ===================================
function checkAuth() {
    token = localStorage.getItem('token');
    
    console.log('🔍 [DEBUG] Token en localStorage:', token ? 'Existe' : 'No existe');
    console.log('🔍 [DEBUG] Token completo:', token);
    
    if (!token) {
        console.error('❌ No hay token, redirigiendo a login');
        window.location.href = 'login.html';
        return false;
    }

    const user = localStorage.getItem('user');
    console.log('🔍 [DEBUG] Usuario en localStorage:', user ? 'Existe' : 'No existe');
    
    if (!user) {
        console.error('❌ No hay usuario, redirigiendo a login');
        window.location.href = 'login.html';
        return false;
    }

    try {
        currentUser = JSON.parse(user);
        console.log('✅ [DEBUG] Usuario parseado:', currentUser);
    } catch (e) {
        console.error('❌ Error al parsear usuario:', e);
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        window.location.href = 'login.html';
        return false;
    }

    if (currentUser.rol !== 'Cliente') {
        alert('No tienes permisos para acceder a esta página');
        window.location.href = 'login.html';
        return false;
    }

    // Mostrar nombre del usuario
    document.getElementById('userName').textContent = `${currentUser.nombre} ${currentUser.apellido}`;
    document.getElementById('clienteName').textContent = currentUser.nombre;
    
    console.log('✅ [DEBUG] Autenticación exitosa');
    return true;
}

// ===================================
// FUNCIONES AUXILIARES
// ===================================
function showMessage(text, type = 'success') {
    const messageDiv = document.getElementById('message');
    messageDiv.textContent = text;
    messageDiv.className = `message ${type}`;
    messageDiv.style.display = 'block';
    
    setTimeout(() => {
        messageDiv.style.display = 'none';
    }, 5000);
}

async function apiRequest(endpoint, options = {}) {
    console.log(`🌐 [DEBUG] Petición a: ${endpoint}`);
    console.log(`🔑 [DEBUG] Token usado: ${token ? token.substring(0, 20) + '...' : 'Sin token'}`);
    
    const defaultOptions = {
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    };

    const finalOptions = { ...defaultOptions, ...options };
    if (options.headers) {
        finalOptions.headers = { ...defaultOptions.headers, ...options.headers };
    }

    console.log('📤 [DEBUG] Headers de la petición:', finalOptions.headers);

    try {
        const response = await fetch(`${API_BASE}${endpoint}`, finalOptions);
        
        console.log(`📥 [DEBUG] Respuesta status: ${response.status}`);
        console.log(`📥 [DEBUG] Respuesta headers:`, [...response.headers.entries()]);
        
        if (response.status === 401 || response.status === 403) {
            console.error(`❌ [DEBUG] Error de autorización (${response.status})`);
            
            // Intentar leer el cuerpo de la respuesta para ver el error
            try {
                const errorBody = await response.text();
                console.error('❌ [DEBUG] Cuerpo del error:', errorBody);
            } catch (e) {
                console.error('No se pudo leer el error del servidor');
            }
            
            // Mostrar alerta para debugging
            alert(`Error ${response.status}: Token inválido o expirado. Revisa la consola para más detalles.`);
            
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            window.location.href = 'login.html';
            throw new Error('Unauthorized');
        }

        if (response.status === 204) {
            console.log('✅ [DEBUG] Respuesta 204 - Sin contenido');
            return null;
        }

        const data = await response.json();
        console.log('✅ [DEBUG] Datos recibidos:', data);
        
        if (!response.ok) {
            throw new Error(data.error || 'Error en la petición');
        }

        return data;
    } catch (error) {
        console.error('❌ [DEBUG] Error en apiRequest:', error);
        
        if (error.message !== 'Unauthorized') {
            showMessage(`Error: ${error.message}`, 'error');
        }
        throw error;
    }
}

function logout() {
    console.log('🚪 [DEBUG] Cerrando sesión');
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    window.location.href = 'login.html';
}

// ===================================
// TABS
// ===================================
function showTab(tabName) {
    console.log(`📑 [DEBUG] Cambiando a tab: ${tabName}`);
    
    document.querySelectorAll('.tab-content').forEach(tab => {
        tab.classList.remove('active');
    });

    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.classList.remove('active');
    });

    document.getElementById(tabName).classList.add('active');
    event.target.classList.add('active');

    switch(tabName) {
        case 'perfil':
            cargarPerfil();
            break;
        case 'membresias':
            cargarMisMembresias();
            break;
        case 'pagos':
            cargarMisPagos();
            break;
    }
}

// ===================================
// MODALES
// ===================================
function showModal(modalId) {
    if (modalId === 'editPerfilModal') {
        document.getElementById('perfilNombre').value = currentUser.nombre;
        document.getElementById('perfilApellido').value = currentUser.apellido;
        document.getElementById('perfilTelefono').value = currentUser.telefono || '';
    }
    document.getElementById(modalId).style.display = 'block';
}

function closeModal(modalId) {
    document.getElementById(modalId).style.display = 'none';
}

window.onclick = function(event) {
    if (event.target.classList.contains('modal')) {
        event.target.style.display = 'none';
    }
}

// ===================================
// PERFIL
// ===================================
async function cargarPerfil() {
    console.log('👤 [DEBUG] Cargando perfil...');
    try {
        const perfil = await apiRequest('/usuario/perfil');
        const perfilDiv = document.getElementById('perfilInfo');
        
        perfilDiv.innerHTML = `
            <h3>${perfil.nombre} ${perfil.apellido}</h3>
            <p><strong>📧 Email:</strong> ${perfil.email}</p>
            <p><strong>📱 Teléfono:</strong> ${perfil.telefono || 'No registrado'}</p>
            <p><strong>👤 Rol:</strong> <span class="badge badge-info">${perfil.rol}</span></p>
        `;
        console.log('✅ [DEBUG] Perfil cargado correctamente');
    } catch (error) {
        if (error.message !== 'Unauthorized') {
            console.error('❌ [DEBUG] Error al cargar perfil:', error);
            document.getElementById('perfilInfo').innerHTML = '<p>Error al cargar el perfil</p>';
        }
    }
}

document.getElementById('editPerfilForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const perfilActualizado = {
        nombre: document.getElementById('perfilNombre').value,
        apellido: document.getElementById('perfilApellido').value,
        telefono: document.getElementById('perfilTelefono').value
    };

    const password = document.getElementById('perfilPassword').value;
    if (password) {
        perfilActualizado.password = password;
    }

    try {
        const resultado = await apiRequest('/usuario/perfil', {
            method: 'PUT',
            body: JSON.stringify(perfilActualizado)
        });

        currentUser.nombre = resultado.nombre;
        currentUser.apellido = resultado.apellido;
        currentUser.telefono = resultado.telefono;
        localStorage.setItem('user', JSON.stringify(currentUser));

        showMessage('✅ Perfil actualizado exitosamente', 'success');
        closeModal('editPerfilModal');
        document.getElementById('editPerfilForm').reset();

        document.getElementById('userName').textContent = `${currentUser.nombre} ${currentUser.apellido}`;
        document.getElementById('clienteName').textContent = currentUser.nombre;
        
        cargarPerfil();
    } catch (error) {
        if (error.message !== 'Unauthorized') {
            console.error('Error al actualizar perfil:', error);
        }
    }
});

// ===================================
// MIS MEMBRESÍAS
// ===================================
async function cargarMisMembresias() {
    console.log('💳 [DEBUG] Cargando membresías...');
    try {
        const membresias = await apiRequest('/clientemembresia/mis-membresias');
        const container = document.getElementById('membresiasContainer');

        if (membresias.length === 0) {
            container.innerHTML = `
                <div class="empty-state">
                    <div class="empty-state-icon">💳</div>
                    <h3>No tienes membresías</h3>
                    <p>Contacta al administrador para adquirir una membresía</p>
                </div>
            `;
            return;
        }

        const activas = membresias.filter(m => m.estado === 'Activa');
        document.getElementById('totalActivas').textContent = activas.length;

        if (activas.length > 0) {
            const hoy = new Date();
            const diasRestantes = activas.map(m => {
                const fechaFin = new Date(m.fechaFin);
                const diff = Math.ceil((fechaFin - hoy) / (1000 * 60 * 60 * 24));
                return diff;
            });
            const minDias = Math.min(...diasRestantes);
            document.getElementById('diasRestantes').textContent = minDias > 0 ? minDias : 0;
        }

        container.innerHTML = `
            <div class="cards-grid">
                ${membresias.map(m => {
                    const estadoClass = m.estado === 'Activa' ? 'badge-success' :
                                      m.estado === 'Vencida' ? 'badge-danger' : 'badge-warning';
                    return `
                        <div class="card">
                            <h3>${m.membresia.nombre}</h3>
                            <p>${m.membresia.descripcion}</p>
                            <div class="price">$${m.membresia.precio.toLocaleString()}</div>
                            <p><strong>Desde:</strong> ${m.fechaInicio}</p>
                            <p><strong>Hasta:</strong> ${m.fechaFin}</p>
                            <p><strong>Estado:</strong> <span class="badge ${estadoClass}">${m.estado}</span></p>
                        </div>
                    `;
                }).join('')}
            </div>
        `;
        console.log('✅ [DEBUG] Membresías cargadas correctamente');
    } catch (error) {
        if (error.message !== 'Unauthorized') {
            console.error('Error al cargar membresías:', error);
            document.getElementById('membresiasContainer').innerHTML = `
                <div class="empty-state">
                    <p>Error al cargar las membresías</p>
                </div>
            `;
        }
    }
}

// ===================================
// MIS PAGOS
// ===================================
async function cargarMisPagos() {
    console.log('💰 [DEBUG] Cargando pagos...');
    try {
        const pagos = await apiRequest('/gimnasio/mis-pagos');
        const tbody = document.getElementById('pagosTable');

        if (pagos.length === 0) {
            tbody.innerHTML = '<tr><td colspan="4" class="empty-state">No tienes pagos registrados</td></tr>';
            document.getElementById('totalPagos').textContent = 0;
            return;
        }

        document.getElementById('totalPagos').textContent = pagos.length;

        tbody.innerHTML = pagos.map(p => `
            <tr>
                <td>${p.fechaPago}</td>
                <td>$${p.monto.toLocaleString()}</td>
                <td><span class="badge badge-info">${p.metodoPago}</span></td>
                <td>${p.membresiaNombre || 'N/A'}</td>
            </tr>
        `).join('');
        console.log('✅ [DEBUG] Pagos cargados correctamente');
    } catch (error) {
        if (error.message !== 'Unauthorized') {
            console.error('Error al cargar pagos:', error);
            document.getElementById('pagosTable').innerHTML = `
                <tr><td colspan="4" class="empty-state">Error al cargar los pagos</td></tr>
            `;
        }
    }
}

// ===================================
// INICIALIZACIÓN
// ===================================
window.addEventListener('DOMContentLoaded', async () => {
    console.log('🚀 [DEBUG] Iniciando aplicación cliente...');
    
    if (checkAuth()) {
        console.log('🔄 [DEBUG] Cargando datos iniciales...');
        
        try {
            // Cargar datos iniciales uno por uno para identificar cuál falla
            console.log('1️⃣ [DEBUG] Cargando perfil...');
            await cargarPerfil();
            
            console.log('2️⃣ [DEBUG] Cargando membresías...');
            await cargarMisMembresias();
            
            console.log('3️⃣ [DEBUG] Cargando pagos...');
            await cargarMisPagos();
            
            console.log('✅ [DEBUG] Todos los datos cargados exitosamente');
        } catch (error) {
            console.error('❌ [DEBUG] Error durante la inicialización:', error);
        }
    }
});