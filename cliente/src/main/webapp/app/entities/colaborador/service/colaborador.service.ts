import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IColaborador, NewColaborador } from '../colaborador.model';

export type PartialUpdateColaborador = Partial<IColaborador> & Pick<IColaborador, 'id'>;

export type EntityResponseType = HttpResponse<IColaborador>;
export type EntityArrayResponseType = HttpResponse<IColaborador[]>;

@Injectable({ providedIn: 'root' })
export class ColaboradorService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/colaboradors');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(colaborador: NewColaborador): Observable<EntityResponseType> {
    return this.http.post<IColaborador>(this.resourceUrl, colaborador, { observe: 'response' });
  }

  update(colaborador: IColaborador): Observable<EntityResponseType> {
    return this.http.put<IColaborador>(`${this.resourceUrl}/${this.getColaboradorIdentifier(colaborador)}`, colaborador, {
      observe: 'response',
    });
  }

  partialUpdate(colaborador: PartialUpdateColaborador): Observable<EntityResponseType> {
    return this.http.patch<IColaborador>(`${this.resourceUrl}/${this.getColaboradorIdentifier(colaborador)}`, colaborador, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IColaborador>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IColaborador[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getColaboradorIdentifier(colaborador: Pick<IColaborador, 'id'>): number {
    return colaborador.id;
  }

  compareColaborador(o1: Pick<IColaborador, 'id'> | null, o2: Pick<IColaborador, 'id'> | null): boolean {
    return o1 && o2 ? this.getColaboradorIdentifier(o1) === this.getColaboradorIdentifier(o2) : o1 === o2;
  }

  addColaboradorToCollectionIfMissing<Type extends Pick<IColaborador, 'id'>>(
    colaboradorCollection: Type[],
    ...colaboradorsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const colaboradors: Type[] = colaboradorsToCheck.filter(isPresent);
    if (colaboradors.length > 0) {
      const colaboradorCollectionIdentifiers = colaboradorCollection.map(
        colaboradorItem => this.getColaboradorIdentifier(colaboradorItem)!
      );
      const colaboradorsToAdd = colaboradors.filter(colaboradorItem => {
        const colaboradorIdentifier = this.getColaboradorIdentifier(colaboradorItem);
        if (colaboradorCollectionIdentifiers.includes(colaboradorIdentifier)) {
          return false;
        }
        colaboradorCollectionIdentifiers.push(colaboradorIdentifier);
        return true;
      });
      return [...colaboradorsToAdd, ...colaboradorCollection];
    }
    return colaboradorCollection;
  }
}
