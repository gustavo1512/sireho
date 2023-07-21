import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IReservaciones } from '../reservaciones.model';
import { ReservacionesService } from '../service/reservaciones.service';

export const reservacionesResolve = (route: ActivatedRouteSnapshot): Observable<null | IReservaciones> => {
  const id = route.params['id'];
  if (id) {
    return inject(ReservacionesService)
      .find(id)
      .pipe(
        mergeMap((reservaciones: HttpResponse<IReservaciones>) => {
          if (reservaciones.body) {
            return of(reservaciones.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        })
      );
  }
  return of(null);
};

export default reservacionesResolve;
