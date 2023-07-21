import dayjs from 'dayjs/esm';

import { IReservaciones, NewReservaciones } from './reservaciones.model';

export const sampleWithRequiredData: IReservaciones = {
  id: 7455,
};

export const sampleWithPartialData: IReservaciones = {
  id: 25469,
  fechaInicio: dayjs('2023-07-17T20:15'),
};

export const sampleWithFullData: IReservaciones = {
  id: 22884,
  fechaInicio: dayjs('2023-07-17T20:46'),
  fechaFinal: dayjs('2023-07-17T12:41'),
};

export const sampleWithNewData: NewReservaciones = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
