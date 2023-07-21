export interface ITipoCargo {
  id: number;
  nombreCargo?: string | null;
}

export type NewTipoCargo = Omit<ITipoCargo, 'id'> & { id: null };
