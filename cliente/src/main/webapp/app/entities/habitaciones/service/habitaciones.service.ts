import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IHabitaciones, NewHabitaciones } from '../habitaciones.model';

export type PartialUpdateHabitaciones = Partial<IHabitaciones> & Pick<IHabitaciones, 'id'>;

export type EntityResponseType = HttpResponse<IHabitaciones>;
export type EntityArrayResponseType = HttpResponse<IHabitaciones[]>;

@Injectable({ providedIn: 'root' })
export class HabitacionesService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/habitaciones');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(habitaciones: NewHabitaciones): Observable<EntityResponseType> {
    return this.http.post<IHabitaciones>(this.resourceUrl, habitaciones, { observe: 'response' });
  }

  update(habitaciones: IHabitaciones): Observable<EntityResponseType> {
    return this.http.put<IHabitaciones>(`${this.resourceUrl}/${this.getHabitacionesIdentifier(habitaciones)}`, habitaciones, {
      observe: 'response',
    });
  }

  partialUpdate(habitaciones: PartialUpdateHabitaciones): Observable<EntityResponseType> {
    return this.http.patch<IHabitaciones>(`${this.resourceUrl}/${this.getHabitacionesIdentifier(habitaciones)}`, habitaciones, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IHabitaciones>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IHabitaciones[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getHabitacionesIdentifier(habitaciones: Pick<IHabitaciones, 'id'>): number {
    return habitaciones.id;
  }

  compareHabitaciones(o1: Pick<IHabitaciones, 'id'> | null, o2: Pick<IHabitaciones, 'id'> | null): boolean {
    return o1 && o2 ? this.getHabitacionesIdentifier(o1) === this.getHabitacionesIdentifier(o2) : o1 === o2;
  }

  addHabitacionesToCollectionIfMissing<Type extends Pick<IHabitaciones, 'id'>>(
    habitacionesCollection: Type[],
    ...habitacionesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const habitaciones: Type[] = habitacionesToCheck.filter(isPresent);
    if (habitaciones.length > 0) {
      const habitacionesCollectionIdentifiers = habitacionesCollection.map(
        habitacionesItem => this.getHabitacionesIdentifier(habitacionesItem)!
      );
      const habitacionesToAdd = habitaciones.filter(habitacionesItem => {
        const habitacionesIdentifier = this.getHabitacionesIdentifier(habitacionesItem);
        if (habitacionesCollectionIdentifiers.includes(habitacionesIdentifier)) {
          return false;
        }
        habitacionesCollectionIdentifiers.push(habitacionesIdentifier);
        return true;
      });
      return [...habitacionesToAdd, ...habitacionesCollection];
    }
    return habitacionesCollection;
  }
}
