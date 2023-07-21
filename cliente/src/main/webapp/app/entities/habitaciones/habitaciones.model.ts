import { IFactura } from 'app/entities/factura/factura.model';

export interface IHabitaciones {
  id: number;
  tipo?: string | null;
  piso?: number | null;
  disponible?: boolean | null;
  facturaHabitaciones?: Pick<IFactura, 'id'> | null;
}

export type NewHabitaciones = Omit<IHabitaciones, 'id'> & { id: null };
