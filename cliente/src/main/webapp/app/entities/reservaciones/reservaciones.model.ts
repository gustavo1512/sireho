import dayjs from 'dayjs/esm';
import { IHabitaciones } from 'app/entities/habitaciones/habitaciones.model';
import { ICliente } from 'app/entities/cliente/cliente.model';
import { IColaborador } from 'app/entities/colaborador/colaborador.model';
import { IEventos } from 'app/entities/eventos/eventos.model';

export interface IReservaciones {
  id: number;
  fechaInicio?: dayjs.Dayjs | null;
  fechaFinal?: dayjs.Dayjs | null;
  habitacionesReservaciones?: Pick<IHabitaciones, 'id'> | null;
  clienteReservaciones?: Pick<ICliente, 'id'> | null;
  colaboradorResrvaciones?: Pick<IColaborador, 'id'> | null;
  eventosReservaciones?: Pick<IEventos, 'id'> | null;
}

export type NewReservaciones = Omit<IReservaciones, 'id'> & { id: null };
