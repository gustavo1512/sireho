import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IEventos, NewEventos } from '../eventos.model';

export type PartialUpdateEventos = Partial<IEventos> & Pick<IEventos, 'id'>;

type RestOf<T extends IEventos | NewEventos> = Omit<T, 'fechaHora'> & {
  fechaHora?: string | null;
};

export type RestEventos = RestOf<IEventos>;

export type NewRestEventos = RestOf<NewEventos>;

export type PartialUpdateRestEventos = RestOf<PartialUpdateEventos>;

export type EntityResponseType = HttpResponse<IEventos>;
export type EntityArrayResponseType = HttpResponse<IEventos[]>;

@Injectable({ providedIn: 'root' })
export class EventosService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/eventos');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(eventos: NewEventos): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(eventos);
    return this.http
      .post<RestEventos>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(eventos: IEventos): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(eventos);
    return this.http
      .put<RestEventos>(`${this.resourceUrl}/${this.getEventosIdentifier(eventos)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(eventos: PartialUpdateEventos): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(eventos);
    return this.http
      .patch<RestEventos>(`${this.resourceUrl}/${this.getEventosIdentifier(eventos)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestEventos>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestEventos[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getEventosIdentifier(eventos: Pick<IEventos, 'id'>): number {
    return eventos.id;
  }

  compareEventos(o1: Pick<IEventos, 'id'> | null, o2: Pick<IEventos, 'id'> | null): boolean {
    return o1 && o2 ? this.getEventosIdentifier(o1) === this.getEventosIdentifier(o2) : o1 === o2;
  }

  addEventosToCollectionIfMissing<Type extends Pick<IEventos, 'id'>>(
    eventosCollection: Type[],
    ...eventosToCheck: (Type | null | undefined)[]
  ): Type[] {
    const eventos: Type[] = eventosToCheck.filter(isPresent);
    if (eventos.length > 0) {
      const eventosCollectionIdentifiers = eventosCollection.map(eventosItem => this.getEventosIdentifier(eventosItem)!);
      const eventosToAdd = eventos.filter(eventosItem => {
        const eventosIdentifier = this.getEventosIdentifier(eventosItem);
        if (eventosCollectionIdentifiers.includes(eventosIdentifier)) {
          return false;
        }
        eventosCollectionIdentifiers.push(eventosIdentifier);
        return true;
      });
      return [...eventosToAdd, ...eventosCollection];
    }
    return eventosCollection;
  }

  protected convertDateFromClient<T extends IEventos | NewEventos | PartialUpdateEventos>(eventos: T): RestOf<T> {
    return {
      ...eventos,
      fechaHora: eventos.fechaHora?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restEventos: RestEventos): IEventos {
    return {
      ...restEventos,
      fechaHora: restEventos.fechaHora ? dayjs(restEventos.fechaHora) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestEventos>): HttpResponse<IEventos> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestEventos[]>): HttpResponse<IEventos[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
