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
    
    if (!token) {
        window.location.href = 'login.html';
        return false;
    }

    const user = localStorage.getItem('user');
    if (!user) {
        window.location.href = 'login.html';
        return false;
    }

    try {
        currentUser = JSON.parse(user);
    } catch (e) {
        console.error('Error al parsear usuario:', e);
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        window.location.href = 'login.html';
        return false;
    }

    if (currentUser.rol !== 'Administrador') {
        alert('No tienes permisos para acceder a esta página');
        window.location.href = 'login.html';
        return false;
    }

    // Mostrar nombre del usuario
    document.getElementById('userName').textContent = `${currentUser.nombre} ${currentUser.apellido}`;
    document.getElementById('adminName').textContent = currentUser.nombre;
    
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

    try {
        const response = await fetch(`${API_BASE}${endpoint}`, finalOptions);
        
        if (response.status === 401 || response.status === 403) {
            // No mostrar mensaje aquí, solo limpiar y redirigir
            console.error('Token inválido o expirado');
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            window.location.href = 'login.html';
            throw new Error('Unauthorized');
        }

        if (response.status === 204) {
            return null;
        }

        const data = await response.json();
        
        if (!response.ok) {
            throw new Error(data.error || 'Error en la petición');
        }

        return data;
    } catch (error) {
        // Solo mostrar mensaje si NO es error de autenticación
        if (error.message !== 'Unauthorized') {
            showMessage(`Error: ${error.message}`, 'error');
        }
        throw error;
    }
}

function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    window.location.href = 'login.html';
}

// ===================================
// TABS
// ===================================
function showTab(tabName) {
    document.querySelectorAll('.tab-content').forEach(tab => {
        tab.classList.remove('active');
    });

    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.classList.remove('active');
    });

    document.getElementById(tabName).classList.add('active');
    event.target.classList.add('active');

    switch(tabName) {
        case 'usuarios':
            listarUsuarios();
            break;
        case 'membresias':
            listarMembresias();
            break;
        case 'asignaciones':
            listarAsignaciones();
            break;
        case 'pagos':
            listarPagos();
            break;
    }
}

