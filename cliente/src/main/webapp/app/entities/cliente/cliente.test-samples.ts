import { ICliente, NewCliente } from './cliente.model';

export const sampleWithRequiredData: ICliente = {
  id: 28668,
};

export const sampleWithPartialData: ICliente = {
  id: 15782,
  apellido: 'seamless Innovador',
  correo: 'backing indexing',
  telefono: 'East Account attentive',
};

export const sampleWithFullData: ICliente = {
  id: 25886,
  nombre: 'mingle Manat thump',
  apellido: 'Joyería Comunicaciones wisely',
  direccion: 'óptima',
  correo: 'DNS Pickup',
  telefono: 'recontextualize mid Smart',
};

export const sampleWithNewData: NewCliente = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
