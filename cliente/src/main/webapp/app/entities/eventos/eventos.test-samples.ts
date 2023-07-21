import dayjs from 'dayjs/esm';

import { IEventos, NewEventos } from './eventos.model';

export const sampleWithRequiredData: IEventos = {
  id: 96910,
};

export const sampleWithPartialData: IEventos = {
  id: 42111,
  participantes: 15821,
};

export const sampleWithFullData: IEventos = {
  id: 55052,
  nombreEvento: 'Central',
  fechaHora: dayjs('2023-07-17T15:59'),
  responsable: 'Southwest Borders Adventure',
  capacidad: 42451,
  participantes: 39453,
};

export const sampleWithNewData: NewEventos = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
