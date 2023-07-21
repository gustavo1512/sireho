import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IFactura } from '../factura.model';
import { FacturaService } from '../service/factura.service';

export const facturaResolve = (route: ActivatedRouteSnapshot): Observable<null | IFactura> => {
  const id = route.params['id'];
  if (id) {
    return inject(FacturaService)
      .find(id)
      .pipe(
        mergeMap((factura: HttpResponse<IFactura>) => {
          if (factura.body) {
            return of(factura.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        })
      );
  }
  return of(null);
};

export default facturaResolve;
