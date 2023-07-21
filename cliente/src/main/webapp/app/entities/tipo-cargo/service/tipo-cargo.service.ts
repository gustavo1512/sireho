import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITipoCargo, NewTipoCargo } from '../tipo-cargo.model';

export type PartialUpdateTipoCargo = Partial<ITipoCargo> & Pick<ITipoCargo, 'id'>;

export type EntityResponseType = HttpResponse<ITipoCargo>;
export type EntityArrayResponseType = HttpResponse<ITipoCargo[]>;

@Injectable({ providedIn: 'root' })
export class TipoCargoService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/tipo-cargos');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(tipoCargo: NewTipoCargo): Observable<EntityResponseType> {
    return this.http.post<ITipoCargo>(this.resourceUrl, tipoCargo, { observe: 'response' });
  }

  update(tipoCargo: ITipoCargo): Observable<EntityResponseType> {
    return this.http.put<ITipoCargo>(`${this.resourceUrl}/${this.getTipoCargoIdentifier(tipoCargo)}`, tipoCargo, { observe: 'response' });
  }

  partialUpdate(tipoCargo: PartialUpdateTipoCargo): Observable<EntityResponseType> {
    return this.http.patch<ITipoCargo>(`${this.resourceUrl}/${this.getTipoCargoIdentifier(tipoCargo)}`, tipoCargo, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITipoCargo>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITipoCargo[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getTipoCargoIdentifier(tipoCargo: Pick<ITipoCargo, 'id'>): number {
    return tipoCargo.id;
  }

  compareTipoCargo(o1: Pick<ITipoCargo, 'id'> | null, o2: Pick<ITipoCargo, 'id'> | null): boolean {
    return o1 && o2 ? this.getTipoCargoIdentifier(o1) === this.getTipoCargoIdentifier(o2) : o1 === o2;
  }

  addTipoCargoToCollectionIfMissing<Type extends Pick<ITipoCargo, 'id'>>(
    tipoCargoCollection: Type[],
    ...tipoCargosToCheck: (Type | null | undefined)[]
  ): Type[] {
    const tipoCargos: Type[] = tipoCargosToCheck.filter(isPresent);
    if (tipoCargos.length > 0) {
      const tipoCargoCollectionIdentifiers = tipoCargoCollection.map(tipoCargoItem => this.getTipoCargoIdentifier(tipoCargoItem)!);
      const tipoCargosToAdd = tipoCargos.filter(tipoCargoItem => {
        const tipoCargoIdentifier = this.getTipoCargoIdentifier(tipoCargoItem);
        if (tipoCargoCollectionIdentifiers.includes(tipoCargoIdentifier)) {
          return false;
        }
        tipoCargoCollectionIdentifiers.push(tipoCargoIdentifier);
        return true;
      });
      return [...tipoCargosToAdd, ...tipoCargoCollection];
    }
    return tipoCargoCollection;
  }
}
