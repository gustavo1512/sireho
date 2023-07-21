import { ITipoCargo } from 'app/entities/tipo-cargo/tipo-cargo.model';

export interface IColaborador {
  id: number;
  nombreColaborador?: string | null;
  cargo?: string | null;
  departamento?: string | null;
  numTelefono?: number | null;
  correo?: string | null;
  tipoCargoColaborador?: Pick<ITipoCargo, 'id'> | null;
}

export type NewColaborador = Omit<IColaborador, 'id'> & { id: null };
