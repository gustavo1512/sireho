import { IHabitaciones, NewHabitaciones } from './habitaciones.model';

export const sampleWithRequiredData: IHabitaciones = {
  id: 27507,
};

export const sampleWithPartialData: IHabitaciones = {
  id: 57093,
  piso: 36947,
};

export const sampleWithFullData: IHabitaciones = {
  id: 68174,
  tipo: 'female Bolivia blank',
  piso: 1744,
  disponible: false,
};

export const sampleWithNewData: NewHabitaciones = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
