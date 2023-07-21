import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IEventos } from '../eventos.model';
import { EventosService } from '../service/eventos.service';

export const eventosResolve = (route: ActivatedRouteSnapshot): Observable<null | IEventos> => {
  const id = route.params['id'];
  if (id) {
    return inject(EventosService)
      .find(id)
      .pipe(
        mergeMap((eventos: HttpResponse<IEventos>) => {
          if (eventos.body) {
            return of(eventos.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        })
      );
  }
  return of(null);
};

export default eventosResolve;
