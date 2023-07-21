import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IHabitaciones } from '../habitaciones.model';
import { HabitacionesService } from '../service/habitaciones.service';

export const habitacionesResolve = (route: ActivatedRouteSnapshot): Observable<null | IHabitaciones> => {
  const id = route.params['id'];
  if (id) {
    return inject(HabitacionesService)
      .find(id)
      .pipe(
        mergeMap((habitaciones: HttpResponse<IHabitaciones>) => {
          if (habitaciones.body) {
            return of(habitaciones.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        })
      );
  }
  return of(null);
};

export default habitacionesResolve;
