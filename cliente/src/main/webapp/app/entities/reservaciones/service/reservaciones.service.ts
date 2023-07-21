import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IReservaciones, NewReservaciones } from '../reservaciones.model';

export type PartialUpdateReservaciones = Partial<IReservaciones> & Pick<IReservaciones, 'id'>;

type RestOf<T extends IReservaciones | NewReservaciones> = Omit<T, 'fechaInicio' | 'fechaFinal'> & {
  fechaInicio?: string | null;
  fechaFinal?: string | null;
};

export type RestReservaciones = RestOf<IReservaciones>;

export type NewRestReservaciones = RestOf<NewReservaciones>;

export type PartialUpdateRestReservaciones = RestOf<PartialUpdateReservaciones>;

export type EntityResponseType = HttpResponse<IReservaciones>;
export type EntityArrayResponseType = HttpResponse<IReservaciones[]>;

@Injectable({ providedIn: 'root' })
export class ReservacionesService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/reservaciones');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(reservaciones: NewReservaciones): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(reservaciones);
    return this.http
      .post<RestReservaciones>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(reservaciones: IReservaciones): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(reservaciones);
    return this.http
      .put<RestReservaciones>(`${this.resourceUrl}/${this.getReservacionesIdentifier(reservaciones)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(reservaciones: PartialUpdateReservaciones): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(reservaciones);
    return this.http
      .patch<RestReservaciones>(`${this.resourceUrl}/${this.getReservacionesIdentifier(reservaciones)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestReservaciones>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestReservaciones[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getReservacionesIdentifier(reservaciones: Pick<IReservaciones, 'id'>): number {
    return reservaciones.id;
  }

  compareReservaciones(o1: Pick<IReservaciones, 'id'> | null, o2: Pick<IReservaciones, 'id'> | null): boolean {
    return o1 && o2 ? this.getReservacionesIdentifier(o1) === this.getReservacionesIdentifier(o2) : o1 === o2;
  }

  addReservacionesToCollectionIfMissing<Type extends Pick<IReservaciones, 'id'>>(
    reservacionesCollection: Type[],
    ...reservacionesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const reservaciones: Type[] = reservacionesToCheck.filter(isPresent);
    if (reservaciones.length > 0) {
      const reservacionesCollectionIdentifiers = reservacionesCollection.map(
        reservacionesItem => this.getReservacionesIdentifier(reservacionesItem)!
      );
      const reservacionesToAdd = reservaciones.filter(reservacionesItem => {
        const reservacionesIdentifier = this.getReservacionesIdentifier(reservacionesItem);
        if (reservacionesCollectionIdentifiers.includes(reservacionesIdentifier)) {
          return false;
        }
        reservacionesCollectionIdentifiers.push(reservacionesIdentifier);
        return true;
      });
      return [...reservacionesToAdd, ...reservacionesCollection];
    }
    return reservacionesCollection;
  }

  protected convertDateFromClient<T extends IReservaciones | NewReservaciones | PartialUpdateReservaciones>(reservaciones: T): RestOf<T> {
    return {
      ...reservaciones,
      fechaInicio: reservaciones.fechaInicio?.toJSON() ?? null,
      fechaFinal: reservaciones.fechaFinal?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restReservaciones: RestReservaciones): IReservaciones {
    return {
      ...restReservaciones,
      fechaInicio: restReservaciones.fechaInicio ? dayjs(restReservaciones.fechaInicio) : undefined,
      fechaFinal: restReservaciones.fechaFinal ? dayjs(restReservaciones.fechaFinal) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestReservaciones>): HttpResponse<IReservaciones> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestReservaciones[]>): HttpResponse<IReservaciones[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
