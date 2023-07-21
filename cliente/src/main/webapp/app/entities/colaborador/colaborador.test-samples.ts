import { IColaborador, NewColaborador } from './colaborador.model';

export const sampleWithRequiredData: IColaborador = {
  id: 40138,
};

export const sampleWithPartialData: IColaborador = {
  id: 46002,
  correo: 'invoice interface because',
};

export const sampleWithFullData: IColaborador = {
  id: 98378,
  nombreColaborador: 'inhere connect',
  cargo: 'Southwest transicional Parque',
  departamento: 'Southeast Kenia',
  numTelefono: 36547,
  correo: 'synthesizing Rústico Hip',
};

export const sampleWithNewData: NewColaborador = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
