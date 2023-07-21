import dayjs from 'dayjs/esm';

export interface IFactura {
  id: number;
  cantidadPaga?: number | null;
  fechaPago?: dayjs.Dayjs | null;
  metodoPgo?: string | null;
}

export type NewFactura = Omit<IFactura, 'id'> & { id: null };
