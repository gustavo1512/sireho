import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IFactura, NewFactura } from '../factura.model';

export type PartialUpdateFactura = Partial<IFactura> & Pick<IFactura, 'id'>;

type RestOf<T extends IFactura | NewFactura> = Omit<T, 'fechaPago'> & {
  fechaPago?: string | null;
};

export type RestFactura = RestOf<IFactura>;

export type NewRestFactura = RestOf<NewFactura>;

export type PartialUpdateRestFactura = RestOf<PartialUpdateFactura>;

export type EntityResponseType = HttpResponse<IFactura>;
export type EntityArrayResponseType = HttpResponse<IFactura[]>;

@Injectable({ providedIn: 'root' })
export class FacturaService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/facturas');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(factura: NewFactura): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(factura);
    return this.http
      .post<RestFactura>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(factura: IFactura): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(factura);
    return this.http
      .put<RestFactura>(`${this.resourceUrl}/${this.getFacturaIdentifier(factura)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(factura: PartialUpdateFactura): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(factura);
    return this.http
      .patch<RestFactura>(`${this.resourceUrl}/${this.getFacturaIdentifier(factura)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestFactura>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestFactura[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getFacturaIdentifier(factura: Pick<IFactura, 'id'>): number {
    return factura.id;
  }

  compareFactura(o1: Pick<IFactura, 'id'> | null, o2: Pick<IFactura, 'id'> | null): boolean {
    return o1 && o2 ? this.getFacturaIdentifier(o1) === this.getFacturaIdentifier(o2) : o1 === o2;
  }

  addFacturaToCollectionIfMissing<Type extends Pick<IFactura, 'id'>>(
    facturaCollection: Type[],
    ...facturasToCheck: (Type | null | undefined)[]
  ): Type[] {
    const facturas: Type[] = facturasToCheck.filter(isPresent);
    if (facturas.length > 0) {
      const facturaCollectionIdentifiers = facturaCollection.map(facturaItem => this.getFacturaIdentifier(facturaItem)!);
      const facturasToAdd = facturas.filter(facturaItem => {
        const facturaIdentifier = this.getFacturaIdentifier(facturaItem);
        if (facturaCollectionIdentifiers.includes(facturaIdentifier)) {
          return false;
        }
        facturaCollectionIdentifiers.push(facturaIdentifier);
        return true;
      });
      return [...facturasToAdd, ...facturaCollection];
    }
    return facturaCollection;
  }

  protected convertDateFromClient<T extends IFactura | NewFactura | PartialUpdateFactura>(factura: T): RestOf<T> {
    return {
      ...factura,
      fechaPago: factura.fechaPago?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restFactura: RestFactura): IFactura {
    return {
      ...restFactura,
      fechaPago: restFactura.fechaPago ? dayjs(restFactura.fechaPago) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestFactura>): HttpResponse<IFactura> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestFactura[]>): HttpResponse<IFactura[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
