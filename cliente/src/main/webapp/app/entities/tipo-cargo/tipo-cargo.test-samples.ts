import { ITipoCargo, NewTipoCargo } from './tipo-cargo.model';

export const sampleWithRequiredData: ITipoCargo = {
  id: 21939,
};

export const sampleWithPartialData: ITipoCargo = {
  id: 7395,
};

export const sampleWithFullData: ITipoCargo = {
  id: 41354,
  nombreCargo: 'estructura Negro Planificador',
};

export const sampleWithNewData: NewTipoCargo = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
