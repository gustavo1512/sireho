import dayjs from 'dayjs/esm';

import { IFactura, NewFactura } from './factura.model';

export const sampleWithRequiredData: IFactura = {
  id: 53248,
};

export const sampleWithPartialData: IFactura = {
  id: 26210,
  fechaPago: dayjs('2023-07-17T21:45'),
};

export const sampleWithFullData: IFactura = {
  id: 34994,
  cantidadPaga: 77793,
  fechaPago: dayjs('2023-07-17T19:10'),
  metodoPgo: 'Negro',
};

export const sampleWithNewData: NewFactura = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
