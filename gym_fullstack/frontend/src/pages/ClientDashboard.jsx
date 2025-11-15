import { useEffect, useMemo, useState } from 'react';

import { useAuth } from '../context/AuthContext.jsx';
import { useApi } from '../hooks/useApi.js';
import '../styles/dashboard.css';

export default function ClientDashboard() {
  const { user, logout, setUser } = useAuth();
  const { request } = useApi();

  const [perfil, setPerfil] = useState(null);
  const [membresias, setMembresias] = useState([]);
  const [pagos, setPagos] = useState([]);

  const [formPerfil, setFormPerfil] = useState({ nombre: '', apellido: '', telefono: '', password: '' });
  const [message, setMessage] = useState(null);
  const [messageType, setMessageType] = useState('success');

  useEffect(() => {
    request('/usuario/perfil')
      .then((data) => {
        setPerfil(data);
        setFormPerfil({ nombre: data.nombre, apellido: data.apellido, telefono: data.telefono || '', password: '' });
      })
      .catch(() => {});

    request('/clientemembresia/mis-membresias')
      .then(setMembresias)
      .catch(() => {});

    request('/gimnasio/mis-pagos')
      .then(setPagos)
      .catch(() => {});
  }, [request]);

  const handleUpdatePerfil = async (event) => {
    event.preventDefault();
    const payload = { ...formPerfil };
    if (!payload.password) {
      delete payload.password;
    }
    try {
      const data = await request('/usuario/perfil', { method: 'PUT', body: payload });
      setPerfil(data);
      setFormPerfil({ nombre: data.nombre, apellido: data.apellido, telefono: data.telefono || '', password: '' });
      setUser(data);
      setMessage('Perfil actualizado correctamente.');
      setMessageType('success');
    } catch (error) {
      setMessage(error.message);
      setMessageType('error');
    }
  };

  const totalActivas = useMemo(
    () => membresias.filter((m) => m.estado === 'Activa').length,
    [membresias]
  );

  const diasRestantes = useMemo(() => {
    const activas = membresias.filter((m) => m.estado === 'Activa');
    if (activas.length === 0) return 0;
    const today = new Date();
    return activas.reduce((min, item) => {
      const fechaFin = new Date(item.fechaFin);
      const diff = Math.ceil((fechaFin - today) / (1000 * 60 * 60 * 24));
      return diff > 0 ? Math.min(min, diff) : min;
    }, Infinity);
  }, [membresias]);

  const totalPagos = pagos.length;
  const totalPagado = useMemo(() => pagos.reduce((acc, item) => acc + Number(item.monto || 0), 0), [pagos]);

  return (
    <div className="main-layout">
      <aside className="sidebar">
        <h2>Mi cuenta</h2>
        <div>
          <strong>{perfil?.nombre} {perfil?.apellido}</strong>
          <br />
          <small>{perfil?.email}</small>
        </div>
        <nav>
          <button className="nav-button active">Resumen</button>
        </nav>
        <button className="logout-button" onClick={logout}>Cerrar sesión</button>
      </aside>

      <main className="content">
        <section>
          <div className="section-header">
            <h1>Perfil y membresías</h1>
          </div>

          <div className="metrics">
            <div className="metric-card">
              <h4>Membresías activas</h4>
              <div className="metric-value">{totalActivas}</div>
            </div>
            <div className="metric-card">
              <h4>Días restantes</h4>
              <div className="metric-value">{diasRestantes === Infinity ? 0 : diasRestantes}</div>
            </div>
            <div className="metric-card">
              <h4>Pagos realizados</h4>
              <div className="metric-value">{totalPagos}</div>
            </div>
            <div className="metric-card">
              <h4>Total pagado</h4>
              <div className="metric-value">${totalPagado.toLocaleString()}</div>
            </div>
          </div>

          <h2>Actualizar perfil</h2>
          <form className="form-grid" onSubmit={handleUpdatePerfil}>
            <div className="form-field">
              <label>Nombre
                <input value={formPerfil.nombre} onChange={(e) => setFormPerfil({ ...formPerfil, nombre: e.target.value })} required />
              </label>
            </div>
            <div className="form-field">
              <label>Apellido
                <input value={formPerfil.apellido} onChange={(e) => setFormPerfil({ ...formPerfil, apellido: e.target.value })} required />
              </label>
            </div>
            <div className="form-field">
              <label>Teléfono
                <input value={formPerfil.telefono} onChange={(e) => setFormPerfil({ ...formPerfil, telefono: e.target.value })} />
              </label>
            </div>
            <div className="form-field">
              <label>Nueva contraseña
                <input type="password" value={formPerfil.password} onChange={(e) => setFormPerfil({ ...formPerfil, password: e.target.value })} placeholder="Opcional" />
              </label>
            </div>
            <div className="form-field" style={{ alignSelf: 'end' }}>
              <button className="primary-button" type="submit">Guardar cambios</button>
            </div>
          </form>

          <h2>Mis membresías</h2>
          <div className="card-grid">
            {membresias.length === 0 ? (
              <div className="card">
                <h3>No tienes membresías</h3>
                <p>Solicita una membresía al administrador.</p>
              </div>
            ) : (
              membresias.map((item) => (
                <div className="card" key={item.id}>
                  <h3>{item.membresia?.nombre}</h3>
                  <p>{item.membresia?.descripcion}</p>
                  <strong>${Number(item.membresia?.precio || 0).toLocaleString()}</strong>
                  <p>Desde: {item.fechaInicio}</p>
                  <p>Hasta: {item.fechaFin}</p>
                  <span className={`badge ${item.estado === 'Activa' ? 'badge-success' : item.estado === 'Vencida' ? 'badge-danger' : 'badge-warning'}`}>
                    {item.estado}
                  </span>
                </div>
              ))
            )}
          </div>

          <h2>Historial de pagos</h2>
          <div className="table-wrapper">
            <table>
              <thead>
                <tr>
                  <th>Fecha</th>
                  <th>Monto</th>
                  <th>Método</th>
                  <th>Membresía</th>
                </tr>
              </thead>
              <tbody>
                {pagos.length === 0 ? (
                  <tr>
                    <td colSpan={4}>No hay pagos registrados.</td>
                  </tr>
                ) : (
                  pagos.map((pago) => (
                    <tr key={pago.id}>
                      <td>{pago.fechaPago}</td>
                      <td>${Number(pago.monto).toLocaleString()}</td>
                      <td><span className="badge badge-info">{pago.metodoPago}</span></td>
                      <td>{pago.membresiaNombre || 'N/A'}</td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>

          {message && <div className={`message ${messageType}`}>{message}</div>}
        </section>
      </main>
    </div>
  );
}
