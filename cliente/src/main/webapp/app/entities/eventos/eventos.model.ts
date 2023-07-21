import dayjs from 'dayjs/esm';

export interface IEventos {
  id: number;
  nombreEvento?: string | null;
  fechaHora?: dayjs.Dayjs | null;
  responsable?: string | null;
  capacidad?: number | null;
  participantes?: number | null;
}

export type NewEventos = Omit<IEventos, 'id'> & { id: null };
