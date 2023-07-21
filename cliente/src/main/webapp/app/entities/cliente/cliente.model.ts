export interface ICliente {
  id: number;
  nombre?: string | null;
  apellido?: string | null;
  direccion?: string | null;
  correo?: string | null;
  telefono?: string | null;
}

export type NewCliente = Omit<ICliente, 'id'> & { id: null };