// ===================================
// MODALES
// ===================================
function showModal(modalId) {
    document.getElementById(modalId).style.display = 'block';
    
    if (modalId === 'addAsignacionModal') {
        cargarUsuariosParaAsignacion();
        cargarMembresiasParaAsignacion();
    } else if (modalId === 'addPagoModal') {
        cargarAsignacionesParaPago();
    }
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
// USUARIOS
// ===================================
async function listarUsuarios() {
    try {
        const usuarios = await apiRequest('/usuario/list');
        const tbody = document.getElementById('usuariosTable');
        
        if (usuarios.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" class="empty-state">No hay usuarios registrados</td></tr>';
            return;
        }

        tbody.innerHTML = usuarios.map(u => `
            <tr>
                <td>${u.id}</td>
                <td>${u.nombre} ${u.apellido}</td>
                <td>${u.email}</td>
                <td>${u.telefono || 'N/A'}</td>
                <td><span class="badge badge-info">${u.rol}</span></td>
                <td>
                    <button onclick="eliminarUsuario(${u.id})" class="btn-danger btn-small">🗑️ Eliminar</button>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        if (error.message !== 'Unauthorized') {
            console.error('Error al listar usuarios:', error);
        }
    }
}

document.getElementById('addUserForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const usuario = {
        nombre: document.getElementById('userNombre').value,
        apellido: document.getElementById('userApellido').value,
        email: document.getElementById('userEmail').value,
        password: document.getElementById('userPassword').value,
        telefono: document.getElementById('userTelefono').value,
        rol: document.getElementById('userRol').value
    };

    try {
        await apiRequest('/usuario/', {
            method: 'POST',
            body: JSON.stringify(usuario)
        });
        
        showMessage('✅ Usuario creado exitosamente', 'success');
        closeModal('addUserModal');
        document.getElementById('addUserForm').reset();
        listarUsuarios();
    } catch (error) {
        if (error.message !== 'Unauthorized') {
            console.error('Error al crear usuario:', error);
        }
    }
});

async function eliminarUsuario(id) {
    if (!confirm('¿Estás seguro de eliminar este usuario?')) return;

    try {
        await apiRequest(`/usuario/${id}`, { method: 'DELETE' });
        showMessage('✅ Usuario eliminado exitosamente', 'success');
        listarUsuarios();
    } catch (error) {
        if (error.message !== 'Unauthorized') {
            console.error('Error al eliminar usuario:', error);
        }
    }
}

// ===================================
// MEMBRESÍAS
// ===================================
async function listarMembresias() {
    try {
        const membresias = await apiRequest('/membresia/list');
        const grid = document.getElementById('membresiasGrid');
        
        if (membresias.length === 0) {
            grid.innerHTML = '<div class="empty-state"><p>No hay membresías registradas</p></div>';
            return;
        }

        grid.innerHTML = membresias.map(m => `
            <div class="card">
                <h3>${m.nombre}</h3>
                <p>${m.descripcion}</p>
                <div class="price">$${m.precio.toLocaleString()}</div>
                <p><strong>Duración:</strong> ${m.duracionDias} días</p>
                <div class="card-actions">
                    <button onclick="eliminarMembresia(${m.id})" class="btn-danger btn-small">🗑️ Eliminar</button>
                </div>
            </div>
        `).join('');
    } catch (error) {
        if (error.message !== 'Unauthorized') {
            console.error('Error al listar membresías:', error);
        }
    }
}

document.getElementById('addMembresiaForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const membresia = {
        nombre: document.getElementById('membresiaNombre').value,
        descripcion: document.getElementById('membresiaDescripcion').value,
        precio: parseFloat(document.getElementById('membresiaPrecio').value),
        duracionDias: parseInt(document.getElementById('membresiaDuracion').value)
    };

    try {
        await apiRequest('/membresia/', {
            method: 'POST',
            body: JSON.stringify(membresia)
        });
        
        showMessage('✅ Membresía creada exitosamente', 'success');
        closeModal('addMembresiaModal');
        document.getElementById('addMembresiaForm').reset();
        listarMembresias();
    } catch (error) {
        if (error.message !== 'Unauthorized') {
            console.error('Error al crear membresía:', error);
        }
    }
});

async function eliminarMembresia(id) {
    if (!confirm('¿Estás seguro de eliminar esta membresía?')) return;

    try {
        await apiRequest(`/membresia/${id}`, { method: 'DELETE' });
        showMessage('✅ Membresía eliminada exitosamente', 'success');
        listarMembresias();
    } catch (error) {
        if (error.message !== 'Unauthorized') {
            console.error('Error al eliminar membresía:', error);
        }
    }
}

// ===================================
// ASIGNACIONES
// ===================================
async function listarAsignaciones() {
    try {
        const asignaciones = await apiRequest('/clientemembresia/list');
        const tbody = document.getElementById('asignacionesTable');
        
        if (asignaciones.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="empty-state">No hay asignaciones registradas</td></tr>';
            return;
        }

        tbody.innerHTML = asignaciones.map(a => {
            const estadoClass = a.estado === 'Activa' ? 'badge-success' :
                              a.estado === 'Vencida' ? 'badge-danger' : 'badge-warning';
            
            return `
                <tr>
                    <td>${a.id}</td>
                    <td>${a.usuario.nombre} ${a.usuario.apellido}</td>
                    <td>${a.membresia.nombre}</td>
                    <td>${a.fechaInicio}</td>
                    <td>${a.fechaFin}</td>
                    <td><span class="badge ${estadoClass}">${a.estado}</span></td>
                    <td>
                        <button onclick="eliminarAsignacion(${a.id})" class="btn-danger btn-small">🗑️ Eliminar</button>
                    </td>
                </tr>
            `;
        }).join('');
    } catch (error) {
        if (error.message !== 'Unauthorized') {
            console.error('Error al listar asignaciones:', error);
        }
    }
}

async function cargarUsuariosParaAsignacion() {
    try {
        const usuarios = await apiRequest('/usuario/list');
        const select = document.getElementById('asignacionUsuario');
        const clientes = usuarios.filter(u => u.rol === 'Cliente');
        
        select.innerHTML = '<option value="">Seleccionar Cliente</option>' +
            clientes.map(u => `<option value="${u.id}">${u.nombre} ${u.apellido} (${u.email})</option>`).join('');
    } catch (error) {
        if (error.message !== 'Unauthorized') {
            console.error('Error al cargar usuarios:', error);
        }
    }
}

async function cargarMembresiasParaAsignacion() {
    try {
        const membresias = await apiRequest('/membresia/list');
        const select = document.getElementById('asignacionMembresia');
        
        select.innerHTML = '<option value="">Seleccionar Membresía</option>' +
            membresias.map(m => `<option value="${m.id}">${m.nombre} - $${m.precio}</option>`).join('');
    } catch (error) {
        if (error.message !== 'Unauthorized') {
            console.error('Error al cargar membresías:', error);
        }
    }
}

document.getElementById('addAsignacionForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const asignacion = {
        usuario: {
            id: parseInt(document.getElementById('asignacionUsuario').value)
        },
        membresia: {
            id: parseInt(document.getElementById('asignacionMembresia').value)
        },
        fechaInicio: document.getElementById('asignacionFechaInicio').value,
        fechaFin: document.getElementById('asignacionFechaFin').value,
        estado: document.getElementById('asignacionEstado').value
    };

    try {
        await apiRequest('/clientemembresia/', {
            method: 'POST',
            body: JSON.stringify(asignacion)
        });
        
        showMessage('✅ Membresía asignada exitosamente', 'success');
        closeModal('addAsignacionModal');
        document.getElementById('addAsignacionForm').reset();
        listarAsignaciones();
    } catch (error) {
        if (error.message !== 'Unauthorized') {
            console.error('Error al asignar membresía:', error);
        }
    }
});

async function eliminarAsignacion(id) {
    if (!confirm('¿Estás seguro de eliminar esta asignación?')) return;

    try {
        await apiRequest(`/clientemembresia/${id}`, { method: 'DELETE' });
        showMessage('✅ Asignación eliminada exitosamente', 'success');
        listarAsignaciones();
    } catch (error) {
        if (error.message !== 'Unauthorized') {
            console.error('Error al eliminar asignación:', error);
        }
    }
}

// ===================================
// PAGOS
// ===================================
async function listarPagos() {
    try {
        const pagos = await apiRequest('/gimnasio/pagos');
        const tbody = document.getElementById('pagosTable');
        
        if (pagos.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="empty-state">No hay pagos registrados</td></tr>';
            return;
        }

        tbody.innerHTML = pagos.map(p => `
            <tr>
                <td>${p.id}</td>
                <td>${p.clienteNombre || 'N/A'}</td>
                <td>${p.membresiaNombre || 'N/A'}</td>
                <td>${p.fechaPago}</td>
                <td>$${p.monto.toLocaleString()}</td>
                <td><span class="badge badge-info">${p.metodoPago}</span></td>
                <td>
                    <button onclick="eliminarPago(${p.id})" class="btn-danger btn-small">🗑️ Eliminar</button>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        if (error.message !== 'Unauthorized') {
            console.error('Error al listar pagos:', error);
        }
    }
}

async function cargarAsignacionesParaPago() {
    try {
        const asignaciones = await apiRequest('/clientemembresia/list');
        const select = document.getElementById('pagoAsignacion');
        const activas = asignaciones.filter(a => a.estado === 'Activa');
        
        select.innerHTML = '<option value="">Seleccionar Asignación</option>' +
            activas.map(a => 
                `<option value="${a.id}">${a.usuario.nombre} ${a.usuario.apellido} - ${a.membresia.nombre}</option>`
            ).join('');
    } catch (error) {
        if (error.message !== 'Unauthorized') {
            console.error('Error al cargar asignaciones:', error);
        }
    }
}

document.getElementById('addPagoForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const pago = {
        clienteMembresiaId: parseInt(document.getElementById('pagoAsignacion').value),
        fechaPago: document.getElementById('pagoFecha').value,
        monto: parseFloat(document.getElementById('pagoMonto').value),
        metodoPago: document.getElementById('pagoMetodo').value
    };

    try {
        await apiRequest('/gimnasio/pago', {
            method: 'POST',
            body: JSON.stringify(pago)
        });
        
        showMessage('✅ Pago registrado exitosamente', 'success');
        closeModal('addPagoModal');
        document.getElementById('addPagoForm').reset();
        listarPagos();
    } catch (error) {
        if (error.message !== 'Unauthorized') {
            console.error('Error al registrar pago:', error);
        }
    }
});

async function eliminarPago(id) {
    if (!confirm('¿Estás seguro de eliminar este pago?')) return;

    try {
        await apiRequest(`/gimnasio/pago/${id}`, { method: 'DELETE' });
        showMessage('✅ Pago eliminado exitosamente', 'success');
        listarPagos();
    } catch (error) {
        if (error.message !== 'Unauthorized') {
            console.error('Error al eliminar pago:', error);
        }
    }
}

// ===================================
// INICIALIZACIÓN
// ===================================
window.addEventListener('DOMContentLoaded', () => {
    if (checkAuth()) {
        listarUsuarios(); // Cargar usuarios por defecto
    }
});